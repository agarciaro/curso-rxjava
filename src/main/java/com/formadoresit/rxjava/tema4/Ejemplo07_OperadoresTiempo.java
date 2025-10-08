package com.formadoresit.rxjava.tema4;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 07: Operadores de Tiempo
 * 
 * Operadores para manejar tiempo en streams reactivos
 * - interval: Emite números secuenciales en intervalos regulares
 * - timer: Emite un solo valor después de un delay
 * - delay: Retrasa todas las emisiones
 * - delaySubscription: Retrasa la suscripción
 * - debounce: Emite solo después de un período de inactividad
 * - throttleFirst: Emite el primer elemento en cada ventana de tiempo
 * - throttleLast/sample: Emite el último elemento en cada ventana de tiempo
 * - throttleWithTimeout: Como debounce pero con timeout
 * - timeout: Genera error si no emite en tiempo
 * - takeUntil: Toma elementos hasta que otro Observable emita
 */
public class Ejemplo07_OperadoresTiempo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 07: Operadores de Tiempo ===\n");

        // 1. interval() - Emite números en intervalos regulares
        System.out.println("--- interval() básico ---");
        Observable.interval(500, TimeUnit.MILLISECONDS)
            .take(5)
            .blockingSubscribe(numero -> System.out.println("Interval: " + numero));

        // interval con delay inicial
        System.out.println("\n--- interval() con delay inicial ---");
        Observable.interval(1000, 500, TimeUnit.MILLISECONDS)
            .take(3)
            .blockingSubscribe(numero -> System.out.println("Delayed interval: " + numero));

        // 2. timer() - Emite un valor después de un delay
        System.out.println("\n--- timer() ---");
        Observable.timer(1, TimeUnit.SECONDS)
            .blockingSubscribe(tick -> System.out.println("Timer completado después de 1 segundo"));

        // timer con múltiples emisiones
        System.out.println("\n--- timer() con repeat ---");
        Observable.timer(500, TimeUnit.MILLISECONDS)
            .repeat(3)
            .blockingSubscribe(tick -> System.out.println("Timer repeat: " + tick));

        // 3. delay() - Retrasa todas las emisiones
        System.out.println("\n--- delay() ---");
        System.out.println("Iniciando delay de 1 segundo...");
        Observable.just("A", "B", "C")
            .delay(1, TimeUnit.SECONDS)
            .blockingSubscribe(letra -> System.out.println("Delayed: " + letra));

        // delay con diferentes delays por elemento
        System.out.println("\n--- delay() personalizado ---");
        Observable.just(1, 2, 3)
            .delay(item -> Observable.timer(item * 200, TimeUnit.MILLISECONDS))
            .blockingSubscribe(numero -> System.out.println("Custom delay: " + numero));

        // 4. delaySubscription() - Retrasa la suscripción
        System.out.println("\n--- delaySubscription() ---");
        System.out.println("Retrasando suscripción 1 segundo...");
        Observable.just("X", "Y", "Z")
            .delaySubscription(1, TimeUnit.SECONDS)
            .blockingSubscribe(letra -> System.out.println("Delayed subscription: " + letra));

        // 5. debounce() - Emite solo después de período de inactividad
        System.out.println("\n--- debounce() ---");
        Observable.create(emitter -> {
            emitter.onNext("A");
            Thread.sleep(100);
            emitter.onNext("B");
            Thread.sleep(100);
            emitter.onNext("C");
            Thread.sleep(500); // Pausa larga
            emitter.onNext("D");
            Thread.sleep(100);
            emitter.onNext("E");
            Thread.sleep(500); // Pausa larga
            emitter.onNext("F");
            emitter.onComplete();
        })
        .debounce(200, TimeUnit.MILLISECONDS)
        .blockingSubscribe(letra -> System.out.println("Debounced: " + letra));

        // 6. throttleFirst() - Primer elemento en cada ventana de tiempo
        System.out.println("\n--- throttleFirst() ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(10)
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .blockingSubscribe(numero -> System.out.println("Throttle first: " + numero));

        // 7. throttleLast() / sample() - Último elemento en cada ventana de tiempo
        System.out.println("\n--- throttleLast() / sample() ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(10)
            .throttleLast(300, TimeUnit.MILLISECONDS)
            .blockingSubscribe(numero -> System.out.println("Throttle last: " + numero));

        // 8. throttleWithTimeout() - Como debounce pero con timeout
        System.out.println("\n--- throttleWithTimeout() ---");
        Observable.create(emitter -> {
            for (int i = 0; i < 5; i++) {
                emitter.onNext("Mensaje " + i);
                Thread.sleep(150);
            }
            emitter.onComplete();
        })
        .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
        .blockingSubscribe(mensaje -> System.out.println("Throttle timeout: " + mensaje));

        // 9. timeout() - Genera error si no emite en tiempo
        System.out.println("\n--- timeout() ---");
        try {
            Observable.create(emitter -> {
                emitter.onNext("Rápido");
                Thread.sleep(2000); // Espera muy larga
                emitter.onNext("Lento");
            })
            .timeout(500, TimeUnit.MILLISECONDS)
            .blockingSubscribe(mensaje -> System.out.println("Timeout: " + mensaje));
        } catch (Exception e) {
            System.err.println("Timeout error: " + e.getMessage());
        }

        // 10. takeUntil() - Toma elementos hasta que otro Observable emita
        System.out.println("\n--- takeUntil() ---");
        Observable<Long> fuente = Observable.interval(200, TimeUnit.MILLISECONDS);
        Observable<Long> señalParada = Observable.timer(1, TimeUnit.SECONDS);
        
        fuente.takeUntil(señalParada)
            .blockingSubscribe(numero -> System.out.println("Take until: " + numero));

        // 11. Ejemplo práctico: Búsqueda con debounce
        System.out.println("\n--- Ejemplo práctico: Búsqueda con debounce ---");
        Observable.just("j", "ju", "jua", "juan", "juan", "juanp", "juanpe", "juanper", "juanperez")
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter(texto -> texto.length() >= 3)
            .distinctUntilChanged()
            .blockingSubscribe(busqueda -> System.out.println("Buscando: " + busqueda));

        // 12. Ejemplo práctico: Rate limiting
        System.out.println("\n--- Ejemplo práctico: Rate limiting ---");
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(20)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .blockingSubscribe(numero -> System.out.println("Rate limited: " + numero));

        // 13. Ejemplo práctico: Heartbeat
        System.out.println("\n--- Ejemplo práctico: Heartbeat ---");
        Observable.interval(1, TimeUnit.SECONDS)
            .take(5)
            .map(tick -> "Heartbeat " + (tick + 1))
            .blockingSubscribe(heartbeat -> System.out.println(heartbeat));

        // 14. Ejemplo práctico: Timeout con fallback
        System.out.println("\n--- Ejemplo práctico: Timeout con fallback ---");
        Observable.create(emitter -> {
            Thread.sleep(800); // Simula operación lenta
            emitter.onNext("Datos obtenidos");
            emitter.onComplete();
        })
        .timeout(500, TimeUnit.MILLISECONDS, Observable.just("Datos de respaldo"))
        .blockingSubscribe(datos -> System.out.println("Datos: " + datos));

        // 15. Ejemplo avanzado: Circuit breaker con timeout
        System.out.println("\n--- Ejemplo avanzado: Circuit breaker ---");
        Observable<String> servicio = Observable.create(emitter -> {
            double random = Math.random();
            if (random < 0.5) {
                Thread.sleep(200); // Respuesta rápida
                emitter.onNext("Servicio OK");
                emitter.onComplete();
            } else {
                Thread.sleep(1000); // Respuesta lenta
                emitter.onNext("Servicio lento");
                emitter.onComplete();
            }
        });

        servicio
            .timeout(500, TimeUnit.MILLISECONDS)
            .onErrorReturn(error -> "Servicio no disponible")
            .blockingSubscribe(resultado -> System.out.println("Resultado: " + resultado));

        // 16. Ejemplo avanzado: Polling con backoff
        System.out.println("\n--- Ejemplo avanzado: Polling con backoff ---");
        Observable.defer(() -> {
            System.out.println("  Haciendo polling...");
            return Observable.just("Datos del polling")
                .delay(100, TimeUnit.MILLISECONDS);
        })
        .repeat()
        .take(5)
        .delay(500, TimeUnit.MILLISECONDS) // Delay entre polls
        .blockingSubscribe(datos -> System.out.println("Polling: " + datos));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• interval: Emite números en intervalos regulares");
        System.out.println("• timer: Emite un valor después de un delay");
        System.out.println("• delay: Retrasa todas las emisiones");
        System.out.println("• debounce: Útil para búsquedas (espera inactividad)");
        System.out.println("• throttleFirst: Rate limiting (primer elemento por ventana)");
        System.out.println("• throttleLast: Rate limiting (último elemento por ventana)");
        System.out.println("• timeout: Genera error si no emite en tiempo");
        System.out.println("• takeUntil: Toma elementos hasta señal de parada");
        System.out.println("• Circuit breaker: Patrón para servicios inestables");
        System.out.println("• Polling: Consulta periódica de datos");
    }
}

