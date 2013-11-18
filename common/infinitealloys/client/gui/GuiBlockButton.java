package infinitealloys.client.gui;

import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/** The type of button used in the x-ray GUI to represent blocks that have been detected */
public class GuiBlockButton extends GuiScreen {

	private final Rectangle SELECTED_OVERLAY = new Rectangle(68, 24, 36, 18);

	/** The background pattern of the button, dependent on the elevation. Can be bedrock, stone, dirt, grass, or sky */
	private final Background[] BACKGROUNDS = { new Background(0, 5, 84, 24), new Background(6, 50, 118, 24), new Background(51, 60, 152, 24),
			new Background(61, 85, 186, 24), new Background(86, Short.MAX_VALUE, 220, 24) };

	private RenderItem itemRenderer;

	/** The position of the button within the GUI */
	private int xPos, yPos;

	private int blockID, blockAmount, blockMeta;
	/** The yValue of blocks that this button represents */
	private int yValue;
	private Background background;
	public boolean activated;

	public GuiBlockButton(Minecraft mc, RenderItem itemRenderer, int xPos, int yPos, int blockID, int blockAmount, int blockMeta, int yValue) {
		this.mc = mc;
		this.itemRenderer = itemRenderer;
		this.xPos = xPos;
		this.yPos = yPos;
		this.blockID = blockID;
		this.blockAmount = blockAmount;
		this.blockMeta = blockMeta;
		this.yValue = yValue;
		width = 36;
		height = 18;

		// Set the background of the button based on its y-value
		for(Background bg : BACKGROUNDS) {
			if(bg.start <= yValue && yValue <= bg.end) {
				background = bg;
				break;
			}
		}
	}

	public void drawButton() {
		if(blockAmount > 0) {
			GuiElectric.bindTexture(GuiMachine.extras);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			// Draw the background texture for the button
			drawTexturedModalRect(xPos, yPos, background.texture.x, background.texture.y, background.texture.width, background.texture.height);
			// If this button is selected, draw an overlay to indicate that
			if(activated)
				drawTexturedModalRect(xPos - 1, yPos - 1, SELECTED_OVERLAY.x, SELECTED_OVERLAY.y, SELECTED_OVERLAY.width, SELECTED_OVERLAY.height);

			// Draw the yValue string
			String display = Integer.toString(yValue);
			mc.fontRenderer.drawStringWithShadow(display, xPos + 9 - (mc.fontRenderer.getStringWidth(display) / 2), yPos + 5, 0xffffff);

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(blockID, 1, blockMeta), xPos + 18, yPos);
			itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(blockID, blockAmount, blockMeta), xPos + 19, yPos + 1);
		}
	}

	public int getYValue() {
		return yValue;
	}

	public boolean mousePressed(int mouseX, int mouseY) {
		return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
	}

	/** The backgrounds for the buttons */
	private class Background {
		/** The y-value of the start of the texture's range (inclusive) */
		int start;
		/** The y-value of the end of the texture's range (inclusive) */
		int end;
		/** The texture's location and size in the texture sheet (extras.png) */
		Rectangle texture;

		private Background(int start, int end, int u, int v) {
			this.start = start;
			this.end = end;
			this.texture = new Rectangle(u, v, 34, 16);
		}
	}
}
