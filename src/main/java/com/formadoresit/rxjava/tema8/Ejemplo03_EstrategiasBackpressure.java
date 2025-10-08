package com.formadoresit.rxjava.tema8;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 8: Flujos y Backpressure
 * Ejemplo 03: Estrategias de Backpressure
 * 
 * Todas las estrategias de backpressure en detalle:
 * BUFFER, DROP, LATEST, ERROR, MISSING
 */
public class Ejemplo03_EstrategiasBackpressure {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 03: Estrategias de Backpressure ===\n");

        // 1. BUFFER Strategy - Almacena todos los elementos
        System.out.println("--- BUFFER Strategy ---");
        AtomicInteger bufferCount = new AtomicInteger(0);
        
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .take(50)
            .onBackpressureBuffer(20) // Buffer limitado a 20 elementos
            .observeOn(Schedulers.computation())
            .map(n -> {
                int count = bufferCount.incrementAndGet();
                log("BUFFER", "Procesando: " + n + " (total: " + count + ")");
                simularTrabajo(100); // Consumidor lento
                return n * 2;
            })
            .subscribe(
                resultado -> log("BUFFER Result", resultado),
                error -> log("BUFFER Error", error.getMessage())
            );

        Thread.sleep(2000);

        // 2. DROP Strategy - Descarta elementos cuando el buffer está lleno
        System.out.println("\n--- DROP Strategy ---");
        AtomicInteger droppedCount = new AtomicInteger(0);
        
        Flowable.interval(5, TimeUnit.MILLISECONDS)
            .take(100)
            .onBackpressureDrop(dropped -> {
                int count = droppedCount.incrementAndGet();
                log("DROPPED", "Elemento descartado: " + dropped + " (total descartados: " + count + ")");
            })
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("DROP", "Procesando: " + n);
                simularTrabajo(50); // Consumidor lento
                return n * 3;
            })
            .subscribe(resultado -> log("DROP Result", resultado));

        Thread.sleep(1000);

        // 3. LATEST Strategy - Solo mantiene el elemento más reciente
        System.out.println("\n--- LATEST Strategy ---");
        AtomicInteger latestCount = new AtomicInteger(0);
        
        Flowable.interval(5, TimeUnit.MILLISECONDS)
            .take(100)
            .onBackpressureLatest()
            .observeOn(Schedulers.computation())
            .map(n -> {
                int count = latestCount.incrementAndGet();
                log("LATEST", "Procesando: " + n + " (procesados: " + count + ")");
                simularTrabajo(50); // Consumidor lento
                return n * 4;
            })
            .subscribe(resultado -> log("LATEST Result", resultado));

        Thread.sleep(1000);

        // 4. ERROR Strategy - Simulado con timeout
        System.out.println("\n--- ERROR Strategy (simulado) ---");
        Flowable.interval(1, TimeUnit.MILLISECONDS)
            .take(1000)
            .timeout(100, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("ERROR", "Procesando: " + n);
                simularTrabajo(10); // Consumidor muy lento
                return n * 5;
            })
            .subscribe(
                resultado -> log("ERROR Result", resultado),
                error -> log("ERROR Exception", error.getMessage())
            );

        Thread.sleep(100);

        // 5. MISSING Strategy - Sin control de backpressure (simulado)
        System.out.println("\n--- MISSING Strategy (simulado) ---");
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .take(20)
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("MISSING", "Procesando: " + n);
                simularTrabajo(50);
                return n * 6;
            })
            .subscribe(resultado -> log("MISSING Result", resultado));

        Thread.sleep(1000);

        // 6. BUFFER con límite de tiempo
        System.out.println("\n--- BUFFER con límite de tiempo ---");
        Flowable.interval(20, TimeUnit.MILLISECONDS)
            .take(30)
            .onBackpressureBuffer(5, () -> log("BUFFER", "Buffer lleno, descartando elementos"))
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("BUFFER Time", "Procesando: " + n);
                simularTrabajo(80);
                return n * 7;
            })
            .subscribe(resultado -> log("BUFFER Time Result", resultado));

        Thread.sleep(1000);

        // 7. DROP con callback personalizado
        System.out.println("\n--- DROP con callback personalizado ---");
        AtomicInteger customDropCount = new AtomicInteger(0);
        
        Flowable.interval(5, TimeUnit.MILLISECONDS)
            .take(50)
            .onBackpressureDrop(dropped -> {
                int count = customDropCount.incrementAndGet();
                log("CUSTOM DROP", "Elemento " + dropped + " descartado (total: " + count + ")");
                if (count % 10 == 0) {
                    log("CUSTOM DROP", "Alerta: " + count + " elementos descartados");
                }
            })
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("CUSTOM DROP", "Procesando: " + n);
                simularTrabajo(60);
                return n * 8;
            })
            .subscribe(resultado -> log("CUSTOM DROP Result", resultado));

        Thread.sleep(800);

        // 8. Comparación de estrategias
        System.out.println("\n--- Comparación de estrategias ---");
        
        // BUFFER
        long bufferStart = System.currentTimeMillis();
        Flowable.range(1, 100)
            .onBackpressureBuffer(50)
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(10);
                return n;
            })
            .count()
            .subscribe(count -> {
                long bufferEnd = System.currentTimeMillis();
                log("BUFFER Comparison", "Procesados: " + count + " en " + (bufferEnd - bufferStart) + "ms");
            });

        Thread.sleep(200);

        // DROP
        long dropStart = System.currentTimeMillis();
        Flowable.range(1, 100)
            .onBackpressureDrop()
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(10);
                return n;
            })
            .count()
            .subscribe(count -> {
                long dropEnd = System.currentTimeMillis();
                log("DROP Comparison", "Procesados: " + count + " en " + (dropEnd - dropStart) + "ms");
            });

        Thread.sleep(200);

        // LATEST
        long latestStart = System.currentTimeMillis();
        Flowable.range(1, 100)
            .onBackpressureLatest()
            .observeOn(Schedulers.computation())
            .map(n -> {
                simularTrabajo(10);
                return n;
            })
            .count()
            .subscribe(count -> {
                long latestEnd = System.currentTimeMillis();
                log("LATEST Comparison", "Procesados: " + count + " en " + (latestEnd - latestStart) + "ms");
            });

        Thread.sleep(200);

        // 9. Estrategias con métricas
        System.out.println("\n--- Estrategias con métricas ---");
        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicInteger droppedCount2 = new AtomicInteger(0);
        
        Flowable.interval(5, TimeUnit.MILLISECONDS)
            .take(100)
            .onBackpressureDrop(dropped -> {
                droppedCount2.incrementAndGet();
                log("METRICS", "Descartado: " + dropped);
            })
            .observeOn(Schedulers.computation())
            .map(n -> {
                processedCount.incrementAndGet();
                log("METRICS", "Procesado: " + n);
                simularTrabajo(50);
                return n;
            })
            .subscribe(
                resultado -> log("METRICS Result", resultado),
                error -> log("METRICS Error", error.getMessage()),
                () -> {
                    log("METRICS Final", "Procesados: " + processedCount.get());
                    log("METRICS Final", "Descartados: " + droppedCount2.get());
                    log("METRICS Final", "Eficiencia: " + (processedCount.get() * 100.0 / (processedCount.get() + droppedCount2.get())) + "%");
                }
            );

        Thread.sleep(1000);

        // 10. Estrategias con diferentes velocidades
        System.out.println("\n--- Estrategias con diferentes velocidades ---");
        
        // Productor rápido, consumidor lento
        log("VELOCITY", "Productor rápido, consumidor lento");
        Flowable.interval(1, TimeUnit.MILLISECONDS)
            .take(50)
            .onBackpressureDrop(dropped -> log("FAST PRODUCER", "Descartado: " + dropped))
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("FAST PRODUCER", "Procesando: " + n);
                simularTrabajo(100); // Consumidor muy lento
                return n;
            })
            .subscribe(resultado -> log("FAST PRODUCER Result", resultado));

        Thread.sleep(1000);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• BUFFER: Almacena elementos (cuidado con memoria)");
        System.out.println("• DROP: Descarta elementos nuevos cuando hay sobrecarga");
        System.out.println("• LATEST: Solo mantiene el elemento más reciente");
        System.out.println("• ERROR: Lanza excepción cuando hay backpressure");
        System.out.println("• MISSING: Sin control de backpressure");
        System.out.println("• Callbacks: Personalizar comportamiento de descarte");
        System.out.println("• Métricas: Monitorear eficiencia de estrategias");
        System.out.println("• Velocidad: Elegir estrategia según velocidad productor/consumidor");
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
