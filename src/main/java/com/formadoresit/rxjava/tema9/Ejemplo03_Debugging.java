package com.formadoresit.rxjava.tema9;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 9: Pruebas y Depuración
 * Ejemplo 03: Debugging
 * 
 * Técnicas de debugging en RxJava usando operadores de debugging
 * y logging para entender el flujo de datos
 */
public class Ejemplo03_Debugging {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 03: Debugging ===\n");

        // 1. Debugging básico con doOnNext, doOnError, doOnComplete
        System.out.println("--- Debugging básico ---");
        Observable<Integer> observable = Observable.just(1, 2, 3, 4, 5)
            .doOnSubscribe(disposable -> System.out.println("🔍 Suscripción iniciada"))
            .doOnNext(value -> System.out.println("📖 Procesando: " + value))
            .doOnComplete(() -> System.out.println("✅ Completado"))
            .doOnError(error -> System.out.println("❌ Error: " + error.getMessage()))
            .doOnDispose(() -> System.out.println("🧹 Limpieza"));

        TestObserver<Integer> testObserver = new TestObserver<>();
        observable.subscribe(testObserver);

        testObserver.assertValues(1, 2, 3, 4, 5);
        testObserver.assertComplete();
        System.out.println("✓ Debugging básico completado");

        // 2. Debugging con operadores de transformación
        System.out.println("\n--- Debugging con transformaciones ---");
        Observable<String> transformacion = Observable.just(1, 2, 3, 4, 5)
            .doOnNext(value -> System.out.println("🔍 Valor original: " + value))
            .filter(x -> x % 2 == 0)
            .doOnNext(value -> System.out.println("✅ Valor filtrado: " + value))
            .map(x -> "Número: " + x)
            .doOnNext(value -> System.out.println("🔄 Valor transformado: " + value))
            .doOnComplete(() -> System.out.println("🎉 Transformación completada"));

        TestObserver<String> testTransformacion = new TestObserver<>();
        transformacion.subscribe(testTransformacion);

        testTransformacion.assertValues("Número: 2", "Número: 4");
        System.out.println("✓ Debugging de transformaciones completado");

        // 3. Debugging con operadores temporales
        System.out.println("\n--- Debugging con operadores temporales ---");
        Observable<String> temporal = Observable.just("A", "B", "C")
            .doOnNext(value -> System.out.println("⏰ Emitiendo: " + value))
            .delay(100, TimeUnit.MILLISECONDS)
            .doOnNext(value -> System.out.println("⏰ Recibido después de delay: " + value))
            .doOnComplete(() -> System.out.println("⏰ Operación temporal completada"));

        TestObserver<String> testTemporal = new TestObserver<>();
        temporal.subscribe(testTemporal);

        // Esperar un poco para que se complete el delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        testTemporal.assertValues("A", "B", "C");
        testTemporal.assertComplete();
        System.out.println("✓ Debugging temporal completado");

        // 4. Debugging con manejo de errores
        System.out.println("\n--- Debugging con manejo de errores ---");
        Observable<String> conError = Observable.just("A", "B", "C")
            .doOnNext(value -> {
                System.out.println("🔍 Procesando: " + value);
                if ("B".equals(value)) {
                    throw new RuntimeException("Error en B");
                }
            })
            .doOnError(error -> System.out.println("❌ Error capturado: " + error.getMessage()))
            .onErrorResumeNext(Observable.just("Recuperación"))
            .doOnNext(value -> System.out.println("🔄 Valor final: " + value))
            .doOnComplete(() -> System.out.println("✅ Recuperación completada"));

        TestObserver<String> testError = new TestObserver<>();
        conError.subscribe(testError);

        testError.assertValues("A", "Recuperación");
        testError.assertComplete();
        System.out.println("✓ Debugging de errores completado");

        // 5. Debugging con operadores de combinación
        System.out.println("\n--- Debugging con combinación ---");
        Observable<String> fuente1 = Observable.just("A", "B", "C")
            .doOnNext(value -> System.out.println("📖 Fuente 1: " + value));
        
        Observable<String> fuente2 = Observable.just("1", "2", "3")
            .doOnNext(value -> System.out.println("📖 Fuente 2: " + value));

        Observable<String> combinado = Observable.zip(fuente1, fuente2, (a, b) -> a + b)
            .doOnNext(value -> System.out.println("🔄 Combinado: " + value))
            .doOnComplete(() -> System.out.println("✅ Combinación completada"));

        TestObserver<String> testCombinado = new TestObserver<>();
        combinado.subscribe(testCombinado);

        testCombinado.assertValues("A1", "B2", "C3");
        testCombinado.assertComplete();
        System.out.println("✓ Debugging de combinación completado");

        // 6. Debugging con operadores de reducción
        System.out.println("\n--- Debugging con reducción ---");
        Observable<Integer> numeros = Observable.just(1, 2, 3, 4, 5)
            .doOnNext(value -> System.out.println("🔍 Número: " + value));

        Observable<Integer> suma = numeros
            .reduce(0, (acc, x) -> {
                int resultado = acc + x;
                System.out.println("➕ Suma parcial: " + acc + " + " + x + " = " + resultado);
                return resultado;
            })
            .toObservable()
            .doOnNext(value -> System.out.println("🎯 Suma total: " + value))
            .doOnComplete(() -> System.out.println("✅ Reducción completada"));

        TestObserver<Integer> testSuma = new TestObserver<>();
        suma.subscribe(testSuma);

        testSuma.assertValues(15);
        testSuma.assertComplete();
        System.out.println("✓ Debugging de reducción completado");

        // 7. Debugging con operadores de agrupación
        System.out.println("\n--- Debugging con agrupación ---");
        Observable<String> palabras = Observable.just("apple", "banana", "cherry", "date", "elderberry")
            .doOnNext(value -> System.out.println("🔍 Palabra: " + value));

        Observable<String> agrupado = palabras
            .groupBy(word -> word.length())
            .flatMap(group -> {
                System.out.println("📦 Grupo de longitud " + group.getKey() + ":");
                return group.doOnNext(word -> System.out.println("  📝 " + word));
            })
            .doOnComplete(() -> System.out.println("✅ Agrupación completada"));

        TestObserver<String> testAgrupado = new TestObserver<>();
        agrupado.subscribe(testAgrupado);

        testAgrupado.assertValueCount(5);
        testAgrupado.assertComplete();
        System.out.println("✓ Debugging de agrupación completado");

        // 8. Debugging con operadores de buffer
        System.out.println("\n--- Debugging con buffer ---");
        Observable<Integer> secuencia = Observable.range(1, 10)
            .doOnNext(value -> System.out.println("🔍 Emitiendo: " + value));

        Observable<java.util.List<Integer>> buffer = secuencia
            .buffer(3)
            .doOnNext(lista -> System.out.println("📦 Buffer: " + lista))
            .doOnComplete(() -> System.out.println("✅ Buffering completado"));

        TestObserver<java.util.List<Integer>> testBuffer = new TestObserver<>();
        buffer.subscribe(testBuffer);

        testBuffer.assertValueCount(4); // 3 buffers de 3 + 1 buffer de 1
        testBuffer.assertComplete();
        System.out.println("✓ Debugging de buffer completado");

        // 9. Debugging con operadores de window
        System.out.println("\n--- Debugging con window ---");
        Observable<Integer> secuencia2 = Observable.range(1, 6)
            .doOnNext(value -> System.out.println("🔍 Emitiendo: " + value));

        Observable<Observable<Integer>> window = secuencia2
            .window(2)
            .doOnNext(obs -> System.out.println("🪟 Nueva ventana creada"))
            .doOnComplete(() -> System.out.println("✅ Windowing completado"));

        TestObserver<Observable<Integer>> testWindow = new TestObserver<>();
        window.subscribe(testWindow);

        testWindow.assertValueCount(3); // 3 windows
        testWindow.assertComplete();
        System.out.println("✓ Debugging de window completado");

        // 10. Debugging con métricas personalizadas
        System.out.println("\n--- Debugging con métricas ---");
        long inicio = System.currentTimeMillis();
        final AtomicInteger contador = new AtomicInteger(0);

        Observable<String> conMetricas = Observable.just("A", "B", "C", "D", "E")
            .doOnSubscribe(disposable -> {
                System.out.println("📊 Iniciando procesamiento - Tiempo: " + (System.currentTimeMillis() - inicio) + "ms");
            })
            .doOnNext(value -> {
                int count = contador.incrementAndGet();
                long tiempoActual = System.currentTimeMillis() - inicio;
                System.out.println("📈 Procesado " + count + " en " + tiempoActual + "ms - Valor: " + value);
            })
            .doOnComplete(() -> {
                long duracionTotal = System.currentTimeMillis() - inicio;
                System.out.println("📊 Métricas finales:");
                System.out.println("   • Total procesado: " + contador.get());
                System.out.println("   • Tiempo total: " + duracionTotal + "ms");
                System.out.println("   • Tiempo promedio: " + (contador.get() > 0 ? duracionTotal / contador.get() : 0) + "ms");
            })
            .doOnError(error -> {
                long duracionError = System.currentTimeMillis() - inicio;
                System.out.println("📊 Error después de " + duracionError + "ms: " + error.getMessage());
            });

        TestObserver<String> testMetricas = new TestObserver<>();
        conMetricas.subscribe(testMetricas);

        testMetricas.assertValues("A", "B", "C", "D", "E");
        testMetricas.assertComplete();
        System.out.println("✓ Debugging con métricas completado");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• doOnNext: Ejecutar código por cada elemento");
        System.out.println("• doOnError: Ejecutar código en caso de error");
        System.out.println("• doOnComplete: Ejecutar código al completar");
        System.out.println("• doOnSubscribe: Ejecutar código al suscribirse");
        System.out.println("• doOnDispose: Ejecutar código al limpiar");
        System.out.println("• Útil para: logging, métricas, debugging, monitoreo");
    }
}
