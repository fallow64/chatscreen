package dev.austinschneider.fzfchat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.collection.ArrayListDeque;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A custom screen extending ChatScreen that is used when reverse searching chat history.
 * <br />
 * When a search is started, this screen replaces the normal chat screen.
 */
public class SearchScreen extends ChatScreen {
    private final MinecraftClient client;
    /**
     * The original message before the search started.
     */
    private final String originalMessage;
    /**
     * The chat input widget.
     */
    private TextFieldWidget searchField;
    /**
     * The currently selected match.
     */
    private String currentMatch;
    /**
     * The amount of times to skip while searching.
     */
    private int skipCount;

    public SearchScreen(ChatScreen parent) {
        super("");

        this.client = MinecraftClient.getInstance();

        try {
            // Get the original input
            Field originalChatField = ChatScreen.class.getDeclaredField("chatField");
            originalChatField.setAccessible(true);
            TextFieldWidget textFieldWidget = (TextFieldWidget) originalChatField.get(parent);
            this.originalMessage = textFieldWidget.getText();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        super.init();

        // Replace the chat field with custom preview
        this.chatField = new SearchPreviewWidget(this.client.advanceValidatingTextRenderer, 4, this.height - 12, this.width - 4, 12, this.originalMessage);

        // Initialize the bck-i-search text
        this.searchField = new TextFieldWidget(this.client.advanceValidatingTextRenderer, 4, this.height - 12 - 12, this.width - 4, 12, Text.translatable("chat.editBox"));
        this.searchField.setChangedListener(ignored -> {
            // Whenever the query is updated, redo search
            this.search();
        });
        this.searchField.setMaxLength(256);
        this.searchField.setDrawsBackground(false);
        this.searchField.setText("");
        this.searchField.setFocusUnlocked(false);
        this.addSelectableChild(this.searchField);
    }

    @Override
    protected void setInitialFocus() {
        // Set the initial focus to be on the search, instead of the chat field
        this.setInitialFocus(this.searchField);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // For maximum customization I stole Screen.keyPressed from the grandparent to avoid calling super.keyPressed()
        //region Screen.keyPressed (modified)
        if (this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        GuiNavigation guiNavigation = switch (keyCode) {
            case 258 -> {
                boolean bl = !hasShiftDown();
                yield new GuiNavigation.Tab(bl);
            }
            case 262 -> new GuiNavigation.Arrow(NavigationDirection.RIGHT);
            case 263 -> new GuiNavigation.Arrow(NavigationDirection.LEFT);
            case 264 -> new GuiNavigation.Arrow(NavigationDirection.DOWN);
            case 265 -> new GuiNavigation.Arrow(NavigationDirection.UP);
            default -> null;
        };

        if (guiNavigation != null) {
            GuiNavigationPath guiNavigationPath = super.getNavigationPath(guiNavigation);
            if (guiNavigationPath == null && guiNavigation instanceof GuiNavigation.Tab) {
                this.blur();
                guiNavigationPath = super.getNavigationPath(guiNavigation);
            }

            if (guiNavigationPath != null) {
                this.switchFocus(guiNavigationPath);
            }
        }
        //endregion

        String query = this.getQuery();
        boolean hasMatch = !(query == null || query.isEmpty() || currentMatch == null || currentMatch.isEmpty());

        if (FzfChatClient.keyBinding.matchesKey(keyCode, scanCode) && modifiers == GLFW.GLFW_MOD_CONTROL) {
            // If Ctrl+R is pressed

            // Since the SearchChatScreen is already open, pressing it again will skip to the next result
            this.skipCount++;
            // Recall search to update match
            this.search();

            return true;
        } else if (hasMatch) {
            // Match available

            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                // Type it out for the user, but do not send
                this.client.setScreen(new ChatScreen(this.currentMatch));
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                // Send the match
                super.sendMessage(currentMatch, true);
                // Close the screen
                this.client.setScreen(null);
                return true;
            } else {
                return false;
            }
        } else {
            // No match available

            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                // Cancel search
                this.client.setScreen(new ChatScreen(this.originalMessage));
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.searchField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Disable mouse clicking
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // Disable mouse scrolling
        return false;
    }

    /**
     * Perform a search operation. Queries and updates instance variables to keep track of skip count, current query,
     * current match, etc.
     */
    private void search() {
        String query = this.getQuery();

        if (query == null || query.isEmpty()) {
            this.chatField.setText("");
            return;
        }

        ChatHud chatHud = this.client.inGameHud.getChatHud();
        ArrayListDeque<String> history = chatHud.getMessageHistory();

        // If no match is found, currentMatch will be null
        currentMatch = null;

        int i = 0;
        HashSet<String> encounteredMessages = new HashSet<>();
        for (Iterator<String> it = history.descendingIterator(); it.hasNext(); i++) {
            String line = it.next();

            // Don't count duplicates
            if (!encounteredMessages.add(line)) {
                continue;
            }

            if (i < skipCount) {
                continue;
            }

            if (line.contains(query)) {
                currentMatch = line;
                break;
            }
        }

        this.chatField.setText(currentMatch);

        FzfChat.LOGGER.info("QUERY: {}, MATCH: {}", query, currentMatch);
    }

    /**
     * Gets the current query.
     *
     * @return the current query.
     */
    private String getQuery() {
        return this.searchField.getText();
    }

}
