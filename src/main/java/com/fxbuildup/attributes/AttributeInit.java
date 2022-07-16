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

package com.fxbuildup.attributes;

import com.fxbuildup.FXBuildup;
import com.fxbuildup.config.EffectBuildupConfig;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Custom attributes to support different stats within this mod.
 * @author Mithion
 *
 */
public class AttributeInit {
	public static DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, FXBuildup.MODID);
	
	/**
	 * The general resistance attribute; increasing this will increase the baseline amount that entities have.
	 */
	public static RegistryObject<Attribute> RESISTANCE = ATTRIBUTES.register("effect_resistance", () -> new ModAttribute("effect_resistance", EffectBuildupConfig.BASELINE_RESISTANCE.get()));
	
	/**
	 * The general stamina attribute that entities have.
	 */
	public static RegistryObject<Attribute> MAX_STAMINA = ATTRIBUTES.register("max_stamina", () -> new ModAttribute("max_stamina", EffectBuildupConfig.STAMINA_BASELINE.get()));
	
	/**
	 * The stamina regeneration rate per second that entities have.
	 */
	public static RegistryObject<Attribute> STAMINA_REGEN = ATTRIBUTES.register("stamina_regen", () -> new ModAttribute("stamina_regen", EffectBuildupConfig.STAMINA_REGEN_BASELINE.get()));
}
