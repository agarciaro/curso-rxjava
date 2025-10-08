package com.formadoresit.rxjava.tema6;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 6: Multicast
 * Ejemplo 09: Error Handling
 * 
 * Manejo de errores en multicast para propagar errores
 * a múltiples suscriptores y recuperarse de fallos
 */
public class Ejemplo09_ErrorHandling {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 09: Error Handling ===\n");

        // 1. Propagación de errores en multicast
        System.out.println("--- Propagación de errores ---");
        ErrorPropagationManager errorManager = new ErrorPropagationManager();
        
        // Suscriptores que manejan errores
        errorManager.getDataStream()
            .subscribe(
                data -> System.out.println("  [SUB1] " + data),
                error -> System.out.println("  [SUB1] Error: " + error.getMessage())
            );
        
        errorManager.getDataStream()
            .subscribe(
                data -> System.out.println("  [SUB2] " + data),
                error -> System.out.println("  [SUB2] Error: " + error.getMessage())
            );
        
        // Emitir datos y error
        errorManager.emitData("Datos 1");
        errorManager.emitData("Datos 2");
        errorManager.emitError(new RuntimeException("Error en el sistema"));

        // 2. Recuperación de errores
        System.out.println("\n--- Recuperación de errores ---");
        ErrorRecoveryManager recoveryManager = new ErrorRecoveryManager();
        
        // Suscriptor con recuperación
        recoveryManager.getDataStream()
            .retry(2)
            .subscribe(
                data -> System.out.println("  [RECOVERY] " + data),
                error -> System.out.println("  [RECOVERY] Error final: " + error.getMessage())
            );
        
        // Simular errores y recuperación
        recoveryManager.emitData("Datos 1");
        recoveryManager.emitError(new RuntimeException("Error temporal"));
        recoveryManager.emitData("Datos 2");

        // 3. Manejo de errores con fallback
        System.out.println("\n--- Manejo de errores con fallback ---");
        FallbackErrorManager fallbackManager = new FallbackErrorManager();
        
        // Suscriptor con fallback
        fallbackManager.getDataStream()
            .onErrorReturn(error -> "Datos de respaldo")
            .subscribe(data -> System.out.println("  [FALLBACK] " + data));
        
        // Emitir error
        fallbackManager.emitError(new RuntimeException("Error crítico"));

        // 4. Manejo de errores con retry condicional
        System.out.println("\n--- Retry condicional ---");
        ConditionalRetryManager conditionalManager = new ConditionalRetryManager();
        
        // Suscriptor con retry condicional
        conditionalManager.getDataStream()
            .retryWhen(errors -> errors
                .zipWith(Observable.range(1, 3), (error, retryCount) -> {
                    System.out.println("  [RETRY] Intento " + retryCount + ": " + error.getMessage());
                    return retryCount;
                })
                .flatMap(retryCount -> Observable.timer(retryCount * 100, TimeUnit.MILLISECONDS))
            )
            .subscribe(
                data -> System.out.println("  [CONDITIONAL] " + data),
                error -> System.out.println("  [CONDITIONAL] Error final: " + error.getMessage())
            );
        
        // Simular errores
        conditionalManager.emitError(new RuntimeException("Error 1"));
        Thread.sleep(500);

        // 5. Manejo de errores con circuit breaker
        System.out.println("\n--- Circuit breaker ---");
        CircuitBreakerManager circuitManager = new CircuitBreakerManager();
        
        // Suscriptor con circuit breaker
        circuitManager.getDataStream()
            .subscribe(
                data -> System.out.println("  [CIRCUIT] " + data),
                error -> System.out.println("  [CIRCUIT] Error: " + error.getMessage())
            );
        
        // Simular fallos
        circuitManager.emitError(new RuntimeException("Error 1"));
        circuitManager.emitError(new RuntimeException("Error 2"));
        circuitManager.emitError(new RuntimeException("Error 3"));
        circuitManager.emitData("Datos después de circuit breaker");

        // 6. Manejo de errores con timeout
        System.out.println("\n--- Timeout ---");
        TimeoutErrorManager timeoutManager = new TimeoutErrorManager();
        
        // Suscriptor con timeout
        timeoutManager.getDataStream()
            .timeout(200, TimeUnit.MILLISECONDS)
            .subscribe(
                data -> System.out.println("  [TIMEOUT] " + data),
                error -> System.out.println("  [TIMEOUT] Error: " + error.getMessage())
            );
        
        // Simular timeout
        timeoutManager.emitData("Datos 1");
        timeoutManager.emitData("Datos 2");
        Thread.sleep(300);

        // 7. Manejo de errores con backpressure
        System.out.println("\n--- Backpressure ---");
        BackpressureErrorManager backpressureManager = new BackpressureErrorManager();
        
        // Suscriptor con backpressure
        backpressureManager.getDataStream()
            .subscribe(data -> System.out.println("  [BACKPRESSURE] " + data));
        
        // Emitir muchos datos rápidamente
        for (int i = 0; i < 10; i++) {
            backpressureManager.emitData("Datos " + i);
        }

        // 8. Manejo de errores con logging
        System.out.println("\n--- Logging de errores ---");
        LoggingErrorManager loggingManager = new LoggingErrorManager();
        
        // Suscriptor con logging
        loggingManager.getDataStream()
            .doOnError(error -> System.out.println("  [LOG] Error registrado: " + error.getMessage()))
            .onErrorResumeNext(Observable.just("Datos de respaldo"))
            .subscribe(data -> System.out.println("  [LOGGING] " + data));
        
        // Emitir error
        loggingManager.emitError(new RuntimeException("Error para logging"));

        // 9. Manejo de errores con métricas
        System.out.println("\n--- Métricas de errores ---");
        MetricsErrorManager metricsManager = new MetricsErrorManager();
        
        // Suscriptor de métricas
        metricsManager.getMetricsStream()
            .subscribe(metric -> System.out.println("  [METRICS] " + metric));
        
        // Suscriptor de datos
        metricsManager.getDataStream()
            .subscribe(
                data -> System.out.println("  [DATA] " + data),
                error -> System.out.println("  [DATA] Error: " + error.getMessage())
            );
        
        // Emitir datos y errores
        metricsManager.emitData("Datos 1");
        metricsManager.emitError(new RuntimeException("Error 1"));
        metricsManager.emitData("Datos 2");
        metricsManager.emitError(new RuntimeException("Error 2"));

        // 10. Manejo de errores con notificaciones
        System.out.println("\n--- Notificaciones de errores ---");
        NotificationErrorManager notificationManager = new NotificationErrorManager();
        
        // Suscriptor de notificaciones
        notificationManager.getNotificationStream()
            .subscribe(notif -> System.out.println("  [NOTIFICATION] " + notif));
        
        // Suscriptor de datos
        notificationManager.getDataStream()
            .subscribe(
                data -> System.out.println("  [DATA] " + data),
                error -> System.out.println("  [DATA] Error: " + error.getMessage())
            );
        
        // Emitir datos y errores
        notificationManager.emitData("Datos 1");
        notificationManager.emitError(new RuntimeException("Error crítico"));
        notificationManager.emitData("Datos 2");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Propagación: Errores se propagan a todos los suscriptores");
        System.out.println("• Recuperación: retry() para reintentar operaciones");
        System.out.println("• Fallback: onErrorReturn() para valores por defecto");
        System.out.println("• Circuit breaker: Prevenir cascadas de errores");
        System.out.println("• Timeout: Evitar operaciones que tardan demasiado");
        System.out.println("• Backpressure: Manejar sobrecarga de datos");
        System.out.println("• Logging: Registrar errores para debugging");
        System.out.println("• Métricas: Monitorear frecuencia de errores");
        System.out.println("• Notificaciones: Alertar sobre errores críticos");
    }

    // Implementaciones de diferentes tipos de Error Managers

    static class ErrorPropagationManager {
        private final PublishSubject<String> dataSubject = PublishSubject.create();
        
        public Observable<String> getDataStream() {
            return dataSubject;
        }
        
        public void emitData(String data) {
            dataSubject.onNext(data);
        }
        
        public void emitError(Throwable error) {
            dataSubject.onError(error);
        }
    }

    static class ErrorRecoveryManager {
        private final PublishSubject<String> dataSubject = PublishSubject.create();
        
        public Observable<String> getDataStream() {
            return dataSubject;
        }
        
        public void emitData(String data) {
            dataSubject.onNext(data);
        }
        
        public void emitError(Throwable error) {
            dataSubject.onError(error);
        }
    }

    static class FallbackErrorManager {
        private final PublishSubject<String> dataSubject = PublishSubject.create();
        
        public Observable<String> getDataStream() {
            return dataSubject;
        }
        
        public void emitError(Throwable error) {
            dataSubject.onError(error);
        }
    }

    static class ConditionalRetryManager {
        private final PublishSubject<String> dataSubject = PublishSubject.create();
        
        public Observable<String> getDataStream() {
            return dataSubject;
        }
        
        public void emitError(Throwable error) {
            dataSubject.onError(error);
        }
    }

    static class CircuitBreakerManager {
        private final PublishSubject<String> dataSubject = PublishSubject.create();
        private int errorCount = 0;
        private boolean circuitOpen = false;
        
        public Observable<String> getDataStream() {
            return dataSubject;
        }
        
        public void emitData(String data) {
            if (circuitOpen) {
                System.out.println("  [CIRCUIT] Circuito abierto, datos descartados");
                return;
            }
            dataSubject.onNext(data);
        }
        
        public void emitError(Throwable error) {
            errorCount++;
            if (errorCount >= 3) {
                circuitOpen = true;
                System.out.println("  [CIRCUIT] Circuito abierto después de " + errorCount + " errores");
            }
            dataSubject.onError(error);
        }
    }

    static class TimeoutErrorManager {
        private final PublishSubject<String> dataSubject = PublishSubject.create();
        
        public Observable<String> getDataStream() {
            return dataSubject;
        }
        
        public void emitData(String data) {
            dataSubject.onNext(data);
        }
    }

    static class BackpressureErrorManager {
        private final PublishSubject<String> dataSubject = PublishSubject.create();
        
        public Observable<String> getDataStream() {
            return dataSubject;
        }
        
        public void emitData(String data) {
            dataSubject.onNext(data);
        }
    }

    static class LoggingErrorManager {
        private final PublishSubject<String> dataSubject = PublishSubject.create();
        
        public Observable<String> getDataStream() {
            return dataSubject;
        }
        
        public void emitError(Throwable error) {
            dataSubject.onError(error);
        }
    }

    static class MetricsErrorManager {
        private final PublishSubject<String> dataSubject = PublishSubject.create();
        private final PublishSubject<String> metricsSubject = PublishSubject.create();
        private int errorCount = 0;
        private int dataCount = 0;
        
        public Observable<String> getDataStream() {
            return dataSubject;
        }
        
        public Observable<String> getMetricsStream() {
            return metricsSubject;
        }
        
        public void emitData(String data) {
            dataCount++;
            dataSubject.onNext(data);
            metricsSubject.onNext("Datos: " + dataCount + ", Errores: " + errorCount);
        }
        
        public void emitError(Throwable error) {
            errorCount++;
            dataSubject.onError(error);
            metricsSubject.onNext("Datos: " + dataCount + ", Errores: " + errorCount);
        }
    }

    static class NotificationErrorManager {
        private final PublishSubject<String> dataSubject = PublishSubject.create();
        private final PublishSubject<String> notificationSubject = PublishSubject.create();
        
        public Observable<String> getDataStream() {
            return dataSubject;
        }
        
        public Observable<String> getNotificationStream() {
            return notificationSubject;
        }
        
        public void emitData(String data) {
            dataSubject.onNext(data);
        }
        
        public void emitError(Throwable error) {
            notificationSubject.onNext("ALERTA: Error crítico - " + error.getMessage());
            dataSubject.onError(error);
        }
    }
}
