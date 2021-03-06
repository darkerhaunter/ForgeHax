package dev.fiki.forgehax.main.services;

import dev.fiki.forgehax.api.events.LocalPlayerUpdateEvent;
import dev.fiki.forgehax.api.mod.ServiceMod;
import dev.fiki.forgehax.api.modloader.RegisterMod;
import dev.fiki.forgehax.main.Common;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Created on 6/14/2017 by fr1kin
 */
@RegisterMod
public class LocalPlayerUpdateEventService extends ServiceMod {
  
  @SubscribeEvent
  public void onUpdate(LivingEvent.LivingUpdateEvent event) {
    if (Common.getWorld() != null
        && event.getEntity().getEntityWorld().isRemote
        && event.getEntityLiving().equals(Common.getLocalPlayer())) {
      Event ev = new LocalPlayerUpdateEvent(event.getEntityLiving());
      MinecraftForge.EVENT_BUS.post(ev);
      event.setCanceled(ev.isCanceled());
    }
  }
}
