package com.taller.patrones.infrastructure.combat;

import com.taller.patrones.domain.Attack;

public interface AttackFactory {
    Attack createAttack(String name);
    
}
