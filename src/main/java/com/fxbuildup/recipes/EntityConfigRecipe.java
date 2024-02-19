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

import java.util.Optional;

import com.fxbuildup.config.EffectBuildupConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Recipe allowing configuration of how effects affect a given mob.
 * Only mandatory parameter is the entity type ID, if not set it will cause a crash.
 * @author Mithion
 *
 */
public class EntityConfigRecipe extends CustomRecipe{	
	ResourceLocation entityTypeId;
	
	EffectWhitelist globalOptions;
	NonNullList<ResourceLocation> immuneEffects;
	NonNullList<EffectWhitelist> individualEffects;
	
	public EntityConfigRecipe(ResourceLocation pId) {
		super(pId, CraftingBookCategory.MISC);
		
		globalOptions = new EffectWhitelist();
		immuneEffects = NonNullList.create();
		individualEffects = NonNullList.create();
	}
	
	/**
	 * Is the entity for this recipe immune to the given effect (as per config; does not look at code settings)?
	 */
	public boolean isImmuneTo(MobEffect effect) {
		return immuneEffects.contains(ForgeRegistries.MOB_EFFECTS.getKey(effect));
	}
	
	/**
	 * Gets the configured settings for the given effect for the entity this recipe represents.
	 */
	public Optional<EffectWhitelist> getConfigFor(MobEffect effect) {
		return individualEffects.stream().filter(e -> e.matches(effect)).findFirst();
	}
	
	void write(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(entityTypeId);
		globalOptions.writeTo(buffer);
		buffer.writeInt(immuneEffects.size());
		for (int i = 0; i < immuneEffects.size(); ++i)
			buffer.writeResourceLocation(immuneEffects.get(i));
		
		buffer.writeInt(individualEffects.size());
		for (int i = 0; i < individualEffects.size(); ++i)
			this.individualEffects.get(i).writeTo(buffer);
	}
	
	void read(FriendlyByteBuf buffer) {
		this.entityTypeId = buffer.readResourceLocation();
		this.globalOptions.readFrom(buffer);
		
		int numImmune = buffer.readInt();
		this.immuneEffects.clear();
		for (int i = 0; i < numImmune; ++i)
			this.immuneEffects.add(buffer.readResourceLocation());
		
		int numIndividual = buffer.readInt();
		this.individualEffects.clear();
		for (int i = 0; i < numIndividual; ++i) {
			EffectWhitelist wl = new EffectWhitelist();
			wl.readFrom(buffer);
			this.individualEffects.add(wl);
		}
	}
	
	void parse (JsonObject json) {
		if (!json.has("entityTypeId"))
			throw new RuntimeException("Missing entityTypeId in Entity Config Recipe");
		
		entityTypeId = new ResourceLocation(json.get("entityTypeId").getAsString());		
		globalOptions.parse(json, true);
					
		if (json.has("immunities") && json.get("immunities").isJsonArray()) {
			JsonArray immunities = json.get("immunities").getAsJsonArray();
			immunities.forEach(e -> {
				if (e.isJsonPrimitive())
					immuneEffects.add(new ResourceLocation(e.getAsString()));
			});		
		}
		
		if (json.has("effects") && json.get("effects").isJsonArray()) {
			JsonArray effects = json.get("effects").getAsJsonArray();
			effects.forEach(e -> {
				EffectWhitelist wl = new EffectWhitelist();
				wl.parse(e.getAsJsonObject(), false);
				individualEffects.add(wl);
			});
		}
	}

	//===========================================
	// Vanilla overrides we don't care about
	//===========================================
	
	@Override
	public boolean matches(CraftingContainer pContainer, Level pLevel) {
		return false;
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeInit.ENTITY_CONFIG_SERIALIZER.get();
	}
	
	@Override
	public RecipeType<?> getType() {
		return RecipeInit.ENTITY_CONFIG_TYPE.get();
	}
	
	/**
	 * Holds the whitelist configuration data for a given effect
	 * @author Mithion
	 *
	 */
	public static class EffectWhitelist{
		ResourceLocation effectId;
		double resistance;
		int maximumMagnitude;
		int duration;
		
		private EffectWhitelist() {
			this.resistance = EffectBuildupConfig.INSTANCE.BASELINE_RESISTANCE.get();
			this.maximumMagnitude = EffectBuildupConfig.INSTANCE.MAXIMUM_AMPLIFIER.get();
			this.duration = -1;
		}
		
		public ResourceLocation getEffectId() {
			return this.effectId;
		}
		
		/**
		 * Confirms whether or not this is for the given effect (by ID)
		 */
		public boolean matches(MobEffect effect) {
			if (this.effectId == null) return false;
			
			return this.effectId.equals(ForgeRegistries.MOB_EFFECTS.getKey(effect));
		}
		
		/**
		 * Gets the configured resistance for this effect/entity combination
		 */
		public double getResist() {
			return this.resistance;
		}
		
		/**
		 * Gets the configured maximum magnitude for this effect/entity combination
		 */
		public int getMagnitude() {
			return this.maximumMagnitude;
		}
		
		/**
		 * Gets the configured duration for this effect/entity combination
		 */
		public int getDuration() {
			return this.duration;
		}
		
		void writeTo(FriendlyByteBuf buffer) {
			buffer.writeResourceLocation(effectId);
			buffer.writeDouble(resistance);
			buffer.writeInt(duration);
			buffer.writeInt(maximumMagnitude);
		}
		
		void readFrom(FriendlyByteBuf buffer) {
			this.effectId = buffer.readResourceLocation();
			this.resistance = buffer.readDouble();
			this.duration = buffer.readInt();
			this.maximumMagnitude = buffer.readInt();
		}
		
		void parse(JsonObject json, boolean allowNoEffectId) {
			if (!json.has("effectId")) {
				if (!allowNoEffectId)
					throw new RuntimeException("Missing effect ID in entity effect config whitelist");
			}else {
				this.effectId = new ResourceLocation(json.get("effectId").getAsString());
			}					
			
			if (json.has("resistance"))
				this.resistance = json.get("resistance").getAsDouble();
			
			if (json.has("duration"))
				this.duration = json.get("duration").getAsInt();
			
			if (json.has("maximumMagnitude"))
				this.maximumMagnitude = json.get("maximumMagnitude").getAsInt();
		}
	}

	
	@Override
	public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
		return ItemStack.EMPTY;
	}
}
