package com.formadoresit.rxjava.tema6;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 6: Multicast
 * Ejemplo 06: State Management
 * 
 * Gestión de estado reactiva usando Subjects para mantener
 * estado compartido entre componentes de una aplicación
 */
public class Ejemplo06_StateManagement {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 06: State Management ===\n");

        // 1. Estado simple con BehaviorSubject
        System.out.println("--- Estado simple ---");
        StateManager stateManager = new StateManager();
        
        // Suscriptores de estado
        stateManager.getStateStream()
            .subscribe(estado -> System.out.println("  [STATE] " + estado));
        
        stateManager.getStateStream()
            .filter(estado -> estado.contains("ERROR"))
            .subscribe(estado -> System.out.println("  [ERROR_HANDLER] " + estado));
        
        // Cambiar estado
        stateManager.setState("INITIALIZING");
        stateManager.setState("CONNECTING");
        stateManager.setState("CONNECTED");
        stateManager.setState("ERROR: Connection failed");
        stateManager.setState("RECONNECTING");

        // 2. Estado complejo con objeto
        System.out.println("\n--- Estado complejo ---");
        AppStateManager appStateManager = new AppStateManager();
        
        // Suscriptores para diferentes partes del estado
        appStateManager.getUserStream()
            .subscribe(usuario -> System.out.println("  [USER] " + usuario));
        
        appStateManager.getConnectionStream()
            .subscribe(conexion -> System.out.println("  [CONNECTION] " + conexion));
        
        appStateManager.getLoadingStream()
            .subscribe(loading -> System.out.println("  [LOADING] " + loading));
        
        // Cambiar estado
        appStateManager.setUser("usuario123");
        appStateManager.setConnection("CONNECTED");
        appStateManager.setLoading(false);
        
        appStateManager.setUser("usuario456");
        appStateManager.setConnection("DISCONNECTED");
        appStateManager.setLoading(true);

        // 3. Estado con historial
        System.out.println("\n--- Estado con historial ---");
        HistoricalStateManager historicalManager = new HistoricalStateManager();
        
        // Cambiar estado varias veces
        historicalManager.setState("ESTADO1");
        historicalManager.setState("ESTADO2");
        historicalManager.setState("ESTADO3");
        
        // Suscribirse después (recibe historial)
        System.out.println("Suscriptor tardío (recibe historial):");
        historicalManager.getStateStream()
            .subscribe(estado -> System.out.println("  [HISTORICAL] " + estado));

        // 4. Estado con validación
        System.out.println("\n--- Estado con validación ---");
        ValidatedStateManager validatedManager = new ValidatedStateManager();
        
        // Suscriptor de cambios válidos
        validatedManager.getValidStateStream()
            .subscribe(estado -> System.out.println("  [VALID] " + estado));
        
        // Suscriptor de errores de validación
        validatedManager.getValidationErrorStream()
            .subscribe(error -> System.out.println("  [VALIDATION_ERROR] " + error));
        
        // Intentar cambiar estado
        validatedManager.setState("VALID_STATE");
        validatedManager.setState("INVALID_STATE");
        validatedManager.setState("ANOTHER_VALID_STATE");

        // 5. Estado con transformaciones
        System.out.println("\n--- Estado con transformaciones ---");
        TransformStateManager transformManager = new TransformStateManager();
        
        // Suscriptor de estado transformado
        transformManager.getTransformedStateStream()
            .subscribe(estado -> System.out.println("  [TRANSFORMED] " + estado));
        
        // Cambiar estado
        transformManager.setState("hello world");
        transformManager.setState("REACTIVE PROGRAMMING");
        transformManager.setState("123");

        // 6. Estado con debounce
        System.out.println("\n--- Estado con debounce ---");
        DebouncedStateManager debouncedManager = new DebouncedStateManager();
        
        // Suscriptor con debounce
        debouncedManager.getStateStream()
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribe(estado -> System.out.println("  [DEBOUNCED] " + estado));
        
        // Cambiar estado rápidamente (solo el último se procesa)
        debouncedManager.setState("Estado 1");
        debouncedManager.setState("Estado 2");
        debouncedManager.setState("Estado 3");
        debouncedManager.setState("Estado final");
        
        Thread.sleep(300);

        // 7. Estado con persistencia
        System.out.println("\n--- Estado con persistencia ---");
        PersistentStateManager persistentManager = new PersistentStateManager();
        
        // Suscriptor de cambios persistentes
        persistentManager.getStateStream()
            .subscribe(estado -> System.out.println("  [PERSISTENT] " + estado));
        
        // Cambiar estado
        persistentManager.setState("Datos importantes");
        persistentManager.setState("Más datos");
        persistentManager.setState("Datos finales");

        // 8. Estado con métricas
        System.out.println("\n--- Estado con métricas ---");
        MetricsStateManager metricsManager = new MetricsStateManager();
        
        // Suscriptor de métricas
        metricsManager.getMetricsStream()
            .subscribe(metric -> System.out.println("  [METRICS] " + metric));
        
        // Cambiar estado
        metricsManager.setState("Estado 1");
        metricsManager.setState("Estado 2");
        metricsManager.setState("Estado 3");

        // 9. Estado con rollback
        System.out.println("\n--- Estado con rollback ---");
        RollbackStateManager rollbackManager = new RollbackStateManager();
        
        // Suscriptor de estado
        rollbackManager.getStateStream()
            .subscribe(estado -> System.out.println("  [STATE] " + estado));
        
        // Cambiar estado y hacer rollback
        rollbackManager.setState("Estado 1");
        rollbackManager.setState("Estado 2");
        rollbackManager.setState("Estado 3");
        
        System.out.println("Haciendo rollback...");
        rollbackManager.rollback();
        rollbackManager.rollback();

        // 10. Estado con middleware
        System.out.println("\n--- Estado con middleware ---");
        MiddlewareStateManager middlewareManager = new MiddlewareStateManager();
        
        // Suscriptor de estado
        middlewareManager.getStateStream()
            .subscribe(estado -> System.out.println("  [STATE] " + estado));
        
        // Cambiar estado (pasa por middleware)
        middlewareManager.setState("Estado inicial");
        middlewareManager.setState("Estado procesado");
        middlewareManager.setState("Estado final");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• BehaviorSubject: Ideal para estado actual");
        System.out.println("• PublishSubject: Para eventos de cambio de estado");
        System.out.println("• Validación: Prevenir estados inválidos");
        System.out.println("• Transformaciones: Modificar estado antes de emitir");
        System.out.println("• Debounce: Evitar cambios excesivos");
        System.out.println("• Persistencia: Guardar estado en almacenamiento");
        System.out.println("• Métricas: Monitorear cambios de estado");
        System.out.println("• Rollback: Deshacer cambios");
        System.out.println("• Middleware: Procesar cambios antes de aplicar");
    }

    // Implementaciones de diferentes tipos de State Managers

    static class StateManager {
        private final BehaviorSubject<String> stateSubject = BehaviorSubject.createDefault("INITIAL");
        
        public Observable<String> getStateStream() {
            return stateSubject;
        }
        
        public void setState(String estado) {
            stateSubject.onNext(estado);
        }
    }

    static class AppStateManager {
        private final BehaviorSubject<AppState> stateSubject = BehaviorSubject.createDefault(new AppState());
        
        public Observable<String> getUserStream() {
            return stateSubject.map(state -> state.usuario);
        }
        
        public Observable<String> getConnectionStream() {
            return stateSubject.map(state -> state.conexion);
        }
        
        public Observable<Boolean> getLoadingStream() {
            return stateSubject.map(state -> state.loading);
        }
        
        public void setUser(String usuario) {
            AppState currentState = stateSubject.getValue();
            stateSubject.onNext(new AppState(usuario, currentState.conexion, currentState.loading));
        }
        
        public void setConnection(String conexion) {
            AppState currentState = stateSubject.getValue();
            stateSubject.onNext(new AppState(currentState.usuario, conexion, currentState.loading));
        }
        
        public void setLoading(boolean loading) {
            AppState currentState = stateSubject.getValue();
            stateSubject.onNext(new AppState(currentState.usuario, currentState.conexion, loading));
        }
    }

    static class HistoricalStateManager {
        private final BehaviorSubject<String> stateSubject = BehaviorSubject.createDefault("INITIAL");
        
        public Observable<String> getStateStream() {
            return stateSubject;
        }
        
        public void setState(String estado) {
            stateSubject.onNext(estado);
        }
    }

    static class ValidatedStateManager {
        private final BehaviorSubject<String> stateSubject = BehaviorSubject.createDefault("INITIAL");
        private final PublishSubject<String> validationErrorSubject = PublishSubject.create();
        
        public Observable<String> getValidStateStream() {
            return stateSubject.filter(estado -> !estado.contains("INVALID"));
        }
        
        public Observable<String> getValidationErrorStream() {
            return validationErrorSubject;
        }
        
        public void setState(String estado) {
            if (estado.contains("INVALID")) {
                validationErrorSubject.onNext("Estado inválido: " + estado);
            } else {
                stateSubject.onNext(estado);
            }
        }
    }

    static class TransformStateManager {
        private final BehaviorSubject<String> stateSubject = BehaviorSubject.createDefault("INITIAL");
        
        public Observable<String> getTransformedStateStream() {
            return stateSubject.map(estado -> estado.toUpperCase());
        }
        
        public void setState(String estado) {
            stateSubject.onNext(estado);
        }
    }

    static class DebouncedStateManager {
        private final PublishSubject<String> stateSubject = PublishSubject.create();
        
        public Observable<String> getStateStream() {
            return stateSubject;
        }
        
        public void setState(String estado) {
            stateSubject.onNext(estado);
        }
    }

    static class PersistentStateManager {
        private final BehaviorSubject<String> stateSubject = BehaviorSubject.createDefault("INITIAL");
        
        public Observable<String> getStateStream() {
            return stateSubject;
        }
        
        public void setState(String estado) {
            stateSubject.onNext(estado);
            // Simular persistencia
            System.out.println("  [PERSIST] Guardando: " + estado);
        }
    }

    static class MetricsStateManager {
        private final BehaviorSubject<String> stateSubject = BehaviorSubject.createDefault("INITIAL");
        private final PublishSubject<String> metricsSubject = PublishSubject.create();
        private int changeCount = 0;
        
        public Observable<String> getStateStream() {
            return stateSubject;
        }
        
        public Observable<String> getMetricsStream() {
            return metricsSubject;
        }
        
        public void setState(String estado) {
            changeCount++;
            stateSubject.onNext(estado);
            metricsSubject.onNext("Cambios: " + changeCount + ", Estado: " + estado);
        }
    }

    static class RollbackStateManager {
        private final BehaviorSubject<String> stateSubject = BehaviorSubject.createDefault("INITIAL");
        private String previousState = "INITIAL";
        
        public Observable<String> getStateStream() {
            return stateSubject;
        }
        
        public void setState(String estado) {
            previousState = stateSubject.getValue();
            stateSubject.onNext(estado);
        }
        
        public void rollback() {
            stateSubject.onNext(previousState);
        }
    }

    static class MiddlewareStateManager {
        private final BehaviorSubject<String> stateSubject = BehaviorSubject.createDefault("INITIAL");
        
        public Observable<String> getStateStream() {
            return stateSubject;
        }
        
        public void setState(String estado) {
            // Middleware: procesar antes de aplicar
            String processedState = "PROCESSED: " + estado;
            stateSubject.onNext(processedState);
        }
    }

    static class AppState {
        String usuario;
        String conexion;
        boolean loading;
        
        AppState() {
            this.usuario = "N/A";
            this.conexion = "DISCONNECTED";
            this.loading = false;
        }
        
        AppState(String usuario, String conexion, boolean loading) {
            this.usuario = usuario;
            this.conexion = conexion;
            this.loading = loading;
        }
    }
}

