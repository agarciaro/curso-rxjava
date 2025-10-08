package com.formadoresit.rxjava.tema8;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 8: Flujos y Backpressure
 * Ejemplo 09: Casos Reales con Backpressure
 * 
 * Casos de uso reales con backpressure:
 * procesamiento de archivos, APIs REST, sistemas de monitoreo, etc.
 */
public class Ejemplo09_CasosRealesBackpressure {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 09: Casos Reales con Backpressure ===\n");

        // 1. Procesamiento de archivos grandes
        System.out.println("--- Procesamiento de archivos grandes ---");
        Flowable.range(1, 1000) // Simular 1000 líneas de archivo
            .map(lineNumber -> {
                log("FILE", "Leyendo línea " + lineNumber);
                return "Línea " + lineNumber + ": Contenido del archivo";
            })
            .onBackpressureBuffer(50, () -> log("FILE", "Buffer de archivo lleno"))
            .observeOn(Schedulers.computation())
            .map(line -> {
                log("FILE", "Procesando línea: " + line);
                simularTrabajo(20);
                return line.toUpperCase();
            })
            .buffer(10) // Procesar en lotes de 10 líneas
            .map(lote -> {
                log("FILE", "Procesando lote de " + lote.size() + " líneas");
                simularTrabajo(100);
                return "Lote procesado: " + lote.size() + " líneas";
            })
            .subscribe(resultado -> log("FILE Result", resultado));

        Thread.sleep(2000);

        // 2. API REST con rate limiting
        System.out.println("\n--- API REST con rate limiting ---");
        Flowable.interval(50, TimeUnit.MILLISECONDS)
            .take(50)
            .map(n -> {
                log("API", "Llamada API " + n);
                return "Request " + n;
            })
            .throttleFirst(200, TimeUnit.MILLISECONDS) // Solo una llamada cada 200ms
            .onBackpressureDrop(dropped -> log("API", "Request descartado: " + dropped))
            .observeOn(Schedulers.io())
            .map(request -> {
                log("API", "Procesando " + request);
                simularTrabajo(150);
                return "Response para " + request;
            })
            .subscribe(resultado -> log("API Result", resultado));

        Thread.sleep(1500);

        // 3. Sistema de monitoreo en tiempo real
        System.out.println("\n--- Sistema de monitoreo en tiempo real ---");
        Flowable.interval(100, TimeUnit.MILLISECONDS)
            .take(30)
            .map(n -> {
                double cpu = 20 + Math.random() * 60; // 20-80%
                double memoria = 30 + Math.random() * 50; // 30-80%
                double disco = 10 + Math.random() * 40; // 10-50%
                return new Metricas(cpu, memoria, disco);
            })
            .onBackpressureLatest() // Solo la métrica más reciente
            .observeOn(Schedulers.computation())
            .map(metricas -> {
                log("MONITOR", "Procesando métricas: " + metricas);
                simularTrabajo(80);
                return "Métricas procesadas: " + metricas;
            })
            .subscribe(resultado -> log("MONITOR Result", resultado));

        Thread.sleep(1500);

        // 4. Sistema de notificaciones push
        System.out.println("\n--- Sistema de notificaciones push ---");
        Flowable.range(1, 100)
            .map(userId -> {
                log("PUSH", "Enviando notificación a usuario " + userId);
                return "Notificación para usuario " + userId;
            })
            .onBackpressureBuffer(20, () -> log("PUSH", "Buffer de notificaciones lleno"))
            .observeOn(Schedulers.io())
            .map(notificacion -> {
                log("PUSH", "Procesando " + notificacion);
                simularTrabajo(50);
                return "Notificación enviada: " + notificacion;
            })
            .buffer(5) // Agrupar en lotes de 5 notificaciones
            .map(lote -> {
                log("PUSH", "Lote de notificaciones: " + lote.size());
                simularTrabajo(100);
                return "Lote enviado: " + lote.size() + " notificaciones";
            })
            .subscribe(resultado -> log("PUSH Result", resultado));

        Thread.sleep(1500);

        // 5. Procesamiento de transacciones financieras
        System.out.println("\n--- Procesamiento de transacciones financieras ---");
        Flowable.range(1, 200)
            .map(n -> {
                double monto = 10 + Math.random() * 990; // $10-$1000
                return new Transaccion(n, monto);
            })
            .onBackpressureDrop(dropped -> log("TRANSACTION", "Transacción descartada: " + dropped.id))
            .observeOn(Schedulers.computation())
            .map(txn -> {
                log("TRANSACTION", "Validando transacción " + txn.id);
                simularTrabajo(30);
                return txn;
            })
            .filter(txn -> txn.monto > 50) // Solo transacciones > $50
            .map(txn -> {
                log("TRANSACTION", "Procesando transacción " + txn.id);
                simularTrabajo(40);
                return "Transacción procesada: " + txn.id + " - $" + String.format("%.2f", txn.monto);
            })
            .subscribe(resultado -> log("TRANSACTION Result", resultado));

        Thread.sleep(1500);

        // 6. Sistema de chat en tiempo real
        System.out.println("\n--- Sistema de chat en tiempo real ---");
        Flowable.interval(200, TimeUnit.MILLISECONDS)
            .take(15)
            .map(n -> {
                String usuario = "Usuario" + (n.intValue() % 3 + 1);
                String mensaje = "Mensaje " + n;
                return new MensajeChat(usuario, mensaje);
            })
            .onBackpressureLatest() // Solo el mensaje más reciente
            .observeOn(Schedulers.computation())
            .map(mensaje -> {
                log("CHAT", "Procesando mensaje: " + mensaje.usuario + ": " + mensaje.mensaje);
                simularTrabajo(60);
                return "Mensaje procesado: " + mensaje.usuario + ": " + mensaje.mensaje;
            })
            .subscribe(resultado -> log("CHAT Result", resultado));

        Thread.sleep(1500);

        // 7. Sistema de recomendaciones
        System.out.println("\n--- Sistema de recomendaciones ---");
        Flowable.range(1, 50)
            .map(userId -> {
                log("RECOMMENDATION", "Analizando usuario " + userId);
                return "Perfil de usuario " + userId;
            })
            .onBackpressureBuffer(10, () -> log("RECOMMENDATION", "Buffer de recomendaciones lleno"))
            .observeOn(Schedulers.computation())
            .map(perfil -> {
                log("RECOMMENDATION", "Generando recomendaciones para " + perfil);
                simularTrabajo(100);
                return "Recomendaciones para " + perfil;
            })
            .buffer(3) // Agrupar en lotes de 3 recomendaciones
            .map(lote -> {
                log("RECOMMENDATION", "Procesando lote de recomendaciones: " + lote.size());
                simularTrabajo(80);
                return "Lote de recomendaciones: " + lote.size() + " usuarios";
            })
            .subscribe(resultado -> log("RECOMMENDATION Result", resultado));

        Thread.sleep(1500);

        // 8. Sistema de backup automático
        System.out.println("\n--- Sistema de backup automático ---");
        Flowable.interval(500, TimeUnit.MILLISECONDS)
            .take(5)
            .map(n -> {
                log("BACKUP", "Iniciando backup " + n);
                return "Backup " + n;
            })
            .onBackpressureBuffer(2, () -> log("BACKUP", "Buffer de backup lleno"))
            .observeOn(Schedulers.io())
            .map(backup -> {
                log("BACKUP", "Ejecutando " + backup);
                simularTrabajo(200);
                return backup + " completado";
            })
            .subscribe(resultado -> log("BACKUP Result", resultado));

        Thread.sleep(1500);

        // 9. Sistema de análisis de logs
        System.out.println("\n--- Sistema de análisis de logs ---");
        Flowable.range(1, 500)
            .map(n -> {
                String nivel = n % 4 == 0 ? "ERROR" : (n % 3 == 0 ? "WARNING" : "INFO");
                return new LogEntry(n, nivel, "Mensaje de log " + n);
            })
            .onBackpressureDrop(dropped -> log("LOG", "Log descartado: " + dropped.id))
            .observeOn(Schedulers.computation())
            .map(log -> {
                log("LOG", "Analizando log " + log.id + ": " + log.nivel);
                simularTrabajo(10);
                return log;
            })
            .filter(log -> "ERROR".equals(log.nivel)) // Solo errores
            .map(log -> {
                log("LOG", "Procesando error: " + log.id);
                simularTrabajo(20);
                return "Error procesado: " + log.id;
            })
            .buffer(10) // Agrupar en lotes de 10 errores
            .map(lote -> {
                log("LOG", "Lote de errores: " + lote.size());
                simularTrabajo(50);
                return "Lote de errores procesado: " + lote.size() + " errores";
            })
            .subscribe(resultado -> log("LOG Result", resultado));

        Thread.sleep(1500);

        // 10. Sistema de métricas de negocio
        System.out.println("\n--- Sistema de métricas de negocio ---");
        AtomicInteger ventas = new AtomicInteger(0);
        AtomicInteger clientes = new AtomicInteger(0);
        
        Flowable.interval(100, TimeUnit.MILLISECONDS)
            .take(40)
            .map(n -> {
                if (n.intValue() % 2 == 0) {
                    int venta = ventas.incrementAndGet();
                    log("BUSINESS", "Nueva venta " + venta);
                    return "Venta " + venta;
                } else {
                    int cliente = clientes.incrementAndGet();
                    log("BUSINESS", "Nuevo cliente " + cliente);
                    return "Cliente " + cliente;
                }
            })
            .onBackpressureLatest() // Solo el evento más reciente
            .observeOn(Schedulers.computation())
            .map(evento -> {
                log("BUSINESS", "Procesando evento: " + evento);
                simularTrabajo(40);
                return evento;
            })
            .buffer(5) // Agrupar en lotes de 5 eventos
            .map(lote -> {
                log("BUSINESS", "Procesando lote de eventos: " + lote.size());
                simularTrabajo(60);
                return "Lote de eventos: " + lote.size() + " eventos";
            })
            .subscribe(
                resultado -> log("BUSINESS Result", resultado),
                error -> log("BUSINESS Error", error.getMessage()),
                () -> {
                    log("BUSINESS Final", "Total ventas: " + ventas.get());
                    log("BUSINESS Final", "Total clientes: " + clientes.get());
                }
            );

        Thread.sleep(1500);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Procesamiento de archivos: Usar buffer para archivos grandes");
        System.out.println("• API REST: Usar throttle para rate limiting");
        System.out.println("• Monitoreo: Usar LATEST para métricas en tiempo real");
        System.out.println("• Notificaciones: Usar buffer para envío masivo");
        System.out.println("• Transacciones: Usar DROP para transacciones no críticas");
        System.out.println("• Chat: Usar LATEST para mensajes en tiempo real");
        System.out.println("• Recomendaciones: Usar buffer para procesamiento en lotes");
        System.out.println("• Backup: Usar buffer para operaciones periódicas");
        System.out.println("• Análisis de logs: Usar DROP para logs no críticos");
        System.out.println("• Métricas de negocio: Usar LATEST para eventos en tiempo real");
    }

    private static void log(String operacion, Object valor) {
        System.out.printf("  [%s] %s: %s\n", 
            Thread.currentThread().getName(), 
            operacion, 
            valor);
    }

    private static void simularTrabajo(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static class Metricas {
        double cpu;
        double memoria;
        double disco;
        
        Metricas(double cpu, double memoria, double disco) {
            this.cpu = cpu;
            this.memoria = memoria;
            this.disco = disco;
        }
        
        @Override
        public String toString() {
            return String.format("CPU: %.1f%%, Memoria: %.1f%%, Disco: %.1f%%", cpu, memoria, disco);
        }
    }

    static class Transaccion {
        int id;
        double monto;
        
        Transaccion(int id, double monto) {
            this.id = id;
            this.monto = monto;
        }
    }

    static class MensajeChat {
        String usuario;
        String mensaje;
        
        MensajeChat(String usuario, String mensaje) {
            this.usuario = usuario;
            this.mensaje = mensaje;
        }
    }

    static class LogEntry {
        int id;
        String nivel;
        String mensaje;
        
        LogEntry(int id, String nivel, String mensaje) {
            this.id = id;
            this.nivel = nivel;
            this.mensaje = mensaje;
        }
    }
}

