package de.louis.xdtils.main;

import de.louis.xdtils.commands.EnchantCommand;
import de.louis.xdtils.commands.GamemodeCommand;
import de.louis.xdtils.commands.HatCommand;
import de.louis.xdtils.commands.InvseeCommand;
import de.louis.xdtils.commands.SpeedCommand;
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
        // Gamemode
        GamemodeCommand gamemodeCommand = new GamemodeCommand();
        registerCommand("gamemode", gamemodeCommand, gamemodeCommand);
        registerCommand("gm", gamemodeCommand, gamemodeCommand);

        // Speed
        SpeedCommand speedAuto = new SpeedCommand(SpeedCommand.SpeedType.AUTO);
        SpeedCommand speedWalk = new SpeedCommand(SpeedCommand.SpeedType.WALK);
        SpeedCommand speedFly  = new SpeedCommand(SpeedCommand.SpeedType.FLY);
        registerCommand("speed",     speedAuto, speedAuto);
        registerCommand("walkspeed", speedWalk, speedWalk);
        registerCommand("flyspeed",  speedFly,  speedFly);

        // Enchant
        EnchantCommand enchantCommand = new EnchantCommand();
        registerCommand("enchant", enchantCommand, enchantCommand);

        // Hat
        registerCommand("hat", new HatCommand(), null);

        // Invsee
        InvseeCommand invseeCommand = new InvseeCommand(this);
        registerCommand("invsee", invseeCommand, invseeCommand);

        // Workstations
        registerWorkstation("workbench",   "workbench");
        registerWorkstation("anvil",       "anvil");
        registerWorkstation("grindstone",  "grindstone");
        registerWorkstation("cartography", "cartography");
        registerWorkstation("loom",        "loom");
        registerWorkstation("stonecutter", "stonecutter");
        registerWorkstation("smithing",    "smithing");
        registerWorkstation("enchanting",  "enchanting");
    }

    private void registerCommand(String name,
                                 org.bukkit.command.CommandExecutor executor,
                                 org.bukkit.command.TabCompleter completer) {
        var cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(executor);
            if (completer != null) cmd.setTabCompleter(completer);
        }
    }

    private void registerWorkstation(String commandName, String type) {
        var cmd = getCommand(commandName);
        if (cmd != null) {
            cmd.setExecutor(new WorkstationCommand(type));
        }
    }
}