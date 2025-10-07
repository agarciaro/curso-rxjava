package com.formadoresit.rxjava.practicas.practica02;

import io.reactivex.Observable;

/**
 * PRÁCTICA 02 - Ejercicio 2.2: Operadores Básicos
 * 
 * Aplicar múltiples operadores en cadena
 */
public class Ejercicio02_OperadoresBasicos {

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 2.2: Operadores Básicos ===\n");

        // 1. Números del 1-20: filtrar múltiplos de 3, elevar al cuadrado, tomar primeros 5
        System.out.println("--- Procesamiento de números ---");
        System.out.println("Objetivo: Múltiplos de 3 → Cuadrado → Primeros 5\n");
        
        Observable.range(1, 20)
            .doOnNext(n -> System.out.println("  Emitido: " + n))
            .filter(n -> n % 3 == 0)
            .doOnNext(n -> System.out.println("  Filtrado (múltiplo de 3): " + n))
            .map(n -> n * n)
            .doOnNext(n -> System.out.println("  Transformado (cuadrado): " + n))
            .take(5)
            .subscribe(
                resultado -> System.out.println("✓ Resultado final: " + resultado),
                error -> System.err.println("✗ Error: " + error),
                () -> System.out.println("\n¡Completado!")
            );

        // 2. Procesamiento de nombres
        System.out.println("\n\n--- Procesamiento de nombres ---");
        System.out.println("Pipeline: Mayúsculas → Filtrar(> 4 letras) → Prefijo → Ordenar\n");
        
        Observable.just("juan", "ana", "pedro", "maría", "carlos", "luis", "elena")
            .doOnNext(nombre -> System.out.println("  Original: " + nombre))
            .map(String::toUpperCase)
            .doOnNext(nombre -> System.out.println("  Mayúsculas: " + nombre))
            .filter(nombre -> nombre.length() > 4)
            .doOnNext(nombre -> System.out.println("  Filtrado (> 4): " + nombre))
            .map(nombre -> "Sr./Sra. " + nombre)
            .doOnNext(nombre -> System.out.println("  Con prefijo: " + nombre))
            .sorted()
            .subscribe(
                resultado -> System.out.println("✓ Resultado: " + resultado),
                error -> System.err.println("✗ Error: " + error),
                () -> System.out.println("\n¡Procesamiento completado!")
            );

        // 3. Ejemplo adicional: Pipeline de validación
        System.out.println("\n\n--- Pipeline de validación de datos ---");
        
        Observable.just(5, -3, 12, 0, 8, -1, 15, 20)
            .filter(n -> n >= 0)
            .map(n -> n * 2)
            .filter(n -> n <= 30)
            .distinct()
            .sorted()
            .subscribe(
                valor -> System.out.println("Valor válido procesado: " + valor),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Validación completada")
            );
    }
}

