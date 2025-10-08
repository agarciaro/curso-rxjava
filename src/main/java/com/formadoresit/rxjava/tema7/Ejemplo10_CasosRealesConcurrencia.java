package com.formadoresit.rxjava.tema7;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 7: Concurrencia
 * Ejemplo 10: Casos Reales con Concurrencia
 * 
 * Casos de uso reales de concurrencia en aplicaciones:
 * APIs REST, procesamiento de datos, sistemas de monitoreo, etc.
 */
public class Ejemplo10_CasosRealesConcurrencia {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 10: Casos Reales con Concurrencia ===\n");

        // 1. API REST con concurrencia
        System.out.println("--- API REST con concurrencia ---");
        Observable<String> apiCall1 = simularAPICall("usuarios", 200)
            .subscribeOn(Schedulers.io());
        
        Observable<String> apiCall2 = simularAPICall("productos", 150)
            .subscribeOn(Schedulers.io());
        
        Observable<String> apiCall3 = simularAPICall("pedidos", 180)
            .subscribeOn(Schedulers.io());
        
        Observable.zip(apiCall1, apiCall2, apiCall3, (u, p, o) -> 
            "Dashboard: " + u + " + " + p + " + " + o
        )
        .subscribe(resultado -> log("API REST", resultado));

        Thread.sleep(500);

        // 2. Procesamiento de archivos en lote
        System.out.println("\n--- Procesamiento de archivos en lote ---");
        Observable.just("archivo1.txt", "archivo2.txt", "archivo3.txt", "archivo4.txt")
            .flatMap(archivo -> 
                Observable.just(archivo)
                    .subscribeOn(Schedulers.io())
                    .map(file -> {
                        log("Cargando archivo", file);
                        simularTrabajo(100);
                        return "Contenido de " + file;
                    })
                    .observeOn(Schedulers.computation())
                    .map(contenido -> {
                        log("Procesando contenido", contenido);
                        simularTrabajo(80);
                        return "Procesado: " + contenido;
                    })
                    .observeOn(Schedulers.single())
                    .map(procesado -> {
                        log("Guardando resultado", procesado);
                        simularTrabajo(60);
                        return "Guardado: " + procesado;
                    })
            )
            .subscribe(resultado -> log("Archivo Final", resultado));

        Thread.sleep(800);

        // 3. Sistema de monitoreo en tiempo real
        System.out.println("\n--- Sistema de monitoreo en tiempo real ---");
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(10)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        double cpu = 20 + Math.random() * 60; // 20-80%
                        double memoria = 30 + Math.random() * 50; // 30-80%
                        double disco = 10 + Math.random() * 40; // 10-50%
                        return new Metricas(cpu, memoria, disco);
                    })
            )
            .observeOn(Schedulers.computation())
            .map(metricas -> {
                log("Procesando métricas", metricas);
                simularTrabajo(50);
                return metricas;
            })
            .observeOn(Schedulers.single())
            .subscribe(metricas -> log("Métricas Finales", metricas));

        Thread.sleep(1000);

        // 4. Sistema de notificaciones push
        System.out.println("\n--- Sistema de notificaciones push ---");
        Observable.range(1, 5)
            .flatMap(userId -> 
                Observable.just(userId)
                    .subscribeOn(Schedulers.io())
                    .map(id -> {
                        log("Enviando notificación", "Usuario " + id);
                        simularTrabajo(120);
                        return "Notificación enviada a usuario " + id;
                    })
            )
            .subscribe(resultado -> log("Push Notification", resultado));

        Thread.sleep(600);

        // 5. Procesamiento de transacciones financieras
        System.out.println("\n--- Procesamiento de transacciones financieras ---");
        Observable.range(1, 8)
            .flatMap(transaccionId -> 
                Observable.just(transaccionId)
                    .subscribeOn(Schedulers.io())
                    .map(id -> {
                        log("Validando transacción", id);
                        simularTrabajo(80);
                        return new Transaccion(id, 100.0 + Math.random() * 900.0);
                    })
                    .observeOn(Schedulers.computation())
                    .map(txn -> {
                        log("Procesando transacción", txn.id);
                        simularTrabajo(60);
                        return txn;
                    })
                    .observeOn(Schedulers.single())
                    .map(txn -> {
                        log("Registrando transacción", txn.id);
                        simularTrabajo(40);
                        return "Transacción " + txn.id + " procesada: $" + String.format("%.2f", txn.monto);
                    })
            )
            .subscribe(resultado -> log("Transacción Final", resultado));

        Thread.sleep(800);

        // 6. Sistema de chat en tiempo real
        System.out.println("\n--- Sistema de chat en tiempo real ---");
        Observable.interval(200, TimeUnit.MILLISECONDS)
            .take(6)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        String usuario = "Usuario" + (x.intValue() % 3 + 1);
                        String mensaje = "Mensaje " + x;
                        return new MensajeChat(usuario, mensaje);
                    })
            )
            .observeOn(Schedulers.computation())
            .map(mensaje -> {
                log("Procesando mensaje", mensaje.usuario + ": " + mensaje.mensaje);
                simularTrabajo(30);
                return mensaje;
            })
            .observeOn(Schedulers.single())
            .subscribe(mensaje -> log("Chat Final", mensaje.usuario + ": " + mensaje.mensaje));

        Thread.sleep(1200);

        // 7. Sistema de recomendaciones
        System.out.println("\n--- Sistema de recomendaciones ---");
        Observable.range(1, 4)
            .flatMap(userId -> 
                Observable.just(userId)
                    .subscribeOn(Schedulers.io())
                    .map(id -> {
                        log("Analizando usuario", id);
                        simularTrabajo(150);
                        return "Perfil de usuario " + id;
                    })
                    .observeOn(Schedulers.computation())
                    .map(perfil -> {
                        log("Generando recomendaciones", perfil);
                        simularTrabajo(100);
                        return "Recomendaciones para " + perfil;
                    })
            )
            .subscribe(resultado -> log("Recomendaciones", resultado));

        Thread.sleep(600);

        // 8. Sistema de backup automático
        System.out.println("\n--- Sistema de backup automático ---");
        Observable.interval(500, TimeUnit.MILLISECONDS)
            .take(3)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        log("Iniciando backup", x);
                        simularTrabajo(200);
                        return "Backup " + x + " completado";
                    })
            )
            .subscribe(resultado -> log("Backup", resultado));

        Thread.sleep(1000);

        // 9. Sistema de análisis de logs
        System.out.println("\n--- Sistema de análisis de logs ---");
        Observable.range(1, 20)
            .buffer(5) // Procesar en lotes de 5
            .flatMap(lote -> 
                Observable.just(lote)
                    .subscribeOn(Schedulers.computation())
                    .map(lista -> {
                        log("Analizando lote", lista);
                        simularTrabajo(80);
                        return "Lote analizado: " + lista.size() + " logs";
                    })
            )
            .subscribe(resultado -> log("Análisis de Logs", resultado));

        Thread.sleep(400);

        // 10. Sistema de métricas de negocio
        System.out.println("\n--- Sistema de métricas de negocio ---");
        AtomicInteger ventas = new AtomicInteger(0);
        AtomicInteger clientes = new AtomicInteger(0);
        
        Observable.interval(150, TimeUnit.MILLISECONDS)
            .take(8)
            .flatMap(n -> 
                Observable.just(n)
                    .subscribeOn(Schedulers.io())
                    .map(x -> {
                        if (x.intValue() % 2 == 0) {
                            int venta = ventas.incrementAndGet();
                            log("Nueva venta", venta);
                            return "Venta #" + venta;
                        } else {
                            int cliente = clientes.incrementAndGet();
                            log("Nuevo cliente", cliente);
                            return "Cliente #" + cliente;
                        }
                    })
            )
            .observeOn(Schedulers.computation())
            .map(evento -> {
                log("Procesando evento", evento);
                simularTrabajo(40);
                return evento;
            })
            .subscribe(evento -> {
                log("Métricas Finales", evento);
                log("Estadísticas", "Ventas: " + ventas.get() + ", Clientes: " + clientes.get());
            });

        Thread.sleep(1000);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• API REST: Múltiples llamadas concurrentes con zip");
        System.out.println("• Procesamiento de archivos: Pipeline IO -> Computation -> Single");
        System.out.println("• Monitoreo: Métricas en tiempo real con interval");
        System.out.println("• Notificaciones: Envío masivo con flatMap");
        System.out.println("• Transacciones: Procesamiento financiero con validación");
        System.out.println("• Chat: Mensajes en tiempo real con observables");
        System.out.println("• Recomendaciones: Análisis de datos con concurrencia");
        System.out.println("• Backup: Operaciones periódicas con interval");
        System.out.println("• Análisis de logs: Procesamiento en lotes con buffer");
        System.out.println("• Métricas de negocio: Eventos en tiempo real con estadísticas");
    }

    private static Observable<String> simularAPICall(String endpoint, long delayMs) {
        return Observable.fromCallable(() -> {
            log("API Call", "Llamando " + endpoint);
            simularTrabajo(delayMs);
            return "Datos de " + endpoint;
        });
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
}

