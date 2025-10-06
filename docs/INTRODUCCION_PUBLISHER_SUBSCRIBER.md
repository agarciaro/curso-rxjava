# 📘 Guía: Patrón Publisher/Subscriber - Fundamentos de Programación Reactiva

## 🎯 Objetivo

Esta guía explica el **patrón Publisher/Subscriber**, que es la base de toda la programación reactiva y RxJava. Antes de usar RxJava, es crucial entender cómo funciona este patrón.

---

## 📋 ¿Qué es el Patrón Publisher/Subscriber?

El patrón **Publisher/Subscriber** (Publicador/Suscriptor) es un patrón de diseño donde:

- **Publisher (Publicador)**: Produce y emite datos
- **Subscriber (Suscriptor)**: Consume y reacciona a los datos

### Características Clave

1. **Desacoplamiento**: Publisher y Subscriber no se conocen directamente
2. **Push-based**: El Publisher EMPUJA datos al Subscriber
3. **Asíncrono**: Los datos pueden emitirse en cualquier momento
4. **Múltiples suscriptores**: Un Publisher puede tener varios Subscribers

---

## 🔄 Modelo PUSH vs PULL

### Modelo PULL (Tradicional - Iterator)

```java
// El CONSUMIDOR controla el flujo
Iterator<Integer> iterator = list.iterator();
while (iterator.hasNext()) {
    Integer value = iterator.next();  // PIDE el siguiente
    System.out.println(value);
}
```

**Características:**
- ❌ El consumidor PIDE datos
- ❌ Síncrono y bloqueante
- ❌ El consumidor debe esperar
- ❌ Difícil de hacer asíncrono

### Modelo PUSH (Reactivo - Publisher)

```java
// El PRODUCTOR controla el flujo
publisher.subscribe(new Subscriber<Integer>() {
    @Override
    public void onNext(Integer value) {  // REACCIONA cuando llega
        System.out.println(value);
    }
    
    @Override
    public void onComplete() {
        System.out.println("Completado");
    }
});
```

**Características:**
- ✅ El productor ENVÍA datos
- ✅ Asíncrono y no bloqueante
- ✅ El consumidor REACCIONA
- ✅ Diseñado para operaciones asíncronas

---

## 🏗️ Arquitectura del Patrón

```
┌─────────────┐
│  Publisher  │ 
│   (Fuente)  │
└──────┬──────┘
       │ subscribe()
       ↓
┌─────────────┐
│ Subscriber  │
│ (Consumidor)│
└─────────────┘

Flujo de datos:
Publisher → onNext(1) → Subscriber
Publisher → onNext(2) → Subscriber
Publisher → onNext(3) → Subscriber
Publisher → onComplete() → Subscriber
```

---

## 📝 Interfaces Fundamentales

### Publisher<T>

```java
interface Publisher<T> {
    void subscribe(Subscriber<T> subscriber);
}
```

**Responsabilidades:**
- Aceptar suscripciones de Subscribers
- Emitir elementos a los Subscribers
- Notificar completado o errores

### Subscriber<T>

```java
interface Subscriber<T> {
    void onNext(T item);           // Recibe un elemento
    void onComplete();             // Stream completado
    void onError(Throwable error); // Error ocurrido
}
```

**Responsabilidades:**
- Recibir y procesar elementos
- Manejar completado del stream
- Manejar errores

### Subscription

```java
interface Subscription {
    void cancel();  // Cancelar la suscripción
}
```

**Responsabilidades:**
- Representar la conexión activa
- Permitir cancelación
- Controlar el flujo de datos

---

## 🔢 Ejemplo Completo Paso a Paso

### 1. Definir las Interfaces

```java
interface Publisher<T> {
    void subscribe(Subscriber<T> subscriber);
}

interface Subscriber<T> {
    void onNext(T item);
    void onComplete();
    void onError(Throwable error);
}
```

### 2. Implementar un Publisher

```java
class NumberPublisher implements Publisher<Integer> {
    private final int count;
    
    public NumberPublisher(int count) {
        this.count = count;
    }
    
    @Override
    public void subscribe(Subscriber<Integer> subscriber) {
        try {
            // Emitir números
            for (int i = 1; i <= count; i++) {
                subscriber.onNext(i);
            }
            // Notificar completado
            subscriber.onComplete();
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }
}
```

### 3. Implementar un Subscriber

```java
class PrintSubscriber implements Subscriber<Integer> {
    @Override
    public void onNext(Integer item) {
        System.out.println("Recibido: " + item);
    }
    
    @Override
    public void onComplete() {
        System.out.println("Completado!");
    }
    
    @Override
    public void onError(Throwable error) {
        System.err.println("Error: " + error.getMessage());
    }
}
```

### 4. Usar el Patrón

```java
public static void main(String[] args) {
    // Crear el Publisher
    Publisher<Integer> publisher = new NumberPublisher(5);
    
    // Crear el Subscriber
    Subscriber<Integer> subscriber = new PrintSubscriber();
    
    // Suscribirse (iniciar el flujo)
    publisher.subscribe(subscriber);
}
```

**Salida:**
```
Recibido: 1
Recibido: 2
Recibido: 3
Recibido: 4
Recibido: 5
Completado!
```

---

## 🎨 Operadores de Transformación

Los operadores permiten transformar el flujo de datos:

### Operador MAP

```java
interface Publisher<T> {
    <R> Publisher<R> map(Function<T, R> mapper);
}

// Uso
Publisher<Integer> numbers = new NumberPublisher(5);
Publisher<Integer> doubled = numbers.map(x -> x * 2);

doubled.subscribe(new PrintSubscriber());
// Salida: 2, 4, 6, 8, 10
```

### Operador FILTER

```java
interface Publisher<T> {
    Publisher<T> filter(Predicate<T> predicate);
}

// Uso
Publisher<Integer> numbers = new NumberPublisher(10);
Publisher<Integer> evenOnly = numbers.filter(x -> x % 2 == 0);

evenOnly.subscribe(new PrintSubscriber());
// Salida: 2, 4, 6, 8, 10
```

### Composición (Pipeline)

```java
Publisher<Integer> result = new NumberPublisher(10)
    .filter(x -> x % 2 == 0)  // Solo pares
    .map(x -> x * x);          // Elevar al cuadrado

result.subscribe(new PrintSubscriber());
// Salida: 4, 16, 36, 64, 100
```

---

## ⚡ Naturaleza Asíncrona

### Publisher Síncrono (Bloquea)

```java
class SyncPublisher implements Publisher<Integer> {
    @Override
    public void subscribe(Subscriber<Integer> subscriber) {
        // Se ejecuta en el thread del llamador
        for (int i = 1; i <= 5; i++) {
            subscriber.onNext(i);
        }
        subscriber.onComplete();
        // Bloquea hasta terminar
    }
}
```

### Publisher Asíncrono (No Bloquea)

```java
class AsyncPublisher implements Publisher<Integer> {
    private ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public void subscribe(Subscriber<Integer> subscriber) {
        // Ejecutar en thread diferente
        executor.submit(() -> {
            for (int i = 1; i <= 5; i++) {
                subscriber.onNext(i);
            }
            subscriber.onComplete();
        });
        // Retorna inmediatamente (no bloquea)
    }
}
```

---

## 🛑 Control de Flujo con Subscription

```java
interface Subscription {
    void cancel();
}

interface Subscriber<T> {
    void onSubscribe(Subscription subscription);
    void onNext(T item);
    void onComplete();
    void onError(Throwable error);
}

// Subscriber que se auto-cancela
class LimitedSubscriber implements Subscriber<Integer> {
    private Subscription subscription;
    private int count = 0;
    
    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
    }
    
    @Override
    public void onNext(Integer item) {
        System.out.println(item);
        count++;
        
        if (count >= 5) {
            subscription.cancel();  // Detener después de 5 elementos
        }
    }
}
```

---

## 📊 Comparación: Java Tradicional vs Reactivo

### Java Tradicional (Imperativo)

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// PULL: Pedimos cada elemento
for (Integer num : numbers) {
    int doubled = num * 2;
    if (doubled > 5) {
        System.out.println(doubled);
    }
}
```

### Java Reactivo (Declarativo)

```java
Publisher<Integer> numbers = new NumberPublisher(5);

// PUSH: Reaccionamos cuando llegan
numbers
    .map(x -> x * 2)
    .filter(x -> x > 5)
    .subscribe(new PrintSubscriber());
```

---

## 🎯 Ventajas del Modelo Reactivo

1. **Asíncrono por diseño** - No bloquea threads
2. **Composición funcional** - Encadenar operaciones fácilmente
3. **Manejo de errores unificado** - onError() centralizado
4. **Control de flujo** - Subscription permite cancelar
5. **Escalabilidad** - Maneja muchas conexiones concurrentes
6. **Backpressure** - Control cuando el consumidor es lento

---

## 🚀 Relación con RxJava

Todo lo que hicimos manualmente, **RxJava lo hace por ti**:

| Concepto Manual | RxJava Equivalente |
|----------------|-------------------|
| `Publisher<T>` | `Observable<T>` |
| `Subscriber<T>` | `Observer<T>` |
| `Subscription` | `Disposable` |
| `map()` manual | `Observable.map()` |
| `filter()` manual | `Observable.filter()` |
| Async Publisher | `subscribeOn(Schedulers.io())` |

**Ventajas de RxJava:**
- ✅ +300 operadores predefinidos
- ✅ Manejo automático de threads
- ✅ Backpressure integrado
- ✅ Combinación de múltiples streams
- ✅ Manejo de errores avanzado
- ✅ Testing utilities

---

## 📚 Resumen de Conceptos

### Patrones Fundamentales

1. **Publisher/Subscriber** - Patrón base
2. **Push vs Pull** - Diferencia clave
3. **Subscription** - Control de flujo
4. **Operadores** - Transformaciones
5. **Asíncrono** - Naturaleza no bloqueante

### Flujo Típico

```
1. Crear Publisher
2. Aplicar operadores (map, filter, etc.)
3. Crear Subscriber
4. subscribe() - Iniciar el flujo
5. Publisher emite datos
6. Subscriber reacciona
7. onComplete() o onError()
```

---

## 🎓 Próximos Pasos

Ahora que entiendes los fundamentos, puedes proceder a:

1. **Introducción (Paquete)** - Implementaciones manuales ✅
2. **Tema 1: Fundamentos** - Programación funcional
3. **Tema 2: RxJava** - Observable y Observer
4. **Tema 3: Operadores** - Transformaciones con RxJava

---

## 💡 Consejos

1. **Practica los ejemplos** - Ejecuta cada uno y entiende la salida
2. **Modifica el código** - Experimenta con cambios
3. **Compara** - Ve las diferencias entre síncrono y asíncrono
4. **Piensa reactivo** - Cambia tu mentalidad de PULL a PUSH

---

**¡Ahora estás listo para dominar RxJava! 🚀**

