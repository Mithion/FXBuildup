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

package com.fxbuildup.recipes;

import com.fxbuildup.config.EffectBuildupConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Recipe allowing customization of attributes for status buildup for a given effect.
 * Only mandatory parameter is effect ID.  If missing, it will intentionally crash.
 * Other values, if not configured, recipe values will fall back to the values from configs.
 * @author Mithion
 *
 */
public class StatusConfigRecipe extends CustomRecipe{
	ResourceLocation effectId;
	double buildupRate;
	double decayRate;
	int applicationMagnitude;
	int applicationDuration;
	int maximumAmplifier;
	
	public StatusConfigRecipe(ResourceLocation pId) {
		super(pId);
		
		buildupRate = EffectBuildupConfig.INSTANCE.APPLICATION_RATE.get();
		decayRate = EffectBuildupConfig.INSTANCE.DECAY_RATE.get();
		
		applicationMagnitude = 0;
		applicationDuration = -1;
		maximumAmplifier = EffectBuildupConfig.INSTANCE.MAXIMUM_AMPLIFIER.get();
	}

	/**
	 * Is this recipe for the given effect?
	 */
	public boolean isFor(MobEffect effect) {
		return this.effectId.equals(ForgeRegistries.MOB_EFFECTS.getKey(effect));
	}
	
	/**
	 * Get the buildup rate for the effect.
	 */
	public double getBuildup() {
		return this.buildupRate;
	}
	
	/**
	 * Get the decay rate for the effect.
	 */
	public double getDecay() {
		return this.decayRate;
	}
	
	/**
	 * Get the maximum magnitude the effect can have.
	 */
	public int getMaximumAmplifier() {
		return this.maximumAmplifier;
	}
	
	/**
	 * Get the amplifier step each time the buildup rolls over and the effect is either applied or increased.
	 */
	public int getApplicationMagnitude() {
		return this.applicationMagnitude;
	}
	
	/**
	 * Get the duration of the effect (as a minimum) when the buildup rolls over and the effect is either applied, increased, or refreshed.
	 */
	public int getApplicationDuration() {
		return this.applicationDuration;
	}
	
	@Override
	public boolean matches(CraftingContainer pContainer, Level pLevel) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingContainer pContainer) {
		return null;
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeInit.STATUS_CONFIG_SERIALIZER.get();
	}
	
	@Override
	public RecipeType<?> getType() {
		return RecipeInit.STATUS_CONFIG_TYPE;
	}
}
