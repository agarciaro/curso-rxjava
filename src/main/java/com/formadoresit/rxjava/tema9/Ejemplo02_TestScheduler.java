package com.formadoresit.rxjava.tema9;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TEMA 9: Pruebas y Depuración
 * Ejemplo 02: TestScheduler
 * 
 * TestScheduler permite controlar el tiempo virtual para probar
 * operadores que dependen del tiempo como delay, interval, timer, etc.
 */
public class Ejemplo02_TestScheduler {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 02: TestScheduler ===\n");

        // 1. Test básico con TestScheduler
        System.out.println("--- Test básico con TestScheduler ---");
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> testObserver = new TestObserver<>();

        Observable.interval(1, TimeUnit.SECONDS, scheduler)
            .take(3)
            .subscribe(testObserver);

        // Inicialmente no hay valores
        testObserver.assertValueCount(0);
        System.out.println("✓ Inicialmente no hay valores");

        // Avanzar 1 segundo
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testObserver.assertValues(0L);
        System.out.println("✓ Después de 1 segundo: " + testObserver.values());

        // Avanzar 2 segundos más
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        testObserver.assertValues(0L, 1L, 2L);
        testObserver.assertComplete();
        System.out.println("✓ Después de 3 segundos total: " + testObserver.values());

        // 2. Test con delay
        System.out.println("\n--- Test con delay ---");
        TestScheduler scheduler2 = new TestScheduler();
        TestObserver<String> testDelay = new TestObserver<>();

        Observable.just("A", "B", "C")
            .delay(2, TimeUnit.SECONDS, scheduler2)
            .subscribe(testDelay);

        // Inicialmente no hay valores
        testDelay.assertValueCount(0);
        System.out.println("✓ Inicialmente no hay valores con delay");

        // Avanzar 1 segundo - aún no hay valores
        scheduler2.advanceTimeBy(1, TimeUnit.SECONDS);
        testDelay.assertValueCount(0);
        System.out.println("✓ Después de 1 segundo: aún no hay valores");

        // Avanzar 1 segundo más - ahora sí hay valores
        scheduler2.advanceTimeBy(1, TimeUnit.SECONDS);
        testDelay.assertValues("A", "B", "C");
        testDelay.assertComplete();
        System.out.println("✓ Después de 2 segundos: " + testDelay.values());

        // 3. Test con timer
        System.out.println("\n--- Test con timer ---");
        TestScheduler scheduler3 = new TestScheduler();
        TestObserver<Long> testTimer = new TestObserver<>();

        Observable.timer(3, TimeUnit.SECONDS, scheduler3)
            .subscribe(testTimer);

        // Inicialmente no hay valores
        testTimer.assertValueCount(0);
        System.out.println("✓ Timer: inicialmente no hay valores");

        // Avanzar 2 segundos - aún no
        scheduler3.advanceTimeBy(2, TimeUnit.SECONDS);
        testTimer.assertValueCount(0);
        System.out.println("✓ Timer: después de 2 segundos aún no hay valores");

        // Avanzar 1 segundo más - ahora sí
        scheduler3.advanceTimeBy(1, TimeUnit.SECONDS);
        testTimer.assertValues(0L);
        testTimer.assertComplete();
        System.out.println("✓ Timer: después de 3 segundos: " + testTimer.values());

        // 4. Test con timeout
        System.out.println("\n--- Test con timeout ---");
        TestScheduler scheduler4 = new TestScheduler();
        TestObserver<String> testTimeout = new TestObserver<>();

        Observable.<String>create(emitter -> {
            // Simular operación lenta
            scheduler4.createWorker().schedule(() -> {
                emitter.onNext("Resultado");
                emitter.onComplete();
            }, 5, TimeUnit.SECONDS);
        })
        .timeout(3, TimeUnit.SECONDS, scheduler4)
        .subscribe(testTimeout);

        // Avanzar 3 segundos - debería timeout
        scheduler4.advanceTimeBy(3, TimeUnit.SECONDS);
        testTimeout.assertError(TimeoutException.class);
        System.out.println("✓ Timeout: error después de 3 segundos");

        // 5. Test con debounce
        System.out.println("\n--- Test con debounce ---");
        TestScheduler scheduler5 = new TestScheduler();
        TestObserver<String> testDebounce = new TestObserver<>();

        Observable.just("A", "B", "C", "D")
            .delay(100, TimeUnit.MILLISECONDS, scheduler5)
            .debounce(200, TimeUnit.MILLISECONDS, scheduler5)
            .subscribe(testDebounce);

        // Avanzar tiempo gradualmente
        scheduler5.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        testDebounce.assertValueCount(0);
        System.out.println("✓ Debounce: después de 100ms no hay valores");

        scheduler5.advanceTimeBy(200, TimeUnit.MILLISECONDS);
        testDebounce.assertValues("D"); // Solo el último valor
        testDebounce.assertComplete();
        System.out.println("✓ Debounce: después de 300ms total: " + testDebounce.values());

        // 6. Test con throttle
        System.out.println("\n--- Test con throttle ---");
        TestScheduler scheduler6 = new TestScheduler();
        TestObserver<String> testThrottle = new TestObserver<>();

        Observable.just("A", "B", "C", "D", "E")
            .delay(50, TimeUnit.MILLISECONDS, scheduler6)
            .throttleFirst(100, TimeUnit.MILLISECONDS, scheduler6)
            .subscribe(testThrottle);

        // Avanzar tiempo
        scheduler6.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        testThrottle.assertValues("A", "D"); // Primer valor de cada ventana
        testThrottle.assertComplete();
        System.out.println("✓ Throttle: " + testThrottle.values());

        // 7. Test con sample
        System.out.println("\n--- Test con sample ---");
        TestScheduler scheduler7 = new TestScheduler();
        TestObserver<String> testSample = new TestObserver<>();

        Observable.just("A", "B", "C", "D", "E")
            .delay(50, TimeUnit.MILLISECONDS, scheduler7)
            .sample(100, TimeUnit.MILLISECONDS, scheduler7)
            .subscribe(testSample);

        // Avanzar tiempo
        scheduler7.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        testSample.assertValues("C", "E"); // Último valor de cada ventana
        testSample.assertComplete();
        System.out.println("✓ Sample: " + testSample.values());

        // 8. Test con retry y delay
        System.out.println("\n--- Test con retry y delay ---");
        TestScheduler scheduler8 = new TestScheduler();
        TestObserver<String> testRetry = new TestObserver<>();

        Observable.<String>create(emitter -> {
            emitter.onError(new RuntimeException("Error simulado"));
        })
        .retryWhen(errors -> errors.delay(1, TimeUnit.SECONDS, scheduler8))
        .subscribe(testRetry);

        // Avanzar tiempo para permitir retry
        scheduler8.advanceTimeBy(2, TimeUnit.SECONDS);
        testRetry.assertError(RuntimeException.class);
        System.out.println("✓ Retry: error después de reintentos");

        // 9. Test con buffer temporal
        System.out.println("\n--- Test con buffer temporal ---");
        TestScheduler scheduler9 = new TestScheduler();
        TestObserver<java.util.List<String>> testBuffer = new TestObserver<>();

        Observable.just("A", "B", "C", "D", "E")
            .delay(50, TimeUnit.MILLISECONDS, scheduler9)
            .buffer(100, TimeUnit.MILLISECONDS, scheduler9)
            .subscribe(testBuffer);

        // Avanzar tiempo
        scheduler9.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        testBuffer.assertValueCount(3); // 3 buffers
        System.out.println("✓ Buffer temporal: " + testBuffer.values().size() + " buffers");

        // 10. Test con window temporal
        System.out.println("\n--- Test con window temporal ---");
        TestScheduler scheduler10 = new TestScheduler();
        TestObserver<Observable<String>> testWindow = new TestObserver<>();

        Observable.just("A", "B", "C", "D", "E")
            .delay(50, TimeUnit.MILLISECONDS, scheduler10)
            .window(100, TimeUnit.MILLISECONDS, scheduler10)
            .subscribe(testWindow);

        // Avanzar tiempo
        scheduler10.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        testWindow.assertValueCount(3); // 3 windows
        System.out.println("✓ Window temporal: " + testWindow.values().size() + " windows");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• TestScheduler: Control de tiempo virtual para testing");
        System.out.println("• advanceTimeBy(): Avanzar tiempo virtual");
        System.out.println("• advanceTimeTo(): Ir a un tiempo específico");
        System.out.println("• createWorker(): Crear worker para scheduling");
        System.out.println("• Útil para: delay, interval, timer, timeout, debounce, throttle");
    }
}
