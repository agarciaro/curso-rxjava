# Curso de Java Reactivo con RxJava 2

Este proyecto contiene todos los ejemplos, prácticas y ejercicios del curso de Java Reactivo basado en RxJava 2.

## 📋 Requisitos Previos

- **Java 21** o superior
- **Maven 3.8+**
- IDE recomendado: IntelliJ IDEA o Eclipse
- Conocimientos sólidos de Java (incluyendo lambdas y streams de Java 8+)

## 🚀 Instalación

```bash
# Clonar el repositorio
git clone <url-del-repo>

# Compilar el proyecto
mvn clean install

# Ejecutar ejemplos individuales
mvn exec:java -Dexec.mainClass="com.formadoresit.rxjava.tema1.Ejemplo01_Introduccion"
```

## 📚 Estructura del Curso

El curso está organizado en 14 temas más una introducción, cada uno en su propio paquete:

### **INTRODUCCIÓN: Patrón Publisher/Subscriber (Java Puro)**
**📂 Paquete:** `introduccion/`

Antes de usar RxJava, entenderás cómo funciona la programación reactiva implementando el patrón Publisher/Subscriber desde cero con Java puro.

**Ejemplos incluidos:**
- `Ejemplo01_PublisherSubscriberBasico.java` - Patrón fundamental
- `Ejemplo02_PublisherConCancelacion.java` - Subscription y cancelación
- `Ejemplo03_PublisherConTransformacion.java` - Operadores map() y filter()
- `Ejemplo04_PublisherAsincrono.java` - Emisión asíncrona con threads
- `Ejemplo05_CompletoPushVsPull.java` - Comparación Push vs Pull
- `Ejemplo06_ImperativaVsReactivaPerformance.java` - Benchmark de rendimiento

**Conceptos clave:**
- Push vs Pull
- Publisher/Subscriber
- Subscription
- Operadores básicos
- Asincronía

**Ejecutar ejemplos:**
```bash
mvn exec:java -Dexec.mainClass=com.formadoresit.rxjava.introduccion.Ejemplo01_PublisherSubscriberBasico
```

📖 **Documentación:** [Guía Publisher/Subscriber](docs/INTRODUCCION_PUBLISHER_SUBSCRIBER.md)

---

### **Tema 1: Fundamentos sobre Programación Funcional y Reactiva**
- Conceptos de programación funcional
- Funciones puras, inmutabilidad
- Introducción a la programación reactiva
- Ejemplos: `tema1/Ejemplo01_*.java` a `Ejemplo05_*.java`

### **Tema 2: Introducción a ReactiveX RxJava 2**
- Conceptos básicos de RxJava
- Patrón Observable
- Primer Observable y Observer
- Ejemplos: `tema2/Ejemplo01_*.java` a `Ejemplo06_*.java`

### **Tema 3: Observables y Observers**
- Tipos de Observables (Observable, Flowable, Single, Maybe, Completable)
- Ciclo de vida del Observer
- Operadores de creación
- Ejemplos: `tema3/Ejemplo01_*.java` a `Ejemplo08_*.java`

### **Tema 4: Operadores RxJava**
- Operadores de transformación (map, flatMap, etc.)
- Operadores de filtrado (filter, take, skip, etc.)
- Operadores de reducción (reduce, scan, etc.)
- Ejemplos: `tema4/Ejemplo01_*.java` a `Ejemplo12_*.java`

### **Tema 5: Combinando Observables**
- merge, concat, zip
- combineLatest, withLatestFrom
- amb, switchOnNext
- Ejemplos: `tema5/Ejemplo01_*.java` a `Ejemplo08_*.java`

### **Tema 6: Multicast**
- ConnectableObservable
- publish, replay, share
- Subjects (PublishSubject, BehaviorSubject, ReplaySubject)
- Ejemplos: `tema6/Ejemplo01_*.java` a `Ejemplo06_*.java`

### **Tema 7: Concurrencia**
- Schedulers (io, computation, newThread)
- subscribeOn vs observeOn
- Paralelización con flatMap
- Ejemplos: `tema7/Ejemplo01_*.java` a `Ejemplo08_*.java`

### **Tema 8: Flujos y Backpressure**
- Flowable vs Observable
- Estrategias de backpressure
- Control de flujo
- Ejemplos: `tema8/Ejemplo01_*.java` a `Ejemplo06_*.java`

### **Tema 9: Pruebas y Depuración**
- TestObserver y TestSubscriber
- TestScheduler
- Debugging con doOnNext, doOnError
- Ejemplos: `tema9/Ejemplo01_*.java` a `Ejemplo05_*.java`

### **Tema 10: Programación Reactiva en la Web**
- Integraciones HTTP con RxJava
- Manejo de eventos asíncronos
- Ejemplos: `tema10/Ejemplo01_*.java` a `Ejemplo04_*.java`

### **Tema 11: Spring WebFlux**
- Introducción a WebFlux
- Mono y Flux en Spring
- Controladores reactivos
- Ejemplos: `tema11/Ejemplo01_*.java` a `Ejemplo06_*.java`

### **Tema 12: Creación de Flujos**
- Observable.create()
- Emisión bajo demanda
- Manejo de recursos
- Ejemplos: `tema12/Ejemplo01_*.java` a `Ejemplo04_*.java`

### **Tema 13: Suscripción desde Clientes**
- Cliente Java
- Cliente JavaScript
- WebSockets reactivos
- Ejemplos: `tema13/Ejemplo01_*.java` a `Ejemplo03_*.java`

### **Tema 14: Aplicaciones Prácticas**
- Casos de uso reales
- Arquitecturas reactivas
- Mejores prácticas
- Ejemplos: `tema14/Ejemplo01_*.java` a `Ejemplo04_*.java`

## 📖 Cómo Usar Este Curso

1. **Estudia el código de ejemplos**: Cada ejemplo está numerado y documentado
2. **Ejecuta los ejemplos**: Cada clase principal tiene un método `main()`
3. **Lee los enunciados de prácticas**: En la carpeta `docs/practicas/`
4. **Resuelve los ejercicios**: En la carpeta `docs/ejercicios/`

## 📁 Estructura de Directorios

```
curso-rxjava/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── formadoresit/
│   │               └── rxjava/
│   │                   ├── tema1/
│   │                   ├── tema2/
│   │                   ├── ...
│   │                   └── tema14/
│   └── test/
│       └── java/
│           └── com/
│               └── formadoresit/
│                   └── rxjava/
│                       └── tema9/
├── docs/
│   ├── practicas/
│   └── ejercicios/
├── pom.xml
└── README.md
```

## 🔗 Referencias

- [GitBook del Curso](https://aitor-garcia.gitbook.io/java-reactivo)
- [Documentación RxJava 2](https://github.com/ReactiveX/RxJava)
- [ReactiveX](http://reactivex.io/)

## 📝 Licencia

Material educativo para FormadoresIT.

