package com.formadoresit.rxjava.practicas.practica01;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PRÁCTICA 01 - Ejercicio 1.4: Programación Declarativa
 * 
 * Análisis de empleados usando Streams de forma declarativa
 */
public class Ejercicio04_ProgramacionDeclarativa {

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1.4: Programación Declarativa ===\n");

        // Datos de prueba
        List<Empleado> empleados = List.of(
            new Empleado("Ana", "IT", 5000),
            new Empleado("Carlos", "IT", 4800),
            new Empleado("Pedro", "IT", 4200),
            new Empleado("Elena", "IT", 4500),
            new Empleado("Juan", "Ventas", 3500),
            new Empleado("María", "Ventas", 3200),
            new Empleado("David", "Ventas", 2900),
            new Empleado("Laura", "RRHH", 3200),
            new Empleado("Roberto", "RRHH", 2800)
        );

        System.out.println("Total de empleados: " + empleados.size() + "\n");

        // 1. Salario promedio por departamento
        System.out.println("=== 1. Salario promedio por departamento ===");
        Map<String, Double> promediosPorDepartamento = empleados.stream()
            .collect(Collectors.groupingBy(
                Empleado::getDepartamento,
                Collectors.averagingDouble(Empleado::getSalario)
            ));
        
        promediosPorDepartamento.forEach((dept, promedio) ->
            System.out.printf("  %s: %.2f €\n", dept, promedio)
        );

        // 2. Top 3 empleados con mayor salario
        System.out.println("\n=== 2. Top 3 empleados con mayor salario ===");
        List<Empleado> top3Salarios = empleados.stream()
            .sorted(Comparator.comparingDouble(Empleado::getSalario).reversed())
            .limit(3)
            .toList();
        
        top3Salarios.forEach(e ->
            System.out.printf("  %s (%.2f €)\n", e.getNombre(), e.getSalario())
        );

        // 3. Total de salarios por departamento
        System.out.println("\n=== 3. Total de salarios por departamento ===");
        Map<String, Double> totalesPorDepartamento = empleados.stream()
            .collect(Collectors.groupingBy(
                Empleado::getDepartamento,
                Collectors.summingDouble(Empleado::getSalario)
            ));
        
        totalesPorDepartamento.forEach((dept, total) ->
            System.out.printf("  %s: %.2f €\n", dept, total)
        );

        // 4. Empleados que ganan más de 3000, ordenados alfabéticamente
        System.out.println("\n=== 4. Empleados con salario > 3000 € (ordenados) ===");
        List<String> empleadosBienPagados = empleados.stream()
            .filter(e -> e.getSalario() > 3000)
            .map(Empleado::getNombre)
            .sorted()
            .toList();
        
        System.out.println("  " + empleadosBienPagados);

        // Análisis adicional
        System.out.println("\n=== Análisis adicional ===");
        
        // Salario más alto y más bajo
        double salarioMaximo = empleados.stream()
            .mapToDouble(Empleado::getSalario)
            .max()
            .orElse(0);
        
        double salarioMinimo = empleados.stream()
            .mapToDouble(Empleado::getSalario)
            .min()
            .orElse(0);
        
        System.out.printf("Salario máximo: %.2f €\n", salarioMaximo);
        System.out.printf("Salario mínimo: %.2f €\n", salarioMinimo);

        // Cantidad de empleados por departamento
        Map<String, Long> empleadosPorDept = empleados.stream()
            .collect(Collectors.groupingBy(
                Empleado::getDepartamento,
                Collectors.counting()
            ));
        
        System.out.println("\nEmpleados por departamento:");
        empleadosPorDept.forEach((dept, cantidad) ->
            System.out.printf("  %s: %d empleados\n", dept, cantidad)
        );
    }
}

/**
 * Clase Empleado inmutable
 */
final class Empleado {
    private final String nombre;
    private final String departamento;
    private final double salario;

    public Empleado(String nombre, String departamento, double salario) {
        this.nombre = nombre;
        this.departamento = departamento;
        this.salario = salario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public double getSalario() {
        return salario;
    }

    @Override
    public String toString() {
        return String.format("Empleado{nombre='%s', departamento='%s', salario=%.2f}", 
            nombre, departamento, salario);
    }
}

