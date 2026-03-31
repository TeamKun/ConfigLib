package net.kunmc.lab.configlib.command;

import net.kunmc.lab.commandlib.CommandContext;

public class SingleValueModifyCommandMessageParameter {
    private final String entryName;
    private final CommandContext ctx;

    public SingleValueModifyCommandMessageParameter(String entryName, CommandContext ctx) {
        this.entryName = entryName;
        this.ctx = ctx;
    }

    public String entryName() {
        return entryName;
    }

    public CommandContext ctx() {
        return ctx;
    }
}
