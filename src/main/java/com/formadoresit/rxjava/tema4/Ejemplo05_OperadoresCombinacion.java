package com.formadoresit.rxjava.tema4;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 05: Operadores de Combinación
 * 
 * Operadores que combinan múltiples Observables
 * - zip: Combina elementos de múltiples Observables
 * - combineLatest: Combina el último elemento de cada Observable
 * - merge: Fusiona múltiples Observables en uno
 * - concat: Concatena Observables secuencialmente
 * - startWith: Agrega elementos al inicio
 * - switchOnNext: Cambia entre Observables
 * - amb: Emite del primer Observable que emita
 */
public class Ejemplo05_OperadoresCombinacion {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 05: Operadores de Combinación ===\n");

        // 1. zip() - Combina elementos de múltiples Observables
        System.out.println("--- zip() básico ---");
        Observable<String> nombres = Observable.just("Juan", "María", "Pedro");
        Observable<Integer> edades = Observable.just(25, 30, 35);
        Observable<String> ciudades = Observable.just("Madrid", "Barcelona", "Valencia");

        Observable.zip(nombres, edades, ciudades, 
            (nombre, edad, ciudad) -> nombre + " (" + edad + " años) de " + ciudad)
            .subscribe(persona -> System.out.println("Persona: " + persona));

        // zip con diferentes tipos
        System.out.println("\n--- zip() con diferentes tipos ---");
        Observable<String> colores = Observable.just("Rojo", "Verde", "Azul");
        Observable<Integer> numeros = Observable.just(1, 2, 3, 4, 5); // Más elementos

        Observable.zip(colores, numeros, 
            (color, numero) -> color + "-" + numero)
            .subscribe(combinacion -> System.out.println("Combinación: " + combinacion));

        // 2. combineLatest() - Combina el último elemento de cada Observable
        System.out.println("\n--- combineLatest() ---");
        Observable<String> fuente1 = Observable.interval(100, TimeUnit.MILLISECONDS)
            .map(i -> "A" + i)
            .take(5);
        
        Observable<String> fuente2 = Observable.interval(150, TimeUnit.MILLISECONDS)
            .map(i -> "B" + i)
            .take(5);

        Observable.combineLatest(fuente1, fuente2, 
            (a, b) -> a + " + " + b)
            .blockingSubscribe(combinacion -> System.out.println("CombineLatest: " + combinacion));

        // 3. merge() - Fusiona múltiples Observables
        System.out.println("\n--- merge() ---");
        Observable<String> obs1 = Observable.just("A", "B", "C");
        Observable<String> obs2 = Observable.just("1", "2", "3");
        Observable<String> obs3 = Observable.just("X", "Y", "Z");

        Observable.merge(obs1, obs2, obs3)
            .subscribe(elemento -> System.out.println("Merge: " + elemento));

        // merge con intervalos
        System.out.println("\n--- merge() con intervalos ---");
        Observable<Long> interval1 = Observable.interval(200, TimeUnit.MILLISECONDS)
            .map(i -> i * 10)
            .take(3);
        
        Observable<Long> interval2 = Observable.interval(300, TimeUnit.MILLISECONDS)
            .map(i -> i * 100)
            .take(3);

        Observable.merge(interval1, interval2)
            .blockingSubscribe(numero -> System.out.println("Número: " + numero));

        // 4. concat() - Concatena Observables secuencialmente
        System.out.println("\n--- concat() ---");
        Observable<String> primera = Observable.just("Primero", "Segundo");
        Observable<String> segunda = Observable.just("Tercero", "Cuarto");
        Observable<String> tercera = Observable.just("Quinto", "Sexto");

        Observable.concat(primera, segunda, tercera)
            .subscribe(elemento -> System.out.println("Concat: " + elemento));

        // concat vs merge - diferencia importante
        System.out.println("\n--- concat() vs merge() ---");
        Observable<String> lento = Observable.just("Lento")
            .delay(500, TimeUnit.MILLISECONDS);
        Observable<String> rapido = Observable.just("Rápido")
            .delay(100, TimeUnit.MILLISECONDS);

        System.out.println("Concat (preserva orden):");
        Observable.concat(lento, rapido)
            .blockingSubscribe(s -> System.out.println("  " + s));

        System.out.println("Merge (no preserva orden):");
        Observable.merge(lento, rapido)
            .blockingSubscribe(s -> System.out.println("  " + s));

        // 5. startWith() - Agrega elementos al inicio
        System.out.println("\n--- startWith() ---");
        Observable.just("B", "C", "D")
            .startWith("A")
            .subscribe(letra -> System.out.println("Letra: " + letra));

        // startWith con múltiples elementos
        Observable.just("C", "D")
            .startWithArray("A", "B")
            .subscribe(letra -> System.out.println("Con array: " + letra));

        // 6. switchOnNext() - Cambia entre Observables
        System.out.println("\n--- switchOnNext() ---");
        Observable<Observable<String>> fuentes = Observable.just(
            Observable.just("Fuente1-A", "Fuente1-B"),
            Observable.just("Fuente2-A", "Fuente2-B"),
            Observable.just("Fuente3-A", "Fuente3-B")
        );

        Observable.switchOnNext(fuentes)
            .subscribe(elemento -> System.out.println("Switch: " + elemento));

        // 7. amb() - Emite del primer Observable que emita
        System.out.println("\n--- amb() ---");
        Observable<String> rapido1 = Observable.timer(100, TimeUnit.MILLISECONDS)
            .map(i -> "Rápido");
        Observable<String> lento1 = Observable.timer(500, TimeUnit.MILLISECONDS)
            .map(i -> "Lento");

        Observable.ambArray(rapido1, lento1)
            .blockingSubscribe(ganador -> System.out.println("Ganador: " + ganador));

        // 8. zip con error handling
        System.out.println("\n--- zip() con manejo de errores ---");
        Observable<String> datos = Observable.just("Dato1", "Dato2", "Dato3");
        Observable<String> procesamiento = Observable.create(emitter -> {
            emitter.onNext("Procesado1");
            emitter.onNext("Procesado2");
            emitter.onError(new RuntimeException("Error en procesamiento"));
        });

        Observable.zip(datos, procesamiento, 
            (dato, proc) -> dato + " -> " + proc)
            .subscribe(
                resultado -> System.out.println("Resultado: " + resultado),
                error -> System.err.println("Error: " + error.getMessage())
            );

        // 9. Ejemplo práctico: Procesamiento de datos en paralelo
        System.out.println("\n--- Ejemplo práctico: Procesamiento paralelo ---");
        Observable<String> usuarios = Observable.just("Juan", "María", "Pedro");
        Observable<String> emails = Observable.just("juan@email.com", "maria@email.com", "pedro@email.com");
        Observable<String> telefonos = Observable.just("123-456", "789-012", "345-678");

        Observable.zip(usuarios, emails, telefonos, 
            (usuario, email, telefono) -> new UsuarioCompleto(usuario, email, telefono))
            .subscribe(usuario -> System.out.println("Usuario completo: " + usuario));

        // 10. Ejemplo avanzado: Sistema de notificaciones
        System.out.println("\n--- Ejemplo avanzado: Sistema de notificaciones ---");
        Observable<String> notificaciones = Observable.interval(200, TimeUnit.MILLISECONDS)
            .map(i -> "Notificación " + (i + 1))
            .take(5);
        
        Observable<String> usuariosOnline = Observable.interval(300, TimeUnit.MILLISECONDS)
            .map(i -> "Usuario " + (i + 1))
            .take(5);

        Observable.combineLatest(notificaciones, usuariosOnline, 
            (notif, usuario) -> "[" + usuario + "] " + notif)
            .blockingSubscribe(mensaje -> System.out.println(mensaje));

        // 11. zip con diferentes velocidades
        System.out.println("\n--- zip() con diferentes velocidades ---");
        Observable<String> rapido2 = Observable.interval(100, TimeUnit.MILLISECONDS)
            .map(i -> "Rápido-" + i)
            .take(5);
        
        Observable<String> lento2 = Observable.interval(200, TimeUnit.MILLISECONDS)
            .map(i -> "Lento-" + i)
            .take(3);

        Observable.zip(rapido2, lento2, 
            (r, l) -> r + " + " + l)
            .blockingSubscribe(combinacion -> System.out.println("Zip: " + combinacion));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• zip: Combina elementos de múltiples Observables (se detiene cuando uno termina)");
        System.out.println("• combineLatest: Combina el último elemento de cada Observable");
        System.out.println("• merge: Fusiona Observables (no preserva orden)");
        System.out.println("• concat: Concatena Observables secuencialmente (preserva orden)");
        System.out.println("• startWith: Agrega elementos al inicio");
        System.out.println("• switchOnNext: Cambia entre Observables");
        System.out.println("• amb: Emite del primer Observable que emita");
    }

    static class UsuarioCompleto {
        String nombre;
        String email;
        String telefono;

        UsuarioCompleto(String nombre, String email, String telefono) {
            this.nombre = nombre;
            this.email = email;
            this.telefono = telefono;
        }

        @Override
        public String toString() {
            return String.format("%s (%s) - %s", nombre, email, telefono);
        }
    }
}
