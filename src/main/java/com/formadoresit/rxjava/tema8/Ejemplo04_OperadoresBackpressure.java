package com.formadoresit.rxjava.tema8;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 8: Flujos y Backpressure
 * Ejemplo 04: Operadores de Backpressure
 * 
 * Operadores específicos para manejar backpressure:
 * buffer, window, sample, throttle, debounce, etc.
 */
public class Ejemplo04_OperadoresBackpressure {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 04: Operadores de Backpressure ===\n");

        // 1. buffer() - Agrupar elementos en lotes
        System.out.println("--- buffer() - Agrupar elementos ---");
        Flowable.interval(50, TimeUnit.MILLISECONDS)
            .take(20)
            .buffer(5) // Agrupar en lotes de 5
            .observeOn(Schedulers.computation())
            .map(lote -> {
                log("BUFFER", "Procesando lote: " + lote);
                simularTrabajo(200);
                return "Lote procesado: " + lote.size() + " elementos";
            })
            .subscribe(resultado -> log("BUFFER Result", resultado));

        Thread.sleep(1500);

        // 2. window() - Crear ventanas de elementos
        System.out.println("\n--- window() - Ventanas de elementos ---");
        Flowable.interval(30, TimeUnit.MILLISECONDS)
            .take(15)
            .window(4) // Ventanas de 4 elementos
            .flatMap(window -> 
                window
                    .observeOn(Schedulers.computation())
                    .map(n -> {
                        log("WINDOW", "Procesando en ventana: " + n);
                        simularTrabajo(100);
                        return n * 2;
                    })
                    .toList()
                    .toFlowable()
            )
            .subscribe(ventana -> log("WINDOW Result", ventana));

        Thread.sleep(1000);

        // 3. sample() - Muestrear elementos periódicamente
        System.out.println("\n--- sample() - Muestreo periódico ---");
        Flowable.interval(20, TimeUnit.MILLISECONDS)
            .take(50)
            .map(n -> {
                log("SAMPLE", "Emisión: " + n);
                return n;
            })
            .sample(100, TimeUnit.MILLISECONDS) // Muestrear cada 100ms
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("SAMPLE", "Procesando muestra: " + n);
                simularTrabajo(80);
                return n * 3;
            })
            .subscribe(resultado -> log("SAMPLE Result", resultado));

        Thread.sleep(800);

        // 4. throttleFirst() - Solo el primer elemento en un período
        System.out.println("\n--- throttleFirst() - Primer elemento por período ---");
        Flowable.interval(30, TimeUnit.MILLISECONDS)
            .take(20)
            .map(n -> {
                log("THROTTLE FIRST", "Emisión: " + n);
                return n;
            })
            .throttleFirst(150, TimeUnit.MILLISECONDS) // Solo el primero cada 150ms
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("THROTTLE FIRST", "Procesando: " + n);
                simularTrabajo(100);
                return n * 4;
            })
            .subscribe(resultado -> log("THROTTLE FIRST Result", resultado));

        Thread.sleep(800);

        // 5. throttleLast() - Solo el último elemento en un período
        System.out.println("\n--- throttleLast() - Último elemento por período ---");
        Flowable.interval(30, TimeUnit.MILLISECONDS)
            .take(20)
            .map(n -> {
                log("THROTTLE LAST", "Emisión: " + n);
                return n;
            })
            .throttleLast(150, TimeUnit.MILLISECONDS) // Solo el último cada 150ms
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("THROTTLE LAST", "Procesando: " + n);
                simularTrabajo(100);
                return n * 5;
            })
            .subscribe(resultado -> log("THROTTLE LAST Result", resultado));

        Thread.sleep(800);

        // 6. debounce() - Esperar pausa antes de procesar
        System.out.println("\n--- debounce() - Esperar pausa ---");
        Flowable.interval(40, TimeUnit.MILLISECONDS)
            .take(15)
            .map(n -> {
                log("DEBOUNCE", "Emisión: " + n);
                return n;
            })
            .debounce(100, TimeUnit.MILLISECONDS) // Esperar 100ms de pausa
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("DEBOUNCE", "Procesando: " + n);
                simularTrabajo(80);
                return n * 6;
            })
            .subscribe(resultado -> log("DEBOUNCE Result", resultado));

        Thread.sleep(800);

        // 7. buffer con tiempo
        System.out.println("\n--- buffer() con tiempo ---");
        Flowable.interval(25, TimeUnit.MILLISECONDS)
            .take(30)
            .buffer(200, TimeUnit.MILLISECONDS) // Buffer cada 200ms
            .filter(lista -> !lista.isEmpty()) // Filtrar buffers vacíos
            .observeOn(Schedulers.computation())
            .map(lote -> {
                log("BUFFER TIME", "Procesando lote temporal: " + lote);
                simularTrabajo(150);
                return "Lote temporal: " + lote.size() + " elementos";
            })
            .subscribe(resultado -> log("BUFFER TIME Result", resultado));

        Thread.sleep(800);

        // 8. window con tiempo
        System.out.println("\n--- window() con tiempo ---");
        Flowable.interval(20, TimeUnit.MILLISECONDS)
            .take(25)
            .window(150, TimeUnit.MILLISECONDS) // Ventanas de 150ms
            .flatMap(window -> 
                window
                    .observeOn(Schedulers.computation())
                    .map(n -> {
                        log("WINDOW TIME", "Procesando en ventana temporal: " + n);
                        simularTrabajo(60);
                        return n * 7;
                    })
                    .toList()
                    .toFlowable()
            )
            .subscribe(ventana -> log("WINDOW TIME Result", ventana));

        Thread.sleep(800);

        // 9. Combinación de operadores
        System.out.println("\n--- Combinación de operadores ---");
        Flowable.interval(15, TimeUnit.MILLISECONDS)
            .take(40)
            .map(n -> {
                log("COMBINED", "Emisión: " + n);
                return n;
            })
            .throttleFirst(100, TimeUnit.MILLISECONDS) // Solo el primero cada 100ms
            .buffer(3) // Agrupar en lotes de 3
            .observeOn(Schedulers.computation())
            .map(lote -> {
                log("COMBINED", "Procesando lote combinado: " + lote);
                simularTrabajo(120);
                return "Lote combinado: " + lote;
            })
            .subscribe(resultado -> log("COMBINED Result", resultado));

        Thread.sleep(1000);

        // 10. Operadores con métricas
        System.out.println("\n--- Operadores con métricas ---");
        AtomicInteger totalEmitidos = new AtomicInteger(0);
        AtomicInteger totalProcesados = new AtomicInteger(0);
        AtomicInteger totalDescartados = new AtomicInteger(0);
        
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .take(100)
            .map(n -> {
                totalEmitidos.incrementAndGet();
                log("METRICS", "Emitido: " + n);
                return n;
            })
            .throttleFirst(50, TimeUnit.MILLISECONDS) // Solo el primero cada 50ms
            .map(n -> {
                totalProcesados.incrementAndGet();
                log("METRICS", "Procesado: " + n);
                simularTrabajo(30);
                return n;
            })
            .subscribe(
                resultado -> log("METRICS Result", resultado),
                error -> log("METRICS Error", error.getMessage()),
                () -> {
                    totalDescartados.set(totalEmitidos.get() - totalProcesados.get());
                    log("METRICS Final", "Emitidos: " + totalEmitidos.get());
                    log("METRICS Final", "Procesados: " + totalProcesados.get());
                    log("METRICS Final", "Descartados: " + totalDescartados.get());
                    log("METRICS Final", "Eficiencia: " + (totalProcesados.get() * 100.0 / totalEmitidos.get()) + "%");
                }
            );

        Thread.sleep(1000);

        // 11. Operadores con diferentes velocidades
        System.out.println("\n--- Operadores con diferentes velocidades ---");
        
        // Productor muy rápido
        log("SPEED", "Productor muy rápido");
        Flowable.interval(5, TimeUnit.MILLISECONDS)
            .take(50)
            .map(n -> {
                log("FAST", "Emisión rápida: " + n);
                return n;
            })
            .sample(100, TimeUnit.MILLISECONDS) // Muestrear para reducir
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("FAST", "Procesando muestra: " + n);
                simularTrabajo(80);
                return n * 8;
            })
            .subscribe(resultado -> log("FAST Result", resultado));

        Thread.sleep(800);

        // 12. Operadores con backpressure personalizado
        System.out.println("\n--- Operadores con backpressure personalizado ---");
        Flowable.range(1, 100)
            .onBackpressureBuffer(10, () -> log("CUSTOM", "Buffer lleno, descartando"))
            .observeOn(Schedulers.computation())
            .map(n -> {
                log("CUSTOM", "Procesando: " + n);
                simularTrabajo(50);
                return n * 9;
            })
            .subscribe(resultado -> log("CUSTOM Result", resultado));

        Thread.sleep(1000);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• buffer: Agrupar elementos en lotes para procesar");
        System.out.println("• window: Crear ventanas de elementos");
        System.out.println("• sample: Muestrear elementos periódicamente");
        System.out.println("• throttleFirst: Solo el primer elemento por período");
        System.out.println("• throttleLast: Solo el último elemento por período");
        System.out.println("• debounce: Esperar pausa antes de procesar");
        System.out.println("• Combinación: Usar múltiples operadores juntos");
        System.out.println("• Métricas: Monitorear eficiencia de operadores");
        System.out.println("• Velocidad: Adaptar operadores a la velocidad de datos");
        System.out.println("• Personalización: Configurar comportamiento de backpressure");
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

