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

public class EntityConfigRecipeSerializer implements RecipeSerializer<EntityConfigRecipe> {

	public static HashMap<ResourceLocation, EntityConfigRecipe> ALL_RECIPES = new HashMap<ResourceLocation, EntityConfigRecipe>();
	

	@Override
	public final EntityConfigRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		EntityConfigRecipe inst = new EntityConfigRecipe(recipeId);		
		inst.parse(json);
		
		ALL_RECIPES.put(recipeId, inst);
		
		return inst;
	}
	
	@Override
	public final EntityConfigRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		EntityConfigRecipe inst = new EntityConfigRecipe(recipeId);		
		inst.read(buffer);
		
		ALL_RECIPES.put(recipeId, inst);
		
		return inst;
	}
	
	@Override
	public final void toNetwork(FriendlyByteBuf buffer, EntityConfigRecipe recipe) {
		recipe.write(buffer);
	}
}
