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

package com.fxbuildup.capabilities.buildup;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Provider for the capability that handles effect buildup.
 * Saves/Loads and defines the capability getter.
 * @author Mithion
 *
 */
public class EffectBuildupProvider implements ICapabilitySerializable<Tag>{

	private static final String NBT_LIST = "list";
	private static final String NBT_KEY = "key";
	private static final String NBT_VALUE = "value";
	
	public static final Capability<EffectBuildup> CAP = CapabilityManager.get(new CapabilityToken<>() {});
	private final LazyOptional<EffectBuildup> holder = LazyOptional.of(EffectBuildup::new);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return CAP.orEmpty(cap, holder);				
	}

	@Override
	public Tag serializeNBT() {
		EffectBuildup instance = holder.orElse(null);
		CompoundTag tag = new CompoundTag();
		
		ListTag list = new ListTag();
		instance.buildup.forEach((k,v) -> {
			CompoundTag elem = new CompoundTag();
			elem.putString(NBT_KEY, k.toString());
			elem.putDouble(NBT_VALUE, v);
			list.add(elem);
		});
		
		tag.put(NBT_LIST, list);
		
		return tag;
	}

	@Override
	public void deserializeNBT(Tag tag) {
		EffectBuildup instance = holder.orElse(null);
		if (tag instanceof CompoundTag) {
			CompoundTag cTag = (CompoundTag) tag;
			instance.buildup.clear();
			if (cTag.contains(NBT_LIST, Tag.TAG_COMPOUND)) {
				ListTag list = cTag.getList(NBT_LIST, Tag.TAG_COMPOUND);
				list.forEach(elem -> {
					CompoundTag eTag = (CompoundTag)elem;
					if (eTag.contains(NBT_KEY, Tag.TAG_STRING) && eTag.contains(NBT_VALUE, Tag.TAG_DOUBLE)) {
						instance.buildup.put(new ResourceLocation(eTag.getString(NBT_KEY)), eTag.getDouble(NBT_VALUE));
					}
				});
			}
		}
	}

}
