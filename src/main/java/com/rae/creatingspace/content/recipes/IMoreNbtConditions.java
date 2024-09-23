package com.rae.creatingspace.content.recipes;

import java.util.ArrayList;

public interface IMoreNbtConditions {
    void setKeepNbt(ArrayList<String> nbtKeys);

    ArrayList<String> getKeepNbt();

    void setMachNbt(ArrayList<String> machNbtList);

    ArrayList<String> getMachNbt();
    boolean isKeepNbt();

    boolean isMachNbt();
}
