package com.formadoresit.rxjava.tema9;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 9: Pruebas y Depuración
 * Ejemplo 09: Testing de Operadores de Combinación
 * 
 * Técnicas para testing de operadores que combinan múltiples Observables
 * incluyendo merge, zip, combineLatest, switch, concat, etc.
 */
public class Ejemplo09_TestingCombinacion {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 09: Testing de Operadores de Combinación ===\n");

        // 1. Test de operador merge
        System.out.println("--- Test de operador merge ---");
        TestObserver<String> testMerge = new TestObserver<>();
        
        Observable<String> fuente1 = Observable.just("A", "B", "C");
        Observable<String> fuente2 = Observable.just("1", "2", "3");
        
        Observable.merge(fuente1, fuente2)
            .subscribe(testMerge);
        
        testMerge.assertValueCount(6);
        testMerge.assertComplete();
        testMerge.assertNoErrors();
        System.out.println("✓ Operador merge validado");

        // 2. Test de operador mergeWith
        System.out.println("\n--- Test de operador mergeWith ---");
        TestObserver<String> testMergeWith = new TestObserver<>();
        
        Observable.just("A", "B", "C")
            .mergeWith(Observable.just("1", "2", "3"))
            .subscribe(testMergeWith);
        
        testMergeWith.assertValueCount(6);
        testMergeWith.assertComplete();
        testMergeWith.assertNoErrors();
        System.out.println("✓ Operador mergeWith validado");

        // 3. Test de operador zip
        System.out.println("\n--- Test de operador zip ---");
        TestObserver<String> testZip = new TestObserver<>();
        
        Observable<String> fuente3 = Observable.just("A", "B", "C");
        Observable<String> fuente4 = Observable.just("1", "2", "3");
        
        Observable.zip(fuente3, fuente4, (a, b) -> a + b)
            .subscribe(testZip);
        
        testZip.assertValues("A1", "B2", "C3");
        testZip.assertComplete();
        testZip.assertNoErrors();
        System.out.println("✓ Operador zip validado");

        // 4. Test de operador zipWith
        System.out.println("\n--- Test de operador zipWith ---");
        TestObserver<String> testZipWith = new TestObserver<>();
        
        Observable.just("A", "B", "C")
            .zipWith(Observable.just("1", "2", "3"), (a, b) -> a + b)
            .subscribe(testZipWith);
        
        testZipWith.assertValues("A1", "B2", "C3");
        testZipWith.assertComplete();
        testZipWith.assertNoErrors();
        System.out.println("✓ Operador zipWith validado");

        // 5. Test de operador combineLatest
        System.out.println("\n--- Test de operador combineLatest ---");
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> testCombineLatest = new TestObserver<>();
        
        Observable<String> fuente5 = Observable.just("A", "B", "C")
            .delay(100, TimeUnit.MILLISECONDS, scheduler);
        Observable<String> fuente6 = Observable.just("1", "2", "3")
            .delay(200, TimeUnit.MILLISECONDS, scheduler);
        
        Observable.combineLatest(fuente5, fuente6, (a, b) -> a + b)
            .subscribe(testCombineLatest);
        
        // Avanzar tiempo
        scheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        
        testCombineLatest.assertValueCount(5); // A1, B1, B2, C2, C3
        testCombineLatest.assertComplete();
        testCombineLatest.assertNoErrors();
        System.out.println("✓ Operador combineLatest validado");

        // 6. Test de operador concat
        System.out.println("\n--- Test de operador concat ---");
        TestObserver<String> testConcat = new TestObserver<>();
        
        Observable<String> fuente7 = Observable.just("A", "B", "C");
        Observable<String> fuente8 = Observable.just("1", "2", "3");
        
        Observable.concat(fuente7, fuente8)
            .subscribe(testConcat);
        
        testConcat.assertValues("A", "B", "C", "1", "2", "3");
        testConcat.assertComplete();
        testConcat.assertNoErrors();
        System.out.println("✓ Operador concat validado");

        // 7. Test de operador concatWith
        System.out.println("\n--- Test de operador concatWith ---");
        TestObserver<String> testConcatWith = new TestObserver<>();
        
        Observable.just("A", "B", "C")
            .concatWith(Observable.just("1", "2", "3"))
            .subscribe(testConcatWith);
        
        testConcatWith.assertValues("A", "B", "C", "1", "2", "3");
        testConcatWith.assertComplete();
        testConcatWith.assertNoErrors();
        System.out.println("✓ Operador concatWith validado");

        // 8. Test de operador startWith
        System.out.println("\n--- Test de operador startWith ---");
        TestObserver<String> testStartWith = new TestObserver<>();
        
        Observable.just("B", "C")
            .startWith("A")
            .subscribe(testStartWith);
        
        testStartWith.assertValues("A", "B", "C");
        testStartWith.assertComplete();
        testStartWith.assertNoErrors();
        System.out.println("✓ Operador startWith validado");

        // 9. Test de operador switchMap
        System.out.println("\n--- Test de operador switchMap ---");
        TestObserver<String> testSwitchMap = new TestObserver<>();
        
        Observable.range(1, 3)
            .switchMap(i -> Observable.just("Switch " + i).delay(100, TimeUnit.MILLISECONDS))
            .subscribe(testSwitchMap);
        
        // Esperar a que se complete
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testSwitchMap.assertValues("Switch 3"); // Solo el último
        testSwitchMap.assertComplete();
        testSwitchMap.assertNoErrors();
        System.out.println("✓ Operador switchMap validado");

        // 10. Test de operador flatMap
        System.out.println("\n--- Test de operador flatMap ---");
        TestObserver<String> testFlatMap = new TestObserver<>();
        
        Observable.range(1, 3)
            .flatMap(i -> Observable.just("Flat " + i))
            .subscribe(testFlatMap);
        
        testFlatMap.assertValues("Flat 1", "Flat 2", "Flat 3");
        testFlatMap.assertComplete();
        testFlatMap.assertNoErrors();
        System.out.println("✓ Operador flatMap validado");

        // 11. Test de operador concatMap
        System.out.println("\n--- Test de operador concatMap ---");
        TestObserver<String> testConcatMap = new TestObserver<>();
        
        Observable.range(1, 3)
            .concatMap(i -> Observable.just("Concat " + i))
            .subscribe(testConcatMap);
        
        testConcatMap.assertValues("Concat 1", "Concat 2", "Concat 3");
        testConcatMap.assertComplete();
        testConcatMap.assertNoErrors();
        System.out.println("✓ Operador concatMap validado");

        // 12. Test de operador amb
        System.out.println("\n--- Test de operador amb ---");
        TestScheduler schedulerAmb = new TestScheduler();
        TestObserver<String> testAmb = new TestObserver<>();
        
        Observable<String> fuente9 = Observable.just("A", "B", "C")
            .delay(200, TimeUnit.MILLISECONDS, schedulerAmb);
        Observable<String> fuente10 = Observable.just("1", "2", "3")
            .delay(100, TimeUnit.MILLISECONDS, schedulerAmb);
        
        Observable.ambArray(fuente9, fuente10)
            .subscribe(testAmb);
        
        // Avanzar tiempo
        schedulerAmb.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        
        testAmb.assertValues("1", "2", "3"); // Solo la fuente más rápida
        testAmb.assertComplete();
        testAmb.assertNoErrors();
        System.out.println("✓ Operador amb validado");

        // 13. Test de operador ambWith
        System.out.println("\n--- Test de operador ambWith ---");
        TestScheduler schedulerAmbWith = new TestScheduler();
        TestObserver<String> testAmbWith = new TestObserver<>();
        
        Observable.just("A", "B", "C")
            .delay(200, TimeUnit.MILLISECONDS, schedulerAmbWith)
            .ambWith(Observable.just("1", "2", "3")
                .delay(100, TimeUnit.MILLISECONDS, schedulerAmbWith))
            .subscribe(testAmbWith);
        
        // Avanzar tiempo
        schedulerAmbWith.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        
        testAmbWith.assertValues("1", "2", "3"); // Solo la fuente más rápida
        testAmbWith.assertComplete();
        testAmbWith.assertNoErrors();
        System.out.println("✓ Operador ambWith validado");

        // 14. Test de operador withLatestFrom
        System.out.println("\n--- Test de operador withLatestFrom ---");
        TestScheduler schedulerWithLatest = new TestScheduler();
        TestObserver<String> testWithLatest = new TestObserver<>();
        
        Observable<String> fuente11 = Observable.just("A", "B", "C")
            .delay(100, TimeUnit.MILLISECONDS, schedulerWithLatest);
        Observable<String> fuente12 = Observable.just("1", "2", "3")
            .delay(200, TimeUnit.MILLISECONDS, schedulerWithLatest);
        
        fuente11.withLatestFrom(fuente12, (a, b) -> a + b)
            .subscribe(testWithLatest);
        
        // Avanzar tiempo
        schedulerWithLatest.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        
        testWithLatest.assertValues("B1", "C2"); // Solo cuando ambas tienen valores
        testWithLatest.assertComplete();
        testWithLatest.assertNoErrors();
        System.out.println("✓ Operador withLatestFrom validado");

        // 15. Test de operador join
        System.out.println("\n--- Test de operador join ---");
        TestScheduler schedulerJoin = new TestScheduler();
        TestObserver<String> testJoin = new TestObserver<>();
        
        Observable<String> fuente13 = Observable.just("A", "B", "C")
            .delay(100, TimeUnit.MILLISECONDS, schedulerJoin);
        Observable<String> fuente14 = Observable.just("1", "2", "3")
            .delay(200, TimeUnit.MILLISECONDS, schedulerJoin);
        
        fuente13.join(fuente14,
            a -> Observable.timer(100, TimeUnit.MILLISECONDS, schedulerJoin),
            b -> Observable.timer(100, TimeUnit.MILLISECONDS, schedulerJoin),
            (a, b) -> a + b)
            .subscribe(testJoin);
        
        // Avanzar tiempo
        schedulerJoin.advanceTimeBy(400, TimeUnit.MILLISECONDS);
        
        testJoin.assertValueCount(3); // A1, B2, C3
        testJoin.assertComplete();
        testJoin.assertNoErrors();
        System.out.println("✓ Operador join validado");

        // 16. Test de operador groupJoin
        System.out.println("\n--- Test de operador groupJoin ---");
        TestScheduler schedulerGroupJoin = new TestScheduler();
        TestObserver<Object> testGroupJoin = new TestObserver<>();
        
        Observable<String> fuente15 = Observable.just("A", "B", "C")
            .delay(100, TimeUnit.MILLISECONDS, schedulerGroupJoin);
        Observable<String> fuente16 = Observable.just("1", "2", "3")
            .delay(200, TimeUnit.MILLISECONDS, schedulerGroupJoin);
        
        fuente15.groupJoin(fuente16,
            a -> Observable.timer(100, TimeUnit.MILLISECONDS, schedulerGroupJoin),
            b -> Observable.timer(100, TimeUnit.MILLISECONDS, schedulerGroupJoin),
            (a, b) -> b.map(item -> a + ":" + item))
            .flatMap(group -> group)
            .subscribe(testGroupJoin);
        
        // Avanzar tiempo
        schedulerGroupJoin.advanceTimeBy(400, TimeUnit.MILLISECONDS);
        
        testGroupJoin.assertValueCount(3); // A:1, B:2, C:3
        testGroupJoin.assertComplete();
        testGroupJoin.assertNoErrors();
        System.out.println("✓ Operador groupJoin validado");

        // 17. Test de operador mergeDelayError
        System.out.println("\n--- Test de operador mergeDelayError ---");
        TestObserver<String> testMergeDelayError = new TestObserver<>();
        
        Observable<String> fuente17 = Observable.just("A", "B", "C");
        Observable<String> fuente18 = Observable.just("1", "2", "3");
        Observable<String> fuente19 = Observable.error(new RuntimeException("Error"));
        
        Observable.mergeDelayError(fuente17, fuente18, fuente19)
            .subscribe(testMergeDelayError);
        
        testMergeDelayError.assertValues("A", "B", "C", "1", "2", "3");
        testMergeDelayError.assertError(RuntimeException.class);
        System.out.println("✓ Operador mergeDelayError validado");

        // 18. Test de operador concatDelayError
        System.out.println("\n--- Test de operador concatDelayError ---");
        TestObserver<String> testConcatDelayError = new TestObserver<>();
        
        Observable<String> fuente20 = Observable.just("A", "B", "C");
        Observable<String> fuente21 = Observable.just("1", "2", "3");
        Observable<String> fuente22 = Observable.error(new RuntimeException("Error"));
        
        Observable.concatArrayDelayError(fuente20, fuente21, fuente22)
            .subscribe(testConcatDelayError);
        
        testConcatDelayError.assertValues("A", "B", "C", "1", "2", "3");
        testConcatDelayError.assertError(RuntimeException.class);
        System.out.println("✓ Operador concatDelayError validado");

        // 19. Test de operador switchOnNext
        System.out.println("\n--- Test de operador switchOnNext ---");
        TestScheduler schedulerSwitchOnNext = new TestScheduler();
        TestObserver<String> testSwitchOnNext = new TestObserver<>();
        
        Observable<Observable<String>> fuentes = Observable.just(
            Observable.just("A", "B", "C").delay(100, TimeUnit.MILLISECONDS, schedulerSwitchOnNext),
            Observable.just("1", "2", "3").delay(200, TimeUnit.MILLISECONDS, schedulerSwitchOnNext)
        );
        
        Observable.switchOnNext(fuentes)
            .subscribe(testSwitchOnNext);
        
        // Avanzar tiempo
        schedulerSwitchOnNext.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        
        testSwitchOnNext.assertValues("1", "2", "3"); // Solo la última fuente
        testSwitchOnNext.assertComplete();
        testSwitchOnNext.assertNoErrors();
        System.out.println("✓ Operador switchOnNext validado");

        // 20. Test de operador combineLatest con múltiples fuentes
        System.out.println("\n--- Test de combineLatest con múltiples fuentes ---");
        TestScheduler schedulerMulti = new TestScheduler();
        TestObserver<String> testMulti = new TestObserver<>();
        
        Observable<String> fuente23 = Observable.just("A", "B", "C")
            .delay(100, TimeUnit.MILLISECONDS, schedulerMulti);
        Observable<String> fuente24 = Observable.just("1", "2", "3")
            .delay(200, TimeUnit.MILLISECONDS, schedulerMulti);
        Observable<String> fuente25 = Observable.just("X", "Y", "Z")
            .delay(300, TimeUnit.MILLISECONDS, schedulerMulti);
        
        Observable.combineLatest(fuente23, fuente24, fuente25, (a, b, c) -> a + b + c)
            .subscribe(testMulti);
        
        // Avanzar tiempo
        schedulerMulti.advanceTimeBy(400, TimeUnit.MILLISECONDS);
        
        testMulti.assertValueCount(7); // Combinaciones válidas
        testMulti.assertComplete();
        testMulti.assertNoErrors();
        System.out.println("✓ combineLatest con múltiples fuentes validado");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• merge: Combinar múltiples fuentes");
        System.out.println("• zip: Combinar elementos por posición");
        System.out.println("• combineLatest: Combinar últimos elementos");
        System.out.println("• concat: Concatenar secuencialmente");
        System.out.println("• switchMap: Cambiar a nueva fuente");
        System.out.println("• flatMap: Aplanar múltiples fuentes");
        System.out.println("• amb: Primera fuente en emitir");
        System.out.println("• withLatestFrom: Combinar con última emisión");
        System.out.println("• join: Combinar con ventana temporal");
        System.out.println("• mergeDelayError: Merge con error diferido");
    }
}
