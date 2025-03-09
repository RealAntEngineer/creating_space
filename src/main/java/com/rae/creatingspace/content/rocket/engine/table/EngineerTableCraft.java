package com.rae.creatingspace.content.rocket.engine.table;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class EngineerTableCraft extends BlockEntityConfigurationPacket<RocketEngineerTableBlockEntity> {
    ItemStack engineBlueprint;

    public EngineerTableCraft(BlockPos pos, ItemStack engineBlueprint) {
        super(pos);
        this.engineBlueprint = engineBlueprint;
    }


    public EngineerTableCraft(BlockPos pos) {
        super(pos);
    }

    public EngineerTableCraft(FriendlyByteBuf buffer) {
        super(buffer);
    }


    public static EngineerTableCraft sendCraft(BlockPos pos, ItemStack engineBluePrint) {
        EngineerTableCraft packet = new EngineerTableCraft(pos);
        packet.engineBlueprint = engineBluePrint;
        return packet;
    }


    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeItem(engineBlueprint);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        engineBlueprint = buffer.readItem();
    }

    @Override
    protected void applySettings(RocketEngineerTableBlockEntity sealerBlockEntity) {
        sealerBlockEntity.craftEngine(engineBlueprint);
    }
}
