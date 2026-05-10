package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TrashCommand implements CommandExecutor, Listener {

    private static final Set<UUID> trashViewers = new HashSet<>();
    private static final int TRASH_SIZE = 54;

    public TrashCommand(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.trash")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        Component title = MiniMessage.miniMessage().deserialize(
                "<gray>[</gray><gradient:#67E8F9:#3B82F6><bold>Mülleimer</bold></gradient><gray>]</gray>"
        );

        Inventory trash = Bukkit.createInventory(null, TRASH_SIZE, title);

        // Deko: roten Rahmen
        ItemStack pane = createPane(Material.RED_STAINED_GLASS_PANE, "<#F87171>Alles hier wird beim Schließen gelöscht</color>");
        for (int i = 0; i < 9; i++) trash.setItem(i, pane);
        for (int i = 45; i < 54; i++) trash.setItem(i, pane);
        for (int i = 9; i < 45; i += 9) trash.setItem(i, pane);
        for (int i = 17; i < 54; i += 9) trash.setItem(i, pane);

        trashViewers.add(player.getUniqueId());
        player.openInventory(trash);
        player.sendMessage(MessageUtil.trashOpened());
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!trashViewers.contains(player.getUniqueId())) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;

        int slot = event.getSlot();
        // Rahmen-Slots blockieren
        if (isFrameSlot(slot)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!trashViewers.contains(player.getUniqueId())) return;

        // Alles im Trash-Inventar wird verworfen — einfach schließen reicht
        trashViewers.remove(player.getUniqueId());
        player.sendMessage(MessageUtil.trashCleared());
    }

    private boolean isFrameSlot(int slot) {
        if (slot < 9 || slot >= 45) return true;
        if (slot % 9 == 0 || slot % 9 == 8) return true;
        return false;
    }

    private ItemStack createPane(Material material, String name) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize(name));
        pane.setItemMeta(meta);
        return pane;
    }
}