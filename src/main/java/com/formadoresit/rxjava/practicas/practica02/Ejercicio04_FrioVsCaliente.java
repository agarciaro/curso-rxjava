package com.formadoresit.rxjava.practicas.practica02;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

/**
 * PRÁCTICA 02 - Ejercicio 2.4: Observables Fríos vs Calientes
 * 
 * Demostrar la diferencia entre Observables fríos y calientes
 */
public class Ejercicio04_FrioVsCaliente {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 2.4: Observables Fríos vs Calientes ===\n");

        // 1. Observable FRÍO - Cada suscriptor recibe su propia secuencia
        System.out.println("--- Observable FRÍO (defer) ---");
        System.out.println("Genera un número aleatorio por cada suscriptor\n");
        
        Observable<Integer> observableFrio = Observable.defer(() -> {
            int numeroAleatorio = (int) (Math.random() * 100);
            System.out.println("  Generando número aleatorio: " + numeroAleatorio);
            return Observable.just(numeroAleatorio);
        });

        System.out.println("Suscriptor 1:");
        observableFrio.subscribe(n -> System.out.println("  Observador 1 recibe: " + n));

        Thread.sleep(100);

        System.out.println("\nSuscriptor 2:");
        observableFrio.subscribe(n -> System.out.println("  Observador 2 recibe: " + n));

        System.out.println("\n→ Conclusión: Cada suscriptor recibió un número diferente\n");

        // 2. Observable CALIENTE - Todos comparten la misma emisión
        System.out.println("\n--- Observable CALIENTE (publish + connect) ---");
        System.out.println("Todos los suscriptores comparten el mismo número\n");

        // Crear observable y convertirlo a caliente
        int numeroCompartido = (int) (Math.random() * 100);
        System.out.println("Generando número compartido: " + numeroCompartido);
        
        ConnectableObservable<Integer> observableCaliente = Observable
            .just(numeroCompartido)
            .publish();

        // Suscribir dos observadores ANTES de connect
        System.out.println("\nSuscribiendo observador 1...");
        observableCaliente.subscribe(n -> System.out.println("  Observador 1 recibe: " + n));

        System.out.println("Suscribiendo observador 2...");
        observableCaliente.subscribe(n -> System.out.println("  Observador 2 recibe: " + n));

        // Iniciar emisión
        System.out.println("\nLlamando a connect() para iniciar emisión...");
        observableCaliente.connect();

        System.out.println("\n→ Conclusión: Ambos suscriptores recibieron el mismo número\n");

        // 3. Demostración con interval (más visual)
        System.out.println("\n--- Ejemplo con interval ---");
        
        ConnectableObservable<Long> intervalCaliente = Observable
            .interval(100, java.util.concurrent.TimeUnit.MILLISECONDS)
            .take(5)
            .doOnNext(n -> System.out.println("  [Emisión] " + n))
            .publish();

        // Primer suscriptor
        intervalCaliente.subscribe(n -> System.out.println("    Suscriptor A: " + n));

        // Segundo suscriptor
        intervalCaliente.subscribe(n -> System.out.println("    Suscriptor B: " + n));

        // Iniciar
        System.out.println("Iniciando interval caliente...\n");
        intervalCaliente.connect();

        Thread.sleep(600);

        System.out.println("\n→ Ambos suscriptores recibieron las mismas emisiones al mismo tiempo");
    }
}

