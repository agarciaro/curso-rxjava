package com.formadoresit.rxjava.tema7;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 7: Concurrencia
 * Ejemplo 01: Schedulers
 * 
 * Schedulers determinan en qué hilo se ejecutan las operaciones
 * Tipos principales:
 * - Schedulers.io(): Para operaciones I/O
 * - Schedulers.computation(): Para cálculos intensivos
 * - Schedulers.newThread(): Crea un nuevo hilo
 * - Schedulers.single(): Un único hilo de trabajo
 * - Schedulers.trampoline(): Cola en el hilo actual
 */
public class Ejemplo01_Schedulers {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 01: Schedulers ===\n");

        // 1. Sin Scheduler (hilo principal)
        System.out.println("--- Sin Scheduler ---");
        Observable.just(1, 2, 3)
            .map(n -> {
                System.out.println("  map en: " + Thread.currentThread().getName());
                return n * 10;
            })
            .subscribe(n -> System.out.println("  subscribe en: " + Thread.currentThread().getName()));

        // 2. Schedulers.io() - Para operaciones I/O
        System.out.println("\n--- Schedulers.io() ---");
        Observable.just("archivo1.txt", "archivo2.txt")
            .subscribeOn(Schedulers.io())
            .map(archivo -> {
                System.out.println("  Leyendo " + archivo + " en: " + Thread.currentThread().getName());
                return "Contenido de " + archivo;
            })
            .subscribe(contenido -> System.out.println("  Resultado: " + contenido));

        Thread.sleep(100);

        // 3. Schedulers.computation() - Para cálculos
        System.out.println("\n--- Schedulers.computation() ---");
        Observable.range(1, 5)
            .subscribeOn(Schedulers.computation())
            .map(n -> {
                System.out.println("  Calculando " + n + " en: " + Thread.currentThread().getName());
                return n * n;
            })
            .subscribe(resultado -> System.out.println("  " + resultado));

        Thread.sleep(100);

        // 4. Schedulers.newThread() - Nuevo hilo para cada suscripción
        System.out.println("\n--- Schedulers.newThread() ---");
        Observable<Integer> obs = Observable.just(1, 2, 3)
            .subscribeOn(Schedulers.newThread())
            .doOnNext(n -> System.out.println("  Hilo: " + Thread.currentThread().getName()));

        obs.subscribe();
        obs.subscribe();

        Thread.sleep(100);

        // 5. Schedulers.single() - Un único hilo worker
        System.out.println("\n--- Schedulers.single() ---");
        Observable.just("Tarea 1")
            .subscribeOn(Schedulers.single())
            .subscribe(s -> System.out.println("  " + s + " en: " + Thread.currentThread().getName()));

        Observable.just("Tarea 2")
            .subscribeOn(Schedulers.single())
            .subscribe(s -> System.out.println("  " + s + " en: " + Thread.currentThread().getName()));

        Thread.sleep(100);

        // 6. Schedulers.trampoline() - Cola en hilo actual
        System.out.println("\n--- Schedulers.trampoline() ---");
        Observable.just(1, 2, 3)
            .subscribeOn(Schedulers.trampoline())
            .subscribe(n -> System.out.println("  Observable 1: " + n));

        Observable.just(10, 20, 30)
            .subscribeOn(Schedulers.trampoline())
            .subscribe(n -> System.out.println("  Observable 2: " + n));

        // Ejemplo práctico: Operación I/O
        System.out.println("\n--- Ejemplo práctico: Base de datos ---");
        Observable.fromCallable(() -> {
            System.out.println("  Consultando DB en: " + Thread.currentThread().getName());
            Thread.sleep(100);  // Simular consulta
            return "Resultado de la consulta";
        })
        .subscribeOn(Schedulers.io())
        .subscribe(resultado -> {
            System.out.println("  Procesando resultado en: " + Thread.currentThread().getName());
            System.out.println("  " + resultado);
        });

        Thread.sleep(200);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Schedulers.io(): Operaciones I/O (DB, archivos, red)");
        System.out.println("• Schedulers.computation(): Cálculos intensivos (CPU)");
        System.out.println("• Schedulers.newThread(): Nuevo hilo por suscripción");
        System.out.println("• Schedulers.single(): Un único hilo worker");
        System.out.println("• subscribeOn(): Define dónde se ejecuta la emisión");
    }
}

