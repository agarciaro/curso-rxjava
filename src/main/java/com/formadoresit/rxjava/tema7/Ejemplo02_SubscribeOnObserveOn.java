package com.formadoresit.rxjava.tema7;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * TEMA 7: Concurrencia
 * Ejemplo 02: subscribeOn vs observeOn
 * 
 * subscribeOn(): Define el hilo donde se ejecuta la FUENTE (emisión)
 * observeOn(): Define el hilo donde se ejecutan los OPERADORES posteriores
 */
public class Ejemplo02_SubscribeOnObserveOn {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 02: subscribeOn vs observeOn ===\n");

        // 1. Solo subscribeOn
        System.out.println("--- Solo subscribeOn ---");
        Observable.just(1, 2, 3)
            .doOnNext(n -> log("Emisión", n))
            .map(n -> {
                log("map1", n);
                return n * 10;
            })
            .map(n -> {
                log("map2", n);
                return n + 1;
            })
            .subscribeOn(Schedulers.io())
            .subscribe(n -> log("subscribe", n));

        Thread.sleep(100);

        // 2. Solo observeOn
        System.out.println("\n--- Solo observeOn ---");
        Observable.just(1, 2, 3)
            .doOnNext(n -> log("Emisión", n))
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("map", n);
                return n * 10;
            })
            .subscribe(n -> log("subscribe", n));

        Thread.sleep(100);

        // 3. subscribeOn + observeOn
        System.out.println("\n--- subscribeOn + observeOn ---");
        Observable.just(1, 2, 3)
            .doOnNext(n -> log("Emisión", n))
            .subscribeOn(Schedulers.io())  // Fuente en io()
            .map(n -> {
                log("map antes de observeOn", n);
                return n * 10;
            })
            .observeOn(Schedulers.computation())  // Downstream en computation()
            .map(n -> {
                log("map después de observeOn", n);
                return n + 1;
            })
            .subscribe(n -> log("subscribe", n));

        Thread.sleep(100);

        // 4. Múltiples observeOn
        System.out.println("\n--- Múltiples observeOn ---");
        Observable.just(1, 2, 3)
            .subscribeOn(Schedulers.io())
            .map(n -> {
                log("map1 (io)", n);
                return n * 10;
            })
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("map2 (computation)", n);
                return n + 1;
            })
            .observeOn(Schedulers.single())
            .map(n -> {
                log("map3 (single)", n);
                return n * 2;
            })
            .subscribe(n -> log("subscribe (single)", n));

        Thread.sleep(100);

        // 5. Posición de subscribeOn no importa
        System.out.println("\n--- subscribeOn al final (mismo efecto) ---");
        Observable.just(1, 2, 3)
            .doOnNext(n -> log("Emisión", n))
            .map(n -> {
                log("map", n);
                return n * 10;
            })
            .subscribeOn(Schedulers.io())  // Afecta toda la emisión
            .subscribe(n -> log("subscribe", n));

        Thread.sleep(100);

        // Ejemplo práctico: API call con procesamiento
        System.out.println("\n--- Ejemplo práctico: API + procesamiento ---");
        Observable.fromCallable(() -> {
            log("Llamada API", "");
            Thread.sleep(50);
            return "Datos de la API";
        })
        .subscribeOn(Schedulers.io())  // API call en background
        .map(datos -> {
            log("Procesando datos", datos);
            return datos.toUpperCase();
        })
        .observeOn(Schedulers.computation())  // Procesamiento pesado
        .map(datos -> {
            log("Transformación compleja", datos);
            return datos + " [PROCESADO]";
        })
        .subscribe(resultado -> log("UI actualizada", resultado));

        Thread.sleep(200);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• subscribeOn: Define hilo de EMISIÓN (toda la cadena)");
        System.out.println("• observeOn: Cambia hilo para operadores POSTERIORES");
        System.out.println("• subscribeOn solo afecta una vez (primera llamada)");
        System.out.println("• observeOn se puede usar múltiples veces");
        System.out.println("• Patrón común: subscribeOn(io) -> observeOn(computation)");
    }

    private static void log(String etapa, Object valor) {
        System.out.printf("  [%s] %s: %s\n", 
            Thread.currentThread().getName(), 
            etapa, 
            valor);
    }
}

