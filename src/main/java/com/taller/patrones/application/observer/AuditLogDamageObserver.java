package com.taller.patrones.application.observer;

public class AuditLogDamageObserver implements DamageObserver {

    @Override
    public void onDamage(DamageEvent event) {
        System.out.println("[audit] " + event.getAttacker().getName()
            + " used " + event.getAttack().getName()
            + " for " + event.getDamage()
            + " on " + event.getDefender().getName());
    }
}
