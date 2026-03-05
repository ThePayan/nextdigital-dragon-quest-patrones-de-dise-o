package com.taller.patrones.infrastructure.combat.strategy.calculateDamage;

import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.Character;

public interface DamageStrategy {
    int calculate(Character attacker, Character defender, Attack attack);
}
