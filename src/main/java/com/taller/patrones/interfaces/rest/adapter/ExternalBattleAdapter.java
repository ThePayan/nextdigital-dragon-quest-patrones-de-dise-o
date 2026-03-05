package com.taller.patrones.interfaces.rest.adapter;

import java.util.Map;

/**
 * Adaptador para convertir el payload externo en datos de dominio.
 */
public class ExternalBattleAdapter {

    public ExternalBattleInput adapt(Map<String, Object> body) {
        String fighter1Name = (String) body.getOrDefault("fighter1_name", "Héroe");
        int fighter1Hp = ((Number) body.getOrDefault("fighter1_hp", 150)).intValue();
        int fighter1Atk = ((Number) body.getOrDefault("fighter1_atk", 25)).intValue();
        String fighter2Name = (String) body.getOrDefault("fighter2_name", "Dragón");
        int fighter2Hp = ((Number) body.getOrDefault("fighter2_hp", 120)).intValue();
        int fighter2Atk = ((Number) body.getOrDefault("fighter2_atk", 30)).intValue();
        return new ExternalBattleInput(fighter1Name, fighter1Hp, fighter1Atk, fighter2Name, fighter2Hp, fighter2Atk);
    }

    public record ExternalBattleInput(String fighter1Name, int fighter1Hp, int fighter1Atk,
                                      String fighter2Name, int fighter2Hp, int fighter2Atk) {}
}
