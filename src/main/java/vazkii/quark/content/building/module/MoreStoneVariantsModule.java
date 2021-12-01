package vazkii.quark.content.building.module;

import java.util.function.BooleanSupplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkPillarBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;
import vazkii.quark.content.building.block.MyalitePillarBlock;
import vazkii.quark.content.world.block.MyaliteBlock;
import vazkii.quark.content.world.module.NewStoneTypesModule;

@LoadModule(category = ModuleCategory.BUILDING)
public class MoreStoneVariantsModule extends QuarkModule {

	@Config(flag = "stone_bricks") public boolean enableBricks = true;
	@Config(flag = "stone_chiseled") public boolean enableChiseledBricks = true;
	@Config(flag = "stone_pavement") public boolean enablePavement = true;
	@Config(flag = "stone_pillar") public boolean enablePillar = true;
	
	@Override
	public void construct() {
		BooleanSupplier _true = () -> true;
		add("granite", MaterialColor.DIRT, _true);
		add("diorite", MaterialColor.QUARTZ, _true);
		add("andesite", MaterialColor.STONE, _true);
		
		add("marble", MaterialColor.QUARTZ, () -> NewStoneTypesModule.enabledWithMarble);
		add("limestone", MaterialColor.STONE, () -> NewStoneTypesModule.enabledWithLimestone);
		add("jasper", MaterialColor.TERRACOTTA_RED, () -> NewStoneTypesModule.enabledWithJasper);
		add("slate", MaterialColor.ICE, () -> NewStoneTypesModule.enabledWithSlate);
		
		add("myalite", MaterialColor.COLOR_PURPLE, () -> NewStoneTypesModule.enabledWithMyalite, MyaliteBlock::new, MyalitePillarBlock::new);
	}
	
	@Override
	public void pushFlags(ConfigFlagManager manager) {
		manager.putFlag(this, "granite", true);
		manager.putFlag(this, "diorite", true);
		manager.putFlag(this, "andesite", true);
	}
	
	private void add(String name, MaterialColor color, BooleanSupplier cond) {
		add(name, color, cond, QuarkBlock::new, QuarkPillarBlock::new);
	}
	
	private void add(String name, MaterialColor color, BooleanSupplier cond, QuarkBlock.Constructor<QuarkBlock> constr, QuarkBlock.Constructor<QuarkPillarBlock> pillarConstr) {
		Block.Properties props = Block.Properties.of(Material.STONE, color)
				.requiresCorrectToolForDrops()
//        		.harvestTool(ToolType.PICKAXE) TODO tag
        		.strength(1.5F, 6.0F);
		
		QuarkBlock bricks = constr.make(name + "_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(() -> cond.getAsBoolean() && enableBricks);
		VariantHandler.addSlabStairsWall(bricks);
		
		constr.make("chiseled_" + name + "_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(() -> cond.getAsBoolean() && enableBricks && enableChiseledBricks);
		constr.make(name + "_pavement", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(() -> cond.getAsBoolean() && enablePavement);
		pillarConstr.make(name + "_pillar", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(() -> cond.getAsBoolean() && enablePillar);
	}
	
}
