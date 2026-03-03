package com.taller.patrones.interfaces.rest;

import com.taller.patrones.application.BattleService;
import com.taller.patrones.domain.Battle;
import com.taller.patrones.domain.Character;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/battle")
@CrossOrigin(origins = "*")
public class BattleController {

    private final BattleService battleService = new BattleService();

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startBattle(@RequestBody(required = false) Map<String, String> body) {
        String playerName = body != null && body.containsKey("playerName") ? body.get("playerName") : null;
        String enemyName = body != null && body.containsKey("enemyName") ? body.get("enemyName") : null;

        var result = battleService.startBattle(playerName, enemyName);
        Battle battle = result.battle();

        return ResponseEntity.ok(Map.of(
                "battleId", result.battleId(),
                "player", toCharacterDto(battle.getPlayer()),
                "enemy", toCharacterDto(battle.getEnemy()),
                "currentTurn", battle.getCurrentTurn(),
                "battleLog", battle.getBattleLog(),
                "finished", battle.isFinished(),
                "playerAttacks", BattleService.PLAYER_ATTACKS,
                "enemyAttacks", BattleService.ENEMY_ATTACKS,
                "lastDamage", 0,
                "lastDamageTarget", ""
        ));
    }

    /**
     * Endpoint alternativo: recibe datos de combate en formato "externo".
     * Los campos vienen con nombres distintos (fighter1_hp, fighter1_atk...).
     * La conversión se hace aquí, manualmente, en el controller.
     */
    @PostMapping("/start/external")
    public ResponseEntity<Map<String, Object>> startBattleFromExternal(@RequestBody Map<String, Object> body) {
        String fighter1Name = (String) body.getOrDefault("fighter1_name", "Héroe");
        int fighter1Hp = ((Number) body.getOrDefault("fighter1_hp", 150)).intValue();
        int fighter1Atk = ((Number) body.getOrDefault("fighter1_atk", 25)).intValue();
        String fighter2Name = (String) body.getOrDefault("fighter2_name", "Dragón");
        int fighter2Hp = ((Number) body.getOrDefault("fighter2_hp", 120)).intValue();
        int fighter2Atk = ((Number) body.getOrDefault("fighter2_atk", 30)).intValue();

        var result = battleService.startBattleFromExternal(
                fighter1Name, fighter1Hp, fighter1Atk,
                fighter2Name, fighter2Hp, fighter2Atk
        );
        Battle battle = result.battle();

        return ResponseEntity.ok(Map.of(
                "battleId", result.battleId(),
                "player", toCharacterDto(battle.getPlayer()),
                "enemy", toCharacterDto(battle.getEnemy()),
                "currentTurn", battle.getCurrentTurn(),
                "battleLog", battle.getBattleLog(),
                "finished", battle.isFinished(),
                "playerAttacks", BattleService.PLAYER_ATTACKS,
                "lastDamage", 0,
                "lastDamageTarget", ""
        ));
    }

    @GetMapping("/{battleId}")
    public ResponseEntity<Map<String, Object>> getBattle(@PathVariable String battleId) {
        Battle battle = battleService.getBattle(battleId);
        if (battle == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toBattleDto(battle));
    }

    @PostMapping("/{battleId}/attack")
    public ResponseEntity<Map<String, Object>> attack(@PathVariable String battleId,
                                                       @RequestBody Map<String, String> body) {
        Battle battle = battleService.getBattle(battleId);
        if (battle == null) return ResponseEntity.notFound().build();

        String attackName = body != null && body.get("attack") != null ? body.get("attack") : "TACKLE";

        if (battle.isPlayerTurn()) {
            battleService.executePlayerAttack(battleId, attackName);
        } else {
            battleService.executeEnemyAttack(battleId, attackName);
        }

        return ResponseEntity.ok(toBattleDto(battleService.getBattle(battleId)));
    }

    @PostMapping("/{battleId}/enemy-turn")
    public ResponseEntity<Map<String, Object>> enemyTurn(@PathVariable String battleId) {
        Battle battle = battleService.getBattle(battleId);
        if (battle == null) return ResponseEntity.notFound().build();
        if (battle.isPlayerTurn() || battle.isFinished()) {
            return ResponseEntity.ok(toBattleDto(battle));
        }
        String attack = BattleService.ENEMY_ATTACKS.get((int) (Math.random() * BattleService.ENEMY_ATTACKS.size()));
        battleService.executeEnemyAttack(battleId, attack);
        return ResponseEntity.ok(toBattleDto(battleService.getBattle(battleId)));
    }

    private Map<String, Object> toBattleDto(Battle battle) {
        return Map.of(
                "player", toCharacterDto(battle.getPlayer()),
                "enemy", toCharacterDto(battle.getEnemy()),
                "currentTurn", battle.getCurrentTurn(),
                "battleLog", battle.getBattleLog(),
                "finished", battle.isFinished(),
                "playerAttacks", BattleService.PLAYER_ATTACKS,
                "lastDamage", battle.getLastDamage(),
                "lastDamageTarget", battle.getLastDamageTarget() != null ? battle.getLastDamageTarget() : ""
        );
    }

    private Map<String, Object> toCharacterDto(Character c) {
        return Map.of(
                "name", c.getName(),
                "currentHp", c.getCurrentHp(),
                "maxHp", c.getMaxHp(),
                "hpPercentage", c.getHpPercentage(),
                "attack", c.getAttack(),
                "defense", c.getDefense(),
                "speed", c.getSpeed(),
                "alive", c.isAlive()
        );
    }
}
