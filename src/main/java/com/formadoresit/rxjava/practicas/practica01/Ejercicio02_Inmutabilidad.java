package com.formadoresit.rxjava.practicas.practica01;

/**
 * PRÁCTICA 01 - Ejercicio 1.2: Inmutabilidad
 * 
 * Crear clase inmutable Producto con métodos que retornan nuevas instancias
 */
public class Ejercicio02_Inmutabilidad {

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1.2: Inmutabilidad ===\n");

        // Crear producto original
        Producto producto = new Producto(1, "Laptop", 1000.0, 10);
        System.out.println("Producto original: " + producto);

        // Cambiar precio (retorna nueva instancia)
        Producto productoConNuevoPrecio = producto.conNuevoPrecio(900.0);
        System.out.println("\nCon nuevo precio: " + productoConNuevoPrecio);
        System.out.println("Producto original no cambió: " + producto);

        // Agregar stock
        Producto productoConMasStock = productoConNuevoPrecio.agregarStock(5);
        System.out.println("\nDespués de agregar stock: " + productoConMasStock);

        // Vender
        try {
            Producto productoDespuesDeVenta = productoConMasStock.vender(5);
            System.out.println("\nDespués de vender 5 unidades: " + productoDespuesDeVenta);
            
            // Intentar vender más de lo disponible
            System.out.println("\nIntentando vender 20 unidades (solo hay 10)...");
            productoDespuesDeVenta.vender(20);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Intentar precio negativo
        System.out.println("\nIntentando establecer precio negativo...");
        try {
            producto.conNuevoPrecio(-100);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

/**
 * Clase inmutable Producto
 * - Todos los campos son final
 * - No hay setters
 * - Los métodos "modificadores" retornan nuevas instancias
 */
final class Producto {
    private final int id;
    private final String nombre;
    private final double precio;
    private final int stock;

    public Producto(int id, String nombre, double precio, int stock) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    /**
     * Retorna una nueva instancia con el nuevo precio
     */
    public Producto conNuevoPrecio(double nuevoPrecio) {
        if (nuevoPrecio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        return new Producto(this.id, this.nombre, nuevoPrecio, this.stock);
    }

    /**
     * Retorna una nueva instancia con stock incrementado
     */
    public Producto agregarStock(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        return new Producto(this.id, this.nombre, this.precio, this.stock + cantidad);
    }

    /**
     * Retorna una nueva instancia con stock decrementado
     * Valida que haya stock suficiente
     */
    public Producto vender(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        if (cantidad > this.stock) {
            throw new IllegalArgumentException(
                "Stock insuficiente. Disponible: " + this.stock + ", solicitado: " + cantidad
            );
        }
        return new Producto(this.id, this.nombre, this.precio, this.stock - cantidad);
    }

    @Override
    public String toString() {
        return String.format("Producto{id=%d, nombre='%s', precio=%.2f, stock=%d}", 
            id, nombre, precio, stock);
    }
}

