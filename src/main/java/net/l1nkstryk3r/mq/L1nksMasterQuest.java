package net.l1nkstryk3r.mq;

import net.fabricmc.api.ModInitializer;
import net.l1nkstryk3r.mq.entity.ModEntities;
import net.l1nkstryk3r.mq.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L1nksMasterQuest implements ModInitializer {
	public static final String MOD_ID = "l1nks-master-quest";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        LOGGER.info("Initializing mod: {}", MOD_ID);

        ModItems.initialize();
        ModEntities.initialize();
	}
}