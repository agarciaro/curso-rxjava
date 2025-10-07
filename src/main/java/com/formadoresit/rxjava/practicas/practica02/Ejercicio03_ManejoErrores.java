package com.formadoresit.rxjava.practicas.practica02;

import io.reactivex.Observable;
import java.util.Random;

/**
 * PRÁCTICA 02 - Ejercicio 2.3: Manejo de Errores
 * 
 * Implementar servicios con diferentes estrategias de manejo de errores
 */
public class Ejercicio03_ManejoErrores {

    private static Random random = new Random();
    private static int intentosDivision = 0;

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 2.3: Manejo de Errores ===\n");

        // 1. obtenerDatosServidor con onErrorReturn
        System.out.println("--- Servicio con onErrorReturn ---");
        obtenerDatosServidor()
            .onErrorReturn(error -> {
                System.out.println("  Error capturado: " + error.getMessage());
                return "Datos por defecto (modo offline)";
            })
            .subscribe(
                datos -> System.out.println("✓ Datos: " + datos),
                error -> System.err.println("✗ Error no manejado: " + error),
                () -> System.out.println("Completado\n")
            );

        // 2. dividir con onErrorResumeNext
        System.out.println("--- División con onErrorResumeNext ---");
        dividir(10, 2).subscribe(
            resultado -> System.out.println("10 / 2 = " + resultado)
        );
        
        dividir(10, 0)
            .onErrorResumeNext(Observable.just(-999))  // Observable alternativo
            .subscribe(
                resultado -> System.out.println("10 / 0 = " + resultado + " (valor alternativo)"),
                error -> System.err.println("Error: " + error),
                () -> System.out.println()
            );

        // 3. procesarArchivo con retry
        System.out.println("--- Procesamiento de archivo con retry ---");
        procesarArchivo("documento.txt")
            .retry(3)
            .subscribe(
                contenido -> System.out.println("✓ Contenido: " + contenido),
                error -> System.err.println("✗ Error después de 3 reintentos: " + error.getMessage()),
                () -> System.out.println("Procesamiento completado\n")
            );

        // 4. Ejemplo complejo: Combinación de estrategias
        System.out.println("--- Ejemplo complejo: retry + onErrorReturn ---");
        intentosDivision = 0;
        
        operacionCompleja()
            .retry(2)  // Reintenta hasta 2 veces
            .onErrorReturn(error -> "Resultado por defecto después de fallar")
            .subscribe(
                resultado -> System.out.println("✓ Resultado final: " + resultado),
                error -> System.err.println("✗ Error: " + error),
                () -> System.out.println("Completado")
            );
    }

    /**
     * Simula obtención de datos de servidor que puede fallar
     */
    private static Observable<String> obtenerDatosServidor() {
        return Observable.create(emitter -> {
            System.out.println("  Conectando al servidor...");
            
            // Simular fallo aleatorio (50% de probabilidad)
            if (random.nextBoolean()) {
                emitter.onError(new RuntimeException("Error de conexión al servidor"));
            } else {
                emitter.onNext("Datos del servidor");
                emitter.onComplete();
            }
        });
    }

    /**
     * División que lanza error si divisor es 0
     */
    private static Observable<Integer> dividir(int dividendo, int divisor) {
        return Observable.create(emitter -> {
            if (divisor == 0) {
                emitter.onError(new ArithmeticException("División por cero"));
            } else {
                emitter.onNext(dividendo / divisor);
                emitter.onComplete();
            }
        });
    }

    /**
     * Simula procesamiento de archivo que puede fallar
     */
    private static Observable<String> procesarArchivo(String ruta) {
        return Observable.create(emitter -> {
            System.out.println("  Intento de procesar: " + ruta);
            
            // Simular que falla las primeras 2 veces
            if (random.nextInt(3) < 2) {
                emitter.onError(new RuntimeException("Archivo temporalmente no disponible"));
            } else {
                emitter.onNext("Contenido del archivo " + ruta);
                emitter.onComplete();
            }
        });
    }

    /**
     * Operación compleja que falla varias veces antes de tener éxito
     */
    private static Observable<String> operacionCompleja() {
        return Observable.create(emitter -> {
            intentosDivision++;
            System.out.println("  Intento de operación compleja #" + intentosDivision);
            
            if (intentosDivision < 3) {
                emitter.onError(new RuntimeException("Operación temporalmente fallida"));
            } else {
                emitter.onNext("Operación exitosa");
                emitter.onComplete();
            }
        });
    }
}

