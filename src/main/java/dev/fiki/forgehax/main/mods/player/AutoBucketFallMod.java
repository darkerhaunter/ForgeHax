package dev.fiki.forgehax.main.mods.player;

import dev.fiki.forgehax.api.cmd.settings.DoubleSetting;
import dev.fiki.forgehax.api.extension.EntityEx;
import dev.fiki.forgehax.api.extension.ItemEx;
import dev.fiki.forgehax.api.extension.LocalPlayerEx;
import dev.fiki.forgehax.api.mod.Category;
import dev.fiki.forgehax.api.mod.ToggleMod;
import dev.fiki.forgehax.api.modloader.RegisterMod;
import dev.fiki.forgehax.api.reflection.ReflectionTools;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.val;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Comparator;

import static dev.fiki.forgehax.main.Common.*;
import static net.minecraft.util.math.RayTraceResult.Type;

/**
 * Created by Babbaj on 9/4/2017. TODO: check all 4 collision box corners
 */
@RegisterMod(
    name = "AutoBucket",
    description = "Automatically place bucket to reset fall damage",
    category = Category.PLAYER
)
@RequiredArgsConstructor
@ExtensionMethod({EntityEx.class, ItemEx.class, LocalPlayerEx.class})
public class AutoBucketFallMod extends ToggleMod {
  private final ReflectionTools reflection;

  public final DoubleSetting preHeight = newDoubleSetting()
      .name("PreHeight")
      .description("how far below to check before preparing")
      .defaultTo(10D)
      .build();
  public final DoubleSetting settingFallHeight = newDoubleSetting()
      .name("height")
      .description("minimum fall distance to work")
      .defaultTo(15D)
      .build();

  private final ItemStack WATER_BUCKET = new ItemStack(Items.WATER_BUCKET);

  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    val lp = getLocalPlayer();
    if (lp == null
        || lp.fallDistance < settingFallHeight.getValue()
        || !lp.inventory.hasItemStack(WATER_BUCKET)
        || lp.isInWaterMotionState()
        || lp.isAboveWater()) {
      return;
    }

    val playerPos = lp.getPositionVec();
    val rayTraceBucket = new Vector3d(playerPos.x, playerPos.y - 5, playerPos.z);
    // find the ground before the player is ready to water bucket
    val rayTracePre = new Vector3d(playerPos.x, playerPos.y - preHeight.getValue(), playerPos.z);

    RayTraceContext ctx = new RayTraceContext(playerPos, rayTraceBucket,
        RayTraceContext.BlockMode.COLLIDER,
        RayTraceContext.FluidMode.NONE,
        lp);

    val result = getWorld().rayTraceBlocks(ctx);

    ctx = new RayTraceContext(playerPos, rayTracePre,
        RayTraceContext.BlockMode.COLLIDER,
        RayTraceContext.FluidMode.NONE,
        lp);

    val resultPre = getWorld().rayTraceBlocks(ctx);

    if (Type.BLOCK.equals(resultPre.getType())
        && !getWorld().getBlockState(resultPre.getPos()).getMaterial().isLiquid()) {
      // set the pitch early to not get cucked by ncp
      lp.prevRotationPitch = 90f;
      lp.rotationPitch = 90f;

      lp.getPrimarySlots().stream()
          .filter(slot -> Items.WATER_BUCKET.equals(slot.getStack().getItem()))
          .min(Comparator.comparingInt(Slot::getSlotIndex))
          .ifPresent(slot -> {
            if (!slot.isInHotbar()) {
              slot.click(ClickType.SWAP, lp.getSelectedIndex());
            } else {
              lp.setSelectedSlot(slot, ticks -> ticks > 20 * 5);
            }
          });
    }

    if (Type.BLOCK.equals(result.getType())
        && !getWorld().getBlockState(result.getPos()).getMaterial().isLiquid()) {
      sendNetworkPacket(new CPlayerPacket.RotationPacket(
          lp.rotationYaw,
          90,
          reflection.Entity_onGround.get(lp)));

      // probably unnecessary but doing it anyways
      lp.prevRotationPitch = 90f;
      lp.rotationPitch = 90f;

      // printMessage("Attempted to place water bucket");
      lp.rightClick(Hand.MAIN_HAND);
    }
  }
}
