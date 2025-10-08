package com.formadoresit.rxjava.tema5;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * TEMA 5: Combinando Observables
 * Ejemplo 01: merge() - Combina múltiples Observables intercalando emisiones
 * 
 * merge emite elementos de múltiples Observables conforme van llegando
 * No garantiza orden específico, solo intercala las emisiones
 */
public class Ejemplo01_Merge {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 01: merge() ===\n");

        // 1. merge básico
        System.out.println("--- merge() básico ---");
        Observable<String> obs1 = Observable.just("A1", "A2", "A3");
        Observable<String> obs2 = Observable.just("B1", "B2", "B3");
        
        Observable.merge(obs1, obs2)
            .subscribe(item -> System.out.println("Item: " + item));

        // 2. merge con tres Observables
        System.out.println("\n--- merge() con tres fuentes ---");
        Observable<Integer> numeros1 = Observable.just(1, 2, 3);
        Observable<Integer> numeros2 = Observable.just(10, 20, 30);
        Observable<Integer> numeros3 = Observable.just(100, 200, 300);
        
        Observable.merge(numeros1, numeros2, numeros3)
            .subscribe(numero -> System.out.println("Número: " + numero));

        // 3. merge con emisiones temporizadas
        System.out.println("\n--- merge() con timing ---");
        Observable<String> rapido = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> "Rápido-" + i);
            
        Observable<String> lento = Observable.interval(200, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> "Lento-" + i);
        
        Observable.merge(rapido, lento)
            .blockingSubscribe(item -> System.out.println(item));

        // 4. mergeArray
        System.out.println("\n--- mergeArray() ---");
        Observable<String>[] observables = new Observable[] {
            Observable.just("Obs1-A", "Obs1-B"),
            Observable.just("Obs2-A", "Obs2-B"),
            Observable.just("Obs3-A", "Obs3-B")
        };
        
        Observable.mergeArray(observables)
            .subscribe(item -> System.out.println(item));

        // 5. mergeWith() - Versión de instancia
        System.out.println("\n--- mergeWith() ---");
        Observable.just(1, 2, 3)
            .mergeWith(Observable.just(10, 20, 30))
            .mergeWith(Observable.just(100, 200, 300))
            .subscribe(numero -> System.out.println(numero));

        // Ejemplo práctico: Combinar múltiples APIs
        System.out.println("\n--- Ejemplo práctico: APIs ---");
        Observable<String> apiUsuarios = simularLlamadaAPI("Usuarios", 100);
        Observable<String> apiProductos = simularLlamadaAPI("Productos", 150);
        Observable<String> apiPedidos = simularLlamadaAPI("Pedidos", 120);
        
        Observable.merge(apiUsuarios, apiProductos, apiPedidos)
            .blockingSubscribe(resultado -> System.out.println(resultado));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• merge: Combina Observables intercalando emisiones");
        System.out.println("• No garantiza orden entre fuentes");
        System.out.println("• Útil para combinar múltiples flujos asíncronos");
        System.out.println("• Si una fuente emite error, merge termina");
    }

    private static Observable<String> simularLlamadaAPI(String nombre, long delay) {
        return Observable.just("Respuesta de " + nombre)
            .delay(delay, TimeUnit.MILLISECONDS);
    }
}

