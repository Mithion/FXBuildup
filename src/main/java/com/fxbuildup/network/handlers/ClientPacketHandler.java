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


package com.fxbuildup.network.handlers;

import java.util.Optional;
import java.util.function.Supplier;

import com.fxbuildup.FXBuildup;
import com.fxbuildup.capabilities.buildup.EffectBuildupProvider;
import com.fxbuildup.capabilities.stamina.StaminaProvider;
import com.fxbuildup.network.packets.BuildupSync;
import com.fxbuildup.network.packets.StaminaFlash;
import com.fxbuildup.network.packets.StaminaSync;
import com.fxbuildup.network.packets.Toast;

import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class ClientPacketHandler {
	public static void handleBuildupSync(final BuildupSync message, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		
		Optional<Level> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (!clientWorld.isPresent()) {
			FXBuildup.LOGGER.error("BuildupSync context could not provide a ClientWorld");
			return;
		}
		
		ctx.enqueueWork(() -> {
			FXBuildup.getProxy().getClientPlayer().getCapability(EffectBuildupProvider.CAP).ifPresent(buildup -> {
				buildup.handlePacket(message);
			});
		});
		
		ctxSupplier.get().setPacketHandled(true);
	}
	
	public static void handleStaminaSync(final StaminaSync message, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		
		Optional<Level> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (!clientWorld.isPresent()) {
			FXBuildup.LOGGER.error("StaminaSync context could not provide a ClientWorld");
			return;
		}
		
		ctx.enqueueWork(() -> {
			FXBuildup.getProxy().getClientPlayer().getCapability(StaminaProvider.CAP).ifPresent(buildup -> {
				buildup.handlePacket(message);
			});
		});
		
		ctxSupplier.get().setPacketHandled(true);
	}
	
	public static void handleToast(final Toast message, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		
		Optional<Level> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (!clientWorld.isPresent()) {
			FXBuildup.LOGGER.error("Toast context could not provide a ClientWorld");
			return;
		}
		
		ctx.enqueueWork(() -> {
			message.handle(FXBuildup.getProxy().getClientPlayer());
		});
		
		ctxSupplier.get().setPacketHandled(true);
	}

	public static void handleStaminaFlash(final StaminaFlash message, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		
		Optional<Level> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (!clientWorld.isPresent()) {
			FXBuildup.LOGGER.error("StaminaFlash context could not provide a ClientWorld");
			return;
		}
		
		ctx.enqueueWork(() -> {
			message.handle(FXBuildup.getProxy().getClientPlayer());
		});		
		
		ctxSupplier.get().setPacketHandled(true);
	}
}
