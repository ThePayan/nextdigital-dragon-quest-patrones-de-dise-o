package com.taller.patrones.application.command;

import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.Battle;
import com.taller.patrones.domain.Character;

public class AttackCommand implements BattleCommand {

    private final Battle battle;
    private final Character attacker;
    private final Character defender;
    private final Attack attack;
    private final int damage;

    private String previousTurn;
    private boolean previousFinished;
    private int previousLastDamage;
    private String previousLastDamageTarget;
    private int previousLogSize;

    public AttackCommand(Battle battle, Character attacker, Character defender, Attack attack, int damage) {
        this.battle = battle;
        this.attacker = attacker;
        this.defender = defender;
        this.attack = attack;
        this.damage = damage;
    }

    @Override
    public void execute() {
        previousTurn = battle.getCurrentTurn();
        previousFinished = battle.isFinished();
        previousLastDamage = battle.getLastDamage();
        previousLastDamageTarget = battle.getLastDamageTarget();
        previousLogSize = battle.getBattleLog().size();

        defender.takeDamage(damage);
        String target = defender == battle.getPlayer() ? "player" : "enemy";
        battle.setLastDamage(damage, target);
        battle.log(attacker.getName() + " usa " + attack.getName() + " y hace " + damage + " de daño a " + defender.getName());
        battle.switchTurn();
        if (!defender.isAlive()) {
            battle.finish(attacker.getName());
        }
    }

    @Override
    public void undo() {
        defender.heal(damage);
        battle.setLastDamage(previousLastDamage, previousLastDamageTarget);
        battle.setCurrentTurn(previousTurn);
        battle.setFinished(previousFinished);
        battle.truncateLog(previousLogSize);
    }

    public Attack getAttack() { return attack; }
    public int getDamage() { return damage; }
    public Character getAttacker() { return attacker; }
    public Character getDefender() { return defender; }
    public Battle getBattle() { return battle; }
}
