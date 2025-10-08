package com.formadoresit.rxjava.tema7;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TEMA 7: Concurrencia
 * Ejemplo 09: Performance y Concurrencia
 * 
 * Optimización de rendimiento, profiling,
 * métricas de concurrencia y técnicas de optimización
 */
public class Ejemplo09_PerformanceConcurrencia {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 09: Performance y Concurrencia ===\n");

        // 1. Medición de performance básica
        System.out.println("--- Medición de performance básica ---");
        long startTime = System.currentTimeMillis();
        
        Observable.range(1, 1000)
            .map(n -> n * 2)
            .filter(n -> n % 4 == 0)
            .count()
            .subscribe(count -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                log("Performance", "Procesados: " + count + " en " + duration + "ms");
            });

        Thread.sleep(100);

        // 2. Performance con concurrencia
        System.out.println("\n--- Performance con concurrencia ---");
        long concurrentStartTime = System.currentTimeMillis();
        
        Observable.range(1, 1000)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.computation())
                    .map(x -> x * 2)
                    .filter(x -> x % 4 == 0)
            )
            .count()
            .subscribe(count -> {
                long concurrentEndTime = System.currentTimeMillis();
                long concurrentDuration = concurrentEndTime - concurrentStartTime;
                log("Concurrent Performance", "Procesados: " + count + " en " + concurrentDuration + "ms");
            });

        Thread.sleep(200);

        // 3. Profiling de operaciones
        System.out.println("\n--- Profiling de operaciones ---");
        AtomicLong mapTime = new AtomicLong(0);
        AtomicLong filterTime = new AtomicLong(0);
        AtomicLong totalTime = new AtomicLong(0);
        
        long profilingStartTime = System.currentTimeMillis();
        
        Observable.range(1, 500)
            .doOnNext(n -> totalTime.set(System.currentTimeMillis()))
            .map(n -> {
                long mapStart = System.currentTimeMillis();
                int result = n * 3;
                mapTime.addAndGet(System.currentTimeMillis() - mapStart);
                return result;
            })
            .filter(n -> {
                long filterStart = System.currentTimeMillis();
                boolean result = n % 6 == 0;
                filterTime.addAndGet(System.currentTimeMillis() - filterStart);
                return result;
            })
            .count()
            .subscribe(count -> {
                long profilingEndTime = System.currentTimeMillis();
                long totalDuration = profilingEndTime - profilingStartTime;
                
                log("Profiling", "Total: " + totalDuration + "ms");
                log("Profiling", "Map: " + mapTime.get() + "ms");
                log("Profiling", "Filter: " + filterTime.get() + "ms");
                log("Profiling", "Procesados: " + count);
            });

        Thread.sleep(150);

        // 4. Optimización con buffer
        System.out.println("\n--- Optimización con buffer ---");
        long bufferStartTime = System.currentTimeMillis();
        
        Observable.range(1, 2000)
            .buffer(100) // Procesar en lotes de 100
            .flatMap(lote -> 
                Observable.just(lote)
                    .subscribeOn(Schedulers.computation())
                    .map(lista -> lista.stream().mapToInt(Integer::intValue).sum())
            )
            .reduce(0, Integer::sum)
            .subscribe(suma -> {
                long bufferEndTime = System.currentTimeMillis();
                long bufferDuration = bufferEndTime - bufferStartTime;
                log("Buffer Optimization", "Suma: " + suma + " en " + bufferDuration + "ms");
            });

        Thread.sleep(200);

        // 5. Optimización con window
        System.out.println("\n--- Optimización con window ---");
        long windowStartTime = System.currentTimeMillis();
        
        Observable.range(1, 1500)
            .window(50) // Ventanas de 50 elementos
            .flatMap(window -> 
                window
                    .subscribeOn(Schedulers.io())
                    .map(n -> n * n)
                    .reduce(0, Integer::sum)
                    .toObservable()
            )
            .reduce(0, Integer::sum)
            .subscribe(suma -> {
                long windowEndTime = System.currentTimeMillis();
                long windowDuration = windowEndTime - windowStartTime;
                log("Window Optimization", "Suma: " + suma + " en " + windowDuration + "ms");
            });

        Thread.sleep(200);

        // 6. Optimización con cache
        System.out.println("\n--- Optimización con cache ---");
        long cacheStartTime = System.currentTimeMillis();
        
        Observable<Integer> cachedObservable = Observable.range(1, 100)
            .map(n -> {
                simularTrabajo(10); // Simular trabajo costoso
                return n * 2;
            })
            .cache(); // Cachear resultados
        
        // Primera suscripción (computación)
        cachedObservable.subscribe(n -> log("Cache 1", n));
        
        // Segunda suscripción (desde cache)
        cachedObservable.subscribe(n -> log("Cache 2", n));
        
        long cacheEndTime = System.currentTimeMillis();
        long cacheDuration = cacheEndTime - cacheStartTime;
        log("Cache Optimization", "Duración total: " + cacheDuration + "ms");

        Thread.sleep(100);

        // 7. Optimización con share
        System.out.println("\n--- Optimización con share ---");
        long shareStartTime = System.currentTimeMillis();
        
        Observable<Integer> sharedObservable = Observable.range(1, 100)
            .map(n -> {
                simularTrabajo(5);
                return n * 3;
            })
            .share(); // Compartir entre suscriptores
        
        // Múltiples suscriptores
        sharedObservable.subscribe(n -> log("Share 1", n));
        sharedObservable.subscribe(n -> log("Share 2", n));
        sharedObservable.subscribe(n -> log("Share 3", n));
        
        long shareEndTime = System.currentTimeMillis();
        long shareDuration = shareEndTime - shareStartTime;
        log("Share Optimization", "Duración total: " + shareDuration + "ms");

        Thread.sleep(100);

        // 8. Optimización con debounce
        System.out.println("\n--- Optimización con debounce ---");
        // long debounceStartTime = System.currentTimeMillis();
        
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(20)
            .map(n -> n.intValue())
            .debounce(100, TimeUnit.MILLISECONDS) // Solo procesar si no hay emisiones por 100ms
            .subscribeOn(Schedulers.io())
            .map(n -> {
                log("Debounce Processing", n);
                simularTrabajo(50);
                return n * 4;
            })
            .subscribe(resultado -> log("Debounce Result", resultado));

        Thread.sleep(800);

        // 9. Optimización con throttle
        System.out.println("\n--- Optimización con throttle ---");
        // long throttleStartTime = System.currentTimeMillis();
        
        Observable.interval(30, TimeUnit.MILLISECONDS)
            .take(25)
            .map(n -> n.intValue())
            .throttleFirst(100, TimeUnit.MILLISECONDS) // Solo el primero cada 100ms
            .subscribeOn(Schedulers.computation())
            .map(n -> {
                log("Throttle Processing", n);
                simularTrabajo(60);
                return n * 5;
            })
            .subscribe(resultado -> log("Throttle Result", resultado));

        Thread.sleep(600);

        // 10. Optimización con métricas avanzadas
        System.out.println("\n--- Optimización con métricas avanzadas ---");
        AtomicInteger operacionesCompletadas = new AtomicInteger(0);
        AtomicInteger operacionesFallidas = new AtomicInteger(0);
        AtomicLong tiempoTotal = new AtomicLong(0);
        
        long metricsStartTime = System.currentTimeMillis();
        
        Observable.range(1, 100)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.computation())
                    .map(x -> {
                        long opStart = System.currentTimeMillis();
                        try {
                            simularTrabajo(20);
                            operacionesCompletadas.incrementAndGet();
                            return x * 6;
                        } catch (Exception e) {
                            operacionesFallidas.incrementAndGet();
                            return -1;
                        } finally {
                            tiempoTotal.addAndGet(System.currentTimeMillis() - opStart);
                        }
                    })
            )
            .count()
            .subscribe(count -> {
                long metricsEndTime = System.currentTimeMillis();
                long totalDuration = metricsEndTime - metricsStartTime;
                
                log("Advanced Metrics", "Total: " + totalDuration + "ms");
                log("Advanced Metrics", "Completadas: " + operacionesCompletadas.get());
                log("Advanced Metrics", "Fallidas: " + operacionesFallidas.get());
                log("Advanced Metrics", "Tiempo promedio: " + (tiempoTotal.get() / count) + "ms");
                log("Advanced Metrics", "Throughput: " + (count * 1000 / totalDuration) + " ops/sec");
            });

        Thread.sleep(300);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Medición: Usar System.currentTimeMillis() para medir tiempo");
        System.out.println("• Profiling: Medir tiempo de operaciones individuales");
        System.out.println("• Buffer: Procesar en lotes para optimizar rendimiento");
        System.out.println("• Window: Usar ventanas para procesamiento eficiente");
        System.out.println("• Cache: Almacenar resultados para evitar recomputación");
        System.out.println("• Share: Compartir Observable entre múltiples suscriptores");
        System.out.println("• Debounce: Reducir frecuencia de procesamiento");
        System.out.println("• Throttle: Limitar frecuencia de emisiones");
        System.out.println("• Métricas: Monitorear rendimiento y throughput");
        System.out.println("• Optimización: Combinar técnicas para máximo rendimiento");
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
