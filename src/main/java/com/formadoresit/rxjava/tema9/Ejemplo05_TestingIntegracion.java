package com.formadoresit.rxjava.tema9;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEMA 9: Pruebas y Depuración
 * Ejemplo 05: Testing de Integración
 * 
 * Ejemplos de testing de integración que simulan escenarios reales
 * con múltiples operadores y flujos complejos
 */
public class Ejemplo05_TestingIntegracion {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 05: Testing de Integración ===\n");

        // 1. Test de sistema de notificaciones
        System.out.println("--- Test de sistema de notificaciones ---");
        TestObserver<String> testNotificaciones = new TestObserver<>();
        
        Observable<String> notificaciones = Observable.just("Usuario A", "Usuario B", "Usuario C")
            .flatMap(usuario -> 
                Observable.just("Notificación para " + usuario)
                    .delay(100, TimeUnit.MILLISECONDS)
            )
            .doOnNext(notif -> System.out.println("📧 Enviando: " + notif))
            .doOnComplete(() -> System.out.println("✅ Todas las notificaciones enviadas"));
        
        notificaciones.subscribe(testNotificaciones);
        
        // Esperar a que se complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testNotificaciones.assertValueCount(3);
        testNotificaciones.assertComplete();
        System.out.println("✓ Sistema de notificaciones validado");

        // 2. Test de sistema de caché con expiración
        System.out.println("\n--- Test de sistema de caché ---");
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> testCache = new TestObserver<>();
        
        Observable<String> cache = Observable.just("Datos importantes")
            .delay(1, TimeUnit.SECONDS, scheduler)
            .doOnNext(datos -> System.out.println("💾 Datos cacheados: " + datos))
            .delay(2, TimeUnit.SECONDS, scheduler)
            .doOnNext(datos -> System.out.println("⏰ Cache expirado: " + datos));
        
        cache.subscribe(testCache);
        
        // Simular tiempo
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        
        testCache.assertValues("Datos importantes", "Datos importantes");
        testCache.assertComplete();
        System.out.println("✓ Sistema de caché validado");

        // 3. Test de sistema de retry con backoff exponencial
        System.out.println("\n--- Test de sistema de retry ---");
        AtomicInteger intentos = new AtomicInteger(0);
        TestObserver<String> testRetry = new TestObserver<>();
        
        Observable<String> conRetry = Observable.<String>create(emitter -> {
            int intento = intentos.incrementAndGet();
            System.out.println("🔄 Intento " + intento);
            if (intento < 3) {
                emitter.onError(new RuntimeException("Error temporal"));
            } else {
                emitter.onNext("Éxito después de " + intento + " intentos");
                emitter.onComplete();
            }
        })
        .retry(3)
        .doOnError(error -> System.out.println("❌ Error final: " + error.getMessage()));
        
        conRetry.subscribe(testRetry);
        
        testRetry.assertValues("Éxito después de 3 intentos");
        testRetry.assertComplete();
        System.out.println("✓ Sistema de retry validado");

        // 4. Test de sistema de rate limiting
        System.out.println("\n--- Test de rate limiting ---");
        TestScheduler schedulerRate = new TestScheduler();
        TestObserver<String> testRateLimit = new TestObserver<>();
        
        Observable<String> rateLimited = Observable.interval(100, TimeUnit.MILLISECONDS, schedulerRate)
            .take(10)
            .map(i -> "Request " + i)
            .throttleFirst(200, TimeUnit.MILLISECONDS, schedulerRate)
            .doOnNext(req -> System.out.println("🚦 Request permitido: " + req));
        
        rateLimited.subscribe(testRateLimit);
        
        // Avanzar tiempo
        schedulerRate.advanceTimeBy(1, TimeUnit.SECONDS);
        
        testRateLimit.assertValueCount(5); // Solo 5 requests permitidos
        testRateLimit.assertComplete();
        System.out.println("✓ Rate limiting validado");

        // 5. Test de sistema de circuit breaker
        System.out.println("\n--- Test de circuit breaker ---");
        AtomicInteger fallos = new AtomicInteger(0);
        TestObserver<String> testCircuitBreaker = new TestObserver<>();
        
        Observable<String> circuitBreaker = Observable.<String>create(emitter -> {
            int fallo = fallos.incrementAndGet();
            System.out.println("🔌 Intento de conexión " + fallo);
            if (fallo <= 3) {
                emitter.onError(new RuntimeException("Servicio no disponible"));
            } else {
                emitter.onNext("Servicio restaurado");
                emitter.onComplete();
            }
        })
        .retry(3)
        .onErrorResumeNext(Observable.just("Circuit breaker activado"))
        .doOnNext(resultado -> System.out.println("📊 Resultado: " + resultado));
        
        circuitBreaker.subscribe(testCircuitBreaker);
        
        testCircuitBreaker.assertValues("Circuit breaker activado");
        testCircuitBreaker.assertComplete();
        System.out.println("✓ Circuit breaker validado");

        // 6. Test de sistema de batch processing
        System.out.println("\n--- Test de batch processing ---");
        TestObserver<java.util.List<Integer>> testBatch = new TestObserver<>();
        
        Observable<java.util.List<Integer>> batch = Observable.range(1, 10)
            .buffer(3)
            .doOnNext(lote -> System.out.println("📦 Procesando lote: " + lote))
            .delay(100, TimeUnit.MILLISECONDS)
            .doOnNext(lote -> System.out.println("✅ Lote procesado: " + lote));
        
        batch.subscribe(testBatch);
        
        // Esperar a que se complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testBatch.assertValueCount(4); // 3 lotes de 3 + 1 lote de 1
        testBatch.assertComplete();
        System.out.println("✓ Batch processing validado");

        // 7. Test de sistema de load balancing
        System.out.println("\n--- Test de load balancing ---");
        TestObserver<String> testLoadBalancer = new TestObserver<>();
        
        Observable<String> loadBalancer = Observable.range(1, 6)
            .map(i -> "Request " + i)
            .flatMap(request -> 
                Observable.just("Servidor " + (Integer.parseInt(request.split(" ")[1]) % 3 + 1) + " procesa " + request)
                    .delay(50, TimeUnit.MILLISECONDS)
            )
            .doOnNext(resultado -> System.out.println("⚖️ " + resultado));
        
        loadBalancer.subscribe(testLoadBalancer);
        
        // Esperar a que se complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testLoadBalancer.assertValueCount(6);
        testLoadBalancer.assertComplete();
        System.out.println("✓ Load balancing validado");

        // 8. Test de sistema de monitoreo
        System.out.println("\n--- Test de sistema de monitoreo ---");
        TestObserver<String> testMonitoreo = new TestObserver<>();
        
        Observable<String> monitoreo = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> "Métrica " + i)
            .doOnNext(metrica -> System.out.println("📊 " + metrica))
            .buffer(2)
            .map(lote -> "Lote de métricas: " + lote.size())
            .doOnNext(lote -> System.out.println("📈 " + lote));
        
        monitoreo.subscribe(testMonitoreo);
        
        // Esperar a que se complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testMonitoreo.assertValueCount(3); // 2 lotes de 2 + 1 lote de 1
        testMonitoreo.assertComplete();
        System.out.println("✓ Sistema de monitoreo validado");

        // 9. Test de sistema de autenticación
        System.out.println("\n--- Test de sistema de autenticación ---");
        TestObserver<String> testAuth = new TestObserver<>();
        
        Observable<String> auth = Observable.just("usuario1", "usuario2", "usuario3")
            .flatMap(usuario -> 
                Observable.just(usuario)
                    .delay(50, TimeUnit.MILLISECONDS)
                    .map(u -> "Token para " + u)
                    .doOnNext(token -> System.out.println("🔐 " + token))
            )
            .doOnComplete(() -> System.out.println("✅ Autenticación completada"));
        
        auth.subscribe(testAuth);
        
        // Esperar a que se complete
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testAuth.assertValueCount(3);
        testAuth.assertComplete();
        System.out.println("✓ Sistema de autenticación validado");

        // 10. Test de sistema de logging
        System.out.println("\n--- Test de sistema de logging ---");
        TestObserver<String> testLogging = new TestObserver<>();
        
        Observable<String> logging = Observable.just("INFO", "WARN", "ERROR", "DEBUG")
            .flatMap(nivel -> 
                Observable.just("Log " + nivel + ": Mensaje de prueba")
                    .delay(25, TimeUnit.MILLISECONDS)
                    .doOnNext(log -> System.out.println("📝 " + log))
            )
            .doOnComplete(() -> System.out.println("✅ Logging completado"));
        
        logging.subscribe(testLogging);
        
        // Esperar a que se complete
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testLogging.assertValueCount(4);
        testLogging.assertComplete();
        System.out.println("✓ Sistema de logging validado");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Testing de integración: Validar flujos completos");
        System.out.println("• Simulación de sistemas reales: Notificaciones, caché, retry");
        System.out.println("• Validación de comportamientos complejos");
        System.out.println("• Testing de sistemas distribuidos");
        System.out.println("• Validación de patrones de resilencia");
    }
}
