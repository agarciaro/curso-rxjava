# Ejercicios Tema 7: Concurrencia y Schedulers

## Ejercicio 7.1: Schedulers Básicos

**Nivel:** Básico  
**Tiempo estimado:** 25 minutos

### Enunciado
Implementa las siguientes operaciones usando el Scheduler apropiado:

1. Lectura de 5 archivos simulados (Schedulers.io())
2. Cálculo del factorial de números grandes (Schedulers.computation())
3. Múltiples tareas pequeñas en un único thread (Schedulers.single())
4. Operación que requiere un nuevo thread (Schedulers.newThread())

Para cada caso, imprime el nombre del thread que ejecuta la operación.

### Resultado Esperado
```
=== Lectura de archivos (I/O) ===
Leyendo archivo1.txt en: RxCachedThreadScheduler-1
Leyendo archivo2.txt en: RxCachedThreadScheduler-2
Leyendo archivo3.txt en: RxCachedThreadScheduler-1
...

=== Cálculos (Computation) ===
Factorial de 10 en: RxComputationThreadPool-1
Factorial de 15 en: RxComputationThreadPool-2
Factorial de 20 en: RxComputationThreadPool-3
...

=== Tareas secuenciales (Single) ===
Tarea 1 en: RxSingleScheduler-1
Tarea 2 en: RxSingleScheduler-1
Tarea 3 en: RxSingleScheduler-1

=== Nuevo thread ===
Operación en: RxNewThreadScheduler-1
```

---

## Ejercicio 7.2: subscribeOn vs observeOn

**Nivel:** Intermedio  
**Tiempo estimado:** 30 minutos

### Enunciado
Crea un pipeline que demuestre la diferencia entre `subscribeOn()` y `observeOn()`:

1. Emisión de datos (debe estar en io())
2. Primera transformación (debe estar en computation())
3. Segunda transformación (debe estar en single())
4. Suscripción final (debe estar en el hilo principal)

Implementa logging detallado mostrando el thread en cada etapa.

### Resultado Esperado
```
=== Pipeline con Schedulers ===

[main] Iniciando suscripción...

[RxCachedThreadScheduler-1] Emitiendo: dato1
[RxCachedThreadScheduler-1] Emitiendo: dato2
[RxCachedThreadScheduler-1] Emitiendo: dato3

[RxComputationThreadPool-1] Primera transformación: dato1 → DATO1
[RxComputationThreadPool-1] Primera transformación: dato2 → DATO2
[RxComputationThreadPool-1] Primera transformación: dato3 → DATO3

[RxSingleScheduler-1] Segunda transformación: DATO1 → [DATO1]
[RxSingleScheduler-1] Segunda transformación: DATO2 → [DATO2]
[RxSingleScheduler-1] Segunda transformación: DATO3 → [DATO3]

[main] Recibido: [DATO1]
[main] Recibido: [DATO2]
[main] Recibido: [DATO3]
```

---

## Ejercicio 7.3: Procesamiento Paralelo

**Nivel:** Intermedio  
**Tiempo estimado:** 35 minutos

### Enunciado
Implementa un sistema que procese 100 URLs en paralelo:
- Descarga el contenido de cada URL (simulado con delay)
- Extrae el título
- Cuenta las palabras
- Genera un reporte consolidado

Compara el rendimiento:
1. Sin paralelización (secuencial)
2. Con `flatMap()` y schedulers
3. Con `parallel()` de Flowable

### Resultado Esperado
```
=== Procesamiento Secuencial ===
Procesando URL 1/100...
Procesando URL 2/100...
...
Tiempo total: 10,000ms

=== Procesamiento Paralelo (flatMap) ===
Procesando URL 1/100 en: RxComputationThreadPool-1
Procesando URL 2/100 en: RxComputationThreadPool-2
Procesando URL 3/100 en: RxComputationThreadPool-3
Procesando URL 4/100 en: RxComputationThreadPool-4
...
Tiempo total: 1,250ms

=== Procesamiento Paralelo (parallel()) ===
Tiempo total: 800ms

=== Resultados ===
URLs procesadas: 100
Títulos extraídos: 98
Errores: 2
Total palabras: 45,678
Mejora de rendimiento: 12.5x más rápido
```

---

## Ejercicio 7.4: Sincronización y Coordinación

**Nivel:** Avanzado  
**Tiempo estimado:** 40 minutos

### Enunciado
Implementa un sistema de descarga de archivos que:
1. Descarga 3 archivos en paralelo (parte1, parte2, parte3)
2. Solo cuando los 3 completen, los combina
3. Procesa el archivo combinado
4. Guarda el resultado final

Requisitos:
- Usar diferentes Schedulers según la operación
- Implementar timeout de 5 segundos por descarga
- Si una parte falla, reintentar hasta 3 veces
- Coordinar usando `zip()` o `merge()`
- Mostrar progreso en tiempo real

### Resultado Esperado
```
=== Descarga de Archivo Grande ===

Descargando partes en paralelo...
[Thread-1] Parte 1: ▓▓▓░░░░░░░ 30%
[Thread-2] Parte 2: ▓▓░░░░░░░░ 20%
[Thread-3] Parte 3: ▓▓▓▓░░░░░░ 40%

[Thread-1] Parte 1: ▓▓▓▓▓▓░░░░ 60%
[Thread-2] Parte 2: ▓▓▓▓▓░░░░░ 50%
[Thread-3] Parte 3: ▓▓▓▓▓▓▓▓░░ 80%

[Thread-1] Parte 1: ▓▓▓▓▓▓▓▓▓▓ 100% ✓
[Thread-2] Parte 2: ▓▓▓▓▓▓▓▓▓▓ 100% ✓
[Thread-3] Parte 3: ▓▓▓▓▓▓▓▓▓▓ 100% ✓

Todas las partes descargadas
Combinando partes... ✓
Procesando archivo... ✓
Guardando resultado... ✓

✓ Descarga completada en 2.3 segundos
```

---

## Ejercicio 7.5: Event Loop Pattern

**Nivel:** Avanzado  
**Tiempo estimado:** 45 minutos

### Enunciado
Implementa un Event Loop reactivo que:
- Escuche eventos de múltiples fuentes
- Procese eventos en orden pero de forma no bloqueante
- Tenga un queue de eventos pendientes
- Muestre estadísticas en tiempo real (eventos/segundo)
- Permita pausar/reanudar el procesamiento

Fuentes de eventos:
1. Eventos de UI (rápidos, cada 50ms)
2. Eventos de red (medios, cada 200ms)
3. Eventos de sistema (lentos, cada 1000ms)

### Resultado Esperado
```
=== Event Loop Reactivo ===

[00:00.050] UI: Click en botón
[00:00.100] UI: Movimiento de mouse
[00:00.150] UI: Tecla presionada
[00:00.200] NET: Respuesta HTTP recibida
[00:00.250] UI: Click en menú
...
[00:01.000] SYS: Checkpoint de sistema

=== Estadísticas (actualización en tiempo real) ===
Eventos procesados: 127
Velocidad: 12.7 eventos/seg
Queue pendientes: 3
CPU: UI=80%, NET=15%, SYS=5%

[Usuario presiona PAUSA]
⏸️ Event Loop pausado
   Eventos en queue: 15
   
[Usuario presiona REANUDAR]
▶️ Event Loop reanudado
   Procesando eventos pendientes...
```

---

## Soluciones de Referencia

Las soluciones están disponibles en:
- `src/main/java/com/formadoresit/rxjava/tema7/soluciones/`

## Evaluación

| Ejercicio | Puntos | Criterios |
|-----------|--------|-----------|
| 7.1 | 15 | Scheduler correcto por operación |
| 7.2 | 20 | Entendimiento de subscribeOn/observeOn |
| 7.3 | 25 | Paralelización efectiva, medición |
| 7.4 | 25 | Coordinación, manejo de errores |
| 7.5 | 15 | Arquitectura event loop, estadísticas |
| **Total** | **100** | |

## Criterios de Evaluación

- **Uso correcto de Schedulers (30%):** Selección apropiada según tipo de operación
- **Performance (25%):** Mejoras medibles en tiempo de ejecución
- **Manejo de concurrencia (25%):** Thread-safety, coordinación correcta
- **Código y logging (20%):** Código limpio, logging informativo

## Recursos Adicionales

- Schedulers en RxJava: http://reactivex.io/documentation/scheduler.html
- Concurrency en RxJava: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#threading
- Ejemplos en: `src/main/java/com/formadoresit/rxjava/tema7/`

