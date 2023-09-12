/**
 * Copyright 2022 Mithion
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * 	The above copyright notice and this permission notice shall be included in all copies or 
 *  substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.fxbuildup.capabilities;

import com.fxbuildup.FXBuildup;
import com.fxbuildup.capabilities.buildup.EffectBuildupProvider;
import com.fxbuildup.capabilities.stamina.StaminaProvider;
import com.fxbuildup.config.EffectBuildupConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = FXBuildup.MODID, bus = Bus.FORGE)
public class CapabilityForgeEventHandlers {
	
	public static final ResourceLocation BUILDUP_CAP = new ResourceLocation(FXBuildup.MODID, "buildup");
	public static final ResourceLocation STAMINA_CAP = new ResourceLocation(FXBuildup.MODID, "stamina");	
	
	@SubscribeEvent
	public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
		//player only caps (stamina)
		if (EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get() && event.getObject() instanceof Player)
			event.addCapability(STAMINA_CAP, new StaminaProvider());
		
		//buildup
		if (event.getObject() instanceof LivingEntity) {
			//is player
			if (event.getObject() instanceof Player && EffectBuildupConfig.INSTANCE.PLAYER_BUILDUP.get())
				event.addCapability(BUILDUP_CAP, new EffectBuildupProvider());			
			
			//is boss
			else if (!((LivingEntity)event.getObject()).canChangeDimensions() && EffectBuildupConfig.INSTANCE.BOSS_BUILDUP.get())
				event.addCapability(BUILDUP_CAP, new EffectBuildupProvider());
			
			//is mob
			else if (event.getObject() instanceof Mob && EffectBuildupConfig.INSTANCE.MOB_BUILDUP.get())
				event.addCapability(BUILDUP_CAP, new EffectBuildupProvider());
		}
	}
	
	
}
