package com.formadoresit.rxjava.tema2;

import io.reactivex.Observable;

/**
 * TEMA 2: Introducción a ReactiveX RxJava 2
 * Ejemplo 04: Operadores Básicos
 * 
 * Operadores: Transforman, filtran y combinan Observables
 */
public class Ejemplo04_OperadoresBasicos {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 04: Operadores Básicos ===\n");

        // Operador map(): Transforma cada elemento
        System.out.println("--- Operador map() ---");
        Observable.just(1, 2, 3, 4, 5)
            .map(numero -> numero * 2)
            .subscribe(resultado -> System.out.println("Número duplicado: " + resultado));

        // Operador filter(): Filtra elementos
        System.out.println("\n--- Operador filter() ---");
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .filter(numero -> numero % 2 == 0)
            .subscribe(par -> System.out.println("Número par: " + par));

        // Operador take(): Toma los primeros N elementos
        System.out.println("\n--- Operador take() ---");
        Observable.range(1, 100)
            .take(5)
            .subscribe(numero -> System.out.println("Primeros 5: " + numero));

        // Operador skip(): Salta los primeros N elementos
        System.out.println("\n--- Operador skip() ---");
        Observable.range(1, 10)
            .skip(5)
            .subscribe(numero -> System.out.println("Después de saltar 5: " + numero));

        // Combinación de operadores
        System.out.println("\n--- Combinación de operadores ---");
        Observable.range(1, 20)
            .filter(n -> n % 2 == 0)  // Solo pares
            .map(n -> n * n)            // Elevar al cuadrado
            .take(5)                     // Primeros 5
            .subscribe(resultado -> System.out.println("Resultado: " + resultado));

        // distinct(): Elimina duplicados
        System.out.println("\n--- Operador distinct() ---");
        Observable.just(1, 2, 2, 3, 3, 3, 4, 5, 5)
            .distinct()
            .subscribe(numero -> System.out.println("Único: " + numero));

        // defaultIfEmpty(): Emite valor por defecto si está vacío
        System.out.println("\n--- Operador defaultIfEmpty() ---");
        Observable.<String>empty()
            .defaultIfEmpty("Valor por defecto")
            .subscribe(valor -> System.out.println("Valor: " + valor));

        // Ejemplo práctico: Procesamiento de nombres
        System.out.println("\n--- Ejemplo práctico ---");
        Observable.just("juan", "PEDRO", "María", "ANA", "carlos")
            .map(String::toUpperCase)
            .filter(nombre -> nombre.length() > 4)
            .sorted()
            .subscribe(nombre -> System.out.println("Nombre procesado: " + nombre));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• map(): Transforma cada elemento");
        System.out.println("• filter(): Filtra elementos según condición");
        System.out.println("• take(): Toma los primeros N elementos");
        System.out.println("• skip(): Salta los primeros N elementos");
        System.out.println("• distinct(): Elimina duplicados");
        System.out.println("• Los operadores se pueden encadenar");
    }
}

