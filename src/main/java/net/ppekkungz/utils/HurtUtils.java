package net.ppekkungz.utils;

public class HurtUtils {
	// Default values for FPS control
	private static int MAX_FPS = 75; // Maximum FPS when at full health
	private static int MIN_FPS = 10;  // Minimum FPS when at critical health
	
	// Boolean to toggle the feature on/off
	private static boolean enabled = true;
	
	// Getters and Setters for FPS values
	public static void setMaxFps(int fps) {
		MAX_FPS = fps;
	}
	
	public static int getMaxFps() {
		return MAX_FPS;
	}
	
	public static void setMinFps(int fps) {
		MIN_FPS = fps;
	}
	
	public static int getMinFps() {
		return MIN_FPS;
	}
	
	// Getters and Setters for the enable/disable state
	public static void setEnabled(boolean state) {
		enabled = state;
	}
	
	public static boolean isEnabled() {
		return enabled;
	}
}
