package dev.fiki.forgehax.api.events;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LocalPlayerUpdateEvent extends LivingEvent {
  
  public LocalPlayerUpdateEvent(LivingEntity e) {
    super(e);
  }
}
