package com.formadoresit.rxjava.tema2;

import io.reactivex.Observable;

/**
 * TEMA 2: Introducción a ReactiveX RxJava 2
 * Ejemplo 05: Manejo de Errores
 * 
 * RxJava proporciona varios operadores para manejar errores gracefully
 */
public class Ejemplo05_ManejoDeErrores {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 05: Manejo de Errores ===\n");

        // Ejemplo 1: Error sin manejo
        System.out.println("--- Error sin manejo especial ---");
        Observable<Integer> observableConError = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onError(new RuntimeException("¡Error!"));
        });

        observableConError.subscribe(
            numero -> System.out.println("Número: " + numero),
            error -> System.err.println("Error capturado: " + error.getMessage()),
            () -> System.out.println("Completado")
        );

        // Ejemplo 2: onErrorReturn - Retorna valor por defecto
        System.out.println("\n--- onErrorReturn ---");
        Observable.just(1, 2, 3, 4, 5)
            .map(n -> {
                if (n == 3) throw new RuntimeException("Error en 3");
                return n * 10;
            })
            .onErrorReturn(error -> -1)
            .subscribe(
                numero -> System.out.println("Resultado: " + numero),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );

        // Ejemplo 3: onErrorReturnItem - Retorna item específico
        System.out.println("\n--- onErrorReturnItem ---");
        Observable.just("A", "B", "C")
            .map(letra -> {
                if (letra.equals("B")) throw new RuntimeException("Error en B");
                return letra.toLowerCase();
            })
            .onErrorReturnItem("valor-error")
            .subscribe(
                letra -> System.out.println("Letra: " + letra),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );

        // Ejemplo 4: onErrorResumeNext - Cambia a otro Observable
        System.out.println("\n--- onErrorResumeNext ---");
        Observable<Integer> observable1 = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onError(new RuntimeException("Error en observable1"));
        });

        Observable<Integer> observableFallback = Observable.just(100, 200, 300);

        observable1
            .onErrorResumeNext(observableFallback)
            .subscribe(
                numero -> System.out.println("Número: " + numero),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );

        // Ejemplo 5: retry - Reintenta en caso de error
        System.out.println("\n--- retry ---");
        final int[] intento = {0};
        
        Observable.create(emitter -> {
            intento[0]++;
            System.out.println("  Intento #" + intento[0]);
            emitter.onNext("Dato 1");
            if (intento[0] < 3) {
                emitter.onError(new RuntimeException("Error temporal"));
            } else {
                emitter.onNext("Dato 2");
                emitter.onComplete();
            }
        })
        .retry(3)
        .subscribe(
            dato -> System.out.println("Recibido: " + dato),
            error -> System.err.println("Error final: " + error),
            () -> System.out.println("Completado exitosamente")
        );

        // Ejemplo 6: Manejo de división por cero
        System.out.println("\n--- Ejemplo práctico: División segura ---");
        Observable.just(10, 5, 0, 2)
            .map(n -> {
                if (n == 0) throw new ArithmeticException("División por cero");
                return 100 / n;
            })
            .onErrorReturn(error -> {
                System.err.println("  Error: " + error.getMessage());
                return 0;
            })
            .subscribe(
                resultado -> System.out.println("Resultado: " + resultado),
                error -> System.err.println("Error no manejado: " + error),
                () -> System.out.println("Completado")
            );

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• onErrorReturn: Retorna valor alternativo en caso de error");
        System.out.println("• onErrorReturnItem: Retorna item específico en caso de error");
        System.out.println("• onErrorResumeNext: Cambia a otro Observable en caso de error");
        System.out.println("• retry: Reintenta la suscripción en caso de error");
        System.out.println("• Un error termina el flujo Observable");
    }
}

