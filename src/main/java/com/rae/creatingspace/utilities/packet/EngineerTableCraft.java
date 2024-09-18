package com.rae.creatingspace.utilities.packet;

import com.rae.creatingspace.server.blockentities.RocketEngineerTableBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class EngineerTableCraft extends BlockEntityConfigurationPacket<RocketEngineerTableBlockEntity> {
    ItemStack engine;

    public EngineerTableCraft(BlockPos pos, ItemStack engine) {
        super(pos);
        this.engine = engine;
    }


    public EngineerTableCraft(BlockPos pos) {
        super(pos);
    }

    public EngineerTableCraft(FriendlyByteBuf buffer) {
        super(buffer);
    }


    public static EngineerTableCraft sendCraft(BlockPos pos, ItemStack engine) {
        EngineerTableCraft packet = new EngineerTableCraft(pos);
        packet.engine = engine;
        return packet;
    }


    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeItem(engine);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        engine = buffer.readItem();
    }

    @Override
    protected void applySettings(RocketEngineerTableBlockEntity sealerBlockEntity) {
        sealerBlockEntity.craftEngine(engine);
    }
}
