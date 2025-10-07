package com.formadoresit.rxjava.tema12;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * TEMA 12: Creación de Flujos
 * Ejemplo 01: Observable.create avanzado
 * 
 * Creación de Observables personalizados con control total
 */
public class Ejemplo01_ObservableCreate {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 01: Creación Avanzada de Flujos ===\n");

        // 1. Observable desde evento personalizado
        System.out.println("--- Observable desde eventos ---");
        crearObservableDeEventos()
            .subscribe(
                evento -> System.out.println("Evento: " + evento),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );

        Thread.sleep(500);

        // 2. Observable con limpieza de recursos
        System.out.println("\n--- Observable con recursos ---");
        crearObservableConRecursos()
            .take(3)
            .subscribe(
                dato -> System.out.println("Dato: " + dato),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );

        Thread.sleep(500);

        // 3. Observable desde callback
        System.out.println("\n--- Observable desde callback ---");
        desdeCallback((resultado, error) -> {
            if (error != null) {
                System.err.println("Error: " + error);
            } else {
                System.out.println("Resultado: " + resultado);
            }
        }).subscribe(
            res -> System.out.println("Observable: " + res),
            err -> System.err.println("Error: " + err)
        );

        Thread.sleep(200);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• create(): Control total sobre emisiones");
        System.out.println("• setCancellable/setDisposable: Limpieza de recursos");
        System.out.println("• Útil para integrar APIs callback");
        System.out.println("• Importante manejar backpressure en Flowable");
    }

    private static Observable<String> crearObservableDeEventos() {
        return Observable.create(emitter -> {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            
            emitter.setCancellable(() -> {
                System.out.println("  Limpiando executor...");
                executor.shutdown();
            });

            executor.scheduleAtFixedRate(() -> {
                if (!emitter.isDisposed()) {
                    emitter.onNext("Evento-" + System.currentTimeMillis());
                }
            }, 0, 100, TimeUnit.MILLISECONDS);

            // Completar después de 400ms
            executor.schedule(() -> {
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }, 400, TimeUnit.MILLISECONDS);
        });
    }

    private static Observable<String> crearObservableConRecursos() {
        return Observable.create(emitter -> {
            RecursoSimulado recurso = new RecursoSimulado();
            
            emitter.setDisposable(io.reactivex.disposables.Disposables.fromRunnable(() -> {
                System.out.println("  Cerrando recurso...");
                recurso.cerrar();
            }));

            for (int i = 1; i <= 5 && !emitter.isDisposed(); i++) {
                emitter.onNext(recurso.leerDato(i));
                Thread.sleep(100);
            }

            if (!emitter.isDisposed()) {
                emitter.onComplete();
            }
        });
    }

    private static Observable<String> desdeCallback(Callback callback) {
        return Observable.create(emitter -> {
            // Simular operación asíncrona con callback
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    String resultado = "Resultado del callback";
                    
                    if (!emitter.isDisposed()) {
                        emitter.onNext(resultado);
                        emitter.onComplete();
                    }
                    
                    callback.onResultado(resultado, null);
                } catch (Exception e) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                    callback.onResultado(null, e);
                }
            }).start();
        });
    }

    interface Callback {
        void onResultado(String resultado, Exception error);
    }

    static class RecursoSimulado {
        public String leerDato(int id) {
            return "Dato-" + id;
        }

        public void cerrar() {
            System.out.println("  Recurso cerrado");
        }
    }
}

