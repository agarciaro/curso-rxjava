package com.formadoresit.rxjava.tema4;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 4: Operadores RxJava
 * Ejemplo 11: Casos Reales con Operadores Combinados
 * 
 * Ejemplos prácticos que combinan múltiples operadores para resolver problemas reales
 * - Sistema de recomendación de productos
 * - Dashboard en tiempo real
 * - Procesamiento de logs
 * - Sistema de notificaciones
 * - Análisis de datos de sensores
 * - API REST con rate limiting
 * - Sistema de cache con invalidación
 * - Pipeline de ETL
 */
public class Ejemplo11_CasosReales {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 11: Casos Reales con Operadores Combinados ===\n");

        // 1. Sistema de recomendación de productos
        System.out.println("--- 1. Sistema de recomendación de productos ---");
        sistemaRecomendacion();

        // 2. Dashboard en tiempo real
        System.out.println("\n--- 2. Dashboard en tiempo real ---");
        dashboardTiempoReal();

        // 3. Procesamiento de logs
        System.out.println("\n--- 3. Procesamiento de logs ---");
        procesamientoLogs();

        // 4. Sistema de notificaciones
        System.out.println("\n--- 4. Sistema de notificaciones ---");
        sistemaNotificaciones();

        // 5. Análisis de datos de sensores
        System.out.println("\n--- 5. Análisis de datos de sensores ---");
        analisisSensores();

        // 6. API REST con rate limiting
        System.out.println("\n--- 6. API REST con rate limiting ---");
        apiRestRateLimiting();

        // 7. Sistema de cache con invalidación
        System.out.println("\n--- 7. Sistema de cache con invalidación ---");
        sistemaCache();

        // 8. Pipeline de ETL
        System.out.println("\n--- 8. Pipeline de ETL ---");
        pipelineETL();

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Los operadores se combinan para resolver problemas complejos");
        System.out.println("• flatMap + zip: Procesamiento paralelo con combinación");
        System.out.println("• debounce + switchMap: Búsquedas en tiempo real");
        System.out.println("• buffer + map: Procesamiento por lotes");
        System.out.println("• groupBy + reduce: Agregación de datos");
        System.out.println("• retry + timeout: Resiliencia en servicios");
        System.out.println("• throttle + sample: Rate limiting");
        System.out.println("• cache + switchIfEmpty: Sistema de cache");
    }

    // 1. Sistema de recomendación de productos
    private static void sistemaRecomendacion() throws InterruptedException {
        Observable.just("usuario123")
            .flatMap(usuario -> 
                Observable.zip(
                    obtenerPerfilUsuario(usuario),
                    obtenerHistorialCompras(usuario),
                    obtenerProductosPopulares(),
                    obtenerTendencias(),
                    (perfil, historial, populares, tendencias) -> 
                        new Recomendacion(perfil, historial, populares, tendencias)
                )
            )
            .map(Recomendacion::generarRecomendaciones)
            .flatMap(recomendaciones -> Observable.just(recomendaciones))
            .take(5)
            .subscribe(recomendacion -> 
                System.out.println("  Recomendación: " + recomendacion)
            );

        Thread.sleep(1000);
    }

    // 2. Dashboard en tiempo real
    private static void dashboardTiempoReal() throws InterruptedException {
        Observable.interval(1, TimeUnit.SECONDS)
            .take(5)
            .flatMap(tick -> 
                Observable.zip(
                    obtenerMetricasVentas(),
                    obtenerMetricasUsuarios(),
                    obtenerMetricasSistema(),
                    (ventas, usuarios, sistema) -> 
                        new Dashboard(ventas, usuarios, sistema)
                )
            )
            .subscribe(dashboard -> 
                System.out.println("  Dashboard: " + dashboard)
            );

        Thread.sleep(5000);
    }

    // 3. Procesamiento de logs
    private static void procesamientoLogs() throws InterruptedException {
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(20)
            .map(tick -> new Log("ERROR", "Error " + tick, System.currentTimeMillis()))
            .buffer(5) // Procesar en lotes de 5
            .flatMap(lote -> 
                Observable.fromCallable(() -> {
                    // Simula procesamiento de lote
                    Thread.sleep(100);
                    return lote;
                })
                .subscribeOn(Schedulers.io())
            )
            .map(lote -> {
                long errores = lote.stream().filter(log -> "ERROR".equals(log.nivel)).count();
                return "Lote procesado: " + lote.size() + " logs, " + errores + " errores";
            })
            .subscribe(resultado -> 
                System.out.println("  " + resultado)
            );

        Thread.sleep(2000);
    }

    // 4. Sistema de notificaciones
    private static void sistemaNotificaciones() throws InterruptedException {
        Observable.just("usuario1", "usuario2", "usuario3")
            .flatMap(usuario -> 
                Observable.zip(
                    obtenerPreferenciasUsuario(usuario),
                    obtenerEventosRecientes(usuario),
                    (prefs, eventos) -> new Notificacion(usuario, prefs, eventos)
                )
            )
            .flatMap(notificacion -> 
                Observable.just(notificacion)
                    .delay(100, TimeUnit.MILLISECONDS) // Simula envío
                    .map(n -> "Notificación enviada a " + n.usuario)
            )
            .subscribe(mensaje -> 
                System.out.println("  " + mensaje)
            );

        Thread.sleep(500);
    }

    // 5. Análisis de datos de sensores
    private static void analisisSensores() throws InterruptedException {
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(20)
            .map(tick -> new SensorData("sensor1", Math.random() * 100, System.currentTimeMillis()))
            .groupBy(sensor -> sensor.sensorId)
            .flatMap(grupo -> 
                grupo
                    .buffer(5) // Agrupar en lotes de 5
                    .map(lote -> {
                        double promedio = lote.stream().mapToDouble(s -> s.valor).average().orElse(0);
                        double maximo = lote.stream().mapToDouble(s -> s.valor).max().orElse(0);
                        return "Sensor " + grupo.getKey() + ": promedio=" + String.format("%.2f", promedio) + 
                               ", max=" + String.format("%.2f", maximo);
                    })
            )
            .subscribe(analisis -> 
                System.out.println("  " + analisis)
            );

        Thread.sleep(1000);
    }

    // 6. API REST con rate limiting
    private static void apiRestRateLimiting() throws InterruptedException {
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(20)
            .map(tick -> "Request " + tick)
            .throttleFirst(200, TimeUnit.MILLISECONDS) // Rate limiting
            .flatMap(request -> 
                Observable.fromCallable(() -> {
                    Thread.sleep(100); // Simula llamada a API
                    return "Response for " + request;
                })
                .subscribeOn(Schedulers.io())
                .timeout(500, TimeUnit.MILLISECONDS)
                .onErrorReturn(error -> "Timeout for " + request)
            )
            .subscribe(response -> 
                System.out.println("  " + response)
            );

        Thread.sleep(2000);
    }

    // 7. Sistema de cache con invalidación
    private static void sistemaCache() throws InterruptedException {
        Observable.just("key1", "key2", "key3", "key1", "key2")
            .flatMap(key -> 
                Observable.just(key)
                    .switchIfEmpty(Observable.just(key))
                    .map(k -> "Cache hit for " + k)
                    .onErrorResumeNext(Observable.just("Cache miss for " + key))
            )
            .subscribe(resultado -> 
                System.out.println("  " + resultado)
            );

        Thread.sleep(500);
    }

    // 8. Pipeline de ETL
    private static void pipelineETL() throws InterruptedException {
        Observable.just("archivo1.csv", "archivo2.csv", "archivo3.csv")
            .flatMap(archivo -> 
                Observable.just(archivo)
                    .map(f -> "Extrayendo " + f)
                    .delay(100, TimeUnit.MILLISECONDS)
                    .map(f -> "Transformando " + f)
                    .delay(100, TimeUnit.MILLISECONDS)
                    .map(f -> "Cargando " + f)
                    .delay(100, TimeUnit.MILLISECONDS)
            )
            .subscribe(paso -> 
                System.out.println("  ETL: " + paso)
            );

        Thread.sleep(500);
    }

    // Métodos auxiliares para simular servicios
    private static Observable<String> obtenerPerfilUsuario(String usuario) {
        return Observable.just("Perfil de " + usuario)
            .delay(100, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> obtenerHistorialCompras(String usuario) {
        return Observable.just("Historial de " + usuario)
            .delay(150, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> obtenerProductosPopulares() {
        return Observable.just("Productos populares")
            .delay(200, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> obtenerTendencias() {
        return Observable.just("Tendencias")
            .delay(120, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> obtenerMetricasVentas() {
        return Observable.just("Ventas: $" + (Math.random() * 1000))
            .delay(50, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> obtenerMetricasUsuarios() {
        return Observable.just("Usuarios: " + (int)(Math.random() * 100))
            .delay(30, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> obtenerMetricasSistema() {
        return Observable.just("CPU: " + String.format("%.1f", Math.random() * 100) + "%")
            .delay(40, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> obtenerPreferenciasUsuario(String usuario) {
        return Observable.just("Prefs de " + usuario)
            .delay(80, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> obtenerEventosRecientes(String usuario) {
        return Observable.just("Eventos de " + usuario)
            .delay(60, TimeUnit.MILLISECONDS);
    }

    // Clases de datos
    static class Recomendacion {
        String perfil;
        String historial;
        String populares;
        String tendencias;

        Recomendacion(String perfil, String historial, String populares, String tendencias) {
            this.perfil = perfil;
            this.historial = historial;
            this.populares = populares;
            this.tendencias = tendencias;
        }

        String generarRecomendaciones() {
            return "Producto recomendado basado en " + perfil + ", " + historial + ", " + populares + ", " + tendencias;
        }
    }

    static class Dashboard {
        String ventas;
        String usuarios;
        String sistema;

        Dashboard(String ventas, String usuarios, String sistema) {
            this.ventas = ventas;
            this.usuarios = usuarios;
            this.sistema = sistema;
        }

        @Override
        public String toString() {
            return ventas + " | " + usuarios + " | " + sistema;
        }
    }

    static class Log {
        String nivel;
        String mensaje;
        long timestamp;

        Log(String nivel, String mensaje, long timestamp) {
            this.nivel = nivel;
            this.mensaje = mensaje;
            this.timestamp = timestamp;
        }
    }

    static class Notificacion {
        String usuario;
        String prefs;
        String eventos;

        Notificacion(String usuario, String prefs, String eventos) {
            this.usuario = usuario;
            this.prefs = prefs;
            this.eventos = eventos;
        }
    }

    static class SensorData {
        String sensorId;
        double valor;
        long timestamp;

        SensorData(String sensorId, double valor, long timestamp) {
            this.sensorId = sensorId;
            this.valor = valor;
            this.timestamp = timestamp;
        }
    }
}
