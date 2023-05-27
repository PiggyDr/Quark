package vazkii.quark.base.item.boat;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.handler.WoodSetHandler.QuarkBoatType;

public class QuarkChestBoat extends ChestBoat implements IQuarkBoat {

	private static final EntityDataAccessor<String> DATA_QUARK_TYPE = SynchedEntityData.defineId(QuarkChestBoat.class, EntityDataSerializers.STRING);

	public QuarkChestBoat(EntityType<? extends Boat> entityType, Level world) {
		super(entityType, world);
	}

	public QuarkChestBoat(Level world, double x, double y, double z) {
		this(WoodSetHandler.quarkChestBoatEntityType, world);
		this.setPos(x, y, z);
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(DATA_QUARK_TYPE, "blossom");
	}

	public String getQuarkBoatType() {
		return entityData.get(DATA_QUARK_TYPE);
	}

	public void setQuarkBoatType(String type) {
		entityData.set(DATA_QUARK_TYPE, type);
	}

	@Override
	protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putString("QuarkType", getQuarkBoatType());
	}

	@Override
	protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("QuarkType", 8)) {
			setQuarkBoatType(tag.getString("QuarkType"));
		}
	}

	@Override
	public ItemEntity spawnAtLocation(ItemLike itemLike) {
		if(ForgeRegistries.ITEMS.getKey(itemLike.asItem()).getPath().contains("_planks"))
			return super.spawnAtLocation(getQuarkBoatTypeObj().planks());
		return super.spawnAtLocation(itemLike);
	}

	@Nonnull
	@Override
	public Item getDropItem() {
		return getQuarkBoatTypeObj().chestBoat();
	}
	
	@Override
	public void setQuarkBoatTypeObj(QuarkBoatType type) {
		setQuarkBoatType(type.name());
	}

	@Override
	public QuarkBoatType getQuarkBoatTypeObj() {
		return WoodSetHandler.getQuarkBoatType(getQuarkBoatType());
	}

}
