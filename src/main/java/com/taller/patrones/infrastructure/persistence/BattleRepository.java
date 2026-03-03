package com.taller.patrones.infrastructure.persistence;

import com.taller.patrones.domain.Battle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Almacena las batallas activas en memoria.
 * <p>
 * Nota: BattleService hace new BattleRepository() cada vez. Si otro servicio
 * también creara su propio BattleRepository, ¿compartirían las batallas?
 */
public class BattleRepository {

    private static final Map<String, Battle> battles = new ConcurrentHashMap<>();

    public void save(String id, Battle battle) {
        battles.put(id, battle);
    }

    public Battle findById(String id) {
        return battles.get(id);
    }

    public void remove(String id) {
        battles.remove(id);
    }
}
