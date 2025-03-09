package com.rae.creatingspace.content.recipes.electrolysis;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.mojang.math.Axis;
import org.joml.Quaternionf;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

public class ElectrolyzerInstance extends ShaftInstance<MechanicalElectrolyzerBlockEntity> implements DynamicInstance {

    private final OrientedData pressHead;

    public ElectrolyzerInstance(MaterialManager materialManager, MechanicalElectrolyzerBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        pressHead = materialManager.defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(PartialModelInit.ELECTROLYZER_HEAD, blockState)
                .createInstance();

        Quaternionf q = Axis.YP
                .rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING)));

        pressHead.setRotation(q);

        transformModels();
    }

    @Override
    public void beginFrame() {
        transformModels();
    }

    private void transformModels() {
        float renderedHeadOffset = getRenderedHeadOffset(blockEntity);

        pressHead.setPosition(getInstancePosition())
                .nudge(0, -renderedHeadOffset, 0);
    }

    private float getRenderedHeadOffset(MechanicalElectrolyzerBlockEntity electrolyzer) {
        return electrolyzer.getRenderedHeadOffset(AnimationTickHolder.getPartialTicks());
    }

    @Override
    public void updateLight() {
        super.updateLight();

        relight(pos, pressHead);
    }

    @Override
    public void remove() {
        super.remove();
        pressHead.delete();
    }
}
