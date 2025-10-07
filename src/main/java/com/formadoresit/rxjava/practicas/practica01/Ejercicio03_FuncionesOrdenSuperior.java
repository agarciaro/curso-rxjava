package com.formadoresit.rxjava.practicas.practica01;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * PRÁCTICA 01 - Ejercicio 1.3: Funciones de Orden Superior
 * 
 * Implementar clase ProcesadorDatos con funciones de orden superior
 */
public class Ejercicio03_FuncionesOrdenSuperior {

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1.3: Funciones de Orden Superior ===\n");

        ProcesadorDatos procesador = new ProcesadorDatos();

        // 1. Transformar nombres a mayúsculas
        System.out.println("--- Transformar nombres a mayúsculas ---");
        List<String> nombres = List.of("juan", "pedro", "maría");
        List<String> nombresMayusculas = procesador.transformar(nombres, String::toUpperCase);
        System.out.println("Original: " + nombres);
        System.out.println("Mayúsculas: " + nombresMayusculas);

        // 2. Filtrar pares y multiplicar por 2
        System.out.println("\n--- Filtrar pares y duplicar ---");
        List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> paresDobles = procesador.filtrarYTransformar(
            numeros,
            n -> n % 2 == 0,
            n -> n * 2
        );
        System.out.println("Original: " + numeros);
        System.out.println("Pares duplicados: " + paresDobles);

        // 3. Sumar todos los elementos
        System.out.println("\n--- Reducir: suma total ---");
        int suma = procesador.reducir(numeros, 0, Integer::sum);
        System.out.println("Lista: " + numeros);
        System.out.println("Suma total: " + suma);

        // Ejemplos adicionales
        System.out.println("\n--- Ejemplos adicionales ---");
        
        // Producto de números
        List<Integer> nums = List.of(2, 3, 4, 5);
        int producto = procesador.reducir(nums, 1, (a, b) -> a * b);
        System.out.println("Producto de " + nums + " = " + producto);

        // Concatenar strings
        List<String> palabras = List.of("Hola", "Mundo", "RxJava");
        String concatenado = procesador.reducir(palabras, "", (a, b) -> a + " " + b);
        System.out.println("Concatenado: " + concatenado.trim());

        // Transformación compleja
        List<String> palabrasLargas = procesador.filtrarYTransformar(
            palabras,
            p -> p.length() > 4,
            p -> p.toLowerCase() + "!"
        );
        System.out.println("Palabras > 4 letras en minúsculas: " + palabrasLargas);
    }
}

/**
 * Clase con funciones de orden superior
 */
class ProcesadorDatos {

    /**
     * Transforma cada elemento de una lista aplicando una función
     * @param <T> Tipo de entrada
     * @param <R> Tipo de salida
     */
    public <T, R> List<R> transformar(List<T> lista, Function<T, R> transformador) {
        return lista.stream()
            .map(transformador)
            .collect(Collectors.toList());
    }

    /**
     * Filtra elementos y luego aplica transformación
     * @param <T> Tipo de elementos
     */
    public <T> List<T> filtrarYTransformar(
            List<T> lista, 
            Predicate<T> filtro, 
            Function<T, T> transformador) {
        
        return lista.stream()
            .filter(filtro)
            .map(transformador)
            .collect(Collectors.toList());
    }

    /**
     * Reduce una lista a un único valor usando un operador binario
     * @param <T> Tipo de elementos
     */
    public <T> T reducir(List<T> lista, T identidad, BinaryOperator<T> operador) {
        return lista.stream()
            .reduce(identidad, operador);
    }
}

