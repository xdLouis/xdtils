package de.louis.xdtils.manager.permissions;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionMenu {

    private final PermissionSystemManager manager;

    public PermissionMenu(PermissionSystemManager manager) {
        this.manager = manager;
    }

    public void openMain(Player player) {
        PermissionMenuHolder holder = new PermissionMenuHolder(PermissionMenuType.MAIN, null, 54, "§8Permissions");
        Inventory inv = holder.getInventory();

        fill(inv);

        inv.setItem(20, createItem(Material.CHISELED_BOOKSHELF, "<gradient:#67E8F9:#3B82F6><bold>Gruppen</bold></gradient>",
                "<gray>Alle Gruppen ansehen</gray>",
                "<gray>und bearbeiten</gray>"));

        inv.setItem(24, createItem(Material.RECOVERY_COMPASS, "<gradient:#67E8F9:#3B82F6><bold>Spieler</bold></gradient>",
                "<gray>Spielerrechte ansehen</gray>",
                "<gray>und Gruppen verwalten</gray>"));

        inv.setItem(49, createItem(Material.BARRIER, "<#F87171><bold>Schließen</bold></#F87171>",
                "<gray>Menü schließen</gray>"));

        player.openInventory(inv);
    }

    public void openGroups(Player player) {
        PermissionMenuHolder holder = new PermissionMenuHolder(PermissionMenuType.GROUPS, null, 54, "§8Permission Gruppen");
        Inventory inv = holder.getInventory();

        fill(inv);

        List<PermissionGroup> groups = manager.getSortedGroups();
        int slot = 10;

        for (PermissionGroup group : groups) {
            if (slot >= 44) break;

            inv.setItem(slot, createItem(Material.TOTEM_OF_UNDYING,
                    "<gradient:#67E8F9:#3B82F6><bold>" + escape(group.getDisplayName()) + "</bold></gradient>",
                    "<gray>Name: <#4DA3FF>" + escape(group.getName()) + "</#4DA3FF>",
                    "<gray>Priorität: <#4DA3FF>" + group.getPriority() + "</#4DA3FF>",
                    "<gray>Permissions: <#4DA3FF>" + group.getPermissions().size() + "</#4DA3FF>",
                    "<gray>Vererbungen: <#4DA3FF>" + group.getInheritedGroups().size() + "</#4DA3FF>",
                    "",
                    "<#86EFAC>Klicke zum Öffnen</#86EFAC>"));

            slot = nextSlot(slot);
        }

        inv.setItem(45, createItem(Material.ARROW, "<gradient:#67E8F9:#3B82F6><bold>Zurück</bold></gradient>",
                "<gray>Zur Hauptübersicht</gray>"));

        inv.setItem(49, createItem(Material.ANVIL, "<gradient:#67E8F9:#3B82F6><bold>Gruppe erstellen</bold></gradient>",
                "<gray>Aktuell per Command:</gray>",
                "<#86EFAC>/permissions group create <name></#86EFAC>"));

        inv.setItem(53, createItem(Material.BARRIER, "<#F87171><bold>Schließen</bold></#F87171>",
                "<gray>Menü schließen</gray>"));

        player.openInventory(inv);
    }

    public void openPlayers(Player player) {
        PermissionMenuHolder holder = new PermissionMenuHolder(PermissionMenuType.PLAYERS, null, 54, "§8Permission Spieler");
        Inventory inv = holder.getInventory();

        fill(inv);

        int slot = 10;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (slot >= 44) break;

            PermissionUserData data = manager.getUser(online.getUniqueId());

            inv.setItem(slot, createPlayerItem(online,
                    "<gradient:#67E8F9:#3B82F6><bold>" + escape(online.getName()) + "</bold></gradient>",
                    "<gray>Gruppen: <#4DA3FF>" + data.getGroups().size() + "</#4DA3FF>",
                    "<gray>Direkte Permissions: <#4DA3FF>" + data.getPermissions().size() + "</#4DA3FF>",
                    "",
                    "<#86EFAC>Klicke zum Öffnen</#86EFAC>"));

            slot = nextSlot(slot);
        }

        inv.setItem(45, createItem(Material.ARROW, "<gradient:#67E8F9:#3B82F6><bold>Zurück</bold></gradient>",
                "<gray>Zur Hauptübersicht</gray>"));

        inv.setItem(53, createItem(Material.BARRIER, "<#F87171><bold>Schließen</bold></#F87171>",
                "<gray>Menü schließen</gray>"));

        player.openInventory(inv);
    }

    public void openGroupDetail(Player player, String groupName) {
        PermissionGroup group = manager.getGroup(groupName);
        if (group == null) {
            player.sendMessage(MessageUtil.prefixed("<gray>Gruppe nicht gefunden.</gray>"));
            return;
        }

        PermissionMenuHolder holder = new PermissionMenuHolder(PermissionMenuType.GROUP_DETAIL, group.getName(), 54, "§8Gruppe: " + group.getName());
        Inventory inv = holder.getInventory();

        fill(inv);

        inv.setItem(13, createItem(Material.TOTEM_OF_UNDYING,
                "<gradient:#67E8F9:#3B82F6><bold>" + escape(group.getDisplayName()) + "</bold></gradient>",
                "<gray>Name: <#4DA3FF>" + escape(group.getName()) + "</#4DA3FF>",
                "<gray>Priorität: <#4DA3FF>" + group.getPriority() + "</#4DA3FF>"));

        inv.setItem(20, createItem(Material.WRITABLE_BOOK,
                "<gradient:#67E8F9:#3B82F6><bold>Permissions</bold></gradient>",
                "<gray>Anzahl: <#4DA3FF>" + group.getPermissions().size() + "</#4DA3FF>"));

        inv.setItem(24, createItem(Material.ECHO_SHARD,
                "<gradient:#67E8F9:#3B82F6><bold>Vererbungen</bold></gradient>",
                "<gray>Anzahl: <#4DA3FF>" + group.getInheritedGroups().size() + "</#4DA3FF>"));

        inv.setItem(31, createItem(Material.PAPER,
                "<gradient:#67E8F9:#3B82F6><bold>Info</bold></gradient>",
                buildGroupInfo(group)));

        inv.setItem(45, createItem(Material.ARROW, "<gradient:#67E8F9:#3B82F6><bold>Zurück</bold></gradient>",
                "<gray>Zur Gruppenübersicht</gray>"));

        inv.setItem(49, createItem(Material.ANVIL, "<gradient:#67E8F9:#3B82F6><bold>Bearbeiten</bold></gradient>",
                "<gray>Aktuell per Command:</gray>",
                "<#86EFAC>/permissions group addperm " + escape(group.getName()) + " <node></#86EFAC>",
                "<#86EFAC>/permissions group inherit " + escape(group.getName()) + " <gruppe></#86EFAC>"));

        inv.setItem(53, createItem(Material.BARRIER, "<#F87171><bold>Schließen</bold></#F87171>",
                "<gray>Menü schließen</gray>"));

        player.openInventory(inv);
    }

    public void openPlayerDetail(Player player, UUID uuid) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        PermissionUserData data = manager.getUser(uuid);

        String name = target.getName() != null ? target.getName() : uuid.toString();

        PermissionMenuHolder holder = new PermissionMenuHolder(PermissionMenuType.PLAYER_DETAIL, uuid.toString(), 54, "§8Spieler: " + name);
        Inventory inv = holder.getInventory();

        fill(inv);

        inv.setItem(13, createPlayerItem(target,
                "<gradient:#67E8F9:#3B82F6><bold>" + escape(name) + "</bold></gradient>",
                "<gray>UUID: <#4DA3FF>" + uuid + "</#4DA3FF>"));

        inv.setItem(20, createItem(Material.TOTEM_OF_UNDYING,
                "<gradient:#67E8F9:#3B82F6><bold>Gruppen</bold></gradient>",
                buildPlayerGroups(data)));

        inv.setItem(24, createItem(Material.WRITABLE_BOOK,
                "<gradient:#67E8F9:#3B82F6><bold>Direkte Permissions</bold></gradient>",
                buildPlayerPermissions(data)));

        inv.setItem(31, createItem(Material.NETHER_STAR,
                "<gradient:#67E8F9:#3B82F6><bold>Effektive Permissions</bold></gradient>",
                buildEffectivePermissions(uuid)));

        inv.setItem(45, createItem(Material.ARROW, "<gradient:#67E8F9:#3B82F6><bold>Zurück</bold></gradient>",
                "<gray>Zur Spielerübersicht</gray>"));

        inv.setItem(49, createItem(Material.ANVIL, "<gradient:#67E8F9:#3B82F6><bold>Bearbeiten</bold></gradient>",
                "<gray>Aktuell per Command:</gray>",
                "<#86EFAC>/permissions user addgroup " + escape(name) + " <gruppe></#86EFAC>",
                "<#86EFAC>/permissions user addperm " + escape(name) + " <node></#86EFAC>"));

        inv.setItem(53, createItem(Material.BARRIER, "<#F87171><bold>Schließen</bold></#F87171>",
                "<gray>Menü schließen</gray>"));

        player.openInventory(inv);
    }

    private void fill(Inventory inv) {
        ItemStack filler = createItem(Material.BLACK_STAINED_GLASS_PANE, "<gray> </gray>");
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }
    }

    private int nextSlot(int current) {
        current++;
        if (current % 9 == 8) {
            current += 2;
        }
        return current;
    }

    private ItemStack createPlayerItem(OfflinePlayer player, String name, String... lore) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(MessageUtil.parse(name));
        meta.lore(toComponents(lore));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        if (meta instanceof org.bukkit.inventory.meta.SkullMeta skullMeta && player.getName() != null) {
            skullMeta.setOwningPlayer(player);
        }

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        meta.lore(toComponents(lore));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private List<Component> toComponents(String... lines) {
        List<Component> list = new ArrayList<>();
        for (String line : lines) {
            list.add(MessageUtil.parse(line));
        }
        return list;
    }

    private String[] buildGroupInfo(PermissionGroup group) {
        List<String> lines = new ArrayList<>();
        lines.add("<gray>Permissions:</gray>");
        if (group.getPermissions().isEmpty()) {
            lines.add("<gray>- <#F87171>Keine</#F87171>");
        } else {
            group.getPermissions().stream().limit(8).forEach(perm ->
                    lines.add("<gray>- <#4DA3FF>" + escape(perm) + "</#4DA3FF>"));
        }

        lines.add("");
        lines.add("<gray>Vererbungen:</gray>");
        if (group.getInheritedGroups().isEmpty()) {
            lines.add("<gray>- <#F87171>Keine</#F87171>");
        } else {
            group.getInheritedGroups().stream().limit(8).forEach(inherit ->
                    lines.add("<gray>- <#4DA3FF>" + escape(inherit) + "</#4DA3FF>"));
        }

        return lines.toArray(new String[0]);
    }

    private String[] buildPlayerGroups(PermissionUserData data) {
        List<String> lines = new ArrayList<>();
        if (data.getGroups().isEmpty()) {
            lines.add("<gray>Keine Gruppen zugewiesen</gray>");
        } else {
            data.getGroups().forEach(group ->
                    lines.add("<gray>- <#4DA3FF>" + escape(group) + "</#4DA3FF>"));
        }
        return lines.toArray(new String[0]);
    }

    private String[] buildPlayerPermissions(PermissionUserData data) {
        List<String> lines = new ArrayList<>();
        if (data.getPermissions().isEmpty()) {
            lines.add("<gray>Keine direkten Permissions</gray>");
        } else {
            data.getPermissions().stream().limit(10).forEach(perm ->
                    lines.add("<gray>- <#4DA3FF>" + escape(perm) + "</#4DA3FF>"));
        }
        return lines.toArray(new String[0]);
    }

    private String[] buildEffectivePermissions(UUID uuid) {
        List<String> lines = new ArrayList<>();
        var effective = manager.getEffectivePermissions(uuid);

        if (effective.isEmpty()) {
            lines.add("<gray>Keine effektiven Permissions</gray>");
        } else {
            effective.stream().limit(10).forEach(perm ->
                    lines.add("<gray>- <#4DA3FF>" + escape(perm) + "</#4DA3FF>"));
        }

        return lines.toArray(new String[0]);
    }

    private String escape(String text) {
        return text.replace("<", "\\<").replace(">", "\\>");
    }
}