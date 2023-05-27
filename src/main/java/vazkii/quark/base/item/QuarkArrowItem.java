package vazkii.quark.base.item;

import java.util.function.BooleanSupplier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.QuarkModule;

public abstract class QuarkArrowItem extends ArrowItem implements IQuarkItem {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkArrowItem(String name, QuarkModule module) {
		super(new Item.Properties());

		RegistryHelper.registerItem(this, name);
		RegistryHelper.setCreativeTab(this, CreativeModeTabs.COMBAT);
		this.module = module;
	}

	@Override
	public QuarkArrowItem setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}
	
	public static class Impl extends QuarkArrowItem {

		private final ArrowCreator creator;
		
		public Impl(String name, QuarkModule module, ArrowCreator creator) {
			super(name, module);
			this.creator = creator;
		}
		
		@Override
		public AbstractArrow createArrow(Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_) {
			return creator.createArrow(p_40513_, p_40514_, p_40515_);
		}
		
		public static interface ArrowCreator {
			public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity living);
		}
		
	}
	
}
