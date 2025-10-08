package com.formadoresit.rxjava.tema5;

import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 5: Combinando Observables
 * Ejemplo 09: withLatestFrom() - Combinar con último valor
 * 
 * withLatestFrom combina elementos del Observable principal con el último valor
 * de otros Observables. No emite hasta que el principal emita.
 * Útil para enriquecer datos con contexto adicional
 */
public class Ejemplo09_WithLatestFrom {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 09: withLatestFrom() ===\n");

        // 1. withLatestFrom básico
        System.out.println("--- withLatestFrom() básico ---");
        Observable<String> principal = Observable.interval(200, TimeUnit.MILLISECONDS)
            .take(4)
            .map(i -> "Evento-" + i);
        
        Observable<String> contexto = Observable.interval(300, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> "Contexto-" + i);
        
        principal.withLatestFrom(contexto, (evento, ctx) -> evento + " + " + ctx)
            .blockingSubscribe(combinacion -> System.out.println("Combinación: " + combinacion));

        // 2. withLatestFrom con múltiples fuentes
        System.out.println("\n--- withLatestFrom() con múltiples fuentes ---");
        Observable<String> eventos = Observable.interval(150, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> "Evento-" + i);
        
        Observable<String> usuario = Observable.just("Juan");
        Observable<String> sesion = Observable.just("Sesión-123");
        
        eventos.withLatestFrom(
            Observable.combineLatest(usuario, sesion, (u, s) -> u + " (" + s + ")"),
            (evento, info) -> evento + " por " + info
        )
        .blockingSubscribe(resultado -> System.out.println("Resultado: " + resultado));

        // 3. withLatestFrom con timing diferente
        System.out.println("\n--- withLatestFrom() con timing diferente ---");
        Observable<String> clicks = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(6)
            .map(i -> "Click-" + i);
        
        Observable<String> estado = Observable.interval(250, TimeUnit.MILLISECONDS)
            .take(3)
            .map(i -> "Estado-" + (i + 1));
        
        clicks.withLatestFrom(estado, (click, estadoActual) -> 
            click + " en " + estadoActual)
        .blockingSubscribe(resultado -> System.out.println("Click: " + resultado));

        // 4. Ejemplo práctico: Logs con contexto
        System.out.println("\n--- Ejemplo práctico: Logs con contexto ---");
        Observable<String> logs = Observable.interval(80, TimeUnit.MILLISECONDS)
            .take(8)
            .map(i -> "Log-" + (i + 1));
        
        Observable<String> usuarioActual = Observable.just("admin");
        Observable<String> version = Observable.just("v2.1.0");
        Observable<String> ambiente1 = Observable.just("producción");
        
        logs.withLatestFrom(
            Observable.combineLatest(usuarioActual, version, ambiente1, 
                (u, v, a) -> "[Usuario: " + u + ", Versión: " + v + ", Ambiente: " + a + "]"),
            (log, ctx) -> ctx + " " + log
        )
        .blockingSubscribe(logCompleto -> System.out.println("Log completo: " + logCompleto));

        // 5. Ejemplo práctico: Formulario reactivo
        System.out.println("\n--- Ejemplo práctico: Formulario reactivo ---");
        Observable<String> campos = Observable.interval(120, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> "Campo-" + (i + 1));
        
        Observable<String> tema = Observable.just("oscuro");
        Observable<String> idioma = Observable.just("español");
        
        campos.withLatestFrom(
            Observable.combineLatest(tema, idioma, (t, i) -> "Tema: " + t + ", Idioma: " + i),
            (campo, config) -> campo + " (" + config + ")"
        )
        .blockingSubscribe(campoConfigurado -> System.out.println("Campo: " + campoConfigurado));

        // 6. Ejemplo práctico: Métricas con metadata
        System.out.println("\n--- Ejemplo práctico: Métricas con metadata ---");
        Observable<Metrica> metricas = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(6)
            .map(i -> new Metrica("CPU", 50.0 + i * 5));
        
        Observable<String> servidor = Observable.just("Servidor-01");
        Observable<String> region = Observable.just("us-east-1");
        Observable<String> ambiente2 = Observable.just("prod");
        
        metricas.withLatestFrom(
            Observable.combineLatest(servidor, region, ambiente2, 
                (s, r, e) -> new Metadata(s, r, e)),
            (metrica, metadata) -> new MetricaEnriquecida(metrica, metadata)
        )
        .blockingSubscribe(metricaEnriquecida -> 
            System.out.println("Métrica: " + metricaEnriquecida));

        // 7. Ejemplo práctico: Transacciones con contexto
        System.out.println("\n--- Ejemplo práctico: Transacciones con contexto ---");
        Observable<Transaccion> transacciones = Observable.interval(90, TimeUnit.MILLISECONDS)
            .take(7)
            .map(i -> new Transaccion("TXN" + (i + 1), 100.0 + i * 25));
        
        Observable<String> moneda = Observable.just("USD");
        Observable<String> tasaCambio = Observable.just("1.0");
        Observable<String> banco = Observable.just("Banco Central");
        
        transacciones.withLatestFrom(
            Observable.combineLatest(moneda, tasaCambio, banco, 
                (m, t, b) -> new ContextoFinanciero(m, t, b)),
            (txn, ctx) -> new TransaccionEnriquecida(txn, ctx)
        )
        .blockingSubscribe(txnEnriquecida -> 
            System.out.println("Transacción: " + txnEnriquecida));

        // 8. withLatestFrom con error handling
        System.out.println("\n--- withLatestFrom() con error handling ---");
        Observable<String> datos = Observable.just("Dato1", "Dato2", "Dato3");
        Observable<String> contextoConError = Observable.just("Contexto1")
            .concatWith(Observable.error(new RuntimeException("Error en contexto")))
            .concatWith(Observable.just("Contexto2"));
        
        datos.withLatestFrom(
            contextoConError.onErrorReturn(error -> "Error: " + error.getMessage()),
            (dato, ctx) -> dato + " + " + ctx
        )
        .subscribe(
            resultado -> System.out.println("Con error: " + resultado),
            error -> System.out.println("Error: " + error.getMessage())
        );

        // 9. withLatestFrom con filtrado
        System.out.println("\n--- withLatestFrom() con filtrado ---");
        Observable<Integer> numeros = Observable.range(1, 10);
        Observable<String> estado2 = Observable.just("activo", "inactivo", "activo", "inactivo", "activo");
        
        numeros.withLatestFrom(estado2, (num, st) -> new NumeroEstado(num, st))
            .filter(ns -> "activo".equals(ns.estado))
            .map(ns -> ns.numero)
            .subscribe(numero -> System.out.println("Número activo: " + numero));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• withLatestFrom: Combina con último valor de otros Observables");
        System.out.println("• No emite hasta que el Observable principal emita");
        System.out.println("• Útil para enriquecer datos con contexto");
        System.out.println("• Mantiene el último valor de las fuentes secundarias");
        System.out.println("• Ideal para formularios reactivos y logging");
    }

    static class Metrica {
        String nombre;
        double valor;
        
        Metrica(String nombre, double valor) {
            this.nombre = nombre;
            this.valor = valor;
        }
        
        @Override
        public String toString() {
            return nombre + "=" + valor;
        }
    }

    static class Metadata {
        String servidor;
        String region;
        String ambiente;
        
        Metadata(String servidor, String region, String ambiente) {
            this.servidor = servidor;
            this.region = region;
            this.ambiente = ambiente;
        }
        
        @Override
        public String toString() {
            return servidor + "/" + region + "/" + ambiente;
        }
    }

    static class MetricaEnriquecida {
        Metrica metrica;
        Metadata metadata;
        
        MetricaEnriquecida(Metrica metrica, Metadata metadata) {
            this.metrica = metrica;
            this.metadata = metadata;
        }
        
        @Override
        public String toString() {
            return metrica + " en " + metadata;
        }
    }

    static class Transaccion {
        String id;
        double monto;
        
        Transaccion(String id, double monto) {
            this.id = id;
            this.monto = monto;
        }
        
        @Override
        public String toString() {
            return id + "($" + monto + ")";
        }
    }

    static class ContextoFinanciero {
        String moneda;
        String tasaCambio;
        String banco;
        
        ContextoFinanciero(String moneda, String tasaCambio, String banco) {
            this.moneda = moneda;
            this.tasaCambio = tasaCambio;
            this.banco = banco;
        }
        
        @Override
        public String toString() {
            return moneda + "/" + tasaCambio + " via " + banco;
        }
    }

    static class TransaccionEnriquecida {
        Transaccion transaccion;
        ContextoFinanciero contexto;
        
        TransaccionEnriquecida(Transaccion transaccion, ContextoFinanciero contexto) {
            this.transaccion = transaccion;
            this.contexto = contexto;
        }
        
        @Override
        public String toString() {
            return transaccion + " " + contexto;
        }
    }

    static class NumeroEstado {
        int numero;
        String estado;
        
        NumeroEstado(int numero, String estado) {
            this.numero = numero;
            this.estado = estado;
        }
    }
}
