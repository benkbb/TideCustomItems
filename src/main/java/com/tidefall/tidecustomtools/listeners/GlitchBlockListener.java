package com.tidefall.tidecustomtools.listeners;

import com.tidefall.tidecustomtools.TideCustomTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class GlitchBlockListener implements Listener {

    private final TideCustomTools plugin;

    private static final int GLITCH_RADIUS = 5;
    private static final int BLOCKS_PER_TRIGGER = 7;
    private static final int MAX_ACTIVE_GLITCHES = 60;
    private static final long COOLDOWN_MS = 75;
    private static final int GLITCH_DURATION_TICKS = 20;

    private static final Material[] GLITCH_MATERIALS = {
            Material.PURPLE_CONCRETE,
            Material.LIME_CONCRETE,
            Material.YELLOW_CONCRETE
    };

    private BlockData[] glitchBlockData;

    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> activeGlitchCounts = new ConcurrentHashMap<>();

    public GlitchBlockListener(TideCustomTools plugin) {
        this.plugin = plugin;
        glitchBlockData = new BlockData[GLITCH_MATERIALS.length];
        for (int i = 0; i < GLITCH_MATERIALS.length; i++) {
            glitchBlockData[i] = Bukkit.createBlockData(GLITCH_MATERIALS[i]);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!plugin.getGlitchEnchantment().hasEnchantment(tool)) return;

        long now = System.currentTimeMillis();
        Long lastUse = cooldowns.get(player.getUniqueId());
        if (lastUse != null && (now - lastUse) < COOLDOWN_MS) return;
        cooldowns.put(player.getUniqueId(), now);

        int currentActive = activeGlitchCounts.getOrDefault(player.getUniqueId(), 0);
        if (currentActive >= MAX_ACTIVE_GLITCHES) return;

        Block broken = event.getBlock();
        Location center = broken.getLocation();

        List<Block> candidates = collectSolidBlocks(center);
        if (candidates.isEmpty()) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int count = Math.min(BLOCKS_PER_TRIGGER, candidates.size());

        List<Block> chosen = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int idx = random.nextInt(candidates.size());
            Block b = candidates.remove(idx);
            if (b.getLocation().equals(center)) continue;
            chosen.add(b);
        }

        if (chosen.isEmpty()) return;

        activeGlitchCounts.merge(player.getUniqueId(), chosen.size(), Integer::sum);

        for (Block block : chosen) {
            BlockData disguise = glitchBlockData[random.nextInt(glitchBlockData.length)];
            player.sendBlockChange(block.getLocation(), disguise);
        }

        int restoreTicks = GLITCH_DURATION_TICKS + random.nextInt(5);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                activeGlitchCounts.remove(player.getUniqueId());
                return;
            }
            for (Block block : chosen) {
                player.sendBlockChange(block.getLocation(), block.getBlockData());
            }
            activeGlitchCounts.merge(player.getUniqueId(), -chosen.size(), Integer::sum);
        }, restoreTicks);
    }

    private List<Block> collectSolidBlocks(Location center) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Block> result = new ArrayList<>();
        org.bukkit.World world = center.getWorld();

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        for (int i = 0; i < 30; i++) {
            int x = cx + random.nextInt(-GLITCH_RADIUS, GLITCH_RADIUS + 1);
            int y = cy + random.nextInt(-GLITCH_RADIUS, GLITCH_RADIUS + 1);
            int z = cz + random.nextInt(-GLITCH_RADIUS, GLITCH_RADIUS + 1);

            if (y < world.getMinHeight() || y > world.getMaxHeight()) continue;

            Block b = world.getBlockAt(x, y, z);
            if (b.getType().isSolid() && b.getType().isBlock() && !b.getType().isAir()) {
                result.add(b);
            }
        }

        return result;
    }

    public void cleanup() {
        cooldowns.clear();
        activeGlitchCounts.clear();
    }
}
