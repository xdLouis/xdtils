package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ArmorTrimCommand implements CommandExecutor {

    private static final List<TrimPattern> PATTERNS = new ArrayList<>();
    private static final List<TrimMaterial> MATERIALS = new ArrayList<>();

    static {
        Registry.TRIM_PATTERN.forEach(PATTERNS::add);
        Registry.TRIM_MATERIAL.forEach(MATERIALS::add);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.armortrim")) {
            player.sendMessage(MessageUtil.noPermission("armortrim"));
            return true;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (!isArmor(held.getType())) {
            player.sendMessage(MessageUtil.armorTrimNoArmor());
            return true;
        }

        openTrimGui(player, held);
        return true;
    }

    private void openTrimGui(Player player, ItemStack armor) {
        int rows = (int) Math.ceil(PATTERNS.size() / 9.0) + 1;
        rows = Math.max(rows, 2);
        if (rows > 6) rows = 6;

        Inventory gui = player.getServer().createInventory(
                null,
                rows * 9,
                net.kyori.adventure.text.Component.text()
                        .append(MessageUtil.parse("<gray>[</gray><gradient:#67E8F9:#3B82F6><bold>xdTils</bold></gradient><gray>]</gray> <gray>Armor Trim"))
                        .build()
        );

        int slot = 0;
        for (TrimPattern pattern : PATTERNS) {
            if (slot >= rows * 9 - 9) break;

            for (TrimMaterial mat : MATERIALS) {
                if (slot >= rows * 9 - 9) break;

                ItemStack display = buildTrimPreview(armor.clone(), pattern, mat);
                gui.setItem(slot, display);
                slot++;
                break; // one item per pattern using first material as preview
            }
        }

        // Bottom row: fill pattern slot for each material
        int bottomStart = (rows - 1) * 9;
        int matSlot = bottomStart;
        for (TrimMaterial mat : MATERIALS) {
            if (matSlot >= rows * 9) break;
            ItemStack matDisplay = buildTrimPreview(armor.clone(), PATTERNS.get(0), mat);
            var meta = matDisplay.getItemMeta();
            if (meta != null) {
                String matName = mat.key().value().replace("_", " ");
                matName = Character.toUpperCase(matName.charAt(0)) + matName.substring(1);
                meta.displayName(MessageUtil.parse("<gradient:#FCD34D:#F59E0B><bold>Material: " + matName + "</bold></gradient>"));
                List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
                lore.add(MessageUtil.parse("<gray>Click to select this material"));
                meta.lore(lore);
                matDisplay.setItemMeta(meta);
            }
            gui.setItem(matSlot, matDisplay);
            matSlot++;
        }

        player.openInventory(gui);
        player.sendMessage(MessageUtil.armorTrimGuiOpened());
    }

    private ItemStack buildTrimPreview(ItemStack armor, TrimPattern pattern, TrimMaterial material) {
        ArmorMeta meta = (ArmorMeta) armor.getItemMeta();
        if (meta == null) return armor;

        ArmorTrim trim = new ArmorTrim(material, pattern);
        meta.setTrim(trim);

        String patternName = pattern.key().value().replace("_", " ");
        patternName = Character.toUpperCase(patternName.charAt(0)) + patternName.substring(1);
        String matName = material.key().value().replace("_", " ");
        matName = Character.toUpperCase(matName.charAt(0)) + matName.substring(1);

        meta.displayName(MessageUtil.parse(
                "<gradient:#C084FC:#818CF8><bold>" + patternName + "</bold></gradient>"));

        List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Material: <#FCD34D>" + matName + "</#FCD34D>"));
        lore.add(MessageUtil.parse("<gray>Left-click to apply to held armor"));
        meta.lore(lore);

        armor.setItemMeta(meta);
        return armor;
    }

    private boolean isArmor(Material mat) {
        if (mat == null) return false;
        String name = mat.name();
        return name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE")
                || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS");
    }
}
