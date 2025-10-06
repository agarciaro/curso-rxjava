package com.formadoresit.rxjava.tema1;

import java.util.ArrayList;
import java.util.List;

/**
 * TEMA 1: Fundamentos sobre Programación Funcional y Reactiva
 * Ejemplo 04: Programación Declarativa vs Imperativa
 * 
 * Programación Imperativa: Describe CÓMO hacer algo (paso a paso)
 * Programación Declarativa: Describe QUÉ queremos lograr
 */
public class Ejemplo04_ProgramacionDeclarativa {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 04: Programación Declarativa vs Imperativa ===\n");

        List<String> nombres = List.of("Ana", "Pedro", "María", "Juan", "Lucía", "Carlos");

        // ESTILO IMPERATIVO: describimos CÓMO hacerlo paso a paso
        System.out.println("--- Estilo IMPERATIVO ---");
        System.out.println("Objetivo: Filtrar nombres que empiezan con 'M', convertir a mayúsculas\n");
        
        List<String> resultadoImperativo = new ArrayList<>();
        for (String nombre : nombres) {
            if (nombre.startsWith("M")) {
                resultadoImperativo.add(nombre.toUpperCase());
            }
        }
        System.out.println("Resultado: " + resultadoImperativo);
        System.out.println("Código: bucle for, if, creación de lista auxiliar");

        // ESTILO DECLARATIVO: describimos QUÉ queremos
        System.out.println("\n--- Estilo DECLARATIVO ---");
        System.out.println("Objetivo: Filtrar nombres que empiezan con 'M', convertir a mayúsculas\n");
        
        List<String> resultadoDeclarativo = nombres.stream()
            .filter(nombre -> nombre.startsWith("M"))
            .map(String::toUpperCase)
            .toList();
        
        System.out.println("Resultado: " + resultadoDeclarativo);
        System.out.println("Código: filter y map expresan directamente la intención");

        // Ejemplo 2: Suma de números pares
        System.out.println("\n\n=== Ejemplo: Suma de números pares ===");
        List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Imperativo
        System.out.println("\n--- Imperativo ---");
        int sumaImperativa = 0;
        for (int numero : numeros) {
            if (numero % 2 == 0) {
                sumaImperativa += numero;
            }
        }
        System.out.println("Suma de pares: " + sumaImperativa);

        // Declarativo
        System.out.println("\n--- Declarativo ---");
        int sumaDeclarativa = numeros.stream()
            .filter(n -> n % 2 == 0)
            .mapToInt(Integer::intValue)
            .sum();
        System.out.println("Suma de pares: " + sumaDeclarativa);

        // Ejemplo 3: Transformación compleja
        System.out.println("\n\n=== Ejemplo: Transformación compleja ===");
        System.out.println("Objetivo: Nombres que empiezan con vocal, longitud > 3, en mayúsculas, ordenados");

        // Declarativo (mucho más legible para operaciones complejas)
        List<String> resultado = nombres.stream()
            .filter(n -> "AEIOUaeiou".indexOf(n.charAt(0)) != -1)
            .filter(n -> n.length() > 3)
            .map(String::toUpperCase)
            .sorted()
            .toList();

        System.out.println("\nResultado: " + resultado);
        System.out.println("\nVentajas del estilo declarativo:");
        System.out.println("✓ Más legible y expresivo");
        System.out.println("✓ Menos propenso a errores");
        System.out.println("✓ Fácil de modificar y mantener");
        System.out.println("✓ Preparado para paralelización");
    }
}

