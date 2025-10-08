package com.formadoresit.rxjava.tema5;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * TEMA 5: Combinando Observables
 * Ejemplo 10: Casos de uso reales combinando múltiples operadores
 * 
 * Este ejemplo muestra casos de uso del mundo real donde se combinan
 * múltiples operadores de RxJava para resolver problemas complejos
 */
public class Ejemplo10_CasosReales {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 10: Casos de uso reales ===\n");

        // 1. Sistema de recomendaciones en tiempo real
        System.out.println("--- 1. Sistema de recomendaciones en tiempo real ---");
        sistemaRecomendaciones();

        // 2. Dashboard de métricas en tiempo real
        System.out.println("\n--- 2. Dashboard de métricas en tiempo real ---");
        dashboardMetricas();

        // 3. Sistema de notificaciones inteligente
        System.out.println("\n--- 3. Sistema de notificaciones inteligente ---");
        sistemaNotificaciones();

        // 4. Procesamiento de transacciones financieras
        System.out.println("\n--- 4. Procesamiento de transacciones financieras ---");
        procesamientoTransacciones();

        // 5. Sistema de monitoreo de aplicaciones
        System.out.println("\n--- 5. Sistema de monitoreo de aplicaciones ---");
        monitoreoAplicaciones();

        // 6. Sistema de chat en tiempo real
        System.out.println("\n--- 6. Sistema de chat en tiempo real ---");
        sistemaChat();

        // 7. Pipeline de procesamiento de datos
        System.out.println("\n--- 7. Pipeline de procesamiento de datos ---");
        pipelineProcesamiento();

        // 8. Sistema de geolocalización
        System.out.println("\n--- 8. Sistema de geolocalización ---");
        sistemaGeolocalizacion();

        System.out.println("\n=== RESUMEN DE CASOS DE USO ===");
        System.out.println("• Recomendaciones: merge + combineLatest + switchMap");
        System.out.println("• Dashboard: interval + combineLatest + withLatestFrom");
        System.out.println("• Notificaciones: zip + filter + debounce");
        System.out.println("• Transacciones: concat + retry + timeout");
        System.out.println("• Monitoreo: interval + merge + buffer");
        System.out.println("• Chat: PublishSubject + merge + distinctUntilChanged");
        System.out.println("• Pipeline: concat + flatMap + reduce");
        System.out.println("• Geolocalización: amb + switchOnNext + withLatestFrom");
    }

    private static void sistemaRecomendaciones() throws InterruptedException {
        // Simular múltiples fuentes de datos
        Observable<Producto> historialCompras = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> new Producto("Producto-" + (i + 1), "Electrónicos", 4.5));
        
        Observable<Producto> productosPopulares = Observable.interval(150, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> new Producto("Popular-" + (i + 1), "Hogar", 4.8));
        
        Observable<Producto> productosTendencia = Observable.interval(200, TimeUnit.MILLISECONDS)
            .take(2)
            .map(i -> new Producto("Trend-" + (i + 1), "Moda", 4.7));

        // Combinar todas las fuentes y generar recomendaciones
        Observable.merge(historialCompras, productosPopulares, productosTendencia)
            .distinct(Producto::getNombre)
            .buffer(3)
            .map(productos -> new Recomendacion(productos))
            .blockingSubscribe(recomendacion -> 
                System.out.println("Recomendación: " + recomendacion));
    }

    private static void dashboardMetricas() throws InterruptedException {
        // Métricas de diferentes servicios
        Observable<Metrica> cpu = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> new Metrica("CPU", 50.0 + i * 5));
        
        Observable<Metrica> memoria = Observable.interval(120, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> new Metrica("Memoria", 60.0 + i * 3));
        
        Observable<Metrica> disco = Observable.interval(140, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> new Metrica("Disco", 30.0 + i * 2));

        // Combinar métricas y generar alertas
        Observable.combineLatest(cpu, memoria, disco, 
            (c, m, d) -> new EstadoSistema(c, m, d))
            .filter(estado -> estado.tieneAlerta())
            .blockingSubscribe(estado -> 
                System.out.println("Alerta: " + estado));
    }

    private static void sistemaNotificaciones() throws InterruptedException {
        PublishSubject<Evento> eventos = PublishSubject.create();
        
        // Diferentes tipos de notificaciones
        Observable<Notificacion> notificacionesEmail = eventos
            .filter(e -> e.tipo.equals("email"))
            .debounce(200, TimeUnit.MILLISECONDS)
            .map(e -> new Notificacion("Email", "Notificación por email: " + e.mensaje));
        
        Observable<Notificacion> notificacionesPush = eventos
            .filter(e -> e.tipo.equals("push"))
            .debounce(100, TimeUnit.MILLISECONDS)
            .map(e -> new Notificacion("Push", "Notificación push: " + e.mensaje));
        
        Observable<Notificacion> notificacionesSMS = eventos
            .filter(e -> e.tipo.equals("sms"))
            .debounce(300, TimeUnit.MILLISECONDS)
            .map(e -> new Notificacion("SMS", "Notificación SMS: " + e.mensaje));

        // Combinar todas las notificaciones
        Observable.merge(notificacionesEmail, notificacionesPush, notificacionesSMS)
            .subscribe(notificacion -> 
                System.out.println("Notificación: " + notificacion));

        // Simular eventos
        eventos.onNext(new Evento("email", "Nuevo mensaje"));
        eventos.onNext(new Evento("push", "Actualización disponible"));
        eventos.onNext(new Evento("sms", "Código de verificación"));
        
        Thread.sleep(500);
    }

    private static void procesamientoTransacciones() throws InterruptedException {
        Observable<Transaccion> transacciones = Observable.interval(80, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> new Transaccion("TXN" + (i + 1), 100.0 + i * 50, "Pendiente"));

        // Pipeline de procesamiento
        transacciones
            .concatMap(txn -> 
                validarTransaccion(txn)
                    .flatMap(validada -> procesarPago(validada))
                    .flatMap(procesada -> registrarTransaccion(procesada))
                    .retry(2)
                    .timeout(5, TimeUnit.SECONDS)
            )
            .blockingSubscribe(txn -> 
                System.out.println("Transacción procesada: " + txn));
    }

    private static void monitoreoAplicaciones() throws InterruptedException {
        // Múltiples aplicaciones monitoreadas
        Observable<LogAplicacion> app1 = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(4)
            .map(i -> new LogAplicacion("App1", "INFO", "Operación " + i));
        
        Observable<LogAplicacion> app2 = Observable.interval(120, TimeUnit.MILLISECONDS)
            .take(4)
            .map(i -> new LogAplicacion("App2", "WARN", "Advertencia " + i));
        
        Observable<LogAplicacion> app3 = Observable.interval(140, TimeUnit.MILLISECONDS)
            .take(4)
            .map(i -> new LogAplicacion("App3", "ERROR", "Error " + i));

        // Combinar logs y detectar patrones
        Observable.merge(app1, app2, app3)
            .buffer(3, TimeUnit.SECONDS)
            .map(logs -> new ResumenLogs(logs))
            .filter(resumen -> resumen.tieneErrores())
            .blockingSubscribe(resumen -> 
                System.out.println("Resumen: " + resumen));
    }

    private static void sistemaChat() throws InterruptedException {
        PublishSubject<Mensaje> mensajes = PublishSubject.create();
        PublishSubject<Usuario> usuarios = PublishSubject.create();

        // Procesar mensajes en tiempo real
        mensajes
            .withLatestFrom(usuarios, (msg, user) -> new MensajeConUsuario(msg, user))
            .distinctUntilChanged(m -> m.mensaje.contenido)
            .debounce(100, TimeUnit.MILLISECONDS)
            .subscribe(msgu -> 
                System.out.println("Chat: " + msgu.usuario.nombre + ": " + msgu.mensaje.contenido));

        // Simular usuarios y mensajes
        usuarios.onNext(new Usuario("Juan"));
        usuarios.onNext(new Usuario("María"));
        
        mensajes.onNext(new Mensaje("Hola a todos"));
        mensajes.onNext(new Mensaje("¿Cómo están?"));
        mensajes.onNext(new Mensaje("Todo bien"));
        
        Thread.sleep(300);
    }

    private static void pipelineProcesamiento() throws InterruptedException {
        Observable<String> datos = Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(6)
            .map(i -> "Dato-" + (i + 1));

        // Pipeline completo de procesamiento
        datos
            .concatMap(dato -> 
                Observable.just(dato)
                    .map(Ejemplo10_CasosReales::validar)
                    .map(Ejemplo10_CasosReales::transformar)
                    .map(Ejemplo10_CasosReales::enriquecer)
                    .map(Ejemplo10_CasosReales::guardar)
            )
            .reduce(0, (contador, resultado) -> contador + 1)
            .subscribe(total -> 
                System.out.println("Pipeline completado: " + total + " elementos procesados"));
    }

    private static void sistemaGeolocalizacion() throws InterruptedException {
        // Múltiples fuentes de geolocalización
        Observable<Ubicacion> gps = Observable.just(new Ubicacion(40.4168, -3.7038, "GPS"))
            .delay(200, TimeUnit.MILLISECONDS);
        
        Observable<Ubicacion> wifi = Observable.just(new Ubicacion(40.4170, -3.7040, "WiFi"))
            .delay(150, TimeUnit.MILLISECONDS);
        
        Observable<Ubicacion> celular = Observable.just(new Ubicacion(40.4165, -3.7035, "Celular"))
            .delay(300, TimeUnit.MILLISECONDS);

        // Usar la fuente más rápida y combinar con contexto
        Observable.ambArray(gps, wifi, celular)
            .withLatestFrom(Observable.just("Madrid"), (ubicacion, ciudad) -> 
                new UbicacionConContexto(ubicacion, ciudad))
            .blockingSubscribe(ubicacion -> 
                System.out.println("Ubicación: " + ubicacion));
    }

    // Métodos auxiliares para el pipeline
    private static String validar(String dato) {
        return dato + "-validado";
    }

    private static String transformar(String dato) {
        return dato + "-transformado";
    }

    private static String enriquecer(String dato) {
        return dato + "-enriquecido";
    }

    private static String guardar(String dato) {
        return dato + "-guardado";
    }

    // Métodos auxiliares para transacciones
    private static Observable<Transaccion> validarTransaccion(Transaccion txn) {
        return Observable.just(new Transaccion(txn.id, txn.monto, "Validada"))
            .delay(50, TimeUnit.MILLISECONDS);
    }

    private static Observable<Transaccion> procesarPago(Transaccion txn) {
        return Observable.just(new Transaccion(txn.id, txn.monto, "Procesada"))
            .delay(100, TimeUnit.MILLISECONDS);
    }

    private static Observable<Transaccion> registrarTransaccion(Transaccion txn) {
        return Observable.just(new Transaccion(txn.id, txn.monto, "Registrada"))
            .delay(80, TimeUnit.MILLISECONDS);
    }

    // Clases de datos
    static class Producto {
        String nombre;
        String categoria;
        double rating;
        
        Producto(String nombre, String categoria, double rating) {
            this.nombre = nombre;
            this.categoria = categoria;
            this.rating = rating;
        }
        
        String getNombre() { return nombre; }
        
        @Override
        public String toString() {
            return nombre + " (" + categoria + ", " + rating + "★)";
        }
    }

    static class Recomendacion {
        java.util.List<Producto> productos;
        
        Recomendacion(java.util.List<Producto> productos) {
            this.productos = productos;
        }
        
        @Override
        public String toString() {
            return "Recomendación: " + productos.size() + " productos";
        }
    }

    static class Metrica {
        String nombre;
        double valor;
        
        Metrica(String nombre, double valor) {
            this.nombre = nombre;
            this.valor = valor;
        }
    }

    static class EstadoSistema {
        Metrica cpu, memoria, disco;
        
        EstadoSistema(Metrica cpu, Metrica memoria, Metrica disco) {
            this.cpu = cpu;
            this.memoria = memoria;
            this.disco = disco;
        }
        
        boolean tieneAlerta() {
            return cpu.valor > 80 || memoria.valor > 90 || disco.valor > 95;
        }
        
        @Override
        public String toString() {
            return String.format("CPU: %.1f%%, Memoria: %.1f%%, Disco: %.1f%%", 
                cpu.valor, memoria.valor, disco.valor);
        }
    }

    static class Evento {
        String tipo;
        String mensaje;
        
        Evento(String tipo, String mensaje) {
            this.tipo = tipo;
            this.mensaje = mensaje;
        }
    }

    static class Notificacion {
        String tipo;
        String mensaje;
        
        Notificacion(String tipo, String mensaje) {
            this.tipo = tipo;
            this.mensaje = mensaje;
        }
        
        @Override
        public String toString() {
            return tipo + ": " + mensaje;
        }
    }

    static class Transaccion {
        String id;
        double monto;
        String estado;
        
        Transaccion(String id, double monto, String estado) {
            this.id = id;
            this.monto = monto;
            this.estado = estado;
        }
        
        @Override
        public String toString() {
            return id + " ($" + monto + ") - " + estado;
        }
    }

    static class LogAplicacion {
        String aplicacion;
        String nivel;
        String mensaje;
        
        LogAplicacion(String aplicacion, String nivel, String mensaje) {
            this.aplicacion = aplicacion;
            this.nivel = nivel;
            this.mensaje = mensaje;
        }
    }

    static class ResumenLogs {
        java.util.List<LogAplicacion> logs;
        
        ResumenLogs(java.util.List<LogAplicacion> logs) {
            this.logs = logs;
        }
        
        boolean tieneErrores() {
            return logs.stream().anyMatch(log -> "ERROR".equals(log.nivel));
        }
        
        @Override
        public String toString() {
            long errores = logs.stream().filter(log -> "ERROR".equals(log.nivel)).count();
            return "Logs: " + logs.size() + " total, " + errores + " errores";
        }
    }

    static class Usuario {
        String nombre;
        
        Usuario(String nombre) {
            this.nombre = nombre;
        }
    }

    static class Mensaje {
        String contenido;
        
        Mensaje(String contenido) {
            this.contenido = contenido;
        }
    }

    static class MensajeConUsuario {
        Mensaje mensaje;
        Usuario usuario;
        
        MensajeConUsuario(Mensaje mensaje, Usuario usuario) {
            this.mensaje = mensaje;
            this.usuario = usuario;
        }
    }

    static class Ubicacion {
        double lat, lon;
        String fuente;
        
        Ubicacion(double lat, double lon, String fuente) {
            this.lat = lat;
            this.lon = lon;
            this.fuente = fuente;
        }
        
        @Override
        public String toString() {
            return String.format("(%.4f, %.4f) via %s", lat, lon, fuente);
        }
    }

    static class UbicacionConContexto {
        Ubicacion ubicacion;
        String contexto;
        
        UbicacionConContexto(Ubicacion ubicacion, String contexto) {
            this.ubicacion = ubicacion;
            this.contexto = contexto;
        }
        
        @Override
        public String toString() {
            return ubicacion + " en " + contexto;
        }
    }
}
