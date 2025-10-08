package com.formadoresit.rxjava.tema8;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 8: Flujos y Backpressure
 * Ejemplo 06: Flujos Complejos
 * 
 * Flujos complejos con múltiples operadores,
 * combinaciones de backpressure y patrones avanzados
 */
public class Ejemplo06_FlujosComplejos {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 06: Flujos Complejos ===\n");

        // 1. Pipeline completo con backpressure
        System.out.println("--- Pipeline completo con backpressure ---");
        Flowable.interval(20, TimeUnit.MILLISECONDS)
            .take(50)
            .map(n -> {
                log("SOURCE", "Emisión: " + n);
                return n;
            })
            .onBackpressureBuffer(10, () -> log("BUFFER", "Buffer lleno"))
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("COMPUTE", "Procesando: " + n);
                simularTrabajo(50);
                return n * 2;
            })
            .filter(n -> n % 4 == 0)
            .map(n -> {
                log("FILTER", "Filtrado: " + n);
                return n + 1;
            })
            .observeOn(Schedulers.io())
            .map(n -> {
                log("IO", "I/O: " + n);
                simularTrabajo(30);
                return "Resultado: " + n;
            })
            .subscribe(resultado -> log("FINAL", resultado));

        Thread.sleep(2000);

        // 2. Flujo con múltiples operadores de backpressure
        System.out.println("\n--- Flujo con múltiples operadores de backpressure ---");
        Flowable.interval(15, TimeUnit.MILLISECONDS)
            .take(40)
            .map(n -> {
                log("MULTI", "Emisión: " + n);
                return n;
            })
            .throttleFirst(100, TimeUnit.MILLISECONDS) // Solo el primero cada 100ms
            .buffer(3) // Agrupar en lotes de 3
            .filter(lote -> !lote.isEmpty())
            .map(lote -> {
                log("MULTI", "Procesando lote: " + lote);
                simularTrabajo(80);
                return lote.stream().mapToLong(Long::longValue).sum();
            })
            .debounce(200, TimeUnit.MILLISECONDS) // Esperar 200ms de pausa
            .observeOn(Schedulers.computation())
            .map(suma -> {
                log("MULTI", "Suma final: " + suma);
                return suma * 2;
            })
            .subscribe(resultado -> log("MULTI Result", resultado));

        Thread.sleep(1500);

        // 3. Flujo con window y backpressure
        System.out.println("\n--- Flujo con window y backpressure ---");
        Flowable.interval(25, TimeUnit.MILLISECONDS)
            .take(30)
            .map(n -> {
                log("WINDOW", "Emisión: " + n);
                return n;
            })
            .window(5) // Ventanas de 5 elementos
            .flatMap(window -> 
                window
                    .onBackpressureDrop(dropped -> log("WINDOW", "Descartado en ventana: " + dropped))
                    .observeOn(Schedulers.computation())
                    .map(n -> {
                        log("WINDOW", "Procesando en ventana: " + n);
                        simularTrabajo(60);
                        return n * 3;
                    })
                    .toList()
                    .toFlowable()
            )
            .map(ventana -> {
                log("WINDOW", "Ventana procesada: " + ventana);
                return "Ventana: " + ventana.size() + " elementos";
            })
            .subscribe(resultado -> log("WINDOW Result", resultado));

        Thread.sleep(1500);

        // 4. Flujo con sample y backpressure
        System.out.println("\n--- Flujo con sample y backpressure ---");
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .take(60)
            .map(n -> {
                log("SAMPLE", "Emisión: " + n);
                return n;
            })
            .sample(150, TimeUnit.MILLISECONDS) // Muestrear cada 150ms
            .onBackpressureBuffer(5, () -> log("SAMPLE", "Buffer de muestra lleno"))
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("SAMPLE", "Procesando muestra: " + n);
                simularTrabajo(100);
                return n * 4;
            })
            .throttleLast(200, TimeUnit.MILLISECONDS) // Solo el último cada 200ms
            .subscribe(resultado -> log("SAMPLE Result", resultado));

        Thread.sleep(1500);

        // 5. Flujo con combinación de estrategias
        System.out.println("\n--- Flujo con combinación de estrategias ---");
        Flowable.interval(30, TimeUnit.MILLISECONDS)
            .take(25)
            .map(n -> {
                log("COMBINED", "Emisión: " + n);
                return n;
            })
            .onBackpressureLatest() // Solo el más reciente
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("COMBINED", "Procesando: " + n);
                simularTrabajo(80);
                return n * 5;
            })
            .buffer(4) // Agrupar en lotes de 4
            .map(lote -> {
                log("COMBINED", "Procesando lote combinado: " + lote);
                simularTrabajo(120);
                return lote.stream().mapToLong(Long::longValue).sum();
            })
            .onBackpressureDrop(dropped -> log("COMBINED", "Lote descartado: " + dropped))
            .observeOn(Schedulers.io())
            .map(suma -> {
                log("COMBINED", "I/O final: " + suma);
                simularTrabajo(50);
                return "Suma final: " + suma;
            })
            .subscribe(resultado -> log("COMBINED Result", resultado));

        Thread.sleep(1500);

        // 6. Flujo con métricas avanzadas
        System.out.println("\n--- Flujo con métricas avanzadas ---");
        AtomicInteger totalEmitidos = new AtomicInteger(0);
        AtomicInteger totalProcesados = new AtomicInteger(0);
        AtomicInteger totalDescartados = new AtomicInteger(0);
        AtomicInteger totalLotes = new AtomicInteger(0);
        
        Flowable.interval(20, TimeUnit.MILLISECONDS)
            .take(40)
            .map(n -> {
                totalEmitidos.incrementAndGet();
                log("METRICS", "Emisión: " + n);
                return n;
            })
            .throttleFirst(100, TimeUnit.MILLISECONDS)
            .buffer(3)
            .filter(lote -> !lote.isEmpty())
            .map(lote -> {
                totalLotes.incrementAndGet();
                log("METRICS", "Procesando lote: " + lote);
                simularTrabajo(60);
                return lote.stream().mapToLong(Long::longValue).sum();
            })
            .onBackpressureDrop(dropped -> {
                totalDescartados.incrementAndGet();
                log("METRICS", "Lote descartado: " + dropped);
            })
            .observeOn(Schedulers.computation())
            .map(suma -> {
                totalProcesados.incrementAndGet();
                log("METRICS", "Suma procesada: " + suma);
                return suma;
            })
            .subscribe(
                resultado -> log("METRICS Result", resultado),
                error -> log("METRICS Error", error.getMessage()),
                () -> {
                    log("METRICS Final", "Emitidos: " + totalEmitidos.get());
                    log("METRICS Final", "Lotes procesados: " + totalLotes.get());
                    log("METRICS Final", "Lotes descartados: " + totalDescartados.get());
                    log("METRICS Final", "Eficiencia: " + (totalLotes.get() * 100.0 / (totalLotes.get() + totalDescartados.get())) + "%");
                }
            );

        Thread.sleep(1500);

        // 7. Flujo con diferentes velocidades
        System.out.println("\n--- Flujo con diferentes velocidades ---");
        
        // Productor muy rápido
        log("SPEED", "Productor muy rápido");
        Flowable.interval(5, TimeUnit.MILLISECONDS)
            .take(50)
            .map(n -> {
                log("FAST", "Emisión rápida: " + n);
                return n;
            })
            .sample(100, TimeUnit.MILLISECONDS) // Muestrear para reducir
            .buffer(2) // Agrupar en lotes pequeños
            .filter(lote -> !lote.isEmpty())
            .map(lote -> {
                log("FAST", "Procesando lote rápido: " + lote);
                simularTrabajo(80);
                return lote.stream().mapToLong(Long::longValue).sum();
            })
            .onBackpressureLatest() // Solo el más reciente
            .observeOn(Schedulers.computation())
            .map(suma -> {
                log("FAST", "Suma rápida: " + suma);
                return suma * 6;
            })
            .subscribe(resultado -> log("FAST Result", resultado));

        Thread.sleep(1000);

        // 8. Flujo con backpressure adaptativo
        System.out.println("\n--- Flujo con backpressure adaptativo ---");
        Flowable.range(1, 30)
            .map(n -> {
                log("ADAPTIVE", "Emisión: " + n);
                return n;
            })
            .onBackpressureBuffer(5, () -> log("ADAPTIVE", "Buffer lleno, adaptando"))
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("ADAPTIVE", "Procesando: " + n);
                simularTrabajo(100);
                return n * 7;
            })
            .throttleFirst(150, TimeUnit.MILLISECONDS) // Adaptar frecuencia
            .buffer(3) // Adaptar tamaño de lote
            .map(lote -> {
                log("ADAPTIVE", "Lote adaptativo: " + lote);
                simularTrabajo(80);
                return lote.stream().mapToLong(Integer::intValue).sum();
            })
            .onBackpressureDrop(dropped -> log("ADAPTIVE", "Lote adaptativo descartado: " + dropped))
            .subscribe(resultado -> log("ADAPTIVE Result", resultado));

        Thread.sleep(1500);

        // 9. Flujo con circuit breaker
        System.out.println("\n--- Flujo con circuit breaker ---");
        AtomicInteger errorCount = new AtomicInteger(0);
        
        Flowable.interval(40, TimeUnit.MILLISECONDS)
            .take(20)
            .map(n -> {
                log("CIRCUIT", "Emisión: " + n);
                return n;
            })
            .onBackpressureBuffer(3, () -> log("CIRCUIT", "Buffer lleno"))
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("CIRCUIT", "Procesando: " + n);
                simularTrabajo(100);
                
                // Simular error ocasional
                if (n % 5 == 0) {
                    errorCount.incrementAndGet();
                    log("CIRCUIT", "Error simulado en: " + n);
                    if (errorCount.get() >= 2) {
                        log("CIRCUIT", "Circuito abierto por errores");
                        throw new RuntimeException("Circuito abierto");
                    }
                }
                
                return n * 8;
            })
            .onErrorResumeNext(error -> {
                log("CIRCUIT", "Recuperando de error: " + error.getMessage());
                return Flowable.just(-1L);
            })
            .subscribe(resultado -> log("CIRCUIT Result", resultado));

        Thread.sleep(1500);

        // 10. Flujo con métricas de rendimiento
        System.out.println("\n--- Flujo con métricas de rendimiento ---");
        long startTime = System.currentTimeMillis();
        AtomicInteger processedCount = new AtomicInteger(0);
        
        Flowable.range(1, 50)
            .map(n -> {
                log("PERFORMANCE", "Emisión: " + n);
                return n;
            })
            .onBackpressureBuffer(10, () -> log("PERFORMANCE", "Buffer lleno"))
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("PERFORMANCE", "Procesando: " + n);
                simularTrabajo(50);
                return n * 9;
            })
            .buffer(5)
            .map(lote -> {
                processedCount.addAndGet(lote.size());
                log("PERFORMANCE", "Lote de rendimiento: " + lote);
                simularTrabajo(100);
                return lote.stream().mapToInt(Integer::intValue).sum();
            })
            .onBackpressureDrop(dropped -> log("PERFORMANCE", "Lote de rendimiento descartado: " + dropped))
            .subscribe(
                resultado -> log("PERFORMANCE Result", resultado),
                error -> log("PERFORMANCE Error", error.getMessage()),
                () -> {
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    log("PERFORMANCE Final", "Procesados: " + processedCount.get());
                    log("PERFORMANCE Final", "Duración: " + duration + "ms");
                    log("PERFORMANCE Final", "Throughput: " + (processedCount.get() * 1000.0 / duration) + " elementos/seg");
                }
            );

        Thread.sleep(1500);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Pipeline completo: Múltiples operadores en secuencia");
        System.out.println("• Múltiples operadores: Combinar diferentes estrategias");
        System.out.println("• Window y backpressure: Manejar ventanas con control de flujo");
        System.out.println("• Sample y backpressure: Muestrear con control de flujo");
        System.out.println("• Combinación de estrategias: Usar múltiples estrategias juntas");
        System.out.println("• Métricas avanzadas: Monitorear múltiples aspectos");
        System.out.println("• Diferentes velocidades: Adaptar a la velocidad de datos");
        System.out.println("• Backpressure adaptativo: Ajustar automáticamente");
        System.out.println("• Circuit breaker: Prevenir cascadas de errores");
        System.out.println("• Métricas de rendimiento: Medir throughput y eficiencia");
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

