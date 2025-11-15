package net.l1nkstryk3r.mq.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.l1nkstryk3r.mq.L1nksMasterQuest;
import net.l1nkstryk3r.mq.util.ModUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;

/**
 * Registers and organizes all custom items in the mod.
 * <p>
 *     Fabric (and modern Minecraft in general) requires each item
 *     to be explicitly registered in the item registry with a unique ID
 * </p>
 * <p>
 *     This file handles registration, creative tab organization, and
 *     defines the custom Master Sword item
 * </p>
 */
public class ModItems {

    /**
     * Registers the Master Sword item.
     * <p></p>
     * Constructor Parameters:
     * <ul>
     *     <li>Tiers.NETHERITE -> defines the material tier (for durability, mining level, etc.)</li>
     *     <li>5 -> attack damage bonus</li>
     *     <li>-2.0f -> attack speed modifier</li>
     *     <li>new Item.Properties() -> configures general properties (durability, rarity, etc.)</li>
     * </ul>
     */
    public static final Item MASTER_SWORD = register(
        new MasterSword(Tiers.NETHERITE, 5, -2.0f, new Item.Properties()),
        "master_sword"
    );

    /**
     * Adds our custom items to the correct Creative Tabs (Item Groups)
     * <p>
     *     This ensures players can find the new items under familiar categories.
     * </p>
     */
    public static void initialize() {
        L1nksMasterQuest.LOGGER.info("Initializing ModItems for {}", L1nksMasterQuest.MOD_ID);
        // add our newly registered items to the appropriate creative mode tabs
        addToCreativeTab(CreativeModeTabs.COMBAT, Items.NETHERITE_SWORD, MASTER_SWORD);
    }

    /**
     * Helper method that registers an item and returns it.
     * <p>
     *     Using a shared register() method avoids repeating the same boilerplate code
     * </p>
     * @param item the item instance being registered
     * @param id the unique identifier (path portion of "modid:id"
     * @return the same item instance for direct reference
     */
    private static Item register(Item item, String id) {
        L1nksMasterQuest.LOGGER.info("Registering item: {}", id);
        return Registry.register(
            BuiltInRegistries.ITEM,
            ModUtils.id(id),
            item
        );
    }

    /**
     * Helper method to add an item to a creative mode tab
     * <p>
     *     Using a shared method avoids repeating the same boilerplate code
     * </p>
     * <p>
     *     Fabric provides ItemGroupEvents for dynamic tab modification.
     * </p>
     * @param tab The CreativeModeTab to assign the item to
     * @param afterItem The item that our new item will be added after
     * @param newItem Our new item
     */
    private static void addToCreativeTab(ResourceKey<CreativeModeTab> tab, Item afterItem, Item newItem) {
        ItemGroupEvents.modifyEntriesEvent(tab)
            .register(entries -> entries.addAfter(afterItem, newItem));
    }
}
