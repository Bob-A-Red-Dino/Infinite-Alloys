package infinitealloys.client.gui;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.tile.TEEXray;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;

public final class GuiXray extends GuiElectric {

  /**
   * Thew amount of columns of {@link BlockButton BlockButtons} that fit on the scroll menu at once
   */
  private final int LIST_WIDTH = 4;

  /**
   * The amount of rows of {@link BlockButton BlockButtons} that fit on the scroll menu at once
   */
  private final int LIST_HEIGHT = 5;

  private final TEEXray tex;

  /**
   * The number of the first displayed line of block. Starts from 0 and goes top-down.
   */
  private int scrollPos;

  private BlockButton[] blockButtons = new BlockButton[0];
  private GuiButton searchButton;

  public GuiXray(InventoryPlayer inventoryPlayer, TEEXray tileEntity) {
    super(196, 240, inventoryPlayer, tileEntity);
    tex = tileEntity;
    progressBar.setLocation(54, 5);
    networkIcon = new Point(9, 6);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void initGui() {
    super.initGui();
    buttonList.add(searchButton = new GuiButton(1, width / 2 - 40, height / 2 - 90, 80, 20,
                                                Funcs.getLoc("machine.xray.search")));
    setButtons();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    // If the list of block is short enough to fit on one page, disable the scroll bar
    if (blockButtons.length <= 20) {
      Funcs.drawTexturedModalRect(this, topLeft.x + SCROLL_BAR.x, topLeft.y + SCROLL_BAR.y,
                                  SCROLL_OFF);
    } else {
      Funcs.drawTexturedModalRect(this, topLeft.x + SCROLL_BAR.x, topLeft.y + SCROLL_BAR.y + (int) (
                                      (float) (SCROLL_BAR.height - 15) / (float) (
                                          blockButtons.length / 4 - 4) * scrollPos),
                                  SCROLL_ON);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    // Disable the search button if there are no ores in the machine
    searchButton.enabled = tex.inventoryStacks[0] != null;

    if (tex.shouldSearch()) {
      setButtons();
    }

    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_DEPTH_TEST);

    if (tex.shouldProcess()) {
      drawCenteredString(fontRendererObj, Funcs.getLoc("machine.xray.searching"), xSize / 2, 56,
                         0xffffff);
    } else if (tex.shouldRevealBlocks()) {
      if (blockButtons.length == 0) {
        drawCenteredString(fontRendererObj, Funcs.getLoc("machine.xray.noBlocks"), xSize / 2, 56,
                           0xffffff);
      } else {
        mc.renderEngine.bindTexture(GuiMachine.extraIcons);
        for (int i = scrollPos * LIST_WIDTH;
             i < blockButtons.length && i < (scrollPos + LIST_HEIGHT) * LIST_WIDTH; i++) {
          blockButtons[i].drawButton();
        }
      }
    }

    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (mouseButton == 0) { // Was the left mouse button clicked?
      for (int i = 0; i < blockButtons.length; i++) { // Iterate though each block button
        // Was this button clicked?
        if (Funcs.pointInZone(mouseX - topLeft.x, mouseY - topLeft.y,
                              blockButtons[i].xPos, blockButtons[i].yPos, blockButtons[i].width,
                              blockButtons[i].height)) {
          // Was there already a selected button? If so, deselect it.
          if (tex.selectedButton >= 0) {
            blockButtons[tex.selectedButton].selected = false;
          }

          // Clear the highlighted block from the last selected button
          InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();

          // Was this button already selected? If so, none of the buttons are selected now
          if (tex.selectedButton == i) {
            tex.selectedButton = -1;
          }

          // This button wasn't already selected
          else {
            // This button is now selected
            tex.selectedButton = i;
            blockButtons[i].selected = true;

            // The block that are represented by the newly selected button get highlighted
            for (BlockPos block : tex.getDetectedBlocks()) {
              // Is this block represented by the newly selected button?
              if (block.getY() == blockButtons[i].yValue) {
                // If so, add this block to the list of block to be highlighted.
                // Convert the x and z coords from relative to absolute.
                InfiniteAlloys.proxy.gfxHandler.xrayBlocks.put(
                    new BlockPos(tex.getPos().getX() + block.getX(),
                                 block.getY(),
                                 tex.getPos().getZ() + block.getY()),
                    MachineHelper.getDetectableColor(blockButtons[i].block,
                                                     blockButtons[i].blockMeta));
              }
            }
          }
        }
      }

      setButtons();

      // Was the scroll up button clicked?
      if (Funcs.pointInZone(mouseX, mouseY, topLeft.x + 172, topLeft.y + 40, 14, 8)) {
        scroll(-1); // Scroll up
      }

      // Was the scroll down button clicked?
      else if (Funcs.pointInZone(mouseX, mouseY, topLeft.x + 172, topLeft.y + 147, 14, 8)) {
        scroll(1); // Scroll down
      }
    }
  }

  @Override
  public void handleMouseInput() throws IOException {
    super.handleMouseInput();
    int scrollAmt = Mouse.getEventDWheel();
    // Scroll one line up or down based on the movement, if the list is long enough to need scrolling
    if (blockButtons.length > 20) {
      scroll(scrollAmt > 0 ? -1 : scrollAmt < 0 ? 1 : 0);
    }
  }

  /**
   * Set the value of each {@link BlockButton} based on the current search results.
   */
  private void setButtons() {
    if (tex.inventoryStacks[0] == null || tee.getProcessProgress() > 0) {
      blockButtons = new BlockButton[0];
    } else {
      int[] blockCounts = new int[tee.getPos().getY()];
      List<Integer> yLevels = new LinkedList<>();

      // For each detected block
      for (BlockPos block : tex.getDetectedBlocks()) {
        // If there hasn't been a block for that y-level yet, add that y to the list
        if (blockCounts[block.getY()]++ == 0) {
          yLevels.add(block.getY());
        }
      }
      blockButtons = new BlockButton[yLevels.size()];

      // Combined standard/enhanced for loop to use iterator for the LinkedList and keep an index
      // for GUI position.
      int i = 0;
      for (int yLevel : yLevels) {
        blockButtons[i] = new BlockButton(
            i % LIST_WIDTH * 40 + 9, (i / LIST_WIDTH - scrollPos) * 20 + 52,
            Block.getBlockFromItem(tex.inventoryStacks[0].getItem()), blockCounts[yLevel],
            tex.inventoryStacks[0].getItemDamage(), yLevels.get(i));
        i++; // Increment i
      }
      if (tex.selectedButton != -1) {
        blockButtons[tex.selectedButton].selected = true;
      }
    }
  }

  /**
   * Scroll the block list the specified amount of lines. Positive is down, negative is up.
   */
  private void scroll(int lines) {
    if (lines > 0 && scrollPos < (blockButtons.length - 1) / LIST_WIDTH
        || lines < 0 && scrollPos > 0) {
      scrollPos += lines;
    }
    setButtons();
  }

  @Override
  public void actionPerformed(GuiButton button) {
    super.actionPerformed(button);
    if (button.id == 1) {
      tex.selectedButton = -1;
      InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
      tex.startProcess();
    }
  }

  /**
   * A button that represents a type of a block, its y-level, and the quantity of that block in the
   * y-level
   */
  private class BlockButton {

    private final int xPos, yPos;
    private final int width, height;
    private final Block block;
    private final int blockAmount, blockMeta;

    /**
     * The yValue of block that this button represents
     */
    private final int yValue;

    private Background background;
    private boolean selected;

    private BlockButton(int xPos, int yPos, Block block, int blockAmount, int blockMeta,
                        int yValue) {
      this.xPos = xPos;
      this.yPos = yPos;
      this.block = block;
      this.blockAmount = blockAmount;
      this.blockMeta = blockMeta;
      this.yValue = yValue;
      width = 33;
      height = 15;

      // Set the backgroundIcon of the button based on its y-value
      for (Background bg : Background.values()) {
        if (bg.start <= yValue && yValue <= bg.end) {
          background = bg;
          break;
        }
      }
    }

    private void drawButton() {
      if (blockAmount > 0) {
        // Draw the backgroundIcon texture for the button
        mc.renderEngine.bindTexture(GuiMachine.extraIcons);
        drawTexturedModalRect(xPos, yPos, background.texture.x, background.texture.y,
                              background.texture.width, background.texture.height);

        // If this button is selected, draw an overlay to indicate that
        if (selected) {
          drawHorizontalLine(xPos - 1, xPos + width + 1, yPos - 1, 0xff000000); // Top
          drawHorizontalLine(xPos - 1, xPos + width + 1, yPos + height + 1, 0xff000000); // Bottom
          drawVerticalLine(xPos - 1, yPos + height + 1, yPos - 1, 0xff000000);// Left
          drawVerticalLine(xPos + width + 1, yPos + height + 1, yPos - 1, 0xff000000);// Right
        }

        // Draw the yValue string
        fontRendererObj.drawStringWithShadow(yValue + "", xPos + 9 - fontRendererObj.getStringWidth(
            yValue + "")                                             /                                           2, yPos + 5, 0xffffff);

        GL11.glEnable(GL11.GL_LIGHTING);

        itemRender.renderItemIntoGUI(new ItemStack(block, 1, blockMeta), xPos + 18, yPos);
        itemRender.renderItemOverlayIntoGUI(fontRendererObj,
                                            new ItemStack(block, blockAmount, blockMeta),
                                            xPos + 19, yPos + 1, Integer.toString(blockAmount));

        GL11.glDisable(GL11.GL_LIGHTING);
      }
    }
  }

  private enum Background {
    BEDROCK(0, 5, 84, 24), STONE(6, 50, 118, 24), DIRT(51, 60, 152, 24),
    GRASS(61, 85, 186, 24), SKY(86, Short.MAX_VALUE, 220, 24);

    /**
     * The y-value of the start of the texture's range (inclusive)
     */
    int start;
    /**
     * The y-value of the end of the texture's range (inclusive)
     */
    int end;
    /**
     * The texture's location and size in the texture sheet (extras.png)
     */
    Rectangle texture;

    Background(int start, int end, int u, int v) {
      this.start = start;
      this.end = end;
      texture = new Rectangle(u, v, 34, 16);
    }
  }
}
