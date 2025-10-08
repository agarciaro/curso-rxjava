package com.formadoresit.rxjava.tema9;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 9: Pruebas y Depuración
 * Ejemplo 04: TestObserver Avanzado
 * 
 * Técnicas avanzadas de testing con TestObserver incluyendo
 * predicados personalizados, validaciones complejas y testing de errores
 */
public class Ejemplo04_TestObserverAvanzado {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 04: TestObserver Avanzado ===\n");

        // 1. Test con predicados personalizados
        System.out.println("--- Test con predicados personalizados ---");
        Observable<String> palabras = Observable.just("apple", "banana", "cherry", "date", "elderberry");
        
        TestObserver<String> testPredicados = palabras.test();
        
        // Verificar propiedades específicas
        testPredicados.assertValueAt(0, palabra -> palabra.startsWith("a"));
        testPredicados.assertValueAt(1, palabra -> palabra.length() == 6);
        testPredicados.assertValueAt(2, palabra -> palabra.contains("cherry"));
        testPredicados.assertValueAt(3, palabra -> palabra.equals("date"));
        testPredicados.assertValueAt(4, palabra -> palabra.length() > 5);
        
        System.out.println("✓ Predicados personalizados validados");

        // 2. Test con validaciones de orden
        System.out.println("\n--- Test con validaciones de orden ---");
        Observable<Integer> numeros = Observable.just(3, 1, 4, 1, 5, 9, 2, 6);
        
        TestObserver<Integer> testOrden = numeros.test();
        
        // Verificar que los valores están en el orden correcto
        testOrden.assertValues(3, 1, 4, 1, 5, 9, 2, 6);
        testOrden.assertValueSequence(java.util.Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6));
        
        System.out.println("✓ Orden de valores validado");

        // 3. Test con validaciones de terminación
        System.out.println("\n--- Test con validaciones de terminación ---");
        Observable<String> completado = Observable.just("A", "B", "C");
        Observable<String> conError = Observable.error(new RuntimeException("Error de prueba"));
        
        // Test de completado
        TestObserver<String> testCompletado = completado.test();
        testCompletado.assertComplete();
        testCompletado.assertNoErrors();
        testCompletado.assertTerminated();
        System.out.println("✓ Observable completado validado");
        
        // Test de error
        TestObserver<String> testError = conError.test();
        testError.assertNotComplete();
        testError.assertError(RuntimeException.class);
        testError.assertErrorMessage("Error de prueba");
        testError.assertTerminated();
        System.out.println("✓ Observable con error validado");

        // 4. Test con validaciones de subscripción
        System.out.println("\n--- Test con validaciones de subscripción ---");
        TestObserver<String> testSubscripcion = new TestObserver<>();
        
        Observable<String> observable = Observable.just("A", "B", "C")
            .doOnSubscribe(disposable -> System.out.println("Suscripción iniciada"));
        
        observable.subscribe(testSubscripcion);
        
        testSubscripcion.assertSubscribed();
        testSubscripcion.assertValueCount(3);
        testSubscripcion.assertComplete();
        System.out.println("✓ Subscripción validada");

        // 5. Test con validaciones de threading
        System.out.println("\n--- Test con validaciones de threading ---");
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> testThreading = new TestObserver<>();
        
        Observable.interval(1, TimeUnit.SECONDS, scheduler)
            .take(3)
            .subscribe(testThreading);
        
        // Verificar que inicialmente no hay valores
        testThreading.assertValueCount(0);
        
        // Avanzar tiempo
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        testThreading.assertValues(0L, 1L, 2L);
        testThreading.assertComplete();
        System.out.println("✓ Threading validado");

        // 6. Test con validaciones de timeout
        System.out.println("\n--- Test con validaciones de timeout ---");
        TestObserver<String> testTimeout = new TestObserver<>();
        
        Observable<String> lento = Observable.just("A", "B", "C")
            .delay(2, TimeUnit.SECONDS);
        
        lento.subscribe(testTimeout);
        
        // Verificar que inicialmente no hay valores
        testTimeout.assertValueCount(0);
        testTimeout.assertNotComplete();
        
        // Esperar un poco para que se complete
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testTimeout.assertValues("A", "B", "C");
        testTimeout.assertComplete();
        System.out.println("✓ Timeout validado");

        // 7. Test con validaciones de backpressure
        System.out.println("\n--- Test con validaciones de backpressure ---");
        TestObserver<Integer> testBackpressure = new TestObserver<>();
        
        Observable.range(1, 1000)
            .observeOn(io.reactivex.schedulers.Schedulers.computation())
            .subscribe(testBackpressure);
        
        // Esperar un poco para que se procese
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testBackpressure.assertValueCount(1000);
        testBackpressure.assertComplete();
        System.out.println("✓ Backpressure validado");

        // 8. Test con validaciones de operadores complejos
        System.out.println("\n--- Test con operadores complejos ---");
        Observable<String> complejo = Observable.just("A", "B", "C", "D", "E")
            .filter(s -> !s.equals("C"))
            .map(s -> s.toLowerCase())
            .distinct()
            .take(3)
            .doOnNext(s -> System.out.println("Procesando: " + s));
        
        TestObserver<String> testComplejo = complejo.test();
        
        testComplejo.assertValues("a", "b", "d");
        testComplejo.assertComplete();
        System.out.println("✓ Operadores complejos validados");

        // 9. Test con validaciones de error handling
        System.out.println("\n--- Test con error handling ---");
        Observable<String> conManejoError = Observable.just("A", "B", "C")
            .map(s -> {
                if ("B".equals(s)) {
                    throw new RuntimeException("Error en B");
                }
                return s;
            })
            .onErrorResumeNext(Observable.just("Recuperación"))
            .doOnError(error -> System.out.println("Error: " + error.getMessage()));
        
        TestObserver<String> testErrorHandling = conManejoError.test();
        
        testErrorHandling.assertValues("A", "Recuperación");
        testErrorHandling.assertComplete();
        System.out.println("✓ Error handling validado");

        // 10. Test con validaciones de métricas
        System.out.println("\n--- Test con métricas ---");
        long inicio = System.currentTimeMillis();
        final AtomicInteger contador = new AtomicInteger(0);
        
        Observable<Integer> conMetricas = Observable.range(1, 5)
            .doOnNext(i -> {
                int count = contador.incrementAndGet();
                System.out.println("Procesando " + i + " (total: " + count + ")");
            })
            .doOnComplete(() -> {
                long duracion = System.currentTimeMillis() - inicio;
                System.out.println("Completado en " + duracion + "ms");
            });
        
        TestObserver<Integer> testMetricas = conMetricas.test();
        
        testMetricas.assertValues(1, 2, 3, 4, 5);
        testMetricas.assertComplete();
        System.out.println("✓ Métricas validadas");

        // 11. Test con validaciones de combinación
        System.out.println("\n--- Test con combinación ---");
        Observable<String> fuente1 = Observable.just("A", "B", "C");
        Observable<String> fuente2 = Observable.just("1", "2", "3");
        
        Observable<String> combinado = Observable.zip(fuente1, fuente2, (a, b) -> a + b);
        
        TestObserver<String> testCombinado = combinado.test();
        
        testCombinado.assertValues("A1", "B2", "C3");
        testCombinado.assertComplete();
        System.out.println("✓ Combinación validada");

        // 12. Test con validaciones de reducción
        System.out.println("\n--- Test con reducción ---");
        Observable<Integer> numerosParaSuma = Observable.just(1, 2, 3, 4, 5);
        
        Observable<Integer> suma = numerosParaSuma
            .reduce(0, (acc, x) -> acc + x)
            .toObservable();
        
        TestObserver<Integer> testSuma = suma.test();
        
        testSuma.assertValues(15);
        testSuma.assertComplete();
        System.out.println("✓ Reducción validada");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• assertValueAt(): Verificar valor en posición específica");
        System.out.println("• assertValues(): Verificar secuencia de valores");
        System.out.println("• assertValueSequence(): Verificar secuencia exacta");
        System.out.println("• assertComplete(): Verificar completado");
        System.out.println("• assertError(): Verificar error");
        System.out.println("• assertTerminated(): Verificar terminación");
        System.out.println("• assertSubscribed(): Verificar suscripción");
        System.out.println("• Predicados personalizados para validaciones complejas");
    }
}
