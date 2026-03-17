package com.taller.patrones.domain;

/**
 * Representa un personaje en combate.
 */
public class Character {

    private final String name;
    private int currentHp;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int speed;

    public static Builder builder() {
        return new Builder();
    }

    public Character(String name, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    private Character(Builder builder) {
        this.name = builder.name;
        this.maxHp = builder.maxHp;
        this.currentHp = builder.currentHp != null ? builder.currentHp : builder.maxHp;
        this.attack = builder.attack;
        this.defense = builder.defense;
        this.speed = builder.speed;
    }

    public String getName() { return name; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }

    public void takeDamage(int damage) {
        this.currentHp = Math.max(0, currentHp - damage);
    }

    public void heal(int amount) {
        this.currentHp = Math.min(maxHp, currentHp + amount);
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public double getHpPercentage() {
        return maxHp > 0 ? (double) currentHp / maxHp * 100 : 0;
    }

    /**
     * Builder para construir personajes con parámetros opcionales y valores por defecto.
     */
    public static class Builder {
        private String name = "Hero";
        private int maxHp = 100;
        private Integer currentHp;
        private int attack = 10;
        private int defense = 10;
        private int speed = 10;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder maxHp(int maxHp) {
            this.maxHp = maxHp;
            return this;
        }

        public Builder currentHp(int currentHp) {
            this.currentHp = currentHp;
            return this;
        }

        public Builder attack(int attack) {
            this.attack = attack;
            return this;
        }

        public Builder defense(int defense) {
            this.defense = defense;
            return this;
        }

        public Builder speed(int speed) {
            this.speed = speed;
            return this;
        }

        public Character build() {
            return new Character(this);
        }
    }
}
