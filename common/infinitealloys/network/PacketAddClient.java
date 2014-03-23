package infinitealloys.network;

import infinitealloys.util.NetworkManager;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import com.google.common.io.ByteArrayDataInput;

public class PacketAddClient implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int networkID = data.readInt();
		int x = data.readInt();
		int y = data.readShort();
		int z = data.readInt();
		NetworkManager.addClient(networkID, new Point(x, y, z));
	}

	public static Packet250CustomPayload getPacket(int networkID, int x, short y, int z) {
		return PacketHandler.getPacket(PacketHandler.ADD_CLIENT, networkID, x, y, z);
	}
}
