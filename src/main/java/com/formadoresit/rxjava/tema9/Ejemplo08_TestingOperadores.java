package com.formadoresit.rxjava.tema9;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 9: Pruebas y Depuración
 * Ejemplo 08: Testing de Operadores
 * 
 * Técnicas para testing de operadores específicos de RxJava
 * incluyendo validación de comportamientos y edge cases
 */
public class Ejemplo08_TestingOperadores {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 08: Testing de Operadores ===\n");

        // 1. Test de operador map
        System.out.println("--- Test de operador map ---");
        TestObserver<String> testMap = new TestObserver<>();
        
        Observable.range(1, 5)
            .map(i -> "Número " + i)
            .subscribe(testMap);
        
        testMap.assertValues("Número 1", "Número 2", "Número 3", "Número 4", "Número 5");
        testMap.assertComplete();
        testMap.assertNoErrors();
        System.out.println("✓ Operador map validado");

        // 2. Test de operador filter
        System.out.println("\n--- Test de operador filter ---");
        TestObserver<Integer> testFilter = new TestObserver<>();
        
        Observable.range(1, 10)
            .filter(i -> i % 2 == 0)
            .subscribe(testFilter);
        
        testFilter.assertValues(2, 4, 6, 8, 10);
        testFilter.assertComplete();
        testFilter.assertNoErrors();
        System.out.println("✓ Operador filter validado");

        // 3. Test de operador take
        System.out.println("\n--- Test de operador take ---");
        TestObserver<Integer> testTake = new TestObserver<>();
        
        Observable.range(1, 10)
            .take(3)
            .subscribe(testTake);
        
        testTake.assertValues(1, 2, 3);
        testTake.assertComplete();
        testTake.assertNoErrors();
        System.out.println("✓ Operador take validado");

        // 4. Test de operador skip
        System.out.println("\n--- Test de operador skip ---");
        TestObserver<Integer> testSkip = new TestObserver<>();
        
        Observable.range(1, 10)
            .skip(3)
            .subscribe(testSkip);
        
        testSkip.assertValues(4, 5, 6, 7, 8, 9, 10);
        testSkip.assertComplete();
        testSkip.assertNoErrors();
        System.out.println("✓ Operador skip validado");

        // 5. Test de operador distinct
        System.out.println("\n--- Test de operador distinct ---");
        TestObserver<String> testDistinct = new TestObserver<>();
        
        Observable.just("A", "B", "A", "C", "B", "D")
            .distinct()
            .subscribe(testDistinct);
        
        testDistinct.assertValues("A", "B", "C", "D");
        testDistinct.assertComplete();
        testDistinct.assertNoErrors();
        System.out.println("✓ Operador distinct validado");

        // 6. Test de operador distinctUntilChanged
        System.out.println("\n--- Test de operador distinctUntilChanged ---");
        TestObserver<String> testDistinctUntilChanged = new TestObserver<>();
        
        Observable.just("A", "A", "B", "B", "C", "A", "A")
            .distinctUntilChanged()
            .subscribe(testDistinctUntilChanged);
        
        testDistinctUntilChanged.assertValues("A", "B", "C", "A");
        testDistinctUntilChanged.assertComplete();
        testDistinctUntilChanged.assertNoErrors();
        System.out.println("✓ Operador distinctUntilChanged validado");

        // 7. Test de operador takeWhile
        System.out.println("\n--- Test de operador takeWhile ---");
        TestObserver<Integer> testTakeWhile = new TestObserver<>();
        
        Observable.range(1, 10)
            .takeWhile(i -> i < 5)
            .subscribe(testTakeWhile);
        
        testTakeWhile.assertValues(1, 2, 3, 4);
        testTakeWhile.assertComplete();
        testTakeWhile.assertNoErrors();
        System.out.println("✓ Operador takeWhile validado");

        // 8. Test de operador skipWhile
        System.out.println("\n--- Test de operador skipWhile ---");
        TestObserver<Integer> testSkipWhile = new TestObserver<>();
        
        Observable.range(1, 10)
            .skipWhile(i -> i < 5)
            .subscribe(testSkipWhile);
        
        testSkipWhile.assertValues(5, 6, 7, 8, 9, 10);
        testSkipWhile.assertComplete();
        testSkipWhile.assertNoErrors();
        System.out.println("✓ Operador skipWhile validado");

        // 9. Test de operador takeUntil
        System.out.println("\n--- Test de operador takeUntil ---");
        TestObserver<Integer> testTakeUntil = new TestObserver<>();
        
        Observable.range(1, 10)
            .takeUntil(i -> i > 5)
            .subscribe(testTakeUntil);
        
        testTakeUntil.assertValues(1, 2, 3, 4, 5, 6);
        testTakeUntil.assertComplete();
        testTakeUntil.assertNoErrors();
        System.out.println("✓ Operador takeUntil validado");

        // 10. Test de operador skipUntil
        System.out.println("\n--- Test de operador skipUntil ---");
        TestObserver<Integer> testSkipUntil = new TestObserver<>();
        
        Observable.range(1, 10)
            .skipWhile(i -> i <= 3)
            .subscribe(testSkipUntil);
        
        testSkipUntil.assertValues(4, 5, 6, 7, 8, 9, 10);
        testSkipUntil.assertComplete();
        testSkipUntil.assertNoErrors();
        System.out.println("✓ Operador skipUntil validado");

        // 11. Test de operador buffer
        System.out.println("\n--- Test de operador buffer ---");
        TestObserver<java.util.List<Integer>> testBuffer = new TestObserver<>();
        
        Observable.range(1, 10)
            .buffer(3)
            .subscribe(testBuffer);
        
        testBuffer.assertValueCount(4); // 3 buffers de 3 + 1 buffer de 1
        testBuffer.assertComplete();
        testBuffer.assertNoErrors();
        System.out.println("✓ Operador buffer validado");

        // 12. Test de operador window
        System.out.println("\n--- Test de operador window ---");
        TestObserver<Observable<Integer>> testWindow = new TestObserver<>();
        
        Observable.range(1, 10)
            .window(3)
            .subscribe(testWindow);
        
        testWindow.assertValueCount(4); // 4 windows
        testWindow.assertComplete();
        testWindow.assertNoErrors();
        System.out.println("✓ Operador window validado");

        // 13. Test de operador groupBy
        System.out.println("\n--- Test de operador groupBy ---");
        TestObserver<String> testGroupBy = new TestObserver<>();
        
        Observable.range(1, 10)
            .groupBy(i -> i % 3)
            .flatMap(group -> group.map(i -> "Grupo " + group.getKey() + ": " + i))
            .subscribe(testGroupBy);
        
        testGroupBy.assertValueCount(10);
        testGroupBy.assertComplete();
        testGroupBy.assertNoErrors();
        System.out.println("✓ Operador groupBy validado");

        // 14. Test de operador scan
        System.out.println("\n--- Test de operador scan ---");
        TestObserver<Integer> testScan = new TestObserver<>();
        
        Observable.range(1, 5)
            .scan(0, (acc, x) -> acc + x)
            .subscribe(testScan);
        
        testScan.assertValues(0, 1, 3, 6, 10, 15);
        testScan.assertComplete();
        testScan.assertNoErrors();
        System.out.println("✓ Operador scan validado");

        // 15. Test de operador reduce
        System.out.println("\n--- Test de operador reduce ---");
        TestObserver<Integer> testReduce = new TestObserver<>();
        
        Observable.range(1, 5)
            .reduce(0, (acc, x) -> acc + x)
            .toObservable()
            .subscribe(testReduce);
        
        testReduce.assertValues(15);
        testReduce.assertComplete();
        testReduce.assertNoErrors();
        System.out.println("✓ Operador reduce validado");

        // 16. Test de operador collect
        System.out.println("\n--- Test de operador collect ---");
        TestObserver<Object> testCollect = new TestObserver<>();
        
        Observable.range(1, 5)
            .collect(java.util.ArrayList::new, (list, item) -> list.add(item))
            .toObservable()
            .subscribe(testCollect);
        
        testCollect.assertValueCount(1);
        testCollect.assertComplete();
        testCollect.assertNoErrors();
        System.out.println("✓ Operador collect validado");

        // 17. Test de operador toList
        System.out.println("\n--- Test de operador toList ---");
        TestObserver<java.util.List<Integer>> testToList = new TestObserver<>();
        
        Observable.range(1, 5)
            .toList()
            .toObservable()
            .subscribe(testToList);
        
        testToList.assertValueCount(1);
        testToList.assertComplete();
        testToList.assertNoErrors();
        System.out.println("✓ Operador toList validado");

        // 18. Test de operador toMap
        System.out.println("\n--- Test de operador toMap ---");
        TestObserver<java.util.Map<Integer, String>> testToMap = new TestObserver<>();
        
        Observable.range(1, 5)
            .toMap(i -> i, i -> "Valor " + i)
            .toObservable()
            .subscribe(testToMap);
        
        testToMap.assertValueCount(1);
        testToMap.assertComplete();
        testToMap.assertNoErrors();
        System.out.println("✓ Operador toMap validado");

        // 19. Test de operador toMultimap
        System.out.println("\n--- Test de operador toMultimap ---");
        TestObserver<java.util.Map<Integer, java.util.Collection<String>>> testToMultimap = new TestObserver<>();
        
        Observable.range(1, 5)
            .toMultimap(i -> i % 2, i -> "Valor " + i)
            .toObservable()
            .subscribe(testToMultimap);
        
        testToMultimap.assertValueCount(1);
        testToMultimap.assertComplete();
        testToMultimap.assertNoErrors();
        System.out.println("✓ Operador toMultimap validado");

        // 20. Test de operador toSortedList
        System.out.println("\n--- Test de operador toSortedList ---");
        TestObserver<java.util.List<Integer>> testToSortedList = new TestObserver<>();
        
        Observable.just(3, 1, 4, 1, 5)
            .toSortedList()
            .toObservable()
            .subscribe(testToSortedList);
        
        testToSortedList.assertValueCount(1);
        testToSortedList.assertComplete();
        testToSortedList.assertNoErrors();
        System.out.println("✓ Operador toSortedList validado");

        // 21. Test de operador delay
        System.out.println("\n--- Test de operador delay ---");
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> testDelay = new TestObserver<>();
        
        Observable.just("A", "B", "C")
            .delay(1, TimeUnit.SECONDS, scheduler)
            .subscribe(testDelay);
        
        // Inicialmente no hay valores
        testDelay.assertValueCount(0);
        
        // Avanzar tiempo
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testDelay.assertValues("A", "B", "C");
        testDelay.assertComplete();
        testDelay.assertNoErrors();
        System.out.println("✓ Operador delay validado");

        // 22. Test de operador debounce
        System.out.println("\n--- Test de operador debounce ---");
        TestScheduler schedulerDebounce = new TestScheduler();
        TestObserver<String> testDebounce = new TestObserver<>();
        
        Observable.just("A", "B", "C")
            .delay(100, TimeUnit.MILLISECONDS, schedulerDebounce)
            .debounce(200, TimeUnit.MILLISECONDS, schedulerDebounce)
            .subscribe(testDebounce);
        
        // Avanzar tiempo
        schedulerDebounce.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        testDebounce.assertValues("C"); // Solo el último valor
        testDebounce.assertComplete();
        testDebounce.assertNoErrors();
        System.out.println("✓ Operador debounce validado");

        // 23. Test de operador throttleFirst
        System.out.println("\n--- Test de operador throttleFirst ---");
        TestScheduler schedulerThrottle = new TestScheduler();
        TestObserver<String> testThrottle = new TestObserver<>();
        
        Observable.just("A", "B", "C", "D", "E")
            .delay(50, TimeUnit.MILLISECONDS, schedulerThrottle)
            .throttleFirst(100, TimeUnit.MILLISECONDS, schedulerThrottle)
            .subscribe(testThrottle);
        
        // Avanzar tiempo
        schedulerThrottle.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        testThrottle.assertValues("A", "D"); // Primer valor de cada ventana
        testThrottle.assertComplete();
        testThrottle.assertNoErrors();
        System.out.println("✓ Operador throttleFirst validado");

        // 24. Test de operador throttleLast
        System.out.println("\n--- Test de operador throttleLast ---");
        TestScheduler schedulerThrottleLast = new TestScheduler();
        TestObserver<String> testThrottleLast = new TestObserver<>();
        
        Observable.just("A", "B", "C", "D", "E")
            .delay(50, TimeUnit.MILLISECONDS, schedulerThrottleLast)
            .throttleLast(100, TimeUnit.MILLISECONDS, schedulerThrottleLast)
            .subscribe(testThrottleLast);
        
        // Avanzar tiempo
        schedulerThrottleLast.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        testThrottleLast.assertValues("C", "E"); // Último valor de cada ventana
        testThrottleLast.assertComplete();
        testThrottleLast.assertNoErrors();
        System.out.println("✓ Operador throttleLast validado");

        // 25. Test de operador sample
        System.out.println("\n--- Test de operador sample ---");
        TestScheduler schedulerSample = new TestScheduler();
        TestObserver<String> testSample = new TestObserver<>();
        
        Observable.just("A", "B", "C", "D", "E")
            .delay(50, TimeUnit.MILLISECONDS, schedulerSample)
            .sample(100, TimeUnit.MILLISECONDS, schedulerSample)
            .subscribe(testSample);
        
        // Avanzar tiempo
        schedulerSample.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        testSample.assertValues("C", "E"); // Último valor de cada ventana
        testSample.assertComplete();
        testSample.assertNoErrors();
        System.out.println("✓ Operador sample validado");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• map: Transformación de elementos");
        System.out.println("• filter: Filtrado de elementos");
        System.out.println("• take/skip: Limitación de elementos");
        System.out.println("• distinct: Eliminación de duplicados");
        System.out.println("• buffer/window: Agrupación de elementos");
        System.out.println("• groupBy: Agrupación por clave");
        System.out.println("• scan/reduce: Acumulación de elementos");
        System.out.println("• delay/debounce/throttle: Control temporal");
        System.out.println("• toList/toMap: Conversión a colecciones");
    }
}
