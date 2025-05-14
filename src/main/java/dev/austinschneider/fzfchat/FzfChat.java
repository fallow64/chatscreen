package dev.austinschneider.fzfchat;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FzfChat implements ModInitializer {
	public static final String MOD_ID = "fzfchat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("FzfChat Initialized");
	}
}