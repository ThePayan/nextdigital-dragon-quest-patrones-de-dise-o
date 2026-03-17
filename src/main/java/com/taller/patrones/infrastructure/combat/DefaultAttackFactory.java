package com.taller.patrones.infrastructure.combat;

import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.CompositeAttack;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DefaultAttackFactory implements AttackFactory {

    private final Map<String, Supplier<Attack>> registry = Map.of(
            "TACKLE", () -> new Attack("Tackle", 40, Attack.AttackType.NORMAL),
            "SLASH", () -> new Attack("Slash", 55, Attack.AttackType.NORMAL),
            "FIREBALL", () -> new Attack("Fireball", 80, Attack.AttackType.SPECIAL),
            "ICE_BEAM", () -> new Attack("Ice Beam", 70, Attack.AttackType.SPECIAL),
            "POISON_STING", () -> new Attack("Poison Sting", 20, Attack.AttackType.STATUS),
            "THUNDER", () -> new Attack("Thunder", 90, Attack.AttackType.SPECIAL),
            "METEORO", () -> new Attack("Meteoro", 120, Attack.AttackType.SPECIAL),
            "CRITICO", () -> new Attack("Crítico", 80, Attack.AttackType.CRITICO),
            "COMBO_TRIPLE", () -> new CompositeAttack("Combo Triple", List.of(
            new Attack("Tackle", 40, Attack.AttackType.NORMAL),
            new Attack("Slash", 55, Attack.AttackType.NORMAL),
            new Attack("Fireball", 80, Attack.AttackType.SPECIAL)
            ))
    );

    private final Supplier<Attack> defaultAttack =
            () -> new Attack("Golpe", 30, Attack.AttackType.NORMAL);

    @Override
    public Attack createAttack(String name) {
        if (name == null) {
            return defaultAttack.get();
        }
        return registry.getOrDefault(name.toUpperCase(), defaultAttack).get();
    }
}
