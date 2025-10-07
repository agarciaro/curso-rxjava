package com.formadoresit.rxjava.tema11;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;

/**
 * TEMA 11: Spring WebFlux
 * Ejemplo 01: Introducción a Mono y Flux
 * 
 * Spring WebFlux usa Project Reactor (no RxJava)
 * Mono: 0 o 1 elemento (equivalente a Maybe/Single)
 * Flux: 0..N elementos (equivalente a Observable/Flowable)
 */
public class Ejemplo01_SpringWebFluxIntro {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 01: Mono y Flux (Reactor) ===\n");

        // 1. Mono básico
        System.out.println("--- Mono básico ---");
        Mono<String> mono = Mono.just("Hola Reactor");
        mono.subscribe(valor -> System.out.println("Mono: " + valor));

        // 2. Mono vacío
        System.out.println("\n--- Mono vacío ---");
        Mono.empty()
            .subscribe(
                valor -> System.out.println("Valor: " + valor),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado sin valor")
            );

        // 3. Flux básico
        System.out.println("\n--- Flux básico ---");
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5);
        flux.subscribe(numero -> System.out.println("Flux: " + numero));

        // 4. Flux desde rango
        System.out.println("\n--- Flux.range ---");
        Flux.range(1, 5)
            .map(n -> n * 10)
            .subscribe(numero -> System.out.println("Número: " + numero));

        // 5. Operadores de transformación
        System.out.println("\n--- Operadores ---");
        Flux.just("java", "python", "javascript")
            .map(String::toUpperCase)
            .filter(lang -> lang.length() > 4)
            .subscribe(lang -> System.out.println("Lenguaje: " + lang));

        // 6. Flux con delay
        System.out.println("\n--- Flux con delay ---");
        Flux.interval(Duration.ofMillis(100))
            .take(5)
            .subscribe(n -> System.out.println("Tick: " + n));

        Thread.sleep(600);

        // 7. Mono a Flux
        System.out.println("\n--- Mono a Flux ---");
        Mono.just("Hello")
            .flux()
            .repeat(3)
            .subscribe(msg -> System.out.println(msg));

        // 8. Flux a Mono
        System.out.println("\n--- Flux a Mono (collectList) ---");
        Flux.just(1, 2, 3, 4, 5)
            .collectList()
            .subscribe(lista -> System.out.println("Lista: " + lista));

        // 9. flatMap en Flux
        System.out.println("\n--- flatMap ---");
        Flux.just("Usuario1", "Usuario2")
            .flatMap(usuario -> obtenerDatosUsuario(usuario))
            .subscribe(datos -> System.out.println(datos));

        // 10. zip en Reactor
        System.out.println("\n--- zip ---");
        Mono<String> nombre = Mono.just("Juan");
        Mono<Integer> edad = Mono.just(30);
        
        Mono.zip(nombre, edad)
            .subscribe(tuple -> System.out.println(tuple.getT1() + " tiene " + tuple.getT2() + " años"));

        System.out.println("\n=== DIFERENCIAS RxJava vs Reactor ===");
        System.out.println("RxJava 2:          Reactor (WebFlux):");
        System.out.println("Observable/Flowable -> Flux");
        System.out.println("Single             -> Mono (1 elemento)");
        System.out.println("Maybe              -> Mono (0 o 1)");
        System.out.println("Completable        -> Mono<Void>");
    }

    private static Flux<String> obtenerDatosUsuario(String usuario) {
        return Flux.just(
            usuario + ": dato1",
            usuario + ": dato2"
        );
    }
}

