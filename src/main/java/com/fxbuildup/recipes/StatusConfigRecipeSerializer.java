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

import java.util.HashMap;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class StatusConfigRecipeSerializer implements RecipeSerializer<StatusConfigRecipe> {

	public static HashMap<ResourceLocation, StatusConfigRecipe> ALL_RECIPES = new HashMap<ResourceLocation, StatusConfigRecipe>();
	
	@Override
	public final StatusConfigRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		StatusConfigRecipe inst = new StatusConfigRecipe(recipeId);
		
		if (json.has("effectId"))
			inst.effectId = new ResourceLocation(json.get("effectId").getAsString());
		else
			throw new RuntimeException("Status config recipe missing required parameter 'effectId'.");
		
		if (json.has("buildupRate"))
			inst.buildupRate = json.get("buildupRate").getAsDouble();
		
		if (json.has("decayRate"))
			inst.decayRate = json.get("decayRate").getAsDouble();
		
		if (json.has("applicationDuration"))
			inst.applicationDuration = json.get("applicationDuration").getAsInt();
		
		if (json.has("applicationMagnitude"))
			inst.applicationMagnitude = json.get("applicationMagnitude").getAsInt();
		
		if (json.has("maximumStrength"))
			inst.maximumAmplifier = json.get("maximumStrength").getAsInt();
		
		ALL_RECIPES.put(inst.effectId, inst);
		
		return inst;
	}
	
	@Override
	public final StatusConfigRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StatusConfigRecipe inst = new StatusConfigRecipe(recipeId);
		
		inst.buildupRate = buffer.readDouble();
		inst.decayRate = buffer.readDouble();
		inst.applicationDuration = buffer.readInt();
		inst.applicationMagnitude = buffer.readInt();
		inst.maximumAmplifier = buffer.readInt();
		
		ALL_RECIPES.put(inst.effectId, inst);
		
		return inst;
	}
	
	@Override
	public final void toNetwork(FriendlyByteBuf buffer, StatusConfigRecipe recipe) {
		buffer.writeDouble(recipe.buildupRate);
		buffer.writeDouble(recipe.decayRate);
		buffer.writeInt(recipe.applicationDuration);
		buffer.writeInt(recipe.applicationMagnitude);
		buffer.writeInt(recipe.maximumAmplifier);
	}
}
