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

import com.fxbuildup.FXBuildup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid=FXBuildup.MODID, bus=Bus.MOD)
public class RecipeInit {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FXBuildup.MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, FXBuildup.MODID);
	
	public static final RegistryObject<StatusConfigRecipeSerializer> STATUS_CONFIG_SERIALIZER = SERIALIZERS.register("status_config", StatusConfigRecipeSerializer::new);
	public static final RegistryObject<EntityConfigRecipeSerializer> ENTITY_CONFIG_SERIALIZER = SERIALIZERS.register("entity_config", EntityConfigRecipeSerializer::new);
	
	public static RegistryObject<RecipeType<StatusConfigRecipe>> STATUS_CONFIG_TYPE = RECIPE_TYPES.register("status-type", () -> new RecipeType<StatusConfigRecipe>() {
			@Override
			public String toString() {
				return new ResourceLocation(FXBuildup.MODID, "status-type").toString();
			}
		});
	
	public static RegistryObject<RecipeType<EntityConfigRecipe>> ENTITY_CONFIG_TYPE = RECIPE_TYPES.register("entity-type", () -> new RecipeType<EntityConfigRecipe>() {
		@Override
		public String toString() {
			return new ResourceLocation(FXBuildup.MODID, "entity-type").toString();
		}
	});
}
