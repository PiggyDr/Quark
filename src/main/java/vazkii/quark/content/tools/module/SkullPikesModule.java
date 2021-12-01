package vazkii.quark.content.tools.module;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.tools.ai.RunAwayFromPikesGoal;
import vazkii.quark.content.tools.client.render.SkullPikeRenderer;
import vazkii.quark.content.tools.entity.SkullPikeEntity;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class SkullPikesModule extends QuarkModule {

	public static EntityType<SkullPikeEntity> skullPikeType;

    public static Tag<Block> pikeTrophiesTag;
    
    @Config public static double pikeRange = 5;
	
	@Override
	public void construct() {
		skullPikeType = EntityType.Builder.<SkullPikeEntity>of(SkullPikeEntity::new, MobCategory.MISC)
				.sized(0.5F, 0.5F)
				.clientTrackingRange(3)
				.updateInterval(Integer.MAX_VALUE) // update interval
				.setShouldReceiveVelocityUpdates(false)
				.setCustomClientFactory((spawnEntity, world) -> new SkullPikeEntity(skullPikeType, world))
				.build("skull_pike");
		RegistryHelper.register(skullPikeType, "skull_pike");
	}
	
    @Override
    public void setup() {
    	pikeTrophiesTag = BlockTags.createOptional(new ResourceLocation(Quark.MOD_ID, "pike_trophies"));
    }
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(skullPikeType, SkullPikeRenderer::new);
	}

	@SubscribeEvent
	public void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
		BlockState state = event.getPlacedBlock();
		
		if(state.getBlock().is(pikeTrophiesTag)) {
			LevelAccessor iworld = event.getWorld();
			
			if(iworld instanceof Level) {
				Level world = (Level) iworld;
				BlockPos pos = event.getPos();
				BlockPos down = pos.below();
				BlockState downState = world.getBlockState(down);
				
				if(downState.getBlock().is(BlockTags.FENCES)) {
					SkullPikeEntity pike = new SkullPikeEntity(skullPikeType, world);
					pike.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
					world.addFreshEntity(pike);
				}
			}
		}
	}
	
    @SubscribeEvent
    public void onMonsterAppear(EntityJoinWorldEvent event) {
    	Entity e = event.getEntity();
        if(e instanceof Monster && !(e instanceof PatrollingMonster) && e.canChangeDimensions()) {
        	Monster monster = (Monster) e;
            boolean alreadySetUp = monster.goalSelector.availableGoals.stream().anyMatch((goal) -> goal.getGoal() instanceof RunAwayFromPikesGoal);

            if (!alreadySetUp)
            	monster.goalSelector.addGoal(3, new RunAwayFromPikesGoal(monster, (float) pikeRange, 1.0D, 1.2D));
        }
    }
}
