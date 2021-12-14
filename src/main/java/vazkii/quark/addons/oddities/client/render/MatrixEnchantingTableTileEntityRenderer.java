package vazkii.quark.addons.oddities.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.addons.oddities.tile.MatrixEnchantingTableTileEntity;

public class MatrixEnchantingTableTileEntityRenderer extends BlockEntityRenderer<MatrixEnchantingTableTileEntity> {

	public MatrixEnchantingTableTileEntityRenderer(BlockEntityRenderDispatcher p_i226006_1_) {
		super(p_i226006_1_);
	}

	@Override
	public void render(MatrixEnchantingTableTileEntity te, float pticks, PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
		float time = te.tickCount + pticks;

		float f1 = te.bookRotation - te.bookRotationPrev;
		while (f1 >= Math.PI)
			f1 -= (Math.PI * 2F);
		while (f1 < -Math.PI)
			f1 += (Math.PI * 2F);

		float rot = te.bookRotationPrev + f1 * pticks;
		float bookOpen = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * pticks;

		renderBook(te, time, rot, pticks, matrix, buffer, light, overlay);

		ItemStack item = te.getItem(0);
		if(!item.isEmpty())
			renderItem(item, time, bookOpen, rot, matrix, buffer, light, overlay);
	}

	private void renderItem(ItemStack item, float time, float bookOpen, float rot, PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
		matrix.pushPose();
		matrix.translate(0.5F, 0.8F, 0.5F);
		matrix.scale(0.6F, 0.6F, 0.6F);

		rot *= -180F / (float) Math.PI;
		rot -= 90F;
		rot *= bookOpen;

		matrix.mulPose(Vector3f.YP.rotationDegrees(rot));
		matrix.translate(0, bookOpen * 1.4F, Math.sin(bookOpen * Math.PI));
		matrix.mulPose(Vector3f.XP.rotationDegrees(-90F * (bookOpen - 1F)));

		float trans = (float) Math.sin(time * 0.06) * bookOpen * 0.2F;
		matrix.translate(0F, trans, 0F);

		ItemRenderer render = Minecraft.getInstance().getItemRenderer();
		render.renderStatic(item, ItemTransforms.TransformType.FIXED, light, overlay, matrix, buffer);
		matrix.popPose();
	}

	public static final Material TEXTURE_BOOK = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/enchanting_table_book"));
	private final BookModel modelBook = new BookModel();

	// Copy of vanilla's book render
	private void renderBook(MatrixEnchantingTableTileEntity tileEntityIn, float time, float bookRot, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		matrixStackIn.pushPose();
		matrixStackIn.translate(0.5D, 0.75D, 0.5D);
		float f = (float) tileEntityIn.tickCount + partialTicks;
		matrixStackIn.translate(0.0D, (double)(0.1F + Mth.sin(f * 0.1F) * 0.01F), 0.0D);

		float f1;
		for(f1 = tileEntityIn.bookRotation - tileEntityIn.bookRotationPrev; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F)) {
			;
		}

		while(f1 < -(float)Math.PI) {
			f1 += ((float)Math.PI * 2F);
		}

		float f2 = tileEntityIn.bookRotationPrev + f1 * partialTicks;
		matrixStackIn.mulPose(Vector3f.YP.rotation(-f2));
		matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(80.0F));
		float f3 = Mth.lerp(partialTicks, tileEntityIn.pageFlipPrev, tileEntityIn.pageFlip);
		float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
		float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;
		float f6 = Mth.lerp(partialTicks, tileEntityIn.bookSpreadPrev, tileEntityIn.bookSpread);
		this.modelBook.setupAnim(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
		VertexConsumer ivertexbuilder = TEXTURE_BOOK.buffer(bufferIn, RenderType::entitySolid);
		this.modelBook.renderToBuffer(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
		matrixStackIn.popPose();
	}

}