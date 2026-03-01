package com.rae.creatingspace.content.rocket.engine.table;

import com.rae.creatingspace.content.rocket.engine.design.ExhaustPackType;
import com.rae.creatingspace.content.rocket.engine.design.PowerPackType;
import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.init.MiscInit;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.rae.creatingspace.content.rocket.engine.RocketEngineItem.appendEngineDependentText;

public class EngineFabricationBlueprint extends Item {
    public EngineFabricationBlueprint(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @NotNull TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        //TODO make this method static somewhere (repetition for the engine, for the engine blueprint then for every item)
        CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
        if (data!=null) {

            CompoundTag recipeData = data.copyTag().getCompound("engineRecipeData");
            try {
                int size = recipeData.getInt("size");
                int materialLevel = recipeData.getInt("materialLevel");
                if (recipeData.contains("size")) components.add(Component.literal("size : " + size));
                if (recipeData.contains("materialLevel"))
                    components.add(Component.literal("materialLevel : " + materialLevel));
                try {
                    ResourceLocation exhaustPackType = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, recipeData.get("exhaustPackType")).getOrThrow();
                    components.add(Component.translatable(exhaustPackType.toLanguageKey("exhaust_pack_type")));
                } catch (Exception ignored) {
                }
                try {
                    ResourceLocation powerPackType = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, recipeData.get("powerPackType")).getOrThrow();
                    components.add(Component.translatable(powerPackType.toLanguageKey("power_pack_type")));
                } catch (Exception ignored) {
                }
                CompoundTag engineInfo = data.copyTag().getCompound("blockEntity");
                components.add(Component.literal("for engine :"));
                appendEngineDependentText(components, engineInfo);
            } catch (Exception ignored) {

            }
        }
        super.appendHoverText(itemStack, context, components, tooltipFlag);
    }

    public ItemStack getBlueprintForEngine(int throatArea, int expansionRatio, int materialLevel, int thrust, float efficiency, ResourceLocation propellantTypeLocation, ResourceLocation exhaustPackTypeLocation, ResourceLocation powerPackTypeLocation) {

        PropellantType propellantType = PropellantTypeInit.getSyncedPropellantRegistry().get(
                propellantTypeLocation);
        ExhaustPackType exhaustPackType = MiscInit.getSyncedExhaustPackRegistry()
                .get(exhaustPackTypeLocation);
        PowerPackType powerPackType = MiscInit.getSyncedPowerPackRegistry().get(powerPackTypeLocation);
        ItemStack defaultInstance = super.getDefaultInstance();
        CustomData data = defaultInstance.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbt = data==null?new CompoundTag():data.copyTag();
        CompoundTag engineInfo = new CompoundTag();
        engineInfo.putInt("thrust", thrust);
        assert exhaustPackType != null;
        engineInfo.putInt("mass", exhaustPackType.getMass((float) throatArea / 1000, expansionRatio));//size will be defined in the exhaust and powerPack as a coef (0.5 fo reach right now)
        engineInfo.putFloat("efficiency", efficiency);
        engineInfo.put("propellantType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, propellantTypeLocation).getOrThrow());

        CompoundTag recipeData = new CompoundTag();
        recipeData.put("exhaustPackType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, exhaustPackTypeLocation).getOrThrow());
        recipeData.put("powerPackType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, powerPackTypeLocation).getOrThrow());
        recipeData.putInt("size", throatArea);
        recipeData.putInt("expansionRatio", expansionRatio);
        recipeData.putInt("materialLevel", materialLevel);
        nbt.put("blockEntity", engineInfo);
        nbt.put("engineRecipeData", recipeData);

        defaultInstance.set(DataComponents.CUSTOM_DATA,CustomData.of(nbt));
        return defaultInstance;
    }
}
