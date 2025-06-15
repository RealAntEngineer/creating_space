package com.rae.creatingspace.mixin.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.levelgen.structure.Structure.settingsCodec;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMixin implements JigsawStructureAccessor{
    @Shadow
    @Mutable
    @Final
    public static Codec<JigsawStructure> CODEC;

    @Unique
    private static DataResult<JigsawStructure> cS_1_20_1$verifyRange(JigsawStructure p_286886_) {
        byte b0 = switch (p_286886_.terrainAdaptation()) {
            case NONE -> 0;
            case BURY, BEARD_THIN, BEARD_BOX -> 12;
            default -> throw new IncompatibleClassChangeError();
        };

        int i = b0;
        return ((JigsawStructureAccessor) (Object)p_286886_).getMaxDistanceFromCenter() + i > 128 ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128") :
                DataResult.success(p_286886_);
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void overrideCodec(CallbackInfo ci) {
        CODEC =  ExtraCodecs.validate(
                RecordCodecBuilder.mapCodec((instance) ->
                        instance.group(
                                settingsCodec(instance),
                                StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> ((JigsawStructureAccessor)  (Object)s).getStartPool()),
                                ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(s -> ((JigsawStructureAccessor) (Object) s).getStartJigsawName()),
                                Codec.intRange(0, 128).fieldOf("size").forGetter(s -> ((JigsawStructureAccessor) (Object) s).getMaxDepth()),
                                HeightProvider.CODEC.fieldOf("start_height").forGetter(s -> ((JigsawStructureAccessor) (Object) s).getStartHeight()),
                                Codec.BOOL.fieldOf("use_expansion_hack").forGetter(s -> ((JigsawStructureAccessor) (Object) s).getUseExpansionHack()),
                                Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(s -> ((JigsawStructureAccessor)  (Object)s).getProjectStartToHeightmap()),
                                Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(s -> ((JigsawStructureAccessor) (Object) s).getMaxDistanceFromCenter())
                        ).apply(instance, JigsawStructure::new)
                ),
                JigsawStructureMixin::cS_1_20_1$verifyRange
        ).codec();
    }
}
