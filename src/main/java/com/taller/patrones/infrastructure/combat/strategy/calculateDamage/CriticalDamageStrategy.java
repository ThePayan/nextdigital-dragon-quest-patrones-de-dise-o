package com.taller.patrones.infrastructure.combat.strategy.calculateDamage;

import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.Character;

import java.util.Random;

public class CriticalDamageStrategy implements DamageStrategy {

    private final Random random = new Random();

    @Override
    public int calculate(Character attacker, Character defender, Attack attack) {
        int base = attacker.getAttack() * attack.getBasePower() / 100;
        int damage = Math.max(1, base - defender.getDefense());
        boolean isCritical = random.nextDouble() < 0.20; // 20% probabilidad
        return isCritical ? (int) Math.round(damage * 1.5) : damage;
    }
}
