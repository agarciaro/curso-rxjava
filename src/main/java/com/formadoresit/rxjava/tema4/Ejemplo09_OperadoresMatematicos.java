package com.formadoresit.rxjava.tema4;

import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.Arrays;
import java.util.List;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 09: Operadores Matemáticos
 * 
 * Operadores para cálculos matemáticos y estadísticas
 * - sum: Suma todos los elementos
 * - average: Calcula el promedio
 * - min: Encuentra el valor mínimo
 * - max: Encuentra el valor máximo
 * - count: Cuenta elementos
 * - reduce: Operación de reducción personalizada
 * - scan: Acumulador que emite cada resultado intermedio
 * - collect: Acumula en una colección personalizada
 * - toList: Convierte a lista
 * - toSortedList: Lista ordenada
 * - toMap: Convierte a mapa
 * - toMultimap: Mapa con múltiples valores
 */
public class Ejemplo09_OperadoresMatematicos {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 09: Operadores Matemáticos ===\n");

        // 1. sum() - Suma todos los elementos (usando reduce)
        System.out.println("--- sum() ---");
        Observable.just(1, 2, 3, 4, 5)
            .reduce(0, (acumulado, actual) -> acumulado + actual)
            .subscribe(suma -> System.out.println("Suma: " + suma));

        // sum con Double
        System.out.println("\n--- sum() con Double ---");
        Observable.just(1.5, 2.5, 3.5, 4.5, 5.5)
            .reduce(0.0, (acumulado, actual) -> acumulado + actual)
            .subscribe(suma -> System.out.println("Suma decimal: " + suma));

        // 2. average() - Calcula el promedio (usando reduce y count)
        System.out.println("\n--- average() ---");
        Observable.just(10, 20, 30, 40, 50)
            .reduce(new double[]{0.0, 0}, (acum, actual) -> {
                acum[0] += actual;
                acum[1]++;
                return acum;
            })
            .map(acum -> acum[0] / acum[1])
            .subscribe(promedio -> System.out.println("Promedio: " + promedio));

        // 3. min() - Encuentra el valor mínimo (usando reduce)
        System.out.println("\n--- min() ---");
        Observable.just(5, 2, 8, 1, 9, 3)
            .reduce(Integer::min)
            .subscribe(minimo -> System.out.println("Mínimo: " + minimo));

        // min con objetos personalizados
        System.out.println("\n--- min() con objetos ---");
        Observable.just(
            new Producto("Laptop", 1000),
            new Producto("Mouse", 20),
            new Producto("Teclado", 50),
            new Producto("Monitor", 300)
        )
        .reduce((p1, p2) -> p1.precio < p2.precio ? p1 : p2)
        .subscribe(barato -> System.out.println("Producto más barato: " + barato));

        // 4. max() - Encuentra el valor máximo (usando reduce)
        System.out.println("\n--- max() ---");
        Observable.just(5, 2, 8, 1, 9, 3)
            .reduce(Integer::max)
            .subscribe(maximo -> System.out.println("Máximo: " + maximo));

        // max con objetos personalizados
        System.out.println("\n--- max() con objetos ---");
        Observable.just(
            new Producto("Laptop", 1000),
            new Producto("Mouse", 20),
            new Producto("Teclado", 50),
            new Producto("Monitor", 300)
        )
        .reduce((p1, p2) -> p1.precio > p2.precio ? p1 : p2)
        .subscribe(caro -> System.out.println("Producto más caro: " + caro));

        // 5. count() - Cuenta elementos
        System.out.println("\n--- count() ---");
        Observable.just("A", "B", "C", "D", "E")
            .count()
            .subscribe(cantidad -> System.out.println("Total elementos: " + cantidad));

        // count con filtro
        System.out.println("\n--- count() con filtro ---");
        Observable.range(1, 20)
            .filter(n -> n % 2 == 0)
            .count()
            .subscribe(pares -> System.out.println("Números pares: " + pares));

        // 6. reduce() - Operación de reducción personalizada
        System.out.println("\n--- reduce() personalizado ---");
        Observable.just(1, 2, 3, 4, 5)
            .reduce(0, (acumulado, actual) -> acumulado + actual * actual)
            .subscribe(sumaCuadrados -> System.out.println("Suma de cuadrados: " + sumaCuadrados));

        // reduce para concatenar strings
        System.out.println("\n--- reduce() concatenación ---");
        Observable.just("Java", "Python", "JavaScript", "C++")
            .reduce((acum, actual) -> acum + ", " + actual)
            .subscribe(lenguajes -> System.out.println("Lenguajes: " + lenguajes));

        // 7. scan() - Acumulador que emite cada resultado intermedio
        System.out.println("\n--- scan() ---");
        Observable.just(1, 2, 3, 4, 5)
            .scan((acumulado, actual) -> acumulado + actual)
            .subscribe(sumaParcial -> System.out.println("Suma parcial: " + sumaParcial));

        // scan con semilla inicial
        System.out.println("\n--- scan() con semilla ---");
        Observable.just(2, 3, 4)
            .scan(10, (acumulado, actual) -> acumulado * actual)
            .subscribe(producto -> System.out.println("Producto parcial: " + producto));

        // 8. collect() - Acumula en una colección personalizada
        System.out.println("\n--- collect() ---");
        Observable.just(1, 2, 3, 4, 5)
            .collect(
                () -> new StringBuilder(),  // Supplier: crea el contenedor
                (builder, numero) -> builder.append(numero).append("-")  // BiConsumer
            )
            .subscribe(resultado -> System.out.println("Resultado: " + resultado));

        // collect para crear una lista personalizada
        System.out.println("\n--- collect() con lista personalizada ---");
        Observable.just("A", "B", "C", "D", "E")
            .collect(
                () -> Arrays.asList(new String[5]),  // Lista de tamaño fijo
                (lista, letra) -> {
                    for (int i = 0; i < lista.size(); i++) {
                        if (lista.get(i) == null) {
                            lista.set(i, letra);
                            break;
                        }
                    }
                }
            )
            .subscribe(lista -> System.out.println("Lista: " + lista));

        // 9. toList() - Convierte a lista
        System.out.println("\n--- toList() ---");
        Observable.just("Manzana", "Naranja", "Plátano", "Uva")
            .toList()
            .subscribe(lista -> System.out.println("Lista: " + lista));

        // 10. toSortedList() - Lista ordenada
        System.out.println("\n--- toSortedList() ---");
        Observable.just(5, 2, 8, 1, 9, 3, 7, 4, 6)
            .toSortedList()
            .subscribe(lista -> System.out.println("Lista ordenada: " + lista));

        // toSortedList con comparador personalizado
        System.out.println("\n--- toSortedList() con comparador ---");
        Observable.just(
            new Producto("Laptop", 1000),
            new Producto("Mouse", 20),
            new Producto("Teclado", 50),
            new Producto("Monitor", 300)
        )
        .toSortedList((p1, p2) -> Double.compare(p1.precio, p2.precio))
        .subscribe(productos -> {
            System.out.println("Productos ordenados por precio:");
            productos.forEach(p -> System.out.println("  " + p));
        });

        // 11. toMap() - Convierte a mapa
        System.out.println("\n--- toMap() ---");
        Observable.just(
            new Producto("Laptop", 1000),
            new Producto("Mouse", 20),
            new Producto("Teclado", 50),
            new Producto("Monitor", 300)
        )
        .toMap(p -> p.nombre)  // Key mapper
        .subscribe(mapa -> {
            System.out.println("Mapa de productos:");
            mapa.forEach((nombre, producto) -> 
                System.out.println("  " + nombre + ": $" + producto.precio)
            );
        });

        // toMap con key y value mapper
        System.out.println("\n--- toMap() con mappers ---");
        Observable.just("uno", "dos", "tres", "cuatro", "cinco")
            .toMap(
                s -> s,                  // Key: el string mismo
                s -> s.length()          // Value: la longitud
            )
            .subscribe(mapa -> System.out.println("Mapa: " + mapa));

        // 12. toMultimap() - Mapa con múltiples valores
        System.out.println("\n--- toMultimap() ---");
        Observable.just(
            new Persona("Juan", 25),
            new Persona("María", 30),
            new Persona("Pedro", 25),
            new Persona("Ana", 30),
            new Persona("Luis", 25)
        )
        .toMultimap(p -> p.edad)  // Agrupar por edad
        .subscribe(mapa -> {
            System.out.println("Personas por edad:");
            mapa.forEach((edad, personas) -> 
                System.out.println("  Edad " + edad + ": " + personas)
            );
        });

        // 13. Ejemplo práctico: Estadísticas de ventas
        System.out.println("\n--- Ejemplo práctico: Estadísticas de ventas ---");
        Observable.just(
            new Venta("Producto A", 100, 10),
            new Venta("Producto B", 200, 5),
            new Venta("Producto C", 150, 8),
            new Venta("Producto D", 300, 3),
            new Venta("Producto E", 120, 12)
        )
        .subscribe(venta -> {
            System.out.println("Venta: " + venta.producto + " - $" + venta.precio + " x " + venta.cantidad);
        });

        // Calcular estadísticas
        Observable.just(
            new Venta("Producto A", 100, 10),
            new Venta("Producto B", 200, 5),
            new Venta("Producto C", 150, 8),
            new Venta("Producto D", 300, 3),
            new Venta("Producto E", 120, 12)
        )
        .map(venta -> venta.precio * venta.cantidad) // Calcular total por venta
        .subscribe(total -> System.out.println("Total por venta: $" + total));

        // Suma total
        Observable.just(
            new Venta("Producto A", 100, 10),
            new Venta("Producto B", 200, 5),
            new Venta("Producto C", 150, 8),
            new Venta("Producto D", 300, 3),
            new Venta("Producto E", 120, 12)
        )
        .map(venta -> venta.precio * venta.cantidad)
        .reduce(0.0, (acum, actual) -> acum + actual)
        .subscribe(totalGeneral -> System.out.println("Total general: $" + totalGeneral));

        // Promedio
        Observable.just(
            new Venta("Producto A", 100, 10),
            new Venta("Producto B", 200, 5),
            new Venta("Producto C", 150, 8),
            new Venta("Producto D", 300, 3),
            new Venta("Producto E", 120, 12)
        )
        .map(venta -> venta.precio * venta.cantidad)
        .reduce(new double[]{0.0, 0}, (acum, actual) -> {
            acum[0] += actual;
            acum[1]++;
            return acum;
        })
        .map(acum -> acum[0] / acum[1])
        .subscribe(promedio -> System.out.println("Promedio: $" + String.format("%.2f", promedio)));

        // 14. Ejemplo avanzado: Análisis de datos
        System.out.println("\n--- Ejemplo avanzado: Análisis de datos ---");
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .buffer(3) // Agrupar en lotes de 3
            .subscribe(lote -> {
                int suma = lote.stream().mapToInt(Integer::intValue).sum();
                double promedio = lote.stream().mapToInt(Integer::intValue).average().orElse(0);
                int maximo = lote.stream().mapToInt(Integer::intValue).max().orElse(0);
                int minimo = lote.stream().mapToInt(Integer::intValue).min().orElse(0);
                
                System.out.println("Lote: " + lote + 
                    " - Suma: " + suma + 
                    ", Promedio: " + String.format("%.2f", promedio) +
                    ", Max: " + maximo + 
                    ", Min: " + minimo);
            });

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• sum: Suma todos los elementos");
        System.out.println("• average: Calcula el promedio");
        System.out.println("• min/max: Encuentra valores mínimo y máximo");
        System.out.println("• count: Cuenta elementos");
        System.out.println("• reduce: Operación de reducción personalizada");
        System.out.println("• scan: Acumulador que emite resultados intermedios");
        System.out.println("• collect: Acumulación en colección personalizada");
        System.out.println("• toList/toMap: Conversión a colecciones");
        System.out.println("• toSortedList: Lista ordenada");
        System.out.println("• toMultimap: Mapa con múltiples valores");
    }

    static class Producto {
        String nombre;
        double precio;

        Producto(String nombre, double precio) {
            this.nombre = nombre;
            this.precio = precio;
        }

        @Override
        public String toString() {
            return nombre + " ($" + precio + ")";
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

    static class Venta {
        String producto;
        double precio;
        int cantidad;

        Venta(String producto, double precio, int cantidad) {
            this.producto = producto;
            this.precio = precio;
            this.cantidad = cantidad;
        }
    }
}
