package com.formadoresit.rxjava.tema2;

import io.reactivex.Observable;

/**
 * TEMA 2: Introducción a ReactiveX RxJava 2
 * Ejemplo 01: Primer Observable
 * 
 * Observable: Es la fuente de datos que emite elementos a los observadores
 */
public class Ejemplo01_PrimerObservable {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 01: Primer Observable ===\n");

        // Crear un Observable simple que emite algunos números
        System.out.println("--- Observable básico ---");
        Observable<Integer> observable = Observable.just(1, 2, 3, 4, 5);

        // Suscribirse al Observable para recibir los elementos
        observable.subscribe(
            numero -> System.out.println("Recibido: " + numero)
        );

        System.out.println("\n--- Observable con String ---");
        Observable<String> nombres = Observable.just("Ana", "Pedro", "María");
        
        nombres.subscribe(
            nombre -> System.out.println("Hola, " + nombre + "!")
        );

        System.out.println("\n--- Observable vacío ---");
        Observable<String> vacio = Observable.empty();
        
        vacio.subscribe(
            item -> System.out.println("Item: " + item),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado (sin emitir elementos)")
        );

        System.out.println("\n--- Observable desde array ---");
        String[] frutas = {"Manzana", "Naranja", "Plátano", "Uva"};
        Observable<String> observableFrutas = Observable.fromArray(frutas);
        
        observableFrutas.subscribe(
            fruta -> System.out.println("Fruta: " + fruta)
        );

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Observable: Fuente de datos que emite elementos");
        System.out.println("• just(): Crea Observable que emite elementos específicos");
        System.out.println("• empty(): Crea Observable que no emite elementos");
        System.out.println("• fromArray(): Crea Observable desde un array");
        System.out.println("• subscribe(): Método para recibir los elementos emitidos");
    }
}

