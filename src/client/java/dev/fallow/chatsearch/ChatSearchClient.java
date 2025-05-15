package dev.fallow.chatsearch;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ChatSearchClient implements ClientModInitializer {
    public static KeyBinding keyBinding;

    @Override
    public void onInitializeClient() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.chatsearch.reverse-i-search", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.chatsearch.reverse-i-search"));
    }
}