package infinitealloys.inventory;

import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TileEntityXray;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SlotXray extends Slot {

	public SlotXray(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return TEHelper.isDetectable(itemstack.itemID, itemstack.getItemDamage());
	}
}
