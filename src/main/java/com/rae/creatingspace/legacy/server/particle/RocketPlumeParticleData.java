package com.rae.creatingspace.legacy.server.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.init.graphics.ParticleTypeInit;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

public class RocketPlumeParticleData implements ParticleOptions,ICustomParticleDataWithSprite<RocketPlumeParticleData>

    {
        public static final Codec<RocketPlumeParticleData> CODEC = RecordCodecBuilder.create(i ->
                i.group(
                                Codec.FLOAT.fieldOf("drag").forGetter(p -> p.drag))
                        .apply(i, RocketPlumeParticleData::new));
        public static final ParticleOptions.Deserializer<RocketPlumeParticleData> DESERIALIZER = new ParticleOptions.Deserializer<RocketPlumeParticleData>() {
            public RocketPlumeParticleData fromCommand(ParticleType<RocketPlumeParticleData> particleTypeIn, StringReader reader)
                    throws CommandSyntaxException {
                reader.expect(' ');
                float drag = reader.readFloat();
                return new RocketPlumeParticleData(drag);
            }

            public RocketPlumeParticleData fromNetwork(ParticleType<RocketPlumeParticleData> particleTypeIn, FriendlyByteBuf buffer) {
                return new RocketPlumeParticleData(buffer.readFloat());
            }
        };
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
        public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(drag);
    }

        @Override
        public String writeToString() {
        return String.format(Locale.ROOT, "%s %f", ParticleTypeInit.ROCKET_PLUME.parameter(), drag);
    }

        @Override
        public ParticleOptions.Deserializer<RocketPlumeParticleData> getDeserializer() {
        return DESERIALIZER;
    }

        @Override
        public Codec<RocketPlumeParticleData> getCodec(ParticleType<RocketPlumeParticleData> type) {
        return CODEC;
    }

        @Override
        @OnlyIn(Dist.CLIENT)
        public ParticleEngine.SpriteParticleRegistration<RocketPlumeParticleData> getMetaFactory() {
        return PlumeParticle.Factory::new;
    }

    }
