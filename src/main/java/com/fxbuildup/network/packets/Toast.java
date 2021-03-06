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


package com.fxbuildup.network.packets;

import com.fxbuildup.gui.Hud;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

public class Toast {
	String effect;
	boolean isNew;
	
	public Toast(String effect, boolean isNew) {
		this.effect = effect;
		this.isNew = isNew;
	}
	
	public static void encode(final Toast msg, FriendlyByteBuf buf) {
		buf.writeUtf(msg.effect);
		buf.writeBoolean(msg.isNew);
	}
	
	public static Toast decode(FriendlyByteBuf buf) {
		return new Toast(buf.readUtf(), buf.readBoolean());
	}
	
	public void handle(Player player) {		
		if (!player.level.isClientSide)
			return;
		
		Hud.showApplicationToast(effect, isNew);
		player.playSound(SoundEvents.BEACON_POWER_SELECT, 1, 1);
	}
}
