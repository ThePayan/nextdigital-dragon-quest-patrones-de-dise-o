package com.taller.patrones.application.observer;

import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.Battle;
import com.taller.patrones.domain.Character;

public class DamageEvent {

    private final Battle battle;
    private final Character attacker;
    private final Character defender;
    private final Attack attack;
    private final int damage;

    public DamageEvent(Battle battle, Character attacker, Character defender, Attack attack, int damage) {
        this.battle = battle;
        this.attacker = attacker;
        this.defender = defender;
        this.attack = attack;
        this.damage = damage;
    }

    public Battle getBattle() { return battle; }
    public Character getAttacker() { return attacker; }
    public Character getDefender() { return defender; }
    public Attack getAttack() { return attack; }
    public int getDamage() { return damage; }
}
