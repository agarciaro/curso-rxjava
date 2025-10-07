package com.formadoresit.rxjava.tema4;

import io.reactivex.Observable;
import java.util.List;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 03: Operadores de Reducción/Agregación
 * 
 * Operadores que combinan múltiples elementos en uno solo
 * - reduce: Combina todos los elementos en uno
 * - count: Cuenta elementos
 * - collect: Acumula en una colección
 * - toList: Convierte a lista
 * - toMap: Convierte a mapa
 * - all: Verifica si todos cumplen condición
 * - any: Verifica si alguno cumple condición
 * - contains: Verifica si contiene elemento
 */
public class Ejemplo03_OperadoresReduccion {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 03: Operadores de Reducción ===\n");

        // 1. reduce() - Combina todos en uno
        System.out.println("--- reduce() (suma) ---");
        Observable.just(1, 2, 3, 4, 5)
            .reduce((acumulado, actual) -> acumulado + actual)
            .subscribe(suma -> System.out.println("Suma total: " + suma));

        // reduce con semilla
        System.out.println("\n--- reduce() con semilla ---");
        Observable.just(1, 2, 3, 4, 5)
            .reduce(10, (acumulado, actual) -> acumulado + actual)
            .subscribe(suma -> System.out.println("Suma (iniciando en 10): " + suma));

        // reduce para concatenar strings
        System.out.println("\n--- reduce() concatenación ---");
        Observable.just("Java", "Python", "JavaScript")
            .reduce((acum, actual) -> acum + ", " + actual)
            .subscribe(resultado -> System.out.println("Lenguajes: " + resultado));

        // 2. count() - Cuenta elementos
        System.out.println("\n--- count() ---");
        Observable.just("A", "B", "C", "D", "E")
            .count()
            .subscribe(cantidad -> System.out.println("Total elementos: " + cantidad));

        // 3. collect() - Acumula en colección personalizada
        System.out.println("\n--- collect() ---");
        Observable.just(1, 2, 3, 4, 5)
            .collect(
                () -> new StringBuilder(),  // Supplier: crea el contenedor
                (builder, numero) -> builder.append(numero).append("-")  // BiConsumer
            )
            .subscribe(resultado -> System.out.println("Resultado: " + resultado));

        // 4. toList() - Convierte a lista
        System.out.println("\n--- toList() ---");
        Observable.just("Manzana", "Naranja", "Plátano")
            .toList()
            .subscribe(lista -> System.out.println("Lista: " + lista));

        // 5. toSortedList() - Lista ordenada
        System.out.println("\n--- toSortedList() ---");
        Observable.just(5, 2, 8, 1, 9, 3)
            .toSortedList()
            .subscribe(lista -> System.out.println("Lista ordenada: " + lista));

        // 6. toMap() - Convierte a mapa
        System.out.println("\n--- toMap() ---");
        Observable.just(
            new Producto(1, "Laptop", 1000),
            new Producto(2, "Mouse", 20),
            new Producto(3, "Teclado", 50)
        )
        .toMap(p -> p.id)  // Key mapper
        .subscribe(mapa -> {
            System.out.println("Mapa de productos:");
            mapa.forEach((id, producto) -> 
                System.out.println("  " + id + ": " + producto.nombre)
            );
        });

        // toMap con key y value mapper
        System.out.println("\n--- toMap() con mappers ---");
        Observable.just("uno", "dos", "tres")
            .toMap(
                s -> s,                  // Key: el string mismo
                s -> s.length()          // Value: la longitud
            )
            .subscribe(mapa -> System.out.println("Mapa: " + mapa));

        // 7. toMultimap() - Mapa con múltiples valores
        System.out.println("\n--- toMultimap() ---");
        Observable.just(
            new Persona("Juan", 25),
            new Persona("María", 30),
            new Persona("Pedro", 25),
            new Persona("Ana", 30)
        )
        .toMultimap(p -> p.edad)  // Agrupar por edad
        .subscribe(mapa -> {
            System.out.println("Personas por edad:");
            mapa.forEach((edad, personas) -> 
                System.out.println("  Edad " + edad + ": " + personas)
            );
        });

        // 8. all() - Verifica si TODOS cumplen
        System.out.println("\n--- all() ---");
        Observable.just(2, 4, 6, 8, 10)
            .all(n -> n % 2 == 0)
            .subscribe(todosPares -> 
                System.out.println("¿Todos son pares? " + todosPares));

        Observable.just(2, 3, 4, 6, 8)
            .all(n -> n % 2 == 0)
            .subscribe(todosPares -> 
                System.out.println("¿Todos son pares? " + todosPares));

        // 9. any() - Verifica si ALGUNO cumple
        System.out.println("\n--- any() ---");
        Observable.just(1, 3, 5, 7, 9)
            .any(n -> n > 5)
            .subscribe(hayMayorQue5 -> 
                System.out.println("¿Hay alguno > 5? " + hayMayorQue5));

        // 10. contains() - Verifica si contiene elemento
        System.out.println("\n--- contains() ---");
        Observable.just("Java", "Python", "JavaScript")
            .contains("Python")
            .subscribe(contiene -> 
                System.out.println("¿Contiene 'Python'? " + contiene));

        // 11. isEmpty() - Verifica si está vacío
        System.out.println("\n--- isEmpty() ---");
        Observable.empty()
            .isEmpty()
            .subscribe(vacio -> System.out.println("¿Está vacío? " + vacio));

        Observable.just(1)
            .isEmpty()
            .subscribe(vacio -> System.out.println("¿Está vacío? " + vacio));

        // 12. sequenceEqual() - Compara dos Observables
        System.out.println("\n--- sequenceEqual() ---");
        Observable<Integer> obs1 = Observable.just(1, 2, 3);
        Observable<Integer> obs2 = Observable.just(1, 2, 3);
        Observable<Integer> obs3 = Observable.just(1, 2, 4);

        Observable.sequenceEqual(obs1, obs2)
            .subscribe(iguales -> System.out.println("obs1 == obs2: " + iguales));

        Observable.sequenceEqual(obs1, obs3)
            .subscribe(iguales -> System.out.println("obs1 == obs3: " + iguales));

        // Ejemplo práctico: Estadísticas
        System.out.println("\n--- Ejemplo práctico: Estadísticas ---");
        Observable<Integer> numeros = Observable.just(5, 3, 8, 1, 9, 2, 7);

        numeros.count()
            .subscribe(total -> System.out.println("Total elementos: " + total));

        numeros.reduce(Integer::sum)
            .subscribe(suma -> System.out.println("Suma: " + suma));

        numeros.reduce(Integer::max)
            .subscribe(maximo -> System.out.println("Máximo: " + maximo));

        numeros.reduce(Integer::min)
            .subscribe(minimo -> System.out.println("Mínimo: " + minimo));

        io.reactivex.Single.zip(
            numeros.reduce(Integer::sum).toSingle(),
            numeros.count(),
            (suma, cantidad) -> suma.doubleValue() / cantidad
        ).subscribe(promedio -> System.out.println("Promedio: " + promedio));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• reduce: Combina todos los elementos en uno");
        System.out.println("• count: Cuenta elementos");
        System.out.println("• toList/toMap: Convierte a colecciones");
        System.out.println("• all/any/contains: Verificaciones booleanas");
        System.out.println("• collect: Acumulación personalizada");
    }

    static class Producto {
        int id;
        String nombre;
        double precio;

        Producto(int id, String nombre, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
        }
    }

    static class Persona {
        String nombre;
        int edad;

        Persona(String nombre, int edad) {
            this.nombre = nombre;
            this.edad = edad;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}

