package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.api.contraption.Synced2AxisContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class SpeedPosRotUpdatePacket extends SimplePacketBase {

    public int entityID;
    public Vec3 coord;
    public Vec3 speed;
    public float yaw;
    public float pitch;
    public Vec2 rotSpeed;

    public SpeedPosRotUpdatePacket(int entityID, Vec3 coord, Vec3 speed, float yaw, float pitch, Vec2 rotSpeed) {
        this.entityID = entityID;
        this.coord = coord;
        this.speed = speed;
        this.yaw = yaw;
        this.pitch = pitch;
        this.rotSpeed = rotSpeed;
    }

    public SpeedPosRotUpdatePacket(FriendlyByteBuf buffer) {
        entityID = buffer.readInt();
        coord = new Vec3(buffer.readVector3f());
        speed = new Vec3(buffer.readVector3f());
        yaw = buffer.readFloat();
        pitch = buffer.readFloat();
        rotSpeed = new Vec2(buffer.readFloat(), buffer.readFloat());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityID);
        buffer.writeVector3f(coord.toVector3f());
        buffer.writeVector3f(speed.toVector3f());
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
        buffer.writeFloat(rotSpeed.x);
        buffer.writeFloat(rotSpeed.y);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(
                () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Synced2AxisContraptionEntity.handlePacket(this)));
        return true;
    }
}
