package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.QuarkModule;

public class QuarkWallBlock extends WallBlock implements IQuarkBlock, IBlockColorProvider {

	private final IQuarkBlock parent;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkWallBlock(IQuarkBlock parent) {
		super(VariantHandler.realStateCopy(parent));

		this.parent = parent;
		RegistryHelper.registerBlock(this, IQuarkBlock.inheritQuark(parent, "%s_wall"));
		RegistryHelper.setCreativeTab(this, CreativeModeTabs.BUILDING_BLOCKS);

		RenderLayerHandler.setInherited(this, parent.getBlock());
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return parent.getModule();
	}

	@Override
	public QuarkWallBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return parent.getBlock().getBeaconColorMultiplier(parent.getBlock().defaultBlockState(), world, pos, beaconPos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockColor getBlockColor() {
		return parent instanceof IBlockColorProvider provider ? provider.getBlockColor() : null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemColor getItemColor() {
		return parent instanceof IItemColorProvider provider ? provider.getItemColor() : null;
	}

}
