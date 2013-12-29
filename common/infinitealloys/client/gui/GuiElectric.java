package infinitealloys.client.gui;

import infinitealloys.tile.TileEntityElectric;
import infinitealloys.util.Funcs;
import java.text.DecimalFormat;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public abstract class GuiElectric extends GuiMachine {

	/** Coordinates of the progress bar texture, changes by machine but still otherwise */
	protected java.awt.Point progressBar = new java.awt.Point();

	/** Coordinates of the energy icon, that indicates power network status */
	protected java.awt.Point energyIcon = new java.awt.Point();

	protected TileEntityElectric tem;

	public GuiElectric(int xSize, int ySize, InventoryPlayer inventoryPlayer, TileEntityElectric tileEntity) {
		super(xSize, ySize, inventoryPlayer, tileEntity);
		tem = tileEntity;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		super.drawScreen(mouseX, mouseY, partialTick);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// Draw the progress info if the mouse is over the progress bar
		if(tem.ticksToProcess > 0)
			if(mouseInZone(mouseX, mouseY, topLeft.x + progressBar.x, topLeft.y + progressBar.y, PROGRESS_BAR.width, PROGRESS_BAR.height))
				// Draw the progress as a percentage, rounded to the nearest tenth
				drawTextBox(mouseX, mouseY, new ColoredLine(new DecimalFormat("0.0").format(tem.getProcessProgressScaled(100F)) + "%", 0xffffff));

		// Draw the power network info if the mouse is over the energy icon
		if(mouseInZone(mouseX, mouseY, topLeft.x + energyIcon.x, topLeft.y + energyIcon.y, ENERGY_ICON.width, ENERGY_ICON.height)) {
			if(tem.energyStorage != null) {
				int rkChange = tem.shouldProcess() ? 0 : tem.getRKChange();
				drawTextBox(mouseX, mouseY, new ColoredLine(Funcs.getLoc("electric.connected.true") + "(" + tem.energyStorage.getCoords() + ")", 0x00ff00),
						new ColoredLine((rkChange > 0 ? "+" : "") + rkChange + " RK/t", rkChange < 0 ? 0xff0000 : rkChange > 0 ? 0x00ff00 : 0xffffff));
			}
			else
				drawTextBox(mouseX, mouseY, new ColoredLine(Funcs.getLoc("electric.connected.false"), 0xff0000));
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
		GL11.glPushMatrix();
		GL11.glTranslatef(topLeft.x, topLeft.y, 0);
		Funcs.bindTexture(extras);

		// Draw the progress bar overlay
		if(tem.ticksToProcess > 0)
			drawTexturedModalRect(progressBar.x, progressBar.y, PROGRESS_BAR.x, PROGRESS_BAR.y, (int)tem.getProcessProgressScaled(PROGRESS_BAR.width),
					PROGRESS_BAR.height);

		// Draw the energy icon overlay
		if(tem.energyStorage != null)
			drawTexturedModalRect(energyIcon.x, energyIcon.y, ENERGY_ICON.x, ENERGY_ICON.y, ENERGY_ICON.width, ENERGY_ICON.height);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
