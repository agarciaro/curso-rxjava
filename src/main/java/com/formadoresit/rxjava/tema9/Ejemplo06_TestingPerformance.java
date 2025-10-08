package com.formadoresit.rxjava.tema9;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TEMA 9: Pruebas y Depuración
 * Ejemplo 06: Testing de Performance
 * 
 * Técnicas para medir y validar el rendimiento de operaciones RxJava
 * incluyendo métricas de tiempo, throughput y uso de recursos
 */
public class Ejemplo06_TestingPerformance {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 06: Testing de Performance ===\n");

        // 1. Test de throughput básico
        System.out.println("--- Test de throughput básico ---");
        long inicio = System.currentTimeMillis();
        AtomicLong contador = new AtomicLong(0);
        
        TestObserver<Integer> testThroughput = new TestObserver<>();
        
        Observable.range(1, 10000)
            .doOnNext(i -> contador.incrementAndGet())
            .subscribe(testThroughput);
        
        long fin = System.currentTimeMillis();
        long duracion = fin - inicio;
        long throughput = contador.get() * 1000 / duracion; // elementos por segundo
        
        testThroughput.assertValueCount(10000);
        testThroughput.assertComplete();
        System.out.println("✓ Throughput: " + throughput + " elementos/segundo");
        System.out.println("✓ Duración: " + duracion + "ms");

        // 2. Test de performance con operadores
        System.out.println("\n--- Test de performance con operadores ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<String> testOperadores = new TestObserver<>();
        
        Observable.range(1, 1000)
            .map(i -> "Item " + i)
            .filter(s -> s.contains("5"))
            .map(s -> s.toUpperCase())
            .subscribe(testOperadores);
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testOperadores.assertValueCount(271); // Números que contienen 5
        testOperadores.assertComplete();
        System.out.println("✓ Operadores completados en: " + duracion + "ms");

        // 3. Test de performance con threading
        System.out.println("\n--- Test de performance con threading ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<Integer> testThreading = new TestObserver<>();
        
        Observable.range(1, 1000)
            .observeOn(Schedulers.computation())
            .map(i -> i * 2)
            .observeOn(Schedulers.io())
            .map(i -> i + 1)
            .subscribe(testThreading);
        
        // Esperar a que se complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testThreading.assertValueCount(1000);
        testThreading.assertComplete();
        System.out.println("✓ Threading completado en: " + duracion + "ms");

        // 4. Test de performance con backpressure
        System.out.println("\n--- Test de performance con backpressure ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<Integer> testBackpressure = new TestObserver<>();
        
        Observable.range(1, 10000)
            .observeOn(Schedulers.computation(), false, 100) // buffer de 100
            .subscribe(testBackpressure);
        
        // Esperar a que se complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testBackpressure.assertValueCount(10000);
        testBackpressure.assertComplete();
        System.out.println("✓ Backpressure completado en: " + duracion + "ms");

        // 5. Test de performance con cache
        System.out.println("\n--- Test de performance con cache ---");
        Observable<String> cacheable = Observable.just("Datos costosos")
            .doOnNext(datos -> {
                try {
                    Thread.sleep(100); // Simular operación costosa
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            })
            .cache();
        
        // Primera llamada (costosa)
        inicio = System.currentTimeMillis();
        TestObserver<String> testCache1 = new TestObserver<>();
        cacheable.subscribe(testCache1);
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        fin = System.currentTimeMillis();
        long primeraLlamada = fin - inicio;
        
        // Segunda llamada (desde cache)
        inicio = System.currentTimeMillis();
        TestObserver<String> testCache2 = new TestObserver<>();
        cacheable.subscribe(testCache2);
        
        fin = System.currentTimeMillis();
        long segundaLlamada = fin - inicio;
        
        testCache1.assertValues("Datos costosos");
        testCache2.assertValues("Datos costosos");
        System.out.println("✓ Primera llamada: " + primeraLlamada + "ms");
        System.out.println("✓ Segunda llamada (cache): " + segundaLlamada + "ms");
        System.out.println("✓ Mejora de performance: " + (primeraLlamada / Math.max(segundaLlamada, 1)) + "x");

        // 6. Test de performance con operadores de tiempo
        System.out.println("\n--- Test de performance con operadores de tiempo ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<Integer> testTiempo = new TestObserver<>();
        
        Observable.range(1, 100)
            .delay(10, TimeUnit.MILLISECONDS)
            .subscribe(testTiempo);
        
        // Esperar a que se complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testTiempo.assertValueCount(100);
        testTiempo.assertComplete();
        System.out.println("✓ Operadores de tiempo completados en: " + duracion + "ms");

        // 7. Test de performance con operadores de combinación
        System.out.println("\n--- Test de performance con combinación ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<String> testCombinacion = new TestObserver<>();
        
        Observable<String> fuente1 = Observable.range(1, 1000).map(i -> "A" + i);
        Observable<String> fuente2 = Observable.range(1, 1000).map(i -> "B" + i);
        
        Observable.zip(fuente1, fuente2, (a, b) -> a + b)
            .subscribe(testCombinacion);
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testCombinacion.assertValueCount(1000);
        testCombinacion.assertComplete();
        System.out.println("✓ Combinación completada en: " + duracion + "ms");

        // 8. Test de performance con operadores de reducción
        System.out.println("\n--- Test de performance con reducción ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<Integer> testReduccion = new TestObserver<>();
        
        Observable.range(1, 10000)
            .reduce(0, (acc, x) -> acc + x)
            .toObservable()
            .subscribe(testReduccion);
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testReduccion.assertValues(50005000); // Suma de 1 a 10000
        testReduccion.assertComplete();
        System.out.println("✓ Reducción completada en: " + duracion + "ms");

        // 9. Test de performance con operadores de agrupación
        System.out.println("\n--- Test de performance con agrupación ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<String> testAgrupacion = new TestObserver<>();
        
        Observable.range(1, 1000)
            .groupBy(i -> i % 10)
            .flatMap(group -> group.map(i -> "Grupo " + group.getKey() + ": " + i))
            .subscribe(testAgrupacion);
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testAgrupacion.assertValueCount(1000);
        testAgrupacion.assertComplete();
        System.out.println("✓ Agrupación completada en: " + duracion + "ms");

        // 10. Test de performance con operadores de buffer
        System.out.println("\n--- Test de performance con buffer ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<java.util.List<Integer>> testBuffer = new TestObserver<>();
        
        Observable.range(1, 1000)
            .buffer(100)
            .subscribe(testBuffer);
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testBuffer.assertValueCount(10); // 10 buffers de 100
        testBuffer.assertComplete();
        System.out.println("✓ Buffer completado en: " + duracion + "ms");

        // 11. Test de performance con operadores de window
        System.out.println("\n--- Test de performance con window ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<Observable<Integer>> testWindow = new TestObserver<>();
        
        Observable.range(1, 1000)
            .window(100)
            .subscribe(testWindow);
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testWindow.assertValueCount(10); // 10 windows de 100
        testWindow.assertComplete();
        System.out.println("✓ Window completado en: " + duracion + "ms");

        // 12. Test de performance con operadores de flatMap
        System.out.println("\n--- Test de performance con flatMap ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<String> testFlatMap = new TestObserver<>();
        
        Observable.range(1, 100)
            .flatMap(i -> Observable.just("Item " + i).delay(1, TimeUnit.MILLISECONDS))
            .subscribe(testFlatMap);
        
        // Esperar a que se complete
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testFlatMap.assertValueCount(100);
        testFlatMap.assertComplete();
        System.out.println("✓ FlatMap completado en: " + duracion + "ms");

        // 13. Test de performance con operadores de merge
        System.out.println("\n--- Test de performance con merge ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<Integer> testMerge = new TestObserver<>();
        
        Observable<Integer> fuenteMerge1 = Observable.range(1, 500);
        Observable<Integer> fuenteMerge2 = Observable.range(501, 500);
        
        Observable.merge(fuenteMerge1, fuenteMerge2)
            .subscribe(testMerge);
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testMerge.assertValueCount(1000);
        testMerge.assertComplete();
        System.out.println("✓ Merge completado en: " + duracion + "ms");

        // 14. Test de performance con operadores de concat
        System.out.println("\n--- Test de performance con concat ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<Integer> testConcat = new TestObserver<>();
        
        Observable<Integer> fuente3 = Observable.range(1, 500);
        Observable<Integer> fuente4 = Observable.range(501, 500);
        
        Observable.concat(fuente3, fuente4)
            .subscribe(testConcat);
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testConcat.assertValueCount(1000);
        testConcat.assertComplete();
        System.out.println("✓ Concat completado en: " + duracion + "ms");

        // 15. Test de performance con operadores de switchMap
        System.out.println("\n--- Test de performance con switchMap ---");
        inicio = System.currentTimeMillis();
        
        TestObserver<String> testSwitchMap = new TestObserver<>();
        
        Observable.range(1, 100)
            .switchMap(i -> Observable.just("Switch " + i).delay(1, TimeUnit.MILLISECONDS))
            .subscribe(testSwitchMap);
        
        // Esperar a que se complete
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        fin = System.currentTimeMillis();
        duracion = fin - inicio;
        
        testSwitchMap.assertValueCount(100);
        testSwitchMap.assertComplete();
        System.out.println("✓ SwitchMap completado en: " + duracion + "ms");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Throughput: Elementos procesados por segundo");
        System.out.println("• Latencia: Tiempo de respuesta");
        System.out.println("• Backpressure: Control de flujo");
        System.out.println("• Cache: Mejora de performance");
        System.out.println("• Threading: Paralelización");
        System.out.println("• Métricas: Medición de rendimiento");
    }
}
