package com.formadoresit.rxjava.tema4;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 10: Operadores Avanzados
 * 
 * Operadores avanzados para transformaciones complejas
 * - groupBy: Agrupa elementos por clave
 * - flatMap: Transforma y aplana Observables (no garantiza orden)
 * - concatMap: Como flatMap pero preserva el orden
 * - switchMap: Cancela el anterior y cambia al nuevo
 * - mergeMap: Alias de flatMap
 * - expand: Expande recursivamente
 * - switchIfEmpty: Cambia a otro Observable si está vacío
 * - defaultIfEmpty: Valor por defecto si está vacío
 * - startWith: Agrega elementos al inicio
 * - concatWith: Concatena con otro Observable
 */
public class Ejemplo10_OperadoresAvanzados {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 10: Operadores Avanzados ===\n");

        // 1. groupBy() - Agrupa elementos por clave
        System.out.println("--- groupBy() básico ---");
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .groupBy(n -> n % 2 == 0 ? "Par" : "Impar")
            .subscribe(grupo -> {
                grupo.toList().subscribe(lista -> 
                    System.out.println(grupo.getKey() + ": " + lista)
                );
            });

        // groupBy con objetos complejos
        System.out.println("\n--- groupBy() con objetos ---");
        Observable.just(
            new Persona("Juan", 25, "Madrid"),
            new Persona("María", 30, "Barcelona"),
            new Persona("Pedro", 25, "Valencia"),
            new Persona("Ana", 30, "Madrid"),
            new Persona("Luis", 25, "Barcelona")
        )
        .groupBy(p -> p.ciudad)
        .subscribe(grupo -> {
            grupo.toList().subscribe(personas -> 
                System.out.println(grupo.getKey() + ": " + personas)
            );
        });

        // 2. flatMap() - Transforma y aplana Observables
        System.out.println("\n--- flatMap() básico ---");
        Observable.just("A", "B", "C")
            .flatMap(letra -> Observable.just(letra + "1", letra + "2", letra + "3"))
            .subscribe(resultado -> System.out.println("flatMap: " + resultado));

        // flatMap con operaciones asíncronas
        System.out.println("\n--- flatMap() asíncrono ---");
        Observable.just(1, 2, 3)
            .flatMap(numero -> obtenerDatosAsync(numero))
            .subscribe(dato -> System.out.println("Dato async: " + dato));

        // flatMap con error handling
        System.out.println("\n--- flatMap() con error handling ---");
        Observable.just(1, 2, 3, 4, 5)
            .flatMap(numero -> {
                if (numero == 3) {
                    return Observable.error(new RuntimeException("Error en " + numero));
                }
                return Observable.just("Dato " + numero);
            })
            .onErrorResumeNext(Observable.just("Error manejado"))
            .subscribe(dato -> System.out.println("Dato: " + dato));

        // 3. concatMap() - Como flatMap pero preserva el orden
        System.out.println("\n--- concatMap() (preserva orden) ---");
        Observable.just(1, 2, 3)
            .concatMap(numero -> obtenerDatosAsync(numero))
            .subscribe(dato -> System.out.println("Dato ordenado: " + dato));

        // 4. switchMap() - Cancela el anterior y cambia al nuevo
        System.out.println("\n--- switchMap() ---");
        Observable.just("Búsqueda1", "Búsqueda2", "Búsqueda3")
            .switchMap(busqueda -> simularBusqueda(busqueda))
            .subscribe(resultado -> System.out.println("Resultado: " + resultado));

        // switchMap con intervalos
        System.out.println("\n--- switchMap() con intervalos ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(5)
            .switchMap(tick -> Observable.timer(200, TimeUnit.MILLISECONDS)
                .map(t -> "Resultado " + tick))
            .blockingSubscribe(resultado -> System.out.println("SwitchMap: " + resultado));

        // 5. expand() - Expande recursivamente (usando flatMap como alternativa)
        System.out.println("\n--- expand() (simulado con flatMap) ---");
        Observable.just(1)
            .flatMap(numero -> {
                if (numero < 10) {
                    return Observable.just(numero * 2);
                } else {
                    return Observable.empty();
                }
            })
            .subscribe(numero -> System.out.println("Expand: " + numero));

        // 6. switchIfEmpty() - Cambia a otro Observable si está vacío
        System.out.println("\n--- switchIfEmpty() ---");
        Observable.empty()
            .switchIfEmpty(Observable.just("Datos de respaldo"))
            .subscribe(dato -> System.out.println("Dato: " + dato));

        // switchIfEmpty con condición
        System.out.println("\n--- switchIfEmpty() con condición ---");
        Observable.just(1, 2, 3)
            .filter(n -> n > 10) // Filtro que no deja pasar nada
            .switchIfEmpty(Observable.just(100, 200, 300))
            .subscribe(numero -> System.out.println("Número: " + numero));

        // 7. defaultIfEmpty() - Valor por defecto si está vacío
        System.out.println("\n--- defaultIfEmpty() ---");
        Observable.empty()
            .defaultIfEmpty("Valor por defecto")
            .subscribe(valor -> System.out.println("Valor: " + valor));

        // 8. startWith() - Agrega elementos al inicio
        System.out.println("\n--- startWith() ---");
        Observable.just("B", "C", "D")
            .startWith("A")
            .subscribe(letra -> System.out.println("Letra: " + letra));

        // startWith con múltiples elementos
        Observable.just("C", "D")
            .startWithArray("A", "B")
            .subscribe(letra -> System.out.println("Con array: " + letra));

        // 9. concatWith() - Concatena con otro Observable
        System.out.println("\n--- concatWith() ---");
        Observable.just("Primero", "Segundo")
            .concatWith(Observable.just("Tercero", "Cuarto"))
            .subscribe(elemento -> System.out.println("Concat: " + elemento));

        // 10. Ejemplo práctico: Búsqueda con switchMap
        System.out.println("\n--- Ejemplo práctico: Búsqueda con switchMap ---");
        Observable.just("j", "ju", "jua", "juan", "juan", "juanp", "juanpe", "juanper", "juanperez")
            .debounce(200, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMap(termino -> simularBusqueda(termino))
            .blockingSubscribe(resultado -> System.out.println("Búsqueda: " + resultado));

        // 11. Ejemplo práctico: Procesamiento de archivos
        System.out.println("\n--- Ejemplo práctico: Procesamiento de archivos ---");
        Observable.just("archivo1.txt", "archivo2.txt", "archivo3.txt")
            .flatMap(archivo -> procesarArchivo(archivo))
            .subscribe(linea -> System.out.println("Línea: " + linea));

        // 12. Ejemplo práctico: Agregación de datos
        System.out.println("\n--- Ejemplo práctico: Agregación de datos ---");
        Observable.just(
            new Venta("Producto A", 100, "Enero"),
            new Venta("Producto B", 200, "Enero"),
            new Venta("Producto A", 150, "Febrero"),
            new Venta("Producto C", 300, "Febrero"),
            new Venta("Producto B", 250, "Marzo")
        )
        .groupBy(venta -> venta.mes)
        .subscribe(grupo -> {
            grupo.toList().subscribe(ventas -> {
                double total = ventas.stream().mapToDouble(v -> v.precio).sum();
                System.out.println(grupo.getKey() + ": " + ventas.size() + " ventas, Total: $" + total);
            });
        });

        // 13. Ejemplo avanzado: Pipeline de procesamiento
        System.out.println("\n--- Ejemplo avanzado: Pipeline de procesamiento ---");
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .groupBy(n -> n % 2 == 0 ? "Par" : "Impar")
            .flatMap(grupo -> 
                grupo
                    .map(n -> n * 10)
                    .filter(n -> n > 20)
                    .toList()
                    .toObservable()
                    .map(lista -> grupo.getKey() + ": " + lista)
            )
            .subscribe(resultado -> System.out.println("Pipeline: " + resultado));

        // 14. Ejemplo avanzado: Circuit breaker con switchMap
        System.out.println("\n--- Ejemplo avanzado: Circuit breaker ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(10)
            .switchMap(tick -> {
                if (tick % 3 == 0) {
                    return Observable.error(new RuntimeException("Error simulado"));
                }
                return Observable.just("Dato " + tick)
                    .delay(50, TimeUnit.MILLISECONDS);
            })
            .onErrorResumeNext(Observable.just("Fallback"))
            .blockingSubscribe(resultado -> System.out.println("Circuit breaker: " + resultado));

        // 15. Ejemplo avanzado: Procesamiento en paralelo
        System.out.println("\n--- Ejemplo avanzado: Procesamiento en paralelo ---");
        Observable.just("Tarea1", "Tarea2", "Tarea3", "Tarea4", "Tarea5")
            .flatMap(tarea -> 
                Observable.fromCallable(() -> {
                    Thread.sleep(200); // Simula trabajo
                    return tarea + " completada";
                })
                .subscribeOn(Schedulers.io())
            )
            .subscribe(resultado -> System.out.println("Paralelo: " + resultado));

        Thread.sleep(1000);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• groupBy: Agrupa elementos por clave");
        System.out.println("• flatMap: Transforma y aplana (no garantiza orden)");
        System.out.println("• concatMap: Como flatMap pero preserva el orden");
        System.out.println("• switchMap: Cancela anterior, útil para búsquedas");
        System.out.println("• expand: Expande recursivamente");
        System.out.println("• switchIfEmpty: Cambia a otro Observable si está vacío");
        System.out.println("• defaultIfEmpty: Valor por defecto si está vacío");
        System.out.println("• startWith: Agrega elementos al inicio");
        System.out.println("• concatWith: Concatena con otro Observable");
        System.out.println("• Circuit breaker: Patrón para servicios inestables");
        System.out.println("• Pipeline: Cadena de procesamiento de datos");
    }

    private static Observable<String> obtenerDatosAsync(int id) {
        return Observable.fromCallable(() -> {
            Thread.sleep(100); // Simula operación asíncrona
            return "Dato-" + id;
        }).subscribeOn(Schedulers.io());
    }

    private static Observable<String> simularBusqueda(String termino) {
        return Observable.timer(100, TimeUnit.MILLISECONDS)
            .map(tick -> "Resultado de: " + termino);
    }

    private static Observable<String> procesarArchivo(String archivo) {
        return Observable.just(
            "Línea 1 de " + archivo,
            "Línea 2 de " + archivo,
            "Línea 3 de " + archivo
        );
    }

    static class Persona {
        String nombre;
        int edad;
        String ciudad;

        Persona(String nombre, int edad, String ciudad) {
            this.nombre = nombre;
            this.edad = edad;
            this.ciudad = ciudad;
        }

        @Override
        public String toString() {
            return nombre + " (" + edad + ")";
        }
    }

    static class Venta {
        String producto;
        double precio;
        String mes;

        Venta(String producto, double precio, String mes) {
            this.producto = producto;
            this.precio = precio;
            this.mes = mes;
        }
    }
}
