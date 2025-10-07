package com.formadoresit.rxjava.tema4;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 06: Operadores de Manejo de Errores
 * 
 * Operadores para manejar errores en streams reactivos
 * - onErrorReturn: Retorna valor por defecto en caso de error
 * - onErrorResumeNext: Continúa con otro Observable en caso de error
 * - onErrorReturnItem: Versión simplificada de onErrorReturn
 * - retry: Reintenta la operación en caso de error
 * - retryWhen: Reintenta con lógica personalizada
 * - catch: Captura errores y los maneja
 * - onErrorComplete: Convierte errores en completado
 * - timeout: Genera error si no emite en tiempo
 */
public class Ejemplo06_OperadoresError {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 06: Operadores de Manejo de Errores ===\n");

        // 1. onErrorReturn() - Valor por defecto en caso de error
        System.out.println("--- onErrorReturn() ---");
        Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onError(new RuntimeException("Error simulado"));
        })
        .onErrorReturn(error -> {
            System.out.println("  Error capturado: " + error.getMessage());
            return -1; // Valor por defecto
        })
        .subscribe(
            numero -> System.out.println("Número: " + numero),
            error -> System.err.println("Error no manejado: " + error),
            () -> System.out.println("Completado")
        );

        // 2. onErrorReturnItem() - Versión simplificada
        System.out.println("\n--- onErrorReturnItem() ---");
        Observable.create(emitter -> {
            emitter.onNext("A");
            emitter.onError(new RuntimeException("Error"));
        })
        .onErrorReturnItem("Valor por defecto")
        .subscribe(valor -> System.out.println("Valor: " + valor));

        // 3. onErrorResumeNext() - Continúa con otro Observable
        System.out.println("\n--- onErrorResumeNext() ---");
        Observable.create(emitter -> {
            emitter.onNext("Datos originales");
            emitter.onError(new RuntimeException("Error en fuente original"));
        })
        .onErrorResumeNext(Observable.just("Datos de respaldo", "Datos adicionales"))
        .subscribe(dato -> System.out.println("Dato: " + dato));

        // 4. onErrorResumeNext() con lógica condicional
        System.out.println("\n--- onErrorResumeNext() condicional ---");
        Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onError(new RuntimeException("Error de red"));
        })
        .onErrorResumeNext(error -> {
            if (error.getMessage().contains("red")) {
                return Observable.just(999); // Valor de respaldo para error de red
            } else {
                return Observable.error(error); // Re-lanza otros errores
            }
        })
        .subscribe(numero -> System.out.println("Número: " + numero));

        // 5. retry() - Reintenta la operación
        System.out.println("\n--- retry() básico ---");
        Observable.create(emitter -> {
            int intento = (int) (Math.random() * 3) + 1;
            System.out.println("  Intento " + intento);
            if (intento < 3) {
                emitter.onError(new RuntimeException("Error en intento " + intento));
            } else {
                emitter.onNext("¡Éxito en intento " + intento + "!");
                emitter.onComplete();
            }
        })
        .retry(2) // Máximo 2 reintentos
        .subscribe(
            resultado -> System.out.println("Resultado: " + resultado),
            error -> System.err.println("Error final: " + error.getMessage())
        );

        // 6. retry() con condición
        System.out.println("\n--- retry() con condición ---");
        Observable.create(emitter -> {
            int codigoError = (int) (Math.random() * 5) + 1;
            System.out.println("  Código de error: " + codigoError);
            if (codigoError == 1) {
                emitter.onNext("Éxito");
                emitter.onComplete();
            } else {
                emitter.onError(new RuntimeException("Error " + codigoError));
            }
        })
        .retry(3, error -> {
            System.out.println("  Reintentando por: " + error.getMessage());
            return true; // Siempre reintentar
        })
        .subscribe(
            resultado -> System.out.println("Resultado: " + resultado),
            error -> System.err.println("Error final: " + error.getMessage())
        );

        // 7. retryWhen() - Reintento con lógica personalizada
        System.out.println("\n--- retryWhen() ---");
        Observable.create(emitter -> {
            System.out.println("  Intentando operación...");
            emitter.onError(new RuntimeException("Error temporal"));
        })
        .retryWhen(errors -> 
            errors.zipWith(Observable.range(1, 3), (error, intento) -> {
                System.out.println("  Reintento " + intento + " en " + (intento * 100) + "ms");
                return intento;
            })
            .flatMap(intento -> Observable.timer(intento * 100, TimeUnit.MILLISECONDS))
        )
        .subscribe(
            resultado -> System.out.println("Resultado: " + resultado),
            error -> System.err.println("Error final: " + error.getMessage())
        );

        // 8. timeout() - Genera error si no emite en tiempo
        System.out.println("\n--- timeout() ---");
        try {
            Observable.create(emitter -> {
                emitter.onNext("Dato rápido");
                Thread.sleep(2000); // Espera muy larga
                emitter.onNext("Dato lento");
            })
            .timeout(500, TimeUnit.MILLISECONDS)
            .subscribe(dato -> System.out.println("Dato: " + dato));
        } catch (Exception e) {
            System.err.println("Timeout: " + e.getMessage());
        }

        // 9. timeout() con Observable de respaldo
        System.out.println("\n--- timeout() con respaldo ---");
        Observable.create(emitter -> {
            emitter.onNext("Dato rápido");
            Thread.sleep(2000);
            emitter.onNext("Dato lento");
        })
        .timeout(500, TimeUnit.MILLISECONDS, Observable.just("Dato de respaldo"))
        .subscribe(dato -> System.out.println("Dato: " + dato));

        // 10. onErrorComplete() - Convierte errores en completado (usando onErrorResumeNext)
        System.out.println("\n--- onErrorComplete() (simulado) ---");
        Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onError(new RuntimeException("Error ignorado"));
        })
        .onErrorResumeNext(Observable.empty())
        .subscribe(
            numero -> System.out.println("Número: " + numero),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado (error ignorado)")
        );

        // 11. Ejemplo práctico: Llamada a API con reintentos
        System.out.println("\n--- Ejemplo práctico: API con reintentos ---");
        simularLlamadaAPI()
            .retry(3)
            .onErrorReturn(error -> "Error: No se pudo obtener datos")
            .subscribe(resultado -> System.out.println("API Result: " + resultado));

        // 12. Ejemplo práctico: Múltiples fuentes de datos
        System.out.println("\n--- Ejemplo práctico: Múltiples fuentes ---");
        Observable<String> fuente1 = Observable.just("Datos de BD")
            .delay(100, TimeUnit.MILLISECONDS);
        
        Observable<String> fuente2 = Observable.just("Datos de cache")
            .delay(50, TimeUnit.MILLISECONDS);
        
        Observable<String> fuente3 = Observable.just("Datos por defecto")
            .delay(200, TimeUnit.MILLISECONDS);

        // Intenta fuente1, si falla fuente2, si falla fuente3
        fuente1
            .onErrorResumeNext(fuente2)
            .onErrorResumeNext(fuente3)
            .subscribe(datos -> System.out.println("Datos obtenidos: " + datos));

        Thread.sleep(300);

        // 13. Ejemplo avanzado: Circuit breaker pattern
        System.out.println("\n--- Ejemplo avanzado: Circuit Breaker ---");
        Observable<String> servicio = Observable.create(emitter -> {
            double random = Math.random();
            if (random < 0.7) {
                emitter.onNext("Servicio funcionando");
                emitter.onComplete();
            } else {
                emitter.onError(new RuntimeException("Servicio caído"));
            }
        });

        servicio
            .retryWhen(errors -> 
                errors.scan(0, (count, error) -> count + 1)
                    .flatMap(count -> {
                        if (count >= 3) {
                            System.out.println("  Circuit breaker abierto - usando fallback");
                            return Observable.timer(1000, TimeUnit.MILLISECONDS)
                                .flatMap(tick -> Observable.just("Fallback activado"));
                        } else {
                            System.out.println("  Reintento " + count + " en 500ms");
                            return Observable.timer(500, TimeUnit.MILLISECONDS);
                        }
                    })
            )
            .subscribe(resultado -> System.out.println("Resultado: " + resultado));

        Thread.sleep(2000);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• onErrorReturn: Valor por defecto en caso de error");
        System.out.println("• onErrorResumeNext: Continúa con otro Observable");
        System.out.println("• retry: Reintenta la operación");
        System.out.println("• retryWhen: Reintento con lógica personalizada");
        System.out.println("• timeout: Genera error si no emite en tiempo");
        System.out.println("• onErrorComplete: Ignora errores y completa");
        System.out.println("• Circuit Breaker: Patrón para servicios inestables");
    }

    private static Observable<String> simularLlamadaAPI() {
        return Observable.create(emitter -> {
            double random = Math.random();
            if (random < 0.3) {
                emitter.onNext("Datos de API obtenidos");
                emitter.onComplete();
            } else {
                emitter.onError(new RuntimeException("API no disponible"));
            }
        });
    }
}
