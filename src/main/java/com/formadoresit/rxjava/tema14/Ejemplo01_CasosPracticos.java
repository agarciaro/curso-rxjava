package com.formadoresit.rxjava.tema14;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 14: Aplicaciones Prácticas
 * Ejemplo 01: Casos de uso reales con RxJava
 */
public class Ejemplo01_CasosPracticos {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 01: Casos Prácticos ===\n");

        // 1. Búsqueda con debounce (típico en autocompletado)
        System.out.println("--- Caso 1: Búsqueda con debounce ---");
        Observable<String> busquedaInput = Observable.create(emitter -> {
            emitter.onNext("j");
            Thread.sleep(50);
            emitter.onNext("ju");
            Thread.sleep(50);
            emitter.onNext("jua");
            Thread.sleep(50);
            emitter.onNext("juan");
            Thread.sleep(500);  // Usuario deja de escribir
            emitter.onComplete();
        });

        busquedaInput
            .debounce(200, TimeUnit.MILLISECONDS)
            .filter(texto -> texto.length() >= 3)
            .distinctUntilChanged()
            .flatMap(termino -> buscarEnAPI(termino))
            .subscribe(
                resultados -> System.out.println("Resultados: " + resultados),
                error -> System.err.println("Error: " + error)
            );

        Thread.sleep(1000);

        // 2. Caché de resultados
        System.out.println("\n--- Caso 2: Caché ---");
        Observable<String> datosConCache = obtenerDatosConCache()
            .cache();  // Cachea el resultado

        System.out.println("Primera llamada:");
        datosConCache.subscribe(dato -> System.out.println("  " + dato));

        Thread.sleep(200);

        System.out.println("Segunda llamada (desde cache):");
        datosConCache.subscribe(dato -> System.out.println("  " + dato));

        Thread.sleep(200);

        // 3. Retry con backoff exponencial
        System.out.println("\n--- Caso 3: Retry con backoff ---");
        obtenerDatosConFallo()
            .retryWhen(errors -> errors
                .zipWith(Observable.range(1, 3), (error, intento) -> intento)
                .flatMap(intento -> {
                    long delay = (long) Math.pow(2, intento) * 100;
                    System.out.println("  Reintentando en " + delay + "ms...");
                    return Observable.timer(delay, TimeUnit.MILLISECONDS);
                })
            )
            .subscribe(
                dato -> System.out.println("Éxito: " + dato),
                error -> System.err.println("Error final: " + error.getMessage())
            );

        Thread.sleep(2000);

        // 4. Procesamiento en paralelo
        System.out.println("\n--- Caso 4: Procesamiento paralelo ---");
        Observable.range(1, 5)
            .flatMap(id -> procesarEnParalelo(id))
            .subscribe(resultado -> System.out.println("Resultado: " + resultado));

        Thread.sleep(500);

        // 5. Combinar múltiples fuentes
        System.out.println("\n--- Caso 5: Combinar APIs ---");
        Observable.zip(
            obtenerUsuario(),
            obtenerPerfil(),
            obtenerConfiguracion(),
            (usuario, perfil, config) -> 
                String.format("Usuario: %s, Perfil: %s, Config: %s", usuario, perfil, config)
        ).subscribe(vista -> System.out.println(vista));

        Thread.sleep(500);

        System.out.println("\n=== PATRONES COMUNES ===");
        System.out.println("• Debounce: Búsquedas, autocompletado");
        System.out.println("• Cache: Evitar llamadas repetidas");
        System.out.println("• Retry con backoff: Resiliencia en APIs");
        System.out.println("• FlatMap paralelo: Procesamiento concurrente");
        System.out.println("• Zip: Combinar resultados de múltiples fuentes");
    }

    private static Observable<String> buscarEnAPI(String termino) {
        return Observable.fromCallable(() -> {
            System.out.println("  Buscando: " + termino);
            Thread.sleep(100);
            return "Resultados para: " + termino;
        }).subscribeOn(Schedulers.io());
    }

    private static Observable<String> obtenerDatosConCache() {
        return Observable.fromCallable(() -> {
            System.out.println("  Obteniendo datos (operación costosa)...");
            Thread.sleep(100);
            return "Datos obtenidos";
        }).subscribeOn(Schedulers.io());
    }

    private static int intentos = 0;
    private static Observable<String> obtenerDatosConFallo() {
        return Observable.fromCallable(() -> {
            intentos++;
            System.out.println("  Intento " + intentos);
            if (intentos < 3) {
                throw new RuntimeException("Error temporal");
            }
            return "Datos obtenidos";
        }).subscribeOn(Schedulers.io());
    }

    private static Observable<String> procesarEnParalelo(int id) {
        return Observable.fromCallable(() -> {
            Thread.sleep(100);
            return "Procesado-" + id + " en " + Thread.currentThread().getName();
        }).subscribeOn(Schedulers.computation());
    }

    private static Observable<String> obtenerUsuario() {
        return Observable.just("Juan").delay(50, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> obtenerPerfil() {
        return Observable.just("Admin").delay(50, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> obtenerConfiguracion() {
        return Observable.just("Dark Theme").delay(50, TimeUnit.MILLISECONDS);
    }
}

