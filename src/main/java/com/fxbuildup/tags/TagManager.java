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

package com.fxbuildup.tags;

import com.fxbuildup.FXBuildup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

/**
 * Helper to manage the various tags this mod expects to exist.
 * @author Mithion
 *
 */
public class TagManager {
	private static final ResourceLocation TAG_BUILDUP_EFFECTS = new ResourceLocation(FXBuildup.MODID, "buildup_effects");
	private static final ResourceLocation TAG_INSTANT_WATER_EFFECTS = new ResourceLocation(FXBuildup.MODID, "instant_water_effects");
	private static final ResourceLocation TAG_INSTANT_LAVA_EFFECTS = new ResourceLocation(FXBuildup.MODID, "instant_lava_effects");
	
	/**
	 * Checks if the given effect is tagged to be managed by the buildup system
	 */
	public static final boolean isEffectBuildup(MobEffect effect) {
		return isEffectInTag(effect, TAG_BUILDUP_EFFECTS);
	}
	
	/**
	 * Checks if the given effect is tagged to be applied instantaneously (essentially bypassing the buildup system regardless of tagging) when the target is in water.
	 */
	public static final boolean isEffectInstantInWater(MobEffect effect) {
		return isEffectInTag(effect, TAG_INSTANT_WATER_EFFECTS);	
	}
	
	/**
	 * Checks if the given effect is tagged to be applied instantaneously (essentially bypassing the buildup system regardless of tagging) when the target is in lava.
	 */
	public static final boolean isEffectInstantInLava(MobEffect effect) {
		return isEffectInTag(effect, TAG_INSTANT_LAVA_EFFECTS);
	}	
	
	/**
	 * Convenience method to check if the given effect is within the tag
	 */
	private static final boolean isEffectInTag(MobEffect effect, ResourceLocation tagId) {
		ITag<MobEffect> tag = ForgeRegistries.MOB_EFFECTS.tags().getTag(ForgeRegistries.MOB_EFFECTS.tags().createTagKey(tagId));
		if (tag != null) {
			return tag.contains(effect);
		}
		
		return false;
	}
}
