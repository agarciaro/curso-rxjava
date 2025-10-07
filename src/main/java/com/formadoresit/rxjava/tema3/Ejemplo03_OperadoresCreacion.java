package com.formadoresit.rxjava.tema3;

import io.reactivex.Observable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 3: Observables y Observers
 * Ejemplo 03: Operadores de Creación
 * 
 * RxJava proporciona múltiples formas de crear Observables
 */
public class Ejemplo03_OperadoresCreacion {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 03: Operadores de Creación ===\n");

        // 1. just() - Crea Observable con elementos específicos
        System.out.println("--- just() ---");
        Observable.just("A", "B", "C")
            .subscribe(letra -> System.out.println("Letra: " + letra));

        // 2. fromArray() - Crea Observable desde array
        System.out.println("\n--- fromArray() ---");
        Integer[] numeros = {1, 2, 3, 4, 5};
        Observable.fromArray(numeros)
            .subscribe(n -> System.out.println("Número: " + n));

        // 3. fromIterable() - Crea Observable desde colección
        System.out.println("\n--- fromIterable() ---");
        List<String> frutas = Arrays.asList("Manzana", "Naranja", "Plátano");
        Observable.fromIterable(frutas)
            .subscribe(fruta -> System.out.println("Fruta: " + fruta));

        // 4. range() - Crea secuencia de enteros
        System.out.println("\n--- range() ---");
        Observable.range(1, 5)
            .subscribe(n -> System.out.println("Rango: " + n));

        // 5. empty() - Observable vacío que completa inmediatamente
        System.out.println("\n--- empty() ---");
        Observable.empty()
            .subscribe(
                item -> System.out.println("Item: " + item),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado (sin elementos)")
            );

        // 6. never() - Observable que nunca emite ni completa
        System.out.println("\n--- never() ---");
        System.out.println("(Observable que nunca emite - no verás output)");
        // Observable.never().subscribe(item -> System.out.println("Item: " + item));

        // 7. error() - Observable que emite error inmediatamente
        System.out.println("\n--- error() ---");
        Observable.error(new RuntimeException("Error intencional"))
            .subscribe(
                item -> System.out.println("Item: " + item),
                error -> System.err.println("Error: " + error.getMessage()),
                () -> System.out.println("Completado")
            );

        // 8. defer() - Crea Observable de forma diferida (lazy)
        System.out.println("\n--- defer() ---");
        System.out.println("defer() crea el Observable en el momento de suscripción");
        Observable<Long> deferredObservable = Observable.defer(() -> 
            Observable.just(System.currentTimeMillis())
        );

        System.out.println("Primera suscripción:");
        deferredObservable.subscribe(time -> System.out.println("  Tiempo: " + time));
        
        Thread.sleep(100);
        
        System.out.println("Segunda suscripción (tiempo diferente):");
        deferredObservable.subscribe(time -> System.out.println("  Tiempo: " + time));

        // 9. interval() - Emite secuencia de números a intervalos
        System.out.println("\n--- interval() ---");
        System.out.println("Emitiendo cada 300ms (primeros 4 elementos):");
        Observable.interval(300, TimeUnit.MILLISECONDS)
            .take(4)
            .blockingSubscribe(n -> System.out.println("  Tick: " + n));

        // 10. timer() - Emite un elemento después de un retraso
        System.out.println("\n--- timer() ---");
        System.out.println("Esperando 500ms...");
        Observable.timer(500, TimeUnit.MILLISECONDS)
            .blockingSubscribe(n -> System.out.println("  ¡Tiempo cumplido!"));

        // 11. fromCallable() - Crea Observable desde operación síncrona
        System.out.println("\n--- fromCallable() ---");
        Observable.fromCallable(() -> {
            System.out.println("  Ejecutando operación costosa...");
            Thread.sleep(100);
            return "Resultado de operación";
        })
        .subscribe(resultado -> System.out.println("  " + resultado));

        // 12. repeat() - Repite la secuencia
        System.out.println("\n--- repeat() ---");
        Observable.just("¡Hola!")
            .repeat(3)
            .subscribe(mensaje -> System.out.println(mensaje));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• just(): Emite elementos específicos");
        System.out.println("• fromArray/fromIterable(): Desde colecciones");
        System.out.println("• range(): Secuencia de números");
        System.out.println("• empty(): Observable vacío");
        System.out.println("• defer(): Creación diferida (lazy)");
        System.out.println("• interval(): Emisión periódica");
        System.out.println("• timer(): Emisión después de delay");
    }
}

