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


package com.fxbuildup.enchantments;

import java.util.UUID;

import com.fxbuildup.FXBuildup;
import com.fxbuildup.attributes.AttributeInit;
import com.fxbuildup.config.EffectBuildupConfig;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Enchantment that boosts the resistance stat of a living entity.
 * @author Mithion
 *
 */
public class Resilience extends Enchantment{

	private static final UUID ID_ATTRIBUTE = UUID.fromString("9bc8c603-5d6c-499e-97c4-274600ab3d57");
	
	protected Resilience() {
		super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[] {
				EquipmentSlot.CHEST,
				EquipmentSlot.FEET,
				EquipmentSlot.LEGS,
				EquipmentSlot.HEAD
		});
	}
	
	@Override
	public int getMinLevel() {
		return 1;
	}
	
	@Override
	public int getMaxLevel() {
		return 5;
	}
	
	/**
	 * Applies the resilience stat modifier to a player.
	 * @param living The entity to apply the stat modifier to.
	 * @param level The level of the enchantment.  Must be greater than 0.
	 */
	public static void apply(LivingEntity living, int level) {
		if (living == null || level < 1)
			return;
		
		try {
			if (living.getAttributes().hasAttribute(AttributeInit.RESISTANCE.get()))
				living.getAttribute(AttributeInit.RESISTANCE.get()).addTransientModifier(new AttributeModifier(ID_ATTRIBUTE, "resilience-resistance", EffectBuildupConfig.INSTANCE.ENCHANTMENT_RESISTANCE.get() * level, Operation.ADDITION));
		}catch(Throwable t){
			FXBuildup.LOGGER.warn("Attempted to apply resilience value to player but it failed:");
			FXBuildup.LOGGER.warn(t.getLocalizedMessage());
		}
	}
	
	/**
	 * Removes the endurance stat modifier from a player.
	 * @param living The entity to remove the stat modifier from.
	 */
	public static void remove(LivingEntity living) {	
		try {
			if (living.getAttributes().hasAttribute(AttributeInit.RESISTANCE.get()))
				living.getAttribute(AttributeInit.RESISTANCE.get()).removeModifier(ID_ATTRIBUTE);
		}catch(Throwable t){
			FXBuildup.LOGGER.warn("Attempted to remove resilience value from player but it failed:");
			FXBuildup.LOGGER.warn(t.getLocalizedMessage());
		}
	}
}
