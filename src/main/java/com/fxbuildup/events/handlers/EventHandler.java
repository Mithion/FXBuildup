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

import java.util.Map;
import java.util.Optional;

import com.fxbuildup.FXBuildup;
import com.fxbuildup.capabilities.buildup.EffectBuildupProvider;
import com.fxbuildup.capabilities.stamina.Stamina;
import com.fxbuildup.capabilities.stamina.StaminaProvider;
import com.fxbuildup.config.EffectBuildupConfig;
import com.fxbuildup.enchantments.Conditioning;
import com.fxbuildup.enchantments.EnchantmentInit;
import com.fxbuildup.enchantments.Endurance;
import com.fxbuildup.enchantments.Resilience;
import com.fxbuildup.network.dispatch.ServerMessageDispatcher;
import com.mojang.datafixers.util.Pair;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent.Applicable;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = FXBuildup.MODID, bus = Bus.FORGE)
public class EventHandler {
	
	@SubscribeEvent
	public static void onLivingTick(LivingEvent.LivingTickEvent event) {
		
		LivingEntity living = event.getEntity();
		
		//handle stamina
		if (EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get()) {
			if (living instanceof Player) {
				Player p = (Player)event.getEntity();				
				
				//perform stamina regeneration if the capability exists
				p.getCapability(StaminaProvider.CAP).ifPresent(stamina -> {
					stamina.tick(p);
					
					//perform stamina drain if sprinting
					if (stamina.isInCombat() && p.isSprinting() && EffectBuildupConfig.INSTANCE.SPRINT_STAMINA.get()) {
						if (!stamina.consumeStamina(p, EffectBuildupConfig.INSTANCE.SPRINT_STAMINA_CONSUMPTION.get() / 20f))
							p.setSprinting(false);
					}
				});			
			}
		
			// If this living entity has a target, is an instace of Mob, and that target is a player, set the player in combat
			if (living instanceof Mob && living.isAlive()) {
				LivingEntity target = ((Mob)living).getTarget();
				if (target != null && target instanceof Player && !living.isAlliedTo(target) && ((Mob)living).getSensing().hasLineOfSight(target)) {
					//distance check
					if (living.distanceTo(target) <= EffectBuildupConfig.INSTANCE.IN_COMBAT_DISTANCE.get()) {
						Stamina.setCombatTicks((Player)target);
					}
				}
			}
		}
		
		//perform effect decay if the capability exists
		living.getCapability(EffectBuildupProvider.CAP).ifPresent(buildup -> {
			buildup.tick(event.getEntity());
		});			
	}
	
	@SubscribeEvent
	public static void onPotionTryingToApply(Applicable event) {
		
		if (event.getEntity().getPersistentData().contains("fxb_force"))
			return;
		
		//handle effect buildup if the capability exists
		event.getEntity().getCapability(EffectBuildupProvider.CAP).ifPresent(buildup -> {
			
			//this is needed because this event is the one that can be "canceled", but it doesn't have the 
			//source entity parameter included.  So, look for one nearby and if we find one that would apply this effect, and we're in the radius of it, assume it's the source. 
			Optional<AreaEffectCloud> likelyCloud = event.getEntity().level().getEntities(event.getEntity(), event.getEntity().getBoundingBox().inflate(32), p -> {
				return 
						p instanceof AreaEffectCloud && 
						((AreaEffectCloud)p).effects.stream().anyMatch(eff -> eff.equals(event.getEffectInstance()))
						&& event.getEntity().distanceTo(p) <= ((AreaEffectCloud)p).getRadius();
			}).stream().map(e -> (AreaEffectCloud)e).findFirst();
			
			//first element is amplifier add, second element is duration override
			Pair<Integer, Integer> overrides = buildup.addBuildup(event.getEntity(), likelyCloud.isPresent(), event.getEffectInstance());
			
			event.getEffectInstance().amplifier = overrides.getFirst();
			event.getEffectInstance().duration = overrides.getSecond();
			
			if (overrides.getSecond() == 0) { //duration zero means nope.
				event.setResult(Result.DENY);
			}else {
				event.setResult(Result.ALLOW);
			}
		});
	}
	
	@SubscribeEvent
	public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
		if (event.getEntity() instanceof Player && EffectBuildupConfig.INSTANCE.JUMP_STAMINA.get()) {
			//handle stamina drain if the capability exists
			Player p = (Player)event.getEntity();			
			
			p.getCapability(StaminaProvider.CAP).ifPresent(stamina -> {
				
				if (!stamina.isInCombat())
					return;
				
				double stamCost = EffectBuildupConfig.INSTANCE.JUMP_STAMINA_CONSUMPTION.get();
				if (p.isSprinting()) {
					stamCost *= EffectBuildupConfig.INSTANCE.JUMP_SPRINT_STAMINA_MULTIPLIER.get();
				}
				//this is canceled during client input if not enough stamina
				stamina.consumeStamina(p, stamCost);			
			});
		}
	}
	
	@SubscribeEvent
	public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
		//handle status buildup reset if enabled by config and capability is present
		if (EffectBuildupConfig.INSTANCE.ALLOW_BED_RESET.get()) {
			event.getEntity().getCapability(EffectBuildupProvider.CAP).ifPresent(buildup -> {
				buildup.resetBuildup();
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerAttackTarget(AttackEntityEvent event) {
		if (EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get() && EffectBuildupConfig.INSTANCE.ATTACK_STAMINA_COST.get() > 0) {
			if (!Stamina.consume(event.getEntity(), EffectBuildupConfig.INSTANCE.ATTACK_STAMINA_COST.get(), true)) {
				event.setCanceled(true);
				event.setResult(Result.DENY);
			}
		}
	}
	
	@SubscribeEvent
	public static void onShieldBlock(ShieldBlockEvent event) {
		if (event.getEntity() instanceof Player && EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get() && EffectBuildupConfig.INSTANCE.BLOCK_STAMINA_DRAIN_RATE.get() > 0) {
			Player p = (Player)event.getEntity();
			
			if (!Stamina.consume(p, EffectBuildupConfig.INSTANCE.BLOCK_STAMINA_DRAIN_RATE.get(), true)) {
				p.getCooldowns().addCooldown(p.getUseItem().getItem(), EffectBuildupConfig.INSTANCE.BLOCK_STAMINA_SHIELD_COOLDOWN.get());
				p.stopUsingItem();
				if (!p.level().isClientSide())
					ServerMessageDispatcher.sendStaminaFlash((ServerPlayer) p);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingChangeGear(LivingEquipmentChangeEvent event) {
		//look for the enchantments that this mod adds on the gear and call their application/removal functions where appropriate
		//this intentionally doesn't check slots in case another mod changes what it can apply on.  Because why not.
		
		//removals first
		Map<Enchantment, Integer> oldEnchants = EnchantmentHelper.getEnchantments(event.getFrom());
		
		if (event.getEntity() instanceof Player) {
			if (oldEnchants.containsKey(EnchantmentInit.ENDURANCE.get()))
				Endurance.remove((Player)event.getEntity());
			if (oldEnchants.containsKey(EnchantmentInit.CONDITIONING.get()))
				Conditioning.remove((Player)event.getEntity());
		}
		if (oldEnchants.containsKey(EnchantmentInit.RESILIENCE.get()))
			Resilience.remove(event.getEntity());
		
		//now additions
		Map<Enchantment, Integer> newEnchants = EnchantmentHelper.getEnchantments(event.getTo());
		
		if (event.getEntity() instanceof Player) {
			if (newEnchants.containsKey(EnchantmentInit.ENDURANCE.get()))
				Endurance.apply((Player)event.getEntity(), newEnchants.get(EnchantmentInit.ENDURANCE.get()));
			if (newEnchants.containsKey(EnchantmentInit.CONDITIONING.get()))
				Conditioning.apply((Player)event.getEntity(), newEnchants.get(EnchantmentInit.CONDITIONING.get()));			
		}
		if (newEnchants.containsKey(EnchantmentInit.RESILIENCE.get()))
			Resilience.apply(event.getEntity(), newEnchants.get(EnchantmentInit.RESILIENCE.get()));
	}
}
