package vazkii.quark.content.mobs.module;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.EntityAttributeHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.CostSensitiveEntitySpawnConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.SoulBeadRenderer;
import vazkii.quark.content.mobs.client.render.WraithRenderer;
import vazkii.quark.content.mobs.entity.SoulBeadEntity;
import vazkii.quark.content.mobs.entity.WraithEntity;
import vazkii.quark.content.mobs.item.SoulBeadItem;

@LoadModule(category = ModuleCategory.MOBS)
public class WraithModule extends QuarkModule {
	
	public static EntityType<WraithEntity> wraithType;
	public static EntityType<SoulBeadEntity> soulBeadType;

	@Config(description = "List of sound sets to use with wraiths.\nThree sounds must be provided per entry, separated by | (in the format idle|hurt|death). Leave blank for no sound (i.e. if a mob has no ambient noise)")
	private static List<String> wraithSounds  = Lists.newArrayList(
			"entity.sheep.ambient|entity.sheep.hurt|entity.sheep.death",
			"entity.cow.ambient|entity.cow.hurt|entity.cow.death",
			"entity.pig.ambient|entity.pig.hurt|entity.pig.death",
			"entity.chicken.ambient|entity.chicken.hurt|entity.chicken.death",
			"entity.horse.ambient|entity.horse.hurt|entity.horse.death",
			"entity.cat.ambient|entity.cat.hurt|entity.cat.death",
			"entity.wolf.ambient|entity.wolf.hurt|entity.wolf.death",
			"entity.villager.ambient|entity.villager.hurt|entity.villager.death",
			"entity.polar_bear.ambient|entity.polar_bear.hurt|entity.polar_bear.death",
			"entity.zombie.ambient|entity.zombie.hurt|entity.zombie.death",
			"entity.skeleton.ambient|entity.skeleton.hurt|entity.skeleton.death",
			"entity.spider.ambient|entity.spider.hurt|entity.spider.death",
			"|entity.creeper.hurt|entity.creeper.death",
			"entity.endermen.ambient|entity.endermen.hurt|entity.endermen.death",
			"entity.zombie_pig.ambient|entity.zombie_pig.hurt|entity.zombie_pig.death",
			"entity.witch.ambient|entity.witch.hurt|entity.witch.death",
			"entity.blaze.ambient|entity.blaze.hurt|entity.blaze.death",
			"entity.llama.ambient|entity.llama.hurt|entity.llama.death",
			"|quark:entity.stoneling.cry|quark:entity.stoneling.die",
			"quark:entity.frog.idle|quark:entity.frog.hurt|quark:entity.frog.die"
			);
	
	@Config
	public static EntitySpawnConfig spawnConfig = new CostSensitiveEntitySpawnConfig(8, 1, 3, 0.7, 0.15, CompoundBiomeConfig.fromBiomeReslocs(false, "minecraft:soul_sand_valley"));
	
	public static Tag<Block> wraithSpawnableTag;
	
	public static List<String> validWraithSounds;

	@Override
	public void construct() {
		new SoulBeadItem(this);
		
		wraithType = EntityType.Builder.of(WraithEntity::new, MobCategory.MONSTER)
				.sized(0.6F, 1.95F)
				.clientTrackingRange(8)
				.fireImmune()
				.setCustomClientFactory((spawnEntity, world) -> new WraithEntity(wraithType, world))
				.build("wraith");
		RegistryHelper.register(wraithType, "wraith");
		
		soulBeadType = EntityType.Builder.of(SoulBeadEntity::new, MobCategory.MISC)
				.sized(0F, 0F)
				.clientTrackingRange(4)
				.updateInterval(10) // update frequency
				.fireImmune()
				.setCustomClientFactory((spawnEntity, world) -> new SoulBeadEntity(soulBeadType, world))
				.build("soul_bead");
		RegistryHelper.register(soulBeadType, "soul_bead");

		EntitySpawnHandler.registerSpawn(this, wraithType, MobCategory.MONSTER, Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, spawnConfig);
		EntitySpawnHandler.addEgg(wraithType, 0xececec, 0xbdbdbd, spawnConfig);
		
		EntityAttributeHandler.put(wraithType, WraithEntity::registerAttributes);
	}
	
	@Override
	public void setup() {
		wraithSpawnableTag = BlockTags.createOptional(new ResourceLocation(Quark.MOD_ID, "wraith_spawnable"));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(wraithType, WraithRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(soulBeadType, SoulBeadRenderer::new);
	}
	
	@Override
	public void configChanged() {
		validWraithSounds = wraithSounds.stream().filter((s) -> s.split("\\|").length == 3).collect(Collectors.toList());
	}
	
}
