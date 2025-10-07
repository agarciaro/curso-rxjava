package com.formadoresit.rxjava.practicas.practica03;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * PRÁCTICA 03 - Ejercicio 3.5: Sistema E-Commerce
 * 
 * Transformaciones anidadas comparando flatMap, concatMap y switchMap
 */
public class Ejercicio05_ECommerce {

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3.5: Sistema E-Commerce ===\n");

        // Datos de prueba
        List<Usuario> usuarios = Arrays.asList(
            new Usuario(1, "Juan"),
            new Usuario(2, "María"),
            new Usuario(3, "Pedro")
        );

        // PARTE 1: Usando flatMap (sin orden garantizado, más rápido)
        System.out.println("=== ANÁLISIS CON FLATMAP ===\n");
        
        Observable.fromIterable(usuarios)
            .flatMap(usuario -> 
                obtenerPedidos(usuario)
                    .flatMap(pedido -> obtenerDetallesPedido(pedido))
            )
            .toList()
            .subscribe(detalles -> {
                System.out.println("\nTotal de productos procesados: " + detalles.size());
                
                // Calcular gasto por usuario
                Map<String, Double> gastoPorUsuario = new HashMap<>();
                detalles.forEach(detalle -> {
                    gastoPorUsuario.merge(detalle.nombreUsuario, detalle.precio, Double::sum);
                });
                
                System.out.println("\n--- Gasto por usuario ---");
                gastoPorUsuario.forEach((usuario, total) ->
                    System.out.printf("%s: %.2f €\n", usuario, total)
                );
                
                // Usuario que más gastó
                gastoPorUsuario.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .ifPresent(entry ->
                        System.out.printf("\nUsuario con mayor gasto: %s (%.2f €)\n", 
                            entry.getKey(), entry.getValue())
                    );
                
                // Top 5 productos más vendidos
                System.out.println("\n--- Top 5 productos más vendidos ---");
                Map<String, Long> conteoProductos = new HashMap<>();
                detalles.forEach(detalle ->
                    conteoProductos.merge(detalle.nombreProducto, 1L, Long::sum)
                );
                
                conteoProductos.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry ->
                        System.out.printf("%d. %s (%d unidades)\n", 
                            conteoProductos.entrySet().stream()
                                .filter(e -> e.getValue() >= entry.getValue())
                                .count(),
                            entry.getKey(), 
                            entry.getValue())
                    );
            });

        // PARTE 2: Comparación de estrategias
        System.out.println("\n\n=== COMPARACIÓN DE ESTRATEGIAS ===\n");

        System.out.println("--- flatMap (procesamiento paralelo) ---");
        long startFlat = System.currentTimeMillis();
        List<PedidoInfo> pedidosFlat = Observable.fromIterable(usuarios)
            .flatMap(Ejercicio05_ECommerce::obtenerPedidos)
            .toList()
            .blockingGet();
        long endFlat = System.currentTimeMillis();
        System.out.println("Pedidos procesados: " + pedidosFlat.size());
        System.out.println("Tiempo: " + (endFlat - startFlat) + "ms");
        System.out.println("Orden: Puede variar (no garantizado)");

        System.out.println("\n--- concatMap (orden preservado) ---");
        long startConcat = System.currentTimeMillis();
        List<PedidoInfo> pedidosConcat = Observable.fromIterable(usuarios)
            .concatMap(Ejercicio05_ECommerce::obtenerPedidos)
            .toList()
            .blockingGet();
        long endConcat = System.currentTimeMillis();
        System.out.println("Pedidos procesados: " + pedidosConcat.size());
        System.out.println("Tiempo: " + (endConcat - startConcat) + "ms");
        System.out.println("Orden: Preservado (secuencial)");

        System.out.println("\n--- switchMap (cancela anteriores) ---");
        System.out.println("Solo procesa el último usuario (cancela los anteriores)");
        Observable.fromIterable(usuarios)
            .switchMap(Ejercicio05_ECommerce::obtenerPedidos)
            .toList()
            .subscribe(pedidos -> {
                System.out.println("Pedidos del último usuario: " + pedidos.size());
                System.out.println("(Útil para búsquedas donde solo importa la última)");
            });

        System.out.println("\n=== CONCLUSIONES ===");
        System.out.println("• flatMap: Más rápido pero sin orden garantizado");
        System.out.println("• concatMap: Orden preservado pero más lento");
        System.out.println("• switchMap: Solo procesa el último, cancela anteriores");
    }

    private static Observable<PedidoInfo> obtenerPedidos(Usuario usuario) {
        // Simular latencia de API
        return Observable.create(emitter -> {
            Thread.sleep(50); // Simular latencia
            
            // Generar pedidos según el usuario
            if (usuario.id == 1) {
                emitter.onNext(new PedidoInfo(101, usuario.nombre, Arrays.asList("Laptop", "Mouse")));
                emitter.onNext(new PedidoInfo(102, usuario.nombre, Arrays.asList("Teclado")));
            } else if (usuario.id == 2) {
                emitter.onNext(new PedidoInfo(201, usuario.nombre, Arrays.asList("Monitor", "Cable HDMI")));
                emitter.onNext(new PedidoInfo(202, usuario.nombre, Arrays.asList("Webcam")));
            } else {
                emitter.onNext(new PedidoInfo(301, usuario.nombre, Arrays.asList("Auriculares", "Micrófono")));
            }
            
            emitter.onComplete();
        });
    }

    private static Observable<DetallePedido> obtenerDetallesPedido(PedidoInfo pedido) {
        return Observable.fromIterable(pedido.productos)
            .map(producto -> {
                double precio = calcularPrecio(producto);
                return new DetallePedido(
                    pedido.id,
                    pedido.nombreUsuario,
                    producto,
                    precio
                );
            });
    }

    private static double calcularPrecio(String producto) {
        Map<String, Double> precios = Map.of(
            "Laptop", 1000.0,
            "Mouse", 20.0,
            "Teclado", 50.0,
            "Monitor", 300.0,
            "Cable HDMI", 15.0,
            "Webcam", 80.0,
            "Auriculares", 60.0,
            "Micrófono", 120.0
        );
        return precios.getOrDefault(producto, 50.0);
    }
}

class Usuario {
    int id;
    String nombre;

    Usuario(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}

class PedidoInfo {
    int id;
    String nombreUsuario;
    List<String> productos;

    PedidoInfo(int id, String nombreUsuario, List<String> productos) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.productos = productos;
    }
}

class DetallePedido {
    int pedidoId;
    String nombreUsuario;
    String nombreProducto;
    double precio;

    DetallePedido(int pedidoId, String nombreUsuario, String nombreProducto, double precio) {
        this.pedidoId = pedidoId;
        this.nombreUsuario = nombreUsuario;
        this.nombreProducto = nombreProducto;
        this.precio = precio;
    }
}

