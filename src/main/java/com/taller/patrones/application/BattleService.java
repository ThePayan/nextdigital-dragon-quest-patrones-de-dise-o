package com.taller.patrones.application;

import com.taller.patrones.application.observer.AnalyticsDamageObserver;
import com.taller.patrones.application.observer.AuditLogDamageObserver;
import com.taller.patrones.application.observer.DamageEvent;
import com.taller.patrones.application.observer.DamageObserver;
import com.taller.patrones.application.observer.StatsDamageObserver;
import com.taller.patrones.application.command.AttackCommand;
import com.taller.patrones.application.command.BattleCommand;
import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.Battle;
import com.taller.patrones.domain.Character;
import com.taller.patrones.infrastructure.combat.CombatEngine;
import com.taller.patrones.infrastructure.persistence.BattleRepository;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Caso de uso: gestionar batallas.
 * <p>
 * Nota: Crea sus propias dependencias con new. Cada vez que necesitamos
 * un CombatEngine o BattleRepository, hacemos new aquí.
 */
public class BattleService {

    private final CombatEngine combatEngine = new CombatEngine();
    private final BattleRepository battleRepository = BattleRepository.getInstance();
    private final List<DamageObserver> damageObservers = new ArrayList<>();
    private final Map<String, Deque<BattleCommand>> battleHistory = new HashMap<>();

    public static final List<String> PLAYER_ATTACKS = List.of("TACKLE", "SLASH", "FIREBALL", "ICE_BEAM", "POISON_STING", "THUNDER", "METEORO", "CRITICO");
    public static final List<String> ENEMY_ATTACKS = List.of("TACKLE", "SLASH", "FIREBALL");

    public BattleService() {
        registerDamageObserver(new AnalyticsDamageObserver());
        registerDamageObserver(new AuditLogDamageObserver());
        registerDamageObserver(new StatsDamageObserver());
    }

    public BattleStartResult startBattle(String playerName, String enemyName) {
        Character player = Character.builder()
            .name(playerName != null ? playerName : "Héroe")
            .maxHp(150)
            .attack(25)
            .defense(15)
            .speed(20)
            .build();

        Character enemy = Character.builder()
            .name(enemyName != null ? enemyName : "Dragón")
            .maxHp(120)
            .attack(30)
            .defense(10)
            .speed(15)
            .build();

        Battle battle = new Battle(player, enemy);
        String battleId = UUID.randomUUID().toString();
        battleRepository.save(battleId, battle);

        return new BattleStartResult(battleId, battle);
    }

    public Battle getBattle(String battleId) {
        return battleRepository.findById(battleId);
    }

    public void executePlayerAttack(String battleId, String attackName) {
        Battle battle = battleRepository.findById(battleId);
        if (battle == null || battle.isFinished() || !battle.isPlayerTurn()) return;

        Attack attack = combatEngine.createAttack(attackName);
        int damage = combatEngine.calculateDamage(battle.getPlayer(), battle.getEnemy(), attack);
        applyDamage(battleId, battle, battle.getPlayer(), battle.getEnemy(), damage, attack);
    }

    public void executeEnemyAttack(String battleId, String attackName) {
        Battle battle = battleRepository.findById(battleId);
        if (battle == null || battle.isFinished() || battle.isPlayerTurn()) return;

        Attack attack = combatEngine.createAttack(attackName != null ? attackName : "TACKLE");
        int damage = combatEngine.calculateDamage(battle.getEnemy(), battle.getPlayer(), attack);
        applyDamage(battleId, battle, battle.getEnemy(), battle.getPlayer(), damage, attack);
    }

    private void applyDamage(String battleId, Battle battle, Character attacker, Character defender, int damage, Attack attack) {
        AttackCommand command = new AttackCommand(battle, attacker, defender, attack, damage);
        command.execute();
        pushHistory(battleId, command);
        notifyDamageObservers(new DamageEvent(battle, attacker, defender, attack, damage));
    }

    public void registerDamageObserver(DamageObserver observer) {
        if (observer != null) {
            damageObservers.add(observer);
        }
    }

    public void removeDamageObserver(DamageObserver observer) {
        damageObservers.remove(observer);
    }

    private void notifyDamageObservers(DamageEvent event) {
        for (DamageObserver observer : List.copyOf(damageObservers)) {
            observer.onDamage(event);
        }
    }

    public void undoLastAttack(String battleId) {
        Battle battle = battleRepository.findById(battleId);
        if (battle == null) return;

        Deque<BattleCommand> history = battleHistory.get(battleId);
        if (history == null || history.isEmpty()) return;

        BattleCommand lastCommand = history.pop();
        lastCommand.undo();
    }

    private void pushHistory(String battleId, BattleCommand command) {
        battleHistory.computeIfAbsent(battleId, id -> new ArrayDeque<>()).push(command);
    }

    public BattleStartResult startBattleFromExternal(String fighter1Name, int fighter1Hp, int fighter1Atk,
                                                     String fighter2Name, int fighter2Hp, int fighter2Atk) {
         Character player = Character.builder()
            .name(fighter1Name)
            .maxHp(fighter1Hp)
            .attack(fighter1Atk)
            .defense(10)
            .speed(10)
            .build();

        Character enemy = Character.builder()
            .name(fighter2Name)
            .maxHp(fighter2Hp)
            .attack(fighter2Atk)
            .defense(10)
            .speed(10)
            .build();
        Battle battle = new Battle(player, enemy);
        String battleId = UUID.randomUUID().toString();
        battleRepository.save(battleId, battle);
        return new BattleStartResult(battleId, battle);
    }

    public record BattleStartResult(String battleId, Battle battle) {}
}
