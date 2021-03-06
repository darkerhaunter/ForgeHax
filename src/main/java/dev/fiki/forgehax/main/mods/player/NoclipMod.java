package dev.fiki.forgehax.main.mods.player;

import dev.fiki.forgehax.api.events.LocalPlayerUpdateEvent;
import dev.fiki.forgehax.api.mod.Category;
import dev.fiki.forgehax.api.mod.ToggleMod;
import dev.fiki.forgehax.api.modloader.RegisterMod;
import dev.fiki.forgehax.api.reflection.ReflectionTools;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static dev.fiki.forgehax.main.Common.getMountedEntityOrPlayer;

@RegisterMod(
    name = "Noclip",
    description = "Enables player noclip",
    category = Category.PLAYER
)
@RequiredArgsConstructor
public class NoclipMod extends ToggleMod {
  private final ReflectionTools reflection;

  @Override
  public void onDisabled() {
    Entity local = getMountedEntityOrPlayer();
    if (local != null) {
      local.noClip = false;
    }
  }

  @SubscribeEvent
  public void onLocalPlayerUpdate(LocalPlayerUpdateEvent event) {
    Entity local = getMountedEntityOrPlayer();
    local.noClip = true;
    local.fallDistance = 0;
    reflection.Entity_onGround.set(local, false);
  }
}
