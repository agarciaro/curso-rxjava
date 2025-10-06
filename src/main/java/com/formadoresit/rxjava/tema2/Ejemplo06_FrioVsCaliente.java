package com.formadoresit.rxjava.tema2;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

/**
 * TEMA 2: Introducción a ReactiveX RxJava 2
 * Ejemplo 06: Observables Fríos vs Calientes
 * 
 * Observable Frío: Emite datos solo cuando alguien se suscribe (unicast)
 * Observable Caliente: Emite datos independientemente de suscriptores (multicast)
 */
public class Ejemplo06_FrioVsCaliente {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 06: Observables Fríos vs Calientes ===\n");

        // Observable FRÍO: Cada suscriptor recibe su propia secuencia
        System.out.println("--- Observable FRÍO ---");
        Observable<Integer> observableFrio = Observable.just(1, 2, 3, 4, 5);

        System.out.println("Suscriptor 1:");
        observableFrio.subscribe(n -> System.out.println("  S1: " + n));

        System.out.println("\nSuscriptor 2:");
        observableFrio.subscribe(n -> System.out.println("  S2: " + n));

        System.out.println("\nCada suscriptor recibe la secuencia completa desde el inicio");

        // Observable CALIENTE: Todos los suscriptores comparten la misma emisión
        System.out.println("\n--- Observable CALIENTE ---");
        
        // Convertir Observable frío en caliente usando publish()
        ConnectableObservable<Long> observableCaliente = Observable
            .interval(500, java.util.concurrent.TimeUnit.MILLISECONDS)
            .take(5)
            .publish();

        // Suscriptor 1
        observableCaliente.subscribe(n -> System.out.println("  S1: " + n));

        // Iniciar emisión
        observableCaliente.connect();
        System.out.println("Observable caliente iniciado...");

        // Esperar 1 segundo y agregar segundo suscriptor
        Thread.sleep(1500);
        System.out.println("\nNuevo suscriptor (se une tarde):");
        observableCaliente.subscribe(n -> System.out.println("  S2: " + n));

        // Esperar a que termine
        Thread.sleep(3000);

        System.out.println("\nEl segundo suscriptor perdió las emisiones iniciales");

        // Ejemplo práctico: Simulación de sensor
        System.out.println("\n--- Ejemplo práctico: Sensor de temperatura ---");
        
        // Observable frío: Cada suscriptor recibe su propia "lectura"
        Observable<Double> sensorFrio = Observable.defer(() -> 
            Observable.just(Math.random() * 100)
        );

        System.out.println("Observable FRÍO (cada suscriptor ve valor diferente):");
        sensorFrio.subscribe(temp -> System.out.println("  Monitor 1: " + String.format("%.2f", temp) + "°C"));
        sensorFrio.subscribe(temp -> System.out.println("  Monitor 2: " + String.format("%.2f", temp) + "°C"));

        // Observable caliente: Todos ven la misma "lectura"
        double temperaturaActual = Math.random() * 100;
        Observable<Double> sensorCaliente = Observable.just(temperaturaActual);

        System.out.println("\nObservable CALIENTE (todos ven el mismo valor):");
        sensorCaliente.subscribe(temp -> System.out.println("  Monitor 1: " + String.format("%.2f", temp) + "°C"));
        sensorCaliente.subscribe(temp -> System.out.println("  Monitor 2: " + String.format("%.2f", temp) + "°C"));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Observable FRÍO: Emite desde el inicio para cada suscriptor");
        System.out.println("• Observable CALIENTE: Comparte la misma emisión entre suscriptores");
        System.out.println("• publish(): Convierte Observable frío en caliente");
        System.out.println("• connect(): Inicia la emisión en ConnectableObservable");
        System.out.println("• Los Observables son fríos por defecto");
    }
}

