package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InvseeCommand implements CommandExecutor, TabCompleter, Listener {

    // Slot-Mapping: GUI-Slot → Armor/Offhand-Typ
    // Zeile 5 (slots 36-44): 36=Helm, 37=Brust, 38=Hose, 39=Schuhe, 40=Offhand
    private static final int SLOT_HELMET     = 36;
    private static final int SLOT_CHESTPLATE = 37;
    private static final int SLOT_LEGGINGS   = 38;
    private static final int SLOT_BOOTS      = 39;
    private static final int SLOT_OFFHAND    = 40;

    // Deko-Slots in Zeile 5 + 6
    private static final Set<Integer> DECO_SLOTS = new HashSet<>(Arrays.asList(
            41, 42, 43, 44,
            45, 46, 47, 48, 49, 50, 51, 52, 53
    ));

    // Öffnende Spieler → Ziel-Spieler UUID
    private final Map<UUID, UUID> viewers = new HashMap<>();

    private final JavaPlugin plugin;

    public InvseeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player viewer)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!viewer.hasPermission("xdtils.invsee")) {
            viewer.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            viewer.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("invsee") + "<gray> <spieler></gray>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            viewer.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        if (target.getUniqueId().equals(viewer.getUniqueId())) {
            viewer.sendMessage(MessageUtil.prefixed("<gray>Du kannst nicht dein eigenes Inventar mit /invsee öffnen.</gray>"));
            return true;
        }

        openInvsee(viewer, target);
        viewer.sendMessage(MessageUtil.invseeOpened(target.getName()));
        return true;
    }

    private void openInvsee(Player viewer, Player target) {
        Component title = MiniMessage.miniMessage().deserialize(
                "<gray>Inventar von </gray><#4DA3FF>" + target.getName() + "</#4DA3FF>"
        );

        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Slots 0-35: normales Inventar des Zielspielers
        ItemStack[] contents = target.getInventory().getStorageContents();
        for (int i = 0; i < 36 && i < contents.length; i++) {
            if (contents[i] != null) {
                gui.setItem(i, contents[i].clone());
            }
        }

        // Rüstungsslots → Zeile 5
        setArmorSlot(gui, SLOT_HELMET,     target.getInventory().getHelmet());
        setArmorSlot(gui, SLOT_CHESTPLATE, target.getInventory().getChestplate());
        setArmorSlot(gui, SLOT_LEGGINGS,   target.getInventory().getLeggings());
        setArmorSlot(gui, SLOT_BOOTS,      target.getInventory().getBoots());
        setArmorSlot(gui, SLOT_OFFHAND,    target.getInventory().getItemInOffHand());

        // Deko: graue Glaspanes für leere Stellen
        ItemStack pane = createPane();
        for (int slot : DECO_SLOTS) {
            gui.setItem(slot, pane);
        }

        // Info-Item in Slot 49 (Mitte Zeile 6)
        gui.setItem(49, createInfoItem(target));

        viewers.put(viewer.getUniqueId(), target.getUniqueId());
        viewer.openInventory(gui);
    }

    private void setArmorSlot(Inventory gui, int slot, ItemStack item) {
        if (item != null && !item.getType().isAir()) {
            gui.setItem(slot, item.clone());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player viewer)) return;
        UUID viewerUuid = viewer.getUniqueId();
        if (!viewers.containsKey(viewerUuid)) return;

        // Nur klicks im oberen (GUI) Inventory abfangen
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;

        int slot = event.getSlot();

        // Deko-Slots blockieren
        if (DECO_SLOTS.contains(slot)) {
            event.setCancelled(true);
            return;
        }

        Player target = getTargetPlayer(viewerUuid);
        if (target == null) {
            event.setCancelled(true);
            viewers.remove(viewerUuid);
            viewer.closeInventory();
            return;
        }

        // Klick erlauben, dann nach kurzer Verzögerung syncen
        Bukkit.getScheduler().runTask(plugin, () -> syncToTarget(viewer, target));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player viewer)) return;
        UUID viewerUuid = viewer.getUniqueId();
        if (!viewers.containsKey(viewerUuid)) return;

        Player target = getTargetPlayer(viewerUuid);
        if (target != null) {
            syncToTarget(viewer, target);
        }

        viewers.remove(viewerUuid);
    }

    private void syncToTarget(Player viewer, Player target) {
        Inventory gui = viewer.getOpenInventory().getTopInventory();

        // Normales Inventar (0-35)
        ItemStack[] newContents = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            ItemStack item = gui.getItem(i);
            newContents[i] = (item != null && !item.getType().isAir()) ? item.clone() : null;
        }
        target.getInventory().setStorageContents(newContents);

        // Rüstung
        target.getInventory().setHelmet(    getCloneOrNull(gui, SLOT_HELMET));
        target.getInventory().setChestplate(getCloneOrNull(gui, SLOT_CHESTPLATE));
        target.getInventory().setLeggings(  getCloneOrNull(gui, SLOT_LEGGINGS));
        target.getInventory().setBoots(     getCloneOrNull(gui, SLOT_BOOTS));

        // Offhand
        ItemStack offhand = getCloneOrNull(gui, SLOT_OFFHAND);
        target.getInventory().setItemInOffHand(offhand != null ? offhand : new ItemStack(Material.AIR));

        target.updateInventory();
    }

    @Nullable
    private ItemStack getCloneOrNull(Inventory inv, int slot) {
        ItemStack item = inv.getItem(slot);
        return (item != null && !item.getType().isAir()) ? item.clone() : null;
    }

    @Nullable
    private Player getTargetPlayer(UUID viewerUuid) {
        UUID targetUuid = viewers.get(viewerUuid);
        if (targetUuid == null) return null;
        return Bukkit.getPlayer(targetUuid);
    }

    private ItemStack createPane() {
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(Component.empty());
        pane.setItemMeta(meta);
        return pane;
    }

    private ItemStack createInfoItem(Player target) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = skull.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize(
                "<#4DA3FF>" + target.getName() + "</#4DA3FF>"
        ));
        meta.lore(List.of(
                MiniMessage.miniMessage().deserialize("<gray>Inventar wird live synchronisiert.</gray>"),
                MiniMessage.miniMessage().deserialize("<gray>Schließe das Fenster zum Speichern.</gray>")
        ));
        skull.setItemMeta(meta);
        return skull;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}