package com.formadoresit.rxjava.tema4;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 08: Operadores de Backpressure
 * 
 * Operadores para manejar backpressure (cuando el productor es más rápido que el consumidor)
 * - buffer: Agrupa elementos en listas
 * - window: Agrupa elementos en Observables
 * - sample: Muestra periódicamente
 * - throttle: Limita la tasa de emisión
 * - onBackpressureBuffer: Almacena elementos en buffer
 * - onBackpressureDrop: Descarta elementos cuando hay backpressure
 * - onBackpressureLatest: Mantiene solo el último elemento
 * - onBackpressureError: Genera error cuando hay backpressure
 */
public class Ejemplo08_OperadoresBackpressure {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 08: Operadores de Backpressure ===\n");

        // 1. buffer() - Agrupa elementos en listas
        System.out.println("--- buffer() básico ---");
        Observable.range(1, 10)
            .buffer(3)
            .subscribe(lista -> System.out.println("Buffer: " + lista));

        // buffer con skip (ventana deslizante)
        System.out.println("\n--- buffer() con skip ---");
        Observable.range(1, 10)
            .buffer(3, 2) // Tamaño 3, saltar 2
            .subscribe(lista -> System.out.println("Buffer deslizante: " + lista));

        // buffer con tiempo
        System.out.println("\n--- buffer() con tiempo ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(10)
            .buffer(500, TimeUnit.MILLISECONDS)
            .blockingSubscribe(lista -> System.out.println("Buffer temporal: " + lista));

        // 2. window() - Agrupa elementos en Observables
        System.out.println("\n--- window() básico ---");
        Observable.range(1, 10)
            .window(3)
            .subscribe(ventana -> {
                System.out.print("Ventana: ");
                ventana.subscribe(
                    item -> System.out.print(item + " "),
                    error -> System.err.println("Error"),
                    () -> System.out.println()
                );
            });

        // window con tiempo
        System.out.println("\n--- window() con tiempo ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(10)
            .window(300, TimeUnit.MILLISECONDS)
            .subscribe(ventana -> {
                System.out.print("Ventana temporal: ");
                ventana.subscribe(
                    item -> System.out.print(item + " "),
                    error -> System.err.println("Error"),
                    () -> System.out.println()
                );
            });

        Thread.sleep(1000);

        // 3. sample() - Muestra periódicamente
        System.out.println("\n--- sample() ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(20)
            .sample(300, TimeUnit.MILLISECONDS)
            .blockingSubscribe(numero -> System.out.println("Sample: " + numero));

        // 4. throttleFirst() - Primer elemento en cada ventana de tiempo
        System.out.println("\n--- throttleFirst() ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(20)
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .blockingSubscribe(numero -> System.out.println("Throttle first: " + numero));

        // 5. throttleLast() - Último elemento en cada ventana de tiempo
        System.out.println("\n--- throttleLast() ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(20)
            .throttleLast(300, TimeUnit.MILLISECONDS)
            .blockingSubscribe(numero -> System.out.println("Throttle last: " + numero));

        // 6. Ejemplo de backpressure con Flowable
        System.out.println("\n--- Backpressure con Flowable ---");
        Flowable.range(1, 1000)
            .onBackpressureBuffer(10) // Buffer de 10 elementos
            .observeOn(Schedulers.computation())
            .subscribe(
                numero -> {
                    System.out.println("Procesando: " + numero);
                    Thread.sleep(100); // Simula procesamiento lento
                },
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );

        Thread.sleep(2000);

        // 7. onBackpressureDrop() - Descarta elementos cuando hay backpressure
        System.out.println("\n--- onBackpressureDrop() ---");
        Flowable.range(1, 100)
            .onBackpressureDrop(dropped -> System.out.println("Descartado: " + dropped))
            .observeOn(Schedulers.computation())
            .subscribe(
                numero -> {
                    System.out.println("Procesando: " + numero);
                    Thread.sleep(200); // Procesamiento lento
                },
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );

        Thread.sleep(3000);

        // 8. onBackpressureLatest() - Mantiene solo el último elemento
        System.out.println("\n--- onBackpressureLatest() ---");
        Flowable.range(1, 100)
            .onBackpressureLatest()
            .observeOn(Schedulers.computation())
            .subscribe(
                numero -> {
                    System.out.println("Procesando: " + numero);
                    Thread.sleep(200); // Procesamiento lento
                },
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );

        Thread.sleep(3000);

        // 9. onBackpressureError() - Genera error cuando hay backpressure (usando onBackpressureDrop)
        System.out.println("\n--- onBackpressureError() (simulado con drop) ---");
        try {
            Flowable.range(1, 100)
                .onBackpressureDrop(dropped -> System.out.println("Descartado: " + dropped))
                .observeOn(Schedulers.computation())
                .subscribe(
                    numero -> {
                        System.out.println("Procesando: " + numero);
                        Thread.sleep(200); // Procesamiento lento
                    },
                    error -> System.err.println("Backpressure error: " + error),
                    () -> System.out.println("Completado")
                );
        } catch (Exception e) {
            System.err.println("Error capturado: " + e.getMessage());
        }

        Thread.sleep(1000);

        // 10. Ejemplo práctico: Procesamiento de logs
        System.out.println("\n--- Ejemplo práctico: Procesamiento de logs ---");
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .map(tick -> "Log " + tick + " - " + System.currentTimeMillis())
            .take(20)
            .buffer(5) // Procesar logs en lotes de 5
            .subscribe(
                lote -> {
                    System.out.println("Procesando lote de " + lote.size() + " logs:");
                    lote.forEach(log -> System.out.println("  " + log));
                    System.out.println("Lote procesado\n");
                }
            );

        Thread.sleep(2000);

        // 11. Ejemplo práctico: Rate limiting de API
        System.out.println("\n--- Ejemplo práctico: Rate limiting de API ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .map(tick -> "API Call " + tick)
            .take(20)
            .throttleFirst(500, TimeUnit.MILLISECONDS) // Máximo 1 llamada cada 500ms
            .subscribe(
                llamada -> {
                    System.out.println("Ejecutando: " + llamada);
                    Thread.sleep(100); // Simula llamada a API
                }
            );

        Thread.sleep(3000);

        // 12. Ejemplo práctico: Agregación de métricas
        System.out.println("\n--- Ejemplo práctico: Agregación de métricas ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .map(tick -> (int) (Math.random() * 100)) // Métrica aleatoria
            .take(30)
            .buffer(1, TimeUnit.SECONDS) // Agrupar por segundo
            .subscribe(
                metricas -> {
                    int suma = metricas.stream().mapToInt(Integer::intValue).sum();
                    double promedio = metricas.stream().mapToInt(Integer::intValue).average().orElse(0);
                    System.out.println("Métricas del segundo: " + metricas.size() + " valores, suma: " + suma + ", promedio: " + String.format("%.2f", promedio));
                }
            );

        Thread.sleep(2000);

        // 13. Ejemplo avanzado: Circuit breaker con backpressure
        System.out.println("\n--- Ejemplo avanzado: Circuit breaker con backpressure ---");
        Flowable.range(1, 50)
            .onBackpressureBuffer(5) // Buffer pequeño
            .flatMap(numero -> 
                Flowable.fromCallable(() -> {
                    if (Math.random() < 0.3) {
                        Thread.sleep(100); // Respuesta rápida
                        return "OK-" + numero;
                    } else {
                        Thread.sleep(1000); // Respuesta lenta
                        return "SLOW-" + numero;
                    }
                })
                .subscribeOn(Schedulers.io())
                .timeout(500, TimeUnit.MILLISECONDS)
                .onErrorReturn(error -> "TIMEOUT-" + numero)
            )
            .observeOn(Schedulers.computation())
            .subscribe(
                resultado -> System.out.println("Resultado: " + resultado),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Circuit breaker completado")
            );

        Thread.sleep(5000);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• buffer: Agrupa elementos en listas (útil para procesamiento por lotes)");
        System.out.println("• window: Agrupa elementos en Observables (útil para streaming)");
        System.out.println("• sample/throttle: Limita la tasa de emisión");
        System.out.println("• onBackpressureBuffer: Almacena elementos en buffer");
        System.out.println("• onBackpressureDrop: Descarta elementos cuando hay backpressure");
        System.out.println("• onBackpressureLatest: Mantiene solo el último elemento");
        System.out.println("• onBackpressureError: Genera error cuando hay backpressure");
        System.out.println("• Flowable: Mejor para backpressure que Observable");
        System.out.println("• Rate limiting: Controla la velocidad de procesamiento");
        System.out.println("• Circuit breaker: Patrón para servicios inestables");
    }
}
