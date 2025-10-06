package com.formadoresit.rxjava.tema1;

import java.util.function.Function;

/**
 * TEMA 1: Fundamentos sobre Programación Funcional y Reactiva
 * Ejemplo 01: Funciones Puras
 * 
 * Una función pura:
 * - Siempre retorna el mismo resultado para los mismos argumentos
 * - No tiene efectos secundarios (no modifica estado externo)
 */
public class Ejemplo01_FuncionesPuras {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 01: Funciones Puras ===\n");

        // Función pura: siempre da el mismo resultado
        Function<Integer, Integer> funcionPura = x -> x * 2;
        
        System.out.println("Función pura - multiplicar por 2:");
        System.out.println("Input: 5 -> Output: " + funcionPura.apply(5));
        System.out.println("Input: 5 -> Output: " + funcionPura.apply(5)); // Mismo resultado
        System.out.println("Input: 10 -> Output: " + funcionPura.apply(10));

        // Función compuesta (composición de funciones puras)
        Function<Integer, Integer> sumarTres = x -> x + 3;
        Function<Integer, Integer> composicion = funcionPura.andThen(sumarTres);
        
        System.out.println("\nComposición de funciones:");
        System.out.println("(x * 2) + 3 con x=5: " + composicion.apply(5)); // (5*2)+3 = 13

        // Ejemplo de función NO pura (con efecto secundario)
        System.out.println("\n--- Comparación con función NO pura ---");
        Contador contador = new Contador();
        System.out.println("Llamada 1: " + contador.incrementar(5)); // 6
        System.out.println("Llamada 2: " + contador.incrementar(5)); // 7 (¡diferente resultado!)
    }

    // Clase para demostrar función NO pura
    static class Contador {
        private int conteo = 0;

        public int incrementar(int valor) {
            conteo++; // Efecto secundario: modifica estado
            return valor + conteo;
        }
    }
}

