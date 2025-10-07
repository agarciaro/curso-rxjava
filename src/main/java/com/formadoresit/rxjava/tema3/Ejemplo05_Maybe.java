package com.formadoresit.rxjava.tema3;

import io.reactivex.Maybe;
import java.util.HashMap;
import java.util.Map;

/**
 * TEMA 3: Observables y Observers
 * Ejemplo 05: Maybe en detalle
 * 
 * Maybe: Puede emitir 0 o 1 elemento, o un error
 * Casos de uso: Búsquedas que pueden no encontrar resultado
 */
public class Ejemplo05_Maybe {

    private static Map<String, String> baseDatos = new HashMap<>();

    static {
        baseDatos.put("user1", "Juan Pérez");
        baseDatos.put("user2", "María García");
        baseDatos.put("user3", "Pedro Martínez");
    }

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 05: Maybe ===\n");

        // 1. Maybe con valor
        System.out.println("--- Maybe.just() ---");
        Maybe<String> maybeConValor = Maybe.just("Tengo valor");
        maybeConValor.subscribe(
            valor -> System.out.println("Valor: " + valor),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado sin valor")
        );

        // 2. Maybe vacío
        System.out.println("\n--- Maybe.empty() ---");
        Maybe<String> maybeVacio = Maybe.empty();
        maybeVacio.subscribe(
            valor -> System.out.println("Valor: " + valor),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado sin emitir valor")
        );

        // 3. Maybe con error
        System.out.println("\n--- Maybe.error() ---");
        Maybe<String> maybeError = Maybe.error(new RuntimeException("Error intencional"));
        maybeError.subscribe(
            valor -> System.out.println("Valor: " + valor),
            error -> System.err.println("Error: " + error.getMessage()),
            () -> System.out.println("Completado")
        );

        // 4. Maybe desde Callable (con valor)
        System.out.println("\n--- Maybe.fromCallable() con valor ---");
        Maybe<String> maybeCallable = Maybe.fromCallable(() -> {
            // Simulación de búsqueda
            return "Resultado encontrado";
        });
        maybeCallable.subscribe(
            valor -> System.out.println("Encontrado: " + valor),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("No encontrado")
        );

        // 5. Maybe desde Callable (sin valor)
        System.out.println("\n--- Maybe.fromCallable() sin valor ---");
        Maybe<String> maybeCallableVacio = Maybe.fromCallable(() -> {
            // Simulación de búsqueda sin resultado
            return null; // null = empty
        });
        maybeCallableVacio.subscribe(
            valor -> System.out.println("Encontrado: " + valor),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("No encontrado")
        );

        // 6. Ejemplo práctico: Buscar usuario
        System.out.println("\n--- Búsqueda de usuarios ---");
        
        buscarUsuario("user1").subscribe(
            nombre -> System.out.println("Usuario encontrado: " + nombre),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Usuario no existe")
        );

        buscarUsuario("user999").subscribe(
            nombre -> System.out.println("Usuario encontrado: " + nombre),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Usuario no existe")
        );

        // 7. Maybe con operador map
        System.out.println("\n--- Maybe con map() ---");
        Maybe.just("juan")
            .map(String::toUpperCase)
            .subscribe(
                nombre -> System.out.println("Nombre: " + nombre),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Sin valor")
            );

        // 8. Maybe con flatMap
        System.out.println("\n--- Maybe con flatMap() ---");
        Maybe.just("user2")
            .flatMap(userId -> buscarUsuario(userId))
            .subscribe(
                nombre -> System.out.println("Usuario: " + nombre),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("No encontrado")
            );

        // 9. Maybe con defaultIfEmpty
        System.out.println("\n--- Maybe con defaultIfEmpty() ---");
        Maybe.empty()
            .defaultIfEmpty("Valor por defecto")
            .subscribe(
                valor -> System.out.println("Valor: " + valor),
                error -> System.err.println("Error: " + error)
            );

        // 10. Convertir Maybe a Single
        System.out.println("\n--- Maybe a Single ---");
        Maybe.just("Hola")
            .toSingle()
            .subscribe(
                valor -> System.out.println("Single: " + valor),
                error -> System.err.println("Error: " + error)
            );

        // 11. Maybe con filter
        System.out.println("\n--- Maybe con filter() ---");
        Maybe.just(15)
            .filter(n -> n > 10)
            .subscribe(
                valor -> System.out.println("Valor filtrado: " + valor),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("No pasó el filtro")
            );

        Maybe.just(5)
            .filter(n -> n > 10)
            .subscribe(
                valor -> System.out.println("Valor filtrado: " + valor),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("No pasó el filtro (5 no es > 10)")
            );

        // 12. Ejemplo práctico: Configuración opcional
        System.out.println("\n--- Ejemplo: Configuración opcional ---");
        obtenerConfiguracion("theme")
            .subscribe(
                config -> System.out.println("Configuración: " + config),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Usando configuración por defecto")
            );

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Maybe puede emitir 0 o 1 elemento");
        System.out.println("• Útil para búsquedas que pueden no tener resultado");
        System.out.println("• Tres posibles estados: onSuccess, onComplete (vacío), onError");
        System.out.println("• Operadores: map, flatMap, filter, defaultIfEmpty");
    }

    private static Maybe<String> buscarUsuario(String userId) {
        return Maybe.fromCallable(() -> baseDatos.get(userId));
    }

    private static Maybe<String> obtenerConfiguracion(String clave) {
        return Maybe.fromCallable(() -> {
            Map<String, String> configs = new HashMap<>();
            configs.put("theme", "dark");
            return configs.get(clave);
        });
    }
}

