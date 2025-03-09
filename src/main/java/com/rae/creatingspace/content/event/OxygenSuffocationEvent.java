package com.rae.creatingspace.content.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public class OxygenSuffocationEvent extends LivingEvent {
    //mostly copied from galacticraft
    public final DimensionType dimension;

    public OxygenSuffocationEvent(LivingEntity entity)
    {
        super(entity);
        this.dimension = entity.level().dimensionType();
    }

    /**
     * This event is posted just before the living entity suffocates
     * <p/>
     * Set the event as cancelled to stop the living entity from suffocating
     * <p/>
     * IF THE Pre EVENT IS CANCELLED, THE "WARNING: OXYGEN SETUP INVALID!" HUD MESSAGE WILL NOT BE SHOWN
     */
    @Cancelable
    public static class Pre extends OxygenSuffocationEvent
    {
        public Pre(LivingEntity entity)
        {
            super(entity);
        }
    }

    /**
     * This event is called after the living entity takes damage from oxygen
     * suffocation
     * <p/>
     * The event is not called if the pre event was canceled
     */
    public static class Post extends OxygenSuffocationEvent
    {
        public Post(LivingEntity entity)
        {
            super(entity);
        }
    }
}
