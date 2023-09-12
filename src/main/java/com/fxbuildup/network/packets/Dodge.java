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

import com.fxbuildup.capabilities.stamina.Stamina;
import com.fxbuildup.config.EffectBuildupConfig;
import com.fxbuildup.events.PlayerDodging;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

public class Dodge{
	public enum Direction {
		LEFT,
		RIGHT,
		BACK
	}
	
	final Direction direction;
	
	public Dodge() {
		this(Direction.LEFT);
	}
	
	public Dodge(Direction direction) {
		this.direction = direction;
	}
	
	public static void encode(final Dodge msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.direction.ordinal());
	}
	
	public static Dodge decode(FriendlyByteBuf buf) {
		return new Dodge(Direction.values()[buf.readInt()]);
	}
	
	public void handle(ServerPlayer player) {		
		if (!player.isOnGround() && !player.isInWaterOrBubble())
			return;
		
		double stamCost = EffectBuildupConfig.INSTANCE.DODGE_STAMINA_COST.get();
		if (!Stamina.consume(player, stamCost, true))
			return;
		
		Vec3 forward = Vec3.directionFromRotation(0, player.getYHeadRot());
		Vec3 dodgeDelta;
		double strength = EffectBuildupConfig.INSTANCE.DODGE_STRENGTH.get();
		
		if (direction == Direction.BACK) {
			dodgeDelta = forward.normalize().scale(-strength);			
		}else {
			Vec3 side = forward.cross(new Vec3(0,1,0));
			if (direction == Direction.LEFT)
				dodgeDelta = side.normalize().scale(-strength);
			else
				dodgeDelta = side.normalize().scale(strength);
		}
		
		if (MinecraftForge.EVENT_BUS.post(new PlayerDodging(player)))
			return;
		
		player.push(dodgeDelta.x, dodgeDelta.y, dodgeDelta.z);
		player.hurtMarked = true;
		player.connection.send(new ClientboundSetEntityMotionPacket(player));
	}
}
