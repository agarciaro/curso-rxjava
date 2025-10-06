package com.formadoresit.rxjava.tema1;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * TEMA 1: Fundamentos sobre Programación Funcional y Reactiva
 * Ejemplo 03: Funciones de Orden Superior
 * 
 * Funciones de orden superior son aquellas que:
 * - Toman otras funciones como parámetros
 * - Retornan funciones como resultado
 */
public class Ejemplo03_FuncionesOrdenSuperior {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 03: Funciones de Orden Superior ===\n");

        List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Ejemplo 1: Función que recibe otra función como parámetro
        System.out.println("--- Aplicar operación a lista ---");
        
        Function<Integer, Integer> duplicar = x -> x * 2;
        Function<Integer, Integer> cuadrado = x -> x * x;
        
        System.out.println("Números originales: " + numeros);
        System.out.println("Números duplicados: " + aplicarOperacion(numeros, duplicar));
        System.out.println("Números al cuadrado: " + aplicarOperacion(numeros, cuadrado));

        // Ejemplo 2: Función que retorna otra función
        System.out.println("\n--- Función que retorna función ---");
        
        Function<Integer, Integer> multiplicadorPor3 = crearMultiplicador(3);
        Function<Integer, Integer> multiplicadorPor5 = crearMultiplicador(5);
        
        System.out.println("5 × 3 = " + multiplicadorPor3.apply(5));
        System.out.println("5 × 5 = " + multiplicadorPor5.apply(5));

        // Ejemplo 3: Filtrado con predicados
        System.out.println("\n--- Filtrado con predicados ---");
        
        Predicate<Integer> esPar = x -> x % 2 == 0;
        Predicate<Integer> mayorQue5 = x -> x > 5;
        
        System.out.println("Números pares: " + filtrar(numeros, esPar));
        System.out.println("Números > 5: " + filtrar(numeros, mayorQue5));
        System.out.println("Números pares y > 5: " + filtrar(numeros, esPar.and(mayorQue5)));

        // Ejemplo 4: Composición de funciones
        System.out.println("\n--- Composición de funciones ---");
        
        Function<Integer, Integer> sumar10 = x -> x + 10;
        Function<Integer, Integer> compuesta = duplicar.andThen(sumar10);
        
        System.out.println("(5 × 2) + 10 = " + compuesta.apply(5));
    }

    // Función de orden superior: recibe función como parámetro
    public static List<Integer> aplicarOperacion(List<Integer> lista, Function<Integer, Integer> operacion) {
        return lista.stream()
            .map(operacion)
            .toList();
    }

    // Función de orden superior: retorna función
    public static Function<Integer, Integer> crearMultiplicador(int factor) {
        return x -> x * factor;
    }

    // Función de orden superior: filtrado con predicado
    public static List<Integer> filtrar(List<Integer> lista, Predicate<Integer> predicado) {
        return lista.stream()
            .filter(predicado)
            .toList();
    }
}

