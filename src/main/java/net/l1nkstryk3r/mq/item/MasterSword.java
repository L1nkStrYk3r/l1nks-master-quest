package net.l1nkstryk3r.mq.item;

import net.l1nkstryk3r.mq.entity.MasterSwordBeamEntity;
import net.l1nkstryk3r.mq.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * The core item class for the Master Sword.
 * <p>
 *     Extends the built-in SwordItem to provide:
 *     <ul>
 *         <li>Custom durability behavior (unbreakable)</li>
 *         <li>Custom projectile firing (beam attack when at full health)</li>
 *     </ul>
 * </p>
 */
public class MasterSword extends SwordItem {
    /**
     * Standard sword constructor.
     * <p></p>
     * @param tier Defines the sword's base stats (like NETHERITE)
     * @param attackDamage Additional damage modifier
     * @param attackSpeed Attack speed modifier
     * @param properties General item configuration
     */
    public MasterSword(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    /** Makes the sword unbreakable (infinite durability). */
    @Override
    public boolean canBeDepleted() {
        return false;
    }

    /** Prevents damage to the item when hitting enemies */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return false;
    }

    /** Prevents durability loss when breaking blocks. */
    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
        return false;
    }

    /**
     * Called when the player right-clicks with the sword.
     * <p>
     *     Fires a beam projectile if the player is near full health.
     * </p>
     */
    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // only execute on the server side to avoid spawning duplicates
        if (!world.isClientSide) {
            // player must be almost or fully healed
            // NOTE: this value was adjusted so that it still fires when you appear to be at max health (in the hotbar)
            //       despite maybe not being at exactly your max health.
            boolean atFullHealth = player.getHealth() >= (player.getMaxHealth() - 1.0f);
            if (atFullHealth) {
                // create the beam entity and shoot it forward
                MasterSwordBeamEntity beam = new MasterSwordBeamEntity(ModEntities.MASTER_SWORD_BEAM, world);
                beam.setOwner(player);
                beam.moveTo(player.getX(), player.getEyeY() - 0.1, player.getZ());
                beam.setNoGravity(true);
                beam.shootFromRotation(
                    player,
                    player.getXRot(),
                    player.getYRot(),
                    0.0F,   // pitch offset
                    0.75F,  // velocity
                    0.0F    // inaccuracy (0 = perfectly straight)
                );

                // add entity to the world and play sound
                world.addFreshEntity(beam);
                world.playSound(
                    null,                           // the Player to exclude, null plays for everyone nearby
                    player.blockPosition(),         // origin point of the sound (e.g. the player's position)
                    SoundEvents.ARROW_SHOOT,        // The sound file to play
                    SoundSource.PLAYERS,            // Determines the category or "channel" of the sound
                    0.8F,                           // Sound Volume (1.0f Normal)
                    1.6F                            // Sound Pitch (1.0F normal)
                );

                // add a short cooldown (prevents spamming)
                player.getCooldowns().addCooldown(this, 20);
            }
        }

        // return success so the animation and cooldown apply correctly
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
    }
}
