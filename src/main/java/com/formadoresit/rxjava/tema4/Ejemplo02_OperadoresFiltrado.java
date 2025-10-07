package com.formadoresit.rxjava.tema4;

import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 02: Operadores de Filtrado
 * 
 * Los operadores de filtrado seleccionan qué elementos emitir
 * - filter: Filtra por predicado
 * - take: Toma primeros N elementos
 * - takeLast: Toma últimos N elementos
 * - skip: Salta primeros N elementos
 * - distinct: Elimina duplicados
 * - distinctUntilChanged: Elimina duplicados consecutivos
 * - debounce: Emite solo después de un período de inactividad
 * - throttle: Limita la tasa de emisión
 */
public class Ejemplo02_OperadoresFiltrado {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 02: Operadores de Filtrado ===\n");

        // 1. filter() - Filtra por condición
        System.out.println("--- filter() ---");
        Observable.range(1, 10)
            .filter(n -> n % 2 == 0)
            .subscribe(par -> System.out.println("Par: " + par));

        // 2. take() - Toma primeros N elementos
        System.out.println("\n--- take() ---");
        Observable.range(1, 100)
            .take(5)
            .subscribe(n -> System.out.println("Primeros 5: " + n));

        // 3. takeLast() - Toma últimos N elementos
        System.out.println("\n--- takeLast() ---");
        Observable.range(1, 10)
            .takeLast(3)
            .subscribe(n -> System.out.println("Últimos 3: " + n));

        // 4. takeWhile() - Toma mientras se cumpla condición
        System.out.println("\n--- takeWhile() ---");
        Observable.range(1, 10)
            .takeWhile(n -> n < 6)
            .subscribe(n -> System.out.println("Menor que 6: " + n));

        // 5. takeUntil() - Toma hasta que otro Observable emita
        System.out.println("\n--- takeUntil() ---");
        Observable<Long> fuente = Observable.interval(100, TimeUnit.MILLISECONDS);
        Observable<Long> señalParada = Observable.timer(500, TimeUnit.MILLISECONDS);
        
        fuente.takeUntil(señalParada)
            .blockingSubscribe(n -> System.out.println("Número: " + n));

        // 6. skip() - Salta primeros N elementos
        System.out.println("\n--- skip() ---");
        Observable.range(1, 10)
            .skip(5)
            .subscribe(n -> System.out.println("Después de saltar 5: " + n));

        // 7. skipLast() - Salta últimos N elementos
        System.out.println("\n--- skipLast() ---");
        Observable.range(1, 10)
            .skipLast(3)
            .subscribe(n -> System.out.println("Sin últimos 3: " + n));

        // 8. skipWhile() - Salta mientras se cumpla condición
        System.out.println("\n--- skipWhile() ---");
        Observable.just(1, 2, 3, 4, 5, 4, 3, 2, 1)
            .skipWhile(n -> n < 4)
            .subscribe(n -> System.out.println("Número: " + n));

        // 9. distinct() - Elimina duplicados
        System.out.println("\n--- distinct() ---");
        Observable.just(1, 2, 2, 3, 3, 3, 4, 5, 5)
            .distinct()
            .subscribe(n -> System.out.println("Único: " + n));

        // 10. distinctUntilChanged() - Elimina duplicados consecutivos
        System.out.println("\n--- distinctUntilChanged() ---");
        Observable.just(1, 1, 2, 2, 2, 3, 1, 1, 4)
            .distinctUntilChanged()
            .subscribe(n -> System.out.println("Sin duplicados consecutivos: " + n));

        // 11. elementAt() - Obtiene elemento en posición específica
        System.out.println("\n--- elementAt() ---");
        Observable.just("A", "B", "C", "D", "E")
            .elementAt(2)  // Índice 2 = "C"
            .subscribe(letra -> System.out.println("Elemento en índice 2: " + letra));

        // 12. first() / last() - Primer o último elemento
        System.out.println("\n--- first() y last() ---");
        Observable.just(10, 20, 30, 40, 50)
            .first(0)  // Con valor por defecto
            .subscribe(primero -> System.out.println("Primero: " + primero));

        Observable.just(10, 20, 30, 40, 50)
            .last(0)
            .subscribe(ultimo -> System.out.println("Último: " + ultimo));

        // 13. ignoreElements() - Ignora todos los elementos, solo completa
        System.out.println("\n--- ignoreElements() ---");
        Observable.just(1, 2, 3, 4, 5)
            .ignoreElements()
            .subscribe(
                () -> System.out.println("Completado (sin recibir elementos)"),
                error -> System.err.println("Error: " + error)
            );

        // 14. sample() / throttleLast() - Muestra periódicamente
        System.out.println("\n--- sample() ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(20)
            .sample(300, TimeUnit.MILLISECONDS)
            .blockingSubscribe(n -> System.out.println("Sample: " + n));

        // 15. debounce() - Emite después de período de inactividad
        System.out.println("\n--- debounce() (simulado) ---");
        Observable.create(emitter -> {
            emitter.onNext("A");
            Thread.sleep(100);
            emitter.onNext("B");
            Thread.sleep(100);
            emitter.onNext("C");
            Thread.sleep(500);  // Pausa larga
            emitter.onNext("D");
            emitter.onComplete();
        })
        .debounce(200, TimeUnit.MILLISECONDS)
        .blockingSubscribe(letra -> System.out.println("Debounced: " + letra));

        // Ejemplo práctico: Búsqueda con debounce
        System.out.println("\n--- Ejemplo práctico: Búsqueda ---");
        Observable.just("j", "ju", "jua", "juan")
            .debounce(100, TimeUnit.MILLISECONDS)
            .filter(texto -> texto.length() >= 3)
            .distinct()
            .blockingSubscribe(busqueda -> System.out.println("Buscar: " + busqueda));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• filter: Filtra por condición");
        System.out.println("• take/skip: Toma o salta N elementos");
        System.out.println("• distinct: Elimina duplicados");
        System.out.println("• distinctUntilChanged: Elimina duplicados consecutivos");
        System.out.println("• debounce: Útil para búsquedas (espera inactividad)");
        System.out.println("• sample/throttle: Limita tasa de emisión");
    }
}

