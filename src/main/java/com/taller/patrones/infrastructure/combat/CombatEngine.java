package com.taller.patrones.infrastructure.combat;

import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.Character;

/**
 * Motor de combate. Calcula daño y crea ataques.
 * <p>
 * Nota: Esta clase crece cada vez que añadimos un ataque nuevo o un tipo de daño distinto.
 */
public class CombatEngine {

    /**
     * Crea un ataque a partir de su nombre.
     * Cada ataque nuevo requiere modificar este método.
     */
    public Attack createAttack(String name) {
        String n = name != null ? name.toUpperCase() : "";
        return switch (n) {
            case "TACKLE" -> new Attack("Tackle", 40, Attack.AttackType.NORMAL);
            case "SLASH" -> new Attack("Slash", 55, Attack.AttackType.NORMAL);
            case "FIREBALL" -> new Attack("Fireball", 80, Attack.AttackType.SPECIAL);
            case "ICE_BEAM" -> new Attack("Ice Beam", 70, Attack.AttackType.SPECIAL);
            case "POISON_STING" -> new Attack("Poison Sting", 20, Attack.AttackType.STATUS);
            case "THUNDER" -> new Attack("Thunder", 90, Attack.AttackType.SPECIAL);
            default -> new Attack("Golpe", 30, Attack.AttackType.NORMAL);
        };
    }

    /**
     * Calcula el daño según el tipo de ataque.
     * Cada fórmula nueva (ej. crítico, veneno con tiempo) requiere modificar este switch.
     */
    public int calculateDamage(Character attacker, Character defender, Attack attack) {
        return switch (attack.getType()) {
            case NORMAL -> {
                int raw = attacker.getAttack() * attack.getBasePower() / 100;
                yield Math.max(1, raw - defender.getDefense());
            }
            case SPECIAL -> {
                int raw = attacker.getAttack() * attack.getBasePower() / 100;
                int effectiveDef = defender.getDefense() / 2;
                yield Math.max(1, raw - effectiveDef);
            }
            case STATUS -> attacker.getAttack(); // Los de estado no hacen daño directo... ¿o sí?
            default -> 0;
        };
    }
}
