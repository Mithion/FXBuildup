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


package com.fxbuildup.gui;

import java.awt.Color;
import java.util.Map.Entry;

import com.fxbuildup.attributes.AttributeInit;
import com.fxbuildup.capabilities.buildup.EffectBuildup;
import com.fxbuildup.capabilities.buildup.EffectBuildupProvider;
import com.fxbuildup.capabilities.stamina.StaminaProvider;
import com.fxbuildup.config.EffectBuildupConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(Dist.CLIENT)
public class Hud extends GuiComponent {
	
	//gui code is rough to begin with.  Let's avoid "magic numbers", and declare as much as we can up here.
	private static final int TOAST_FADE_IN_TIME = 10;
	private static final int TOAST_WAIT_TIME = 60;
	private static final int TOAST_FADE_OUT_TIME = 20;
	
	private static final int UNDERWATER_STAMINA_OFFSET = 10;
	
	private static final int EFFECT_ICON_SIZE = 20; 	//effect icon size including borders and padding
	private static final int EFFECT_BAR_WIDTH = 100;
	private static final int EFFECT_BAR_HEIGHT = 5;
	private static final int EFFECT_BAR_PADDING = 10;
	private static final int EFFECT_BADGE_PADDING = 3;
	
	//really basic singleton implementation
	public static Hud instance;
	
	//toast text
	private Component toastComponent;
	//could be done with a single variable, but this makes the math easier for me.
	private int fadeInCounter = 0;
	private int waitCounter = 0;
	private int fadeOutCounter = 0;
	
	private int staminaFlashCounter = 0;
	
	/**
	 * Register our main overlay with forge.
	 */	
	public static void registerOverlay(ForgeGui gui, PoseStack mStack, float partialTicks, int screenWidth, int screenHeight) {
		 gui.setupOverlayRenderState(true, false);
	        
	        Minecraft mc = Minecraft.getInstance();
	        
	        if (EffectBuildupConfig.INSTANCE.STAMINA_ENABLED.get() && !mc.player.isCreative() && !mc.player.isSpectator()) {
	        	mc.player.getCapability(StaminaProvider.CAP).ifPresent(stamina -> {
	        		instance.renderStamina(mStack, screenWidth, screenHeight, partialTicks, stamina.getAmount(), mc.player.getAttributeValue(AttributeInit.MAX_STAMINA.get()), stamina.isInCombat());
	        	});
	        	
	        }
	        
	        mc.player.getCapability(EffectBuildupProvider.CAP).ifPresent(buildup -> {
	        	instance.renderBuildup(mStack, buildup, mc.font, screenWidth, screenHeight, partialTicks);
	        });
	        	        
	        instance.renderToast(mStack, mc.font, screenWidth, screenHeight, partialTicks);
	}
	
	/**
	 * Displays a toast indicating that the buildup for an effect has rolled over and the effect has been applied or incremented
	 * @param effect The effect that is being added or incremented
	 * @param isNew Is this a new application of the effect or an increment (slightly changes wording)
	 */
	public static void showApplicationToast(String effectTranslation, boolean isNew) {
		String translatedEffectName = Component.translatable(effectTranslation).getString();
		String translatedAction = Component.translatable(isNew ? "gui.fxbuildup.applied" : "gui.fxbuildup.increased").getString();
		instance.toastComponent = Component.literal(String.format("%s %s", translatedEffectName, translatedAction));
		
		instance.fadeInCounter = 0; //fade in counts up for easier math
		instance.waitCounter = TOAST_WAIT_TIME;
		instance.fadeOutCounter = TOAST_FADE_OUT_TIME;
	}
	
	private void renderBuildup(PoseStack matrixStack, EffectBuildup buildup, Font fr, int screenWidth, int screenHeight, float partialTicks) {
		
		//no effects, don't need to do anything
		if (buildup.list().size() == 0)
			return;
		
		//start at center screen minus half the height the total effects will take up.  Ish.
		int yCoord = screenHeight / 2 - (int)Math.floor(buildup.list().size() / 2) * EFFECT_ICON_SIZE;		
		final int xCoord = screenWidth - EFFECT_BAR_WIDTH - EFFECT_ICON_SIZE - EFFECT_BAR_PADDING;
		
		Minecraft mc = Minecraft.getInstance();
		
		//render the effects
		for (Entry<ResourceLocation, Double> e : buildup.list().entrySet()) {
			MobEffect eff = ForgeRegistries.MOB_EFFECTS.getValue(e.getKey());
			if (eff != null) {
				double resistance = buildup.getResistanceTo(mc.player, eff);
				double fillPct = Mth.clamp(e.getValue() / resistance, 0, 1);
				
				renderEffectIcon(matrixStack, xCoord, yCoord - EFFECT_ICON_SIZE / 2 + EFFECT_BADGE_PADDING, screenWidth, screenHeight, eff);
				renderEffectBar(matrixStack, xCoord + EFFECT_ICON_SIZE + 7, yCoord + 2, EFFECT_BAR_WIDTH, EFFECT_BAR_HEIGHT, fillPct, eff.getColor(), false);
				yCoord += EFFECT_ICON_SIZE + EFFECT_BADGE_PADDING;
			}
		}
	}
	
	private void renderStamina(PoseStack matrixStack, int screenWidth, int screenHeight, float partialTicks, double stamina, double maxStamina, boolean isInCombat) {
		int x = screenWidth / 2 + 10;
		int y = screenHeight - 47;
		
		Minecraft mc = Minecraft.getInstance();
		if (mc.player.getAirSupply() < mc.player.getMaxAirSupply())
			y -= UNDERWATER_STAMINA_OFFSET;
		
		int color = 0xFF00CC00;
		if (staminaFlashCounter > 0) {
			double hue = Mth.abs((float) Math.sin((staminaFlashCounter + partialTicks) / 10.0f)) / 3.0f;			
			color = Mth.hsvToRgb((float)hue, 1.0F, 0.8F) | 0xFF000000;
		}
		
		renderEffectBar(matrixStack, x, y, 80, 5, stamina / maxStamina, color, true);
		
		if (isInCombat) {
			RenderSystem.setShaderTexture(0, GuiTextures.IN_COBMAT);
			blit(matrixStack, x + 74, y - 3, 12, 12, 0, 0, 16, 16, 16, 16);
		}
	}
	
	private void renderToast(PoseStack matrixStack, Font font, int screenWidth, int screenHeight, float partialTicks) {
		if (toastComponent == null || fadeOutCounter == 0) //toast is cleared
			return;
		
		float alpha = 1;
		//fade in
		if (fadeInCounter < TOAST_FADE_IN_TIME) {
			alpha = (fadeInCounter + partialTicks) / TOAST_FADE_IN_TIME;
		}else if (waitCounter == 0) {
			alpha = (fadeOutCounter - partialTicks) / TOAST_FADE_OUT_TIME;
		}
		
		alpha = Mth.clamp(alpha, 0, 1);
		
		//put the alpha into the color
		int color = 0x00AA0000 | ((int)(alpha * 255) << 24);
		
		//calculate coords
		int x = screenWidth / 2;
		int y = screenWidth / 4;
		
		float textScale = 1.75f;
		
		matrixStack.pushPose();
		matrixStack.scale(textScale, textScale, textScale);
		drawCenteredString(matrixStack, font, toastComponent, (int)(x / textScale), (int)(y / textScale), color);
		matrixStack.popPose();
	}
	
	private void renderEffectIcon(PoseStack matrixStack, int x, int y, int width, int height, MobEffect effect) {
		MobEffectInstance dummyInstance = new MobEffectInstance(effect);
		
		MobEffectTextureManager mobeffecttexturemanager = Minecraft.getInstance().getMobEffectTextures();
		
		 var renderer = net.minecraftforge.client.extensions.common.IClientMobEffectExtensions.of(dummyInstance);
         if (!renderer.isVisibleInGui(dummyInstance)) return;
		
		RenderSystem.enableBlend();
		RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		
		//frame
		this.blit(matrixStack, x, y, 141, 166, 24, 24);
		
		TextureAtlasSprite textureatlassprite = mobeffecttexturemanager.get(effect);
		Minecraft mc = Minecraft.getInstance();
		renderer.renderGuiIcon(dummyInstance, mc.gui, matrixStack, x, y, this.getBlitOffset(), 1.0f);
		
		RenderSystem.setShaderTexture(0, textureatlassprite.m_118414_().location());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
        blit(matrixStack, x + 3, y + 3, this.getBlitOffset(), 18, 18, textureatlassprite);
	}
	
	private void renderEffectBar(PoseStack stack, int x, int y, int width, int height, double fill, int color, boolean reverseFill) {
		//border
		GuiComponent.fill(stack, x - 1, y - 1, x + width + 1, y + height + 1, 0xFF000000);
		
		//background
		GuiHelper.drawGradientRect(stack, x, y, x + width, y + height, 0xFF333333, 0xFF111111);		
		
		//fill
		int fillWidth = (int)(width * fill);
		int color2 = new Color(color).darker().getRGB();
		if (reverseFill)
			GuiHelper.drawGradientRect(stack, x + width - fillWidth, y, x + width, y + height, color2, color);
		else
			GuiHelper.drawGradientRect(stack, x, y, x + fillWidth, y + height, color2, color);
		
		//ticks
		int firstTick = (int)(width * 0.25f);
		int secondTick = (int)(width * 0.5f);
		int thirdTick = (int)(width * 0.75f);
		
		GuiComponent.fill(stack, x + firstTick - 1, y + height - 2, x + firstTick + 1, y + height, 0xFF000000);
		GuiComponent.fill(stack, x + secondTick - 1, y + height - 2, x + secondTick + 1, y + height, 0xFF000000);
		GuiComponent.fill(stack, x + thirdTick - 1, y + height - 2, x + thirdTick + 1, y + height, 0xFF000000);
	}

	/*
	 * Advances toast display counters appropriately so it fades in, waits, then fades out before clearing itself.
	 */
	private void advanceToast() {
		if (toastComponent != null) {
			if (fadeInCounter < TOAST_FADE_IN_TIME) {
				fadeInCounter++;
			}else if (waitCounter > 0) {
				waitCounter--;
			}else if (fadeOutCounter > 0) {
				fadeOutCounter--;
			}else {
				toastComponent = null;
			}
		}
	}

	/**
	 * Advances the stamina flash counter 
	 */
	private void advanceStaminaFlash() {
		if (staminaFlashCounter > 0)
			staminaFlashCounter--;
	}
	
	public static void flashStamina() {
		instance.staminaFlashCounter = 110;
	}
	
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		instance.advanceToast();
		instance.advanceStaminaFlash();
	}
}
