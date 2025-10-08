package com.formadoresit.rxjava.tema6;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 6: Multicast
 * Ejemplo 01: ConnectableObservable
 * 
 * ConnectableObservable permite que múltiples suscriptores compartan
 * la misma ejecución del Observable (multicast en lugar de unicast)
 */
public class Ejemplo01_ConnectableObservable {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 01: ConnectableObservable ===\n");

        // 1. Observable normal (unicast - cada suscriptor recibe su propia secuencia)
        System.out.println("--- Observable normal (unicast) ---");
        Observable<Long> observableNormal = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(5)
            .doOnNext(n -> System.out.println("  Emitiendo: " + n));

        observableNormal.subscribe(n -> System.out.println("Suscriptor 1: " + n));
        Thread.sleep(250);
        observableNormal.subscribe(n -> System.out.println("Suscriptor 2: " + n));
        Thread.sleep(600);

        // 2. ConnectableObservable (multicast - comparten la misma secuencia)
        System.out.println("\n--- ConnectableObservable (multicast) ---");
        ConnectableObservable<Long> connectable = Observable
            .interval(100, TimeUnit.MILLISECONDS)
            .take(5)
            .doOnNext(n -> System.out.println("  Emitiendo: " + n))
            .publish();  // Convierte a ConnectableObservable

        connectable.subscribe(n -> System.out.println("Suscriptor A: " + n));
        connectable.subscribe(n -> System.out.println("Suscriptor B: " + n));

        System.out.println("\nIniciando emisión con connect()...");
        connectable.connect();  // Inicia la emisión

        Thread.sleep(600);

        // 3. autoConnect() - Se conecta automáticamente después de N suscriptores
        System.out.println("\n--- autoConnect() ---");
        Observable<Long> autoConnectable = Observable
            .interval(100, TimeUnit.MILLISECONDS)
            .take(4)
            .doOnNext(n -> System.out.println("  Emitiendo: " + n))
            .publish()
            .autoConnect(2);  // Se conecta automáticamente después de 2 suscriptores

        System.out.println("Suscriptor 1 (esperando...)");
        autoConnectable.subscribe(n -> System.out.println("Sub1: " + n));
        
        Thread.sleep(200);
        
        System.out.println("Suscriptor 2 (activa autoConnect)");
        autoConnectable.subscribe(n -> System.out.println("Sub2: " + n));

        Thread.sleep(500);

        // 4. refCount() - Se conecta/desconecta automáticamente
        System.out.println("\n--- refCount() ---");
        Observable<Long> refCounted = Observable
            .interval(100, TimeUnit.MILLISECONDS)
            .doOnNext(n -> System.out.println("  Emitiendo: " + n))
            .doOnDispose(() -> System.out.println("  Disposed"))
            .publish()
            .refCount();

        System.out.println("Primera suscripción (inicia emisión):");
        var sub1 = refCounted.subscribe(n -> System.out.println("Sub1: " + n));
        
        Thread.sleep(250);
        
        System.out.println("Segunda suscripción:");
        var sub2 = refCounted.subscribe(n -> System.out.println("Sub2: " + n));
        
        Thread.sleep(200);
        
        System.out.println("Cancelando sub1:");
        sub1.dispose();
        
        Thread.sleep(200);
        
        System.out.println("Cancelando sub2 (detiene emisión):");
        sub2.dispose();
        
        Thread.sleep(100);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• ConnectableObservable: Multicast en lugar de unicast");
        System.out.println("• publish(): Convierte Observable a ConnectableObservable");
        System.out.println("• connect(): Inicia manualmente la emisión");
        System.out.println("• autoConnect(n): Conecta automáticamente después de n suscriptores");
        System.out.println("• refCount(): Conecta/desconecta según número de suscriptores");
    }
}

