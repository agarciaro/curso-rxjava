package com.formadoresit.rxjava.tema5;

import io.reactivex.Observable;

/**
 * TEMA 5: Combinando Observables
 * Ejemplo 02: concat() - Combina Observables secuencialmente
 * 
 * concat espera a que el Observable anterior complete antes de suscribirse al siguiente
 * Mantiene el orden estricto
 */
public class Ejemplo02_Concat {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 02: concat() ===\n");

        // 1. concat básico
        System.out.println("--- concat() básico ---");
        Observable<String> primero = Observable.just("A1", "A2", "A3");
        Observable<String> segundo = Observable.just("B1", "B2", "B3");
        
        Observable.concat(primero, segundo)
            .subscribe(item -> System.out.println(item));

        // 2. concat con múltiples fuentes
        System.out.println("\n--- concat() con múltiples fuentes ---");
        Observable.concat(
            Observable.just(1, 2, 3),
            Observable.just(10, 20, 30),
            Observable.just(100, 200, 300)
        )
        .subscribe(numero -> System.out.println(numero));

        // 3. concatWith() - Versión de instancia
        System.out.println("\n--- concatWith() ---");
        Observable.just("Inicio")
            .concatWith(Observable.just("Medio"))
            .concatWith(Observable.just("Fin"))
            .subscribe(item -> System.out.println(item));

        // 4. startWith() - Añade elementos al inicio
        System.out.println("\n--- startWith() ---");
        Observable.just(2, 3, 4, 5)
            .startWith(1)
            .subscribe(numero -> System.out.println(numero));

        Observable.just("B", "C", "D")
            .startWith(Observable.just("A1", "A2"))
            .subscribe(letra -> System.out.println(letra));

        // Ejemplo práctico: Pipeline secuencial
        System.out.println("\n--- Ejemplo práctico: Pipeline ---");
        Observable.concat(
            validarDatos(),
            procesarDatos(),
            guardarDatos()
        )
        .subscribe(
            paso -> System.out.println("✓ " + paso),
            error -> System.err.println("✗ Error: " + error),
            () -> System.out.println("✓ Pipeline completado")
        );

        System.out.println("\n=== DIFERENCIAS: concat vs merge ===");
        System.out.println("• concat: Secuencial, espera a que complete cada uno");
        System.out.println("• merge: Paralelo, intercala emisiones");
        System.out.println("• concat mantiene orden estricto");
        System.out.println("• merge es más eficiente para operaciones paralelas");
    }

    private static Observable<String> validarDatos() {
        return Observable.just("Paso 1: Validación de datos");
    }

    private static Observable<String> procesarDatos() {
        return Observable.just("Paso 2: Procesamiento de datos");
    }

    private static Observable<String> guardarDatos() {
        return Observable.just("Paso 3: Guardado en base de datos");
    }
}

