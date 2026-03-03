# Taller Patrones de Diseño — Combat Simulator

Este proyecto es un **simulador de batalla por turnos** que funciona correctamente. Sin embargo, el código tiene decisiones de diseño que podrían mejorarse. Tu objetivo no es implementar métodos, sino **identificar qué patrón aplicar** en cada situación.

## Cómo ejecutar

```bash
mvn spring-boot:run
```

Abre http://localhost:8080

---

## Estructura del proyecto

```
src/main/java/com/taller/patrones/
├── domain/              # Modelo: Character, Attack, Battle
├── application/         # Casos de uso: BattleService
├── infrastructure/      # Implementaciones: CombatEngine, BattleRepository
└── interfaces/rest/     # API REST: BattleController
```

---

## Ejercicios (enfoque escenario → patrón)

### 1. Añadir un nuevo tipo de ataque

**Situación:** Quieres añadir el ataque "Meteoro" (120 de poder, tipo especial). Abres `CombatEngine` y ves que tanto `createAttack()` como `calculateDamage()` tienen un `switch` que crece con cada ataque o tipo nuevo.

**Preguntas:**
- ¿Qué problema te encuentras al añadir "Meteoro"?
- ¿Qué pasa si mañana piden 10 ataques más?
- ¿Qué patrón permitiría añadir ataques **sin modificar** `CombatEngine`?

**Pista:** Busca en `infrastructure/combat/CombatEngine.java`

---

### 2. Añadir una nueva fórmula de daño

**Situación:** Los ataques de tipo STATUS (veneno, parálisis) no deberían hacer daño directo. Pero en `calculateDamage()` el case STATUS devuelve `attacker.getAttack()` — algo no cuadra.

Además, te piden un nuevo tipo: "CRÍTICO", con fórmula `daño * 1.5` y 20% de probabilidad.

**Preguntas:**
- ¿Qué principio SOLID se viola al añadir otro `case` en el switch?
- ¿Qué patrón permitiría tener fórmulas de daño intercambiables sin tocar el código existente?

**Pista:** Cada tipo de ataque (NORMAL, SPECIAL, STATUS) tiene una fórmula distinta.

---

### 3. Crear personajes con muchas estadísticas

**Situación:** En `BattleService.startBattle()` creas personajes así:

```java
Character player = new Character("Héroe", 150, 25, 15, 20);
```

Ahora necesitas soportar: equipamiento, buffos temporales, clase (guerrero/mago). El constructor de `Character` empieza a tener 10+ parámetros. Algunos son opcionales.

**Preguntas:**
- ¿Qué problema tiene un constructor con muchos parámetros?
- ¿Cómo harías para que `new Character(...)` sea legible cuando hay valores por defecto?
- ¿Qué patrón permite construir objetos complejos paso a paso?

**Pista:** Mira cómo se crean los personajes en `BattleService` y en el endpoint `/start/external`.

---

### 4. Un único almacén de batallas

**Situación:** `BattleRepository` usa un `Map` estático para que funcione. Pero `BattleService` hace `new BattleRepository()` cada vez. Si otro equipo crea un `TournamentService` que también hace `new BattleRepository()`, ¿compartirían las batallas?

**Preguntas:**
- ¿Qué pasaría si dos clases crean su propio `BattleRepository` sin el `static`?
- ¿Cómo asegurar que **toda la aplicación** use la misma instancia de almacenamiento?
- ¿Qué patrón garantiza una única instancia de una clase?

**Pista:** `infrastructure/persistence/BattleRepository.java`

---

### 5. Recibir datos de un API externo

**Situación:** El endpoint `POST /api/battle/start/external` recibe JSON con campos `fighter1_hp`, `fighter1_atk`, `fighter2_name`, etc. El controller hace el mapeo manual a `Character` y `Battle`.

Mañana llega otro proveedor con formato distinto: `player.health`, `player.attack`, `enemy.health`...

**Preguntas:**
- ¿Qué problema hay en poner la lógica de conversión en el controller?
- ¿Cómo aislar la conversión "formato externo → nuestro dominio" para no ensuciar el controller?
- ¿Qué patrón permite que un objeto "adaptado" se use como si fuera uno de los nuestros?

**Pista:** `interfaces/rest/BattleController.java` — método `startBattleFromExternal`

---

### 6. Notificar cuando ocurre daño

**Situación:** Necesitas:
- Enviar un evento a un sistema de analytics cada vez que hay daño
- Escribir en un log de auditoría
- Actualizar estadísticas en tiempo real

Ahora mismo solo existe `battle.log()`. Tendrías que añadir código en `BattleService.applyDamage()` para cada uno de estos casos.

**Preguntas:**
- ¿Qué pasa si añades 5 "suscriptores" más? ¿Cuántas líneas tocarías en `applyDamage()`?
- ¿Cómo desacoplar "ejecutar ataque" de "notificar a quien le interese"?
- ¿Qué patrón permite que varios objetos reaccionen a un evento sin que el emisor los conozca?

**Pista:** El método `applyDamage` en `BattleService` es el único que sabe cuándo hay daño.

---

### 7. Deshacer el último ataque

**Situación:** Quieren la funcionalidad "Deshacer" — revertir el último ataque ejecutado.

Ahora el ataque se ejecuta directamente en `applyDamage()`. No hay registro de "qué se hizo".

**Preguntas:**
- ¿Qué tendrías que cambiar para poder "deshacer"?
- ¿Cómo encapsular una acción (ataque) para poder ejecutarla, guardarla y revertirla?
- ¿Qué patrón trata las acciones como objetos de primera clase?

**Pista:** La lógica del ataque está en `BattleService.applyDamage()`.

---

### 8. Simplificar la API del combate

**Situación:** Para ejecutar un ataque, el controller llama a `battleService.executePlayerAttack()` o `executeEnemyAttack()`, que a su vez usa `CombatEngine`, aplica daño, cambia turno, etc. Un cliente externo que quiera integrarse tendría que conocer `BattleService`, `CombatEngine`, `BattleRepository`...

**Preguntas:**
- ¿Qué problema hay en exponer muchos detalles internos a quien solo quiere "hacer un ataque"?
- ¿Qué patrón ofrece una interfaz simple que oculta la complejidad del subsistema?

**Pista:** Piensa en qué necesita saber un cliente para ejecutar un ataque.

---

### 9. Ataques compuestos (combo)

**Situación:** Quieres un ataque "Combo Triple" que ejecuta Tackle + Slash + Fireball en secuencia.

Ahora cada ataque es independiente. No hay forma de agrupar varios.

**Preguntas:**
- ¿Cómo representar "un ataque que son varios ataques"?
- ¿Qué patrón permite tratar un grupo de objetos igual que un objeto individual?

**Pista:** `Attack` es una unidad. ¿Cómo hacer que varios `Attack` se comporten como uno?

---

## Resumen: Patrones del taller

| Patrón   | Situación típica                                      |
|----------|--------------------------------------------------------|
| Singleton| Una única instancia en toda la aplicación              |
| Factory  | Crear objetos sin conocer la clase concreta           |
| Builder  | Construir objetos con muchos parámetros opcionales    |
| Adapter  | Usar una interfaz externa como si fuera la nuestra    |
| Strategy | Algoritmos intercambiables (fórmulas de daño)        |
| Observer | Notificar a varios sin acoplar emisor y receptores     |
| Command  | Encapsular acciones para ejecutar, deshacer, encolar  |
| Facade   | Interfaz simple sobre un subsistema complejo          |
| Composite| Tratar grupos como elementos individuales             |