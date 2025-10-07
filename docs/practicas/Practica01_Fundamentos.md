# Práctica 01: Fundamentos de Programación Funcional y Reactiva

## Objetivos
- Practicar conceptos de programación funcional
- Implementar funciones puras e inmutables
- Entender la diferencia entre programación imperativa y declarativa

## Ejercicios

### Ejercicio 1.1: Funciones Puras
**Duración estimada:** 15 minutos

Implementa las siguientes funciones puras:

1. Una función que calcule el factorial de un número usando recursión
2. Una función que filtre números primos de una lista
3. Una función que calcule el promedio de una lista sin usar variables mutables

**Requisitos:**
- No usar variables mutables
- No modificar el estado externo
- La función debe retornar siempre el mismo resultado para los mismos argumentos

**Ejemplo de salida esperada:**
```
factorial(5) = 120
primos([1,2,3,4,5,6,7,8,9,10]) = [2, 3, 5, 7]
promedio([10, 20, 30, 40]) = 25.0
```

### Ejercicio 1.2: Inmutabilidad
**Duración estimada:** 20 minutos

Crea una clase inmutable `Producto` con los siguientes atributos:
- id (int)
- nombre (String)
- precio (double)
- stock (int)

Implementa métodos que retornen nuevas instancias:
- `conNuevoPrecio(double nuevoPrecio)`
- `agregarStock(int cantidad)`
- `vender(int cantidad)` (debe validar stock suficiente)

**Validaciones:**
- El precio debe ser positivo
- El stock no puede ser negativo
- Todos los atributos deben ser `final`

### Ejercicio 1.3: Funciones de Orden Superior
**Duración estimada:** 25 minutos

Implementa una clase `ProcesadorDatos` con los siguientes métodos:

1. `<T, R> List<R> transformar(List<T> lista, Function<T, R> transformador)`
2. `<T> List<T> filtrarYTransformar(List<T> lista, Predicate<T> filtro, Function<T, T> transformador)`
3. `<T> T reducir(List<T> lista, T identidad, BinaryOperator<T> operador)`

Úsalos para:
- Convertir una lista de nombres a mayúsculas
- Filtrar números pares y multiplicarlos por 2
- Sumar todos los elementos de una lista

### Ejercicio 1.4: Programación Declarativa
**Duración estimada:** 30 minutos

Dada una lista de empleados con nombre, departamento y salario:

1. Obtener el salario promedio por departamento
2. Encontrar los 3 empleados con mayor salario
3. Agrupar empleados por departamento y mostrar el total de salarios por departamento
4. Obtener nombres de empleados que ganan más de 3000 y ordenarlos alfabéticamente

**Requisito:** Usa solo Streams y operaciones declarativas (sin bucles ni variables mutables).

## Resultados Esperados

### Ejercicio 1.1
```java
Factorial de 5: 120
Números primos: [2, 3, 5, 7]
Promedio: 25.0
```

### Ejercicio 1.2
```java
Producto original: Producto{id=1, nombre='Laptop', precio=1000.0, stock=10}
Con nuevo precio: Producto{id=1, nombre='Laptop', precio=900.0, stock=10}
Después de agregar stock: Producto{id=1, nombre='Laptop', precio=900.0, stock=15}
Después de vender: Producto{id=1, nombre='Laptop', precio=900.0, stock=10}
```

### Ejercicio 1.3
```java
Nombres en mayúsculas: [JUAN, PEDRO, MARÍA]
Pares duplicados: [4, 8, 12, 16, 20]
Suma total: 150
```

### Ejercicio 1.4
```java
Salario promedio por departamento:
  IT: 4500.0
  Ventas: 3200.0
  RRHH: 3000.0

Top 3 salarios:
  Ana (5000.0)
  Carlos (4800.0)
  Pedro (4200.0)

Total por departamento:
  IT: 18000.0
  Ventas: 9600.0
  RRHH: 6000.0

Empleados con salario > 3000 (ordenados):
  [Ana, Carlos, David, Elena, Pedro]
```

## Evaluación

- **Ejercicio 1.1:** 20 puntos
- **Ejercicio 1.2:** 25 puntos
- **Ejercicio 1.3:** 25 puntos
- **Ejercicio 1.4:** 30 puntos

**Total:** 100 puntos

## Recursos Adicionales

- [Java Functional Programming](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)
- [Immutability in Java](https://www.baeldung.com/java-immutable-object)
- Ejemplos en: `src/main/java/com/formadoresit/rxjava/tema1/`

