package infinitealloys.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import infinitealloys.tile.TileEntityMachine;
import io.netty.buffer.ByteBuf;

public class MessageTEToServer implements IMessage, IMessageHandler<MessageTEToServer, IMessage> {

  private TileEntityMachine tem;
  private ByteBuf bytes;

  public MessageTEToServer() {
  }

  public MessageTEToServer(TileEntityMachine tem) {
    this.tem = tem;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(
        bytes.readInt(), bytes.readInt(), bytes.readInt());
    if (te instanceof TileEntityMachine) {
      tem = (TileEntityMachine) te;
    }
    this.bytes = bytes;
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    tem.writeToServerData(bytes);
  }

  @Override
  public IMessage onMessage(MessageTEToServer message, MessageContext context) {
    message.tem.readToServerData(message.bytes);
    return null;
  }
}
