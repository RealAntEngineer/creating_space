package com.rae.creatingspace.legacy.server.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.init.graphics.ParticleTypeInit;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class RocketPlumeParticleData implements ParticleOptions,ICustomParticleDataWithSprite<RocketPlumeParticleData>

    {
        public static final MapCodec<RocketPlumeParticleData> CODEC = RecordCodecBuilder.mapCodec(i ->
                i.group(
                                Codec.FLOAT.fieldOf("drag").forGetter(p -> p.drag))
                        .apply(i, RocketPlumeParticleData::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, RocketPlumeParticleData> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.FLOAT, p -> p.drag,
                        RocketPlumeParticleData::new
                );
        public float drag;


	    public RocketPlumeParticleData(float drag) {
        this.drag =drag;
    }

	    public RocketPlumeParticleData() {
        this(0);
    }

        @Override
        public ParticleType<?> getType() {
        return ParticleTypeInit.ROCKET_PLUME.get();
    }

        @Override
        public MapCodec<RocketPlumeParticleData> getCodec(ParticleType<RocketPlumeParticleData> type) {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RocketPlumeParticleData> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public ParticleEngine.SpriteParticleRegistration<RocketPlumeParticleData> getMetaFactory() {
        return PlumeParticle.Factory::new;
    }

    }
