# Práctica 03: Operadores RxJava

## Objetivos
- Dominar operadores de transformación, filtrado y reducción
- Combinar múltiples operadores
- Implementar casos de uso reales con operadores

## Ejercicios

### Ejercicio 3.1: Operadores de Transformación
**Duración estimada:** 30 minutos

Dado un Observable de pedidos con estructura:
```java
class Pedido {
    int id;
    String cliente;
    List<String> productos;
    double total;
}
```

Implementa:
1. Usa `map()` para extraer solo los IDs
2. Usa `flatMap()` para obtener todos los productos de todos los pedidos en un único flujo
3. Usa `scan()` para calcular el total acumulado de todos los pedidos
4. Usa `buffer(3)` para agrupar pedidos de 3 en 3
5. Usa `groupBy()` para agrupar pedidos por cliente

### Ejercicio 3.2: Operadores de Filtrado
**Duración estimada:** 25 minutos

Crea un sistema de monitoreo de sensores que:

1. Emita lecturas de temperatura cada 100ms (valores aleatorios 15-35°C)
2. Use `filter()` para alertar cuando supere 30°C
3. Use `debounce()` para evitar múltiples alertas consecutivas
4. Use `distinct()` para no repetir la misma temperatura
5. Use `take()` para solo procesar las primeras 20 lecturas
6. Use `sample()` para mostrar una lectura cada 500ms

### Ejercicio 3.3: Operadores de Reducción
**Duración estimada:** 30 minutos

Dada una lista de transacciones bancarias:
```java
class Transaccion {
    String tipo; // "deposito" o "retiro"
    double monto;
    LocalDateTime fecha;
}
```

Calcula usando operadores:
1. Balance total usando `reduce()`
2. Número total de transacciones usando `count()`
3. Transacciones agrupadas por tipo usando `toMap()`
4. Lista de montos ordenados usando `toSortedList()`
5. Verificar si todas las transacciones son > 0 usando `all()`
6. Verificar si hay algún retiro > 1000 usando `any()`

### Ejercicio 3.4: Pipeline Complejo
**Duración estimada:** 40 minutos

Implementa un sistema de procesamiento de logs que:

1. Lee logs desde un Observable (simula con `Observable.create()`)
2. Filtra solo logs de nivel ERROR o WARNING
3. Extrae el timestamp y el mensaje
4. Agrupa por hora usando `groupBy()`
5. Cuenta errores por hora
6. Emite un reporte cada vez que se completa una hora

Formato de log: `"[2024-01-15 10:23:45] ERROR: Database connection failed"`

### Ejercicio 3.5: Transformaciones Anidadas
**Duración estimada:** 35 minutos

Simula un sistema de e-commerce:

1. Observable de usuarios
2. Para cada usuario, obtén sus pedidos usando `flatMap()`
3. Para cada pedido, obtén los detalles de productos usando `concatMap()`
4. Calcula el total gastado por usuario
5. Encuentra el usuario que más ha gastado
6. Lista los 5 productos más vendidos

Compara el comportamiento usando:
- `flatMap()` (sin orden garantizado)
- `concatMap()` (orden preservado)
- `switchMap()` (cancela anteriores)

## Resultados Esperados

### Ejercicio 3.1
```
=== IDs de pedidos ===
1, 2, 3, 4, 5

=== Todos los productos ===
Laptop, Mouse, Teclado, Monitor, Cable HDMI, ...

=== Total acumulado ===
100.0 → 250.0 → 420.0 → 680.0 → ...

=== Pedidos agrupados de 3 ===
Grupo 1: [Pedido1, Pedido2, Pedido3]
Grupo 2: [Pedido4, Pedido5, Pedido6]

=== Pedidos por cliente ===
Cliente Juan: [Pedido1, Pedido4]
Cliente María: [Pedido2, Pedido5]
```

### Ejercicio 3.2
```
Lectura: 18.5°C
Lectura: 22.3°C
Lectura: 31.2°C
⚠️ ALERTA: Temperatura alta 31.2°C
Lectura: 32.8°C
⚠️ ALERTA: Temperatura alta 32.8°C (después de debounce)
...
Muestreo cada 500ms: 25.4°C
Muestreo cada 500ms: 28.1°C
```

### Ejercicio 3.3
```
=== Balance total ===
Saldo final: 1,250.50 €

=== Estadísticas ===
Total transacciones: 47
Depósitos: 2,100.00 €
Retiros: 849.50 €

=== Montos ordenados ===
[10.0, 15.5, 20.0, 50.0, 100.0, ...]

=== Validaciones ===
¿Todas > 0? true
¿Algún retiro > 1000? false
```

### Ejercicio 3.4
```
=== Reporte de logs ===
Hora 10:00 - Errores: 5, Warnings: 12
Hora 11:00 - Errores: 3, Warnings: 8
Hora 12:00 - Errores: 8, Warnings: 15

Logs críticos:
  [10:23:45] ERROR: Database connection failed
  [10:45:12] ERROR: Out of memory
  [11:15:33] ERROR: Authentication failed
```

### Ejercicio 3.5
```
=== Análisis por usuario ===
Usuario: Juan - Total gastado: 1,450.00 €
Usuario: María - Total gastado: 2,300.00 €
Usuario: Pedro - Total gastado: 890.00 €

Usuario con mayor gasto: María (2,300.00 €)

=== Top 5 productos ===
1. Laptop (45 unidades)
2. Mouse (38 unidades)
3. Teclado (32 unidades)
4. Monitor (28 unidades)
5. Webcam (25 unidades)

=== Comparación de estrategias ===
flatMap: Resultados rápidos pero sin orden
concatMap: Resultados en orden pero más lento
switchMap: Solo procesa el último usuario
```

## Evaluación

- **Ejercicio 3.1:** 20 puntos
- **Ejercicio 3.2:** 20 puntos
- **Ejercicio 3.3:** 20 puntos
- **Ejercicio 3.4:** 20 puntos
- **Ejercicio 3.5:** 20 puntos

**Total:** 100 puntos

## Criterios de Evaluación

- Uso correcto de operadores (40%)
- Manejo de errores (20%)
- Eficiencia y buenas prácticas (20%)
- Código limpio y comentado (20%)

## Recursos Adicionales

- RxJava Operators: http://reactivex.io/documentation/operators.html
- RxMarbles (visualización): https://rxmarbles.com/
- Ejemplos en: `src/main/java/com/formadoresit/rxjava/tema4/`

