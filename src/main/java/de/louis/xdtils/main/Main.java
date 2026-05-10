package de.louis.xdtils.main;

import de.louis.xdtils.commands.GamemodeCommand;
import de.louis.xdtils.commands.WorkstationCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
        getLogger().info("xdtils wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("xdtils wurde deaktiviert.");
    }

    private void registerCommands() {
        GamemodeCommand gamemodeCommand = new GamemodeCommand();

        if (getCommand("gamemode") != null) {
            getCommand("gamemode").setExecutor(gamemodeCommand);
            getCommand("gamemode").setTabCompleter(gamemodeCommand);
        }

        if (getCommand("gm") != null) {
            getCommand("gm").setExecutor(gamemodeCommand);
            getCommand("gm").setTabCompleter(gamemodeCommand);
        }

        registerWorkstation("workbench", "workbench");
        registerWorkstation("anvil", "anvil");
        registerWorkstation("grindstone", "grindstone");
        registerWorkstation("cartography", "cartography");
        registerWorkstation("loom", "loom");
        registerWorkstation("stonecutter", "stonecutter");
        registerWorkstation("smithing", "smithing");
        registerWorkstation("enchanting", "enchanting");
    }

    private void registerWorkstation(String commandName, String type) {
        if (getCommand(commandName) != null) {
            getCommand(commandName).setExecutor(new WorkstationCommand(type));
        }
    }
}