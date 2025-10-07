package com.formadoresit.rxjava.tema3;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * TEMA 3: Observables y Observers
 * Ejemplo 08: Ciclo de Vida del Observer
 * 
 * Entender el ciclo completo de un Observable/Observer
 */
public class Ejemplo08_CicloDeVida {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 08: Ciclo de Vida del Observer ===\n");

        // Ejemplo 1: Ciclo completo exitoso
        System.out.println("--- Ciclo completo exitoso ---");
        Observable<Integer> observableExitoso = Observable.just(1, 2, 3);
        
        observableExitoso.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("1. onSubscribe: Suscripción iniciada");
                System.out.println("   isDisposed: " + d.isDisposed());
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("2. onNext: Recibido elemento: " + item);
            }

            @Override
            public void onError(Throwable e) {
                System.err.println("X. onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("3. onComplete: Flujo completado exitosamente");
            }
        });

        // Ejemplo 2: Ciclo con error
        System.out.println("\n--- Ciclo con error ---");
        Observable<Integer> observableConError = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onError(new RuntimeException("Error intencional"));
            emitter.onNext(3); // No se ejecutará
        });

        observableConError.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("1. onSubscribe: Iniciado");
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("2. onNext: " + item);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("3. onError: " + e.getMessage());
                System.out.println("   (El flujo termina aquí)");
            }

            @Override
            public void onComplete() {
                System.out.println("X. onComplete: No se llama si hay error");
            }
        });

        // Ejemplo 3: Ciclo con cancelación
        System.out.println("\n--- Ciclo con cancelación (dispose) ---");
        Observable<Long> observableInfinito = Observable.interval(
            100, 
            java.util.concurrent.TimeUnit.MILLISECONDS
        );

        final Disposable[] disposableHolder = new Disposable[1];
        
        observableInfinito.subscribe(new Observer<Long>() {
            private Disposable subscription;
            private int contador = 0;

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("1. onSubscribe: Iniciado");
                this.subscription = d;
                disposableHolder[0] = d;  // Guardar referencia
            }

            @Override
            public void onNext(Long item) {
                contador++;
                System.out.println("2. onNext: Tick " + item);
                
                if (contador >= 3) {
                    System.out.println("   Cancelando suscripción...");
                    subscription.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                System.err.println("X. onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("X. onComplete: (interval nunca completa naturalmente)");
            }
        });

        // Esperar a que se cancele
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (disposableHolder[0] != null) {
            System.out.println("   isDisposed: " + disposableHolder[0].isDisposed());
        }

        // Ejemplo 4: Múltiples emisiones con logging
        System.out.println("\n--- Ciclo con operadores y logging ---");
        Observable.just("A", "B", "C", "D")
            .doOnSubscribe(d -> System.out.println("→ doOnSubscribe"))
            .doOnNext(item -> System.out.println("→ doOnNext: " + item))
            .map(String::toLowerCase)
            .doAfterNext(item -> System.out.println("→ doAfterNext: " + item))
            .doOnComplete(() -> System.out.println("→ doOnComplete"))
            .doFinally(() -> System.out.println("→ doFinally (siempre se ejecuta)"))
            .subscribe(
                item -> System.out.println("  Suscriptor recibe: " + item),
                error -> System.err.println("  Error: " + error),
                () -> System.out.println("  ¡Completado!")
            );

        // Ejemplo 5: Ciclo con dispose explícito
        System.out.println("\n--- Dispose explícito ---");
        Observable<Integer> observable = Observable.range(1, 10);
        
        Disposable d = observable
            .doOnDispose(() -> System.out.println("→ doOnDispose: Limpiando recursos"))
            .subscribe(
                item -> System.out.println("Item: " + item),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );

        System.out.println("Estado después de completar: isDisposed = " + d.isDisposed());

        System.out.println("\n=== RESUMEN DEL CICLO DE VIDA ===");
        System.out.println("1. onSubscribe(): Se llama al iniciar la suscripción");
        System.out.println("2. onNext(): Se llama 0..N veces (por cada elemento)");
        System.out.println("3a. onComplete(): Se llama si el flujo termina exitosamente");
        System.out.println("3b. onError(): Se llama si ocurre un error");
        System.out.println("\nNOTA: onComplete y onError son mutuamente excluyentes");
        System.out.println("      Después de onComplete o onError, no hay más emisiones");
    }
}

