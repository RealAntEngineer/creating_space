package com.rae.creatingspace.recipes;

import java.util.ArrayList;

public interface IMoreNbtConditions {
    void setKeepNbt(ArrayList<String> nbtKeys);

    void setMachNbt(boolean value);

    boolean isKeepNbt();

    boolean isMachNbt();
}
