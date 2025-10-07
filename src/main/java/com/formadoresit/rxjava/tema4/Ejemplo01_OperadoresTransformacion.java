package com.formadoresit.rxjava.tema4;

import io.reactivex.Observable;
import java.util.Arrays;
import java.util.List;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 01: Operadores de Transformación
 * 
 * Los operadores de transformación modifican los elementos emitidos
 * - map: Transforma cada elemento
 * - flatMap: Transforma y aplana Observables
 * - concatMap: Como flatMap pero preserva el orden
 * - switchMap: Cambia al último Observable
 * - scan: Acumulador que emite cada resultado intermedio
 * - buffer: Agrupa elementos en listas
 * - window: Agrupa elementos en Observables
 */
public class Ejemplo01_OperadoresTransformacion {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 01: Operadores de Transformación ===\n");

        // 1. map() - Transformación 1:1
        System.out.println("--- map() ---");
        Observable.just(1, 2, 3, 4, 5)
            .map(n -> n * 10)
            .subscribe(resultado -> System.out.println("Resultado: " + resultado));

        // map con objetos
        System.out.println("\n--- map() con objetos ---");
        Observable.just("juan", "pedro", "maría")
            .map(String::toUpperCase)
            .map(nombre -> "Hola, " + nombre)
            .subscribe(saludo -> System.out.println(saludo));

        // 2. flatMap() - Transforma a Observable y aplana
        System.out.println("\n--- flatMap() ---");
        Observable.just("A", "B", "C")
            .flatMap(letra -> Observable.just(letra + "1", letra + "2", letra + "3"))
            .subscribe(resultado -> System.out.println("flatMap: " + resultado));

        // flatMap con operaciones asíncronas
        System.out.println("\n--- flatMap() asíncrono ---");
        Observable.just(1, 2, 3)
            .flatMap(numero -> obtenerDatosAsync(numero))
            .subscribe(dato -> System.out.println("Dato async: " + dato));

        // 3. concatMap() - Como flatMap pero preserva el orden
        System.out.println("\n--- concatMap() (preserva orden) ---");
        Observable.just(1, 2, 3)
            .concatMap(numero -> obtenerDatosAsync(numero))
            .subscribe(dato -> System.out.println("Dato ordenado: " + dato));

        // 4. switchMap() - Cancela el anterior y cambia al nuevo
        System.out.println("\n--- switchMap() ---");
        Observable.just("Búsqueda1", "Búsqueda2", "Búsqueda3")
            .switchMap(busqueda -> simularBusqueda(busqueda))
            .subscribe(resultado -> System.out.println("Resultado: " + resultado));

        // 5. scan() - Acumulador que emite cada resultado intermedio
        System.out.println("\n--- scan() (acumulador) ---");
        Observable.just(1, 2, 3, 4, 5)
            .scan((acumulado, actual) -> acumulado + actual)
            .subscribe(suma -> System.out.println("Suma parcial: " + suma));

        // scan con semilla inicial
        System.out.println("\n--- scan() con semilla ---");
        Observable.just("A", "B", "C")
            .scan("Inicio", (acumulado, actual) -> acumulado + "-" + actual)
            .subscribe(resultado -> System.out.println(resultado));

        // 6. buffer() - Agrupa elementos en listas
        System.out.println("\n--- buffer() ---");
        Observable.range(1, 10)
            .buffer(3)  // Grupos de 3
            .subscribe(lista -> System.out.println("Buffer: " + lista));

        // 7. window() - Agrupa elementos en Observables
        System.out.println("\n--- window() ---");
        Observable.range(1, 9)
            .window(3)
            .subscribe(ventana -> {
                System.out.print("Ventana: ");
                ventana.subscribe(
                    item -> System.out.print(item + " "),
                    error -> System.err.println("Error"),
                    () -> System.out.println()
                );
            });

        // 8. cast() - Convierte el tipo
        System.out.println("\n--- cast() ---");
        Observable.just(1, 2, 3)
            .cast(Number.class)
            .subscribe(numero -> System.out.println("Number: " + numero));

        // 9. flatMapIterable() - Transforma a Iterable y aplana
        System.out.println("\n--- flatMapIterable() ---");
        Observable.just("Hello", "World")
            .flatMapIterable(palabra -> Arrays.asList(palabra.split("")))
            .subscribe(letra -> System.out.print(letra + " "));
        System.out.println();

        // 10. groupBy() - Agrupa por clave
        System.out.println("\n--- groupBy() ---");
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .groupBy(n -> n % 2 == 0 ? "Par" : "Impar")
            .subscribe(grupo -> {
                grupo.toList().subscribe(lista -> 
                    System.out.println(grupo.getKey() + ": " + lista)
                );
            });

        // Ejemplo práctico completo
        System.out.println("\n--- Ejemplo práctico: Procesamiento de usuarios ---");
        Observable.just(
            new Usuario(1, "Juan"),
            new Usuario(2, "María"),
            new Usuario(3, "Pedro")
        )
        .flatMap(usuario -> obtenerPedidosUsuario(usuario))
        .map(pedido -> pedido.toUpperCase())
        .subscribe(pedido -> System.out.println("Pedido: " + pedido));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• map: Transformación 1:1");
        System.out.println("• flatMap: Transforma a Observable y aplana (no garantiza orden)");
        System.out.println("• concatMap: Como flatMap pero preserva el orden");
        System.out.println("• switchMap: Cancela anterior, útil para búsquedas");
        System.out.println("• scan: Acumulador que emite resultados intermedios");
        System.out.println("• buffer: Agrupa en listas");
        System.out.println("• groupBy: Agrupa por clave");
    }

    private static Observable<String> obtenerDatosAsync(int id) {
        return Observable.just("Dato-" + id);
    }

    private static Observable<String> simularBusqueda(String termino) {
        return Observable.just("Resultado de: " + termino);
    }

    private static Observable<String> obtenerPedidosUsuario(Usuario usuario) {
        return Observable.just(
            "Pedido-" + usuario.id + "A",
            "Pedido-" + usuario.id + "B"
        );
    }

    static class Usuario {
        int id;
        String nombre;

        Usuario(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
    }
}

