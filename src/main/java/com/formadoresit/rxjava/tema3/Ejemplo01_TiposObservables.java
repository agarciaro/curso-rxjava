package com.formadoresit.rxjava.tema3;

import io.reactivex.*;

/**
 * TEMA 3: Observables y Observers
 * Ejemplo 01: Tipos de Observables
 * 
 * RxJava 2 tiene 5 tipos de fuentes reactivas:
 * - Observable<T>: 0..N elementos, sin backpressure
 * - Flowable<T>: 0..N elementos, con backpressure
 * - Single<T>: Exactamente 1 elemento o error
 * - Maybe<T>: 0 o 1 elemento, o error
 * - Completable: Sin elementos, solo completa o error
 */
public class Ejemplo01_TiposObservables {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 01: Tipos de Observables ===\n");

        // 1. Observable: Emite 0 o más elementos
        System.out.println("--- OBSERVABLE ---");
        System.out.println("Puede emitir múltiples elementos");
        Observable<Integer> observable = Observable.just(1, 2, 3, 4, 5);
        observable.subscribe(
            item -> System.out.println("  Elemento: " + item),
            error -> System.err.println("  Error: " + error),
            () -> System.out.println("  Completado")
        );

        // 2. Single: Emite exactamente UN elemento o error
        System.out.println("\n--- SINGLE ---");
        System.out.println("Emite exactamente UN elemento");
        Single<String> single = Single.just("Un único valor");
        single.subscribe(
            item -> System.out.println("  Valor: " + item),
            error -> System.err.println("  Error: " + error)
        );

        // Ejemplo práctico: Consulta de base de datos que retorna un único registro
        Single<String> consultaDB = Single.fromCallable(() -> {
            // Simulación de consulta DB
            Thread.sleep(100);
            return "Usuario: Juan Pérez";
        });
        consultaDB.subscribe(resultado -> System.out.println("  DB: " + resultado));

        // 3. Maybe: Emite 0 o 1 elemento, o error
        System.out.println("\n--- MAYBE ---");
        System.out.println("Puede emitir 0 o 1 elemento");
        
        Maybe<String> maybeConValor = Maybe.just("Tengo valor");
        maybeConValor.subscribe(
            item -> System.out.println("  Con valor: " + item),
            error -> System.err.println("  Error: " + error),
            () -> System.out.println("  Completado sin valor")
        );

        Maybe<String> maybeVacio = Maybe.empty();
        maybeVacio.subscribe(
            item -> System.out.println("  Item: " + item),
            error -> System.err.println("  Error: " + error),
            () -> System.out.println("  Completado sin emitir valor")
        );

        // Ejemplo práctico: Búsqueda que puede no encontrar resultado
        Maybe<String> buscarUsuario = Maybe.fromCallable(() -> {
            String usuarioId = "user123";
            if (usuarioId.equals("user123")) {
                return "Usuario encontrado";
            }
            return null; // No encontrado
        });
        buscarUsuario.subscribe(
            usuario -> System.out.println("  " + usuario),
            error -> System.err.println("  Error: " + error),
            () -> System.out.println("  Usuario no encontrado")
        );

        // 4. Completable: No emite elementos, solo completa o error
        System.out.println("\n--- COMPLETABLE ---");
        System.out.println("No emite elementos, solo indica completación");
        
        Completable completable = Completable.fromRunnable(() -> {
            System.out.println("  Ejecutando tarea...");
            // Simulación de tarea (ej: guardar en DB)
        });
        
        completable.subscribe(
            () -> System.out.println("  ¡Tarea completada!"),
            error -> System.err.println("  Error: " + error)
        );

        // Ejemplo práctico: Operación de escritura
        Completable guardarDatos = Completable.fromAction(() -> {
            System.out.println("  Guardando datos en la base de datos...");
            Thread.sleep(100);
            System.out.println("  Datos guardados exitosamente");
        });
        guardarDatos.subscribe(
            () -> System.out.println("  Operación completada"),
            error -> System.err.println("  Error al guardar: " + error)
        );

        // 5. Flowable: Como Observable pero con backpressure
        System.out.println("\n--- FLOWABLE ---");
        System.out.println("Similar a Observable pero con soporte de backpressure");
        Flowable<Integer> flowable = Flowable.range(1, 5);
        flowable.subscribe(
            item -> System.out.println("  Elemento: " + item),
            error -> System.err.println("  Error: " + error),
            () -> System.out.println("  Completado")
        );

        System.out.println("\n=== CUÁNDO USAR CADA UNO ===");
        System.out.println("• Observable: Flujos con pocos elementos (< 1000)");
        System.out.println("• Flowable: Flujos con muchos elementos o backpressure");
        System.out.println("• Single: Operaciones que retornan un único resultado");
        System.out.println("• Maybe: Operaciones que pueden o no retornar resultado");
        System.out.println("• Completable: Operaciones sin resultado (solo éxito/fallo)");
    }
}

