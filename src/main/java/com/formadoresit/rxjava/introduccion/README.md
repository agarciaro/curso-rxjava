# 📚 INTRODUCCIÓN - Patrón Publisher/Subscriber con Java Puro

Este paquete contiene ejemplos que implementan el patrón **Publisher/Subscriber** usando **solo Java estándar** (sin RxJava). El objetivo es entender los fundamentos de la programación reactiva antes de usar RxJava.

## 🎯 Objetivo

Entender **cómo funciona internamente** la programación reactiva implementando los patrones desde cero. Esto te dará una base sólida antes de usar RxJava.

---

## 📋 Ejemplos Incluidos

### ✅ Ejemplo 01: Publisher/Subscriber Básico
**Archivo:** `Ejemplo01_PublisherSubscriberBasico.java`

Implementa el patrón fundamental Publisher/Subscriber:
- Define las interfaces `Publisher<T>` y `Subscriber<T>`
- Implementa un Publisher que emite números
- Crea diferentes tipos de Subscribers
- Demuestra múltiples suscripciones

**Conceptos:**
- Push-based vs Pull-based
- Flujo de datos unidireccional
- onNext(), onComplete(), onError()

**Ejecutar:**
```bash
mvn exec:java -Dexec.mainClass=com.formadoresit.rxjava.introduccion.Ejemplo01_PublisherSubscriberBasico
```

---

### ✅ Ejemplo 02: Publisher con Cancelación
**Archivo:** `Ejemplo02_PublisherConCancelacion.java`

Añade el concepto de **Subscription** para controlar el flujo:
- Implementa la interfaz `Subscription`
- Permite cancelar la emisión de datos
- Subscriber puede auto-cancelarse con condiciones
- Previene emisión innecesaria de datos

**Conceptos:**
- Subscription (similar a Disposable en RxJava)
- Control de flujo
- Cancelación explícita
- Gestión de recursos

**Ejecutar:**
```bash
mvn exec:java -Dexec.mainClass=com.formadoresit.rxjava.introduccion.Ejemplo02_PublisherConCancelacion
```

---

### ✅ Ejemplo 03: Publisher con Transformaciones
**Archivo:** `Ejemplo03_PublisherConTransformacion.java`

Implementa **operadores** para transformar el flujo de datos:
- Operador `map()` - Transforma cada elemento
- Operador `filter()` - Filtra elementos
- Composición de operadores (pipeline)
- Inmutabilidad (cada operador crea nuevo Publisher)

**Conceptos:**
- Operadores de transformación
- Pipeline reactivo
- Composición funcional
- Inmutabilidad

**Ejecutar:**
```bash
mvn exec:java -Dexec.mainClass=com.formadoresit.rxjava.introduccion.Ejemplo03_PublisherConTransformacion
```

---

### ✅ Ejemplo 04: Publisher Asíncrono
**Archivo:** `Ejemplo04_PublisherAsincrono.java`

Implementa emisión **asíncrona** usando threads:
- Publisher síncrono (bloquea el thread)
- Publisher asíncrono (no bloquea)
- Ejecución concurrente
- Demuestra la naturaleza no bloqueante

**Conceptos:**
- Síncrono vs Asíncrono
- Bloqueante vs No bloqueante
- ExecutorService y threads
- Concurrencia

**Ejecutar:**
```bash
mvn exec:java -Dexec.mainClass=com.formadoresit.rxjava.introduccion.Ejemplo04_PublisherAsincrono
```

---

### ✅ Ejemplo 05: Push vs Pull - Comparación Completa
**Archivo:** `Ejemplo05_CompletoPushVsPull.java`

Compara el modelo tradicional **PULL** (Iterator) con el modelo **PUSH** (Reactivo):
- Implementación con Iterator (PULL)
- Implementación con Publisher/Subscriber (PUSH)
- Tabla comparativa de diferencias
- Casos de uso de cada modelo

**Conceptos:**
- Modelo PULL (imperativo)
- Modelo PUSH (reactivo)
- Cuándo usar cada uno
- Ventajas de programación reactiva

**Ejecutar:**
```bash
mvn exec:java -Dexec.mainClass=com.formadoresit.rxjava.introduccion.Ejemplo05_CompletoPushVsPull
```

---

### ✅ Ejemplo 06: Imperativa vs Reactiva - Benchmark de Performance
**Archivo:** `Ejemplo06_ImperativaVsReactivaPerformance.java`

Comparación exhaustiva de **rendimiento** entre programación imperativa y reactiva:
- Implementación imperativa tradicional
- Implementación con Java Streams
- Implementación reactiva con Publisher/Subscriber
- Medición de tiempos de ejecución
- Comparación de throughput
- Múltiples escenarios de benchmark

**Conceptos:**
- Medición de performance
- Benchmarking correcto (warm-up JVM)
- Throughput y latencia
- Trade-offs de cada enfoque
- Casos de uso ideales

**Ejecutar:**
```bash
mvn exec:java -Dexec.mainClass=com.formadoresit.rxjava.introduccion.Ejemplo06_ImperativaVsReactivaPerformance
```

**Resultados típicos:**
- **Imperativa**: Más rápida para operaciones simples en memoria
- **Streams**: Mejor rendimiento con optimizaciones de Java
- **Reactiva**: Ventaja en operaciones I/O y concurrentes (no mostrado en CPU-bound)

**Nota importante:** Este ejemplo muestra operaciones CPU-bound. La **verdadera ventaja reactiva** se ve en:
- Operaciones I/O (base de datos, APIs REST)
- Múltiples operaciones concurrentes
- Streaming de datos en tiempo real
- Backpressure y control de flujo

---

### ✅ Ejemplo 07: Ventaja Real de Programación Reactiva ⭐ **MÁS IMPORTANTE**
**Archivo:** `Ejemplo07_VentajaReactivaIO.java`

Demuestra la **VERDADERA VENTAJA** de programación reactiva con un caso de uso real:
- Sistema de recomendación de productos e-commerce
- 4 llamadas a APIs/servicios diferentes (I/O bound)
- Comparación imperativa (secuencial) vs reactiva (paralela)
- Operaciones que se ejecutan EN PARALELO con RxJava

**Conceptos:**
- Operaciones I/O no bloqueantes
- Observable.zip() para ejecutar en paralelo
- Schedulers.io() para operaciones I/O
- Composición de múltiples streams
- Timeouts y manejo de errores

**Ejecutar:**
```bash
mvn exec:java -Dexec.mainClass=com.formadoresit.rxjava.introduccion.Ejemplo07_VentajaReactivaIO
```

**Resultados típicos:**
- **Imperativa**: 2,600ms (secuencial: 500 + 800 + 600 + 700)
- **Reactiva**: 800-1,200ms (paralela: max de todos)
- **Speedup**: **2-3x más rápida** ⚡
- **Mejora**: **55-70% menos tiempo**

**Por qué es más rápida:**
```
IMPERATIVA (Secuencial):
API 1 [===500ms===]
API 2                [========800ms========]
API 3                                       [====600ms====]
API 4                                                      [=======700ms=======]
Total: 2600ms

REACTIVA (Paralela):
API 1 [===500ms===]
API 2 [========800ms========]
API 3 [====600ms====]
API 4 [=======700ms=======]
Total: 800ms (la más lenta)
```

**💡 Este es el ejemplo MÁS IMPORTANTE** porque muestra:
- La ventaja REAL de programación reactiva
- Cuándo usar RxJava en producción
- Por qué RxJava es indispensable para microservicios
- Cómo el paralelismo mejora dramáticamente el rendimiento

---

## 🎓 Flujo de Aprendizaje Recomendado

1. **Ejemplo 01** → Entender el patrón básico
2. **Ejemplo 02** → Agregar control de flujo con Subscription
3. **Ejemplo 03** → Comprender operadores y transformaciones
4. **Ejemplo 04** → Ver la naturaleza asíncrona
5. **Ejemplo 05** → Comparar con programación tradicional
6. **Ejemplo 06** → Benchmark de performance con operaciones CPU
7. **Ejemplo 07** ⭐ → **Ventaja real con operaciones I/O** (el más importante)

---

## 📊 Conceptos Fundamentales

### 1. Patrón Publisher/Subscriber

```
Publisher          Subscriber
    |                  |
    |    onNext(1)     |
    |----------------->|
    |    onNext(2)     |
    |----------------->|
    |    onNext(3)     |
    |----------------->|
    |   onComplete()   |
    |----------------->|
```

### 2. Interfaces Básicas

```java
interface Publisher<T> {
    void subscribe(Subscriber<T> subscriber);
}

interface Subscriber<T> {
    void onNext(T item);        // Nuevo elemento
    void onComplete();          // Flujo terminado
    void onError(Throwable e);  // Error ocurrido
}

interface Subscription {
    void cancel();  // Cancelar suscripción
}
```

### 3. Modelo PUSH vs PULL

| Aspecto | PULL (Iterator) | PUSH (Publisher) |
|---------|----------------|------------------|
| Control | Consumidor | Productor |
| Método | next(), hasNext() | onNext() |
| Naturaleza | Síncrono | Asíncrono |
| Bloqueo | Sí | No |
| Ideal para | Colecciones | Streams de datos |

---

## 🔗 Relación con RxJava

Estos ejemplos implementan manualmente lo que RxJava hace automáticamente:

| Nuestro Ejemplo | RxJava Equivalente |
|----------------|-------------------|
| `Publisher<T>` | `Observable<T>` |
| `Subscriber<T>` | `Observer<T>` |
| `Subscription` | `Disposable` |
| `map()` | `Observable.map()` |
| `filter()` | `Observable.filter()` |
| Async Publisher | `subscribeOn()` |

**Próximo paso:** Ahora que entiendes los fundamentos, pasarás al **Tema 1 (Fundamentos)** y luego al **Tema 2 (RxJava)**, donde verás cómo RxJava implementa todo esto de forma más potente.

---

## 💡 ¿Por qué hacer esto manualmente?

1. **Entender cómo funciona** - No usar RxJava como "magia negra"
2. **Apreciar RxJava** - Ver todo lo que hace por ti
3. **Base sólida** - Entender los conceptos fundamentales
4. **Debug mejor** - Saber qué pasa internamente
5. **Decisiones informadas** - Saber cuándo usar programación reactiva

---

## 🚀 Siguiente Paso

Después de completar estos ejemplos, continúa con:
- **Tema 1: Fundamentos** - Conceptos de programación funcional
- **Tema 2: Introducción a RxJava 2** - Tu primer Observable

---

## 📚 Recursos Adicionales

- [Reactive Manifesto](https://www.reactivemanifesto.org/)
- [Reactive Streams Specification](https://www.reactive-streams.org/)
- [RxJava Documentation](https://github.com/ReactiveX/RxJava)

---

**¡Feliz aprendizaje de programación reactiva! 🎉**

