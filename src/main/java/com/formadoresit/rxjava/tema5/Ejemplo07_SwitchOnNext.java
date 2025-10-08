package com.formadoresit.rxjava.tema5;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 5: Combinando Observables
 * Ejemplo 07: switchOnNext() - Cambiar entre Observables
 * 
 * switchOnNext cancela el Observable anterior cuando llega uno nuevo
 * Solo emite del Observable más reciente
 * Útil para búsquedas, navegación, y operaciones que se cancelan
 */
public class Ejemplo07_SwitchOnNext {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 07: switchOnNext() ===\n");

        // 1. switchOnNext básico
        System.out.println("--- switchOnNext() básico ---");
        Observable<Observable<String>> fuentes = Observable.just(
            Observable.just("A1", "A2", "A3"),
            Observable.just("B1", "B2", "B3"),
            Observable.just("C1", "C2", "C3")
        );
        
        Observable.switchOnNext(fuentes)
            .subscribe(item -> System.out.println("Item: " + item));

        // 2. switchOnNext con timing
        System.out.println("\n--- switchOnNext() con timing ---");
        Observable<Observable<String>> fuentesTemporales = Observable.interval(500, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(5)
                .map(j -> "Fuente" + i + "-" + j));
        
        Observable.switchOnNext(fuentesTemporales)
            .blockingSubscribe(item -> System.out.println("Temporal: " + item));

        // 3. switchOnNext con Subject
        System.out.println("\n--- switchOnNext() con Subject ---");
        PublishSubject<Observable<String>> switchSubject = PublishSubject.create();
        
        Observable.switchOnNext(switchSubject)
            .subscribe(item -> System.out.println("Subject: " + item));
        
        // Emitir diferentes fuentes
        switchSubject.onNext(Observable.just("Primera fuente"));
        Thread.sleep(200);
        switchSubject.onNext(Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> "Segunda-" + i));
        Thread.sleep(200);
        switchSubject.onNext(Observable.just("Tercera fuente"));

        // 4. Ejemplo práctico: Búsqueda con cancelación
        System.out.println("\n--- Ejemplo práctico: Búsqueda ---");
        PublishSubject<String> busquedaSubject = PublishSubject.create();
        
        Observable.switchOnNext(
            busquedaSubject
                .map(termino -> buscarProductos(termino))
        )
        .subscribe(producto -> System.out.println("Resultado: " + producto));
        
        // Simular búsquedas rápidas
        busquedaSubject.onNext("laptop");
        Thread.sleep(100);
        busquedaSubject.onNext("mouse");
        Thread.sleep(100);
        busquedaSubject.onNext("teclado");

        // 5. Ejemplo práctico: Navegación de páginas
        System.out.println("\n--- Ejemplo práctico: Navegación ---");
        PublishSubject<String> navegacionSubject = PublishSubject.create();
        
        Observable.switchOnNext(
            navegacionSubject
                .map(pagina -> cargarPagina(pagina))
        )
        .subscribe(contenido -> System.out.println("Página: " + contenido));
        
        // Simular navegación
        navegacionSubject.onNext("inicio");
        Thread.sleep(200);
        navegacionSubject.onNext("productos");
        Thread.sleep(200);
        navegacionSubject.onNext("contacto");

        // 6. Ejemplo práctico: Streaming de datos
        System.out.println("\n--- Ejemplo práctico: Streaming ---");
        PublishSubject<String> canalSubject = PublishSubject.create();
        
        Observable.switchOnNext(
            canalSubject
                .map(canal -> obtenerStream(canal))
        )
        .subscribe(dato -> System.out.println("Stream: " + dato));
        
        // Cambiar entre canales
        canalSubject.onNext("deportes");
        Thread.sleep(300);
        canalSubject.onNext("noticias");
        Thread.sleep(300);
        canalSubject.onNext("musica");

        // 7. Ejemplo práctico: Múltiples APIs
        System.out.println("\n--- Ejemplo práctico: Múltiples APIs ---");
        PublishSubject<String> apiSubject = PublishSubject.create();
        
        Observable.switchOnNext(
            apiSubject
                .map(servicio -> llamarAPI(servicio))
        )
        .subscribe(resultado -> System.out.println("API: " + resultado));
        
        // Probar diferentes APIs
        apiSubject.onNext("usuarios");
        Thread.sleep(150);
        apiSubject.onNext("productos");
        Thread.sleep(150);
        apiSubject.onNext("pedidos");

        // 8. switchOnNext con error handling
        System.out.println("\n--- switchOnNext() con error handling ---");
        Observable<Observable<String>> fuentesConError = Observable.just(
            Observable.just("OK"),
            Observable.error(new RuntimeException("Error en fuente")),
            Observable.just("Recuperado")
        );
        
        Observable.switchOnNext(fuentesConError)
            .subscribe(
                item -> System.out.println("Éxito: " + item),
                error -> System.out.println("Error: " + error.getMessage()),
                () -> System.out.println("Completado")
            );

        // 9. switchOnNext con delay
        System.out.println("\n--- switchOnNext() con delay ---");
        Observable<Observable<String>> fuentesConDelay = Observable.interval(300, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> Observable.just("Delayed-" + i)
                .delay(100, TimeUnit.MILLISECONDS));
        
        Observable.switchOnNext(fuentesConDelay)
            .blockingSubscribe(item -> System.out.println("Delayed: " + item));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• switchOnNext: Cancela el Observable anterior");
        System.out.println("• Solo emite del Observable más reciente");
        System.out.println("• Útil para búsquedas y navegación");
        System.out.println("• Evita condiciones de carrera");
        System.out.println("• Ideal para operaciones que se cancelan");
    }

    private static Observable<String> buscarProductos(String termino) {
        return Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> termino + " resultado " + (i + 1));
    }

    private static Observable<String> cargarPagina(String pagina) {
        return Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(2)
            .map(i -> "Contenido de " + pagina + " parte " + (i + 1));
    }

    private static Observable<String> obtenerStream(String canal) {
        return Observable.interval(80, TimeUnit.MILLISECONDS)
            .take(4)
            .map(i -> canal + " dato " + (i + 1));
    }

    private static Observable<String> llamarAPI(String servicio) {
        return Observable.interval(60, TimeUnit.MILLISECONDS)
            .take(2)
            .map(i -> servicio + " respuesta " + (i + 1));
    }
}

