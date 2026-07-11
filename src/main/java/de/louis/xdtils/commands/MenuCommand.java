package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuCommand implements CommandExecutor, Listener {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final String TITLE = "xdtils Menu";

    public MenuCommand(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.menu")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        openMenu(player);
        return true;
    }

    private void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
                MM.deserialize("<dark_gray>✦ </#dark_gray><#4DA3FF><b>xdtils Menu</b></#4DA3FF><dark_gray> ✦</dark_gray>"));

        // Füllung
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, "<dark_gray> </dark_gray>", List.of());
        for (int i = 0; i < 54; i++) inv.setItem(i, filler);

        // Kategorie-Items
        inv.setItem(10, createItem(Material.DIAMOND_SWORD,     "<#4DA3FF><b>Gameplay</b></#4DA3FF>",
                List.of("<gray>/fly, /god, /heal, /feed</gray>")));
        inv.setItem(12, createItem(Material.ENDER_PEARL,       "<#86EFAC><b>Teleport</b></#86EFAC>",
                List.of("<gray>/tp, /tphere, /tpworld</gray>")));
        inv.setItem(14, createItem(Material.BOOK,              "<#FCD34D><b>Info</b></#FCD34D>",
                List.of("<gray>/info, /ping, /playtime</gray>")));
        inv.setItem(16, createItem(Material.IRON_SWORD,        "<#F87171><b>Moderation</b></#F87171>",
                List.of("<gray>/kick, /ban, /mute, /freeze</gray>")));
        inv.setItem(28, createItem(Material.CHEST,             "<#67E8F9><b>Inventar</b></#67E8F9>",
                List.of("<gray>/invsee, /invsave, /invload</gray>")));
        inv.setItem(30, createItem(Material.WRITABLE_BOOK,     "<#C084FC><b>Chat</b></#C084FC>",
                List.of("<gray>/msg, /broadcast, /mute</gray>")));
        inv.setItem(32, createItem(Material.NETHER_STAR,       "<#F59E0B><b>Server</b></#F59E0B>",
                List.of("<gray>/tps, /memory, /restart</gray>")));
        inv.setItem(34, createItem(Material.FIREWORK_ROCKET,   "<#86EFAC><b>Fun</b></#86EFAC>",
                List.of("<gray>/firework, /skull, /trail</gray>")));

        // Spieler-Head unten mittig
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        var headMeta = (org.bukkit.inventory.meta.SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(player);
        headMeta.displayName(MM.deserialize("<#4DA3FF><b>" + player.getName() + "</b></#4DA3FF>"));
        headMeta.lore(List.of(
                MM.deserialize("<gray>Ping: <#67E8F9>" + player.getPing() + "ms</#67E8F9>"),
                MM.deserialize("<gray>Welt: <#67E8F9>" + player.getWorld().getName() + "</#67E8F9>"),
                MM.deserialize("<gray>GM: <#67E8F9>" + player.getGameMode().name() + "</#67E8F9>")
        ));
        head.setItemMeta(headMeta);
        inv.setItem(49, head);

        player.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name, List<String> loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MM.deserialize(name));
        meta.lore(loreLines.stream().map(MM::deserialize).toList());
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().title().equals(MM.deserialize(
                "<dark_gray>✦ </#dark_gray><#4DA3FF><b>xdtils Menu</b></#4DA3FF><dark_gray> ✦</dark_gray>")))
            event.setCancelled(true);
    }
}