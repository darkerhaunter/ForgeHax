package dev.fiki.forgehax.main.mods.world;

import dev.fiki.forgehax.api.mod.Category;
import dev.fiki.forgehax.api.mod.ToggleMod;
import dev.fiki.forgehax.api.modloader.RegisterMod;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@RegisterMod(
    name = "AntiFog",
    description = "Removes fog",
    category = Category.WORLD
)
public class AntiFogMod extends ToggleMod {
  @SubscribeEvent
  public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
    event.setDensity(0);
    event.setCanceled(true);
  }

  @SubscribeEvent
  public void onFogColor(EntityViewRenderEvent.FogColors event) {
    event.setRed(55);
    event.setGreen(55);
    event.setBlue(55);
  }
}
