package infinitealloys.client;

import infinitealloys.handlers.PacketHandler;
import infinitealloys.inventory.ContainerMachine;
import infinitealloys.tile.TileEntityComputer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiComputer extends GuiMachine {

	public TileEntityComputer tec;
	private GuiTextField xInput, yInput, zInput;
	private GuiButton addMachine;

	public GuiComputer(InventoryPlayer inventoryPlayer, TileEntityComputer tileEntity) {
		super(176, 176, tileEntity, new ContainerMachine(inventoryPlayer, tileEntity), "computer");
		tec = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(addMachine = new GuiButton(0, width / 2 + 44, height / 2 - 83, 32, 20, "Add"));
		xInput = new GuiTextField(mc.fontRenderer, width / 2 - 80, height / 2 - 81, 30, 16);
		yInput = new GuiTextField(mc.fontRenderer, width / 2 - 38, height / 2 - 81, 30, 16);
		zInput = new GuiTextField(mc.fontRenderer, width / 2 + 4, height / 2 - 81, 30, 16);
		xInput.setText("X");
		yInput.setText("Y");
		zInput.setText("Z");
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		super.drawScreen(mouseX, mouseY, partialTick);
		xInput.drawTextBox();
		yInput.drawTextBox();
		zInput.drawTextBox();
		boolean full = machineTabs.size() < tec.networkCapacity;
		addMachine.enabled = full;
		xInput.func_82265_c(full);
		yInput.func_82265_c(full);
		zInput.func_82265_c(full);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		xInput.mouseClicked(mouseX, mouseY, mouseButton);
		yInput.mouseClicked(mouseX, mouseY, mouseButton);
		zInput.mouseClicked(mouseX, mouseY, mouseButton);

		if(xInput.isFocused() && xInput.getText().equals("X"))
			xInput.setText("");
		else if(xInput.getText().equals(""))
			xInput.setText("X");

		if(yInput.isFocused() && yInput.getText().equals("Y"))
			yInput.setText("");
		else if(yInput.getText().equals(""))
			yInput.setText("Y");

		if(zInput.isFocused() && zInput.getText().equals("Z"))
			zInput.setText("");
		else if(zInput.getText().equals(""))
			zInput.setText("Z");

	}

	@Override
	protected void keyTyped(char key, int eventKey) {
		super.keyTyped(key, eventKey);
		if(eventKey == Keyboard.KEY_TAB) {
			if(xInput.isFocused())
				mouseClicked(width / 2 - 38, height / 2 - 81, 0);
			else if(yInput.isFocused())
				mouseClicked(width / 2 + 4, height / 2 - 81, 0);
			else if(!zInput.isFocused())
				mouseClicked(width / 2 - 80, height / 2 - 81, 0);
		}
		if(eventKey == Keyboard.KEY_RETURN)
			actionPerformed(addMachine);
		if(Character.isDigit(key) || eventKey == Keyboard.KEY_BACK) {
			xInput.textboxKeyTyped(key, eventKey);
			yInput.textboxKeyTyped(key, eventKey);
			zInput.textboxKeyTyped(key, eventKey);
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button.id == 0) {
			try {
				int x = new Integer(xInput.getText());
				int y = new Integer(yInput.getText());
				int z = new Integer(zInput.getText());
				tec.addMachine(mc.thePlayer, new Integer(xInput.getText()), new Integer(yInput.getText()), new Integer(zInput.getText()));
				PacketDispatcher.sendPacketToServer(PacketHandler.getComputerPacketAddMachine(tec.xCoord, tec.yCoord, tec.zCoord, x, y, z));
			}
			catch(NumberFormatException e) {
			}
		}
	}
}
