package com.taller.patrones.application.observer;

public class AnalyticsDamageObserver implements DamageObserver {

    @Override
    public void onDamage(DamageEvent event) {
        System.out.println("[analytics] " + event.getAttacker().getName()
            + " -> " + event.getDefender().getName()
            + " attack=" + event.getAttack().getName()
            + " damage=" + event.getDamage());
    }
}
