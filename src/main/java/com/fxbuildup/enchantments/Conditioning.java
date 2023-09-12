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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * This enchant increases the stamina regen rate a player has.
 * It does nothing to non-players as they don't have stamina.
 * It will still register if the config disables stamina, but it will be removed from as many lists as possible, and if applied will have no effect.
 * @author Mithion
 *
 */
public class Conditioning extends Enchantment {
	
	private static final UUID ID_ATTRIBUTE = UUID.fromString("6705f92c-abb9-461e-b64c-e92fcd28efc2");

	protected Conditioning() {
		super(Rarity.COMMON, EnchantmentCategory.ARMOR, new EquipmentSlot[] {
				EquipmentSlot.LEGS
		});
	}
	
	@Override
	public int getMinLevel() {
		return 1;
	}
	
	@Override
	public int getMaxLevel() {
		return 3;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get();
	}
	
	@Override
	public boolean isAllowedOnBooks() {
		return EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get();
	}
	
	@Override
	public boolean isDiscoverable() {
		return EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get();
	}
	
	@Override
	public boolean isTradeable() {
		return EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get();
	}
	
	@Override
	public boolean canEnchant(ItemStack pStack) {
		return EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get();
	}
	
	/**
	 * Applies the endurance stat modifier to a player.
	 * @param player The player to apply the stat modifier to.
	 * @param level The level of the enchantment.  Must be greater than 0.
	 */
	public static void apply(Player player, int level) {
		if (!EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get() || player == null || level < 1)
			return;
		
		try {
			if (player.getAttributes().hasAttribute(AttributeInit.STAMINA_REGEN.get()))
				player.getAttribute(AttributeInit.STAMINA_REGEN.get()).addTransientModifier(new AttributeModifier(ID_ATTRIBUTE, "conditioning-stamina", EffectBuildupConfig.INSTANCE.STAMINA_REGEN_ENCHANT.get() * level, Operation.ADDITION));
		}catch(Throwable t){
			FXBuildup.LOGGER.warn("Attempted to apply conditioning value to player but it failed:");
			FXBuildup.LOGGER.warn(t.getLocalizedMessage());
		}
	}
	
	/**
	 * Removes the endurance stat modifier from a player.
	 * @param player The player to remove the stat modifier from.
	 */
	public static void remove(Player player) {
		if (!EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get())
			return;
		
		try {
			if (player.getAttributes().hasAttribute(AttributeInit.STAMINA_REGEN.get()))
				player.getAttribute(AttributeInit.STAMINA_REGEN.get()).removeModifier(ID_ATTRIBUTE);
		}catch(Throwable t){
			FXBuildup.LOGGER.warn("Attempted to remove conditioning value from player but it failed:");
			FXBuildup.LOGGER.warn(t.getLocalizedMessage());
		}
	}
}
