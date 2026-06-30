package tschipp.carryon.carry;

import net.minecraft.nbt.CompoundTag;
import tschipp.carryon.common.carry.CarryOnData;

public class CarryOnDataCapability implements ICarryOnDataCapability {

    private CarryOnData data;

    public CarryOnDataCapability() {
        this.data = new CarryOnData(new CompoundTag());
    }

    @Override
    public CarryOnData getCarryData() {
        return data;
    }

    @Override
    public void setCarryData(CarryOnData data) {
        this.data = data;
    }
}
