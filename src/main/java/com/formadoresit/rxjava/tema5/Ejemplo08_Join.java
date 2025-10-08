package com.formadoresit.rxjava.tema5;

import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 5: Combinando Observables
 * Ejemplo 08: join() y groupJoin() - Ventanas temporales
 * 
 * join combina elementos de dos Observables basándose en ventanas temporales
 * groupJoin agrupa elementos del Observable izquierdo con elementos del derecho
 * Útil para correlacionar eventos que ocurren en períodos de tiempo específicos
 */
public class Ejemplo08_Join {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 08: join() y groupJoin() ===\n");

        // 1. join básico
        System.out.println("--- join() básico ---");
        Observable<String> clicks = Observable.interval(200, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> "Click-" + i);
        
        Observable<String> hovers = Observable.interval(300, TimeUnit.MILLISECONDS)
            .take(4)
            .map(i -> "Hover-" + i);
        
        clicks.join(
            hovers,
            click -> Observable.timer(100, TimeUnit.MILLISECONDS), // Ventana de 100ms
            hover -> Observable.timer(150, TimeUnit.MILLISECONDS), // Ventana de 150ms
            (click, hover) -> click + " + " + hover
        )
        .blockingSubscribe(combinacion -> System.out.println("Combinación: " + combinacion));

        // 2. join con ventanas más largas
        System.out.println("\n--- join() con ventanas largas ---");
        Observable<String> eventos1 = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> "Evento1-" + i);
        
        Observable<String> eventos2 = Observable.interval(150, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> "Evento2-" + i);
        
        eventos1.join(
            eventos2,
            e1 -> Observable.timer(200, TimeUnit.MILLISECONDS),
            e2 -> Observable.timer(200, TimeUnit.MILLISECONDS),
            (e1, e2) -> e1 + " & " + e2
        )
        .blockingSubscribe(combinacion -> System.out.println("Larga: " + combinacion));

        // 3. groupJoin básico
        System.out.println("\n--- groupJoin() básico ---");
        Observable<String> usuarios = Observable.just("Juan", "María", "Pedro");
        Observable<String> acciones = Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(6)
            .map(i -> "Acción-" + (i % 3 + 1));
        
        usuarios.groupJoin(
            acciones,
            usuario -> Observable.timer(100, TimeUnit.MILLISECONDS),
            accion -> Observable.timer(50, TimeUnit.MILLISECONDS),
            (usuario, accionesUsuario) -> 
                accionesUsuario.map(accion -> usuario + " -> " + accion)
        )
        .flatMap(observable -> observable)
        .blockingSubscribe(resultado -> System.out.println("GroupJoin: " + resultado));

        // 4. Ejemplo práctico: Clicks y Double-clicks
        System.out.println("\n--- Ejemplo práctico: Clicks y Double-clicks ---");
        Observable<Long> clicksSimples = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(8);
        
        clicksSimples.join(
            clicksSimples.skip(1), // Clicks desplazados
            click -> Observable.timer(200, TimeUnit.MILLISECONDS), // Ventana de 200ms
            click -> Observable.timer(200, TimeUnit.MILLISECONDS),
            (click1, click2) -> "Double-click detectado: " + click1 + " -> " + click2
        )
        .blockingSubscribe(detection -> System.out.println(detection));

        // 5. Ejemplo práctico: Eventos de teclado y mouse
        System.out.println("\n--- Ejemplo práctico: Teclado y Mouse ---");
        Observable<String> teclas = Observable.interval(80, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> "Tecla-" + (char)('A' + i));
        
        Observable<String> mouse = Observable.interval(120, TimeUnit.MILLISECONDS)
            .take(4)
            .map(i -> "Mouse-" + (i + 1));
        
        teclas.join(
            mouse,
            tecla -> Observable.timer(100, TimeUnit.MILLISECONDS),
            mouseEvent -> Observable.timer(100, TimeUnit.MILLISECONDS),
            (tecla, mouseEvent) -> "Combinación: " + tecla + " + " + mouseEvent
        )
        .blockingSubscribe(combinacion -> System.out.println(combinacion));

        // 6. Ejemplo práctico: Sensores y alarmas
        System.out.println("\n--- Ejemplo práctico: Sensores y Alarmas ---");
        Observable<SensorData> sensores = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(6)
            .map(i -> new SensorData("Sensor" + (i % 3), 20 + i * 5));
        
        Observable<Alarma> alarmas = Observable.interval(150, TimeUnit.MILLISECONDS)
            .take(4)
            .map(i -> new Alarma("Alarma" + (i + 1), "Temperatura alta"));
        
        sensores.join(
            alarmas,
            sensor -> Observable.timer(200, TimeUnit.MILLISECONDS),
            alarma -> Observable.timer(200, TimeUnit.MILLISECONDS),
            (sensor, alarma) -> new EventoCombinado(sensor, alarma)
        )
        .blockingSubscribe(evento -> System.out.println("Evento: " + evento));

        // 7. Ejemplo práctico: Transacciones y notificaciones
        System.out.println("\n--- Ejemplo práctico: Transacciones y Notificaciones ---");
        Observable<Transaccion> transacciones = Observable.interval(200, TimeUnit.MILLISECONDS)
            .take(4)
            .map(i -> new Transaccion("TXN" + (i + 1), 100.0 + i * 50));
        
        Observable<Notificacion> notificaciones = Observable.interval(180, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> new Notificacion("Notif" + (i + 1), "Transacción procesada"));
        
        transacciones.join(
            notificaciones,
            txn -> Observable.timer(300, TimeUnit.MILLISECONDS),
            notif -> Observable.timer(300, TimeUnit.MILLISECONDS),
            (txn, notif) -> txn.id + " -> " + notif.mensaje
        )
        .blockingSubscribe(resultado -> System.out.println("TXN-Notif: " + resultado));

        // 8. groupJoin con conteo
        System.out.println("\n--- groupJoin() con conteo ---");
        Observable<String> categorias = Observable.just("Electrónicos", "Ropa", "Hogar");
        Observable<String> productos = Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(9)
            .map(i -> "Producto-" + (i % 3 + 1));
        
        categorias.groupJoin(
            productos,
            categoria -> Observable.timer(200, TimeUnit.MILLISECONDS),
            producto -> Observable.timer(100, TimeUnit.MILLISECONDS),
            (categoria, productosCategoria) -> 
                productosCategoria.count().map(count -> categoria + ": " + count + " productos").toObservable()
        )
        .flatMap(observable -> observable)
        .blockingSubscribe(resultado -> System.out.println("Conteo: " + resultado));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• join: Combina elementos basándose en ventanas temporales");
        System.out.println("• groupJoin: Agrupa elementos del izquierdo con elementos del derecho");
        System.out.println("• Ventanas temporales definen cuándo elementos son compatibles");
        System.out.println("• Útil para correlacionar eventos en el tiempo");
        System.out.println("• Ideal para detección de patrones y análisis temporal");
    }

    static class SensorData {
        String nombre;
        double temperatura;
        
        SensorData(String nombre, double temperatura) {
            this.nombre = nombre;
            this.temperatura = temperatura;
        }
        
        @Override
        public String toString() {
            return nombre + "(" + temperatura + "°C)";
        }
    }

    static class Alarma {
        String id;
        String mensaje;
        
        Alarma(String id, String mensaje) {
            this.id = id;
            this.mensaje = mensaje;
        }
        
        @Override
        public String toString() {
            return id + ": " + mensaje;
        }
    }

    static class EventoCombinado {
        SensorData sensor;
        Alarma alarma;
        
        EventoCombinado(SensorData sensor, Alarma alarma) {
            this.sensor = sensor;
            this.alarma = alarma;
        }
        
        @Override
        public String toString() {
            return sensor + " -> " + alarma;
        }
    }

    static class Transaccion {
        String id;
        double monto;
        
        Transaccion(String id, double monto) {
            this.id = id;
            this.monto = monto;
        }
    }

    static class Notificacion {
        String id;
        String mensaje;
        
        Notificacion(String id, String mensaje) {
            this.id = id;
            this.mensaje = mensaje;
        }
    }
}
