package vazkii.quark.content.mobs.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
import vazkii.quark.content.mobs.client.render.FoxhoundRenderer;
import vazkii.quark.content.mobs.entity.FoxhoundEntity;

/**
 * @author WireSegal
 * Created at 5:00 PM on 9/26/19.
 */
@LoadModule(category = ModuleCategory.MOBS, hasSubscriptions = true)
public class FoxhoundModule extends QuarkModule {

	public static EntityType<FoxhoundEntity> foxhoundType;

	@Config(description = "The chance coal will tame a foxhound")
	public static double tameChance = 0.05;

	@Config
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(30, 1, 2, CompoundBiomeConfig.fromBiomeReslocs(false, "minecraft:nether_wastes", "minecraft:basalt_deltas"));
	
	@Config
	public static EntitySpawnConfig lesserSpawnConfig = new CostSensitiveEntitySpawnConfig(2, 1, 1, 0.7, 0.15, CompoundBiomeConfig.fromBiomeReslocs(false, "minecraft:soul_sand_valley"));
	
	public static Tag<Block> foxhoundSpawnableTag;
	
	@Override
	public void construct() {
		foxhoundType = EntityType.Builder.of(FoxhoundEntity::new, MobCategory.CREATURE)
				.sized(0.8F, 0.8F)
				.clientTrackingRange(8)
				.fireImmune()
				.setCustomClientFactory((spawnEntity, world) -> new FoxhoundEntity(foxhoundType, world))
				.build("foxhound");
		RegistryHelper.register(foxhoundType, "foxhound");

		EntitySpawnHandler.registerSpawn(this, foxhoundType, MobCategory.MONSTER, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FoxhoundEntity::spawnPredicate, spawnConfig);
		EntitySpawnHandler.track(this, foxhoundType, MobCategory.MONSTER, lesserSpawnConfig, true);
		
		EntitySpawnHandler.addEgg(foxhoundType, 0x890d0d, 0xf2af4b, spawnConfig);
		
		EntityAttributeHandler.put(foxhoundType, Wolf::createAttributes);
	}

	@Override
	public void setup() {		
		foxhoundSpawnableTag = BlockTags.createOptional(new ResourceLocation(Quark.MOD_ID, "foxhound_spawnable"));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(foxhoundType, FoxhoundRenderer::new);
	}

	@SubscribeEvent
	public void onAggro(LivingSetAttackTargetEvent event) {
		if(event.getTarget() != null 
				&& event.getEntityLiving().getType() == EntityType.IRON_GOLEM 
				&& event.getTarget().getType() == foxhoundType 
				&& ((FoxhoundEntity) event.getTarget()).isTame())
			((IronGolem) event.getEntityLiving()).setTarget(null);
	}
}
