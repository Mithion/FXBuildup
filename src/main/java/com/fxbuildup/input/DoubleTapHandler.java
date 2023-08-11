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


package com.fxbuildup.input;

import com.fxbuildup.config.EffectBuildupClientConfig;
import com.fxbuildup.config.KeybindInit;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DoubleTapHandler {
	
	boolean wasPressed = false;
	boolean wasReleased = false;
	int timer = 0;
	
	public boolean update(boolean movementKeyPressed) {
		if (timer > 0)
			timer--;
		
		if (KeybindInit.dodge.isUnbound()) {		
			if (!wasPressed && movementKeyPressed) {
				if (timer == 0) {
					timer = EffectBuildupClientConfig.DOUBLE_TAP_SENSITIVITY.get();
				}else if (wasReleased) {
					wasReleased = false;
					wasPressed = false;
					timer = 0;
					return true;
				}
			}
			
			if (wasPressed && !movementKeyPressed) {
				wasReleased = true;
			}		
			if (timer == 0) {
				wasReleased = false;
			}
			
			wasPressed = movementKeyPressed;
			
			
		}else {
			if (KeybindInit.dodge.isDown() && !wasPressed && movementKeyPressed) {
				wasPressed = true;
				return true;
			}else {
				wasPressed = false;
			}
		}
		
		return false;
	}
}
