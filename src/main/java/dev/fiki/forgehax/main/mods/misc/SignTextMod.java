package dev.fiki.forgehax.main.mods.misc;

import dev.fiki.forgehax.api.asm.MapField;
import dev.fiki.forgehax.api.mod.Category;
import dev.fiki.forgehax.api.mod.ToggleMod;
import dev.fiki.forgehax.api.modloader.RegisterMod;
import dev.fiki.forgehax.api.reflection.types.ReflectionField;
import dev.fiki.forgehax.main.Common;
import lombok.RequiredArgsConstructor;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

@RegisterMod(
    name = "SignText",
    description = "get sign text",
    category = Category.MISC
)
@RequiredArgsConstructor
public class SignTextMod extends ToggleMod {
  @MapField(parentClass = SignTileEntity.class, value = "signText")
  private final ReflectionField<ITextComponent[]> SignTileEntity_signText;

  @SubscribeEvent
  public void onInput(InputEvent.MouseInputEvent event) {
    // TODO: 1.15 mouse input
    if (event.getButton() == 2 /*&& Mouse.getEventButtonState()*/) { // on middle click
      RayTraceResult result = Common.getLocalPlayer().pick(999, 0, false);
      if (RayTraceResult.Type.BLOCK.equals(result.getType())) {
        TileEntity tileEntity = Common.getWorld().getTileEntity(new BlockPos(result.getHitVec()));

        if (tileEntity instanceof SignTileEntity) {
          SignTileEntity sign = (SignTileEntity) tileEntity;
          ITextComponent[] texts = SignTileEntity_signText.get(sign);

          int signTextLength = 0;
          // find the first line from the bottom that isn't empty
          for (int i = 3; i >= 0; i--) {
            if (!texts[i].getUnformattedComponentText().isEmpty()) {
              signTextLength = i + 1;
              break;
            }
          }
          if (signTextLength == 0) {
            return; // if the sign is empty don't do anything
          }

          String[] lines = new String[signTextLength];

          for (int i = 0; i < signTextLength; i++) {
            lines[i] = texts[i].getString().replace(TextFormatting.RESET.toString(), "");
          }

          String fullText = String.join("\n", lines);

          Common.print("Copied sign");
          setClipboardString(fullText);
        }
      }
    }
  }

  private static void setClipboardString(String stringIn) {
    StringSelection selection = new StringSelection(stringIn);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
  }
}
