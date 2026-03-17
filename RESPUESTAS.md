## Ejercicios (enfoque escenario → patrón)

### 1. Añadir un nuevo tipo de ataque

**Situación:** Quieres añadir el ataque "Meteoro" (120 de poder, tipo especial). Abres `CombatEngine` y ves que tanto `createAttack()` como `calculateDamage()` tienen un `switch` que crece con cada ataque o tipo nuevo.

**Preguntas:**
- ¿Qué problema te encuentras al añadir "Meteoro"?
    - Incumples los principios SOLID (principalmente el Open/Extend)
- ¿Qué pasa si mañana piden 10 ataques más?
    - Deberías añadir mano a mano cada ataque nuevo.
- ¿Qué patrón permitiría añadir ataques **sin modificar** `CombatEngine`?
    - Factory

**Solución implementada:**
- Se creó la interfaz `AttackFactory` y la implementación `DefaultAttackFactory` con un registro `Map<String,Supplier<Attack>>` para instanciar ataques (incluyendo Meteoro) sin modificar el motor.
- `CombatEngine` ahora recibe una `AttackFactory` (por defecto usa `DefaultAttackFactory`) y su método `createAttack` delega allí, eliminando el `switch` y respetando Open/Closed.

**Pista:** Busca en `infrastructure/combat/CombatEngine.java`

---

### 2. Añadir una nueva fórmula de daño

**Situación:** Los ataques de tipo STATUS (veneno, parálisis) no deberían hacer daño directo. Pero en `calculateDamage()` el case STATUS devuelve `attacker.getAttack()` — algo no cuadra.

Además, te piden un nuevo tipo: "CRÍTICO", con fórmula `daño * 1.5` y 20% de probabilidad.

**Preguntas:**
- ¿Qué principio SOLID se viola al añadir otro `case` en el switch?
    - Open/Closed
- ¿Qué patrón permitiría tener fórmulas de daño intercambiables sin tocar el código existente?
    - Strategy
**Pista:** Cada tipo de ataque (NORMAL, SPECIAL, STATUS) tiene una fórmula distinta.

**Solución implementada:**
- Se añadió `AttackType.CRITICO` y un ataque "CRITICO" en la factoría.
- Se creó la interfaz `DamageStrategy` y estrategias concretas `NormalDamageStrategy`, `SpecialDamageStrategy`, `StatusDamageStrategy` (0 daño directo) y `CriticalDamageStrategy` (+50% daño con 20% probabilidad).
- `CombatEngine` ahora usa un `Map<AttackType, DamageStrategy>` (con `defaultStrategies()`) para delegar el cálculo, eliminando el `switch` y cumpliendo Open/Closed.

---

### 3. Crear personajes con muchas estadísticas

**Situación:** En `BattleService.startBattle()` creas personajes así:

```java
Character player = new Character("Héroe", 150, 25, 15, 20);
```

Ahora necesitas soportar: equipamiento, buffos temporales, clase (guerrero/mago). El constructor de `Character` empieza a tener 10+ parámetros. Algunos son opcionales.

**Preguntas:**
- ¿Qué problema tiene un constructor con muchos parámetros?
    - A la hora de crear un objeto nuevo puede resultar lioso realizar el constructor.
- ¿Cómo harías para que `new Character(...)` sea legible cuando hay valores por defecto?
    - Aplicando el `Builder`, haciendolo mucho más legible y con la posibilidad de omitir los opcionales y añadir validadores.
- ¿Qué patrón permite construir objetos complejos paso a paso?
    - Builder
**Pista:** Mira cómo se crean los personajes en `BattleService` y en el endpoint `/start/external`.
**Solución implementada:**
 - Se añadió `Character.builder()` con valores por defecto y setters fluidos; `BattleService` lo usa para instanciar personajes de forma legible y con opcionales.
---

### 4. Un único almacén de batallas

**Situación:** `BattleRepository` usa un `Map` estático para que funcione. Pero `BattleService` hace `new BattleRepository()` cada vez. Si otro equipo crea un `TournamentService` que también hace `new BattleRepository()`, ¿compartirían las batallas?

**Preguntas:**
- ¿Qué pasaría si dos clases crean su propio `BattleRepository` sin el `static`?
    - Cada clase tendría su propio mapa en memoria y no compartirían batallas.
- ¿Cómo asegurar que **toda la aplicación** use la misma instancia de almacenamiento?
    - Proveyendo una única instancia accesible.
- ¿Qué patrón garantiza una única instancia de una clase?
    - Singleton.

**Pista:** `infrastructure/persistence/BattleRepository.java`

**Solución implementada:**
- `BattleRepository` se convirtió en Singleton (constructor privado, `getInstance()`); el mapa de batallas es de instancia.
- `BattleService` usa `BattleRepository.getInstance()` en lugar de `new BattleRepository()`, asegurando un único almacén compartido.

---

### 5. Recibir datos de un API externo

**Situación:** El endpoint `POST /api/battle/start/external` recibe JSON con campos `fighter1_hp`, `fighter1_atk`, `fighter2_name`, etc. El controller hace el mapeo manual a `Character` y `Battle`.

Mañana llega otro proveedor con formato distinto: `player.health`, `player.attack`, `enemy.health`...

**Preguntas:**
- ¿Qué problema hay en poner la lógica de conversión en el controller?
    - El controller se acopla a formatos externos y duplica lógica por proveedor.
- ¿Cómo aislar la conversión "formato externo → nuestro dominio" para no ensuciar el controller?
    - Usando mapeadores específicos por proveedor para construir los objetos de dominio.
- ¿Qué patrón permite que un objeto "adaptado" se use como si fuera uno de los nuestros?
    - Adapter.

**Pista:** `interfaces/rest/BattleController.java` — método `startBattleFromExternal`

**Solución implementada:**
- Se creó `ExternalBattleAdapter` que recibe el JSON externo (claves `fighter1_*`, `fighter2_*`) y lo transforma en un DTO interno (`ExternalBattleInput`).
- `BattleController` usa el adaptador en `/start/external` para delegar la conversión y mantener el controller limpio y desacoplado del formato externo.

---

### 6. Notificar cuando ocurre daño

**Situación:** Necesitas:
- Enviar un evento a un sistema de analytics cada vez que hay daño
- Escribir en un log de auditoría
- Actualizar estadísticas en tiempo real

Ahora mismo solo existe `battle.log()`. Tendrías que añadir código en `BattleService.applyDamage()` para cada uno de estos casos.

**Preguntas:**
- ¿Qué pasa si añades 5 "suscriptores" más? ¿Cuántas líneas tocarías en `applyDamage()`?
- Terminarías metiendo más lógica en `applyDamage()` y tocando varias líneas por cada nuevo suscriptor, acoplando el servicio y haciéndolo difícil de mantener.
- ¿Cómo desacoplar "ejecutar ataque" de "notificar a quien le interese"?
- Publicando un evento de daño y dejando que observadores lo manejen sin conocer al emisor.
- ¿Qué patrón permite que varios objetos reaccionen a un evento sin que el emisor los conozca?
- Observer

**Pista:** El método `applyDamage` en `BattleService` es el único que sabe cuándo hay daño.

**Solución propuesta:**
- Definir un evento de dominio `DamageEvent` y una interfaz `DamageObserver`.
- `BattleService` actúa como `Subject`: mantiene una lista de observadores y, tras aplicar daño, notifica a todos.
- Implementar observadores concretos  sin modificar `BattleService` cuando aparezcan nuevos.

---

### 7. Deshacer el último ataque

**Situación:** Quieren la funcionalidad "Deshacer" — revertir el último ataque ejecutado.

Ahora el ataque se ejecuta directamente en `applyDamage()`. No hay registro de "qué se hizo".

**Preguntas:**
- ¿Qué tendrías que cambiar para poder "deshacer"?
- Envolver la lógica del ataque en un objeto que guarde el estado previo para poder revertirlo.
- ¿Cómo encapsular una acción (ataque) para poder ejecutarla, guardarla y revertirla?
- Creando un comando con métodos `execute()` y `undo()`, y guardándolo en un historial.
- ¿Qué patrón trata las acciones como objetos de primera clase?
- Command.

**Pista:** La lógica del ataque está en `BattleService.applyDamage()`.

**Solución implementada:**
- Se creó la interfaz `BattleCommand` y el comando `AttackCommand` que ejecuta y revierte el ataque.
- Encontré la clase `Deque`, el cual poseé una complejidad O(1) para este tipo de interacciones, y me parecía curioso implementarlo 
- `BattleService` mantiene un historial por batalla y expone `undoLastAttack()`.
- Se añadió el endpoint `POST /api/battle/{battleId}/undo` para deshacer el último ataque.

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
