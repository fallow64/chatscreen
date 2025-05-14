package dev.austinschneider.fzfchat.mixin.client;

import dev.austinschneider.fzfchat.FzfChatClient;
import dev.austinschneider.fzfchat.SearchScreen;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A mixin to hook when the user presses Ctrl+R while typing in chat.
 */
@Mixin(Keyboard.class)
public class KeyboardOnKeyMixin {

    @Inject(method="onKey", at = @At("RETURN"))
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo cbInfo) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.currentScreen instanceof ChatScreen chatScreen) {
            boolean isNormalChatScreen = !(chatScreen instanceof SearchScreen);
            boolean wasSearchKeyPressed = FzfChatClient.keyBinding.matchesKey(key, scancode);
            boolean wasCtrlHeld = modifiers == GLFW.GLFW_MOD_CONTROL;

            if (wasCtrlHeld && wasSearchKeyPressed && isNormalChatScreen) {
                client.setScreen(new SearchScreen(chatScreen));
            }
        }
    }

}
