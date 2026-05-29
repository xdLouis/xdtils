package de.louis.xdtils.listener;

import de.louis.xdtils.manager.permissions.PermissionGroup;
import de.louis.xdtils.manager.permissions.PermissionMenu;
import de.louis.xdtils.manager.permissions.PermissionMenuHolder;
import de.louis.xdtils.manager.permissions.PermissionSystemManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PermissionMenuListener implements Listener {

    private final PermissionSystemManager manager;
    private final PermissionMenu menu;

    public PermissionMenuListener(PermissionSystemManager manager) {
        this.manager = manager;
        this.menu = new PermissionMenu(manager);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof PermissionMenuHolder holder)) return;

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        switch (holder.getType()) {
            case MAIN -> handleMain(player, item);
            case GROUPS -> handleGroups(player, item);
            case PLAYERS -> handlePlayers(player, item);
            case GROUP_DETAIL -> handleGroupDetail(player, item, holder);
            case PLAYER_DETAIL -> handlePlayerDetail(player, item, holder);
        }
    }

    private void handleMain(Player player, ItemStack item) {
        switch (item.getType()) {
            case CHISELED_BOOKSHELF -> menu.openGroups(player);
            case RECOVERY_COMPASS -> menu.openPlayers(player);
            case BARRIER -> player.closeInventory();
            default -> {
            }
        }
    }

    private void handleGroups(Player player, ItemStack item) {
        switch (item.getType()) {
            case TOTEM_OF_UNDYING -> {
                String groupName = plainName(item);
                PermissionGroup group = manager.getSortedGroups().stream()
                        .filter(g -> strip(g.getDisplayName()).equalsIgnoreCase(groupName) || g.getName().equalsIgnoreCase(groupName))
                        .findFirst()
                        .orElse(null);

                if (group != null) {
                    menu.openGroupDetail(player, group.getName());
                }
            }
            case ARROW -> menu.openMain(player);
            case BARRIER -> player.closeInventory();
            case ANVIL -> player.sendMessage(MessageUtil.prefixed("<gray>Nutze aktuell " + MessageUtil.command("permissions group create <name>") + "<gray>.</gray>"));
            default -> {
            }
        }
    }

    private void handlePlayers(Player player, ItemStack item) {
        switch (item.getType()) {
            case PLAYER_HEAD -> {
                String playerName = plainName(item);
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                if (target.getUniqueId() != null) {
                    menu.openPlayerDetail(player, target.getUniqueId());
                }
            }
            case ARROW -> menu.openMain(player);
            case BARRIER -> player.closeInventory();
            default -> {
            }
        }
    }

    private void handleGroupDetail(Player player, ItemStack item, PermissionMenuHolder holder) {
        switch (item.getType()) {
            case ARROW -> menu.openGroups(player);
            case BARRIER -> player.closeInventory();
            case ANVIL -> player.sendMessage(MessageUtil.prefixed("<gray>Bearbeite die Gruppe aktuell per Command.</gray>"));
            default -> {
            }
        }
    }

    private void handlePlayerDetail(Player player, ItemStack item, PermissionMenuHolder holder) {
        switch (item.getType()) {
            case ARROW -> menu.openPlayers(player);
            case BARRIER -> player.closeInventory();
            case ANVIL -> player.sendMessage(MessageUtil.prefixed("<gray>Bearbeite den Spieler aktuell per Command.</gray>"));
            default -> {
            }
        }
    }

    private String plainName(ItemStack item) {
        if (!item.hasItemMeta() || item.getItemMeta().displayName() == null) {
            return "";
        }

        return strip(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(item.getItemMeta().displayName()));
    }

    private String strip(String input) {
        return input.replace("§8", "")
                .replace("§7", "")
                .replace("§f", "")
                .trim();
    }
}