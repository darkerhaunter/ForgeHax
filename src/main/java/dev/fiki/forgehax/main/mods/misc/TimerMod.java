package dev.fiki.forgehax.main.mods.misc;

import dev.fiki.forgehax.api.asm.MapField;
import dev.fiki.forgehax.api.cmd.settings.BooleanSetting;
import dev.fiki.forgehax.api.cmd.settings.FloatSetting;
import dev.fiki.forgehax.api.mod.Category;
import dev.fiki.forgehax.api.mod.ToggleMod;
import dev.fiki.forgehax.api.modloader.RegisterMod;
import dev.fiki.forgehax.api.reflection.types.ReflectionField;
import dev.fiki.forgehax.asm.events.packet.PacketInboundEvent;
import dev.fiki.forgehax.main.Common;
import dev.fiki.forgehax.main.services.TickRateService;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.util.Timer;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@RegisterMod(
    name = "Timer",
    description = "Speed up game time",
    category = Category.MISC
)
@RequiredArgsConstructor
public class TimerMod extends ToggleMod {
  @MapField(parentClass = Minecraft.class, value = "timer")
  public final ReflectionField<Timer> Minecraft_timer;

  @MapField(parentClass = Timer.class, value = "tickLength")
  public final ReflectionField<Float> Timer_tickLength;

  public final FloatSetting factor = newFloatSetting()
      .name("speed")
      .description("how fast to make the game run")
      .defaultTo(1f)
      .min(0f)
      .changedListener((from, to) -> {
        if (this.isEnabled()) {
          updateTimer();
        }
      })
      .build();

  public final BooleanSetting tpsSync = newBooleanSetting()
      .name("tps-sync")
      .description("sync timer to tps")
      .defaultTo(false)
      .build();

  private final float DEFAULT_SPEED = 1000f / 20; // default speed - 50 ms

  @Override
  public void onEnabled() {
    updateTimer();
  }

  @Override
  public void onDisabled() {
    setSpeed(DEFAULT_SPEED);
  }

  private void updateTimer() {
    if (!tpsSync.getValue()) {
      setSpeed(DEFAULT_SPEED / factor.getValue());
    }
  }

  @SubscribeEvent
  public void onPacketPreceived(PacketInboundEvent event) {
    if (event.getPacket() instanceof SUpdateTimePacket && tpsSync.getValue()) {
      TickRateService monitor = TickRateService.getInstance();
      if (!monitor.isEmpty()) {
        setSpeed((float) (DEFAULT_SPEED / (monitor.getTickrate() / 20.f)));
      }
    } else {
      updateTimer();
    }
  }

  private void setSpeed(float value) {
    Timer timer = Minecraft_timer.get(Common.MC);
    Timer_tickLength.set(timer, value);
  }

  @Override
  public String getDisplayText() {
    if (tpsSync.getValue()) {
      TickRateService monitor = TickRateService.getInstance();
      if (!monitor.isEmpty()) {
        return String.format("%s[%.2f]", super.getDisplayText(), monitor.getTickrate() / 20);
      }
    } else {
      return String.format("%s[%.2f]", super.getDisplayText(), factor.getValue());
    }
    return super.getDisplayText();
  }
}
