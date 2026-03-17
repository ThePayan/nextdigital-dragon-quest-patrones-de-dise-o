package com.taller.patrones.application.observer;

public class StatsDamageObserver implements DamageObserver {

    @Override
    public void onDamage(DamageEvent event) {
        double hpLeft = event.getDefender().getHpPercentage();
        System.out.println("[stats] " + event.getDefender().getName()
            + " hp=" + String.format("%.2f", hpLeft) + "%");
    }
}
