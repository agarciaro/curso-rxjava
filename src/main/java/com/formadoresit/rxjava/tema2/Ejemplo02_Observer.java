package com.formadoresit.rxjava.tema2;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * TEMA 2: Introducción a ReactiveX RxJava 2
 * Ejemplo 02: Observer
 * 
 * Observer: Interfaz que define cómo reaccionar a los elementos emitidos
 * Tiene 4 métodos: onSubscribe, onNext, onError, onComplete
 */
public class Ejemplo02_Observer {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 02: Observer ===\n");

        Observable<String> observable = Observable.just("Elemento 1", "Elemento 2", "Elemento 3");

        // Forma 1: Observer completo implementando la interfaz
        System.out.println("--- Observer completo ---");
        
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe: Suscripción iniciada");
            }

            @Override
            public void onNext(String elemento) {
                System.out.println("onNext: " + elemento);
            }

            @Override
            public void onError(Throwable e) {
                System.err.println("onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete: Flujo completado");
            }
        };

        observable.subscribe(observer);

        // Forma 2: Subscribe simplificado (solo onNext)
        System.out.println("\n--- Subscribe simplificado (solo onNext) ---");
        observable.subscribe(
            elemento -> System.out.println("Recibido: " + elemento)
        );

        // Forma 3: Subscribe con onNext y onError
        System.out.println("\n--- Subscribe con onNext y onError ---");
        observable.subscribe(
            elemento -> System.out.println("Recibido: " + elemento),
            error -> System.err.println("Error: " + error.getMessage())
        );

        // Forma 4: Subscribe completo (onNext, onError, onComplete)
        System.out.println("\n--- Subscribe completo ---");
        observable.subscribe(
            elemento -> System.out.println("Recibido: " + elemento),
            error -> System.err.println("Error: " + error.getMessage()),
            () -> System.out.println("¡Completado!")
        );

        // Ejemplo con error
        System.out.println("\n--- Observable con error ---");
        Observable<Integer> observableConError = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onError(new RuntimeException("Error intencional"));
            emitter.onNext(3); // Este no se emitirá
        });

        observableConError.subscribe(
            numero -> System.out.println("Número: " + numero),
            error -> System.err.println("Capturado error: " + error.getMessage()),
            () -> System.out.println("Completado")
        );

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• onSubscribe: Se llama cuando se inicia la suscripción");
        System.out.println("• onNext: Se llama por cada elemento emitido");
        System.out.println("• onError: Se llama si ocurre un error (termina el flujo)");
        System.out.println("• onComplete: Se llama cuando el flujo termina exitosamente");
    }
}

