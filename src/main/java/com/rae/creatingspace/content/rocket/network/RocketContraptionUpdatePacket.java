package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class RocketContraptionUpdatePacket  extends SimplePacketBase {

    public int entityID;
    public double coord;
    public double speed;

    public RocketContraptionUpdatePacket(int entityID, double coord, double speed) {
        this.entityID = entityID;
        this.coord = coord;
        this.speed = speed;
    }

    public RocketContraptionUpdatePacket(FriendlyByteBuf buffer) {
        entityID = buffer.readInt();
        coord = buffer.readFloat();
        speed = buffer.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityID);
        buffer.writeFloat((float) coord);
        buffer.writeFloat((float) speed);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(
                () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RocketContraptionEntity.handlePacket(this)));
        return true;
    }
}
