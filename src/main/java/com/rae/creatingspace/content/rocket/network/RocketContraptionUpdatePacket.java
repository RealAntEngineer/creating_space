package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.content.rocket.contraption.entity.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class RocketContraptionUpdatePacket  extends SimplePacketBase {

    public int entityID;
    public Vec3 coord;
    public Vec3 speed;
    public float yaw;
    public float prevYaw;
    public float pitch;
    public float prevPitch;

    public RocketContraptionUpdatePacket(int entityID, Vec3 coord, Vec3 speed, float yaw,float prevYaw, float pitch, float prevPitch) {
        this.entityID = entityID;
        this.coord = coord;
        this.speed = speed;
        this.yaw = yaw;
        this.pitch = pitch;
        this.prevYaw = prevYaw;
        this.prevPitch = prevPitch;
    }

    public RocketContraptionUpdatePacket(FriendlyByteBuf buffer) {
        entityID = buffer.readInt();
        coord = new Vec3(buffer.readVector3f());
        speed = new Vec3(buffer.readVector3f());
        yaw = buffer.readFloat();
        prevYaw = buffer.readFloat();
        pitch = buffer.readFloat();
        prevPitch = buffer.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityID);
        buffer.writeVector3f(coord.toVector3f());
        buffer.writeVector3f(speed.toVector3f());
        buffer.writeFloat(yaw);
        buffer.writeFloat(prevYaw);
        buffer.writeFloat(pitch);
        buffer.writeFloat(prevPitch);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(
                () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RocketContraptionEntity.handlePacket(this)));
        return true;
    }
}
