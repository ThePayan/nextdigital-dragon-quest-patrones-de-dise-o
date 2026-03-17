package com.taller.patrones.application.command;

public interface BattleCommand {
    void execute();
    void undo();
}
