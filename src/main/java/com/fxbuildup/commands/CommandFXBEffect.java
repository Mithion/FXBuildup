/**
 * Copyright 2023 Mithion
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


package com.fxbuildup.commands;

import java.util.Collection;

import javax.annotation.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CommandFXBEffect{
	private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.give.failed"));
	
	 public static void register(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext p_251610_) {
	      pDispatcher.register(Commands.literal("fxbeffect").requires((p_136958_) -> {
	         return p_136958_.hasPermission(2);
	      }).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("effect", ResourceArgument.resource(p_251610_, Registries.MOB_EFFECT)).executes((p_136978_) -> {
	         return giveEffect(p_136978_.getSource(), EntityArgument.getEntities(p_136978_, "targets"), ResourceArgument.getMobEffect(p_136978_, "effect"), (Integer)null, 0, true);
	      }).then(Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((p_136976_) -> {
	         return giveEffect(p_136976_.getSource(), EntityArgument.getEntities(p_136976_, "targets"), ResourceArgument.getMobEffect(p_136976_, "effect"), IntegerArgumentType.getInteger(p_136976_, "seconds"), 0, true);
	      }).then(Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((p_136974_) -> {
	         return giveEffect(p_136974_.getSource(), EntityArgument.getEntities(p_136974_, "targets"), ResourceArgument.getMobEffect(p_136974_, "effect"), IntegerArgumentType.getInteger(p_136974_, "seconds"), IntegerArgumentType.getInteger(p_136974_, "amplifier"), true);
	      }).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((p_136956_) -> {
	         return giveEffect(p_136956_.getSource(), EntityArgument.getEntities(p_136956_, "targets"), ResourceArgument.getMobEffect(p_136956_, "effect"), IntegerArgumentType.getInteger(p_136956_, "seconds"), IntegerArgumentType.getInteger(p_136956_, "amplifier"), !BoolArgumentType.getBool(p_136956_, "hideParticles"));
	      }))))))));
	   }

	   private static int giveEffect(CommandSourceStack pSource, Collection<? extends Entity> pTargets, Reference<MobEffect> pEffect, @Nullable Integer pSeconds, int pAmplifier, boolean pShowParticles) throws CommandSyntaxException {
	      int i = 0;
	      int j;
	      if (pSeconds != null) {
	         if (pEffect.get().isInstantenous()) {
	            j = pSeconds;
	         } else {
	            j = pSeconds * 20;
	         }
	      } else if (pEffect.get().isInstantenous()) {
	         j = 1;
	      } else {
	         j = 600;
	      }

	      for(Entity entity : pTargets) {
	         if (entity instanceof LivingEntity) {
	            MobEffectInstance mobeffectinstance = new MobEffectInstance(pEffect.get(), j, pAmplifier, false, pShowParticles);
	            ((LivingEntity)entity).getPersistentData().putBoolean("fxb_force", true);
	            if (((LivingEntity)entity).addEffect(mobeffectinstance, pSource.getEntity())) {
	               ++i;
	            }
	            ((LivingEntity)entity).getPersistentData().remove("fxb_force");
	         }
	      }
	      
	      if (i == 0) {
	         throw ERROR_GIVE_FAILED.create();
	      } else {
	         if (pTargets.size() == 1) {
	            pSource.sendSuccess(Component.translatable("commands.effect.give.success.single", pEffect.get().getDisplayName(), pTargets.iterator().next().getDisplayName(), j / 20), true);
	         } else {
	            pSource.sendSuccess(Component.translatable("commands.effect.give.success.multiple", pEffect.get().getDisplayName(), pTargets.size(), j / 20), true);
	         }

	         return i;
	      }
	   }

}
