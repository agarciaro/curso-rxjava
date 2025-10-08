package com.formadoresit.rxjava.tema5;

import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 5: Combinando Observables
 * Ejemplo 06: startWith() - Añadir elementos al inicio
 * 
 * startWith añade elementos al comienzo de un Observable
 * Útil para valores por defecto, datos de cache, o inicialización
 */
public class Ejemplo06_StartWith {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 06: startWith() ===\n");

        // 1. startWith básico con un elemento
        System.out.println("--- startWith() básico ---");
        Observable.just(2, 3, 4, 5)
            .startWith(1)
            .subscribe(numero -> System.out.println("Número: " + numero));

        // 2. startWith con múltiples elementos
        System.out.println("\n--- startWith() con múltiples elementos ---");
        Observable.just("C", "D", "E")
            .startWithArray("A", "B")
            .subscribe(letra -> System.out.println("Letra: " + letra));

        // 3. startWith con Observable
        System.out.println("\n--- startWith() con Observable ---");
        Observable.just("Medio", "Final")
            .startWith(Observable.just("Inicio"))
            .subscribe(palabra -> System.out.println("Palabra: " + palabra));

        // 4. startWithArray
        System.out.println("\n--- startWithArray() ---");
        Observable.just(100, 200, 300)
            .startWithArray(10, 20, 30)
            .subscribe(numero -> System.out.println("Número: " + numero));

        // 5. startWith con timing
        System.out.println("\n--- startWith() con timing ---");
        Observable.interval(200, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> "Dato-" + i)
            .startWith("Inicial")
            .blockingSubscribe(dato -> System.out.println("Dato: " + dato));

        // 6. Ejemplo práctico: Valores por defecto
        System.out.println("\n--- Ejemplo práctico: Valores por defecto ---");
        Observable<String> configuracion = obtenerConfiguracion()
            .startWithArray("Tema: claro", "Idioma: español", "Notificaciones: activas");
        
        configuracion.subscribe(config -> System.out.println("Config: " + config));

        // 7. Ejemplo práctico: Cache de datos
        System.out.println("\n--- Ejemplo práctico: Cache de datos ---");
        Observable<String> datosNuevos = obtenerDatosNuevos()
            .startWith(obtenerDatosCache());
        
        datosNuevos.subscribe(dato -> System.out.println("Dato: " + dato));

        // 8. Ejemplo práctico: Inicialización de UI
        System.out.println("\n--- Ejemplo práctico: Inicialización de UI ---");
        Observable<String> mensajes = obtenerMensajes()
            .startWithArray("Sistema iniciado", "Cargando datos...", "Conectando...");
        
        mensajes.subscribe(mensaje -> System.out.println("UI: " + mensaje));

        // 9. Ejemplo práctico: Historial de transacciones
        System.out.println("\n--- Ejemplo práctico: Historial de transacciones ---");
        Observable<Transaccion> nuevasTransacciones = obtenerNuevasTransacciones()
            .startWith(obtenerTransaccionesRecientes());
        
        nuevasTransacciones.subscribe(transaccion -> 
            System.out.println("Transacción: " + transaccion.descripcion + " - $" + transaccion.monto));

        // 10. Ejemplo práctico: Dashboard con datos iniciales
        System.out.println("\n--- Ejemplo práctico: Dashboard ---");
        Observable<Metrica> metricas = obtenerMetricasEnTiempoReal()
            .startWith(obtenerMetricasIniciales());
        
        metricas.subscribe(metrica -> 
            System.out.println("Métrica: " + metrica.nombre + " = " + metrica.valor));

        // 11. startWith con error handling
        System.out.println("\n--- startWith() con error handling ---");
        Observable.just("Datos normales")
            .startWith("Datos de cache")
            .onErrorReturn(error -> "Error: " + error.getMessage())
            .subscribe(dato -> System.out.println("Dato: " + dato));

        // 12. startWith con filtrado
        System.out.println("\n--- startWith() con filtrado ---");
        Observable.just(1, 2, 3, 4, 5)
            .startWith(0)
            .filter(num -> num > 0)
            .subscribe(numero -> System.out.println("Número positivo: " + numero));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• startWith: Añade elementos al inicio");
        System.out.println("• Útil para valores por defecto y cache");
        System.out.println("• Mantiene el orden (inicio + original)");
        System.out.println("• Puede usar elementos individuales o Observables");
        System.out.println("• Ideal para inicialización de UI y datos");
    }

    private static Observable<String> obtenerConfiguracion() {
        return Observable.just("Tema: oscuro", "Idioma: inglés");
    }

    private static Observable<String> obtenerDatosCache() {
        return Observable.just("Dato cache 1", "Dato cache 2");
    }

    private static Observable<String> obtenerDatosNuevos() {
        return Observable.just("Dato nuevo 1", "Dato nuevo 2");
    }

    private static Observable<String> obtenerMensajes() {
        return Observable.just("Datos cargados", "Conexión establecida");
    }

    private static Observable<Transaccion> obtenerTransaccionesRecientes() {
        return Observable.just(
            new Transaccion("Compra café", 3.50),
            new Transaccion("Pago factura", 45.00)
        );
    }

    private static Observable<Transaccion> obtenerNuevasTransacciones() {
        return Observable.just(
            new Transaccion("Compra gasolina", 25.00),
            new Transaccion("Transferencia", 100.00)
        );
    }

    private static Observable<Metrica> obtenerMetricasIniciales() {
        return Observable.just(
            new Metrica("CPU", 45.0),
            new Metrica("Memoria", 60.0)
        );
    }

    private static Observable<Metrica> obtenerMetricasEnTiempoReal() {
        return Observable.just(
            new Metrica("CPU", 50.0),
            new Metrica("Memoria", 65.0),
            new Metrica("Disco", 30.0)
        );
    }

    static class Transaccion {
        String descripcion;
        double monto;
        
        Transaccion(String descripcion, double monto) {
            this.descripcion = descripcion;
            this.monto = monto;
        }
    }

    static class Metrica {
        String nombre;
        double valor;
        
        Metrica(String nombre, double valor) {
            this.nombre = nombre;
            this.valor = valor;
        }
    }
}
