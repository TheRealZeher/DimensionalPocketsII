package com.tcn.cosmoslibrary.impl.util;

import javax.annotation.Nonnull;

import com.tcn.cosmoslibrary.client.impl.util.TextHelper;
import com.tcn.cosmoslibrary.impl.interfaces.tile.ISidedTile;

import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Collection of useful utilities connected to AZRF.
 */
public class CompatUtil {
	
	/**
	 * Used to generate an {@link ItemStack} from a block to produce a {@link ItemBlock} with the required NBT data.
	 * @param {@link World} [given world the block is in]
	 * @param {@link BlockPos} [position of the given block]
	 */
	@SuppressWarnings("static-access")
	public static void generateStack(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		Block block = world.getBlockState(pos).getBlock();
		
		ItemStack stack = new ItemStack(block);
		CompoundNBT compound = new CompoundNBT();
		stack.setTag(new CompoundNBT());
		//ListNBT list = new ListNBT();
		
		if (tile != null) {
			if (tile instanceof IInventory) {
				if (!((IInventory) tile).isEmpty()) {
					int size = ((IInventory) tile).getSizeInventory();
					
					NonNullList<ItemStack> list_ = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
					
					for (int i = 0; i < size; i++) {
						list_.set(i, ((IInventory) tile).getStackInSlot(i));
					}
					
					ItemStackHelper.saveAllItems(compound, list_);
					compound.putInt("size", size);
				}
			}
		
			if (tile instanceof ISidedTile) {
				CompoundNBT compound_tag = new CompoundNBT();
				compound.put("sides", compound_tag);
				
				for (Direction c : Direction.values()) {
					compound_tag.putInt("index_" + c.getIndex(), ((ISidedTile) tile).getSideArray()[c.getIndex()].getIndex());
				}
			}
			/**
			if (tile instanceof IEnergyHandler) {
				compound.setInteger("energy", ((IEnergyHandler) tile).getEnergyStored(Direction.DOWN));
			} */
			
			stack.getTag().put("nbt_data", compound);
		}
		
		block.spawnAsEntity(world, pos, stack);
		world.removeBlock(pos, false);
	}
	
	/**
	 * Generates a blank itemstack when NBT data is not required.
	 * @param block [block to generate from]
	 * @return {@link ItemStack} [itemstack containing the block]
	 */
	public static ItemStack generateItemStackFromTile(Block block) {
		return new ItemStack(block);
	}
	
	public static void spawnStack(ItemStack itemStack, World world, double d, double e, double f, int delayBeforePickup) {
		ItemEntity entityItem = new ItemEntity(world, d, e, f, itemStack);
		entityItem.setPickupDelay(delayBeforePickup);

		world.addEntity(entityItem);
	}

	public static ItemStack generateItem(ItemStack itemStack, String name, boolean forceCleanName, String... loreStrings) {
		CompoundNBT nbt = itemStack.getTag();
		CompoundNBT display;
		if (nbt == null) {
			nbt = new CompoundNBT();
			itemStack.setTag(nbt);
		}
		if (!itemStack.getTag().contains("display")) {
			itemStack.setTagInfo("display", new CompoundNBT());
		}

		display = (CompoundNBT) itemStack.getTag().get("display");

		if (loreStrings != null && loreStrings.length > 0) {
			ListNBT lore = new ListNBT();
			for (String s : loreStrings) {
				if (s != null) {
					lore.add(StringNBT.valueOf(TextHelper.GRAY + s));
				}
			}
			display.put("Lore", lore);
		}

		if (name != null) {
			StringBuilder sb = new StringBuilder();
			if (forceCleanName) {
				sb.append(TextHelper.END);
			}
			sb.append(name);

			display.putString("Name", sb.toString());
		}

		return itemStack;
	}
	
	/**
	 * Returns null, while claiming to never return null.
	 * Useful for constants with @ObjectHolder who's values are null at compile time, but not at runtime
	 *
	 * @return null
	 */
	@Nonnull
	// Get rid of "Returning null from Nonnull method" warnings
	public static <T> T _null() {
		return null;
	}
}