package com.formadoresit.rxjava.tema5;

import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 5: Combinando Observables
 * Ejemplo 04: combineLatest() - Combina los valores más recientes
 * 
 * combineLatest emite cuando CUALQUIERA de los Observables emite,
 * combinando con el último valor de los demás
 */
public class Ejemplo04_CombineLatest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 04: combineLatest() ===\n");

        // 1. combineLatest básico
        System.out.println("--- combineLatest() básico ---");
        Observable<String> letras = Observable.just("A", "B", "C");
        Observable<Integer> numeros = Observable.just(1, 2, 3);
        
        Observable.combineLatest(letras, numeros, 
            (letra, numero) -> letra + numero)
            .subscribe(resultado -> System.out.println(resultado));

        // 2. combineLatest con timing
        System.out.println("\n--- combineLatest() con timing ---");
        Observable<String> fuente1 = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> "F1-" + i);
            
        Observable<String> fuente2 = Observable.interval(150, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> "F2-" + i);
        
        Observable.combineLatest(fuente1, fuente2, 
            (v1, v2) -> v1 + " + " + v2)
            .blockingSubscribe(resultado -> System.out.println(resultado));

        // 3. combineLatest con tres fuentes
        System.out.println("\n--- combineLatest() con tres fuentes ---");
        Observable<Integer> temp = Observable.just(20, 22, 25);
        Observable<Integer> humedad = Observable.just(60, 65);
        Observable<String> ubicacion = Observable.just("Sala1", "Sala2", "Sala3");
        
        Observable.combineLatest(temp, humedad, ubicacion,
            (t, h, u) -> u + ": " + t + "°C, " + h + "%")
            .subscribe(reporte -> System.out.println(reporte));

        // Ejemplo práctico: Formulario reactivo
        System.out.println("\n--- Ejemplo práctico: Validación formulario ---");
        Observable<String> nombreInput = Observable.just("", "J", "Ju", "Juan");
        Observable<String> emailInput = Observable.just("", "j", "j@", "j@mail.com");
        
        Observable.combineLatest(nombreInput, emailInput,
            (nombre, email) -> {
                boolean nombreValido = nombre.length() >= 3;
                boolean emailValido = email.contains("@") && email.contains(".");
                return String.format("Nombre: %s (%s), Email: %s (%s)", 
                    nombre, nombreValido ? "✓" : "✗",
                    email, emailValido ? "✓" : "✗");
            })
            .subscribe(estado -> System.out.println(estado));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• combineLatest: Emite cuando cualquier fuente emite");
        System.out.println("• Usa el valor más reciente de cada Observable");
        System.out.println("• Requiere que todos hayan emitido al menos una vez");
        System.out.println("• Útil para formularios reactivos y estados combinados");
    }
}

