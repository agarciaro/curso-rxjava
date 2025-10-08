package com.formadoresit.rxjava.tema7;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.observers.TestObserver;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 7: Concurrencia
 * Ejemplo 08: Testing de Concurrencia
 * 
 * Testing de código concurrente, TestScheduler,
 * TestObserver y técnicas de testing para RxJava
 */
public class Ejemplo08_TestingConcurrencia {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 08: Testing de Concurrencia ===\n");

        // 1. TestScheduler básico
        System.out.println("--- TestScheduler básico ---");
        TestScheduler testScheduler = new TestScheduler();
        
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS, testScheduler)
            .take(5)
            .map(n -> n * 2);
        
        TestObserver<Long> testObserver = observable.test();
        
        // Avanzar tiempo virtual
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        testObserver.assertValues(0L, 2L);
        
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        testObserver.assertValues(0L, 2L, 4L, 6L, 8L);
        
        testObserver.assertComplete();
        log("TestScheduler", "Test completado exitosamente");

        // 2. Testing de operaciones concurrentes
        System.out.println("\n--- Testing de operaciones concurrentes ---");
        TestScheduler concurrentScheduler = new TestScheduler();
        
        Observable<Integer> concurrentObservable = Observable.range(1, 3)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(concurrentScheduler)
                    .map(x -> {
                        log("Concurrent Test", x);
                        return x * 10;
                    })
            );
        
        TestObserver<Integer> concurrentObserver = concurrentObservable.test();
        
        // Avanzar tiempo para activar operaciones concurrentes
        concurrentScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        concurrentObserver.assertValues(10, 20, 30);
        concurrentObserver.assertComplete();
        log("Concurrent Test", "Test concurrente completado");

        // 3. Testing de timeout
        System.out.println("\n--- Testing de timeout ---");
        TestScheduler timeoutScheduler = new TestScheduler();
        
        Observable<String> timeoutObservable = Observable.just("Datos")
            .subscribeOn(timeoutScheduler)
            .map(datos -> {
                log("Timeout Test", datos);
                return datos.toUpperCase();
            })
            .timeout(500, TimeUnit.MILLISECONDS, timeoutScheduler);
        
        TestObserver<String> timeoutObserver = timeoutObservable.test();
        
        // Avanzar tiempo para activar
        timeoutScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        timeoutObserver.assertValue("DATOS");
        timeoutObserver.assertComplete();
        log("Timeout Test", "Test de timeout completado");

        // 4. Testing de retry
        System.out.println("\n--- Testing de retry ---");
        TestScheduler retryScheduler = new TestScheduler();
        AtomicInteger retryCount = new AtomicInteger(0);
        
        Observable<String> retryObservable = Observable.fromCallable(() -> {
            int count = retryCount.incrementAndGet();
            log("Retry Test", "Intento " + count);
            if (count < 3) {
                throw new RuntimeException("Error simulado");
            }
            return "Éxito después de " + count + " intentos";
        })
        .subscribeOn(retryScheduler)
        .retry(3);
        
        TestObserver<String> retryObserver = retryObservable.test();
        
        // Avanzar tiempo para activar retry
        retryScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        retryObserver.assertValue("Éxito después de 3 intentos");
        retryObserver.assertComplete();
        log("Retry Test", "Test de retry completado");

        // 5. Testing de backpressure
        System.out.println("\n--- Testing de backpressure ---");
        TestScheduler backpressureScheduler = new TestScheduler();
        
        Observable<java.util.List<Long>> backpressureObservable = Observable.interval(100, TimeUnit.MILLISECONDS, backpressureScheduler)
            .take(10)
            .buffer(3)
            .subscribeOn(backpressureScheduler);
        
        TestObserver<java.util.List<Long>> backpressureObserver = backpressureObservable.test();
        
        // Avanzar tiempo para activar backpressure
        backpressureScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        backpressureObserver.assertValueCount(2); // 2 buffers completos
        log("Backpressure Test", "Test de backpressure completado");

        // 6. Testing de error handling
        System.out.println("\n--- Testing de error handling ---");
        TestScheduler errorScheduler = new TestScheduler();
        
        Observable<String> errorObservable = Observable.range(1, 5)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(errorScheduler)
                    .map(x -> {
                        if (x == 3) {
                            throw new RuntimeException("Error de test: " + x);
                        }
                        return "Éxito: " + x;
                    })
                    .onErrorReturn(error -> "Error manejado: " + n)
            );
        
        TestObserver<String> errorObserver = errorObservable.test();
        
        // Avanzar tiempo para activar
        errorScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        errorObserver.assertValueCount(5);
        errorObserver.assertComplete();
        log("Error Test", "Test de error handling completado");

        // 7. Testing de métricas
        System.out.println("\n--- Testing de métricas ---");
        TestScheduler metricsScheduler = new TestScheduler();
        AtomicInteger processedCount = new AtomicInteger(0);
        
        Observable<Integer> metricsObservable = Observable.range(1, 4)
            .subscribeOn(metricsScheduler)
            .map(n -> {
                processedCount.incrementAndGet();
                log("Metrics Test", n);
                return n * 2;
            });
        
        TestObserver<Integer> metricsObserver = metricsObservable.test();
        
        // Avanzar tiempo para activar
        metricsScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        metricsObserver.assertValues(2, 4, 6, 8);
        metricsObserver.assertComplete();
        
        log("Metrics Test", "Procesados: " + processedCount.get());
        log("Metrics Test", "Test de métricas completado");

        // 8. Testing de concurrencia con múltiples schedulers
        System.out.println("\n--- Testing de múltiples schedulers ---");
        TestScheduler scheduler1 = new TestScheduler();
        TestScheduler scheduler2 = new TestScheduler();
        
        Observable<String> multiSchedulerObservable = Observable.just("Inicio")
            .subscribeOn(scheduler1)
            .map(s -> {
                log("Scheduler 1", s);
                return s + " -> Scheduler1";
            })
            .observeOn(scheduler2)
            .map(s -> {
                log("Scheduler 2", s);
                return s + " -> Scheduler2";
            });
        
        TestObserver<String> multiSchedulerObserver = multiSchedulerObservable.test();
        
        // Avanzar tiempo en ambos schedulers
        scheduler1.advanceTimeBy(1, TimeUnit.SECONDS);
        scheduler2.advanceTimeBy(1, TimeUnit.SECONDS);
        
        multiSchedulerObserver.assertValue("Inicio -> Scheduler1 -> Scheduler2");
        multiSchedulerObserver.assertComplete();
        log("Multi Scheduler Test", "Test de múltiples schedulers completado");

        // 9. Testing de operaciones asíncronas
        System.out.println("\n--- Testing de operaciones asíncronas ---");
        TestScheduler asyncScheduler = new TestScheduler();
        
        Observable<String> asyncObservable = Observable.fromCallable(() -> {
            log("Async Test", "Procesando...");
            return "Resultado asíncrono";
        })
        .subscribeOn(asyncScheduler)
        .delay(500, TimeUnit.MILLISECONDS, asyncScheduler);
        
        TestObserver<String> asyncObserver = asyncObservable.test();
        
        // Avanzar tiempo para activar operación asíncrona
        asyncScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        asyncObserver.assertValue("Resultado asíncrono");
        asyncObserver.assertComplete();
        log("Async Test", "Test de operaciones asíncronas completado");

        // 10. Testing de performance
        System.out.println("\n--- Testing de performance ---");
        TestScheduler performanceScheduler = new TestScheduler();
        long startTime = System.currentTimeMillis();
        
        Observable<Integer> performanceObservable = Observable.range(1, 1000)
            .subscribeOn(performanceScheduler)
            .map(n -> n * 2)
            .filter(n -> n % 4 == 0);
        
        TestObserver<Integer> performanceObserver = performanceObservable.test();
        
        // Avanzar tiempo para activar
        performanceScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        performanceObserver.assertValueCount(250); // 1000/4 = 250
        performanceObserver.assertComplete();
        
        log("Performance Test", "Duración: " + duration + "ms");
        log("Performance Test", "Test de performance completado");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• TestScheduler: Control total sobre el tiempo virtual");
        System.out.println("• TestObserver: Observador para testing con aserciones");
        System.out.println("• advanceTimeBy: Avanzar tiempo virtual para activar operaciones");
        System.out.println("• assertValues: Verificar valores emitidos");
        System.out.println("• assertComplete: Verificar que el Observable se completó");
        System.out.println("• Testing concurrente: Verificar operaciones en múltiples hilos");
        System.out.println("• Testing de timeout: Verificar comportamiento con límites de tiempo");
        System.out.println("• Testing de retry: Verificar reintentos de operaciones");
        System.out.println("• Testing de backpressure: Verificar manejo de sobrecarga");
        System.out.println("• Testing de métricas: Verificar contadores y estadísticas");
        System.out.println("• Testing de performance: Verificar rendimiento de operaciones");
    }

    private static void log(String operacion, Object valor) {
        System.out.printf("  [%s] %s: %s\n", 
            Thread.currentThread().getName(), 
            operacion, 
            valor);
    }
}
