package vazkii.quark.content.tools.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.tools.module.BottledCloudModule;
import vazkii.quark.content.tools.tile.CloudTileEntity;

public class CloudBlock extends QuarkBlock {

	public CloudBlock(QuarkModule module) {
		super("cloud", module, null, 
				Block.Properties.of(Material.CLAY)
				.sound(SoundType.WOOL)
				.strength(0)
				.noOcclusion());
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult raytrace) {
		ItemStack stack = player.getItemInHand(hand);
		
		if(stack.getItem() == Items.GLASS_BOTTLE) {
			fillBottle(player, player.inventory.selected);
			world.removeBlock(pos, false);
			return InteractionResult.SUCCESS;
		}
		
		if(stack.getItem() instanceof BlockItem) {
			BlockItem bitem = (BlockItem) stack.getItem();
			Block block = bitem.getBlock();
			
			UseOnContext context = new UseOnContext(player, hand, new BlockHitResult(new Vec3(0.5F, 1F, 0.5F), raytrace.getDirection(), pos, false));
			BlockPlaceContext bcontext = new BlockPlaceContext(context);
			
			BlockState stateToPlace = block.getStateForPlacement(bcontext);
			if(stateToPlace != null && stateToPlace.canSurvive(world, pos)) {
				world.setBlockAndUpdate(pos, stateToPlace);
				world.playSound(player, pos, stateToPlace.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1F, 1F);
				
				if(!player.isCreative()) {
					stack.shrink(1);
					fillBottle(player, 0);
				}
				
				return InteractionResult.SUCCESS;
			}
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		return ItemStack.EMPTY;
	}
	
	private void fillBottle(Player player, int startIndex) {
		Inventory inv = player.inventory;
		for(int i = startIndex ; i < inv.getContainerSize(); i++) {
			ItemStack stackInSlot = inv.getItem(i);
			if(stackInSlot.getItem() == Items.GLASS_BOTTLE) {
				stackInSlot.shrink(1);
				
				ItemStack give = new ItemStack(BottledCloudModule.bottled_cloud);
				if(!player.addItem(give))
					player.drop(give, false);
				return;
			}
		}
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new CloudTileEntity();
	}

}
