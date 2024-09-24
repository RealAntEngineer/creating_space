package com.rae.creatingspace.content.rocket.engine.table;

import com.rae.creatingspace.content.rocket.engine.design.ExhaustPackType;
import com.rae.creatingspace.content.rocket.engine.design.PowerPackType;
import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.init.MiscInit;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.rae.creatingspace.content.rocket.engine.RocketEngineItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EngineFabricationBlueprint extends Item {
    public EngineFabricationBlueprint(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        //TODO make this method static somewhere (repetition for the engine, for the engine blueprint then for every item)
        CompoundTag recipeData = itemStack.getTagElement("engineRecipeData");
        try {
            if (recipeData != null) {
                int size = recipeData.getInt("size");
                int materialLevel = recipeData.getInt("materialLevel");
                components.add(Component.literal("size : " + size));
                components.add(Component.literal("materialLevel : " + materialLevel));
                try {
                    ResourceLocation exhaustPackType = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, recipeData.get("exhaustPackType")).get().orThrow();
                    ResourceLocation powerPackType = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, recipeData.get("powerPackType")).get().orThrow();

                    components.add(Component.translatable(exhaustPackType.toLanguageKey("exhaust_pack_type")));
                    components.add(Component.translatable(powerPackType.toLanguageKey("power_pack_type")));
                } catch (Exception ignored) {
                }
            }
            CompoundTag engineInfo = itemStack.getTagElement("blockEntity");
            if (engineInfo != null) {
                components.add(Component.literal("for engine :"));
                //TODO there is a need for a description : size and type of exhaustPack + powerPack + material level
                PropellantType propellantType = PropellantTypeInit.getSyncedPropellantRegistry().getOptional(
                        ResourceLocation.CODEC.parse(NbtOps.INSTANCE, engineInfo.get("propellantType"))
                                .resultOrPartial(s -> {
                                }).orElse(PropellantTypeInit.METHALOX.getId())).orElseThrow();
                RocketEngineItem.appendEngineDependentText(components, propellantType, (int) (propellantType.getMaxISP() * engineInfo.getFloat("efficiency")), engineInfo.getInt("mass"), engineInfo.getInt("thrust"));
            }
        } catch (Exception ignored){

        }
        super.appendHoverText(itemStack, level, components, tooltipFlag);
    }

    public ItemStack getBlueprintForEngine(int throatArea, int expansionRatio, int materialLevel, int thrust, float efficiency, ResourceLocation propellantTypeLocation, ResourceLocation exhaustPackTypeLocation, ResourceLocation powerPackTypeLocation) {

        PropellantType propellantType = PropellantTypeInit.getSyncedPropellantRegistry().getOptional(
                propellantTypeLocation).orElse(PropellantTypeInit.METHALOX.get());
        ExhaustPackType exhaustPackType = MiscInit.getSyncedExhaustPackRegistry()
                .get(exhaustPackTypeLocation);
        PowerPackType powerPackType = MiscInit.getSyncedPowerPackRegistry().get(powerPackTypeLocation);
        ItemStack defaultInstance = super.getDefaultInstance();
        CompoundTag nbt = defaultInstance.getOrCreateTag();
        CompoundTag engineInfo = new CompoundTag();
        engineInfo.putInt("thrust", thrust);
        assert exhaustPackType != null;
        engineInfo.putInt("mass", exhaustPackType.getMass((float) throatArea / 1000, expansionRatio));//size will be defined in the exhaust and powerPack as a coef (0.5 fo reach right now)
        engineInfo.putFloat("efficiency", efficiency);
        engineInfo.put("propellantType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, propellantTypeLocation).get().orThrow());

        CompoundTag recipeData = new CompoundTag();
        recipeData.put("exhaustPackType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, exhaustPackTypeLocation).get().orThrow());
        recipeData.put("powerPackType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, powerPackTypeLocation).get().orThrow());
        recipeData.putInt("size", throatArea);
        recipeData.putInt("expansionRatio", expansionRatio);
        recipeData.putInt("materialLevel", materialLevel);
        nbt.put("blockEntity", engineInfo);
        nbt.put("engineRecipeData", recipeData);
        defaultInstance.setTag(nbt);
        return defaultInstance;
    }
}
