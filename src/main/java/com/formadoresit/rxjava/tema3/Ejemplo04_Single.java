package com.formadoresit.rxjava.tema3;

import io.reactivex.Single;
import java.util.Random;

/**
 * TEMA 3: Observables y Observers
 * Ejemplo 04: Single en detalle
 * 
 * Single: Emite exactamente UN valor o un error
 * Casos de uso: llamadas HTTP, consultas DB que retornan un único registro
 */
public class Ejemplo04_Single {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 04: Single ===\n");

        // 1. Crear Single con just()
        System.out.println("--- Single.just() ---");
        Single<String> single = Single.just("Valor único");
        single.subscribe(
            valor -> System.out.println("Valor: " + valor),
            error -> System.err.println("Error: " + error)
        );

        // 2. Single desde Callable
        System.out.println("\n--- Single.fromCallable() ---");
        Single<Integer> singleCallable = Single.fromCallable(() -> {
            System.out.println("  Ejecutando cálculo...");
            return 42;
        });
        singleCallable.subscribe(
            resultado -> System.out.println("Resultado: " + resultado),
            error -> System.err.println("Error: " + error)
        );

        // 3. Single con operadores map
        System.out.println("\n--- Single con map() ---");
        Single.just(5)
            .map(n -> n * n)
            .subscribe(
                resultado -> System.out.println("5 al cuadrado = " + resultado),
                error -> System.err.println("Error: " + error)
            );

        // 4. Single con flatMap (encadenamiento)
        System.out.println("\n--- Single con flatMap() ---");
        Single.just("Usuario123")
            .flatMap(userId -> obtenerDatosUsuario(userId))
            .flatMap(usuario -> obtenerPerfilUsuario(usuario))
            .subscribe(
                perfil -> System.out.println("Perfil: " + perfil),
                error -> System.err.println("Error: " + error)
            );

        // 5. Convertir Observable a Single
        System.out.println("\n--- Observable a Single ---");
        io.reactivex.Observable.just(1, 2, 3, 4, 5)
            .reduce((a, b) -> a + b)  // Retorna Single
            .subscribe(
                suma -> System.out.println("Suma total: " + suma),
                error -> System.err.println("Error: " + error)
            );

        // 6. Single con error
        System.out.println("\n--- Single con error ---");
        Single<String> singleConError = Single.create(emitter -> {
            // Simular operación que falla
            if (new Random().nextBoolean()) {
                emitter.onError(new RuntimeException("Operación falló"));
            } else {
                emitter.onSuccess("Operación exitosa");
            }
        });

        singleConError.subscribe(
            valor -> System.out.println("Éxito: " + valor),
            error -> System.err.println("Error capturado: " + error.getMessage())
        );

        // 7. Single con manejo de error
        System.out.println("\n--- Single con onErrorReturn ---");
        Single.error(new RuntimeException("Error simulado"))
            .onErrorReturn(error -> "Valor por defecto en caso de error")
            .subscribe(
                valor -> System.out.println("Valor: " + valor),
                error -> System.err.println("Error: " + error)
            );

        // 8. Single con zip (combinar dos Singles)
        System.out.println("\n--- Single.zip() ---");
        Single<String> nombre = Single.just("Juan");
        Single<Integer> edad = Single.just(30);

        Single.zip(nombre, edad, (n, e) -> n + " tiene " + e + " años")
            .subscribe(
                resultado -> System.out.println(resultado),
                error -> System.err.println("Error: " + error)
            );

        // 9. Ejemplo práctico: Autenticación
        System.out.println("\n--- Ejemplo práctico: Autenticación ---");
        autenticarUsuario("admin", "1234")
            .subscribe(
                token -> System.out.println("Token de acceso: " + token),
                error -> System.err.println("Autenticación fallida: " + error.getMessage())
            );

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Single emite exactamente UN valor o error");
        System.out.println("• Útil para operaciones que retornan un único resultado");
        System.out.println("• Métodos: onSuccess() y onError()");
        System.out.println("• Operadores: map, flatMap, zip, etc.");
    }

    private static Single<String> obtenerDatosUsuario(String userId) {
        return Single.just("Usuario: " + userId);
    }

    private static Single<String> obtenerPerfilUsuario(String usuario) {
        return Single.just(usuario + " - Perfil Admin");
    }

    private static Single<String> autenticarUsuario(String username, String password) {
        return Single.create(emitter -> {
            // Simular autenticación
            if (username.equals("admin") && password.equals("1234")) {
                emitter.onSuccess("TOKEN_ABC123XYZ");
            } else {
                emitter.onError(new RuntimeException("Credenciales inválidas"));
            }
        });
    }
}

