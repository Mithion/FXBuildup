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

package com.fxbuildup.capabilities.stamina;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Provider for the capability that handles player stamina.
 * Saves/Loads and defines the capability getter.
 * @author Mithion
 *
 */
public class StaminaProvider implements ICapabilitySerializable<Tag>{
	public static final Capability<Stamina> CAP = CapabilityManager.get(new CapabilityToken<>() {});
	private final LazyOptional<Stamina> holder = LazyOptional.of(Stamina::new);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return CAP.orEmpty(cap, holder);				
	}

	@Override
	public Tag serializeNBT() {
		Stamina instance = holder.orElse(null);
		CompoundTag tag = new CompoundTag();
		
		tag.putDouble("amount", instance.amount);
		
		return tag;
	}

	@Override
	public void deserializeNBT(Tag tag) {
		Stamina instance = holder.orElse(null);
		if (tag instanceof CompoundTag) {
			CompoundTag cTag = (CompoundTag) tag;
			
			if (cTag.contains("amount"))
				instance.amount = cTag.getDouble("amount");
		}
	}
}
