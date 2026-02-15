package com.tidefall.tidecustomtools.commands;

import com.tidefall.tidecustomtools.TideCustomTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TCustomCommand implements CommandExecutor, TabCompleter {

    private final TideCustomTools plugin;

    public TCustomCommand(TideCustomTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /tcustom enchant glitch", NamedTextColor.RED));
            return true;
        }

        if (!args[0].equalsIgnoreCase("enchant")) {
            player.sendMessage(Component.text("Usage: /tcustom enchant glitch", NamedTextColor.RED));
            return true;
        }

        if (!args[1].equalsIgnoreCase("glitch")) {
            player.sendMessage(Component.text("Unknown enchantment: " + args[1], NamedTextColor.RED));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(Component.text("You must be holding an item!", NamedTextColor.RED));
            return true;
        }

        if (plugin.getGlitchEnchantment().hasEnchantment(item)) {
            player.sendMessage(Component.text("This item already has the Glitch enchantment!", NamedTextColor.GOLD));
            return true;
        }

        plugin.getGlitchEnchantment().applyEnchantment(item);

        player.sendMessage(Component.empty()
                .append(Component.text("Applied ", NamedTextColor.GREEN))
                .append(Component.text("G", NamedTextColor.GREEN))
                .append(Component.text("l", NamedTextColor.GOLD))
                .append(Component.text("i", NamedTextColor.GREEN))
                .append(Component.text("t", NamedTextColor.GOLD))
                .append(Component.text("c", NamedTextColor.GREEN))
                .append(Component.text("h", NamedTextColor.GOLD))
                .append(Component.text(" V", NamedTextColor.GREEN))
                .append(Component.text(" to your item!", NamedTextColor.GREEN)));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("enchant".startsWith(args[0].toLowerCase())) {
                completions.add("enchant");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("enchant")) {
            if ("glitch".startsWith(args[1].toLowerCase())) {
                completions.add("glitch");
            }
        }
        return completions;
    }
}
