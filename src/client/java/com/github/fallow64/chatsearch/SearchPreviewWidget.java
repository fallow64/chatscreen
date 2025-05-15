package com.github.fallow64.chatsearch;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * The search preview widget, meant to allow for the preview of the current match.
 * This has to be a new class due to things like syntax highlighting and autocomplete suggestions.
 * <br />
 * To set the preview, you only need to use .setText().
 */
public class SearchPreviewWidget extends TextFieldWidget {

    public SearchPreviewWidget(TextRenderer textRenderer, int x, int y, int width, int height, String originalText) {
        super(textRenderer, x, y, width, height, Text.translatable("chat.editBox"));

        this.setEditable(false);
        this.setFocusUnlocked(true);
        this.setMaxLength(256);
        this.setDrawsBackground(false);
        this.setText(originalText);
    }

    @Override
    public void setSuggestion(@Nullable String ignoredSuggestion) {
        // Disable suggestions
    }

    @Override
    public void onClick(double ignoredMouseX, double ignoredMouseY) {
        // Disable clicking (prevent changing cursor position)
    }

}
