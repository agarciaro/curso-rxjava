package com.formadoresit.rxjava.tema10;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * TEMA 10: Programación Reactiva en la Web
 * Ejemplo 01: Cliente HTTP con RxJava
 * 
 * Simulación de llamadas HTTP reactivas
 */
public class Ejemplo01_HTTPClient {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 01: Cliente HTTP Reactivo ===\n");

        // 1. Llamada HTTP simple
        System.out.println("--- GET request ---");
        obtenerUsuario(1)
            .subscribe(
                usuario -> System.out.println("Usuario: " + usuario),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );

        Thread.sleep(200);

        // 2. Múltiples llamadas en paralelo
        System.out.println("\n--- Llamadas en paralelo ---");
        Observable.merge(
            obtenerUsuario(1),
            obtenerUsuario(2),
            obtenerUsuario(3)
        )
        .subscribe(usuario -> System.out.println("Recibido: " + usuario));

        Thread.sleep(300);

        // 3. Llamadas secuenciales con flatMap
        System.out.println("\n--- Llamadas secuenciales ---");
        obtenerUsuario(1)
            .flatMap(usuario -> {
                System.out.println("Usuario obtenido: " + usuario);
                return obtenerPedidos(usuario);
            })
            .subscribe(
                pedidos -> System.out.println("Pedidos: " + pedidos),
                error -> System.err.println("Error: " + error)
            );

        Thread.sleep(300);

        // 4. Retry en caso de fallo
        System.out.println("\n--- Retry automático ---");
        obtenerUsuarioConFallo(1)
            .retry(3)
            .subscribe(
                usuario -> System.out.println("Usuario: " + usuario),
                error -> System.err.println("Error después de reintentos: " + error.getMessage())
            );

        Thread.sleep(200);

        // 5. Timeout
        System.out.println("\n--- Timeout ---");
        obtenerUsuarioLento(1)
            .timeout(100, java.util.concurrent.TimeUnit.MILLISECONDS)
            .subscribe(
                usuario -> System.out.println("Usuario: " + usuario),
                error -> System.err.println("Timeout: " + error.getMessage())
            );

        Thread.sleep(200);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Operaciones HTTP son naturalmente asíncronas");
        System.out.println("• flatMap: Para encadenar llamadas dependientes");
        System.out.println("• merge: Para ejecutar en paralelo");
        System.out.println("• retry: Para reintentar en caso de error");
        System.out.println("• timeout: Para limitar tiempo de espera");
    }

    private static Observable<String> obtenerUsuario(int id) {
        return Observable.fromCallable(() -> {
            System.out.println("  Consultando usuario " + id + "...");
            Thread.sleep(100);  // Simular latencia de red
            return "Usuario_" + id;
        }).subscribeOn(Schedulers.io());
    }

    private static Observable<String> obtenerPedidos(String usuario) {
        return Observable.fromCallable(() -> {
            System.out.println("  Consultando pedidos de " + usuario + "...");
            Thread.sleep(100);
            return "Pedidos de " + usuario;
        }).subscribeOn(Schedulers.io());
    }

    private static int intentos = 0;
    private static Observable<String> obtenerUsuarioConFallo(int id) {
        return Observable.fromCallable(() -> {
            intentos++;
            System.out.println("  Intento " + intentos);
            if (intentos < 3) {
                throw new RuntimeException("Error de red");
            }
            return "Usuario_" + id;
        }).subscribeOn(Schedulers.io());
    }

    private static Observable<String> obtenerUsuarioLento(int id) {
        return Observable.fromCallable(() -> {
            Thread.sleep(200);  // Demasiado lento
            return "Usuario_" + id;
        }).subscribeOn(Schedulers.io());
    }
}

