package com.tidefall.tidecustomtools;

import com.tidefall.tidecustomtools.commands.TCustomCommand;
import com.tidefall.tidecustomtools.enchantments.GlitchEnchantment;
import com.tidefall.tidecustomtools.listeners.GlitchBlockListener;
import org.bukkit.plugin.java.JavaPlugin;

public class TideCustomTools extends JavaPlugin {

    private static TideCustomTools instance;
    private GlitchEnchantment glitchEnchantment;
    private GlitchBlockListener glitchBlockListener;

    @Override
    public void onEnable() {
        instance = this;

        glitchEnchantment = new GlitchEnchantment(this);

        glitchBlockListener = new GlitchBlockListener(this);
        getServer().getPluginManager().registerEvents(glitchBlockListener, this);

        getCommand("tcustom").setExecutor(new TCustomCommand(this));
        getCommand("tcustom").setTabCompleter(new TCustomCommand(this));

        getLogger().info("TideCustomTools enabled!");
    }

    @Override
    public void onDisable() {
        if (glitchBlockListener != null) {
            glitchBlockListener.cleanup();
        }
        getLogger().info("TideCustomTools disabled!");
    }

    public static TideCustomTools getInstance() {
        return instance;
    }

    public GlitchEnchantment getGlitchEnchantment() {
        return glitchEnchantment;
    }
}
