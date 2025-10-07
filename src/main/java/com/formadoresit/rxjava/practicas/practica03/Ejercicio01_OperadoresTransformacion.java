package com.formadoresit.rxjava.practicas.practica03;

import io.reactivex.Observable;
import java.util.Arrays;
import java.util.List;

/**
 * PRÁCTICA 03 - Ejercicio 3.1: Operadores de Transformación
 * 
 * Trabajar con operadores map, flatMap, scan, buffer y groupBy
 */
public class Ejercicio01_OperadoresTransformacion {

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3.1: Operadores de Transformación ===\n");

        // Datos de prueba
        List<Pedido> pedidos = Arrays.asList(
            new Pedido(1, "Juan", Arrays.asList("Laptop", "Mouse"), 1200.0),
            new Pedido(2, "María", Arrays.asList("Teclado", "Monitor"), 450.0),
            new Pedido(3, "Juan", Arrays.asList("Cable HDMI"), 15.0),
            new Pedido(4, "Pedro", Arrays.asList("Webcam", "Micrófono"), 180.0),
            new Pedido(5, "María", Arrays.asList("Auriculares"), 80.0),
            new Pedido(6, "Pedro", Arrays.asList("Laptop", "Mochila"), 1150.0)
        );

        // 1. map() - Extraer solo IDs
        System.out.println("--- 1. Extraer IDs con map() ---");
        Observable.fromIterable(pedidos)
            .map(pedido -> pedido.id)
            .subscribe(id -> System.out.print(id + " "));
        System.out.println("\n");

        // 2. flatMap() - Obtener todos los productos en un único flujo
        System.out.println("--- 2. Todos los productos con flatMap() ---");
        Observable.fromIterable(pedidos)
            .flatMap(pedido -> Observable.fromIterable(pedido.productos))
            .subscribe(producto -> System.out.println("  " + producto));
        System.out.println();

        // 3. scan() - Total acumulado
        System.out.println("--- 3. Total acumulado con scan() ---");
        Observable.fromIterable(pedidos)
            .map(pedido -> pedido.total)
            .scan((acum, actual) -> acum + actual)
            .subscribe(totalAcum -> System.out.printf("  Total acumulado: %.2f €\n", totalAcum));
        System.out.println();

        // 4. buffer() - Agrupar pedidos de 3 en 3
        System.out.println("--- 4. Agrupar pedidos con buffer(3) ---");
        Observable.fromIterable(pedidos)
            .buffer(3)
            .subscribe(grupo -> {
                System.out.println("Grupo de " + grupo.size() + " pedidos:");
                grupo.forEach(p -> System.out.println("  - Pedido #" + p.id + " de " + p.cliente));
            });
        System.out.println();

        // 5. groupBy() - Agrupar por cliente
        System.out.println("--- 5. Agrupar por cliente con groupBy() ---");
        Observable.fromIterable(pedidos)
            .groupBy(pedido -> pedido.cliente)
            .subscribe(grupo -> {
                String cliente = grupo.getKey();
                grupo.toList().subscribe(pedidosCliente -> {
                    System.out.println("Cliente " + cliente + ":");
                    pedidosCliente.forEach(p -> 
                        System.out.printf("  - Pedido #%d: %.2f €\n", p.id, p.total)
                    );
                    double totalCliente = pedidosCliente.stream()
                        .mapToDouble(p -> p.total)
                        .sum();
                    System.out.printf("  Total: %.2f €\n\n", totalCliente);
                });
            });
    }
}

class Pedido {
    int id;
    String cliente;
    List<String> productos;
    double total;

    Pedido(int id, String cliente, List<String> productos, double total) {
        this.id = id;
        this.cliente = cliente;
        this.productos = productos;
        this.total = total;
    }
}

