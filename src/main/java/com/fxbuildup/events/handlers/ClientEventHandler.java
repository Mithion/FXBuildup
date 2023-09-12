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


package com.fxbuildup.events.handlers;

import com.fxbuildup.capabilities.stamina.Stamina;
import com.fxbuildup.config.EffectBuildupConfig;
import com.fxbuildup.gui.Hud;
import com.fxbuildup.input.DoubleTapHandler;
import com.fxbuildup.network.dispatch.ClientMessageDispatcher;
import com.fxbuildup.network.packets.Dodge.Direction;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {
	private static DoubleTapHandler left = new DoubleTapHandler();
	private static DoubleTapHandler right = new DoubleTapHandler();
	private static DoubleTapHandler back = new DoubleTapHandler();
	
	@SubscribeEvent
	public static void playerInputEvent(MovementInputUpdateEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null)
			return;		
		
		if (EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get()) {
			double stamCost = EffectBuildupConfig.INSTANCE.JUMP_STAMINA_CONSUMPTION.get();
			if (mc.player.isSprinting()) {
				stamCost *= EffectBuildupConfig.INSTANCE.JUMP_SPRINT_STAMINA_MULTIPLIER.get();
			}
			
			if (event.getInput().jumping && Stamina.getAmount(mc.player) < stamCost) {
				event.getInput().jumping = false;
				Hud.flashStamina();
			}
		}
		
		if (EffectBuildupConfig.INSTANCE.DODGING_ENABLED.get() && !mc.player.isCrouching()) {
			double stamCost = EffectBuildupConfig.INSTANCE.DODGE_STAMINA_COST.get();			
			
			if (left.update(event.getInput().left)) {				
				if (Stamina.getAmount(mc.player) >= stamCost)
					ClientMessageDispatcher.sendDodge(Direction.LEFT);
				else 
					Hud.flashStamina();
			}
			
			if (right.update(event.getInput().right)) {
				if (Stamina.getAmount(mc.player) >= stamCost)
					ClientMessageDispatcher.sendDodge(Direction.RIGHT);
				else 
					Hud.flashStamina();
			}
			
			if (back.update(event.getInput().down)) {
				if (Stamina.getAmount(mc.player) >= stamCost)
					ClientMessageDispatcher.sendDodge(Direction.BACK);
				else 
					Hud.flashStamina();
			}
		}
	} 
	
	@SubscribeEvent
	public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
		if (EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get() && EffectBuildupConfig.INSTANCE.ATTACK_STAMINA_COST.get() > 0) {
			Minecraft mc = Minecraft.getInstance();
			if (event.isAttack() && Stamina.getAmount(mc.player) < EffectBuildupConfig.INSTANCE.ATTACK_STAMINA_COST.get()) {
				Hud.flashStamina();
				event.setCanceled(true);
			}
		}
	}
}
