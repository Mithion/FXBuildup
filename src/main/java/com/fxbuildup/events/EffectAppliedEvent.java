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


package com.fxbuildup.events;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when an effect’s buildup is filled and it is about to be applied, can be canceled, and/or resultant values can be modified.
 * This event is cancelable.
 * @author Mithion
 *
 */
public class EffectAppliedEvent extends Event {
	final LivingEntity target;
	final MobEffectInstance effect;
	double buildupLeftover;
	int amplifierIncrease;
	
	public EffectAppliedEvent(LivingEntity target, MobEffectInstance inst, double buildupLeftover, int amplifierIncrease) {
		this.target = target;
		this.effect = inst;
		this.buildupLeftover = buildupLeftover;
		this.amplifierIncrease = amplifierIncrease;
	}
	
	public LivingEntity getTarget() {
		return target;
	}
	
	public MobEffectInstance getEffect() {
		return effect;
	}
	
	public double getBuildupLeftover() {
		return buildupLeftover;
	}
	
	public void setBuildupLeftover(double amount) {
		this.buildupLeftover = amount;
	}
	
	public int getAmplifierIncrease() {
		return this.amplifierIncrease;
	}
	
	public void setAmplifierIncrease(int amplifierIncrease) {
		this.amplifierIncrease = amplifierIncrease;
	}
	
	@Override
	public boolean isCancelable() {
		return true;
	}
}
