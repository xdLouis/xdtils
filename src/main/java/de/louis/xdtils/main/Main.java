package de.louis.xdtils.main;

import de.louis.xdtils.commands.WorkstationCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
        getLogger().info("xdTils wurde erfolgreich gestartet!");
    }

    @Override
    public void onDisable() {
        getLogger().info("xdTils wurde deaktiviert.");
    }

    private void registerCommands() {
        getCommand("workbench").setExecutor(new WorkstationCommand("workbench"));
        getCommand("anvil").setExecutor(new WorkstationCommand("anvil"));
        getCommand("grindstone").setExecutor(new WorkstationCommand("grindstone"));
        getCommand("cartography").setExecutor(new WorkstationCommand("cartography"));
        getCommand("loom").setExecutor(new WorkstationCommand("loom"));
        getCommand("stonecutter").setExecutor(new WorkstationCommand("stonecutter"));
        getCommand("smithing").setExecutor(new WorkstationCommand("smithing"));
        getCommand("enchanting").setExecutor(new WorkstationCommand("enchanting"));
    }
}
