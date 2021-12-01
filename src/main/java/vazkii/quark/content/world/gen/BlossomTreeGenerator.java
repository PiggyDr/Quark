package vazkii.quark.content.world.gen;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluids;
import vazkii.quark.base.world.generator.Generator;
import vazkii.quark.content.world.block.BlossomSaplingBlock.BlossomTree;
import vazkii.quark.content.world.config.BlossomTreeConfig;

public class BlossomTreeGenerator extends Generator {

	BlossomTreeConfig config;
	BlossomTree tree;
	
	public BlossomTreeGenerator(BlossomTreeConfig config, BlossomTree tree) {
		super(config.dimensions);
		this.config = config;
		this.tree = tree;
	}

	@Override
	public void generateChunk(WorldGenRegion worldIn, ChunkGenerator generator, Random rand, BlockPos pos) {
		BlockPos placePos = pos.offset(rand.nextInt(16), 0, rand.nextInt(16));
		if(config.biomeConfig.canSpawn(getBiome(worldIn, placePos, false)) && rand.nextInt(config.rarity) == 0) {
			placePos = worldIn.getHeightmapPos(Types.MOTION_BLOCKING, placePos).below();

			BlockState state = worldIn.getBlockState(placePos);
			if(state.getBlock().canSustainPlant(state, worldIn, pos, Direction.UP, (SaplingBlock) Blocks.OAK_SAPLING)) {
				BlockPos up = placePos.above();
				BlockState upState = worldIn.getBlockState(up);
				if(upState.canBeReplaced(Fluids.WATER))
					worldIn.setBlock(up, Blocks.AIR.defaultBlockState(), 0);
				
				Feature.TREE.place(worldIn, generator, rand, placePos, tree.config);
			}
		}
	}

}
