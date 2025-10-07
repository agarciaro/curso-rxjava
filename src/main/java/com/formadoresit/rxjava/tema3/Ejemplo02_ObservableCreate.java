package com.formadoresit.rxjava.tema3;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

/**
 * TEMA 3: Observables y Observers
 * Ejemplo 02: Observable.create()
 * 
 * create() permite crear Observables personalizados con control total
 * sobre las emisiones
 */
public class Ejemplo02_ObservableCreate {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 02: Observable.create() ===\n");

        // Ejemplo 1: Observable básico con create
        System.out.println("--- Observable básico ---");
        Observable<String> observable = Observable.create(emitter -> {
            emitter.onNext("Primer elemento");
            emitter.onNext("Segundo elemento");
            emitter.onNext("Tercer elemento");
            emitter.onComplete();
        });

        observable.subscribe(
            item -> System.out.println("Recibido: " + item),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado")
        );

        // Ejemplo 2: Observable con lógica condicional
        System.out.println("\n--- Observable con lógica ---");
        Observable<Integer> numerosCondicionales = Observable.create(emitter -> {
            for (int i = 1; i <= 10; i++) {
                if (emitter.isDisposed()) {
                    return; // Detener si se cancela la suscripción
                }
                
                if (i % 2 == 0) {
                    emitter.onNext(i);
                }
            }
            emitter.onComplete();
        });

        numerosCondicionales.subscribe(
            numero -> System.out.println("Número par: " + numero),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado")
        );

        // Ejemplo 3: Observable que emite con error
        System.out.println("\n--- Observable con error ---");
        Observable<Integer> observableConError = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            
            // Simular error
            if (true) {
                emitter.onError(new RuntimeException("Error intencional"));
                return;
            }
            
            emitter.onNext(3); // No se ejecutará
            emitter.onComplete();
        });

        observableConError.subscribe(
            numero -> System.out.println("Número: " + numero),
            error -> System.err.println("Capturado error: " + error.getMessage()),
            () -> System.out.println("Completado")
        );

        // Ejemplo 4: Observable que lee datos de una fuente externa
        System.out.println("\n--- Observable leyendo datos ---");
        Observable<String> lectorDatos = crearLectorDatos();
        lectorDatos.subscribe(
            dato -> System.out.println("Dato leído: " + dato),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Lectura completada")
        );

        // Ejemplo 5: Observable con manejo de recursos
        System.out.println("\n--- Observable con recursos ---");
        Observable<String> observableConRecursos = Observable.create(emitter -> {
            // Adquirir recurso
            RecursoSimulado recurso = new RecursoSimulado();
            recurso.abrir();
            
            // Configurar limpieza
            emitter.setCancellable(() -> {
                System.out.println("  Limpiando recursos...");
                recurso.cerrar();
            });
            
            // Emitir datos
            emitter.onNext(recurso.leerDato());
            emitter.onNext(recurso.leerDato());
            emitter.onComplete();
        });

        observableConRecursos.subscribe(
            dato -> System.out.println("Dato: " + dato),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado")
        );

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• create(): Crea Observables personalizados");
        System.out.println("• ObservableEmitter: Interfaz para emitir elementos");
        System.out.println("• onNext(): Emite un elemento");
        System.out.println("• onComplete(): Indica finalización exitosa");
        System.out.println("• onError(): Indica un error (termina el flujo)");
        System.out.println("• isDisposed(): Verifica si se canceló la suscripción");
        System.out.println("• setCancellable(): Define lógica de limpieza");
    }

    private static Observable<String> crearLectorDatos() {
        return Observable.create(emitter -> {
            String[] datos = {"Archivo1.txt", "Archivo2.txt", "Archivo3.txt"};
            
            for (String archivo : datos) {
                if (emitter.isDisposed()) {
                    break;
                }
                emitter.onNext(archivo);
            }
            
            emitter.onComplete();
        });
    }

    // Clase simulada de recurso
    static class RecursoSimulado {
        private int contador = 0;

        public void abrir() {
            System.out.println("  Recurso abierto");
        }

        public String leerDato() {
            contador++;
            return "Dato #" + contador;
        }

        public void cerrar() {
            System.out.println("  Recurso cerrado");
        }
    }
}

