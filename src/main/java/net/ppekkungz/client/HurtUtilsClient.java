package net.ppekkungz.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.ppekkungz.command.HurtCommand;
import net.ppekkungz.utils.HurtUtils;

public class HurtUtilsClient implements ClientModInitializer {
	private static int currentFrameRate;
	private static long lastFpsUpdate = 0;
	private static int frameCount = 0;
	private static int displayedFps = 0;
	
	@Override
	public void onInitializeClient() {
		// Register commands
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				HurtCommand.register(dispatcher)
		);
		
		// Register client tick event
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && HurtUtils.isEnabled()) {
				// Calculate health percentage
				PlayerEntity player = client.player;
				float maxHealth = player.getMaxHealth();
				float currentHealth = player.getHealth();
				float healthPercentage = (currentHealth / maxHealth) * 100f;
				
				// Adjust FPS limit based on health percentage
				int targetFps = (int) (HurtUtils.getMinFps() + (healthPercentage / 100f) * (HurtUtils.getMaxFps() - HurtUtils.getMinFps()));
				
				// Clamp FPS between MIN_FPS and MAX_FPS
				currentFrameRate = Math.max(HurtUtils.getMinFps(), Math.min(HurtUtils.getMaxFps(), targetFps));
				
				// Apply the FPS limit
				GameOptions options = client.options;
				options.getMaxFps().setValue(currentFrameRate);
				
				// Count frames for FPS calculation
				frameCount++;
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastFpsUpdate >= 1000) {
					displayedFps = frameCount;
					frameCount = 0;
					lastFpsUpdate = currentTime;
				}
			} else if (client.player != null && !HurtUtils.isEnabled()) {
				// Restore default FPS settings
				GameOptions options = client.options;
				if (options.getMaxFps().getValue() != HurtUtils.getMaxFps()) {
					options.getMaxFps().setValue(HurtUtils.getMaxFps());
				}
			}
		});
		
		// Register HUD render event
		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			if (!HurtUtils.isEnabled()) return;
			
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.player != null) {
				PlayerEntity player = client.player;
				float maxHealth = player.getMaxHealth();
				float currentHealth = player.getHealth();
				float healthPercentage = (currentHealth / maxHealth) * 100f;
				
				// Display FPS and health info
				TextRenderer textRenderer = client.textRenderer;
				String fpsText = String.format("FPS: %d", currentFrameRate);
				String healthText = String.format("Health: %.1f%%", healthPercentage);
				
				// Render text on screen
				drawContext.drawText(textRenderer, fpsText, 10, 10, 0xFFFFFF, true);
				drawContext.drawText(textRenderer, healthText, 10, 22, getHealthColor(healthPercentage), true);
			}
		});
	}
	
	private static int getHealthColor(float percentage) {
		if (percentage > 75) {
			return 0x55FF55; // Green
		} else if (percentage > 50) {
			return 0xFFFF55; // Yellow
		} else if (percentage > 25) {
			return 0xFFAA00; // Orange
		} else {
			return 0xFF5555; // Red
		}
	}
}
