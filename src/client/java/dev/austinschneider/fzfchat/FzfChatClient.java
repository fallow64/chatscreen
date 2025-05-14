package dev.austinschneider.fzfchat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FzfChatClient implements ClientModInitializer {
	public static KeyBinding keyBinding;

	@Override
	public void onInitializeClient() {
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.fzfchat.reverse-i-search",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"category.fzfchat.reverse-i-search"
		));
	}
}