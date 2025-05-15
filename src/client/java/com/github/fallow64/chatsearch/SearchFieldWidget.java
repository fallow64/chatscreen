package com.github.fallow64.chatsearch;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SearchFieldWidget extends TextFieldWidget {
    public SearchFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);

        this.setMaxLength(256);
        this.setDrawsBackground(false);
        this.setText("");
    }
}
