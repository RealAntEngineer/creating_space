package com.rae.creatingspace.api.planets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * registry object driven implementation of the old CustomDimensionParameter
 * because it was hideous
 */
public class RocketAccessibleDimension {
    public static final ResourceKey<Registry<RocketAccessibleDimension>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation("creatingspace:rocket_accessible_dimension"));
    public static final ResourceLocation BASE_BODY = new ResourceLocation("sun");
    public static final UnboundedMapCodec<ResourceLocation, AccessibilityParameter> ADJACENT_DIMENSIONS_CODEC =
            Codec.unboundedMap(ResourceLocation.CODEC, AccessibilityParameter.CODEC);
    //use ResourceLocation rather than ResourceKey
    public static final Codec<RocketAccessibleDimension> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.INT.fieldOf("distanceToOrbitingBody").forGetter(i -> i.distanceToOrbitingBody),
                            ResourceLocation.CODEC.optionalFieldOf("orbitedBody", BASE_BODY).forGetter(i -> i.orbitedBody),
                            Codec.INT.fieldOf("arrivalHeight").forGetter(i -> i.arrivalHeight),
                            Codec.FLOAT.fieldOf("gravity").forGetter(i -> i.gravity),
                            ADJACENT_DIMENSIONS_CODEC.fieldOf("adjacentDimensions").forGetter(i -> i.adjacentDimensions)
                    )
                    .apply(instance, RocketAccessibleDimension::new));
    int distanceToOrbitingBody;
    //in what ? km will be too much, Mm : 400 for the mun 1500000 for the sun ?
    // (with changes for visibility ?)
    int arrivalHeight;
    float gravity;
    ResourceLocation orbitedBody;//mostly used for the DestinationScreen and for falling out of an orbit

    public Map<ResourceLocation, AccessibilityParameter> adjacentDimensions() {
        return adjacentDimensions;
    }

    // is orbit should be more than just gravity = 0 no ?
    Map<ResourceLocation, AccessibilityParameter> adjacentDimensions;

    public RocketAccessibleDimension(int distanceToOrbitingBody, ResourceLocation orbitedBody, int arrivalHeight, float gravity, Map<ResourceLocation, AccessibilityParameter> adjacentDimensions) {
        this.distanceToOrbitingBody = distanceToOrbitingBody;
        this.arrivalHeight = arrivalHeight;
        this.gravity = gravity;
        this.orbitedBody = orbitedBody;
        this.adjacentDimensions = adjacentDimensions;
    }

    public float gravity() {
        return gravity;
    }

    public int arrivalHeight() {
        return arrivalHeight;
    }

    public ResourceLocation orbitedBody() {
        return orbitedBody;
    }

    public int distanceToOrbitedBody() {
        return distanceToOrbitingBody;
    }

    //TODO remove the duplicated arrivalHeight or rename it if it's used
    public record AccessibilityParameter(int deltaV, int arrivalHeight) {
        public static final Codec<AccessibilityParameter> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Codec.INT.fieldOf("deltaV").forGetter(i -> i.deltaV),
                                Codec.INT.optionalFieldOf("arrivalHeight", 64).forGetter(i -> i.arrivalHeight)
                        )
                        .apply(instance, AccessibilityParameter::new));
    }
}