package com.taller.patrones.domain;

import java.util.List;

public class CompositeAttack extends Attack {

    private final List<Attack> attacks;

    public CompositeAttack(String name, List<Attack> attacks) {
        super(name, 0, AttackType.NORMAL);
        this.attacks = attacks;
    }

    public List<Attack> getAttacks() {
        return attacks;
    }
}
