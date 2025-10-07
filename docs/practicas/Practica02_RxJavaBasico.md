# Práctica 02: Introducción a RxJava

## Objetivos
- Crear y suscribirse a Observables
- Entender el ciclo de vida del Observer
- Implementar manejo de errores
- Trabajar con Disposables

## Ejercicios

### Ejercicio 2.1: Primeros Observables
**Duración estimada:** 20 minutos

Crea los siguientes Observables y suscrí bete a ellos:

1. Un Observable que emita los números del 1 al 10
2. Un Observable que emita los días de la semana
3. Un Observable que lea líneas de un archivo simulado
4. Un Observable vacío y observa qué sucede

Para cada uno, imprime:
- Los elementos emitidos (onNext)
- Si hay error (onError)
- Cuando completa (onComplete)

### Ejercicio 2.2: Operadores Básicos
**Duración estimada:** 25 minutos

Partiendo de un Observable de números del 1 al 20:

1. Filtra solo los múltiplos de 3
2. Transforma cada número a su cuadrado
3. Toma solo los primeros 5 elementos
4. Imprime el resultado

Luego, crea una cadena que:
1. Emita nombres de personas
2. Convierta a mayúsculas
3. Filtre los que tienen más de 4 letras
4. Agregue el prefijo "Sr./Sra. "
5. Ordene alfabéticamente

### Ejercicio 2.3: Manejo de Errores
**Duración estimada:** 30 minutos

Implementa un servicio que simule operaciones que pueden fallar:

1. `Observable<String> obtenerDatosServidor()` - Falla aleatoriamente
2. `Observable<Integer> dividir(int a, int b)` - Falla si b es 0
3. `Observable<String> procesarArchivo(String ruta)` - Falla si no existe

Para cada uno, implementa manejo de errores usando:
- `onErrorReturn()` - Retorna valor por defecto
- `onErrorResumeNext()` - Cambia a Observable alternativo
- `retry()` - Reintenta 3 veces

### Ejercicio 2.4: Observables Fríos vs Calientes
**Duración estimada:** 25 minutos

1. Crea un Observable frío que emita un número aleatorio
2. Suscribe dos observadores y verifica que reciben valores diferentes
3. Convierte el Observable a caliente usando `publish()`
4. Suscribe dos observadores y verifica que reciben el mismo valor

Documenta la diferencia observada.

### Ejercicio 2.5: Disposables
**Duración estimada:** 20 minutos

Crea un Observable que emita números cada 100ms:

1. Suscríbete y guarda el Disposable
2. Después de recibir 5 elementos, llama a `dispose()`
3. Verifica que no se reciben más elementos
4. Imprime el estado con `isDisposed()`

Implementa también:
- Un sistema que se suscriba a múltiples Observables
- Use `CompositeDisposable` para gestionar todas las suscripciones
- Cancele todas al mismo tiempo

## Resultados Esperados

### Ejercicio 2.1
```
=== Observable de números ===
onNext: 1
onNext: 2
...
onNext: 10
onComplete

=== Días de la semana ===
onNext: Lunes
onNext: Martes
...
onComplete

=== Observable vacío ===
onComplete (sin emisiones)
```

### Ejercicio 2.2
```
=== Múltiplos de 3 al cuadrado (primeros 5) ===
9
36
81
144
225

=== Nombres procesados ===
Sr./Sra. CARLOS
Sr./Sra. MARÍA
Sr./Sra. PEDRO
```

### Ejercicio 2.3
```
=== obtenerDatosServidor con retry ===
Intento 1: Error
Intento 2: Error
Intento 3: Éxito - Datos obtenidos

=== dividir con onErrorReturn ===
10 / 2 = 5
10 / 0 = -1 (valor por defecto)

=== procesarArchivo con onErrorResumeNext ===
Archivo no encontrado, usando archivo de respaldo
Contenido del respaldo
```

### Ejercicio 2.4
```
=== Observable FRÍO ===
Observador 1: Número aleatorio = 42
Observador 2: Número aleatorio = 87

=== Observable CALIENTE ===
Observador 1: Número = 15
Observador 2: Número = 15
```

### Ejercicio 2.5
```
Elemento: 0
Elemento: 1
Elemento: 2
Elemento: 3
Elemento: 4
Llamando a dispose()...
isDisposed: true
(No hay más emisiones)
```

## Evaluación

- **Ejercicio 2.1:** 15 puntos
- **Ejercicio 2.2:** 20 puntos
- **Ejercicio 2.3:** 30 puntos
- **Ejercicio 2.4:** 20 puntos
- **Ejercicio 2.5:** 15 puntos

**Total:** 100 puntos

## Recursos Adicionales

- Documentación RxJava: http://reactivex.io/documentation/observable.html
- Ejemplos en: `src/main/java/com/formadoresit/rxjava/tema2/`

