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


package com.fxbuildup.capabilities;

import net.minecraft.world.entity.player.Player;

/**
 * Base class for capabilities that need to sync at some regular interval or immediately.
 * @author Mithion
 *
 */
public abstract class SyncedCapability {
	private enum SyncStatus {
		NONE,
		LAZY,
		IMMEDIATE
	}
	
	private SyncStatus syncStatus = SyncStatus.IMMEDIATE;
	private int syncCounter = -1;
	
	protected void setDirty(boolean immediateSync) {
		syncStatus = immediateSync ? SyncStatus.IMMEDIATE : SyncStatus.LAZY;
		syncCounter = immediateSync ? 0 : 200;
	}
	
	protected boolean needsSync() {
		return syncStatus != SyncStatus.NONE;
	}
	
	protected void tickSync(Player player) {
		if (syncStatus == SyncStatus.NONE)
			return;
		
		if (syncCounter > 0) {
			syncCounter--;
			return;
		}
		
		dispatchPacket(player);
		syncStatus = SyncStatus.NONE;		
	}
	
	protected abstract void dispatchPacket(Player player);
}
