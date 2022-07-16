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


package com.fxbuildup.network;

import com.fxbuildup.FXBuildup;
import com.fxbuildup.network.handlers.ClientPacketHandler;
import com.fxbuildup.network.handlers.ServerPacketHandler;
import com.fxbuildup.network.packets.BuildupSync;
import com.fxbuildup.network.packets.Dodge;
import com.fxbuildup.network.packets.StaminaFlash;
import com.fxbuildup.network.packets.StaminaSync;
import com.fxbuildup.network.packets.Toast;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(modid = FXBuildup.MODID, bus = Bus.MOD)
public class PacketHandler {
	static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel network = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(FXBuildup.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	@SubscribeEvent
	public static void setup(final FMLCommonSetupEvent event) {
		int packet_id = 1;
		
		//Server -> Client Messages
		network.registerMessage(packet_id++, BuildupSync.class, BuildupSync::encode,
				BuildupSync::decode, ClientPacketHandler::handleBuildupSync);
		
		network.registerMessage(packet_id++, StaminaSync.class, StaminaSync::encode,
				StaminaSync::decode, ClientPacketHandler::handleStaminaSync);
		
		network.registerMessage(packet_id++, Toast.class, Toast::encode,
				Toast::decode, ClientPacketHandler::handleToast);
		
		network.registerMessage(packet_id++, StaminaFlash.class, StaminaFlash::encode,
				StaminaFlash::decode, ClientPacketHandler::handleStaminaFlash);
		
		//Client -> Server Messages
		network.registerMessage(packet_id++, Dodge.class, Dodge::encode,
				Dodge::decode, ServerPacketHandler::handleDodge);
	}
}
