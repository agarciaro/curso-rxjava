package com.formadoresit.rxjava.tema9;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 9: Pruebas y Depuración
 * Ejemplo 10: Testing Completo
 * 
 * Ejemplo completo que integra todas las técnicas de testing
 * para un sistema real de procesamiento de datos
 */
public class Ejemplo10_TestingCompleto {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 10: Testing Completo ===\n");

        // 1. Test de sistema de procesamiento de datos
        System.out.println("--- Test de sistema de procesamiento de datos ---");
        TestObserver<String> testSistema = new TestObserver<>();
        
        Observable<String> sistema = Observable.just("Dato1", "Dato2", "Dato3", "Dato4", "Dato5")
            .doOnNext(dato -> System.out.println("📥 Recibido: " + dato))
            .filter(dato -> !dato.contains("3")) // Filtrar datos problemáticos
            .map(dato -> "Procesado: " + dato)
            .doOnNext(dato -> System.out.println("🔄 " + dato))
            .delay(100, TimeUnit.MILLISECONDS)
            .doOnNext(dato -> System.out.println("⏰ " + dato))
            .doOnComplete(() -> System.out.println("✅ Sistema completado"));
        
        sistema.subscribe(testSistema);
        
        // Esperar a que se complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testSistema.assertValueCount(4); // 5 - 1 filtrado
        testSistema.assertComplete();
        testSistema.assertNoErrors();
        System.out.println("✓ Sistema de procesamiento validado");

        // 2. Test de sistema con manejo de errores
        System.out.println("\n--- Test de sistema con manejo de errores ---");
        TestObserver<String> testSistemaError = new TestObserver<>();
        
        Observable<String> sistemaConError = Observable.just("Dato1", "Dato2", "Dato3", "Dato4")
            .doOnNext(dato -> System.out.println("📥 Recibido: " + dato))
            .map(dato -> {
                if ("Dato3".equals(dato)) {
                    throw new RuntimeException("Error en procesamiento");
                }
                return "Procesado: " + dato;
            })
            .onErrorResumeNext(Observable.just("Recuperación exitosa"))
            .doOnError(error -> System.out.println("❌ Error: " + error.getMessage()))
            .doOnComplete(() -> System.out.println("✅ Sistema con error completado"));
        
        sistemaConError.subscribe(testSistemaError);
        
        testSistemaError.assertValues("Procesado: Dato1", "Procesado: Dato2", "Recuperación exitosa");
        testSistemaError.assertComplete();
        testSistemaError.assertNoErrors();
        System.out.println("✓ Sistema con manejo de errores validado");

        // 3. Test de sistema con retry
        System.out.println("\n--- Test de sistema con retry ---");
        AtomicInteger intentos = new AtomicInteger(0);
        TestObserver<String> testSistemaRetry = new TestObserver<>();
        
        Observable<String> sistemaConRetry = Observable.<String>create(emitter -> {
            int intento = intentos.incrementAndGet();
            System.out.println("🔄 Intento " + intento);
            if (intento < 3) {
                emitter.onError(new RuntimeException("Error temporal " + intento));
            } else {
                emitter.onNext("Éxito después de " + intento + " intentos");
                emitter.onComplete();
            }
        })
        .retry(3)
        .doOnError(error -> System.out.println("❌ Error final: " + error.getMessage()))
        .doOnComplete(() -> System.out.println("✅ Sistema con retry completado"));
        
        sistemaConRetry.subscribe(testSistemaRetry);
        
        testSistemaRetry.assertValues("Éxito después de 3 intentos");
        testSistemaRetry.assertComplete();
        testSistemaRetry.assertNoErrors();
        System.out.println("✓ Sistema con retry validado");

        // 4. Test de sistema con timeout
        System.out.println("\n--- Test de sistema con timeout ---");
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> testSistemaTimeout = new TestObserver<>();
        
        Observable<String> sistemaConTimeout = Observable.<String>create(emitter -> {
            // Simular operación lenta
            scheduler.createWorker().schedule(() -> {
                emitter.onNext("Resultado tardío");
                emitter.onComplete();
            }, 5, TimeUnit.SECONDS);
        })
        .timeout(3, TimeUnit.SECONDS, scheduler)
        .doOnError(error -> System.out.println("⏰ Timeout: " + error.getMessage()))
        .doOnComplete(() -> System.out.println("✅ Sistema con timeout completado"));
        
        sistemaConTimeout.subscribe(testSistemaTimeout);
        
        // Avanzar tiempo para timeout
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        
        testSistemaTimeout.assertError(java.util.concurrent.TimeoutException.class);
        System.out.println("✓ Sistema con timeout validado");

        // 5. Test de sistema con cache
        System.out.println("\n--- Test de sistema con cache ---");
        Observable<String> sistemaConCache = Observable.just("Datos costosos")
            .doOnNext(datos -> {
                try {
                    Thread.sleep(100); // Simular operación costosa
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            })
            .cache();
        
        // Primera llamada (costosa)
        final long inicio1 = System.currentTimeMillis();
        TestObserver<String> testCache1 = new TestObserver<>();
        sistemaConCache.subscribe(testCache1);
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long primeraLlamada = System.currentTimeMillis() - inicio1;
        
        // Segunda llamada (desde cache)
        final long inicio2 = System.currentTimeMillis();
        TestObserver<String> testCache2 = new TestObserver<>();
        sistemaConCache.subscribe(testCache2);
        
        long segundaLlamada = System.currentTimeMillis() - inicio2;
        
        testCache1.assertValues("Datos costosos");
        testCache2.assertValues("Datos costosos");
        System.out.println("✓ Primera llamada: " + primeraLlamada + "ms");
        System.out.println("✓ Segunda llamada (cache): " + segundaLlamada + "ms");
        System.out.println("✓ Mejora de performance: " + (primeraLlamada / Math.max(segundaLlamada, 1)) + "x");

        // 6. Test de sistema con métricas
        System.out.println("\n--- Test de sistema con métricas ---");
        final long inicio3 = System.currentTimeMillis();
        AtomicInteger contador = new AtomicInteger(0);
        
        TestObserver<String> testSistemaMetricas = new TestObserver<>();
        
        Observable<String> sistemaConMetricas = Observable.just("Dato1", "Dato2", "Dato3", "Dato4", "Dato5")
            .doOnSubscribe(disposable -> {
                System.out.println("📊 Iniciando procesamiento - Tiempo: " + (System.currentTimeMillis() - inicio3) + "ms");
            })
            .doOnNext(dato -> {
                contador.incrementAndGet();
                long tiempoActual = System.currentTimeMillis() - inicio3;
                System.out.println("📈 Procesado " + contador.get() + " en " + tiempoActual + "ms - " + dato);
            })
            .doOnComplete(() -> {
                long duracionTotal = System.currentTimeMillis() - inicio3;
                System.out.println("📊 Métricas finales:");
                System.out.println("   • Total procesado: " + contador.get());
                System.out.println("   • Tiempo total: " + duracionTotal + "ms");
                System.out.println("   • Tiempo promedio: " + (contador.get() > 0 ? duracionTotal / contador.get() : 0) + "ms");
            })
            .doOnError(error -> {
                long duracionError = System.currentTimeMillis() - inicio3;
                System.out.println("📊 Error después de " + duracionError + "ms: " + error.getMessage());
            });
        
        sistemaConMetricas.subscribe(testSistemaMetricas);
        
        testSistemaMetricas.assertValueCount(5);
        testSistemaMetricas.assertComplete();
        testSistemaMetricas.assertNoErrors();
        System.out.println("✓ Sistema con métricas validado");

        // 7. Test de sistema con backpressure
        System.out.println("\n--- Test de sistema con backpressure ---");
        TestObserver<Integer> testSistemaBackpressure = new TestObserver<>();
        
        Observable<Integer> sistemaConBackpressure = Observable.range(1, 10000)
            .observeOn(io.reactivex.schedulers.Schedulers.computation(), false, 100) // buffer de 100
            .doOnNext(i -> {
                if (i % 1000 == 0) {
                    System.out.println("📊 Procesado: " + i);
                }
            });
        
        sistemaConBackpressure.subscribe(testSistemaBackpressure);
        
        // Esperar a que se complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testSistemaBackpressure.assertValueCount(10000);
        testSistemaBackpressure.assertComplete();
        testSistemaBackpressure.assertNoErrors();
        System.out.println("✓ Sistema con backpressure validado");

        // 8. Test de sistema con operadores complejos
        System.out.println("\n--- Test de sistema con operadores complejos ---");
        TestObserver<String> testSistemaComplejo = new TestObserver<>();
        
        Observable<String> sistemaComplejo = Observable.range(1, 100)
            .doOnNext(i -> System.out.println("📥 Recibido: " + i))
            .filter(i -> i % 2 == 0)
            .doOnNext(i -> System.out.println("✅ Filtrado: " + i))
            .map(i -> "Procesado: " + i)
            .doOnNext(s -> System.out.println("🔄 " + s))
            .distinct()
            .doOnNext(s -> System.out.println("🔍 Distinto: " + s))
            .take(5)
            .doOnNext(s -> System.out.println("📦 Tomado: " + s))
            .doOnComplete(() -> System.out.println("✅ Sistema complejo completado"));
        
        sistemaComplejo.subscribe(testSistemaComplejo);
        
        testSistemaComplejo.assertValueCount(5);
        testSistemaComplejo.assertComplete();
        testSistemaComplejo.assertNoErrors();
        System.out.println("✓ Sistema con operadores complejos validado");

        // 9. Test de sistema con combinación
        System.out.println("\n--- Test de sistema con combinación ---");
        TestObserver<String> testSistemaCombinacion = new TestObserver<>();
        
        Observable<String> fuente1 = Observable.just("A", "B", "C")
            .doOnNext(s -> System.out.println("📖 Fuente 1: " + s));
        Observable<String> fuente2 = Observable.just("1", "2", "3")
            .doOnNext(s -> System.out.println("📖 Fuente 2: " + s));
        
        Observable<String> sistemaCombinacion = Observable.zip(fuente1, fuente2, (a, b) -> a + b)
            .doOnNext(s -> System.out.println("🔄 Combinado: " + s))
            .doOnComplete(() -> System.out.println("✅ Sistema con combinación completado"));
        
        sistemaCombinacion.subscribe(testSistemaCombinacion);
        
        testSistemaCombinacion.assertValues("A1", "B2", "C3");
        testSistemaCombinacion.assertComplete();
        testSistemaCombinacion.assertNoErrors();
        System.out.println("✓ Sistema con combinación validado");

        // 10. Test de sistema con reducción
        System.out.println("\n--- Test de sistema con reducción ---");
        TestObserver<Integer> testSistemaReduccion = new TestObserver<>();
        
        Observable<Integer> sistemaReduccion = Observable.range(1, 10)
            .doOnNext(i -> System.out.println("📥 Número: " + i))
            .reduce(0, (acc, x) -> {
                int resultado = acc + x;
                System.out.println("➕ Suma parcial: " + acc + " + " + x + " = " + resultado);
                return resultado;
            })
            .toObservable()
            .doOnNext(suma -> System.out.println("🎯 Suma total: " + suma))
            .doOnComplete(() -> System.out.println("✅ Sistema con reducción completado"));
        
        sistemaReduccion.subscribe(testSistemaReduccion);
        
        testSistemaReduccion.assertValues(55);
        testSistemaReduccion.assertComplete();
        testSistemaReduccion.assertNoErrors();
        System.out.println("✓ Sistema con reducción validado");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Testing completo: Validar sistemas reales");
        System.out.println("• Manejo de errores: Recuperación y retry");
        System.out.println("• Performance: Métricas y cache");
        System.out.println("• Backpressure: Control de flujo");
        System.out.println("• Operadores complejos: Combinación de operadores");
        System.out.println("• Métricas: Monitoreo de rendimiento");
        System.out.println("• Timeout: Control de tiempo");
        System.out.println("• Cache: Optimización de performance");
        System.out.println("• Combinación: Múltiples fuentes");
        System.out.println("• Reducción: Acumulación de datos");
    }
}
