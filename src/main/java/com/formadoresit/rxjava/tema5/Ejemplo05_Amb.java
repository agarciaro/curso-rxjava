package com.formadoresit.rxjava.tema5;

import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 5: Combinando Observables
 * Ejemplo 05: amb() - Competencia entre Observables
 * 
 * amb (ambient) emite elementos del primer Observable que emita
 * Los demás se descartan automáticamente
 * Útil para múltiples fuentes de datos donde solo necesitas la más rápida
 */
public class Ejemplo05_Amb {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 05: amb() ===\n");

        // 1. amb básico
        System.out.println("--- amb() básico ---");
        Observable<String> rapido = Observable.just("Rápido")
            .delay(100, TimeUnit.MILLISECONDS);
        Observable<String> lento = Observable.just("Lento")
            .delay(500, TimeUnit.MILLISECONDS);
        
        Observable.ambArray(rapido, lento)
            .blockingSubscribe(resultado -> System.out.println("Ganador: " + resultado));

        // 2. amb con múltiples fuentes
        System.out.println("\n--- amb() con múltiples fuentes ---");
        Observable<String> fuente1 = Observable.just("Fuente 1")
            .delay(200, TimeUnit.MILLISECONDS);
        Observable<String> fuente2 = Observable.just("Fuente 2")
            .delay(150, TimeUnit.MILLISECONDS);
        Observable<String> fuente3 = Observable.just("Fuente 3")
            .delay(300, TimeUnit.MILLISECONDS);
        
        Observable.ambArray(fuente1, fuente2, fuente3)
            .blockingSubscribe(ganador -> System.out.println("Primera en responder: " + ganador));

        // 3. amb con emisiones múltiples
        System.out.println("\n--- amb() con emisiones múltiples ---");
        Observable<String> servidor1 = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> "Servidor1-" + i);
        Observable<String> servidor2 = Observable.interval(80, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> "Servidor2-" + i);
        
        Observable.ambArray(servidor1, servidor2)
            .blockingSubscribe(dato -> System.out.println("Dato: " + dato));

        // 4. ambWith() - Versión de instancia
        System.out.println("\n--- ambWith() ---");
        Observable.just("Local")
            .delay(50, TimeUnit.MILLISECONDS)
            .ambWith(Observable.just("Remoto")
                .delay(100, TimeUnit.MILLISECONDS))
            .blockingSubscribe(fuente -> System.out.println("Fuente seleccionada: " + fuente));

        // 5. Ejemplo práctico: Múltiples APIs de geolocalización
        System.out.println("\n--- Ejemplo práctico: APIs de geolocalización ---");
        Observable<Coordenada> gpsAPI = simularGPS()
            .delay(200, TimeUnit.MILLISECONDS);
        Observable<Coordenada> wifiAPI = simularWiFi()
            .delay(150, TimeUnit.MILLISECONDS);
        Observable<Coordenada> celularAPI = simularCelular()
            .delay(300, TimeUnit.MILLISECONDS);
        
        Observable.ambArray(gpsAPI, wifiAPI, celularAPI)
            .blockingSubscribe(coordenada -> 
                System.out.println("Ubicación obtenida: " + coordenada + " (método más rápido)"));

        // 6. Ejemplo práctico: Múltiples fuentes de noticias
        System.out.println("\n--- Ejemplo práctico: Fuentes de noticias ---");
        Observable<Noticia> cnn = simularNoticias("CNN", 100);
        Observable<Noticia> bbc = simularNoticias("BBC", 80);
        Observable<Noticia> reuters = simularNoticias("Reuters", 120);
        
        Observable.ambArray(cnn, bbc, reuters)
            .blockingSubscribe(noticia -> 
                System.out.println("Primera noticia: " + noticia.titulo + " (" + noticia.fuente + ")"));

        // 7. Ejemplo práctico: Múltiples servicios de pago
        System.out.println("\n--- Ejemplo práctico: Servicios de pago ---");
        Observable<ResultadoPago> paypal = simularPago("PayPal", 300);
        Observable<ResultadoPago> stripe = simularPago("Stripe", 250);
        Observable<ResultadoPago> square = simularPago("Square", 400);
        
        Observable.ambArray(paypal, stripe, square)
            .blockingSubscribe(resultado -> 
                System.out.println("Pago procesado: " + resultado.servicio + " - " + resultado.estado));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• amb: Emite del primer Observable que emita");
        System.out.println("• Descarta automáticamente los demás");
        System.out.println("• Útil para competencia entre fuentes");
        System.out.println("• Ideal para fallback y redundancia");
        System.out.println("• No garantiza cuál será el más rápido");
    }

    private static Observable<Coordenada> simularGPS() {
        return Observable.just(new Coordenada(40.4168, -3.7038, "GPS"));
    }

    private static Observable<Coordenada> simularWiFi() {
        return Observable.just(new Coordenada(40.4170, -3.7040, "WiFi"));
    }

    private static Observable<Coordenada> simularCelular() {
        return Observable.just(new Coordenada(40.4165, -3.7035, "Celular"));
    }

    private static Observable<Noticia> simularNoticias(String fuente, long delay) {
        return Observable.just(new Noticia("Última hora: " + fuente, fuente))
            .delay(delay, TimeUnit.MILLISECONDS);
    }

    private static Observable<ResultadoPago> simularPago(String servicio, long delay) {
        return Observable.just(new ResultadoPago(servicio, "Exitoso"))
            .delay(delay, TimeUnit.MILLISECONDS);
    }

    static class Coordenada {
        double lat, lon;
        String metodo;
        
        Coordenada(double lat, double lon, String metodo) {
            this.lat = lat;
            this.lon = lon;
            this.metodo = metodo;
        }
        
        @Override
        public String toString() {
            return String.format("(%.4f, %.4f) via %s", lat, lon, metodo);
        }
    }

    static class Noticia {
        String titulo;
        String fuente;
        
        Noticia(String titulo, String fuente) {
            this.titulo = titulo;
            this.fuente = fuente;
        }
    }

    static class ResultadoPago {
        String servicio;
        String estado;
        
        ResultadoPago(String servicio, String estado) {
            this.servicio = servicio;
            this.estado = estado;
        }
    }
}

