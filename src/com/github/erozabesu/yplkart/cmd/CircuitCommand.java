package com.github.erozabesu.yplkart.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.cmd.circuit.CircuitCommandEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;

/**
 * /ka circuitコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class CircuitCommand extends Command {
    public CircuitCommand() {
        super("circuit");
    }
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 1) {
            // TODO: ここでコマンドの使い方を表示させられる。
            return false;
        }

        if(args.length == 1 || !CircuitCommandEnum.execute(sender, label, args)) {
            if (CommonUtil.isPlayer(sender)) {
                SystemMessageEnum.referenceCircuitIngame.sendConvertedMessage(sender);
            } else {
                SystemMessageEnum.referenceCircuitOutgame.sendConvertedMessage(sender);
            }
        }

        return true;
    }
}