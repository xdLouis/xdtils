package de.louis.xdtils.commands;

import de.louis.xdtils.manager.BanManager;
import de.louis.xdtils.manager.BanManager.HistoryEntry;
import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BanHistoryCommand implements CommandExecutor, TabCompleter {

    private final BanManager banManager;

    // GUI-Title-Prefix zum Erkennen beim Click-Event
    public static final String GUI_TITLE_PREFIX = "[xdTils] History:";

    public BanHistoryCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("xdtils.banhistory")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (!(sender instanceof Player player)) {
            if (args.length < 1) {
                sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: " + MessageUtil.command("banhistory") + "<gray> <spieler>"));
                return true;
            }
            printHistoryToConsole(sender, args[0]);
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: " + MessageUtil.command("banhistory") + "<gray> <spieler>"));
            return true;
        }

        openHistoryGui(player, args[0]);
        return true;
    }

    // ── GUI ───────────────────────────────────────────────────────────

    public void openHistoryGui(Player player, String targetName) {
        List<HistoryEntry> entries = banManager.getHistory(targetName);
        BanManager.BanEntry activeBan = banManager.getBan(targetName);

        // Größe: mind. 3 Reihen, max 6 Reihen
        int rows = Math.min(6, Math.max(3, 2 + (int) Math.ceil(entries.size() / 7.0)));
        int size = rows * 9;

        // Fester Title-String (plain) zum Erkennen im InventoryClickEvent
        String titlePlain = GUI_TITLE_PREFIX + " " + targetName;
        Inventory gui = Bukkit.createInventory(null, size,
            MessageUtil.parse("<gray>[</gray><gradient:#67E8F9:#3B82F6><bold>xdTils</bold></gradient><gray>]</gray> <gray>History: <#4DA3FF>" + targetName + "</#4DA3FF>"));

        // ── Rahmen: schwarze Glasscheiben ─────────────────────────────
        ItemStack border = makeFiller(Material.BLACK_STAINED_GLASS_PANE);
        // Obere Reihe
        for (int i = 0; i < 9; i++) gui.setItem(i, border);
        // Untere Reihe
        for (int i = size - 9; i < size; i++) gui.setItem(i, border);
        // Linke + rechte Spalte
        for (int row = 1; row < rows - 1; row++) {
            gui.setItem(row * 9, border);
            gui.setItem(row * 9 + 8, border);
        }
        // Innere leere Slots mit dunkelgrauem Glas auffüllen
        ItemStack innerFiller = makeFiller(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 1; i < size - 1; i++) {
            int col = i % 9;
            if (col == 0 || col == 8) continue;
            if (gui.getItem(i) == null) gui.setItem(i, innerFiller);
        }

        // ── Spieler-Kopf (Mitte oben, Slot 4) ────────────────────────
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.displayName(MessageUtil.parse("<#4DA3FF><bold>" + targetName + "</bold>"));
        List<Component> skullLore = new ArrayList<>();
        skullLore.add(MessageUtil.parse("<gray>Moderationshistorie</gray>"));
        skullLore.add(MessageUtil.parse("<dark_gray>» <gray>" + entries.size() + " Einträge"));
        if (activeBan != null) {
            skullLore.add(Component.empty());
            skullLore.add(MessageUtil.parse("<#F87171><bold>⚠ AKTUELL GEBANNT</bold></#F87171>"));
            skullLore.add(MessageUtil.parse("<gray>Grund: <white>" + activeBan.reason() + "</white>"));
            skullLore.add(MessageUtil.parse("<gray>Von: <#4DA3FF>" + activeBan.bannedBy() + "</#4DA3FF>"));
            if (activeBan.isTemp()) {
                skullLore.add(MessageUtil.parse("<gray>Verbleibend: <#FCD34D>" + BanManager.formatDuration(activeBan.remainingMs()) + "</#FCD34D>"));
            } else {
                skullLore.add(MessageUtil.parse("<gray>Dauer: <#F87171>Permanent</#F87171>"));
            }
        }
        skullLore.add(Component.empty());
        skullMeta.lore(skullLore);
        skull.setItemMeta(skullMeta);
        gui.setItem(4, skull);

        // ── Einträge (neueste zuerst, ab Reihe 1 Spalten 1-7) ─────────
        List<HistoryEntry> reversed = new ArrayList<>(entries);
        java.util.Collections.reverse(reversed);

        // Verfügbare Slots: Reihen 1..rows-2, Spalten 1..7
        List<Integer> contentSlots = new ArrayList<>();
        for (int row = 1; row < rows - 1; row++) {
            if (row == 0) continue; // Kopfzeile
            for (int col = 1; col <= 7; col++) {
                int slot = row * 9 + col;
                if (slot == 4) continue; // Skull-Slot überspringen
                contentSlots.add(slot);
            }
        }

        int idx = 0;
        for (HistoryEntry entry : reversed) {
            if (idx >= contentSlots.size()) break;
            gui.setItem(contentSlots.get(idx++), buildEntryItem(entry));
        }

        // ── Keine Einträge ────────────────────────────────────────────
        if (entries.isEmpty()) {
            ItemStack empty = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta emptyMeta = empty.getItemMeta();
            emptyMeta.displayName(MessageUtil.parse("<#86EFAC><bold>Saubere Akte</bold></#86EFAC>"));
            emptyMeta.lore(List.of(MessageUtil.parse("<gray>Dieser Spieler hat keine Einträge.</gray>")));
            empty.setItemMeta(emptyMeta);
            // Mittelslot der mittleren Reihe
            gui.setItem(rows / 2 * 9 + 4, empty);
        }

        // ── Schließen-Button (unten rechts, vorletzter Slot) ──────────
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.displayName(MessageUtil.parse("<#F87171><bold>✖ Schließen</bold></#F87171>"));
        closeMeta.lore(List.of(MessageUtil.parse("<gray>Klicke zum Schließen</gray>")));
        close.setItemMeta(closeMeta);
        gui.setItem(size - 2, close);

        player.openInventory(gui);
        player.sendMessage(MessageUtil.banHistoryOpened(targetName));
    }

    private ItemStack makeFiller(Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.empty());
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildEntryItem(HistoryEntry entry) {
        Material mat = switch (entry.type()) {
            case "BAN"      -> Material.RED_CONCRETE;
            case "TEMPBAN"  -> Material.ORANGE_CONCRETE;
            case "KICK"     -> Material.YELLOW_CONCRETE;
            case "UNBAN"    -> Material.LIME_CONCRETE;
            case "MUTE"     -> Material.PURPLE_CONCRETE;
            case "TEMPMUTE" -> Material.MAGENTA_CONCRETE;
            case "UNMUTE"   -> Material.CYAN_CONCRETE;
            default         -> Material.GRAY_CONCRETE;
        };

        String typeColor = switch (entry.type()) {
            case "BAN"      -> "<#F87171>";
            case "TEMPBAN"  -> "<#FCD34D>";
            case "KICK"     -> "<#FBBF24>";
            case "UNBAN"    -> "<#86EFAC>";
            case "MUTE"     -> "<#C084FC>";
            case "TEMPMUTE" -> "<#E879F9>";
            case "UNMUTE"   -> "<#67E8F9>";
            default         -> "<gray>";
        };
        String typeEnd = typeColor.replace("<", "</");

        String typeLabel = switch (entry.type()) {
            case "BAN"      -> "⛔ BAN";
            case "TEMPBAN"  -> "⏳ TEMPBAN";
            case "KICK"     -> "👢 KICK";
            case "UNBAN"    -> "✅ UNBAN";
            case "MUTE"     -> "🔇 MUTE";
            case "TEMPMUTE" -> "⏱ TEMPMUTE";
            case "UNMUTE"   -> "🔊 UNMUTE";
            default         -> entry.type();
        };

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(typeColor + "<bold>" + typeLabel + "</bold>" + typeEnd));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(MessageUtil.parse("<gray>Datum: <white>" + BanManager.formatTimestamp(entry.at()) + "</white>"));
        lore.add(MessageUtil.parse("<gray>Von: <#4DA3FF>" + entry.by() + "</#4DA3FF>"));
        lore.add(MessageUtil.parse("<gray>Grund: <white>" + entry.reason() + "</white>"));
        if (entry.expires() > 0) {
            lore.add(MessageUtil.parse("<gray>Läuft ab: <#FCD34D>" + BanManager.formatTimestamp(entry.expires()) + "</#FCD34D>"));
        }
        lore.add(Component.empty());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void printHistoryToConsole(CommandSender sender, String targetName) {
        List<HistoryEntry> entries = banManager.getHistory(targetName);
        sender.sendMessage(MessageUtil.prefixed("<gray>History von " + MessageUtil.player(targetName) + "<gray>:"));
        if (entries.isEmpty()) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Keine Einträge gefunden."));
            return;
        }
        for (HistoryEntry entry : entries) {
            sender.sendMessage(MessageUtil.parse(
                "  <dark_gray>» <gray>[<white>" + entry.type() + "</white>] "
                + BanManager.formatTimestamp(entry.at())
                + " <gray>von <#4DA3FF>" + entry.by() + "</#4DA3FF>"
                + "<gray> — <white>" + entry.reason() + "</white>"));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        return List.of();
    }
}
