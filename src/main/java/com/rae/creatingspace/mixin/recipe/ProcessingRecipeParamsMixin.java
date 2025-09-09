package com.rae.creatingspace.mixin.recipe;

import com.rae.creatingspace.content.recipes.IMoreNbtConditions;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ProcessingRecipeParams.class)
public class ProcessingRecipeParamsMixin implements IMoreNbtConditions {
    @Shadow protected NonNullList<Ingredient> ingredients;
    @Unique
    public ArrayList<String> nbtKeys = new ArrayList<>();
    @Unique
    public ArrayList<String> matchNbtList = new ArrayList<>();

    public void setKeepNbt(ArrayList<String> nbtKeys) {
        this.nbtKeys = nbtKeys;
    }

    @Override
    public ArrayList<String> getKeepNbt() {
        return nbtKeys;
    }

    @Override
    public void setMachNbt(ArrayList<String> machNbtList) {
        matchNbtList = machNbtList;
    }

    @Override
    public ArrayList<String> getMachNbt() {
        return matchNbtList;
    }

    @Override
    public boolean isKeepNbt() {
        return !nbtKeys.isEmpty();
    }

    @Override
    public boolean isMachNbt() {
        return !matchNbtList.isEmpty();
    }

    @Inject(method = "encode", at = @At("HEAD"))
    private void addEncodeFailSafe(RegistryFriendlyByteBuf buf, CallbackInfo ci){
        //buf.writeCollection(nbtKeys, ByteBufCodecs.STRING_UTF8);
        //buf.writeCollection(matchNbtList,ByteBufCodecs.STRING_UTF8);
    }

    @Inject(method = "encode", at = @At("TAIL"))
    private void addEncode(RegistryFriendlyByteBuf buf, CallbackInfo ci){
        buf.writeCollection(nbtKeys, ByteBufCodecs.STRING_UTF8);
        buf.writeCollection(matchNbtList,ByteBufCodecs.STRING_UTF8);
    }

    @Inject(method = "decode", at = @At("TAIL"))
    private void addDecode(RegistryFriendlyByteBuf buf, CallbackInfo ci){
        nbtKeys = buf.readCollection(size -> new ArrayList<>(),ByteBufCodecs.STRING_UTF8);
        matchNbtList = buf.readCollection(size -> new ArrayList<>(),ByteBufCodecs.STRING_UTF8);
    }
}
