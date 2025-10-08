package com.formadoresit.rxjava.tema7;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 7: Concurrencia
 * Ejemplo 06: Backpressure con Concurrencia
 * 
 * Manejo de backpressure en contextos concurrentes,
 * buffer, window, y estrategias de control de flujo
 */
public class Ejemplo06_BackpressureConcurrencia {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 06: Backpressure con Concurrencia ===\n");

        // 1. Backpressure con buffer
        System.out.println("--- Backpressure con buffer ---");
        Observable.interval(10, TimeUnit.MILLISECONDS)
            .take(20)
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Procesando", n);
                simularTrabajo(50); // Procesamiento más lento que emisión
                return n * 2;
            })
            .buffer(5) // Agrupar en lotes de 5
            .subscribe(lote -> log("Buffer Result", lote));

        Thread.sleep(1000);

        // 2. Backpressure con window
        System.out.println("\n--- Backpressure con window ---");
        Observable.interval(20, TimeUnit.MILLISECONDS)
            .take(15)
            .window(3) // Ventanas de 3 elementos
            .flatMap(window -> 
                window
                    .observeOn(Schedulers.computation())
                    .map(n -> {
                        log("Window Processing", n);
                        simularTrabajo(80);
                        return n * 3;
                    })
                    .toList()
                    .toObservable()
            )
            .subscribe(ventana -> log("Window Result", ventana));

        Thread.sleep(800);

        // 3. Backpressure con sample
        System.out.println("\n--- Backpressure con sample ---");
        Observable.interval(30, TimeUnit.MILLISECONDS)
            .take(20)
            .map(n -> {
                log("Emisión", n);
                return n;
            })
            .sample(100, TimeUnit.MILLISECONDS) // Muestrear cada 100ms
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Sample Processing", n);
                simularTrabajo(60);
                return n * 4;
            })
            .subscribe(resultado -> log("Sample Result", resultado));

        Thread.sleep(800);

        // 4. Backpressure con throttle
        System.out.println("\n--- Backpressure con throttle ---");
        Observable.interval(25, TimeUnit.MILLISECONDS)
            .take(15)
            .map(n -> {
                log("Throttle Emisión", n);
                return n;
            })
            .throttleFirst(100, TimeUnit.MILLISECONDS) // Solo el primero cada 100ms
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Throttle Processing", n);
                simularTrabajo(70);
                return n * 5;
            })
            .subscribe(resultado -> log("Throttle Result", resultado));

        Thread.sleep(600);

        // 5. Backpressure con debounce
        System.out.println("\n--- Backpressure con debounce ---");
        Observable.interval(40, TimeUnit.MILLISECONDS)
            .take(12)
            .map(n -> {
                log("Debounce Emisión", n);
                return n;
            })
            .debounce(80, TimeUnit.MILLISECONDS) // Solo si no hay emisiones por 80ms
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Debounce Processing", n);
                simularTrabajo(90);
                return n * 6;
            })
            .subscribe(resultado -> log("Debounce Result", resultado));

        Thread.sleep(600);

        // 6. Backpressure con flatMap y concurrencia limitada
        System.out.println("\n--- Backpressure con flatMap limitado ---");
        Observable.interval(15, TimeUnit.MILLISECONDS)
            .take(25)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("FlatMap Processing", x);
                        simularTrabajo(100);
                        return x * 7;
                    }),
                3 // Máximo 3 operaciones concurrentes
            )
            .subscribe(resultado -> log("FlatMap Result", resultado));

        Thread.sleep(1000);

        // 7. Backpressure con buffer con tiempo
        System.out.println("\n--- Backpressure con buffer temporal ---");
        Observable.interval(20, TimeUnit.MILLISECONDS)
            .take(18)
            .buffer(200, TimeUnit.MILLISECONDS) // Buffer cada 200ms
            .filter(lista -> !lista.isEmpty()) // Filtrar buffers vacíos
            .observeOn(Schedulers.computation())
            .map(lote -> {
                log("Buffer Temporal", lote);
                simularTrabajo(80);
                return "Procesado: " + lote;
            })
            .subscribe(resultado -> log("Buffer Temporal Result", resultado));

        Thread.sleep(800);

        // 8. Backpressure con window con tiempo
        System.out.println("\n--- Backpressure con window temporal ---");
        Observable.interval(30, TimeUnit.MILLISECONDS)
            .take(15)
            .window(150, TimeUnit.MILLISECONDS) // Ventanas de 150ms
            .flatMap(window -> 
                window
                    .observeOn(Schedulers.computation())
                    .map(n -> {
                        log("Window Temporal", n);
                        simularTrabajo(60);
                        return n * 8;
                    })
                    .toList()
                    .toObservable()
            )
            .subscribe(ventana -> log("Window Temporal Result", ventana));

        Thread.sleep(800);

        // 9. Backpressure con estrategia de caída
        System.out.println("\n--- Backpressure con estrategia de caída ---");
        AtomicInteger descartados = new AtomicInteger(0);
        
        Observable.interval(10, TimeUnit.MILLISECONDS)
            .take(30)
            .map(n -> {
                log("Drop Emisión", n);
                return n;
            })
            .sample(100, TimeUnit.MILLISECONDS) // Muestrear para reducir
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Drop Processing", n);
                simularTrabajo(50);
                return n * 9;
            })
            .subscribe(
                resultado -> log("Drop Result", resultado),
                error -> log("Drop Error", error.getMessage()),
                () -> log("Drop Completado", "Descartados: " + descartados.get())
            );

        Thread.sleep(800);

        // 10. Backpressure con métricas
        System.out.println("\n--- Backpressure con métricas ---");
        AtomicInteger procesados = new AtomicInteger(0);
        AtomicInteger perdidos = new AtomicInteger(0);
        
        Observable.interval(20, TimeUnit.MILLISECONDS)
            .take(20)
            .map(n -> {
                log("Métricas Emisión", n);
                return n;
            })
            .buffer(100, TimeUnit.MILLISECONDS)
            .filter(lista -> !lista.isEmpty())
            .observeOn(Schedulers.computation())
            .map(lote -> {
                procesados.addAndGet(lote.size());
                log("Métricas Processing", lote);
                simularTrabajo(70);
                return "Métricas: " + lote;
            })
            .subscribe(
                resultado -> log("Métricas Result", resultado),
                error -> log("Métricas Error", error.getMessage()),
                () -> {
                    log("Métricas Final", "Procesados: " + procesados.get());
                    log("Métricas Final", "Perdidos: " + perdidos.get());
                }
            );

        Thread.sleep(800);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• buffer: Agrupar elementos para procesar en lotes");
        System.out.println("• window: Crear ventanas de elementos");
        System.out.println("• sample: Muestrear elementos periódicamente");
        System.out.println("• throttle: Limitar frecuencia de emisiones");
        System.out.println("• debounce: Esperar pausa antes de procesar");
        System.out.println("• flatMap limitado: Controlar concurrencia máxima");
        System.out.println("• Buffer temporal: Agrupar por tiempo");
        System.out.println("• Window temporal: Ventanas basadas en tiempo");
        System.out.println("• Estrategia de caída: Descartar elementos cuando hay sobrecarga");
        System.out.println("• Métricas: Monitorear backpressure y rendimiento");
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

