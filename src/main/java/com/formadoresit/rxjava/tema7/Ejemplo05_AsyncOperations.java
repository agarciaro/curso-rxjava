package com.formadoresit.rxjava.tema7;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 7: Concurrencia
 * Ejemplo 05: Operaciones Asíncronas
 * 
 * Integración con CompletableFuture, callbacks asíncronos,
 * y patrones de programación asíncrona
 */
public class Ejemplo05_AsyncOperations {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 05: Operaciones Asíncronas ===\n");

        // 1. CompletableFuture a Observable
        System.out.println("--- CompletableFuture a Observable ---");
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log("CompletableFuture", "Procesando...");
            simularTrabajo(200);
            return "Resultado del Future";
        });

        Observable.fromFuture(future)
            .subscribeOn(Schedulers.io())
            .subscribe(resultado -> log("Future Result", resultado));

        Thread.sleep(300);

        // 2. Observable a Future
        System.out.println("\n--- Observable a Future ---");
        Observable<String> observable = Observable.just("Datos del Observable")
            .subscribeOn(Schedulers.computation())
            .map(datos -> {
                log("Observable", datos);
                simularTrabajo(150);
                return datos.toUpperCase();
            });

        // Simular conversión a Future
        observable.subscribe(resultado -> 
            log("Future from Observable", resultado)
        );

        Thread.sleep(200);

        // 3. Múltiples CompletableFuture
        System.out.println("\n--- Múltiples CompletableFuture ---");
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            log("Future 1", "Procesando...");
            simularTrabajo(100);
            return "Resultado 1";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            log("Future 2", "Procesando...");
            simularTrabajo(150);
            return "Resultado 2";
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            log("Future 3", "Procesando...");
            simularTrabajo(120);
            return "Resultado 3";
        });

        Observable.zip(
            Observable.fromFuture(future1),
            Observable.fromFuture(future2),
            Observable.fromFuture(future3),
            (r1, r2, r3) -> r1 + " + " + r2 + " + " + r3
        )
        .subscribe(resultado -> log("Zipped Futures", resultado));

        Thread.sleep(300);

        // 4. Callbacks asíncronos
        System.out.println("\n--- Callbacks asíncronos ---");
        AtomicInteger callbackCount = new AtomicInteger(0);
        
        Observable.range(1, 5)
            .flatMap(n -> 
                Observable.create(emitter -> {
                    // Simular callback asíncrono
                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                            log("Callback", n);
                            emitter.onNext("Callback " + n);
                            emitter.onComplete();
                        } catch (InterruptedException e) {
                            emitter.onError(e);
                        }
                    }).start();
                })
                .subscribeOn(Schedulers.io())
            )
            .subscribe(resultado -> log("Callback Result", resultado));

        Thread.sleep(600);

        // 5. Operaciones asíncronas con timeout
        System.out.println("\n--- Operaciones asíncronas con timeout ---");
        Observable.fromCallable(() -> {
            log("Async Operation", "Iniciando...");
            simularTrabajo(300);
            return "Operación completada";
        })
        .subscribeOn(Schedulers.io())
        .timeout(200, TimeUnit.MILLISECONDS)
        .onErrorReturn(error -> "Timeout: " + error.getMessage())
        .subscribe(resultado -> log("Timeout Result", resultado));

        Thread.sleep(400);

        // 6. Patrón async/await con RxJava
        System.out.println("\n--- Patrón async/await ---");
        Observable<String> asyncOperation1 = Observable.fromCallable(() -> {
            log("Async 1", "Procesando...");
            simularTrabajo(100);
            return "Async 1 completado";
        }).subscribeOn(Schedulers.io());

        Observable<String> asyncOperation2 = Observable.fromCallable(() -> {
            log("Async 2", "Procesando...");
            simularTrabajo(150);
            return "Async 2 completado";
        }).subscribeOn(Schedulers.io());

        asyncOperation1
            .flatMap(result1 -> 
                asyncOperation2.map(result2 -> result1 + " + " + result2)
            )
            .subscribe(resultado -> log("Async/Await Result", resultado));

        Thread.sleep(300);

        // 7. Operaciones asíncronas con retry
        System.out.println("\n--- Operaciones asíncronas con retry ---");
        AtomicInteger retryCount = new AtomicInteger(0);
        
        Observable.fromCallable(() -> {
            int count = retryCount.incrementAndGet();
            log("Async Retry", "Intento " + count);
            
            if (count < 3) {
                simularTrabajo(100);
                throw new RuntimeException("Error simulado");
            }
            
            simularTrabajo(100);
            return "Éxito después de " + count + " intentos";
        })
        .subscribeOn(Schedulers.io())
        .retry(3)
        .onErrorReturn(error -> "Falló después de todos los reintentos")
        .subscribe(resultado -> log("Retry Result", resultado));

        Thread.sleep(500);

        // 8. Operaciones asíncronas con circuit breaker
        System.out.println("\n--- Operaciones asíncronas con circuit breaker ---");
        AtomicInteger fallos = new AtomicInteger(0);
        AtomicInteger exitos = new AtomicInteger(0);
        
        Observable.range(1, 6)
            .flatMap(n -> 
                Observable.fromCallable(() -> {
                    if (fallos.get() >= 2) {
                        log("Circuit Breaker", "Circuito abierto");
                        return "Circuito abierto";
                    }
                    
                    if (n % 3 == 0) {
                        fallos.incrementAndGet();
                        log("Async Fallo", n);
                        throw new RuntimeException("Error asíncrono");
                    }
                    
                    exitos.incrementAndGet();
                    log("Async Éxito", n);
                    simularTrabajo(100);
                    return "Éxito asíncrono: " + n;
                })
                .subscribeOn(Schedulers.io())
                .onErrorReturn(error -> "Error manejado: " + n)
            )
            .subscribe(resultado -> log("Circuit Breaker Result", resultado));

        Thread.sleep(600);

        // 9. Operaciones asíncronas con backpressure
        System.out.println("\n--- Operaciones asíncronas con backpressure ---");
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(10)
            .flatMap(n -> 
                Observable.fromCallable(() -> {
                    log("Async Backpressure", n);
                    simularTrabajo(100);
                    return "Procesado: " + n;
                })
                .subscribeOn(Schedulers.io())
            )
            .buffer(3) // Agrupar para manejar backpressure
            .subscribe(lote -> log("Backpressure Batch", lote));

        Thread.sleep(800);

        // 10. Operaciones asíncronas con métricas
        System.out.println("\n--- Operaciones asíncronas con métricas ---");
        AtomicInteger operacionesCompletadas = new AtomicInteger(0);
        AtomicInteger operacionesFallidas = new AtomicInteger(0);
        
        Observable.range(1, 5)
            .flatMap(n -> 
                Observable.fromCallable(() -> {
                    log("Async Métricas", n);
                    simularTrabajo(100);
                    
                    if (n % 4 == 0) {
                        operacionesFallidas.incrementAndGet();
                        throw new RuntimeException("Error de métricas");
                    }
                    
                    operacionesCompletadas.incrementAndGet();
                    return "Métricas: " + n;
                })
                .subscribeOn(Schedulers.io())
                .onErrorReturn(error -> "Error en métricas: " + n)
            )
            .subscribe(resultado -> {
                log("Métricas Result", resultado);
                log("Estadísticas", "Completadas: " + operacionesCompletadas.get() + 
                    ", Fallidas: " + operacionesFallidas.get());
            });

        Thread.sleep(600);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• CompletableFuture: Integración con Java 8+ async");
        System.out.println("• fromFuture(): Convertir Future a Observable");
        System.out.println("• toFuture(): Convertir Observable a Future");
        System.out.println("• Callbacks: Patrón tradicional de async");
        System.out.println("• async/await: Patrón moderno de programación async");
        System.out.println("• timeout: Evitar operaciones que tardan demasiado");
        System.out.println("• retry: Recuperación de errores en operaciones async");
        System.out.println("• Circuit breaker: Prevenir cascadas de fallos");
        System.out.println("• Backpressure: Manejar sobrecarga en operaciones async");
        System.out.println("• Métricas: Monitorear operaciones asíncronas");
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
