package infinitealloys.client;

import infinitealloys.TileEntityMetalForge;
import org.lwjgl.opengl.GL11;
import com.overminddl1.minecraft.libs.NMT.NMTModelFileOBJ;
import com.overminddl1.minecraft.libs.NMT.NMTModelRenderer;
import net.minecraft.src.ModelBase;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

public class RendererMetalForge extends TileEntitySpecialRenderer {

	private ModelBase model = new ModelBase() {};
	private NMTModelRenderer modelRenderer;
	private NMTModelFileOBJ objFile;

	public RendererMetalForge() {
		modelRenderer = new NMTModelRenderer(model);
		modelRenderer.addModelOBJ("file:///E:/Files/github/Infinite-Alloys/src/common/infinitealloys/gfx/metalforge.obj");
	}

	public void render(TileEntityMetalForge temf, double x, double y, double z, float partialTick) {
		bindTextureByName("/infinitealloys/gfx/replace.png");
		GL11.glPushMatrix();
		GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)x + 0.5F, (float)y, (float)z + 0.5F);
		GL11.glRotatef(90, 1F, 0F, 0F);
		GL11.glRotatef(180, 0F, 1F, 0F);
		GL11.glRotatef((temf.orientation - 1) * -90, 0F, 0F, 1F);
		modelRenderer.render(0.5F);
		GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
		render((TileEntityMetalForge)te, x, y, z, partialTick);
	}
}
