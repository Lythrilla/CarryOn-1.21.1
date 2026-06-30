package tschipp.carryon.carry;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tschipp.carryon.common.carry.CarryOnData;

public class CarryOnDataCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<ICarryOnDataCapability> CARRY_ON_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<ICarryOnDataCapability>() {});

    private final CarryOnDataCapability impl = new CarryOnDataCapability();
    private final LazyOptional<ICarryOnDataCapability> opt = LazyOptional.of(() -> impl);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CARRY_ON_DATA_CAPABILITY ? opt.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
        return (CompoundTag) CarryOnData.CODEC.encodeStart(NbtOps.INSTANCE, impl.getCarryData()).getOrThrow();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
        CarryOnData data = CarryOnData.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow();
        impl.setCarryData(data);
    }
}
