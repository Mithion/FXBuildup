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

package com.fxbuildup.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Central serverside config for the mod.
 * Get config values from here.
 * @author Mithion
 *
 */
public class EffectBuildupConfig {
	public static ForgeConfigSpec SERVERCONFIG;
	
	//===============================================
	// Effects
	//===============================================
	
	public static ForgeConfigSpec.DoubleValue BASELINE_RESISTANCE;
	public static ForgeConfigSpec.DoubleValue DECAY_RATE;
	public static ForgeConfigSpec.DoubleValue APPLICATION_RATE;
	public static ForgeConfigSpec.IntValue MAXIMUM_AMPLIFIER;
	public static ForgeConfigSpec.BooleanValue ALLOW_BED_RESET;
	public static ForgeConfigSpec.BooleanValue PLAYER_BUILDUP;
	public static ForgeConfigSpec.BooleanValue BOSS_BUILDUP;
	public static ForgeConfigSpec.BooleanValue MOB_BUILDUP;
	public static ForgeConfigSpec.DoubleValue LINGERING_BUILDUP_FACTOR;
	public static ForgeConfigSpec.DoubleValue AMBIENT_BUILDUP_FACTOR;	
	
	//===============================================
	// Dodging
	//===============================================
	
	public static ForgeConfigSpec.BooleanValue DODGING_ENABLED;
	public static ForgeConfigSpec.DoubleValue DODGE_STRENGTH;
	public static ForgeConfigSpec.DoubleValue DODGE_STAMINA_COST;
	
	//===============================================
	// Stamina
	//===============================================
	
	public static ForgeConfigSpec.BooleanValue STAMINA_ENABLED;
	public static ForgeConfigSpec.DoubleValue STAMINA_BASELINE;	
	public static ForgeConfigSpec.DoubleValue STAMINA_REGEN_BASELINE;
	public static ForgeConfigSpec.IntValue STAMINA_USE_REGEN_PAUSE;
	public static ForgeConfigSpec.BooleanValue SPRINT_STAMINA;
	public static ForgeConfigSpec.DoubleValue SPRINT_STAMINA_CONSUMPTION;
	public static ForgeConfigSpec.BooleanValue JUMP_STAMINA;
	public static ForgeConfigSpec.DoubleValue JUMP_STAMINA_CONSUMPTION;
	public static ForgeConfigSpec.DoubleValue JUMP_SPRINT_STAMINA_MULTIPLIER;
	public static ForgeConfigSpec.DoubleValue BLOCK_STAMINA_REGEN_MODIFIER;
	public static ForgeConfigSpec.DoubleValue BLOCK_STAMINA_DRAIN_RATE;
	public static ForgeConfigSpec.IntValue BLOCK_STAMINA_SHIELD_COOLDOWN;
	public static ForgeConfigSpec.DoubleValue MINIMUM_FOOD_FACTOR;
	public static ForgeConfigSpec.DoubleValue ATTACK_STAMINA_COST;
	
	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		
		initEffectsConfig(builder);
		initDodgingConfig(builder);
		initStaminaConfig(builder);
		
		EffectBuildupConfig.SERVERCONFIG = builder.build();
	}
	
	private static void initEffectsConfig(ForgeConfigSpec.Builder serverBuilder) {
		serverBuilder.comment("Effect Buildup // Effects Configuration").push("effects_config");
		
		BASELINE_RESISTANCE = serverBuilder.comment("how resistant is something to given effects by default?").defineInRange("baselineResistance", 1000, 0, Double.MAX_VALUE);
		DECAY_RATE = serverBuilder.comment("This is how fast effects decay by default, per second").defineInRange("decayRate", 25, 1, Double.MAX_VALUE);
		APPLICATION_RATE = serverBuilder.comment("This is how fast effects are built up by default.  Magnitude * Duration(in ticks) * <this value> = amount added.").defineInRange("applicationRate", 0.5, 0, Double.MAX_VALUE);
		MAXIMUM_AMPLIFIER = serverBuilder.comment("This is the maximum effect strength by default.").defineInRange("maximumAmplifier", 5, 0, 100);
		LINGERING_BUILDUP_FACTOR = serverBuilder.comment("Buildup is multiplied by this value when the effect is from a lingering potion.").defineInRange("lingeringMultiplier", 0.05, 0, Double.MAX_VALUE);
		AMBIENT_BUILDUP_FACTOR = serverBuilder.comment("Buildup is multiplied by this value when the effect is ambient.").defineInRange("ambientMultiplier", 0.05, 0, Double.MAX_VALUE);
		
		ALLOW_BED_RESET = serverBuilder.comment("Does sleeping in a bed reset status buildup?").define("allowSleepReset", false);
		PLAYER_BUILDUP = serverBuilder.comment("Should players be affected by status buildup?").define("doPlayerBuildup", true);
		BOSS_BUILDUP = serverBuilder.comment("Should bosses be affected by status buildup?").define("doBossBuildup", true);
		MOB_BUILDUP = serverBuilder.comment("Should mobs be affected by status buildup?").define("doMobBuildup", false);
		
		serverBuilder.pop();
	}
	
	private static void initDodgingConfig(ForgeConfigSpec.Builder serverBuilder) {
		serverBuilder.comment("Effect Buildup // Dodging Configuration").push("dodging_config");
		
		DODGING_ENABLED = serverBuilder.comment("Should the dodge functions be enabled?").define("enableDodging", true);
		DODGE_STRENGTH = serverBuilder.comment("How strong is dodging, movement-wise?").defineInRange("dodgeStrength", 1.0, 0.1, 3.0);
		DODGE_STAMINA_COST = serverBuilder.comment("How much stamina does dodging use?").defineInRange("dodgeStamina", 10.0, 0.0, Double.MAX_VALUE);
		
		serverBuilder.pop();
	}

	private static void initStaminaConfig(ForgeConfigSpec.Builder serverBuilder) {
		serverBuilder.comment("Effect Buildup // Stamina Configuration").push("stamina_config");
		
		STAMINA_ENABLED = serverBuilder.comment("Should stamina functionality be enabled?").define("enableStamina", true);
		STAMINA_BASELINE = serverBuilder.comment("How much stamina should players have by default?").defineInRange("baselineStamina", 100, 0, Double.MAX_VALUE);
		STAMINA_REGEN_BASELINE = serverBuilder.comment("How much stamina should players regen by default per second?").defineInRange("staminaRegenRate", 20, 0, Double.MAX_VALUE);
		STAMINA_USE_REGEN_PAUSE = serverBuilder.comment("How long, after dodging, should stamina not regenerate (in ticks)?").defineInRange("staminaRegenPause", 20, 0, Integer.MAX_VALUE);
		
		SPRINT_STAMINA = serverBuilder.comment("Should sprinting use and require stamina?").define("sprintStamina", true);
		SPRINT_STAMINA_CONSUMPTION = serverBuilder.comment("How much stamina should sprinting use per second?  Ignored if sprintStamina is false.").defineInRange("sprintStaminaUse", 5, 0, Double.MAX_VALUE);
		
		JUMP_STAMINA = serverBuilder.comment("Should jumping use and require stamina?").define("jumpStamina", true);
		JUMP_STAMINA_CONSUMPTION = serverBuilder.comment("How much stamina should jumping use per jump?  Even if jumpStamina is false, it is used in sprint jumping if that is enabled.").defineInRange("jumpStaminaUse", 10, 0, Double.MAX_VALUE);
		
		JUMP_SPRINT_STAMINA_MULTIPLIER = serverBuilder.comment("When sprint jumping, if either sprintStamina or jumpStamina is enabled, sprintStaminaUse and jumpStaminaUse are added together then multiplied by this value.  This is the stamina that is consumed.  If jumpStaminaUse is true, this calculated value is used in place of the baseline jump stamina value while sprinting.").defineInRange("sprintJumpStaminaModifier", 1.25, 0, Double.MAX_VALUE);
		
		BLOCK_STAMINA_REGEN_MODIFIER = serverBuilder.comment("When blocking, this value is multiplied into stamina regen rate and can be used to slow stamina regen.").defineInRange("blockHoldStaminaDrain", 5d, 0d, Double.MAX_VALUE);
		BLOCK_STAMINA_DRAIN_RATE = serverBuilder.comment("When blocking an attack, how much stamina should be drained?  If stamina is below this, the attack will not be blocked and the shield will be set on cooldown.  Set to zero to disable.").defineInRange("blockStaminaDrain", 10d, 0d, Double.MAX_VALUE);
		BLOCK_STAMINA_SHIELD_COOLDOWN = serverBuilder.comment("If a shield is disabled due to not enough stamina, how many ticks should it be disabled for?").defineInRange("shieldDisableTime", 40, 0, Integer.MAX_VALUE);
		
		MINIMUM_FOOD_FACTOR = serverBuilder.comment("With low food, stamina regen slows.  What is the lowest rate it should go (as a percent)?").defineInRange("lowestFoodMultiplier", 0.1d, 0.0d, 1.0d);
		ATTACK_STAMINA_COST = serverBuilder.comment("How much stamina should attacking (using left click) consume?  Set to zero to disable.").defineInRange("attackStaminaCost", 10, 0, Double.MAX_VALUE);
		
		serverBuilder.pop();
	}
}
