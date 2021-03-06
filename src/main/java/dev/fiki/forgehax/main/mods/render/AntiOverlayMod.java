package dev.fiki.forgehax.main.mods.render;

import dev.fiki.forgehax.api.asm.MapField;
import dev.fiki.forgehax.api.events.RenderEvent;
import dev.fiki.forgehax.api.mod.Category;
import dev.fiki.forgehax.api.mod.ToggleMod;
import dev.fiki.forgehax.api.modloader.RegisterMod;
import dev.fiki.forgehax.api.reflection.types.ReflectionField;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static dev.fiki.forgehax.main.Common.*;

@RegisterMod(
    name = "AntiOverlay",
    description = "Removes screen overlays",
    category = Category.RENDER
)
@RequiredArgsConstructor
public class AntiOverlayMod extends ToggleMod {
  @MapField(parentClass = GameRenderer.class, value = "itemActivationItem")
  private final ReflectionField<ItemStack> GameRenderer_itemActivationItem;

  /**
   * Disables water/lava fog
   */
  @SubscribeEvent
  public void onFogRender(EntityViewRenderEvent.FogDensity event) {
    if (isInWorld() && (getLocalPlayer().isInLava() || getLocalPlayer().isInWater())) {
      event.setDensity(0);
      event.setCanceled(true);
    }
  }

  /**
   * Disables screen overlays
   */
  @SubscribeEvent
  public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
    event.setCanceled(true);
  }

  @SubscribeEvent
  public void onRenderGameOverlay(RenderGameOverlayEvent event) {
    if (event.getType().equals(RenderGameOverlayEvent.ElementType.HELMET)
        || event.getType().equals(RenderGameOverlayEvent.ElementType.PORTAL)) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public void onRender(RenderEvent event) {
    ItemStack stack = GameRenderer_itemActivationItem.get(getGameRenderer());

    if (stack != null && Items.TOTEM_OF_UNDYING.equals(stack.getItem())) {
      GameRenderer_itemActivationItem.set(getGameRenderer(), null);
    }
  }
}
