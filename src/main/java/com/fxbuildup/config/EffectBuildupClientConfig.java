/**
 * Copyright 2023 Mithion
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

import org.apache.commons.lang3.tuple.Pair;

import com.fxbuildup.FXBuildup;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = FXBuildup.MODID, bus = Bus.MOD)
public class EffectBuildupClientConfig {
	
	public static final EffectBuildupClientConfig INSTANCE;
	public static final ForgeConfigSpec CLIENT_SPEC;
	
	static {
		final Pair<EffectBuildupClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(EffectBuildupClientConfig::new);
		INSTANCE = specPair.getLeft();
		CLIENT_SPEC = specPair.getRight();
	}
	
	public static ForgeConfigSpec.IntValue DOUBLE_TAP_SENSITIVITY;
	
	public EffectBuildupClientConfig(ForgeConfigSpec.Builder clientBuilder) {
		clientBuilder.comment("FXBuildup // Client Settings").push("fxb_client_settings");
		
		DOUBLE_TAP_SENSITIVITY = clientBuilder.comment("Change how many ticks can pass between presses of the directional keys to consider it a dodge input.  Lower will require you to double tap faster in order to dodge.  If you bind the dodge key to anything, this setting will be ignored.").defineInRange("dodgeSensitivity", 7, 1, 20);		
		
		clientBuilder.pop();
	}
}
