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

package com.fxbuildup.capabilities.buildup;

import java.util.HashMap;

import com.fxbuildup.attributes.AttributeInit;
import com.fxbuildup.capabilities.SyncedCapability;
import com.fxbuildup.config.EffectBuildupConfig;
import com.fxbuildup.events.EffectAppliedEvent;
import com.fxbuildup.events.EffectBuildupEvent;
import com.fxbuildup.network.dispatch.ServerMessageDispatcher;
import com.fxbuildup.network.packets.BuildupSync;
import com.fxbuildup.recipes.EntityConfigRecipe;
import com.fxbuildup.recipes.EntityConfigRecipeSerializer;
import com.fxbuildup.recipes.StatusConfigRecipe;
import com.fxbuildup.recipes.StatusConfigRecipeSerializer;
import com.fxbuildup.tags.TagManager;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

/**
 * Capability handler to control effect buildup.
 * @author Mithion
 *
 */
public class EffectBuildup extends SyncedCapability {
	HashMap<ResourceLocation, Double> buildup;	
	
	public EffectBuildup() {
		buildup = new HashMap<ResourceLocation, Double>();
	}
	
	/**
	 * Intended to be called every tick for every living entity that has the capability attached.
	 * Applies decay to all active built up effects.
	 */
	public void tick(LivingEntity living) {
		if (living instanceof Player)
			tickSync((Player) living);
		
		buildup.replaceAll((k, v) -> {
			return v - getDecayRate(k);
		});
		
		buildup.values().removeIf(v -> v <= 0);
		
		if (buildup.size() > 0 && !needsSync())
			setDirty(false);
	}
	
	/**
	 * Attempts to add buildup for a given effect.
	 * @param inst The mob effect instance that is attempting to be added.
	 * @return The amount of instance levels that should be added, and the duration the effect should be
	 */
	public Pair<Integer, Integer> addBuildup(LivingEntity target, boolean isLingering, MobEffectInstance inst) {
		if (!target.level.isClientSide && TagManager.isEffectBuildup(inst.getEffect())) {
			
			if (target.isInWaterOrBubble() && TagManager.isEffectInstantInWater(inst.getEffect()))
				return new Pair<>(inst.getAmplifier(), inst.getDuration());
			
			if (target.isInLava() && TagManager.isEffectInstantInLava(inst.getEffect()))
				return new Pair<>(inst.getAmplifier(), inst.getDuration());
						
			double cap = getResistanceTo(target, inst.getEffect());
			
			double buildup_rate = getBuildupRate(inst.getEffect());
			
			if (inst.isAmbient())
				buildup_rate *= EffectBuildupConfig.AMBIENT_BUILDUP_FACTOR.get();
			
			if (isLingering)
				buildup_rate *= EffectBuildupConfig.LINGERING_BUILDUP_FACTOR.get();
			
			double new_buildup = getBuildup(inst.getEffect()) + ((inst.getAmplifier() + 1) * inst.getDuration() * buildup_rate);
			
			if (MinecraftForge.EVENT_BUS.post(new EffectBuildupEvent(target, inst, new_buildup)))
				return new Pair<>(inst.getAmplifier(), inst.getDuration());
			
			int addedLevels = (int)Math.floor(new_buildup / cap);
			new_buildup %= cap;
			
			MobEffectInstance existing = target.getEffect(inst.getEffect());
			
			if (addedLevels > 0) {
				
				if (MinecraftForge.EVENT_BUS.post(new EffectAppliedEvent(target, inst, new_buildup, addedLevels))) {
					return new Pair<>(0,0);
				}
				if (target instanceof ServerPlayer) {
					ServerMessageDispatcher.sendToast((ServerPlayer) target, inst.getEffect().getDescriptionId(), existing == null);
				}								
			}
			
			buildup.put(inst.getEffect().getRegistryName(), new_buildup);
			
			setDirty(true);
						
			int existingAmplifier = existing == null ? -1 : existing.amplifier;
			int newAmplifier = existingAmplifier + (addedLevels + getConfiguredAmplifier(inst.getEffect()));
			
			if (newAmplifier > EffectBuildupConfig.MAXIMUM_AMPLIFIER.get())
				newAmplifier = EffectBuildupConfig.MAXIMUM_AMPLIFIER.get();
						
			return new Pair<>(
				 newAmplifier,
				addedLevels == 0 ? 0 : getConfiguredDuration(inst.getEffect(), inst.getDuration())
			);
		}
		return new Pair<>(inst.getAmplifier(), inst.getDuration());
	}

	/**
	 * Clears all effect buildup
	 */
	public void resetBuildup() {
		buildup.clear();
		setDirty(true);
	}
	
	/**
	 * Gets the current buildup for the effect, if any.
	 * @param effect The effect to search.
	 * @return The buildup.  0 or higher.
	 */
	public double getBuildup(MobEffect effect) {
		return buildup.getOrDefault(effect.getRegistryName(), 0d);
	}
	
	/**
	 * Gets the resistance to the given effect for the entity that this capability is attached to.
	 * @param effect The effect to search for.
	 * @return The resistance to the effect.
	 */
	public double getResistanceTo(LivingEntity target, MobEffect effect) {
		var attr = target.getAttribute(AttributeInit.RESISTANCE.get());
		if (attr == null)
			return 0;
		
		double configuredBaseline = EffectBuildupConfig.BASELINE_RESISTANCE.get();
		if (attr.getBaseValue() != configuredBaseline)
			attr.setBaseValue(configuredBaseline);
		
		double current = attr.getValue();
		double baseline = attr.getValue();
		double modifier = current - baseline;
		
		//check to see if there is a custom recipe for this effect for my entity
		EntityConfigRecipe entityConfig = EntityConfigRecipeSerializer.ALL_RECIPES.getOrDefault(effect.getRegistryName(), null);
		if (entityConfig != null) {
			return entityConfig.getConfigFor(effect).get().getResist() + modifier;
		}
		
		return current;
	}
	
	/**
	 * Gets the buildup rate for the given effect for the entity that this capability is attached to.
	 * When the buildup meets or exceeds resistance, the effect is pushed through.
	 * @param effect The effect to search for.
	 * @return The buildup rate for the effect.
	 */
	public double getBuildupRate(MobEffect effect) {
		//check to see if there is a custom config for this effect in general
		StatusConfigRecipe statusConfig = StatusConfigRecipeSerializer.ALL_RECIPES.getOrDefault(effect.getRegistryName(), null);
		if (statusConfig != null)
			return statusConfig.getBuildup();
		
		return EffectBuildupConfig.APPLICATION_RATE.get();
	}
	
	/**
	 * Gets the decay rate for the given effect for the entity that this capability is attached to.
	 * The decay rate is how fast buildup decays.
	 * @param effect The effect to search for.
	 * @return The decay rate for the effect.
	 */
	public double getDecayRate(ResourceLocation effectId) {
		//check to see if there is a custom config for this effect in general
		StatusConfigRecipe statusConfig = StatusConfigRecipeSerializer.ALL_RECIPES.getOrDefault(effectId, null);
		if (statusConfig != null)
			return statusConfig.getDecay() / 20f;
		
		return EffectBuildupConfig.DECAY_RATE.get() / 20f;
	}	
	
	/**
	 * Gets the duration for the given effect for the entity that this capability is attached to.
	 * @param effect The effect to search for.
	 * @param passedDuration The original duration for the effect, before buildup is appled (from the baseline entity.addEffect).
	 * @return The higher of the configured duration (if present) or the passed duration.
	 */
	public int getConfiguredDuration(MobEffect effect, int passedDuration) {
		//check to see if there is a custom config for this effect in general
		StatusConfigRecipe statusConfig = StatusConfigRecipeSerializer.ALL_RECIPES.getOrDefault(effect.getRegistryName(), null);
		if (statusConfig != null)
			return Math.max(statusConfig.getApplicationDuration(), passedDuration);
		
		return passedDuration;
	}
	
	/**
	 * Gets the amplifier for the given effect for the entity that this capability is attached to.
	 * @param effect The effect to search for.
	 * @return The amplifier for the effect, or 1 if not configured; this is how much to increase the amplifier by if the buildup exceeds the resistance.
	 */
	public int getConfiguredAmplifier(MobEffect effect) {
		//check to see if there is a custom config for this effect in general
		StatusConfigRecipe statusConfig = StatusConfigRecipeSerializer.ALL_RECIPES.getOrDefault(effect.getRegistryName(), null);
		if (statusConfig != null)
			return statusConfig.getApplicationMagnitude();
		
		return 0;
	}

	@Override
	protected void dispatchPacket(Player player) {
		if (!player.level.isClientSide) {
			//only send the packet server -> client
			ServerMessageDispatcher.sendBuildupMessage((ServerPlayer)player);
		}
	}
	
	/**
	 * Creates a packet for network sync
	 */
	public BuildupSync createPacket() {
		return new BuildupSync(buildup);
	}
	
	/**
	 * Handles a packet from the network
	 */
	public void handlePacket(BuildupSync packet) {
		this.buildup = packet.getBuildup();
	}

		
	public HashMap<ResourceLocation, Double> list() {
		return buildup;
	}
}
