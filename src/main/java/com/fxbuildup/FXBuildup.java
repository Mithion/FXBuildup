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

package com.fxbuildup;

import org.slf4j.Logger;

import com.fxbuildup.attributes.AttributeInit;
import com.fxbuildup.config.EffectBuildupClientConfig;
import com.fxbuildup.config.EffectBuildupConfig;
import com.fxbuildup.config.KeybindInit;
import com.fxbuildup.enchantments.EnchantmentInit;
import com.fxbuildup.events.handlers.ClientEventHandler;
import com.fxbuildup.gui.GuiInit;
import com.fxbuildup.proxy.ClientProxy;
import com.fxbuildup.proxy.ISidedProxy;
import com.fxbuildup.proxy.ServerProxy;
import com.fxbuildup.recipes.RecipeInit;
import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("fxbuildup")
public class FXBuildup
{
	public static final String MODID = "fxbuildup";
    public static final Logger LOGGER = LogUtils.getLogger();
    final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    private static ISidedProxy proxy;

    public FXBuildup(){
    	//load configs
    	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EffectBuildupConfig.SERVERCONFIG, "fx-buildup.toml");
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, EffectBuildupClientConfig.CLIENT_SPEC);
    	
    	AttributeInit.ATTRIBUTES.register(modEventBus);
    	RecipeInit.SERIALIZERS.register(modEventBus);
    	EnchantmentInit.ENCHANTMENTS.register(modEventBus);
    	
    	DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
    		MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
    		modEventBus.register(GuiInit.class);
    		modEventBus.register(KeybindInit.class);
    		proxy = new ClientProxy();
    	});
    	
    	DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {			
			proxy = new ServerProxy();
		});
    }
    
    public static ISidedProxy getProxy() {
    	return proxy;
    }
}
