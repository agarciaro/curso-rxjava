package com.formadoresit.rxjava.practicas.practica01;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PRÁCTICA 01 - Ejercicio 1.1: Funciones Puras
 * 
 * Implementar funciones puras para:
 * 1. Calcular factorial
 * 2. Filtrar números primos
 * 3. Calcular promedio
 */
public class Ejercicio01_FuncionesPuras {

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1.1: Funciones Puras ===\n");

        // 1. Factorial
        System.out.println("--- Factorial ---");
        System.out.println("factorial(5) = " + factorial(5));
        System.out.println("factorial(0) = " + factorial(0));
        System.out.println("factorial(10) = " + factorial(10));

        // 2. Números primos
        System.out.println("\n--- Números Primos ---");
        List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        List<Integer> primos = filtrarPrimos(numeros);
        System.out.println("primos(" + numeros + ") = " + primos);

        // 3. Promedio
        System.out.println("\n--- Promedio ---");
        List<Integer> valores = List.of(10, 20, 30, 40);
        System.out.println("promedio(" + valores + ") = " + calcularPromedio(valores));

        List<Integer> otrosValores = List.of(5, 15, 25, 35, 45);
        System.out.println("promedio(" + otrosValores + ") = " + calcularPromedio(otrosValores));
    }

    /**
     * Calcula el factorial de un número usando recursión
     * Función pura: siempre retorna el mismo resultado para el mismo input
     */
    public static long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("El factorial no está definido para números negativos");
        }
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }

    /**
     * Filtra números primos de una lista
     * Función pura: no modifica la lista original, retorna nueva lista
     */
    public static List<Integer> filtrarPrimos(List<Integer> numeros) {
        return numeros.stream()
            .filter(Ejercicio01_FuncionesPuras::esPrimo)
            .collect(Collectors.toList());
    }

    /**
     * Verifica si un número es primo
     * Función pura auxiliar
     */
    private static boolean esPrimo(int numero) {
        if (numero < 2) {
            return false;
        }
        if (numero == 2) {
            return true;
        }
        if (numero % 2 == 0) {
            return false;
        }
        // Solo verificar hasta la raíz cuadrada
        for (int i = 3; i * i <= numero; i += 2) {
            if (numero % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calcula el promedio de una lista sin usar variables mutables
     * Función pura: usa reduce para acumular y luego divide
     */
    public static double calcularPromedio(List<Integer> numeros) {
        if (numeros.isEmpty()) {
            throw new IllegalArgumentException("No se puede calcular el promedio de una lista vacía");
        }
        
        int suma = numeros.stream()
            .reduce(0, Integer::sum);
        
        return (double) suma / numeros.size();
    }
}

