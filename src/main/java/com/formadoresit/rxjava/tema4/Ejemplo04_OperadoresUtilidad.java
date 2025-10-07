package com.formadoresit.rxjava.tema4;

import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 04: Operadores de Utilidad
 * 
 * Operadores auxiliares para debugging, control y observación
 * - doOnNext, doOnError, doOnComplete: Side effects
 * - doOnSubscribe, doOnDispose: Lifecycle hooks
 * - delay: Retrasa emisiones
 * - timeout: Limite de tiempo
 * - timestamp: Agrega timestamp
 * - timeInterval: Tiempo entre emisiones
 */
public class Ejemplo04_OperadoresUtilidad {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 04: Operadores de Utilidad ===\n");

        // 1. doOnNext() - Ejecuta acción para cada elemento
        System.out.println("--- doOnNext() ---");
        Observable.just(1, 2, 3)
            .doOnNext(n -> System.out.println("  Procesando: " + n))
            .map(n -> n * 10)
            .subscribe(resultado -> System.out.println("Resultado: " + resultado));

        // 2. doOnComplete() - Ejecuta al completar
        System.out.println("\n--- doOnComplete() ---");
        Observable.just("A", "B", "C")
            .doOnComplete(() -> System.out.println("  [Logging] Flujo completado"))
            .subscribe(letra -> System.out.println("Letra: " + letra));

        // 3. doOnError() - Ejecuta al ocurrir error
        System.out.println("\n--- doOnError() ---");
        Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onError(new RuntimeException("Error intencional"));
        })
        .doOnError(error -> System.err.println("  [Logging] Error: " + error.getMessage()))
        .subscribe(
            n -> System.out.println("Número: " + n),
            error -> System.err.println("Capturado: " + error.getMessage())
        );

        // 4. doOnSubscribe() - Ejecuta al suscribirse
        System.out.println("\n--- doOnSubscribe() ---");
        Observable.just(10, 20, 30)
            .doOnSubscribe(d -> System.out.println("  [Logging] Suscripción iniciada"))
            .subscribe(n -> System.out.println("Número: " + n));

        // 5. doOnDispose() - Ejecuta al cancelar
        System.out.println("\n--- doOnDispose() ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .doOnDispose(() -> System.out.println("  [Logging] Suscripción cancelada"))
            .take(3)
            .blockingSubscribe(n -> System.out.println("Tick: " + n));

        // 6. doFinally() - Ejecuta siempre al final
        System.out.println("\n--- doFinally() ---");
        Observable.just(1, 2, 3)
            .doFinally(() -> System.out.println("  [Cleanup] Limpiando recursos"))
            .subscribe(n -> System.out.println("Número: " + n));

        // 7. doAfterNext() - Ejecuta DESPUÉS de emitir
        System.out.println("\n--- doAfterNext() vs doOnNext() ---");
        Observable.just("X", "Y", "Z")
            .doOnNext(s -> System.out.println("  Antes de emitir: " + s))
            .doAfterNext(s -> System.out.println("  Después de emitir: " + s))
            .subscribe(s -> System.out.println("Recibido: " + s));

        // 8. delay() - Retrasa las emisiones
        System.out.println("\n--- delay() ---");
        System.out.println("Retrasando 500ms...");
        Observable.just(1, 2, 3)
            .delay(500, TimeUnit.MILLISECONDS)
            .blockingSubscribe(n -> System.out.println("Número: " + n));

        // 9. delaySubscription() - Retrasa la suscripción
        System.out.println("\n--- delaySubscription() ---");
        System.out.println("Retrasando suscripción 300ms...");
        Observable.just("A", "B", "C")
            .delaySubscription(300, TimeUnit.MILLISECONDS)
            .blockingSubscribe(letra -> System.out.println("Letra: " + letra));

        // 10. timeout() - Timeout si no emite en tiempo
        System.out.println("\n--- timeout() ---");
        try {
            Observable.create(emitter -> {
                emitter.onNext(1);
                Thread.sleep(2000);  // Espera muy larga
                emitter.onNext(2);
            })
            .timeout(500, TimeUnit.MILLISECONDS)
            .blockingSubscribe(n -> System.out.println("Número: " + n));
        } catch (Exception e) {
            System.err.println("Timeout: " + e.getMessage());
        }

        // 11. timestamp() - Agrega timestamp a cada elemento
        System.out.println("\n--- timestamp() ---");
        Observable.just("A", "B", "C")
            .timestamp()
            .subscribe(timed -> 
                System.out.println("Elemento: " + timed.value() + 
                                 " en tiempo: " + timed.time() + "ms"));

        // 12. timeInterval() - Tiempo entre emisiones
        System.out.println("\n--- timeInterval() ---");
        Observable.interval(200, TimeUnit.MILLISECONDS)
            .take(3)
            .timeInterval()
            .blockingSubscribe(timed -> 
                System.out.println("Elemento: " + timed.value() + 
                                 ", intervalo: " + timed.time() + "ms"));

        // 13. materialize() / dematerialize() - Envuelve en Notification
        System.out.println("\n--- materialize() ---");
        Observable.just(1, 2, 3)
            .materialize()
            .subscribe(notification -> 
                System.out.println("Notification: " + notification));

        // 14. observeOn() - Cambia hilo de observación (se verá más en concurrencia)
        System.out.println("\n--- observeOn() ---");
        Observable.just(1, 2, 3)
            .doOnNext(n -> System.out.println("  Emitido en: " + Thread.currentThread().getName()))
            .observeOn(io.reactivex.schedulers.Schedulers.io())
            .subscribe(n -> System.out.println("  Observado en: " + Thread.currentThread().getName()));

        Thread.sleep(100);

        // 15. repeat() - Repite la secuencia
        System.out.println("\n--- repeat() ---");
        Observable.just("¡Hola!")
            .repeat(3)
            .subscribe(mensaje -> System.out.println(mensaje));

        // 16. cache() - Cachea resultados
        System.out.println("\n--- cache() ---");
        Observable<Long> cacheado = Observable
            .interval(100, TimeUnit.MILLISECONDS)
            .take(3)
            .doOnNext(n -> System.out.println("  Emitiendo: " + n))
            .cache();

        System.out.println("Primera suscripción:");
        cacheado.blockingSubscribe(n -> System.out.println("Sub1: " + n));

        System.out.println("Segunda suscripción (desde cache):");
        cacheado.blockingSubscribe(n -> System.out.println("Sub2: " + n));

        // Ejemplo práctico: Logging completo
        System.out.println("\n--- Ejemplo práctico: Logging completo ---");
        Observable.just(1, 2, 3, 4, 5)
            .doOnSubscribe(d -> System.out.println("→ Iniciando procesamiento"))
            .doOnNext(n -> System.out.println("→ Procesando: " + n))
            .filter(n -> n % 2 == 0)
            .doAfterNext(n -> System.out.println("→ Filtrado aprobado: " + n))
            .map(n -> n * 10)
            .doOnNext(n -> System.out.println("→ Transformado: " + n))
            .doOnComplete(() -> System.out.println("→ Procesamiento completado"))
            .doFinally(() -> System.out.println("→ Limpieza finalizada"))
            .subscribe(
                resultado -> System.out.println("✓ Resultado final: " + resultado),
                error -> System.err.println("✗ Error: " + error),
                () -> System.out.println("✓ ¡Listo!")
            );

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• doOn* operators: Side effects sin alterar el flujo");
        System.out.println("• doOnNext/doOnComplete/doOnError: Para logging/debugging");
        System.out.println("• delay: Retrasa emisiones");
        System.out.println("• timeout: Límite de tiempo");
        System.out.println("• timestamp/timeInterval: Información temporal");
        System.out.println("• repeat/cache: Control de reproducción");
    }
}

