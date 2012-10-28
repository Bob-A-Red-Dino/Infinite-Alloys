package infinitealloys.client;

import infinitealloys.IAValues;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;

public class GuiConnectMachineButton extends GuiScreen {

	public int xPos, yPos, blockX, blockY, blockZ;

	public GuiConnectMachineButton(int xPos, int yPos, int blockX, int blockY, int blockZ) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		width = 16;
		height = 16;
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture(IAValues.TEXTURE_PATH + "guicomputer.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(xPos, yPos, 192 + mc.theWorld.getBlockMetadata(blockX, blockY, blockZ) * 16, 0, width, height);
	}

	public boolean mousePressed(int mouseX, int mouseY) {
		return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
	}
}