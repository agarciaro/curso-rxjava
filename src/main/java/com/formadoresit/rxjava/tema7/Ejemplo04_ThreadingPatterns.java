package com.formadoresit.rxjava.tema7;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 7: Concurrencia
 * Ejemplo 04: Threading Patterns
 * 
 * Patrones de threading, pool de hilos personalizados,
 * sincronización y control de concurrencia
 */
public class Ejemplo04_ThreadingPatterns {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 04: Threading Patterns ===\n");

        // 1. Pool de hilos personalizado
        System.out.println("--- Pool de hilos personalizado ---");
        ExecutorService customExecutor = Executors.newFixedThreadPool(3, r -> {
            Thread t = new Thread(r, "CustomThread-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });

        Observable.range(1, 5)
            .subscribeOn(Schedulers.from(customExecutor))
            .map(n -> {
                log("Custom Pool", n);
                simularTrabajo(100);
                return n * 2;
            })
            .subscribe(resultado -> log("Resultado Custom", resultado));

        Thread.sleep(600);

        // 2. Sincronización con AtomicInteger
        System.out.println("\n--- Sincronización con AtomicInteger ---");
        AtomicInteger contador = new AtomicInteger(0);
        
        Observable.range(1, 10)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.computation())
                    .map(x -> {
                        int valor = contador.incrementAndGet();
                        log("Contador atómico", valor);
                        simularTrabajo(50);
                        return valor;
                    })
            )
            .subscribe(resultado -> log("Valor final", resultado));

        Thread.sleep(800);

        // 3. Patrón Producer-Consumer
        System.out.println("\n--- Patrón Producer-Consumer ---");
        Observable<Integer> producer = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> i.intValue() + 1)
            .doOnNext(n -> log("Producer", n));

        producer
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("Consumer", n);
                simularTrabajo(150);
                return n * 10;
            })
            .subscribe(resultado -> log("Procesado", resultado));

        Thread.sleep(800);

        // 4. Patrón Worker Pool
        System.out.println("\n--- Patrón Worker Pool ---");
        ExecutorService workerPool = Executors.newFixedThreadPool(2);
        
        Observable.range(1, 6)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.from(workerPool))
                    .map(x -> {
                        log("Worker", x);
                        simularTrabajo(200);
                        return "Trabajo " + x + " completado";
                    })
            )
            .subscribe(resultado -> log("Worker Result", resultado));

        Thread.sleep(1000);

        // 5. Patrón Thread-per-Request
        System.out.println("\n--- Patrón Thread-per-Request ---");
        Observable.range(1, 3)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.newThread())
                    .map(x -> {
                        log("Thread-per-Request", x);
                        simularTrabajo(150);
                        return "Request " + x + " procesado";
                    })
            )
            .subscribe(resultado -> log("Request Result", resultado));

        Thread.sleep(600);

        // 6. Patrón Single-threaded
        System.out.println("\n--- Patrón Single-threaded ---");
        Observable.range(1, 5)
            .subscribeOn(Schedulers.single())
            .map(n -> {
                log("Single Thread", n);
                simularTrabajo(100);
                return n * 3;
            })
            .subscribe(resultado -> log("Single Result", resultado));

        Thread.sleep(600);

        // 7. Patrón Trampoline (Sincrónico)
        System.out.println("\n--- Patrón Trampoline ---");
        Observable.range(1, 3)
            .subscribeOn(Schedulers.trampoline())
            .map(n -> {
                log("Trampoline", n);
                return n * 4;
            })
            .subscribe(resultado -> log("Trampoline Result", resultado));

        // 8. Patrón de Sincronización con Semáforo
        System.out.println("\n--- Patrón con Semáforo ---");
        java.util.concurrent.Semaphore semaphore = new java.util.concurrent.Semaphore(2);
        
        Observable.range(1, 5)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        try {
                            semaphore.acquire();
                            log("Semáforo adquirido", x);
                            simularTrabajo(200);
                            return "Procesado con semáforo: " + x;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return "Interrumpido: " + x;
                        } finally {
                            semaphore.release();
                            log("Semáforo liberado", x);
                        }
                    })
            )
            .subscribe(resultado -> log("Semáforo Result", resultado));

        Thread.sleep(1000);

        // 9. Patrón de Batching
        System.out.println("\n--- Patrón de Batching ---");
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(10)
            .buffer(3) // Agrupar en lotes de 3
            .subscribeOn(Schedulers.io())
            .map(lote -> {
                log("Procesando lote", lote);
                simularTrabajo(150);
                return "Lote procesado: " + lote;
            })
            .subscribe(resultado -> log("Batch Result", resultado));

        Thread.sleep(600);

        // 10. Patrón de Circuit Breaker
        System.out.println("\n--- Patrón de Circuit Breaker ---");
        AtomicInteger fallos = new AtomicInteger(0);
        AtomicInteger exitos = new AtomicInteger(0);
        
        Observable.range(1, 8)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        if (fallos.get() >= 3) {
                            log("Circuit Breaker abierto", x);
                            return "Circuito abierto: " + x;
                        }
                        
                        if (x % 3 == 0) {
                            fallos.incrementAndGet();
                            log("Fallando", x);
                            throw new RuntimeException("Error simulado");
                        }
                        
                        exitos.incrementAndGet();
                        log("Exitoso", x);
                        simularTrabajo(100);
                        return "Exitoso: " + x;
                    })
                    .onErrorReturn(error -> "Error manejado: " + n)
            )
            .subscribe(resultado -> log("Circuit Breaker Result", resultado));

        Thread.sleep(800);

        // 11. Patrón de Rate Limiting
        System.out.println("\n--- Patrón de Rate Limiting ---");
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(10)
            .throttleFirst(200, TimeUnit.MILLISECONDS) // Solo uno cada 200ms
            .subscribeOn(Schedulers.io())
            .map(n -> {
                log("Rate Limited", n);
                simularTrabajo(100);
                return "Rate limited: " + n;
            })
            .subscribe(resultado -> log("Rate Limit Result", resultado));

        Thread.sleep(600);

        // 12. Patrón de Load Balancing
        System.out.println("\n--- Patrón de Load Balancing ---");
        ExecutorService[] workers = {
            Executors.newSingleThreadExecutor(r -> new Thread(r, "Worker-1")),
            Executors.newSingleThreadExecutor(r -> new Thread(r, "Worker-2")),
            Executors.newSingleThreadExecutor(r -> new Thread(r, "Worker-3"))
        };
        
        AtomicInteger workerIndex = new AtomicInteger(0);
        
        Observable.range(1, 9)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.from(workers[workerIndex.getAndIncrement() % workers.length]))
                    .map(x -> {
                        log("Load Balanced", x);
                        simularTrabajo(150);
                        return "Balanceado: " + x;
                    })
            )
            .subscribe(resultado -> log("Load Balance Result", resultado));

        Thread.sleep(1000);

        // Limpiar recursos
        customExecutor.shutdown();
        workerPool.shutdown();
        for (ExecutorService worker : workers) {
            worker.shutdown();
        }

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Pool personalizado: Control total sobre hilos");
        System.out.println("• AtomicInteger: Sincronización thread-safe");
        System.out.println("• Producer-Consumer: Separación de responsabilidades");
        System.out.println("• Worker Pool: Pool de trabajadores especializados");
        System.out.println("• Thread-per-Request: Un hilo por solicitud");
        System.out.println("• Single-threaded: Un solo hilo para todo");
        System.out.println("• Trampoline: Ejecución síncrona en cola");
        System.out.println("• Semáforo: Control de acceso a recursos");
        System.out.println("• Batching: Procesar en lotes");
        System.out.println("• Circuit Breaker: Prevenir cascadas de fallos");
        System.out.println("• Rate Limiting: Controlar frecuencia de operaciones");
        System.out.println("• Load Balancing: Distribuir carga entre workers");
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
