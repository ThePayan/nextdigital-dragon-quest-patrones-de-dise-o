package com.taller.patrones.infrastructure.combat.strategy.calculateDamage;

import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.Character;

public class StatusDamageStrategy implements DamageStrategy {
    @Override
    public int calculate(Character attacker, Character defender, Attack attack) {
        // Los de estado no deberían hacer daño directo; devolvemos 0 por defecto.
        return 0;
    }
}
