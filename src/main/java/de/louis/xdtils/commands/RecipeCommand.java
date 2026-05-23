package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RecipeCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.recipe")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("recipe") + "<gray> <material></gray>"));
            return true;
        }

        Material mat = Material.matchMaterial(args[0]);
        if (mat == null) {
            player.sendMessage(MessageUtil.prefixed("<gray>Material <#F87171>"
                    + args[0] + "</#F87171><gray> nicht gefunden.</gray>"));
            return true;
        }

        List<Recipe> recipes = Bukkit.getRecipesFor(new ItemStack(mat));
        if (recipes.isEmpty()) {
            player.sendMessage(MessageUtil.prefixed("<gray>Kein Rezept für <#F87171>"
                    + mat.name().toLowerCase() + "</#F87171><gray> gefunden.</gray>"));
            return true;
        }

        // Erstes Rezept anzeigen
        Recipe recipe = recipes.get(0);
        player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        player.sendMessage(MM.deserialize("  <#4DA3FF><b>Rezept: </b></#4DA3FF><#67E8F9>"
                + mat.name().toLowerCase() + "</#67E8F9>"));
        player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));

        if (recipe instanceof ShapedRecipe shaped) {
            String[] shape = shaped.getShape();
            Map<Character, RecipeChoice> choiceMap = shaped.getChoiceMap();

            // 3x3 Grid aufbauen
            String[] grid = new String[9];
            Arrays.fill(grid, "<dark_gray>AIR<dark_gray>");
            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length(); col++) {
                    char c = shape[row].charAt(col);
                    RecipeChoice choice = choiceMap.get(c);
                    String name = "AIR";
                    if (choice instanceof RecipeChoice.MaterialChoice mc) {
                        name = mc.getItemStack().getType().name().toLowerCase();
                    }
                    grid[row * 3 + col] = name;
                }
            }

            for (int row = 0; row < 3; row++) {
                player.sendMessage(MM.deserialize(
                        "  <#67E8F9>" + grid[row*3] + "</#67E8F9>"
                                + " <dark_gray>|</dark_gray> <#67E8F9>" + grid[row*3+1] + "</#67E8F9>"
                                + " <dark_gray>|</dark_gray> <#67E8F9>" + grid[row*3+2] + "</#67E8F9>"));
            }

        } else if (recipe instanceof ShapelessRecipe shapeless) {
            List<String> items = new ArrayList<>();
            for (RecipeChoice choice : shapeless.getChoiceList()) {
                if (choice instanceof RecipeChoice.MaterialChoice mc) {
                    items.add(mc.getItemStack().getType().name().toLowerCase());
                }
            }
            player.sendMessage(MM.deserialize("  <gray>Zutaten:</gray>"));
            for (String item : items) {
                player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <#67E8F9>" + item + "</#67E8F9>"));
            }

        } else if (recipe instanceof FurnaceRecipe furnace) {
            player.sendMessage(MM.deserialize("  <gray>Typ:</gray> <#FCD34D>Schmelzofen</#FCD34D>"));
            player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Zutat:</gray> <#67E8F9>"
                    + furnace.getInput().getType().name().toLowerCase() + "</#67E8F9>"));
            player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>XP:</gray> <#86EFAC>"
                    + furnace.getExperience() + "</#86EFAC>"));
        }

        player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Ergebnis:</gray> <#86EFAC>"
                + recipe.getResult().getAmount() + "x "
                + mat.name().toLowerCase() + "</#86EFAC>"));
        if (recipes.size() > 1) {
            player.sendMessage(MM.deserialize("  <dark_gray>(" + (recipes.size() - 1)
                    + " weitere Rezepte vorhanden)</dark_gray>"));
        }
        player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            String input = args[0].toLowerCase(Locale.ROOT);
            for (Material mat : Material.values()) {
                if (!mat.isAir() && mat.name().toLowerCase().startsWith(input)) {
                    list.add(mat.name().toLowerCase());
                    if (list.size() >= 20) break;
                }
            }
            return list;
        }
        return List.of();
    }
}