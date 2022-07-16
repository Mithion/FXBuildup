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


package com.fxbuildup.network.dispatch;

import com.fxbuildup.capabilities.buildup.EffectBuildup;
import com.fxbuildup.capabilities.buildup.EffectBuildupProvider;
import com.fxbuildup.capabilities.stamina.Stamina;
import com.fxbuildup.capabilities.stamina.StaminaProvider;
import com.fxbuildup.network.PacketHandler;
import com.fxbuildup.network.packets.StaminaFlash;
import com.fxbuildup.network.packets.Toast;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

public class ServerMessageDispatcher {
	public static void sendBuildupMessage(ServerPlayer player) {		
		LazyOptional<EffectBuildup> buildup = player.getCapability(EffectBuildupProvider.CAP);
		if (!buildup.isPresent())
			return;
		
		PacketHandler.network.send(
			PacketDistributor.PLAYER.with(() -> player),
			buildup.resolve().get().createPacket()
		);		
	}
	
	public static void sendStaminaMessage(ServerPlayer player) {		
		LazyOptional<Stamina> stamina = player.getCapability(StaminaProvider.CAP);
		if (!stamina.isPresent())
			return;
		
		PacketHandler.network.send(
			PacketDistributor.PLAYER.with(() -> player),
			stamina.resolve().get().createPacket()
		);		
	}
	
	public static void sendToast(ServerPlayer player, String effect, boolean isNew) {
		PacketHandler.network.send(
				PacketDistributor.PLAYER.with(() -> player),
				new Toast(effect, isNew)
			);		
	}
	
	public static void sendStaminaFlash(ServerPlayer player) {
		PacketHandler.network.send(
				PacketDistributor.PLAYER.with(() -> player),
				new StaminaFlash()
			);		
	}
}
