package com.formadoresit.rxjava.tema2;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 2: Introducción a ReactiveX RxJava 2
 * Ejemplo 03: Disposable
 * 
 * Disposable: Permite cancelar una suscripción y liberar recursos
 */
public class Ejemplo03_Disposable {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 03: Disposable ===\n");

        // Ejemplo 1: Disposable básico
        System.out.println("--- Disposable básico ---");
        Observable<Integer> observable = Observable.range(1, 5);
        
        Disposable disposable = observable.subscribe(
            numero -> System.out.println("Recibido: " + numero),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado")
        );

        System.out.println("¿Está disposed? " + disposable.isDisposed());
        
        // El flujo ya terminó, ahora está disposed
        System.out.println("Después de completar: ¿Está disposed? " + disposable.isDisposed());

        // Ejemplo 2: Cancelar suscripción manualmente
        System.out.println("\n--- Cancelar suscripción ---");
        Observable<Long> intervalObservable = Observable.interval(500, TimeUnit.MILLISECONDS);
        
        Disposable intervalDisposable = intervalObservable.subscribe(
            numero -> System.out.println("Intervalo: " + numero)
        );

        System.out.println("Esperando 2 segundos...");
        Thread.sleep(2000);

        System.out.println("Cancelando suscripción...");
        intervalDisposable.dispose();
        System.out.println("¿Está disposed? " + intervalDisposable.isDisposed());

        Thread.sleep(1000);
        System.out.println("Ya no se emiten más elementos después de dispose()");

        // Ejemplo 3: Múltiples disposables
        System.out.println("\n--- Múltiples suscriptores ---");
        Observable<Integer> fuente = Observable.range(1, 3);
        
        Disposable d1 = fuente.subscribe(n -> System.out.println("Suscriptor 1: " + n));
        Disposable d2 = fuente.subscribe(n -> System.out.println("Suscriptor 2: " + n));

        System.out.println("\nSuscriptor 1 disposed: " + d1.isDisposed());
        System.out.println("Suscriptor 2 disposed: " + d2.isDisposed());

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Disposable: Representa una suscripción activa");
        System.out.println("• dispose(): Cancela la suscripción y libera recursos");
        System.out.println("• isDisposed(): Verifica si ya se canceló la suscripción");
        System.out.println("• Importante: Siempre dispose() de suscripciones de larga duración");
    }
}

