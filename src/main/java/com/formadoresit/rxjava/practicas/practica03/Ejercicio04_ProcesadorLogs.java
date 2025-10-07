package com.formadoresit.rxjava.practicas.practica03;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

/**
 * PRÁCTICA 03 - Ejercicio 3.4: Procesador de Logs
 * 
 * Pipeline complejo para procesar logs con groupBy y reducción
 */
public class Ejercicio04_ProcesadorLogs {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 3.4: Procesador de Logs ===\n");

        // Simular logs
        Observable<String> logsStream = generarLogs();

        System.out.println("--- Procesando logs ---\n");

        logsStream
            // 1. Filtrar solo ERROR y WARNING
            .filter(log -> log.contains("ERROR") || log.contains("WARNING"))
            .doOnNext(log -> System.out.println("Filtrado: " + log))
            
            // 2. Extraer timestamp y mensaje
            .map(log -> parsearLog(log))
            
            // 3. Agrupar por hora
            .groupBy(logEntry -> logEntry.hora)
            
            // 4. Contar por hora
            .flatMapSingle(grupo -> 
                grupo.toList()
                    .map(logs -> new ReporteHora(
                        grupo.getKey(),
                        logs.stream().filter(l -> l.nivel.equals("ERROR")).count(),
                        logs.stream().filter(l -> l.nivel.equals("WARNING")).count()
                    ))
            )
            
            // 5. Emitir reporte
            .subscribe(
                reporte -> {
                    System.out.println("\n=== Reporte Hora " + reporte.hora + ":00 ===");
                    System.out.println("  Errores: " + reporte.errores);
                    System.out.println("  Warnings: " + reporte.warnings);
                    System.out.println("  Total: " + (reporte.errores + reporte.warnings));
                },
                error -> System.err.println("Error: " + error),
                () -> System.out.println("\n✓ Procesamiento de logs completado")
            );
    }

    /**
     * Genera logs simulados
     */
    private static Observable<String> generarLogs() {
        List<String> logs = Arrays.asList(
            "[2024-01-15 10:23:45] ERROR: Database connection failed",
            "[2024-01-15 10:25:12] INFO: User logged in",
            "[2024-01-15 10:30:33] WARNING: High memory usage detected",
            "[2024-01-15 10:35:21] ERROR: Out of memory",
            "[2024-01-15 10:45:11] WARNING: Slow query detected",
            "[2024-01-15 11:05:45] ERROR: Authentication failed",
            "[2024-01-15 11:15:23] INFO: Cache cleared",
            "[2024-01-15 11:20:56] WARNING: Deprecated API usage",
            "[2024-01-15 11:30:12] ERROR: File not found",
            "[2024-01-15 11:45:33] WARNING: Rate limit approaching",
            "[2024-01-15 12:10:22] ERROR: Network timeout",
            "[2024-01-15 12:15:45] WARNING: SSL certificate expiring soon",
            "[2024-01-15 12:25:11] ERROR: Invalid input data",
            "[2024-01-15 12:35:40] ERROR: Service unavailable"
        );

        return Observable.fromIterable(logs);
    }

    /**
     * Parsea una línea de log
     */
    private static LogEntry parsearLog(String log) {
        // Formato: [YYYY-MM-DD HH:MM:SS] NIVEL: mensaje
        String timestamp = log.substring(1, 20);
        LocalDateTime fecha = LocalDateTime.parse(timestamp, FORMATTER);
        int hora = fecha.getHour();
        
        String nivel;
        if (log.contains("ERROR")) {
            nivel = "ERROR";
        } else if (log.contains("WARNING")) {
            nivel = "WARNING";
        } else {
            nivel = "INFO";
        }
        
        String mensaje = log.substring(log.indexOf(":") + 2);
        
        return new LogEntry(hora, nivel, mensaje, fecha);
    }
}

class LogEntry {
    int hora;
    String nivel;
    String mensaje;
    LocalDateTime timestamp;

    LogEntry(int hora, String nivel, String mensaje, LocalDateTime timestamp) {
        this.hora = hora;
        this.nivel = nivel;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
    }
}

class ReporteHora {
    int hora;
    long errores;
    long warnings;

    ReporteHora(int hora, long errores, long warnings) {
        this.hora = hora;
        this.errores = errores;
        this.warnings = warnings;
    }
}

