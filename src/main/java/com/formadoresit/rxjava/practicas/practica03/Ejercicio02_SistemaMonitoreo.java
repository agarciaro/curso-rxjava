package com.formadoresit.rxjava.practicas.practica03;

import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * PRÁCTICA 03 - Ejercicio 3.2: Sistema de Monitoreo de Sensores
 * 
 * Aplicar operadores de filtrado: filter, debounce, distinct, take, sample
 */
public class Ejercicio02_SistemaMonitoreo {

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3.2: Sistema de Monitoreo de Sensores ===\n");

        // Simular lecturas de temperatura cada 100ms
        Observable<Double> sensorTemperatura = Observable
            .interval(100, TimeUnit.MILLISECONDS)
            .map(n -> 15.0 + Math.random() * 20)  // Temperaturas entre 15-35°C
            .take(20);  // Solo 20 lecturas

        System.out.println("--- Monitoreo de Temperatura ---\n");
        System.out.println("Procesando lecturas del sensor...\n");

        sensorTemperatura
            // 1. Mostrar todas las lecturas
            .doOnNext(temp -> System.out.printf("Lectura: %.1f°C\n", temp))
            
            // 2. Filtrar temperaturas altas (> 30°C)
            .filter(temp -> {
                if (temp > 30) {
                    System.out.printf("  ⚠️ ALERTA: Temperatura alta %.1f°C\n", temp);
                    return true;
                }
                return false;
            })
            
            // 3. Debounce para evitar múltiples alertas consecutivas
            .debounce(200, TimeUnit.MILLISECONDS)
            .doOnNext(temp -> System.out.printf("  📢 Alerta después de debounce: %.1f°C\n", temp))
            
            // 4. distinct para no repetir misma temperatura
            .distinct()
            
            // Suscripción bloqueante para ver resultados
            .blockingSubscribe(
                temp -> System.out.printf("  ✓ Alerta única registrada: %.1f°C\n\n", temp),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("\n✓ Monitoreo completado")
            );

        // Demostración adicional: sample
        System.out.println("\n\n--- Demostración de sample() ---");
        System.out.println("Muestreando temperatura cada 500ms:\n");

        Observable.interval(100, TimeUnit.MILLISECONDS)
            .map(n -> 20.0 + Math.random() * 10)
            .take(30)
            .sample(500, TimeUnit.MILLISECONDS)
            .blockingSubscribe(
                temp -> System.out.printf("Muestra: %.1f°C\n", temp),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("\n✓ Muestreo completado")
            );
    }
}

