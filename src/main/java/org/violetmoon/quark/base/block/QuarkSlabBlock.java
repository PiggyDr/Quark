package org.violetmoon.quark.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.handler.CreativeTabHandler;
import org.violetmoon.quark.base.handler.VariantHandler;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.IZetaBlockColorProvider;
import org.violetmoon.zeta.registry.IZetaItemColorProvider;

import java.util.function.BooleanSupplier;

public class QuarkSlabBlock extends SlabBlock implements IQuarkBlock, IZetaBlockColorProvider {

	public final IQuarkBlock parent;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkSlabBlock(IQuarkBlock parent) {
		super(VariantHandler.realStateCopy(parent));

		this.parent = parent;
		String resloc = IQuarkBlock.inheritQuark(parent, "%s_slab");
		Quark.ZETA.registry.registerBlock(this, resloc, true);

		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_BUILDING_BLOCKS);

		Quark.ZETA.renderLayerRegistry.mock(this, parent.getBlock());
	}

	@Override
	public boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		BlockState parentState = parent.getBlock().defaultBlockState();
		return Quark.ZETA.blockExtensions.get(parentState).isFlammableZeta(parentState, world, pos, face);
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		BlockState parentState = parent.getBlock().defaultBlockState();
		return Quark.ZETA.blockExtensions.get(parentState).getFlammabilityZeta(parentState, world, pos, face);
	}
	
	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(parent.isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return parent.getModule();
	}

	@Override
	public QuarkSlabBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		BlockState parentState = parent.getBlock().defaultBlockState();
		return Quark.ZETA.blockExtensions.get(parentState).getBeaconColorMultiplierZeta(parentState, world, pos, beaconPos);
	}

	@Override
	public @Nullable String getBlockColorProviderName() {
		return parent instanceof IZetaBlockColorProvider prov ? prov.getBlockColorProviderName() : null;
	}

	@Override
	public @Nullable String getItemColorProviderName() {
		return parent instanceof IZetaItemColorProvider prov ? prov.getItemColorProviderName() : null;
	}
}