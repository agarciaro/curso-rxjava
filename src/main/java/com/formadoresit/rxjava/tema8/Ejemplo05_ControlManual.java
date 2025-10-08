package com.formadoresit.rxjava.tema8;

import io.reactivex.Flowable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 8: Flujos y Backpressure
 * Ejemplo 05: Control Manual de Backpressure
 * 
 * Control manual de backpressure con Subscription,
 * request(), cancel() y patrones avanzados
 */
public class Ejemplo05_ControlManual {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 05: Control Manual de Backpressure ===\n");

        // 1. Control básico con request()
        System.out.println("--- Control básico con request() ---");
        Flowable.range(1, 20)
            .subscribe(new Subscriber<Integer>() {
                private Subscription subscription;
                private int count = 0;

                @Override
                public void onSubscribe(Subscription s) {
                    this.subscription = s;
                    log("CONTROL", "Solicitando 5 elementos iniciales");
                    subscription.request(5); // Solicitar 5 inicialmente
                }

                @Override
                public void onNext(Integer n) {
                    log("CONTROL", "Procesando: " + n);
                    simularTrabajo(100);
                    count++;
                    
                    if (count % 5 == 0 && count < 15) {
                        log("CONTROL", "Solicitando 5 más...");
                        subscription.request(5);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log("CONTROL", "Error: " + t.getMessage());
                }

                @Override
                public void onComplete() {
                    log("CONTROL", "Completado");
                }
            });

        Thread.sleep(2000);

        // 2. Control con cancel()
        System.out.println("\n--- Control con cancel() ---");
        Flowable.interval(100, TimeUnit.MILLISECONDS)
            .subscribe(new Subscriber<Long>() {
                private Subscription subscription;
                private int count = 0;

                @Override
                public void onSubscribe(Subscription s) {
                    this.subscription = s;
                    log("CANCEL", "Solicitando elementos");
                    subscription.request(10);
                }

                @Override
                public void onNext(Long n) {
                    log("CANCEL", "Procesando: " + n);
                    simularTrabajo(50);
                    count++;
                    
                    if (count >= 5) {
                        log("CANCEL", "Cancelando suscripción");
                        subscription.cancel();
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log("CANCEL", "Error: " + t.getMessage());
                }

                @Override
                public void onComplete() {
                    log("CANCEL", "Completado");
                }
            });

        Thread.sleep(1000);

        // 3. Control con backpressure dinámico
        System.out.println("\n--- Control con backpressure dinámico ---");
        Flowable.range(1, 50)
            .subscribe(new Subscriber<Integer>() {
                private Subscription subscription;
                private int count = 0;
                private int requestSize = 5;

                @Override
                public void onSubscribe(Subscription s) {
                    this.subscription = s;
                    log("DYNAMIC", "Solicitando " + requestSize + " elementos iniciales");
                    subscription.request(requestSize);
                }

                @Override
                public void onNext(Integer n) {
                    log("DYNAMIC", "Procesando: " + n);
                    simularTrabajo(80);
                    count++;
                    
                    // Ajustar tamaño de solicitud basado en el rendimiento
                    if (count % 5 == 0) {
                        if (count < 20) {
                            requestSize = 3; // Reducir solicitud
                        } else {
                            requestSize = 8; // Aumentar solicitud
                        }
                        log("DYNAMIC", "Solicitando " + requestSize + " más...");
                        subscription.request(requestSize);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log("DYNAMIC", "Error: " + t.getMessage());
                }

                @Override
                public void onComplete() {
                    log("DYNAMIC", "Completado");
                }
            });

        Thread.sleep(2000);

        // 4. Control con métricas
        System.out.println("\n--- Control con métricas ---");
        AtomicInteger totalRequested = new AtomicInteger(0);
        AtomicInteger totalProcessed = new AtomicInteger(0);
        
        Flowable.range(1, 30)
            .subscribe(new Subscriber<Integer>() {
                private Subscription subscription;
                private int count = 0;

                @Override
                public void onSubscribe(Subscription s) {
                    this.subscription = s;
                    log("METRICS", "Solicitando 5 elementos iniciales");
                    subscription.request(5);
                    totalRequested.addAndGet(5);
                }

                @Override
                public void onNext(Integer n) {
                    log("METRICS", "Procesando: " + n);
                    simularTrabajo(100);
                    count++;
                    totalProcessed.incrementAndGet();
                    
                    if (count % 5 == 0 && count < 25) {
                        log("METRICS", "Solicitando 5 más...");
                        subscription.request(5);
                        totalRequested.addAndGet(5);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log("METRICS", "Error: " + t.getMessage());
                }

                @Override
                public void onComplete() {
                    log("METRICS", "Completado");
                    log("METRICS", "Total solicitados: " + totalRequested.get());
                    log("METRICS", "Total procesados: " + totalProcessed.get());
                    log("METRICS", "Eficiencia: " + (totalProcessed.get() * 100.0 / totalRequested.get()) + "%");
                }
            });

        Thread.sleep(2000);

        // 5. Control con timeout
        System.out.println("\n--- Control con timeout ---");
        Flowable.interval(50, TimeUnit.MILLISECONDS)
            .subscribe(new Subscriber<Long>() {
                private Subscription subscription;
                private int count = 0;
                private long startTime = System.currentTimeMillis();

                @Override
                public void onSubscribe(Subscription s) {
                    this.subscription = s;
                    log("TIMEOUT", "Solicitando elementos");
                    subscription.request(10);
                }

                @Override
                public void onNext(Long n) {
                    log("TIMEOUT", "Procesando: " + n);
                    simularTrabajo(100);
                    count++;
                    
                    // Timeout después de 2 segundos
                    if (System.currentTimeMillis() - startTime > 2000) {
                        log("TIMEOUT", "Timeout alcanzado, cancelando");
                        subscription.cancel();
                    } else if (count % 5 == 0) {
                        log("TIMEOUT", "Solicitando 5 más...");
                        subscription.request(5);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log("TIMEOUT", "Error: " + t.getMessage());
                }

                @Override
                public void onComplete() {
                    log("TIMEOUT", "Completado");
                }
            });

        Thread.sleep(3000);

        // 6. Control con rate limiting
        System.out.println("\n--- Control con rate limiting ---");
        Flowable.interval(10, TimeUnit.MILLISECONDS)
            .take(100)
            .subscribe(new Subscriber<Long>() {
                private Subscription subscription;
                private int count = 0;
                private long lastRequestTime = 0;

                @Override
                public void onSubscribe(Subscription s) {
                    this.subscription = s;
                    log("RATE", "Solicitando elementos iniciales");
                    subscription.request(5);
                    lastRequestTime = System.currentTimeMillis();
                }

                @Override
                public void onNext(Long n) {
                    log("RATE", "Procesando: " + n);
                    simularTrabajo(50);
                    count++;
                    
                    // Rate limiting: solo solicitar cada 200ms
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastRequestTime >= 200 && count % 3 == 0) {
                        log("RATE", "Solicitando 3 más...");
                        subscription.request(3);
                        lastRequestTime = currentTime;
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log("RATE", "Error: " + t.getMessage());
                }

                @Override
                public void onComplete() {
                    log("RATE", "Completado");
                }
            });

        Thread.sleep(2000);

        // 7. Control con circuit breaker
        System.out.println("\n--- Control con circuit breaker ---");
        AtomicInteger errorCount = new AtomicInteger(0);
        
        Flowable.range(1, 20)
            .subscribe(new Subscriber<Integer>() {
                private Subscription subscription;
                private int count = 0;
                private boolean circuitOpen = false;

                @Override
                public void onSubscribe(Subscription s) {
                    this.subscription = s;
                    log("CIRCUIT", "Solicitando elementos iniciales");
                    subscription.request(5);
                }

                @Override
                public void onNext(Integer n) {
                    if (circuitOpen) {
                        log("CIRCUIT", "Circuito abierto, descartando: " + n);
                        return;
                    }
                    
                    log("CIRCUIT", "Procesando: " + n);
                    simularTrabajo(100);
                    count++;
                    
                    // Simular error ocasional
                    if (n % 7 == 0) {
                        errorCount.incrementAndGet();
                        log("CIRCUIT", "Error simulado en: " + n);
                        if (errorCount.get() >= 3) {
                            circuitOpen = true;
                            log("CIRCUIT", "Circuito abierto por demasiados errores");
                            subscription.cancel();
                        }
                    }
                    
                    if (count % 5 == 0 && !circuitOpen) {
                        log("CIRCUIT", "Solicitando 5 más...");
                        subscription.request(5);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log("CIRCUIT", "Error: " + t.getMessage());
                }

                @Override
                public void onComplete() {
                    log("CIRCUIT", "Completado");
                }
            });

        Thread.sleep(2000);

        // 8. Control con backpressure adaptativo
        System.out.println("\n--- Control con backpressure adaptativo ---");
        Flowable.range(1, 40)
            .subscribe(new Subscriber<Integer>() {
                private Subscription subscription;
                private int count = 0;
                private int requestSize = 5;
                private long lastRequestTime = 0;

                @Override
                public void onSubscribe(Subscription s) {
                    this.subscription = s;
                    log("ADAPTIVE", "Solicitando " + requestSize + " elementos iniciales");
                    subscription.request(requestSize);
                    lastRequestTime = System.currentTimeMillis();
                }

                @Override
                public void onNext(Integer n) {
                    log("ADAPTIVE", "Procesando: " + n);
                    simularTrabajo(80);
                    count++;
                    
                    // Adaptar tamaño de solicitud basado en el tiempo
                    long currentTime = System.currentTimeMillis();
                    if (count % 5 == 0) {
                        long timeSinceLastRequest = currentTime - lastRequestTime;
                        
                        if (timeSinceLastRequest < 500) {
                            requestSize = Math.max(2, requestSize - 1); // Reducir
                            log("ADAPTIVE", "Reduciendo solicitud a: " + requestSize);
                        } else if (timeSinceLastRequest > 1000) {
                            requestSize = Math.min(10, requestSize + 1); // Aumentar
                            log("ADAPTIVE", "Aumentando solicitud a: " + requestSize);
                        }
                        
                        log("ADAPTIVE", "Solicitando " + requestSize + " más...");
                        subscription.request(requestSize);
                        lastRequestTime = currentTime;
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log("ADAPTIVE", "Error: " + t.getMessage());
                }

                @Override
                public void onComplete() {
                    log("ADAPTIVE", "Completado");
                }
            });

        Thread.sleep(2000);

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• request(): Solicitar elementos del productor");
        System.out.println("• cancel(): Cancelar la suscripción");
        System.out.println("• Control dinámico: Ajustar solicitudes basado en rendimiento");
        System.out.println("• Métricas: Monitorear solicitudes vs procesamiento");
        System.out.println("• Timeout: Cancelar después de un tiempo límite");
        System.out.println("• Rate limiting: Controlar frecuencia de solicitudes");
        System.out.println("• Circuit breaker: Abrir circuito en caso de errores");
        System.out.println("• Backpressure adaptativo: Ajustar automáticamente el tamaño");
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
}
