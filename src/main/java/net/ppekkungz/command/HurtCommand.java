package net.ppekkungz.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.ppekkungz.client.HurtUtilsClient;
import net.ppekkungz.utils.HurtUtils;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class HurtCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("fpshealthmod")
				.then(literal("help").executes(context -> { showHelp(context); return 1;}))
				.then(literal("setmax").then(argument("fps", IntegerArgumentType.integer(20, 1000)).executes(context -> { int fps = IntegerArgumentType.getInteger(context, "fps"); setMaxFps(context, fps); return 1;})))
				.then(literal("setmin").then(argument("fps", IntegerArgumentType.integer(5, 1000)).executes(context -> {int fps = IntegerArgumentType.getInteger(context, "fps");setMinFps(context, fps);return 1;})))
				.then(literal("toggle")
						.then(argument("enabled", BoolArgumentType.bool())
								.executes(context -> {
									boolean enabled = BoolArgumentType.getBool(context, "enabled");
									setEnabled(context, enabled);
									return 1;
								}))
						.executes(context -> {
							boolean currentState = HurtUtils.isEnabled();
							setEnabled(context, !currentState);
							return 1;
						}))
				.then(literal("status").executes(context -> { showStatus(context); return 1; })).executes(context -> { showStatus(context);return 1; })
		);
	}
	
	private static void showHelp(CommandContext<FabricClientCommandSource> context) {
		context.getSource().sendFeedback(Text.literal("§a=== FPS Health Mod Commands ==="));
		context.getSource().sendFeedback(Text.literal("§7/fpshealthmod §f- Show current status"));
		context.getSource().sendFeedback(Text.literal("§7/fpshealthmod help §f- Show this help"));
		context.getSource().sendFeedback(Text.literal("§7/fpshealthmod setmax <fps> §f- Set maximum FPS (full health)"));
		context.getSource().sendFeedback(Text.literal("§7/fpshealthmod setmin <fps> §f- Set minimum FPS (zero health)"));
		context.getSource().sendFeedback(Text.literal("§7/fpshealthmod toggle §f- Toggle the mod on/off"));
		context.getSource().sendFeedback(Text.literal("§7/fpshealthmod toggle <true/false> §f- Set mod state"));
		context.getSource().sendFeedback(Text.literal("§7/fpshealthmod status §f- Show current configuration"));
		context.getSource().sendFeedback(Text.literal("By. §d@PPekKunGzDev§f, §6Claude.Ai"));
	}
	
	private static void setMaxFps(CommandContext<FabricClientCommandSource> context, int fps) {
		int minFps = HurtUtils.getMinFps();
		
		if (fps <= minFps) {
			context.getSource().sendError(Text.literal("Maximum FPS must be greater than minimum FPS (" + minFps + ")"));
			return;
		}
		
		HurtUtils.setMaxFps(fps);
		context.getSource().sendFeedback(Text.literal("Maximum FPS set to: " + fps));
	}
	
	private static void setMinFps(CommandContext<FabricClientCommandSource> context, int fps) {
		int maxFps = HurtUtils.getMaxFps();
		
		if (fps >= maxFps) {
			context.getSource().sendError(Text.literal("Minimum FPS must be less than maximum FPS (" + maxFps + ")"));
			return;
		}
		
		HurtUtils.setMinFps(fps);
		context.getSource().sendFeedback(Text.literal("Minimum FPS set to: " + fps));
	}
	
	private static void setEnabled(CommandContext<FabricClientCommandSource> context, boolean enabled) {
		HurtUtils.setEnabled(enabled);
		if (enabled) {
			context.getSource().sendFeedback(Text.literal("FPS Health Mod: §aEnabled"));
		} else {
			context.getSource().sendFeedback(Text.literal("FPS Health Mod: §cDisabled"));
		}
	}
	
	private static void showStatus(CommandContext<FabricClientCommandSource> context) {
		boolean enabled = HurtUtils.isEnabled();
		int minFps = HurtUtils.getMinFps();
		int maxFps = HurtUtils.getMaxFps();
		
		context.getSource().sendFeedback(Text.literal("§a=== FPS Health Mod Status ==="));
		context.getSource().sendFeedback(Text.literal("Status: " + (enabled ? "§aEnabled" : "§cDisabled")));
		context.getSource().sendFeedback(Text.literal("Maximum FPS (full health): §b" + maxFps));
		context.getSource().sendFeedback(Text.literal("Minimum FPS (zero health): §b" + minFps));
		context.getSource().sendFeedback(Text.literal("Use §7/fpshealthmod help§f for commands"));
	}
}