package com.example.vaultsnipe;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Client-side only. Each tick, if you are looking at an ominous vault while
 * holding an ominous trial key, and the vault's displayed item has just
 * become a heavy core, send the normal "use item on block" interaction.
 */
public class VaultSnipeClient implements ClientModInitializer {

    /** Was the heavy core being displayed on the previous tick? (edge detection) */
    private boolean heavyCoreShownLastTick = false;
    private BlockPos lastVaultPos = null;

    @Override
    public void onInitializeClient() {
        // START tick: runs after incoming packets were applied, before the
        // game processes this tick's input, so we act on the freshest state.
        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || mc.gameMode == null || mc.screen != null) {
            reset();
            return;
        }

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
            reset();
            return;
        }

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        if (!state.is(Blocks.VAULT)) {
            reset();
            return;
        }

        // Must hold an ominous trial key in the main hand.
        if (!player.getMainHandItem().is(Items.OMINOUS_TRIAL_KEY)) {
            reset();
            return;
        }

        BlockEntity be = mc.level.getBlockEntity(pos);
        if (!(be instanceof VaultBlockEntity vault)) {
            reset();
            return;
        }

        ItemStack displayed = vault.getSharedData().getDisplayItem();
        boolean heavyCoreNow = displayed.is(Items.HEAVY_CORE);

        // Fire only on the tick it appears (rising edge), or when we just
        // started looking at a vault that is already showing it.
        boolean newTarget = !pos.equals(lastVaultPos);
        boolean fire = heavyCoreNow && (!heavyCoreShownLastTick || newTarget);

        heavyCoreShownLastTick = heavyCoreNow;
        lastVaultPos = pos;

        if (fire) {
            mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, blockHit);
            player.swing(InteractionHand.MAIN_HAND);
        }
    }

    private void reset() {
        heavyCoreShownLastTick = false;
        lastVaultPos = null;
    }
}
