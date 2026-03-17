package com.taller.patrones.application;

import com.taller.patrones.domain.Battle;

import java.util.List;

public class BattleFacade {

    private final BattleService battleService = new BattleService();

    public BattleService.BattleStartResult startBattle(String playerName, String enemyName) {
        return battleService.startBattle(playerName, enemyName);
    }

    public BattleService.BattleStartResult startBattleFromExternal(String fighter1Name, int fighter1Hp, int fighter1Atk,
                                                                   String fighter2Name, int fighter2Hp, int fighter2Atk) {
        return battleService.startBattleFromExternal(
            fighter1Name, fighter1Hp, fighter1Atk,
            fighter2Name, fighter2Hp, fighter2Atk
        );
    }

    public Battle getBattle(String battleId) {
        return battleService.getBattle(battleId);
    }

    public void executeAttack(String battleId, String attackName) {
        Battle battle = battleService.getBattle(battleId);
        if (battle == null) return;

        if (battle.isPlayerTurn()) {
            battleService.executePlayerAttack(battleId, attackName);
        } else {
            battleService.executeEnemyAttack(battleId, attackName);
        }
    }

    public void executeEnemyTurn(String battleId) {
        Battle battle = battleService.getBattle(battleId);
        if (battle == null || battle.isPlayerTurn() || battle.isFinished()) return;

        List<String> attacks = BattleService.ENEMY_ATTACKS;
        String attack = attacks.get((int) (Math.random() * attacks.size()));
        battleService.executeEnemyAttack(battleId, attack);
    }

    public void undoLastAttack(String battleId) {
        battleService.undoLastAttack(battleId);
    }
}
