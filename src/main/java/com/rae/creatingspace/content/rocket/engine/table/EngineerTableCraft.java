package com.rae.creatingspace.content.rocket.engine.table;

import com.rae.creatingspace.init.PacketInit;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EngineerTableCraft extends BlockEntityConfigurationPacket<RocketEngineerTableBlockEntity> {
    ItemStack engineBlueprint;
    public static final StreamCodec<RegistryFriendlyByteBuf,EngineerTableCraft> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ItemStack.STREAM_CODEC, packet -> packet.engineBlueprint,
            EngineerTableCraft::new
    );

    public EngineerTableCraft(BlockPos pos, ItemStack engineBlueprint) {
        super(pos);
        this.engineBlueprint = engineBlueprint;
    }


    public EngineerTableCraft(BlockPos pos) {
        super(pos);
    }

    @Override
    protected void applySettings(ServerPlayer player, RocketEngineerTableBlockEntity rocketEngineerTableBlockEntity) {
        rocketEngineerTableBlockEntity.craftEngine(engineBlueprint);

    }

    public static EngineerTableCraft sendCraft(BlockPos pos, ItemStack engineBluePrint) {
        EngineerTableCraft packet = new EngineerTableCraft(pos);
        packet.engineBlueprint = engineBluePrint;
        return packet;
    }


    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.CRAFT_ENGINE;
    }
}
