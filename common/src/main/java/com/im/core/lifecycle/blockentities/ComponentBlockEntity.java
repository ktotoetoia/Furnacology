package com.im.core.lifecycle.blockentities;

import com.im.core.statemachine.statemachine.ITickable;
import com.im.infrastructure.saving.IItemDataSerializable;
import com.im.infrastructure.saving.IStateSerializable;
import com.im.infrastructure.saving.ISyncSerializable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ComponentBlockEntity extends BlockEntity implements ICompositeBlockEntity, MenuProvider
{
    private static final String SyncTag = "sync";

    private final List<Object> _components;
    private boolean _syncRequested;

    public ComponentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Function<ComponentBlockEntity, List<Object>> assembler)
    {
        super(type, pos, state);
        _components = List.copyOf(assembler.apply(this));
    }

    public Collection<Object> getCollection() { return _components; }
    public boolean isServer() { return level != null && !level.isClientSide(); }
    public boolean opensMenu() { return hasOfType(MenuProvider.class); }
    public void requestSync() {  _syncRequested = true; }
    public Direction getFacing() { return getBlockState().getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(Direction.NORTH); }
    public WorldlyContainer getAccess() {  return get(WorldlyContainer.class); }

    public void dropItem(ItemStack stack)
    {
        if (level != null && !stack.isEmpty()) Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
    }

    public void tick()
    {
        if (level == null) return;

        getAll(ITickable.class).forEach(ITickable::tick);

        if (level.isClientSide()) return;

        if (_syncRequested) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);

        _syncRequested = false;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        getAll(IStateSerializable.class).forEach(serializable -> serializable.captureState(tag, registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        if (!tag.contains(SyncTag))
        {
            getAll(IStateSerializable.class).forEach(serializable -> serializable.restoreState(tag, registries));
            return;
        }

        CompoundTag sync = tag.getCompound(SyncTag);

        if (!sync.isEmpty()) getAll(ISyncSerializable.class).forEach(serializable -> serializable.restoreSync(sync));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        CompoundTag sync = new CompoundTag();
        CompoundTag tag = new CompoundTag();

        getAll(ISyncSerializable.class).forEach(serializable -> serializable.captureSync(sync));
        tag.put(SyncTag, sync);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder)
    {
        super.collectImplicitComponents(builder);
        getAll(IItemDataSerializable.class).forEach(data -> data.collectImplicit(builder));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input)
    {
        super.applyImplicitComponents(input);

        IItemDataSerializable.IDataSource source = new IItemDataSerializable.IDataSource()
        {
            public <T> T get(DataComponentType<T> type) { return input.get(type); }
        };

        getAll(IItemDataSerializable.class).forEach(data -> data.applyImplicit(source));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        super.removeComponentsFromTag(tag);
        getAll(IItemDataSerializable.class).forEach(data -> data.removeFromTag(tag));
    }

    public Component getDisplayName() { return tryGet(MenuProvider.class).map(MenuProvider::getDisplayName).orElseGet(() -> getBlockState().getBlock().getName()); }

    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return tryGet(MenuProvider.class).map(menu -> menu.createMenu(containerId, inventory, player)).orElse(null);
    }
}