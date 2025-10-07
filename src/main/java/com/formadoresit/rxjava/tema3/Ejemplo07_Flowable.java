package com.formadoresit.rxjava.tema3;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * TEMA 3: Observables y Observers
 * Ejemplo 07: Flowable (introducción)
 * 
 * Flowable: Similar a Observable pero con soporte de backpressure
 * Usa el estándar Reactive Streams
 */
public class Ejemplo07_Flowable {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 07: Flowable (introducción) ===\n");

        // 1. Flowable básico
        System.out.println("--- Flowable básico ---");
        Flowable<Integer> flowable = Flowable.just(1, 2, 3, 4, 5);
        flowable.subscribe(numero -> System.out.println("Número: " + numero));

        // 2. Flowable desde range
        System.out.println("\n--- Flowable.range() ---");
        Flowable.range(1, 10)
            .subscribe(n -> System.out.println("Item: " + n));

        // 3. Flowable con operadores
        System.out.println("\n--- Flowable con operadores ---");
        Flowable.range(1, 20)
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .subscribe(resultado -> System.out.println("Resultado: " + resultado));

        // 4. Diferencia con Observable (Backpressure)
        System.out.println("\n--- Demostración de Backpressure ---");
        System.out.println("Flowable emite muchos elementos rápidamente...");
        
        Flowable.range(1, 1000)
            .observeOn(Schedulers.io())
            .subscribe(new Subscriber<Integer>() {
                private Subscription subscription;
                private int count = 0;

                @Override
                public void onSubscribe(Subscription s) {
                    System.out.println("  Suscrito");
                    this.subscription = s;
                    subscription.request(5); // Solicitar solo 5 elementos inicialmente
                }

                @Override
                public void onNext(Integer item) {
                    count++;
                    System.out.println("  Procesando: " + item);
                    
                    if (count % 5 == 0 && count < 20) {
                        subscription.request(5); // Solicitar más elementos
                    }
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("  Error: " + t);
                }

                @Override
                public void onComplete() {
                    System.out.println("  Completado");
                }
            });

        Thread.sleep(100);

        // 5. Flowable.interval
        System.out.println("\n--- Flowable.interval() ---");
        Flowable.interval(100, java.util.concurrent.TimeUnit.MILLISECONDS)
            .take(5)
            .blockingSubscribe(n -> System.out.println("Tick: " + n));

        // 6. Flowable desde Iterable
        System.out.println("\n--- Flowable.fromIterable() ---");
        java.util.List<String> nombres = java.util.Arrays.asList("Ana", "Pedro", "María");
        Flowable.fromIterable(nombres)
            .subscribe(nombre -> System.out.println("Nombre: " + nombre));

        // 7. Flowable.create()
        System.out.println("\n--- Flowable.create() ---");
        Flowable<String> flowableCreate = Flowable.create(emitter -> {
            emitter.onNext("Elemento 1");
            emitter.onNext("Elemento 2");
            emitter.onNext("Elemento 3");
            emitter.onComplete();
        }, io.reactivex.BackpressureStrategy.BUFFER);

        flowableCreate.subscribe(
            item -> System.out.println("Recibido: " + item),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado")
        );

        // 8. Convertir Observable a Flowable
        System.out.println("\n--- Observable a Flowable ---");
        io.reactivex.Observable.just("A", "B", "C")
            .toFlowable(io.reactivex.BackpressureStrategy.BUFFER)
            .subscribe(letra -> System.out.println("Letra: " + letra));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Flowable: Soporte de backpressure (control de flujo)");
        System.out.println("• Usa el estándar Reactive Streams");
        System.out.println("• Subscriber puede controlar cuántos elementos recibe");
        System.out.println("• Preferir Flowable para flujos grandes (> 1000 elementos)");
        System.out.println("• BackpressureStrategy: BUFFER, DROP, LATEST, ERROR, MISSING");
    }
}

