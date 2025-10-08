package com.formadoresit.rxjava.tema8;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TEMA 8: Flujos y Backpressure
 * Ejemplo 08: Performance y Backpressure
 * 
 * Optimización de rendimiento con backpressure,
 * profiling, métricas y técnicas de optimización
 */
public class Ejemplo08_PerformanceBackpressure {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 08: Performance y Backpressure ===\n");

        // 1. Medición de performance básica
        System.out.println("--- Medición de performance básica ---");
        long startTime = System.currentTimeMillis();
        
        Flowable.range(1, 1000)
            .onBackpressureBuffer(100)
            .observeOn(Schedulers.computation())
            .map(n -> n * 2)
            .count()
            .subscribe(count -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                log("PERFORMANCE", "Procesados: " + count + " en " + duration + "ms");
                log("PERFORMANCE", "Throughput: " + (count * 1000.0 / duration) + " elementos/seg");
            });

        Thread.sleep(200);

        // 2. Comparación de estrategias de backpressure
        System.out.println("\n--- Comparación de estrategias de backpressure ---");
        
        // BUFFER
        long bufferStart = System.currentTimeMillis();
        Flowable.range(1, 500)
            .onBackpressureBuffer(50)
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(5);
                return n * 2;
            })
            .count()
            .subscribe(count -> {
                long bufferEnd = System.currentTimeMillis();
                log("BUFFER", "Procesados: " + count + " en " + (bufferEnd - bufferStart) + "ms");
            });

        Thread.sleep(300);

        // DROP
        long dropStart = System.currentTimeMillis();
        Flowable.range(1, 500)
            .onBackpressureDrop()
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(5);
                return n * 2;
            })
            .count()
            .subscribe(count -> {
                long dropEnd = System.currentTimeMillis();
                log("DROP", "Procesados: " + count + " en " + (dropEnd - dropStart) + "ms");
            });

        Thread.sleep(300);

        // LATEST
        long latestStart = System.currentTimeMillis();
        Flowable.range(1, 500)
            .onBackpressureLatest()
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(5);
                return n * 2;
            })
            .count()
            .subscribe(count -> {
                long latestEnd = System.currentTimeMillis();
                log("LATEST", "Procesados: " + count + " en " + (latestEnd - latestStart) + "ms");
            });

        Thread.sleep(300);

        // 3. Profiling de operadores
        System.out.println("\n--- Profiling de operadores ---");
        AtomicLong mapTime = new AtomicLong(0);
        AtomicLong filterTime = new AtomicLong(0);
        AtomicLong bufferTime = new AtomicLong(0);
        
        long profilingStart = System.currentTimeMillis();
        
        Flowable.range(1, 200)
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
            .buffer(5)
            .map(lote -> {
                long bufferStartTime = System.currentTimeMillis();
                int sum = lote.stream().mapToInt(Integer::intValue).sum();
                bufferTime.addAndGet(System.currentTimeMillis() - bufferStartTime);
                return sum;
            })
            .count()
            .subscribe(count -> {
                long profilingEnd = System.currentTimeMillis();
                long totalTime = profilingEnd - profilingStart;
                
                log("PROFILING", "Total: " + totalTime + "ms");
                log("PROFILING", "Map: " + mapTime.get() + "ms");
                log("PROFILING", "Filter: " + filterTime.get() + "ms");
                log("PROFILING", "Buffer: " + bufferTime.get() + "ms");
                log("PROFILING", "Procesados: " + count);
            });

        Thread.sleep(200);

        // 4. Optimización con buffer
        System.out.println("\n--- Optimización con buffer ---");
        long bufferOptStart = System.currentTimeMillis();
        
        Flowable.range(1, 1000)
            .buffer(50) // Procesar en lotes de 50
            .observeOn(Schedulers.computation())
            .map(lote -> {
                log("BUFFER OPT", "Procesando lote: " + lote.size() + " elementos");
                simularTrabajo(20);
                return lote.stream().mapToInt(Integer::intValue).sum();
            })
            .count()
            .subscribe(count -> {
                long bufferOptEnd = System.currentTimeMillis();
                log("BUFFER OPT", "Lotes procesados: " + count + " en " + (bufferOptEnd - bufferOptStart) + "ms");
            });

        Thread.sleep(300);

        // 5. Optimización con window
        System.out.println("\n--- Optimización con window ---");
        long windowOptStart = System.currentTimeMillis();
        
        Flowable.range(1, 800)
            .window(40) // Ventanas de 40 elementos
            .flatMap(window -> 
                window
                    .observeOn(Schedulers.computation())
                    .map(n -> {
                        simularTrabajo(10);
                        return n * 2;
                    })
                    .toList()
                    .toFlowable()
            )
            .count()
            .subscribe(count -> {
                long windowOptEnd = System.currentTimeMillis();
                log("WINDOW OPT", "Ventanas procesadas: " + count + " en " + (windowOptEnd - windowOptStart) + "ms");
            });

        Thread.sleep(300);

        // 6. Optimización con sample
        System.out.println("\n--- Optimización con sample ---");
        long sampleOptStart = System.currentTimeMillis();
        
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .take(100)
            .sample(100, TimeUnit.MILLISECONDS) // Muestrear cada 100ms
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(50);
                return n * 3;
            })
            .count()
            .subscribe(count -> {
                long sampleOptEnd = System.currentTimeMillis();
                log("SAMPLE OPT", "Muestras procesadas: " + count + " en " + (sampleOptEnd - sampleOptStart) + "ms");
            });

        Thread.sleep(300);

        // 7. Optimización con throttle
        System.out.println("\n--- Optimización con throttle ---");
        long throttleOptStart = System.currentTimeMillis();
        
        Flowable.interval(20, TimeUnit.MILLISECONDS)
            .take(80)
            .throttleFirst(150, TimeUnit.MILLISECONDS) // Solo el primero cada 150ms
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(60);
                return n * 4;
            })
            .count()
            .subscribe(count -> {
                long throttleOptEnd = System.currentTimeMillis();
                log("THROTTLE OPT", "Elementos procesados: " + count + " en " + (throttleOptEnd - throttleOptStart) + "ms");
            });

        Thread.sleep(300);

        // 8. Optimización con debounce
        System.out.println("\n--- Optimización con debounce ---");
        long debounceOptStart = System.currentTimeMillis();
        
        Flowable.interval(30, TimeUnit.MILLISECONDS)
            .take(60)
            .debounce(100, TimeUnit.MILLISECONDS) // Esperar 100ms de pausa
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(80);
                return n * 5;
            })
            .count()
            .subscribe(count -> {
                long debounceOptEnd = System.currentTimeMillis();
                log("DEBOUNCE OPT", "Elementos procesados: " + count + " en " + (debounceOptEnd - debounceOptStart) + "ms");
            });

        Thread.sleep(300);

        // 9. Optimización con métricas
        System.out.println("\n--- Optimización con métricas ---");
        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicInteger droppedCount = new AtomicInteger(0);
        AtomicLong totalProcessingTime = new AtomicLong(0);
        
        long metricsStart = System.currentTimeMillis();
        
        Flowable.range(1, 300)
            .map(n -> {
                log("METRICS", "Emisión: " + n);
                return n;
            })
            .onBackpressureDrop(dropped -> {
                droppedCount.incrementAndGet();
                log("METRICS", "Descartado: " + dropped);
            })
            .observeOn(Schedulers.computation())
            .map(n -> {
                long processStart = System.currentTimeMillis();
                simularTrabajo(20);
                processedCount.incrementAndGet();
                totalProcessingTime.addAndGet(System.currentTimeMillis() - processStart);
                return n * 6;
            })
            .count()
            .subscribe(count -> {
                long metricsEnd = System.currentTimeMillis();
                long totalTime = metricsEnd - metricsStart;
                
                log("METRICS", "Procesados: " + processedCount.get());
                log("METRICS", "Descartados: " + droppedCount.get());
                log("METRICS", "Tiempo total: " + totalTime + "ms");
                log("METRICS", "Tiempo promedio por elemento: " + (totalProcessingTime.get() / processedCount.get()) + "ms");
                log("METRICS", "Throughput: " + (processedCount.get() * 1000.0 / totalTime) + " elementos/seg");
                log("METRICS", "Eficiencia: " + (processedCount.get() * 100.0 / (processedCount.get() + droppedCount.get())) + "%");
            });

        Thread.sleep(400);

        // 10. Optimización con diferentes tamaños de buffer
        System.out.println("\n--- Optimización con diferentes tamaños de buffer ---");
        
        int[] bufferSizes = {10, 25, 50, 100};
        for (int bufferSize : bufferSizes) {
            long bufferSizeStart = System.currentTimeMillis();
            
            Flowable.range(1, 200)
                .onBackpressureBuffer(bufferSize)
                .observeOn(Schedulers.computation())
                .map(n -> {
                    simularTrabajo(15);
                    return n * 2;
                })
                .count()
                .subscribe(count -> {
                    long bufferSizeEnd = System.currentTimeMillis();
                    log("BUFFER SIZE", "Tamaño: " + bufferSize + ", Procesados: " + count + " en " + (bufferSizeEnd - bufferSizeStart) + "ms");
                });
            
            Thread.sleep(100);
        }

        // 11. Optimización con diferentes velocidades
        System.out.println("\n--- Optimización con diferentes velocidades ---");
        
        // Productor rápido
        long fastStart = System.currentTimeMillis();
        Flowable.interval(5, TimeUnit.MILLISECONDS)
            .take(100)
            .onBackpressureDrop(dropped -> log("FAST", "Descartado: " + dropped))
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(30);
                return n * 2;
            })
            .count()
            .subscribe(count -> {
                long fastEnd = System.currentTimeMillis();
                log("FAST", "Procesados: " + count + " en " + (fastEnd - fastStart) + "ms");
            });

        Thread.sleep(300);

        // Productor lento
        long slowStart = System.currentTimeMillis();
        Flowable.interval(50, TimeUnit.MILLISECONDS)
            .take(20)
            .onBackpressureBuffer(10)
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(30);
                return n * 2;
            })
            .count()
            .subscribe(count -> {
                long slowEnd = System.currentTimeMillis();
                log("SLOW", "Procesados: " + count + " en " + (slowEnd - slowStart) + "ms");
            });

        Thread.sleep(300);

        // 12. Optimización con combinación de operadores
        System.out.println("\n--- Optimización con combinación de operadores ---");
        long combinedStart = System.currentTimeMillis();
        
        Flowable.range(1, 400)
            .map(n -> {
                log("COMBINED", "Emisión: " + n);
                return n;
            })
            .throttleFirst(50, TimeUnit.MILLISECONDS) // Solo el primero cada 50ms
            .buffer(10) // Agrupar en lotes de 10
            .map(lote -> {
                log("COMBINED", "Procesando lote: " + lote.size() + " elementos");
                simularTrabajo(40);
                return lote.stream().mapToInt(Integer::intValue).sum();
            })
            .onBackpressureDrop(dropped -> log("COMBINED", "Lote descartado: " + dropped))
            .observeOn(Schedulers.computation())
            .map(suma -> {
                simularTrabajo(20);
                return suma * 2;
            })
            .count()
            .subscribe(count -> {
                long combinedEnd = System.currentTimeMillis();
                log("COMBINED", "Lotes procesados: " + count + " en " + (combinedEnd - combinedStart) + "ms");
            });

        Thread.sleep(400);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Medición: Usar System.currentTimeMillis() para medir tiempo");
        System.out.println("• Comparación: Comparar diferentes estrategias de backpressure");
        System.out.println("• Profiling: Medir tiempo de operadores individuales");
        System.out.println("• Optimización con buffer: Procesar en lotes para mejor rendimiento");
        System.out.println("• Optimización con window: Usar ventanas para procesamiento eficiente");
        System.out.println("• Optimización con sample: Muestrear para reducir carga");
        System.out.println("• Optimización con throttle: Limitar frecuencia de procesamiento");
        System.out.println("• Optimización con debounce: Esperar pausa antes de procesar");
        System.out.println("• Métricas: Monitorear throughput, eficiencia y tiempo de procesamiento");
        System.out.println("• Tamaños de buffer: Probar diferentes tamaños para optimizar");
        System.out.println("• Velocidades: Adaptar optimizaciones a la velocidad de datos");
        System.out.println("• Combinación: Usar múltiples operadores para máximo rendimiento");
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
