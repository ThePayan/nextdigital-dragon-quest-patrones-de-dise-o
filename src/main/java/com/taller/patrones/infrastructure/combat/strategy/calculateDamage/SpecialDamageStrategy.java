package com.taller.patrones.infrastructure.combat.strategy.calculateDamage;

import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.Character;

public class SpecialDamageStrategy implements DamageStrategy {
    @Override
    public int calculate(Character attacker, Character defender, Attack attack) {
        int raw = attacker.getAttack() * attack.getBasePower() / 100;
        int effectiveDef = defender.getDefense() / 2;
        return Math.max(1, raw - effectiveDef);
    }
}
