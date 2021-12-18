/*******************************************************************************
 *******************************************************************************/
package fr.fifoube.blocks;


import fr.fifoube.blocks.tileentity.TileEntityBlockVault2by2;
import fr.fifoube.items.ItemsRegistery;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;

import java.util.UUID;

public class BlockVault2by2 extends ContainerBlock {

 	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	private static final TranslationTextComponent NAME = new TranslationTextComponent("container.vault2by2");
	
	public static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0, 0, 0, 2D, 2, 1D);
	public static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(-1D, 0, 0, 1D, 2, 1D);
	public static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0, 0, 0, 1D, 2, 2D);
	public static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0, 0, -1D, 1D, 2, 1D);
	
	public static final VoxelShape NORTH_VOXELSHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 32.0D, 32.0D, 16.0D);

	public static VoxelShape shapeMain;

	
	public BlockVault2by2(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
		shapeMain = VoxelShapes.create(NORTH_AABB);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityBlockVault2by2();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	public void dropBlocks(TileEntity tileentity, World world, BlockPos pos) {
		
		
		if(tileentity instanceof TileEntityBlockVault2by2)
		{
			TileEntityBlockVault2by2 te = (TileEntityBlockVault2by2)tileentity;
			IItemHandler inventory = te.getHandler();
			ItemEntity itemBase = new ItemEntity(world, pos.getX() + 0.5, pos.getY()+0.5, pos.getZ() +0.5, new ItemStack(BlocksRegistry.BLOCK_VAULT, 4));
			world.addEntity(itemBase);
			if(inventory != null)
			{
				for(int i=0; i < inventory.getSlots(); i++)
				{
					if(inventory.getStackInSlot(i) != ItemStack.EMPTY)
					{
						ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, pos.getY()+0.5, pos.getZ() +0.5, inventory.getStackInSlot(i));
						
						float multiplier = 0.1f;
						float motionX = world.rand.nextFloat() - 0.5F;
						float motionY = world.rand.nextFloat() - 0.5F;
						float motionZ = world.rand.nextFloat() - 0.5F;
						
						item.lastTickPosX = motionX * multiplier;
						item.lastTickPosY = motionY * multiplier;
						item.lastTickPosZ = motionZ * multiplier;
						
						world.addEntity(item);
					}
				}
			}
		}	
	}
	
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		
		worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);
		TileEntity tileentity = worldIn.getTileEntity(pos);
    	if(tileentity instanceof TileEntityBlockVault2by2)
    	{
    		TileEntityBlockVault2by2 te = (TileEntityBlockVault2by2)tileentity;
    		te.setOwner(placer.getUniqueID());
    		int direction = MathHelper.floor((double) (placer.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
    		te.setDirection((byte) direction);
    	}
    	
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(!worldIn.isRemote)
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);		
			if(tileentity instanceof TileEntityBlockVault2by2)
			{
				TileEntityBlockVault2by2 te = (TileEntityBlockVault2by2)tileentity;
				if(te.getOwner() != null)
				{
					UUID checkONBT = te.getOwner();
					UUID checkOBA = player.getUniqueID();
					
					if(checkONBT.equals(checkOBA))
					{
			            NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)tileentity, buf -> buf.writeBlockPos(pos));
						te.markDirty();
						return ActionResultType.SUCCESS;
					}
					else if(player.hasPermissionLevel(4))
					{
			            NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, buf -> buf.writeBlockPos(pos));
						te.markDirty();
						return ActionResultType.SUCCESS;
					}
					else
					{
						for(int i = 0; i < te.getAllowedPlayers().size(); i++)
						{
							String fullString = te.getAllowedPlayers().get(i);
							String listToCheck = fullString.substring(fullString.indexOf(",") + 1);
							if(player.getUniqueID().equals(UUID.fromString(listToCheck)))
							{
					            NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)tileentity, buf -> buf.writeBlockPos(pos));
								te.markDirty();
								return ActionResultType.SUCCESS;
							}
						}
					}
	
				}
				
			}
		}
         return ActionResultType.FAIL;
	}

	@Override
	public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity instanceof TileEntityBlockVault2by2)
		{
			TileEntityBlockVault2by2 te = (TileEntityBlockVault2by2) tileentity;
			ItemStack stack = player.getHeldItemMainhand();
			state = worldIn.getBlockState(pos);
			
			if(te != null)
			{
				if(stack.isItemEqual(new ItemStack(ItemsRegistery.ITEM_REMOVER)))
				{
					UUID checkONBT = te.getOwner();
					UUID checkOBA = player.getUniqueID();
					
					if(checkONBT.equals(checkOBA))
					{
						worldIn.destroyBlock(pos, false);
						dropBlocks(te, worldIn, pos);
						worldIn.removeTileEntity(pos);
					}
				}
			}
		}
	}
	
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		
		this.setDefaultFacing(worldIn, pos, state);
	}
	
	 private void setDefaultFacing(World worldIn, BlockPos pos, BlockState state) {
	        if (!worldIn.isRemote)
	        {
	            BlockState blockstate = worldIn.getBlockState(pos.north());
	            BlockState blockstate1 = worldIn.getBlockState(pos.south());
	            BlockState blockstate2 = worldIn.getBlockState(pos.west());
	            BlockState blockstate3 = worldIn.getBlockState(pos.east());
	            Direction dir = (Direction)state.get(FACING);

	            if (dir == Direction.NORTH && blockstate.isSolid() && blockstate1.isSolid())
	            {
	                dir = Direction.SOUTH;
	            }
	            else if (dir == Direction.SOUTH && blockstate1.isSolid() && blockstate.isSolid())
	            {
	            	dir = Direction.NORTH;
	            }
	            else if (dir == Direction.WEST && blockstate2.isSolid() && blockstate3.isSolid())
	            {
	            	dir = Direction.EAST;
	            }
	            else if (dir == Direction.EAST && blockstate3.isSolid() && blockstate2.isSolid())
	            {
	            	dir = Direction.WEST;
	            }
	            worldIn.setBlockState(pos, state.with(FACING, dir), 2);
	        }
	    }
		
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate((Direction)state.get(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation((Direction)state.get(FACING)));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
    
	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}
	
	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
	     return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		return getShapeMainFromDir(state);
	}
	
	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return getShapeMainFromDir(state);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return getShapeMainFromDir(state);
	}
	
	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return getShapeMainFromDir(state);
	}
	
	public VoxelShape getShapeMainFromDir(BlockState state)
	{
    	Direction dir = (Direction)state.get(FACING);
		switch (dir) {
		case NORTH:
			shapeMain = VoxelShapes.create(NORTH_AABB);
			break;
		case SOUTH:
			shapeMain = VoxelShapes.create(SOUTH_AABB);
			break;
		case WEST:
			shapeMain = VoxelShapes.create(WEST_AABB);
			break;
		case EAST:
			shapeMain = VoxelShapes.create(EAST_AABB);
			break;
		default:
			shapeMain = VoxelShapes.create(NORTH_AABB);
			break;
		}
		return shapeMain;
	}
	
	
	
}
