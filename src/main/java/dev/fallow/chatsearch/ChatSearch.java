package dev.fallow.chatsearch;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatSearch implements ModInitializer {
	public static final String MOD_ID = "chatsearch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("ChatSearch Initialized");
	}
}