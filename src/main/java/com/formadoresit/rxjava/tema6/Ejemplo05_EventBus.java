package com.formadoresit.rxjava.tema6;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 6: Multicast
 * Ejemplo 05: Event Bus
 * 
 * Sistema completo de event bus usando Subjects para comunicación
 * entre componentes de una aplicación
 */
public class Ejemplo05_EventBus {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 05: Event Bus ===\n");

        // 1. Event Bus básico con PublishSubject
        System.out.println("--- Event Bus básico ---");
        EventBus eventBus = new EventBus();
        
        // Registrar suscriptores
        eventBus.onEvent("USER_LOGIN")
            .subscribe(evento -> System.out.println("  [AUTH] Usuario logueado: " + evento.getData()));
        
        eventBus.onEvent("USER_LOGOUT")
            .subscribe(evento -> System.out.println("  [AUTH] Usuario deslogueado: " + evento.getData()));
        
        eventBus.onEvent("ORDER_CREATED")
            .subscribe(evento -> System.out.println("  [ORDER] Nueva orden: " + evento.getData()));
        
        // Emitir eventos
        eventBus.emit("USER_LOGIN", "usuario123");
        eventBus.emit("ORDER_CREATED", "ORD-001");
        eventBus.emit("USER_LOGOUT", "usuario123");

        // 2. Event Bus con filtrado por tipo
        System.out.println("\n--- Event Bus con filtrado ---");
        EventBus filtradoBus = new EventBus();
        
        // Suscriptor para todos los eventos de error
        filtradoBus.onEvent("ERROR")
            .subscribe(evento -> System.out.println("  [ERROR_HANDLER] " + evento.getData()));
        
        // Suscriptor para eventos de sistema
        filtradoBus.onEvent("SYSTEM")
            .subscribe(evento -> System.out.println("  [SYSTEM_MONITOR] " + evento.getData()));
        
        // Emitir eventos
        filtradoBus.emit("ERROR", "Error de conexión a BD");
        filtradoBus.emit("SYSTEM", "Memoria baja");
        filtradoBus.emit("ERROR", "Timeout en API");
        filtradoBus.emit("SYSTEM", "CPU alta");

        // 3. Event Bus con estado (BehaviorSubject)
        System.out.println("\n--- Event Bus con estado ---");
        StatefulEventBus statefulBus = new StatefulEventBus();
        
        // Suscriptor de estado
        statefulBus.getStateStream()
            .subscribe(estado -> System.out.println("  [STATE] " + estado));
        
        // Cambiar estados
        statefulBus.setState("CONNECTING");
        statefulBus.setState("CONNECTED");
        statefulBus.setState("PROCESSING");
        statefulBus.setState("DISCONNECTED");

        // 4. Event Bus con historial (ReplaySubject)
        System.out.println("\n--- Event Bus con historial ---");
        HistoricalEventBus historicalBus = new HistoricalEventBus();
        
        // Emitir algunos eventos
        historicalBus.emit("EVENT1", "Datos 1");
        historicalBus.emit("EVENT2", "Datos 2");
        historicalBus.emit("EVENT3", "Datos 3");
        
        // Suscribirse después (recibe historial)
        System.out.println("Suscriptor tardío (recibe historial):");
        historicalBus.onEvent("EVENT1")
            .subscribe(evento -> System.out.println("  [HISTORICAL] " + evento.getData()));

        // 5. Event Bus con prioridades
        System.out.println("\n--- Event Bus con prioridades ---");
        PriorityEventBus priorityBus = new PriorityEventBus();
        
        // Suscriptores por prioridad
        priorityBus.onEvent("CRITICAL")
            .subscribe(evento -> System.out.println("  [CRITICAL] " + evento.getData()));
        
        priorityBus.onEvent("HIGH")
            .subscribe(evento -> System.out.println("  [HIGH] " + evento.getData()));
        
        priorityBus.onEvent("NORMAL")
            .subscribe(evento -> System.out.println("  [NORMAL] " + evento.getData()));
        
        // Emitir eventos con diferentes prioridades
        priorityBus.emit("NORMAL", "Operación normal");
        priorityBus.emit("CRITICAL", "Sistema caído");
        priorityBus.emit("HIGH", "Memoria crítica");
        priorityBus.emit("NORMAL", "Usuario conectado");

        // 6. Event Bus con debounce
        System.out.println("\n--- Event Bus con debounce ---");
        DebouncedEventBus debouncedBus = new DebouncedEventBus();
        
        // Suscriptor con debounce
        debouncedBus.onEvent("SEARCH")
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribe(evento -> System.out.println("  [SEARCH] Buscando: " + evento.getData()));
        
        // Emitir eventos rápidos (solo el último se procesa)
        debouncedBus.emit("SEARCH", "a");
        debouncedBus.emit("SEARCH", "ab");
        debouncedBus.emit("SEARCH", "abc");
        debouncedBus.emit("SEARCH", "abcd");
        
        Thread.sleep(300);

        // 7. Event Bus con retry
        System.out.println("\n--- Event Bus con retry ---");
        RetryEventBus retryBus = new RetryEventBus();
        
        // Suscriptor con retry
        retryBus.onEvent("API_CALL")
            .retry(2)
            .subscribe(
                evento -> System.out.println("  [API] Éxito: " + evento.getData()),
                error -> System.out.println("  [API] Error después de reintentos: " + error.getMessage())
            );
        
        // Simular llamada API que puede fallar
        retryBus.emit("API_CALL", "Llamada 1");

        // 8. Event Bus con transformación
        System.out.println("\n--- Event Bus con transformación ---");
        TransformEventBus transformBus = new TransformEventBus();
        
        // Suscriptor con transformación
        transformBus.onEvent("DATA")
            .map(evento -> evento.getData().toString().toUpperCase())
            .subscribe(data -> System.out.println("  [TRANSFORMED] " + data));
        
        transformBus.emit("DATA", "texto en minúsculas");

        // 9. Event Bus con múltiples tipos
        System.out.println("\n--- Event Bus con múltiples tipos ---");
        MultiTypeEventBus multiBus = new MultiTypeEventBus();
        
        // Suscriptores para diferentes tipos
        multiBus.onEvent("USER_EVENT")
            .subscribe(evento -> System.out.println("  [USER] " + evento.getData()));
        
        multiBus.onEvent("SYSTEM_EVENT")
            .subscribe(evento -> System.out.println("  [SYSTEM] " + evento.getData()));
        
        multiBus.onEvent("BUSINESS_EVENT")
            .subscribe(evento -> System.out.println("  [BUSINESS] " + evento.getData()));
        
        // Emitir diferentes tipos
        multiBus.emit("USER_EVENT", "Usuario creado");
        multiBus.emit("SYSTEM_EVENT", "Sistema iniciado");
        multiBus.emit("BUSINESS_EVENT", "Venta realizada");

        // 10. Event Bus con métricas
        System.out.println("\n--- Event Bus con métricas ---");
        MetricsEventBus metricsBus = new MetricsEventBus();
        
        // Suscriptor de métricas
        metricsBus.getMetricsStream()
            .subscribe(metric -> System.out.println("  [METRICS] " + metric));
        
        // Emitir eventos
        metricsBus.emit("EVENT1", "Datos 1");
        metricsBus.emit("EVENT2", "Datos 2");
        metricsBus.emit("EVENT1", "Datos 3");
        metricsBus.emit("EVENT3", "Datos 4");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Event Bus: Patrón para comunicación entre componentes");
        System.out.println("• PublishSubject: Para eventos en tiempo real");
        System.out.println("• BehaviorSubject: Para estado compartido");
        System.out.println("• ReplaySubject: Para historial de eventos");
        System.out.println("• Filtrado, debounce, retry: Operadores útiles");
        System.out.println("• Métricas: Monitoreo del sistema de eventos");
    }

    // Implementaciones de diferentes tipos de Event Bus

    static class EventBus {
        private final PublishSubject<Evento> subject = PublishSubject.create();
        
        public Observable<Evento> onEvent(String tipo) {
            return subject.filter(evento -> evento.getTipo().equals(tipo));
        }
        
        public void emit(String tipo, Object data) {
            subject.onNext(new Evento(tipo, data));
        }
    }

    static class StatefulEventBus {
        private final BehaviorSubject<String> stateSubject = BehaviorSubject.createDefault("INITIAL");
        
        public Observable<String> getStateStream() {
            return stateSubject;
        }
        
        public void setState(String estado) {
            stateSubject.onNext(estado);
        }
    }

    static class HistoricalEventBus {
        private final ReplaySubject<Evento> subject = ReplaySubject.create();
        
        public Observable<Evento> onEvent(String tipo) {
            return subject.filter(evento -> evento.getTipo().equals(tipo));
        }
        
        public void emit(String tipo, Object data) {
            subject.onNext(new Evento(tipo, data));
        }
    }

    static class PriorityEventBus {
        private final PublishSubject<Evento> subject = PublishSubject.create();
        
        public Observable<Evento> onEvent(String tipo) {
            return subject.filter(evento -> evento.getTipo().equals(tipo));
        }
        
        public void emit(String tipo, Object data) {
            subject.onNext(new Evento(tipo, data));
        }
    }

    static class DebouncedEventBus {
        private final PublishSubject<Evento> subject = PublishSubject.create();
        
        public Observable<Evento> onEvent(String tipo) {
            return subject.filter(evento -> evento.getTipo().equals(tipo));
        }
        
        public void emit(String tipo, Object data) {
            subject.onNext(new Evento(tipo, data));
        }
    }

    static class RetryEventBus {
        private final PublishSubject<Evento> subject = PublishSubject.create();
        
        public Observable<Evento> onEvent(String tipo) {
            return subject.filter(evento -> evento.getTipo().equals(tipo));
        }
        
        public void emit(String tipo, Object data) {
            subject.onNext(new Evento(tipo, data));
        }
    }

    static class TransformEventBus {
        private final PublishSubject<Evento> subject = PublishSubject.create();
        
        public Observable<Evento> onEvent(String tipo) {
            return subject.filter(evento -> evento.getTipo().equals(tipo));
        }
        
        public void emit(String tipo, Object data) {
            subject.onNext(new Evento(tipo, data));
        }
    }

    static class MultiTypeEventBus {
        private final PublishSubject<Evento> subject = PublishSubject.create();
        
        public Observable<Evento> onEvent(String tipo) {
            return subject.filter(evento -> evento.getTipo().equals(tipo));
        }
        
        public void emit(String tipo, Object data) {
            subject.onNext(new Evento(tipo, data));
        }
    }

    static class MetricsEventBus {
        private final PublishSubject<Evento> subject = PublishSubject.create();
        private final BehaviorSubject<String> metricsSubject = BehaviorSubject.createDefault("Métricas iniciadas");
        private int eventCount = 0;
        
        public Observable<Evento> onEvent(String tipo) {
            return subject.filter(evento -> evento.getTipo().equals(tipo));
        }
        
        public Observable<String> getMetricsStream() {
            return metricsSubject;
        }
        
        public void emit(String tipo, Object data) {
            eventCount++;
            subject.onNext(new Evento(tipo, data));
            metricsSubject.onNext("Total eventos: " + eventCount + ", Último: " + tipo);
        }
    }

    static class Evento {
        private final String tipo;
        private final Object data;
        
        public Evento(String tipo, Object data) {
            this.tipo = tipo;
            this.data = data;
        }
        
        public String getTipo() {
            return tipo;
        }
        
        public Object getData() {
            return data;
        }
    }
}

