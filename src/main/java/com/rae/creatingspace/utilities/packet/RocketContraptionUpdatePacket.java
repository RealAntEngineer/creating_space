package com.rae.creatingspace.utilities.packet;

import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class RocketContraptionUpdatePacket  extends SimplePacketBase {

    public int entityID;
    public double coord;
    public double motion;

    public RocketContraptionUpdatePacket(int entityID, double coord, double motion) {
        this.entityID = entityID;
        this.coord = coord;
        this.motion = motion;
    }

    public RocketContraptionUpdatePacket(FriendlyByteBuf buffer) {
        entityID = buffer.readInt();
        coord = buffer.readFloat();
        motion = buffer.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityID);
        buffer.writeFloat((float) coord);
        buffer.writeFloat((float) motion);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(
                () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RocketContraptionEntity.handlePacket(this)));
        return true;
    }
}
