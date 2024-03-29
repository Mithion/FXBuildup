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

import net.minecraft.network.FriendlyByteBuf;

public class StaminaSync {
	final double amount;
	final int cooldown;
	final int inCombatTicks;
	
	public StaminaSync(double amount, int cooldown, int inCombatTicks) {
		this.amount = amount;
		this.cooldown = cooldown;
		this.inCombatTicks = inCombatTicks;
	}
	
	public static void encode(final StaminaSync msg, FriendlyByteBuf buf) {
		buf.writeDouble(msg.amount);
		buf.writeInt(msg.cooldown);		
		buf.writeInt(msg.inCombatTicks);
	}
	
	public static StaminaSync decode(FriendlyByteBuf buf) {
		return new StaminaSync(buf.readDouble(), buf.readInt(), buf.readInt());		
	}

	public double getAmount() {
		return amount;
	}
	
	public int getPauseCounter() {
		return this.cooldown;
	}
	
	public int getInCombatTicks() {
		return this.inCombatTicks;
	}
}
