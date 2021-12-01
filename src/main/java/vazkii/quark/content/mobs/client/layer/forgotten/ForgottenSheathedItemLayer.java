package vazkii.quark.content.mobs.client.layer.forgotten;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.content.mobs.entity.ForgottenEntity;

@OnlyIn(Dist.CLIENT)
public class ForgottenSheathedItemLayer<M extends EntityModel<ForgottenEntity>> extends RenderLayer<ForgottenEntity, M> {

	public ForgottenSheathedItemLayer(RenderLayerParent<ForgottenEntity, M> p_i50919_1_) {
		super(p_i50919_1_);
	}

	@Override
	public void render(PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, ForgottenEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		ItemStack item = entitylivingbaseIn.getEntityData().get(ForgottenEntity.SHEATHED_ITEM);
		
		matrix.pushPose();
		matrix.translate(0.1, 0.2, 0.15);
		matrix.scale(0.75F, 0.75F, 0.75F);
		matrix.mulPose(Vector3f.ZP.rotationDegrees(90));
		Minecraft.getInstance().getItemInHandRenderer().renderItem(entitylivingbaseIn, item, ItemTransforms.TransformType.NONE, true, matrix, bufferIn, packedLightIn);
		matrix.popPose();
	}
}
