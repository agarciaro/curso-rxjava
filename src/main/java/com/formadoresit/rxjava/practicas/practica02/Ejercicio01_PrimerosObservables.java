package com.formadoresit.rxjava.practicas.practica02;

import io.reactivex.Observable;
import java.util.Arrays;
import java.util.List;

/**
 * PRÁCTICA 02 - Ejercicio 2.1: Primeros Observables
 * 
 * Crear Observables básicos y suscribirse con manejo completo
 */
public class Ejercicio01_PrimerosObservables {

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 2.1: Primeros Observables ===\n");

        // 1. Observable que emite números del 1 al 10
        System.out.println("--- Observable de números 1-10 ---");
        Observable<Integer> numeros = Observable.range(1, 10);
        
        numeros.subscribe(
            numero -> System.out.println("onNext: " + numero),
            error -> System.err.println("onError: " + error.getMessage()),
            () -> System.out.println("onComplete\n")
        );

        // 2. Observable de días de la semana
        System.out.println("--- Días de la semana ---");
        Observable<String> diasSemana = Observable.just(
            "Lunes", "Martes", "Miércoles", "Jueves", 
            "Viernes", "Sábado", "Domingo"
        );
        
        diasSemana.subscribe(
            dia -> System.out.println("onNext: " + dia),
            error -> System.err.println("onError: " + error.getMessage()),
            () -> System.out.println("onComplete\n")
        );

        // 3. Observable que lee líneas de un archivo simulado
        System.out.println("--- Lectura de archivo simulado ---");
        Observable<String> lineasArchivo = leerArchivoSimulado("documento.txt");
        
        lineasArchivo.subscribe(
            linea -> System.out.println("onNext: " + linea),
            error -> System.err.println("onError: " + error.getMessage()),
            () -> System.out.println("onComplete: Archivo leído completamente\n")
        );

        // 4. Observable vacío
        System.out.println("--- Observable vacío ---");
        Observable<String> vacio = Observable.empty();
        
        vacio.subscribe(
            item -> System.out.println("onNext: " + item),
            error -> System.err.println("onError: " + error.getMessage()),
            () -> System.out.println("onComplete: Sin emisiones")
        );
    }

    /**
     * Simula la lectura de un archivo retornando un Observable
     */
    private static Observable<String> leerArchivoSimulado(String nombreArchivo) {
        List<String> lineas = Arrays.asList(
            "Línea 1: Encabezado del documento",
            "Línea 2: Contenido principal",
            "Línea 3: Más contenido",
            "Línea 4: Datos importantes",
            "Línea 5: Pie de página"
        );
        
        return Observable.fromIterable(lineas)
            .doOnSubscribe(d -> System.out.println("  Abriendo archivo: " + nombreArchivo))
            .doOnComplete(() -> System.out.println("  Cerrando archivo: " + nombreArchivo));
    }
}

