package net.l1nkstryk3r.mq.util;

import net.l1nkstryk3r.mq.L1nksMasterQuest;
import net.minecraft.resources.ResourceLocation;

/**
 * Helper class with utilities needed throughout the project
 */
public class ModUtils {
    /**
     * Retrieves the ResourceLocation for a given name
     *
     * @param name The name to retrieve the ResourceLocation for
     * @return The ResourceLocation for that name
     */
    public static ResourceLocation id(String name) {
        return new ResourceLocation(L1nksMasterQuest.MOD_ID, name);
    }
}
