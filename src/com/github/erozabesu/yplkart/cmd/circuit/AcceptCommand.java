package com.github.erozabesu.yplkart.cmd.circuit;

import java.awt.print.Paper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.RaceManager;

public class AcceptCommand extends Command {
    public AcceptCommand() {
        super("accept");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Paper))
            // TODO: ここでコマンドの使い方を表示させられる。
            return false;
        Player player = (Player) sender;
        RaceManager.setMatchingCircuitData(player.getUniqueId());
        return true;
    }
}
