package com.taller.patrones.infrastructure.combat;

import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.Character;
import com.taller.patrones.infrastructure.combat.strategy.calculateDamage.CriticalDamageStrategy;
import com.taller.patrones.infrastructure.combat.strategy.calculateDamage.DamageStrategy;
import com.taller.patrones.infrastructure.combat.strategy.calculateDamage.NormalDamageStrategy;
import com.taller.patrones.infrastructure.combat.strategy.calculateDamage.SpecialDamageStrategy;
import com.taller.patrones.infrastructure.combat.strategy.calculateDamage.StatusDamageStrategy;

import java.util.Map;

/**
 * Motor de combate. Calcula daño y crea ataques.
 * <p>
 * Nota: Esta clase crece cada vez que añadimos un ataque nuevo o un tipo de daño distinto.
 */
public class CombatEngine {

    private final AttackFactory attackFactory;
    private final Map<Attack.AttackType, DamageStrategy> damageStrategies;

    public CombatEngine() {
        this(new DefaultAttackFactory(), defaultStrategies());
    }

    public CombatEngine(AttackFactory attackFactory) {
        this(attackFactory, defaultStrategies());
    }

    public CombatEngine(AttackFactory attackFactory, Map<Attack.AttackType, DamageStrategy> damageStrategies) {
        this.attackFactory = attackFactory;
        this.damageStrategies = damageStrategies;
    }

    /**
     * Crea un ataque a partir de su nombre.
     * Cada ataque nuevo requiere modificar este método.
     */
    public Attack createAttack(String name) {
        return attackFactory.createAttack(name);
    }

    /**
     * Calcula el daño según el tipo de ataque.
     * Cada fórmula nueva (ej. crítico, veneno con tiempo) requiere modificar este switch.
     */
    public int calculateDamage(Character attacker, Character defender, Attack attack) {
        DamageStrategy strategy = damageStrategies.get(attack.getType());
        if (strategy == null) {
            return 0;
        }
        return strategy.calculate(attacker, defender, attack);
    }

    private static Map<Attack.AttackType, DamageStrategy> defaultStrategies() {
        return Map.of(
                Attack.AttackType.NORMAL, new NormalDamageStrategy(),
                Attack.AttackType.SPECIAL, new SpecialDamageStrategy(),
                Attack.AttackType.STATUS, new StatusDamageStrategy(),
                Attack.AttackType.CRITICO, new CriticalDamageStrategy()
        );
    }
}
