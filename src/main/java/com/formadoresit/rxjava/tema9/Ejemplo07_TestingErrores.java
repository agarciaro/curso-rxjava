package com.formadoresit.rxjava.tema9;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 9: Pruebas y Depuración
 * Ejemplo 07: Testing de Errores
 * 
 * Técnicas para testing de manejo de errores en RxJava incluyendo
 * validación de tipos de error, recuperación y propagación
 */
public class Ejemplo07_TestingErrores {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 07: Testing de Errores ===\n");

        // 1. Test de error básico
        System.out.println("--- Test de error básico ---");
        TestObserver<String> testErrorBasico = new TestObserver<>();
        
        Observable<String> conError = Observable.<String>create(emitter -> {
            emitter.onNext("A");
            emitter.onNext("B");
            emitter.onError(new RuntimeException("Error de prueba"));
        });
        
        conError.subscribe(testErrorBasico);
        
        testErrorBasico.assertValues("A", "B");
        testErrorBasico.assertError(RuntimeException.class);
        testErrorBasico.assertErrorMessage("Error de prueba");
        testErrorBasico.assertNotComplete();
        testErrorBasico.assertTerminated();
        System.out.println("✓ Error básico validado");

        // 2. Test de error con onErrorResumeNext
        System.out.println("\n--- Test de error con onErrorResumeNext ---");
        TestObserver<String> testErrorResume = new TestObserver<>();
        
        Observable<String> conRecuperacion = Observable.<String>create(emitter -> {
            emitter.onNext("A");
            emitter.onError(new RuntimeException("Error recuperable"));
        })
        .onErrorResumeNext(Observable.just("Recuperación", "Exitosa"));
        
        conRecuperacion.subscribe(testErrorResume);
        
        testErrorResume.assertValues("A", "Recuperación", "Exitosa");
        testErrorResume.assertComplete();
        testErrorResume.assertNoErrors();
        System.out.println("✓ Error con recuperación validado");

        // 3. Test de error con onErrorReturn
        System.out.println("\n--- Test de error con onErrorReturn ---");
        TestObserver<String> testErrorReturn = new TestObserver<>();
        
        Observable<String> conReturn = Observable.<String>create(emitter -> {
            emitter.onNext("A");
            emitter.onError(new RuntimeException("Error con return"));
        })
        .onErrorReturn(throwable -> "Valor por defecto");
        
        conReturn.subscribe(testErrorReturn);
        
        testErrorReturn.assertValues("A", "Valor por defecto");
        testErrorReturn.assertComplete();
        testErrorReturn.assertNoErrors();
        System.out.println("✓ Error con return validado");

        // 4. Test de error con retry
        System.out.println("\n--- Test de error con retry ---");
        AtomicInteger intentos = new AtomicInteger(0);
        TestObserver<String> testRetry = new TestObserver<>();
        
        Observable<String> conRetry = Observable.<String>create(emitter -> {
            int intento = intentos.incrementAndGet();
            System.out.println("🔄 Intento " + intento);
            if (intento < 3) {
                emitter.onError(new RuntimeException("Error temporal " + intento));
            } else {
                emitter.onNext("Éxito después de " + intento + " intentos");
                emitter.onComplete();
            }
        })
        .retry(3);
        
        conRetry.subscribe(testRetry);
        
        testRetry.assertValues("Éxito después de 3 intentos");
        testRetry.assertComplete();
        testRetry.assertNoErrors();
        System.out.println("✓ Error con retry validado");

        // 5. Test de error con retryWhen
        System.out.println("\n--- Test de error con retryWhen ---");
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> testRetryWhen = new TestObserver<>();
        
        Observable<String> conRetryWhen = Observable.<String>create(emitter -> {
            emitter.onError(new RuntimeException("Error con retryWhen"));
        })
        .retryWhen(errors -> errors.delay(1, TimeUnit.SECONDS, scheduler));
        
        conRetryWhen.subscribe(testRetryWhen);
        
        // Avanzar tiempo para permitir retry
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        
        testRetryWhen.assertError(RuntimeException.class);
        System.out.println("✓ Error con retryWhen validado");

        // 6. Test de error con timeout
        System.out.println("\n--- Test de error con timeout ---");
        TestScheduler schedulerTimeout = new TestScheduler();
        TestObserver<String> testTimeout = new TestObserver<>();
        
        Observable<String> conTimeout = Observable.<String>create(emitter -> {
            // Simular operación lenta
            schedulerTimeout.createWorker().schedule(() -> {
                emitter.onNext("Resultado tardío");
                emitter.onComplete();
            }, 5, TimeUnit.SECONDS);
        })
        .timeout(3, TimeUnit.SECONDS, schedulerTimeout);
        
        conTimeout.subscribe(testTimeout);
        
        // Avanzar tiempo para timeout
        schedulerTimeout.advanceTimeBy(3, TimeUnit.SECONDS);
        
        testTimeout.assertError(java.util.concurrent.TimeoutException.class);
        System.out.println("✓ Error con timeout validado");

        // 7. Test de error con catch
        System.out.println("\n--- Test de error con catch ---");
        TestObserver<String> testCatch = new TestObserver<>();
        
        Observable<String> conCatch = Observable.<String>create(emitter -> {
            emitter.onNext("A");
            emitter.onError(new RuntimeException("Error capturado"));
        })
        .onErrorResumeNext(throwable -> {
            if (throwable instanceof RuntimeException) {
                return Observable.just("Error manejado");
            }
            return Observable.error(throwable);
        });
        
        conCatch.subscribe(testCatch);
        
        testCatch.assertValues("A", "Error manejado");
        testCatch.assertComplete();
        testCatch.assertNoErrors();
        System.out.println("✓ Error con catch validado");

        // 8. Test de error con doOnError
        System.out.println("\n--- Test de error con doOnError ---");
        TestObserver<String> testDoOnError = new TestObserver<>();
        
        Observable<String> conDoOnError = Observable.<String>create(emitter -> {
            emitter.onNext("A");
            emitter.onError(new RuntimeException("Error con doOnError"));
        })
        .doOnError(error -> System.out.println("❌ Error capturado: " + error.getMessage()));
        
        conDoOnError.subscribe(testDoOnError);
        
        testDoOnError.assertValues("A");
        testDoOnError.assertError(RuntimeException.class);
        System.out.println("✓ Error con doOnError validado");

        // 9. Test de error con finally
        System.out.println("\n--- Test de error con finally ---");
        TestObserver<String> testFinally = new TestObserver<>();
        
        Observable<String> conFinally = Observable.<String>create(emitter -> {
            emitter.onNext("A");
            emitter.onError(new RuntimeException("Error con finally"));
        })
        .doFinally(() -> System.out.println("🧹 Limpieza ejecutada"));
        
        conFinally.subscribe(testFinally);
        
        testFinally.assertValues("A");
        testFinally.assertError(RuntimeException.class);
        System.out.println("✓ Error con finally validado");

        // 10. Test de error con multiple errors
        System.out.println("\n--- Test de error con múltiples errores ---");
        TestObserver<String> testMultipleErrors = new TestObserver<>();
        
        Observable<String> conMultipleErrors = Observable.just("A", "B", "C")
            .map(s -> {
                if ("B".equals(s)) {
                    throw new RuntimeException("Error en B");
                }
                return s;
            })
            .onErrorResumeNext(Observable.just("Recuperación"));
        
        conMultipleErrors.subscribe(testMultipleErrors);
        
        testMultipleErrors.assertValues("A", "Recuperación");
        testMultipleErrors.assertComplete();
        testMultipleErrors.assertNoErrors();
        System.out.println("✓ Error con múltiples errores validado");

        // 11. Test de error con error específico
        System.out.println("\n--- Test de error con error específico ---");
        TestObserver<String> testErrorEspecifico = new TestObserver<>();
        
        Observable<String> conErrorEspecifico = Observable.<String>create(emitter -> {
            emitter.onError(new IllegalArgumentException("Error específico"));
        });
        
        conErrorEspecifico.subscribe(testErrorEspecifico);
        
        testErrorEspecifico.assertError(IllegalArgumentException.class);
        testErrorEspecifico.assertErrorMessage("Error específico");
        System.out.println("✓ Error específico validado");

        // 12. Test de error con error personalizado
        System.out.println("\n--- Test de error con error personalizado ---");
        TestObserver<String> testErrorPersonalizado = new TestObserver<>();
        
        class ErrorPersonalizado extends Exception {
            public ErrorPersonalizado(String message) {
                super(message);
            }
        }
        
        Observable<String> conErrorPersonalizado = Observable.<String>create(emitter -> {
            emitter.onError(new ErrorPersonalizado("Error personalizado"));
        });
        
        conErrorPersonalizado.subscribe(testErrorPersonalizado);
        
        testErrorPersonalizado.assertError(ErrorPersonalizado.class);
        testErrorPersonalizado.assertErrorMessage("Error personalizado");
        System.out.println("✓ Error personalizado validado");

        // 13. Test de error con error en operador
        System.out.println("\n--- Test de error con error en operador ---");
        TestObserver<String> testErrorOperador = new TestObserver<>();
        
        Observable<String> conErrorOperador = Observable.just("A", "B", "C")
            .map(s -> {
                if ("B".equals(s)) {
                    throw new RuntimeException("Error en operador map");
                }
                return s;
            });
        
        conErrorOperador.subscribe(testErrorOperador);
        
        testErrorOperador.assertValues("A");
        testErrorOperador.assertError(RuntimeException.class);
        testErrorOperador.assertErrorMessage("Error en operador map");
        System.out.println("✓ Error en operador validado");

        // 14. Test de error con error en flatMap
        System.out.println("\n--- Test de error con error en flatMap ---");
        TestObserver<String> testErrorFlatMap = new TestObserver<>();
        
        Observable<String> conErrorFlatMap = Observable.just("A", "B", "C")
            .flatMap(s -> {
                if ("B".equals(s)) {
                    return Observable.error(new RuntimeException("Error en flatMap"));
                }
                return Observable.just(s);
            });
        
        conErrorFlatMap.subscribe(testErrorFlatMap);
        
        testErrorFlatMap.assertValues("A");
        testErrorFlatMap.assertError(RuntimeException.class);
        testErrorFlatMap.assertErrorMessage("Error en flatMap");
        System.out.println("✓ Error en flatMap validado");

        // 15. Test de error con error en subscribe
        System.out.println("\n--- Test de error con error en subscribe ---");
        TestObserver<String> testErrorSubscribe = new TestObserver<>();
        
        Observable<String> conErrorSubscribe = Observable.just("A", "B", "C")
            .doOnNext(s -> {
                if ("B".equals(s)) {
                    throw new RuntimeException("Error en subscribe");
                }
            });
        
        conErrorSubscribe.subscribe(testErrorSubscribe);
        
        testErrorSubscribe.assertValues("A");
        testErrorSubscribe.assertError(RuntimeException.class);
        testErrorSubscribe.assertErrorMessage("Error en subscribe");
        System.out.println("✓ Error en subscribe validado");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• assertError(): Verificar tipo de error");
        System.out.println("• assertErrorMessage(): Verificar mensaje de error");
        System.out.println("• assertNotComplete(): Verificar que no se completó");
        System.out.println("• assertTerminated(): Verificar que terminó");
        System.out.println("• onErrorResumeNext(): Recuperación con Observable");
        System.out.println("• onErrorReturn(): Recuperación con valor");
        System.out.println("• retry(): Reintentos automáticos");
        System.out.println("• timeout(): Timeout con error");
    }
}
