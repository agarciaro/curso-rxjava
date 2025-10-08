package com.formadoresit.rxjava.tema9;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 9: Pruebas y Depuración
 * Ejemplo 01: TestObserver
 * 
 * TestObserver permite hacer assertions sobre los elementos emitidos
 */
public class Ejemplo01_TestObserver {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 01: TestObserver ===\n");

        // 1. Test básico con TestObserver
        System.out.println("--- Test básico ---");
        TestObserver<Integer> testObserver = new TestObserver<>();
        
        Observable.just(1, 2, 3, 4, 5)
            .subscribe(testObserver);

        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(5);
        testObserver.assertValues(1, 2, 3, 4, 5);
        
        System.out.println("✓ Test pasado: 5 valores correctos");

        // 2. Test con transformación
        System.out.println("\n--- Test con map ---");
        TestObserver<Integer> testMap = Observable.just(1, 2, 3)
            .map(n -> n * 10)
            .test();  // Atajo para crear TestObserver y suscribirse

        testMap.assertValues(10, 20, 30);
        testMap.assertComplete();
        
        System.out.println("✓ Test pasado: map funciona correctamente");

        // 3. Test con filtrado
        System.out.println("\n--- Test con filter ---");
        TestObserver<Integer> testFilter = Observable.range(1, 10)
            .filter(n -> n % 2 == 0)
            .test();

        testFilter.assertValues(2, 4, 6, 8, 10);
        System.out.println("✓ Test pasado: filtro de pares");

        // 4. Test con error
        System.out.println("\n--- Test con error ---");
        TestObserver<Integer> testError = Observable.<Integer>create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onError(new RuntimeException("Error de prueba"));
        }).test();

        testError.assertNotComplete();
        testError.assertError(RuntimeException.class);
        testError.assertErrorMessage("Error de prueba");
        testError.assertValues(1, 2);
        
        System.out.println("✓ Test pasado: error manejado correctamente");

        // 5. Test con TestScheduler (operaciones temporales)
        System.out.println("\n--- Test con TestScheduler ---");
        TestScheduler scheduler = new TestScheduler();
        
        TestObserver<Long> testTemporal = Observable
            .interval(1, TimeUnit.SECONDS, scheduler)
            .take(3)
            .test();

        // Inicialmente no hay valores
        testTemporal.assertValueCount(0);
        
        // Avanzar tiempo virtual
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testTemporal.assertValues(0L);
        
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        testTemporal.assertValues(0L, 1L, 2L);
        testTemporal.assertComplete();
        
        System.out.println("✓ Test pasado: interval con tiempo virtual");

        // 6. Test con predicados
        System.out.println("\n--- Test con predicados ---");
        TestObserver<String> testPredicado = Observable.just("a", "bb", "ccc")
            .test();

        testPredicado.assertValueAt(0, "a");
        testPredicado.assertValueAt(1, value -> value.length() == 2);
        testPredicado.assertValueAt(2, value -> value.equals("ccc"));
        
        System.out.println("✓ Test pasado: predicados correctos");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• TestObserver: Facilita testing de Observables");
        System.out.println("• test(): Atajo para crear y suscribir TestObserver");
        System.out.println("• assertValues: Verifica valores emitidos");
        System.out.println("• assertComplete/assertError: Verifica terminación");
        System.out.println("• TestScheduler: Para probar operaciones temporales");
    }
}

