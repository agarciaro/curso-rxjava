package com.formadoresit.rxjava.tema3;

import io.reactivex.Completable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 3: Observables y Observers
 * Ejemplo 06: Completable en detalle
 * 
 * Completable: No emite elementos, solo indica completación o error
 * Casos de uso: Operaciones de escritura, actualizaciones, tareas sin resultado
 */
public class Ejemplo06_Completable {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 06: Completable ===\n");

        // 1. Completable básico
        System.out.println("--- Completable.complete() ---");
        Completable completable = Completable.complete();
        completable.subscribe(
            () -> System.out.println("¡Completado!"),
            error -> System.err.println("Error: " + error)
        );

        // 2. Completable con error
        System.out.println("\n--- Completable.error() ---");
        Completable completableError = Completable.error(new RuntimeException("Error intencional"));
        completableError.subscribe(
            () -> System.out.println("Completado"),
            error -> System.err.println("Error: " + error.getMessage())
        );

        // 3. Completable desde Runnable
        System.out.println("\n--- Completable.fromRunnable() ---");
        Completable completableRunnable = Completable.fromRunnable(() -> {
            System.out.println("  Ejecutando tarea...");
            System.out.println("  Tarea completada");
        });
        completableRunnable.subscribe(
            () -> System.out.println("¡Éxito!"),
            error -> System.err.println("Error: " + error)
        );

        // 4. Completable desde Action
        System.out.println("\n--- Completable.fromAction() ---");
        Completable completableAction = Completable.fromAction(() -> {
            System.out.println("  Guardando datos...");
            Thread.sleep(100);
            System.out.println("  Datos guardados");
        });
        completableAction.subscribe(
            () -> System.out.println("Operación completada"),
            error -> System.err.println("Error: " + error)
        );

        // 5. Completable con delay
        System.out.println("\n--- Completable con delay() ---");
        System.out.println("Esperando 500ms...");
        Completable.complete()
            .delay(500, TimeUnit.MILLISECONDS)
            .blockingAwait(); // Bloquea hasta que complete
        System.out.println("¡Delay completado!");

        // 6. Completable con andThen (encadenar)
        System.out.println("\n--- Completable.andThen() ---");
        Completable paso1 = Completable.fromAction(() -> 
            System.out.println("  Paso 1: Validar datos"));
        Completable paso2 = Completable.fromAction(() -> 
            System.out.println("  Paso 2: Guardar en DB"));
        Completable paso3 = Completable.fromAction(() -> 
            System.out.println("  Paso 3: Enviar notificación"));

        paso1.andThen(paso2)
             .andThen(paso3)
             .subscribe(
                 () -> System.out.println("¡Todos los pasos completados!"),
                 error -> System.err.println("Error: " + error)
             );

        // 7. Completable.merge (ejecutar en paralelo)
        System.out.println("\n--- Completable.merge() ---");
        Completable tarea1 = Completable.fromAction(() -> 
            System.out.println("  Tarea 1 ejecutándose"));
        Completable tarea2 = Completable.fromAction(() -> 
            System.out.println("  Tarea 2 ejecutándose"));
        Completable tarea3 = Completable.fromAction(() -> 
            System.out.println("  Tarea 3 ejecutándose"));

        Completable.mergeArray(tarea1, tarea2, tarea3)
            .subscribe(
                () -> System.out.println("Todas las tareas completadas"),
                error -> System.err.println("Error: " + error)
            );

        // 8. Convertir a otros tipos
        System.out.println("\n--- Conversiones ---");
        
        // Completable a Observable
        Completable.complete()
            .toObservable()
            .subscribe(
                item -> System.out.println("Item: " + item),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Observable completado (sin items)")
            );

        // Completable a Single
        Completable.complete()
            .toSingleDefault("Valor por defecto")
            .subscribe(
                valor -> System.out.println("Single: " + valor),
                error -> System.err.println("Error: " + error)
            );

        // 9. Ejemplo práctico: Actualización de base de datos
        System.out.println("\n--- Ejemplo práctico: Actualización DB ---");
        actualizarUsuario(123, "Nuevo nombre")
            .subscribe(
                () -> System.out.println("Usuario actualizado exitosamente"),
                error -> System.err.println("Error al actualizar: " + error.getMessage())
            );

        // 10. Ejemplo práctico: Operaciones secuenciales
        System.out.println("\n--- Ejemplo: Pipeline de operaciones ---");
        validarDatos()
            .andThen(procesarDatos())
            .andThen(guardarResultado())
            .subscribe(
                () -> System.out.println("Pipeline completado exitosamente"),
                error -> System.err.println("Pipeline falló: " + error.getMessage())
            );

        // 11. Completable con retry
        System.out.println("\n--- Completable con retry() ---");
        final int[] intentos = {0};
        Completable.fromAction(() -> {
            intentos[0]++;
            System.out.println("  Intento #" + intentos[0]);
            if (intentos[0] < 3) {
                throw new RuntimeException("Error temporal");
            }
        })
        .retry(3)
        .subscribe(
            () -> System.out.println("Completado después de reintentos"),
            error -> System.err.println("Error final: " + error.getMessage())
        );

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Completable no emite elementos");
        System.out.println("• Solo indica completación exitosa o error");
        System.out.println("• Útil para operaciones de escritura/actualización");
        System.out.println("• Métodos: onComplete() y onError()");
        System.out.println("• Operadores: andThen, merge, delay, retry");
    }

    private static Completable actualizarUsuario(int userId, String nuevoNombre) {
        return Completable.fromAction(() -> {
            System.out.println("  Actualizando usuario " + userId + " a: " + nuevoNombre);
            Thread.sleep(100); // Simular operación DB
        });
    }

    private static Completable validarDatos() {
        return Completable.fromAction(() -> 
            System.out.println("  ✓ Datos validados"));
    }

    private static Completable procesarDatos() {
        return Completable.fromAction(() -> 
            System.out.println("  ✓ Datos procesados"));
    }

    private static Completable guardarResultado() {
        return Completable.fromAction(() -> 
            System.out.println("  ✓ Resultado guardado"));
    }
}

