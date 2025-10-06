package com.formadoresit.rxjava.tema1;

import java.util.ArrayList;
import java.util.List;

/**
 * TEMA 1: Fundamentos sobre Programación Funcional y Reactiva
 * Ejemplo 02: Inmutabilidad
 * 
 * La inmutabilidad es un concepto clave en programación funcional:
 * - Los objetos no cambian después de su creación
 * - Se crean nuevas instancias en lugar de modificar existentes
 * - Facilita la programación concurrente y reduce errores
 */
public class Ejemplo02_Inmutabilidad {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 02: Inmutabilidad ===\n");

        // Ejemplo 1: Lista mutable vs inmutable
        System.out.println("--- Listas Mutables vs Inmutables ---");
        
        List<String> listaMutable = new ArrayList<>();
        listaMutable.add("Java");
        listaMutable.add("Python");
        System.out.println("Lista mutable original: " + listaMutable);
        
        listaMutable.add("JavaScript"); // Modifica la lista existente
        System.out.println("Lista mutable modificada: " + listaMutable);

        // Lista inmutable
        List<String> listaInmutable = List.of("Java", "Python");
        System.out.println("\nLista inmutable: " + listaInmutable);
        
        try {
            listaInmutable.add("JavaScript"); // Esto lanzará excepción
        } catch (UnsupportedOperationException e) {
            System.out.println("Error: No se puede modificar lista inmutable");
        }

        // Ejemplo 2: Clase inmutable
        System.out.println("\n--- Clase Inmutable: Persona ---");
        
        Persona persona1 = new Persona("Juan", 30);
        System.out.println(persona1);
        
        // Para "modificar", creamos una nueva instancia
        Persona persona2 = persona1.conNuevaEdad(31);
        System.out.println("Persona original: " + persona1);
        System.out.println("Persona modificada (nueva instancia): " + persona2);

        // Ejemplo 3: Trabajando con streams (inmutable por naturaleza)
        System.out.println("\n--- Streams Inmutables ---");
        
        List<Integer> numeros = List.of(1, 2, 3, 4, 5);
        System.out.println("Lista original: " + numeros);
        
        List<Integer> numerosDobles = numeros.stream()
            .map(n -> n * 2)
            .toList();
        
        System.out.println("Lista original después del stream: " + numeros);
        System.out.println("Nueva lista con valores dobles: " + numerosDobles);
    }

    // Clase inmutable
    static final class Persona {
        private final String nombre;
        private final int edad;

        public Persona(String nombre, int edad) {
            this.nombre = nombre;
            this.edad = edad;
        }

        public String getNombre() {
            return nombre;
        }

        public int getEdad() {
            return edad;
        }

        // Método que retorna una nueva instancia en lugar de modificar
        public Persona conNuevaEdad(int nuevaEdad) {
            return new Persona(this.nombre, nuevaEdad);
        }

        @Override
        public String toString() {
            return "Persona{nombre='" + nombre + "', edad=" + edad + "}";
        }
    }
}

