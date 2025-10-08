package com.formadoresit.rxjava.tema7;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 7: Concurrencia
 * Ejemplo 07: Error Handling con Concurrencia
 * 
 * Manejo de errores en contextos concurrentes,
 * propagación de errores, recuperación y circuit breakers
 */
public class Ejemplo07_ErrorHandlingConcurrencia {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 07: Error Handling con Concurrencia ===\n");

        // 1. Propagación de errores en operaciones concurrentes
        System.out.println("--- Propagación de errores concurrentes ---");
        Observable.range(1, 5)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("Procesando", x);
                        if (x == 3) {
                            throw new RuntimeException("Error en operación concurrente: " + x);
                        }
                        simularTrabajo(100);
                        return x * 2;
                    })
            )
            .subscribe(
                resultado -> log("Resultado", resultado),
                error -> log("Error propagado", error.getMessage())
            );

        Thread.sleep(500);

        // 2. Recuperación de errores con retry
        System.out.println("\n--- Recuperación con retry ---");
        Observable.range(1, 4)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.computation())
                    .map(x -> {
                        log("Retry Processing", x);
                        if (x == 2) {
                            throw new RuntimeException("Error temporal: " + x);
                        }
                        simularTrabajo(80);
                        return x * 3;
                    })
                    .retry(2)
                    .onErrorReturn(error -> -1) // Valor por defecto si falla
            )
            .subscribe(resultado -> log("Retry Result", resultado));

        Thread.sleep(600);

        // 3. Circuit breaker en operaciones concurrentes
        System.out.println("\n--- Circuit breaker concurrente ---");
        AtomicInteger fallos = new AtomicInteger(0);
        AtomicInteger exitos = new AtomicInteger(0);
        
        Observable.range(1, 8)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        if (fallos.get() >= 3) {
                            log("Circuit Breaker", "Circuito abierto para: " + x);
                            return "Circuito abierto: " + x;
                        }
                        
                        if (x % 4 == 0) {
                            fallos.incrementAndGet();
                            log("Fallando", x);
                            throw new RuntimeException("Error de circuit breaker: " + x);
                        }
                        
                        exitos.incrementAndGet();
                        log("Exitoso", x);
                        simularTrabajo(100);
                        return "Éxito: " + x;
                    })
                    .onErrorReturn(error -> "Error manejado: " + n)
            )
            .subscribe(resultado -> log("Circuit Breaker Result", resultado));

        Thread.sleep(800);

        // 4. Timeout en operaciones concurrentes
        System.out.println("\n--- Timeout concurrente ---");
        Observable.range(1, 5)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("Timeout Processing", x);
                        simularTrabajo(100 + x * 50); // Algunos tardan más
                        return x * 4;
                    })
                    .timeout(200, TimeUnit.MILLISECONDS)
                    .onErrorReturn(error -> -1) // Timeout
            )
            .subscribe(resultado -> log("Timeout Result", resultado));

        Thread.sleep(600);

        // 5. Manejo de errores con fallback
        System.out.println("\n--- Fallback concurrente ---");
        Observable.range(1, 4)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.computation())
                    .map(x -> {
                        log("Fallback Processing", x);
                        if (x == 2) {
                            throw new RuntimeException("Error crítico: " + x);
                        }
                        simularTrabajo(90);
                        return x * 5;
                    })
                    .onErrorResumeNext((Throwable error) -> 
                        Observable.just(-1) // Fallback
                            .subscribeOn(Schedulers.single())
                    )
            )
            .subscribe(resultado -> log("Fallback Result", resultado));

        Thread.sleep(500);

        // 6. Errores con métricas
        System.out.println("\n--- Errores con métricas ---");
        AtomicInteger erroresTotales = new AtomicInteger(0);
        AtomicInteger erroresRecuperados = new AtomicInteger(0);
        AtomicInteger erroresNoRecuperados = new AtomicInteger(0);
        
        Observable.range(1, 6)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("Métricas Processing", x);
                        if (x % 3 == 0) {
                            erroresTotales.incrementAndGet();
                            throw new RuntimeException("Error de métricas: " + x);
                        }
                        simularTrabajo(100);
                        return x * 6;
                    })
                    .retry(1)
                    .onErrorReturn(error -> {
                        erroresNoRecuperados.incrementAndGet();
                        return -1;
                    })
            )
            .subscribe(
                resultado -> log("Métricas Result", resultado),
                error -> log("Métricas Error", error.getMessage()),
                () -> {
                    log("Métricas Final", "Errores totales: " + erroresTotales.get());
                    log("Métricas Final", "Errores recuperados: " + erroresRecuperados.get());
                    log("Métricas Final", "Errores no recuperados: " + erroresNoRecuperados.get());
                }
            );

        Thread.sleep(600);

        // 7. Errores con logging
        System.out.println("\n--- Errores con logging ---");
        Observable.range(1, 5)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.computation())
                    .map(x -> {
                        log("Logging Processing", x);
                        if (x == 3) {
                            log("ERROR", "Error crítico en operación: " + x);
                            throw new RuntimeException("Error de logging: " + x);
                        }
                        simularTrabajo(80);
                        return x * 7;
                    })
                    .doOnError(error -> 
                        log("ERROR LOG", "Error registrado: " + error.getMessage())
                    )
                    .onErrorReturn(error -> -1)
            )
            .subscribe(resultado -> log("Logging Result", resultado));

        Thread.sleep(500);

        // 8. Errores con notificaciones
        System.out.println("\n--- Errores con notificaciones ---");
        Observable.range(1, 4)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("Notificación Processing", x);
                        if (x == 2) {
                            log("ALERTA", "Error crítico detectado: " + x);
                            throw new RuntimeException("Error de notificación: " + x);
                        }
                        simularTrabajo(100);
                        return x * 8;
                    })
                    .onErrorResumeNext(error -> {
                        log("NOTIFICACIÓN", "Enviando alerta por error: " + error.getMessage());
                        return Observable.just(-1);
                    })
            )
            .subscribe(resultado -> log("Notificación Result", resultado));

        Thread.sleep(500);

        // 9. Errores con rollback
        System.out.println("\n--- Errores con rollback ---");
        Observable.range(1, 5)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.computation())
                    .map(x -> {
                        log("Rollback Processing", x);
                        if (x == 3) {
                            log("ROLLBACK", "Iniciando rollback para: " + x);
                            throw new RuntimeException("Error de rollback: " + x);
                        }
                        simularTrabajo(90);
                        return x * 9;
                    })
                    .onErrorResumeNext(error -> {
                        log("ROLLBACK", "Ejecutando rollback: " + error.getMessage());
                        return Observable.just(-1);
                    })
            )
            .subscribe(resultado -> log("Rollback Result", resultado));

        Thread.sleep(500);

        // 10. Errores con recovery automático
        System.out.println("\n--- Recovery automático ---");
        AtomicInteger intentosRecovery = new AtomicInteger(0);
        
        Observable.range(1, 6)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("Recovery Processing", x);
                        if (x % 3 == 0 && intentosRecovery.get() < 2) {
                            intentosRecovery.incrementAndGet();
                            log("RECOVERY", "Intentando recovery para: " + x);
                            throw new RuntimeException("Error de recovery: " + x);
                        }
                        simularTrabajo(100);
                        return x * 10;
                    })
                    .retryWhen(errors -> 
                        errors.flatMap(error -> {
                            log("RECOVERY", "Ejecutando recovery automático");
                            return Observable.timer(200, TimeUnit.MILLISECONDS);
                        })
                    )
                    .onErrorReturn(error -> -1)
            )
            .subscribe(resultado -> log("Recovery Result", resultado));

        Thread.sleep(800);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Propagación: Errores se propagan a través de operaciones concurrentes");
        System.out.println("• Retry: Reintentar operaciones que fallan");
        System.out.println("• Circuit breaker: Prevenir cascadas de errores");
        System.out.println("• Timeout: Evitar operaciones que tardan demasiado");
        System.out.println("• Fallback: Valores alternativos cuando fallan operaciones");
        System.out.println("• Métricas: Monitorear frecuencia y tipos de errores");
        System.out.println("• Logging: Registrar errores para debugging");
        System.out.println("• Notificaciones: Alertar sobre errores críticos");
        System.out.println("• Rollback: Deshacer cambios cuando fallan operaciones");
        System.out.println("• Recovery: Recuperación automática de errores");
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
