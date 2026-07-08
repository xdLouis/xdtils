package de.louis.xdtils.listener;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;

import java.util.regex.Pattern;

public class ArmorTrimListener implements Listener {

    private static final Pattern GUI_TITLE_PATTERN = Pattern.compile(".*Armor Trim.*");

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
                .plainText().serialize(event.getView().title());

        if (!GUI_TITLE_PATTERN.matcher(title).matches()) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !(clicked.getItemMeta() instanceof ArmorMeta clickedMeta)) return;

        ArmorTrim trim = clickedMeta.getTrim();
        if (trim == null) return;

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getItemMeta() instanceof ArmorMeta heldMeta) {
            heldMeta.setTrim(new ArmorTrim(trim.getMaterial(), trim.getPattern()));
            heldMeta.displayName(null);
            heldMeta.lore(null);
            held.setItemMeta(heldMeta);
            player.closeInventory();
            player.sendMessage(MessageUtil.armorTrimApplied(
                    trim.getPattern().key().value(),
                    trim.getMaterial().key().value()
            ));
        } else {
            player.sendMessage(MessageUtil.armorTrimNoArmor());
        }
    }
}
