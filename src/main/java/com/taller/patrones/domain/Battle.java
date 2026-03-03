package com.taller.patrones.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una batalla entre dos personajes.
 */
public class Battle {

    private final Character player;
    private final Character enemy;
    private String currentTurn;
    private final List<String> battleLog;
    private boolean finished;
    private int lastDamage;
    private String lastDamageTarget;

    public Battle(Character player, Character enemy) {
        this.player = player;
        this.enemy = enemy;
        this.battleLog = new ArrayList<>();
        this.finished = false;
        this.currentTurn = player.getSpeed() >= enemy.getSpeed() ? "player" : "enemy";
        log("¡Comienza la batalla! " + player.getName() + " vs " + enemy.getName());
    }

    public Character getPlayer() { return player; }
    public Character getEnemy() { return enemy; }
    public String getCurrentTurn() { return currentTurn; }
    public List<String> getBattleLog() { return battleLog; }
    public boolean isFinished() { return finished; }
    public int getLastDamage() { return lastDamage; }
    public String getLastDamageTarget() { return lastDamageTarget; }

    public void log(String message) { battleLog.add(message); }
    public void switchTurn() { currentTurn = "player".equals(currentTurn) ? "enemy" : "player"; }
    public void finish(String winner) { finished = true; log("¡" + winner + " gana la batalla!"); }
    public boolean isPlayerTurn() { return "player".equals(currentTurn); }
    public void setLastDamage(int damage, String target) { this.lastDamage = damage; this.lastDamageTarget = target; }
}
