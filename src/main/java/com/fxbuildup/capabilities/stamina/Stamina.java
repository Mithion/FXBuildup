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

package com.fxbuildup.capabilities.stamina;

import com.fxbuildup.attributes.AttributeInit;
import com.fxbuildup.capabilities.SyncedCapability;
import com.fxbuildup.config.EffectBuildupConfig;
import com.fxbuildup.events.StaminaConsumedEvent;
import com.fxbuildup.network.dispatch.ServerMessageDispatcher;
import com.fxbuildup.network.packets.StaminaSync;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Capability to handle stamina for players.
 * @author Mithion
 *
 */
public class Stamina extends SyncedCapability{
	double amount;
	int pauseCounter;
	int inCombatTicks = 0;
	float lastAttackStrengthScale = 0;
	int tickCount = 0;
	
	/**
	 * Tick intended to be called each frame for every entity with this capability attached.
	 * Handles stamina regeneration.
	 */
	public void tick(Player player) {
		tickSync(player);		
		
		//are we in combat				
		if (inCombatTicks > 0) {
			inCombatTicks--;
			//just left combat; sync
			// also more frequent syncs when in combat
			if (!player.level.isClientSide && (inCombatTicks == 0 || tickCount++ % 20 == 0)) {
				setDirty(true);
			}
		}else {
			tickCount = 0;
		}
		
		//are we paused?
		if (pauseCounter > 0) {
			pauseCounter--;
			return;
		}
		
		//handle regeneration
		var attr = player.getAttribute(AttributeInit.MAX_STAMINA.get());
		var regen = player.getAttribute(AttributeInit.STAMINA_REGEN.get());
		if (attr == null || regen == null)
			return;
		
		double configuredAttrBaseline = EffectBuildupConfig.INSTANCE.STAMINA_BASELINE.get();
		double configuredRegenBaseline = EffectBuildupConfig.INSTANCE.STAMINA_REGEN_BASELINE.get();
		if (attr.getBaseValue() != configuredAttrBaseline) {
			attr.setBaseValue(configuredAttrBaseline);
		}
		
		if (regen.getBaseValue() != configuredRegenBaseline) {
			regen.setBaseValue(configuredRegenBaseline);
		}
		
		double maximum = attr.getValue();		
		double regenVal = regen.getValue();
		
		if (player.isCreative() || player.isSpectator())
			amount = maximum;		
		
		//no sense doing anything if it's already full
		if (amount == maximum)
			return;
		
		double regenRate = regenVal / 20f;		
		
		double foodPct = Mth.clamp(player.getFoodData().getFoodLevel() / 20f, EffectBuildupConfig.INSTANCE.MINIMUM_FOOD_FACTOR.get(), 1f);
		regenRate *= foodPct;
		
		if (player.isBlocking()) {
			regenRate *= EffectBuildupConfig.INSTANCE.BLOCK_STAMINA_REGEN_MODIFIER.get();
		}			
		
		amount += regenRate;
		
		//apply constraints
		if (amount > maximum)
			amount = maximum;
		
		if (amount < 0)
			amount = 0;
		
		setDirty(false);		
	}
	
	/**
	 * Attempt to consume a given amount of stamina.
	 * @param amount The amount to attempt to consume
	 * @return True if success or the stamina consume event is canceled, otherwise false (insufficient stamina)
	 */
	public boolean consumeStamina(Player player, double amount) {
		
		if (player.isCreative())
			return true;
		
		if (MinecraftForge.EVENT_BUS.post(new StaminaConsumedEvent(player, amount)))
			return true;
		
		if (this.amount < amount) {
			if (player instanceof ServerPlayer) {
				ServerMessageDispatcher.sendStaminaFlash((ServerPlayer) player);
			}
			return false;			
		}			
		
		this.amount -= amount;
		this.pauseCounter = EffectBuildupConfig.INSTANCE.STAMINA_USE_REGEN_PAUSE.get();
		setDirty(false);
		return true;
	}
	
	/**
	 * Are we in combat?
	 */
	public boolean isInCombat() {
		return this.inCombatTicks > 0;
	}
	
	/**
	 * Instanced get amount
	 */
	public double getAmount() {
		return this.amount;
	}
	
	/**
	 * Set the number of ticks to be considered "in combat" to an absolute value
	 * @param ticks The number of ticks to be in combat for
	 */
	public void setCombatTicks(int ticks) {
		//entering combat, sync
		if (!this.isInCombat() && ticks > 0)
			this.setDirty(true);
		
		
		this.inCombatTicks = ticks;		
	}
	
	/**
	 * Convenience method to set the number of ticks to be considered "in combat" to an absolute value for the given player
	 * @param player The player to set the combat ticks for
	 * @param ticks The number of ticks to be in combat for
	 */
	public static void setCombatTicks(Player player, int ticks) {
		player.getCapability(StaminaProvider.CAP).ifPresent(s -> {
			s.setCombatTicks(ticks);
		});
	}
	
	/**
	 * Convenience method to set the number of ticks to be considered "in combat" to an absolute value for the given player to the default value in the configs
	 * @param player The player to set the combat ticks for
	 * @param ticks The number of ticks to be in combat for
	 */
	public static void setCombatTicks(Player player) {
		setCombatTicks(player, EffectBuildupConfig.INSTANCE.IN_COMBAT_TICKS.get());
	}
	
	/**
	 * Convenience method to consume stamina for a given entity.
	 * @param from The entity to consume from
	 * @param amount The amount to consume
	 * @return True if the capability is attached to the entity and stamina was successfully consumed, otherwise false.
	 */
	public static boolean consume(Player from, double amount) {
		return consume(from, amount, false);
	}
	
	/**
	 * Convenience method to consume stamina for a given entity.
	 * @param from The entity to consume from
	 * @param amount The amount to consume
	 * @syncImmediate If true, immediately sync stamina to the player.  If false, it will lazy sync within 10 seconds.
	 * @return True if the capability is attached to the entity and stamina was successfully consumed, otherwise false.
	 */
	public static boolean consume(Player from, double amount, boolean syncImmediate) {
		LazyOptional<Stamina> cap = from.getCapability(StaminaProvider.CAP);
		if (cap.isPresent()) {
			Stamina stamina = cap.resolve().get();
			if (stamina.consumeStamina(from, amount)) {
				if (syncImmediate)
					stamina.setDirty(true);
				return true;
			}
		}
		
		return false;		
	}
	
	/**
	 * Convenience method to get the stamina for a given entity.
	 * @param from The entity to search.
	 * @return The stamina (0 if default)
	 */
	public static double getAmount(Player from) {
		LazyOptional<Stamina> cap = from.getCapability(StaminaProvider.CAP);
		if (cap.isPresent())
			return cap.resolve().get().amount;
		
		return 0;			
	}
	
	@Override
	protected void dispatchPacket(Player player) {
		if (player.level.isClientSide)
			return;
		
		//only send packets serverside
		ServerMessageDispatcher.sendStaminaMessage((ServerPlayer) player);
	}

	/**
	 * Creates a packet for network sync
	 */
	public StaminaSync createPacket() {
		return new StaminaSync(amount, pauseCounter, inCombatTicks);
	}
	
	/**
	 * Handles a packet from the network
	 */
	public void handlePacket(StaminaSync packet) {
		this.amount = packet.getAmount();
		this.pauseCounter = packet.getPauseCounter();
		this.inCombatTicks = packet.getInCombatTicks();
	}
}
