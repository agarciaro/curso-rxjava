package com.formadoresit.rxjava.tema8;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 8: Flujos y Backpressure
 * Ejemplo 02: Flowable vs Observable
 * 
 * Diferencias entre Flowable y Observable,
 * casos de uso y cuándo usar cada uno
 */
public class Ejemplo02_FlowableVsObservable {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 02: Flowable vs Observable ===\n");

        // 1. Observable básico (sin backpressure)
        System.out.println("--- Observable básico ---");
        Observable.range(1, 10)
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Observable", n);
                simularTrabajo(50);
                return n * 2;
            })
            .subscribe(resultado -> log("Observable Result", resultado));

        Thread.sleep(600);

        // 2. Flowable básico (con backpressure)
        System.out.println("\n--- Flowable básico ---");
        Flowable.range(1, 10)
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Flowable", n);
                simularTrabajo(50);
                return n * 2;
            })
            .subscribe(resultado -> log("Flowable Result", resultado));

        Thread.sleep(600);

        // 3. Observable con muchos elementos (puede causar problemas)
        System.out.println("\n--- Observable con muchos elementos ---");
        Observable.range(1, 1000)
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Observable Large", n);
                simularTrabajo(10);
                return n * 2;
            })
            .subscribe(resultado -> log("Observable Large Result", resultado));

        Thread.sleep(200);

        // 4. Flowable con muchos elementos (maneja backpressure)
        System.out.println("\n--- Flowable con muchos elementos ---");
        Flowable.range(1, 1000)
            .onBackpressureBuffer(100) // Buffer limitado
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Flowable Large", n);
                simularTrabajo(10);
                return n * 2;
            })
            .subscribe(resultado -> log("Flowable Large Result", resultado));

        Thread.sleep(200);

        // 5. Observable con interval (sin backpressure)
        System.out.println("\n--- Observable con interval ---");
        Observable.interval(10, TimeUnit.MILLISECONDS)
            .take(20)
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Observable Interval", n);
                simularTrabajo(50);
                return n * 3;
            })
            .subscribe(resultado -> log("Observable Interval Result", resultado));

        Thread.sleep(1000);

        // 6. Flowable con interval (con backpressure)
        System.out.println("\n--- Flowable con interval ---");
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .take(20)
            .onBackpressureDrop(dropped -> log("Dropped", dropped))
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Flowable Interval", n);
                simularTrabajo(50);
                return n * 3;
            })
            .subscribe(resultado -> log("Flowable Interval Result", resultado));

        Thread.sleep(1000);

        // 7. Conversión de Observable a Flowable
        System.out.println("\n--- Conversión Observable a Flowable ---");
        Observable.just("A", "B", "C", "D", "E")
            .toFlowable(BackpressureStrategy.BUFFER)
            .observeOn(Schedulers.computation())
            .map(s -> {
                log("Converted", s);
                simularTrabajo(30);
                return s.toLowerCase();
            })
            .subscribe(resultado -> log("Converted Result", resultado));

        Thread.sleep(300);

        // 8. Conversión de Flowable a Observable
        System.out.println("\n--- Conversión Flowable a Observable ---");
        Flowable.just(1, 2, 3, 4, 5)
            .toObservable()
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Flowable to Observable", n);
                simularTrabajo(30);
                return n * 4;
            })
            .subscribe(resultado -> log("Flowable to Observable Result", resultado));

        Thread.sleep(300);

        // 9. Casos de uso para Observable
        System.out.println("\n--- Casos de uso para Observable ---");
        System.out.println("• UI events (clicks, teclas)");
        System.out.println("• Datos pequeños y finitos");
        System.out.println("• Operaciones síncronas");
        System.out.println("• Cuando no hay riesgo de backpressure");
        
        Observable.just("Click", "KeyPress", "MouseMove")
            .subscribe(evento -> log("UI Event", evento));

        // 10. Casos de uso para Flowable
        System.out.println("\n--- Casos de uso para Flowable ---");
        System.out.println("• Streams de datos grandes");
        System.out.println("• Operaciones I/O intensivas");
        System.out.println("• Procesamiento de archivos");
        System.out.println("• Cuando hay riesgo de backpressure");
        
        Flowable.range(1, 100)
            .onBackpressureBuffer(50)
            .observeOn(Schedulers.io())
            .map(n -> {
                log("File Processing", n);
                simularTrabajo(20);
                return "Processed line " + n;
            })
            .subscribe(resultado -> log("File Result", resultado));

        Thread.sleep(500);

        // 11. Comparación de rendimiento
        System.out.println("\n--- Comparación de rendimiento ---");
        long startTime = System.currentTimeMillis();
        
        Observable.range(1, 1000)
            .observeOn(Schedulers.computation())
            .map(n -> n * 2)
            .count()
            .subscribe(count -> {
                long endTime = System.currentTimeMillis();
                log("Observable Performance", "Procesados: " + count + " en " + (endTime - startTime) + "ms");
            });

        Thread.sleep(100);

        long flowableStartTime = System.currentTimeMillis();
        
        Flowable.range(1, 1000)
            .observeOn(Schedulers.computation())
            .map(n -> n * 2)
            .count()
            .subscribe(count -> {
                long flowableEndTime = System.currentTimeMillis();
                log("Flowable Performance", "Procesados: " + count + " en " + (flowableEndTime - flowableStartTime) + "ms");
            });

        Thread.sleep(100);

        // 12. Manejo de errores
        System.out.println("\n--- Manejo de errores ---");
        Observable.just(1, 2, 3, 4, 5)
            .map(n -> {
                if (n == 3) throw new RuntimeException("Error en Observable");
                return n * 2;
            })
            .subscribe(
                resultado -> log("Observable Success", resultado),
                error -> log("Observable Error", error.getMessage())
            );

        Flowable.just(1, 2, 3, 4, 5)
            .map(n -> {
                if (n == 3) throw new RuntimeException("Error en Flowable");
                return n * 2;
            })
            .subscribe(
                resultado -> log("Flowable Success", resultado),
                error -> log("Flowable Error", error.getMessage())
            );

        Thread.sleep(200);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Observable: Sin backpressure, ideal para UI y datos pequeños");
        System.out.println("• Flowable: Con backpressure, ideal para streams grandes");
        System.out.println("• Conversión: toFlowable() y toObservable()");
        System.out.println("• Rendimiento: Flowable tiene overhead mínimo");
        System.out.println("• Casos de uso: Elegir según el tipo de datos");
        System.out.println("• Backpressure: Solo Flowable lo maneja automáticamente");
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

