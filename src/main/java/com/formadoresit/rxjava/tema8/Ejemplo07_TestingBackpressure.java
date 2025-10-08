package com.formadoresit.rxjava.tema8;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 8: Flujos y Backpressure
 * Ejemplo 07: Testing de Backpressure
 * 
 * Testing de código con backpressure,
 * TestSubscriber, TestScheduler y técnicas de testing
 */
public class Ejemplo07_TestingBackpressure {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 07: Testing de Backpressure ===\n");

        // 1. TestSubscriber básico
        System.out.println("--- TestSubscriber básico ---");
        TestSubscriber<Integer> testSubscriber = Flowable.range(1, 10)
            .onBackpressureBuffer(5)
            .observeOn(Schedulers.computation())
            .map(n -> n * 2)
            .test();

        testSubscriber.assertValues(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        testSubscriber.assertComplete();
        log("TEST", "TestSubscriber básico completado");

        // 2. TestSubscriber con backpressure
        System.out.println("\n--- TestSubscriber con backpressure ---");
        TestSubscriber<Long> backpressureTest = Flowable.interval(10, TimeUnit.MILLISECONDS)
            .take(20)
            .onBackpressureDrop(dropped -> log("TEST", "Descartado: " + dropped))
            .observeOn(Schedulers.computation())
            .map(n -> n * 3)
            .test();

        Thread.sleep(500); // Esperar a que se procese
        backpressureTest.assertValueCount(20);
        backpressureTest.assertComplete();
        log("TEST", "TestSubscriber con backpressure completado");

        // 3. TestSubscriber con métricas
        System.out.println("\n--- TestSubscriber con métricas ---");
        AtomicInteger processedCount = new AtomicInteger(0);
        
        TestSubscriber<String> metricsTest = Flowable.range(1, 15)
            .onBackpressureBuffer(5, () -> log("TEST", "Buffer lleno"))
            .observeOn(Schedulers.computation())
            .map(n -> {
                processedCount.incrementAndGet();
                return "Procesado: " + n;
            })
            .test();

        Thread.sleep(300);
        metricsTest.assertValueCount(15);
        metricsTest.assertComplete();
        log("TEST", "Procesados: " + processedCount.get());
        log("TEST", "TestSubscriber con métricas completado");

        // 4. TestScheduler con backpressure
        System.out.println("\n--- TestScheduler con backpressure ---");
        TestScheduler testScheduler = new TestScheduler();
        
        TestSubscriber<Long> schedulerTest = Flowable.interval(100, TimeUnit.MILLISECONDS, testScheduler)
            .take(10)
            .onBackpressureDrop(dropped -> log("TEST", "Descartado en scheduler: " + dropped))
            .observeOn(Schedulers.computation())
            .map(n -> n * 2)
            .test();

        // Avanzar tiempo virtual
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        schedulerTest.assertValueCount(5);
        
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        schedulerTest.assertValueCount(10);
        schedulerTest.assertComplete();
        log("TEST", "TestScheduler con backpressure completado");

        // 5. Testing de estrategias de backpressure
        System.out.println("\n--- Testing de estrategias de backpressure ---");
        
        // BUFFER
        TestSubscriber<Integer> bufferTest = Flowable.range(1, 20)
            .onBackpressureBuffer(10)
            .observeOn(Schedulers.computation())
            .map(n -> n * 2)
            .test();

        Thread.sleep(200);
        bufferTest.assertValueCount(20);
        bufferTest.assertComplete();
        log("TEST", "BUFFER strategy test completado");

        // DROP
        TestSubscriber<Long> dropTest = Flowable.interval(5, TimeUnit.MILLISECONDS)
            .take(50)
            .onBackpressureDrop(dropped -> log("TEST", "DROP: " + dropped))
            .observeOn(Schedulers.computation())
            .map(n -> n * 3)
            .test();

        Thread.sleep(300);
        dropTest.assertValueCount(50);
        dropTest.assertComplete();
        log("TEST", "DROP strategy test completado");

        // LATEST
        TestSubscriber<Long> latestTest = Flowable.interval(5, TimeUnit.MILLISECONDS)
            .take(50)
            .onBackpressureLatest()
            .observeOn(Schedulers.computation())
            .map(n -> n * 4)
            .test();

        Thread.sleep(300);
        latestTest.assertValueCount(50);
        latestTest.assertComplete();
        log("TEST", "LATEST strategy test completado");

        // 6. Testing de operadores de backpressure
        System.out.println("\n--- Testing de operadores de backpressure ---");
        
        // buffer
        TestSubscriber<java.util.List<Integer>> bufferOpTest = Flowable.range(1, 20)
            .buffer(5)
            .observeOn(Schedulers.computation())
            .map(lote -> {
                log("TEST", "Procesando lote: " + lote);
                return lote;
            })
            .test();

        Thread.sleep(200);
        bufferOpTest.assertValueCount(4); // 20/5 = 4 lotes
        bufferOpTest.assertComplete();
        log("TEST", "buffer operator test completado");

        // window
        TestSubscriber<java.util.List<Long>> windowOpTest = Flowable.interval(20, TimeUnit.MILLISECONDS)
            .take(15)
            .window(3)
            .flatMap(window -> 
                window
                    .observeOn(Schedulers.computation())
                    .map(n -> n * 2)
                    .toList()
                    .toFlowable()
            )
            .test();

        Thread.sleep(400);
        windowOpTest.assertValueCount(5); // 15/3 = 5 ventanas
        windowOpTest.assertComplete();
        log("TEST", "window operator test completado");

        // sample
        TestSubscriber<Long> sampleOpTest = Flowable.interval(10, TimeUnit.MILLISECONDS)
            .take(30)
            .sample(100, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map(n -> n * 3)
            .test();

        Thread.sleep(400);
        sampleOpTest.assertValueCount(3); // Aproximadamente 3 muestras en 300ms
        sampleOpTest.assertComplete();
        log("TEST", "sample operator test completado");

        // 7. Testing de control manual
        System.out.println("\n--- Testing de control manual ---");
        TestSubscriber<Integer> manualTest = Flowable.range(1, 20)
            .test();

        manualTest.assertValueCount(20);
        manualTest.assertComplete();
        log("TEST", "Control manual test completado");

        // 8. Testing de flujos complejos
        System.out.println("\n--- Testing de flujos complejos ---");
        TestSubscriber<String> complexTest = Flowable.range(1, 15)
            .onBackpressureBuffer(5)
            .observeOn(Schedulers.computation())
            .map(n -> n * 2)
            .filter(n -> n % 4 == 0)
            .map(n -> "Resultado: " + n)
            .test();

        Thread.sleep(200);
        complexTest.assertValueCount(7); // 15 * 2 = 30, 30/4 = 7.5 -> 7
        complexTest.assertComplete();
        log("TEST", "Flujo complejo test completado");

        // 9. Testing de rendimiento
        System.out.println("\n--- Testing de rendimiento ---");
        long startTime = System.currentTimeMillis();
        
        TestSubscriber<Integer> performanceTest = Flowable.range(1, 100)
            .onBackpressureBuffer(20)
            .observeOn(Schedulers.computation())
            .map(n -> n * 3)
            .test();

        Thread.sleep(300);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        performanceTest.assertValueCount(100);
        performanceTest.assertComplete();
        log("TEST", "Rendimiento: " + duration + "ms para 100 elementos");
        log("TEST", "Throughput: " + (100 * 1000.0 / duration) + " elementos/seg");

        // 10. Testing de errores con backpressure
        System.out.println("\n--- Testing de errores con backpressure ---");
        TestSubscriber<Integer> errorTest = Flowable.range(1, 10)
            .onBackpressureBuffer(5)
            .observeOn(Schedulers.computation())
            .map(n -> {
                if (n == 5) throw new RuntimeException("Error simulado");
                return n * 2;
            })
            .test();

        Thread.sleep(200);
        errorTest.assertError(RuntimeException.class);
        errorTest.assertValueCount(4); // Solo los primeros 4 valores
        log("TEST", "Error con backpressure test completado");

        // 11. Testing de métricas avanzadas
        System.out.println("\n--- Testing de métricas avanzadas ---");
        AtomicInteger totalEmitidos = new AtomicInteger(0);
        AtomicInteger totalProcesados = new AtomicInteger(0);
        AtomicInteger totalDescartados = new AtomicInteger(0);
        
        TestSubscriber<Integer> advancedMetricsTest = Flowable.range(1, 25)
            .map(n -> {
                totalEmitidos.incrementAndGet();
                return n;
            })
            .onBackpressureDrop(dropped -> {
                totalDescartados.incrementAndGet();
                log("TEST", "Descartado: " + dropped);
            })
            .observeOn(Schedulers.computation())
            .map(n -> {
                totalProcesados.incrementAndGet();
                return n * 2;
            })
            .test();

        Thread.sleep(300);
        advancedMetricsTest.assertValueCount(25);
        advancedMetricsTest.assertComplete();
        log("TEST", "Emitidos: " + totalEmitidos.get());
        log("TEST", "Procesados: " + totalProcesados.get());
        log("TEST", "Descartados: " + totalDescartados.get());
        log("TEST", "Eficiencia: " + (totalProcesados.get() * 100.0 / totalEmitidos.get()) + "%");

        // 12. Testing de diferentes velocidades
        System.out.println("\n--- Testing de diferentes velocidades ---");
        
        // Productor rápido
        TestSubscriber<Long> fastProducerTest = Flowable.interval(5, TimeUnit.MILLISECONDS)
            .take(20)
            .onBackpressureDrop(dropped -> log("TEST", "Descartado rápido: " + dropped))
            .observeOn(Schedulers.computation())
            .map(n -> n * 2)
            .test();

        Thread.sleep(200);
        fastProducerTest.assertValueCount(20);
        fastProducerTest.assertComplete();
        log("TEST", "Productor rápido test completado");

        // Productor lento
        TestSubscriber<Integer> slowProducerTest = Flowable.interval(100, TimeUnit.MILLISECONDS)
            .take(5)
            .map(n -> n.intValue())
            .onBackpressureBuffer(10)
            .observeOn(Schedulers.computation())
            .map(n -> n * 3)
            .test();

        Thread.sleep(600);
        slowProducerTest.assertValueCount(5);
        slowProducerTest.assertComplete();
        log("TEST", "Productor lento test completado");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• TestSubscriber: Observador para testing con aserciones");
        System.out.println("• TestScheduler: Control total sobre el tiempo virtual");
        System.out.println("• Testing de estrategias: Verificar comportamiento de backpressure");
        System.out.println("• Testing de operadores: Verificar operadores específicos");
        System.out.println("• Testing de control manual: Verificar control de suscripción");
        System.out.println("• Testing de flujos complejos: Verificar pipelines completos");
        System.out.println("• Testing de rendimiento: Medir throughput y eficiencia");
        System.out.println("• Testing de errores: Verificar manejo de errores con backpressure");
        System.out.println("• Testing de métricas: Verificar contadores y estadísticas");
        System.out.println("• Testing de velocidades: Verificar comportamiento con diferentes velocidades");
    }

    private static void log(String operacion, Object valor) {
        System.out.printf("  [%s] %s: %s\n", 
            Thread.currentThread().getName(), 
            operacion, 
            valor);
    }
}
