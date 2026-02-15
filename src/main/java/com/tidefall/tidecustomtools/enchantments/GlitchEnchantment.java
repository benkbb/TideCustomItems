package com.tidefall.tidecustomtools.enchantments;

import com.tidefall.tidecustomtools.TideCustomTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class GlitchEnchantment {

    private final NamespacedKey enchantKey;

    public GlitchEnchantment(TideCustomTools plugin) {
        this.enchantKey = new NamespacedKey(plugin, "glitch_enchant");
    }

    public NamespacedKey getKey() {
        return enchantKey;
    }

    public boolean hasEnchantment(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(enchantKey, PersistentDataType.BYTE);
    }

    public void applyEnchantment(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(enchantKey, PersistentDataType.BYTE, (byte) 1);

        List<Component> lore = meta.hasLore() ? new ArrayList<>(meta.lore()) : new ArrayList<>();

        lore.removeIf(line -> {
            String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(line);
            return plain.contains("Glitch");
        });

        Component glitchLore = Component.empty()
                .append(Component.text("G", NamedTextColor.GREEN))
                .append(Component.text("l", NamedTextColor.GOLD))
                .append(Component.text("i", NamedTextColor.GREEN))
                .append(Component.text("t", NamedTextColor.GOLD))
                .append(Component.text("c", NamedTextColor.GREEN))
                .append(Component.text("h", NamedTextColor.GOLD))
                .append(Component.text(" ", NamedTextColor.GREEN))
                .append(Component.text("V", NamedTextColor.GREEN))
                .decoration(TextDecoration.ITALIC, false);

        lore.add(0, glitchLore);
        meta.lore(lore);

        item.setItemMeta(meta);
    }

    public void removeEnchantment(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(enchantKey);

        if (meta.hasLore()) {
            List<Component> lore = new ArrayList<>(meta.lore());
            lore.removeIf(line -> {
                String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(line);
                return plain.contains("Glitch");
            });
            meta.lore(lore.isEmpty() ? null : lore);
        }

        item.setItemMeta(meta);
    }
}
