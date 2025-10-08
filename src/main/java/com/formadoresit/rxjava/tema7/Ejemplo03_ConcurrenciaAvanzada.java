package com.formadoresit.rxjava.tema7;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 7: Concurrencia
 * Ejemplo 03: Concurrencia Avanzada
 * 
 * Paralelización, flatMap con concurrencia, merge con schedulers,
 * y patrones avanzados de concurrencia en RxJava
 */
public class Ejemplo03_ConcurrenciaAvanzada {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 03: Concurrencia Avanzada ===\n");

        // 1. Paralelización con flatMap
        System.out.println("--- Paralelización con flatMap ---");
        Observable.range(1, 5)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.computation())
                    .map(x -> {
                        log("Procesando", x);
                        simularTrabajo(100);
                        return x * x;
                    })
            )
            .subscribe(resultado -> log("Resultado", resultado));

        Thread.sleep(600);

        // 2. Paralelización con merge
        System.out.println("\n--- Paralelización con merge ---");
        Observable<Integer> obs1 = Observable.just(1, 2, 3)
            .subscribeOn(Schedulers.io())
            .map(n -> {
                log("IO Task", n);
                simularTrabajo(150);
                return n * 10;
            });

        Observable<Integer> obs2 = Observable.just(4, 5, 6)
            .subscribeOn(Schedulers.computation())
            .map(n -> {
                log("Computation Task", n);
                simularTrabajo(100);
                return n * 20;
            });

        Observable.merge(obs1, obs2)
            .subscribe(resultado -> log("Merge Result", resultado));

        Thread.sleep(600);

        // 3. Concurrencia controlada con flatMap
        System.out.println("\n--- Concurrencia controlada ---");
        Observable.range(1, 10)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("Procesando", x);
                        simularTrabajo(50);
                        return x * 2;
                    }),
                3  // Máximo 3 operaciones concurrentes
            )
            .subscribe(resultado -> log("Resultado controlado", resultado));

        Thread.sleep(800);

        // 4. Paralelización con zip
        System.out.println("\n--- Paralelización con zip ---");
        Observable<String> api1 = Observable.just("API1")
            .subscribeOn(Schedulers.io())
            .map(s -> {
                log("API 1", s);
                simularTrabajo(200);
                return "Datos de API 1";
            });

        Observable<String> api2 = Observable.just("API2")
            .subscribeOn(Schedulers.io())
            .map(s -> {
                log("API 2", s);
                simularTrabajo(150);
                return "Datos de API 2";
            });

        Observable<String> api3 = Observable.just("API3")
            .subscribeOn(Schedulers.io())
            .map(s -> {
                log("API 3", s);
                simularTrabajo(100);
                return "Datos de API 3";
            });

        Observable.zip(api1, api2, api3, (d1, d2, d3) -> d1 + " + " + d2 + " + " + d3)
            .subscribe(resultado -> log("Zip Result", resultado));

        Thread.sleep(500);

        // 5. Concurrencia con timeout
        System.out.println("\n--- Concurrencia con timeout ---");
        Observable.range(1, 3)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("Procesando con timeout", x);
                        simularTrabajo(100 + x * 50);
                        return x * 10;
                    })
                    .timeout(200, TimeUnit.MILLISECONDS)
                    .onErrorReturn(error -> -1) // Valor por defecto si hay timeout
            )
            .subscribe(resultado -> log("Resultado con timeout", resultado));

        Thread.sleep(500);

        // 6. Paralelización con buffer
        System.out.println("\n--- Paralelización con buffer ---");
        Observable.range(1, 20)
            .buffer(5) // Agrupar en lotes de 5
            .flatMap(lote -> 
                Observable.just(lote)
                    .subscribeOn(Schedulers.computation())
                    .map(lista -> {
                        log("Procesando lote", lista);
                        simularTrabajo(100);
                        return lista.stream().mapToInt(Integer::intValue).sum();
                    })
            )
            .subscribe(suma -> log("Suma del lote", suma));

        Thread.sleep(600);

        // 7. Concurrencia con retry
        System.out.println("\n--- Concurrencia con retry ---");
        Observable.range(1, 3)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("Procesando con retry", x);
                        if (x == 2) {
                            throw new RuntimeException("Error simulado");
                        }
                        simularTrabajo(100);
                        return x * 5;
                    })
                    .retry(2)
                    .onErrorReturn(error -> -1)
            )
            .subscribe(resultado -> log("Resultado con retry", resultado));

        Thread.sleep(400);

        // 8. Paralelización con window
        System.out.println("\n--- Paralelización con window ---");
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(10)
            .window(3) // Ventanas de 3 elementos
            .flatMap(window -> 
                window
                    .subscribeOn(Schedulers.computation())
                    .map(n -> {
                        log("Procesando ventana", n);
                        simularTrabajo(80);
                        return n * 2;
                    })
                    .toList()
                    .toObservable()
            )
            .subscribe(lista -> log("Ventana procesada", lista));

        Thread.sleep(600);

        // 9. Concurrencia con debounce
        System.out.println("\n--- Concurrencia con debounce ---");
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(10)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("Procesando con debounce", x);
                        simularTrabajo(30);
                        return x;
                    })
            )
            .debounce(100, TimeUnit.MILLISECONDS)
            .subscribe(resultado -> log("Resultado con debounce", resultado));

        Thread.sleep(500);

        // 10. Ejemplo práctico: Procesamiento de imágenes
        System.out.println("\n--- Ejemplo práctico: Procesamiento de imágenes ---");
        Observable.just("imagen1.jpg", "imagen2.jpg", "imagen3.jpg", "imagen4.jpg")
            .flatMap(archivo -> 
                Observable.just(archivo)
                    .subscribeOn(Schedulers.io())
                    .map(img -> {
                        log("Cargando imagen", img);
                        simularTrabajo(200);
                        return "Cargada: " + img;
                    })
                    .observeOn(Schedulers.computation())
                    .map(img -> {
                        log("Procesando imagen", img);
                        simularTrabajo(150);
                        return "Procesada: " + img;
                    })
                    .observeOn(Schedulers.single())
                    .map(img -> {
                        log("Guardando imagen", img);
                        simularTrabajo(100);
                        return "Guardada: " + img;
                    })
            )
            .subscribe(resultado -> log("Imagen final", resultado));

        Thread.sleep(1000);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• flatMap: Paralelización automática con subscribeOn");
        System.out.println("• merge: Combinar múltiples Observables concurrentes");
        System.out.println("• zip: Esperar resultados de múltiples fuentes");
        System.out.println("• Concurrencia controlada: Límite de operaciones simultáneas");
        System.out.println("• timeout: Evitar operaciones que tardan demasiado");
        System.out.println("• buffer/window: Procesar datos en lotes");
        System.out.println("• retry: Recuperación de errores en operaciones concurrentes");
        System.out.println("• debounce: Evitar procesamiento excesivo");
        System.out.println("• Patrones: IO -> Computation -> Single para pipelines");
    }

    private static void log(String operacion, Object valor) {
        System.out.printf("  [%s] %s: %s\n", 
            Thread.currentThread().getName(), 
            operacion, 
            valor);
    }

    private static void simularTrabajo(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

