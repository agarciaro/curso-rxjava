package com.formadoresit.rxjava.tema6;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.Observable;

/**
 * TEMA 6: Multicast
 * Ejemplo 07: WebSocket
 * 
 * Implementación de WebSocket con multicast para compartir
 * mensajes entre múltiples clientes y componentes
 */
public class Ejemplo07_WebSocket {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 07: WebSocket ===\n");

        // 1. WebSocket básico con PublishSubject
        System.out.println("--- WebSocket básico ---");
        WebSocketManager wsManager = new WebSocketManager();
        
        // Suscriptores (simulan diferentes clientes)
        wsManager.getMessageStream()
            .subscribe(mensaje -> System.out.println("  [CLIENTE1] " + mensaje));
        
        wsManager.getMessageStream()
            .subscribe(mensaje -> System.out.println("  [CLIENTE2] " + mensaje));
        
        // Simular mensajes
        wsManager.sendMessage("Hola desde el servidor");
        wsManager.sendMessage("Mensaje de notificación");
        wsManager.sendMessage("Datos actualizados");

        // 2. WebSocket con tipos de mensaje
        System.out.println("\n--- WebSocket con tipos de mensaje ---");
        TypedWebSocketManager typedWsManager = new TypedWebSocketManager();
        
        // Suscriptores por tipo
        typedWsManager.getMessageStream("CHAT")
            .subscribe(mensaje -> System.out.println("  [CHAT] " + mensaje));
        
        typedWsManager.getMessageStream("NOTIFICATION")
            .subscribe(mensaje -> System.out.println("  [NOTIFICATION] " + mensaje));
        
        typedWsManager.getMessageStream("DATA")
            .subscribe(mensaje -> System.out.println("  [DATA] " + mensaje));
        
        // Enviar mensajes de diferentes tipos
        typedWsManager.sendMessage("CHAT", "Usuario conectado");
        typedWsManager.sendMessage("NOTIFICATION", "Nueva actualización disponible");
        typedWsManager.sendMessage("DATA", "Datos actualizados");
        typedWsManager.sendMessage("CHAT", "Mensaje de chat");

        // 3. WebSocket con estado de conexión
        System.out.println("\n--- WebSocket con estado de conexión ---");
        StatefulWebSocketManager statefulWsManager = new StatefulWebSocketManager();
        
        // Suscriptor de estado
        statefulWsManager.getConnectionStateStream()
            .subscribe(estado -> System.out.println("  [CONNECTION] " + estado));
        
        // Suscriptor de mensajes
        statefulWsManager.getMessageStream()
            .subscribe(mensaje -> System.out.println("  [MESSAGE] " + mensaje));
        
        // Simular ciclo de conexión
        statefulWsManager.connect();
        statefulWsManager.sendMessage("Mensaje 1");
        statefulWsManager.sendMessage("Mensaje 2");
        statefulWsManager.disconnect();
        statefulWsManager.connect();
        statefulWsManager.sendMessage("Mensaje 3");

        // 4. WebSocket con heartbeat
        System.out.println("\n--- WebSocket con heartbeat ---");
        HeartbeatWebSocketManager heartbeatWsManager = new HeartbeatWebSocketManager();
        
        // Suscriptor de heartbeat
        heartbeatWsManager.getHeartbeatStream()
            .subscribe(heartbeat -> System.out.println("  [HEARTBEAT] " + heartbeat));
        
        // Suscriptor de mensajes
        heartbeatWsManager.getMessageStream()
            .subscribe(mensaje -> System.out.println("  [MESSAGE] " + mensaje));
        
        // Simular heartbeat
        heartbeatWsManager.startHeartbeat();
        heartbeatWsManager.sendMessage("Mensaje importante");
        Thread.sleep(300);
        heartbeatWsManager.stopHeartbeat();

        // 5. WebSocket con reconexión automática
        System.out.println("\n--- WebSocket con reconexión automática ---");
        AutoReconnectWebSocketManager autoReconnectWsManager = new AutoReconnectWebSocketManager();
        
        // Suscriptor de estado
        autoReconnectWsManager.getConnectionStateStream()
            .subscribe(estado -> System.out.println("  [CONNECTION] " + estado));
        
        // Suscriptor de mensajes
        autoReconnectWsManager.getMessageStream()
            .subscribe(mensaje -> System.out.println("  [MESSAGE] " + mensaje));
        
        // Simular desconexión y reconexión
        autoReconnectWsManager.connect();
        autoReconnectWsManager.sendMessage("Mensaje antes de desconexión");
        autoReconnectWsManager.simulateDisconnection();
        Thread.sleep(200);
        autoReconnectWsManager.sendMessage("Mensaje después de reconexión");

        // 6. WebSocket con filtrado de mensajes
        System.out.println("\n--- WebSocket con filtrado de mensajes ---");
        FilteredWebSocketManager filteredWsManager = new FilteredWebSocketManager();
        
        // Suscriptores con filtros
        filteredWsManager.getMessageStream()
            .filter(mensaje -> mensaje.contains("ERROR"))
            .subscribe(mensaje -> System.out.println("  [ERROR_HANDLER] " + mensaje));
        
        filteredWsManager.getMessageStream()
            .filter(mensaje -> mensaje.contains("SUCCESS"))
            .subscribe(mensaje -> System.out.println("  [SUCCESS_HANDLER] " + mensaje));
        
        filteredWsManager.getMessageStream()
            .filter(mensaje -> mensaje.contains("WARNING"))
            .subscribe(mensaje -> System.out.println("  [WARNING_HANDLER] " + mensaje));
        
        // Enviar mensajes
        filteredWsManager.sendMessage("Operación SUCCESS");
        filteredWsManager.sendMessage("ERROR en la conexión");
        filteredWsManager.sendMessage("WARNING: Memoria baja");
        filteredWsManager.sendMessage("Mensaje normal");

        // 7. WebSocket con rate limiting
        System.out.println("\n--- WebSocket con rate limiting ---");
        RateLimitedWebSocketManager rateLimitedWsManager = new RateLimitedWebSocketManager();
        
        // Suscriptor de mensajes
        rateLimitedWsManager.getMessageStream()
            .subscribe(mensaje -> System.out.println("  [MESSAGE] " + mensaje));
        
        // Enviar muchos mensajes rápidamente
        for (int i = 0; i < 10; i++) {
            rateLimitedWsManager.sendMessage("Mensaje " + i);
        }
        
        Thread.sleep(500);

        // 8. WebSocket con métricas
        System.out.println("\n--- WebSocket con métricas ---");
        MetricsWebSocketManager metricsWsManager = new MetricsWebSocketManager();
        
        // Suscriptor de métricas
        metricsWsManager.getMetricsStream()
            .subscribe(metric -> System.out.println("  [METRICS] " + metric));
        
        // Suscriptor de mensajes
        metricsWsManager.getMessageStream()
            .subscribe(mensaje -> System.out.println("  [MESSAGE] " + mensaje));
        
        // Enviar mensajes
        metricsWsManager.sendMessage("Mensaje 1");
        metricsWsManager.sendMessage("Mensaje 2");
        metricsWsManager.sendMessage("Mensaje 3");

        // 9. WebSocket con persistencia
        System.out.println("\n--- WebSocket con persistencia ---");
        PersistentWebSocketManager persistentWsManager = new PersistentWebSocketManager();
        
        // Suscriptor de mensajes
        persistentWsManager.getMessageStream()
            .subscribe(mensaje -> System.out.println("  [MESSAGE] " + mensaje));
        
        // Enviar mensajes
        persistentWsManager.sendMessage("Mensaje persistente 1");
        persistentWsManager.sendMessage("Mensaje persistente 2");
        persistentWsManager.sendMessage("Mensaje persistente 3");

        // 10. WebSocket con autenticación
        System.out.println("\n--- WebSocket con autenticación ---");
        AuthenticatedWebSocketManager authWsManager = new AuthenticatedWebSocketManager();
        
        // Suscriptor de mensajes
        authWsManager.getMessageStream()
            .subscribe(mensaje -> System.out.println("  [MESSAGE] " + mensaje));
        
        // Simular autenticación
        authWsManager.authenticate("usuario123", "password123");
        authWsManager.sendMessage("Mensaje autenticado");
        authWsManager.logout();
        authWsManager.sendMessage("Mensaje no autenticado");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• PublishSubject: Ideal para mensajes en tiempo real");
        System.out.println("• BehaviorSubject: Para estado de conexión");
        System.out.println("• Filtrado: Separar diferentes tipos de mensajes");
        System.out.println("• Heartbeat: Mantener conexión viva");
        System.out.println("• Reconexión: Recuperar conexión automáticamente");
        System.out.println("• Rate limiting: Controlar frecuencia de mensajes");
        System.out.println("• Métricas: Monitorear uso del WebSocket");
        System.out.println("• Persistencia: Guardar mensajes importantes");
        System.out.println("• Autenticación: Verificar identidad de clientes");
    }

    // Implementaciones de diferentes tipos de WebSocket Managers

    static class WebSocketManager {
        private final PublishSubject<String> messageSubject = PublishSubject.create();
        
        public Observable<String> getMessageStream() {
            return messageSubject;
        }
        
        public void sendMessage(String mensaje) {
            messageSubject.onNext(mensaje);
        }
    }

    static class TypedWebSocketManager {
        private final PublishSubject<TypedMessage> messageSubject = PublishSubject.create();
        
        public Observable<String> getMessageStream(String tipo) {
            return messageSubject
                .filter(msg -> msg.tipo.equals(tipo))
                .map(msg -> msg.contenido);
        }
        
        public void sendMessage(String tipo, String contenido) {
            messageSubject.onNext(new TypedMessage(tipo, contenido));
        }
    }

    static class StatefulWebSocketManager {
        private final PublishSubject<String> messageSubject = PublishSubject.create();
        private final BehaviorSubject<String> connectionStateSubject = BehaviorSubject.createDefault("DISCONNECTED");
        
        public Observable<String> getMessageStream() {
            return messageSubject;
        }
        
        public Observable<String> getConnectionStateStream() {
            return connectionStateSubject;
        }
        
        public void connect() {
            connectionStateSubject.onNext("CONNECTED");
        }
        
        public void disconnect() {
            connectionStateSubject.onNext("DISCONNECTED");
        }
        
        public void sendMessage(String mensaje) {
            if ("CONNECTED".equals(connectionStateSubject.getValue())) {
                messageSubject.onNext(mensaje);
            }
        }
    }

    static class HeartbeatWebSocketManager {
        private final PublishSubject<String> messageSubject = PublishSubject.create();
        private final PublishSubject<String> heartbeatSubject = PublishSubject.create();
        private boolean heartbeatActive = false;
        
        public Observable<String> getMessageStream() {
            return messageSubject;
        }
        
        public Observable<String> getHeartbeatStream() {
            return heartbeatSubject;
        }
        
        public void startHeartbeat() {
            heartbeatActive = true;
            new Thread(() -> {
                while (heartbeatActive) {
                    heartbeatSubject.onNext("PING");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }).start();
        }
        
        public void stopHeartbeat() {
            heartbeatActive = false;
        }
        
        public void sendMessage(String mensaje) {
            messageSubject.onNext(mensaje);
        }
    }

    static class AutoReconnectWebSocketManager {
        private final PublishSubject<String> messageSubject = PublishSubject.create();
        private final BehaviorSubject<String> connectionStateSubject = BehaviorSubject.createDefault("DISCONNECTED");
        
        public Observable<String> getMessageStream() {
            return messageSubject;
        }
        
        public Observable<String> getConnectionStateStream() {
            return connectionStateSubject;
        }
        
        public void connect() {
            connectionStateSubject.onNext("CONNECTED");
        }
        
        public void simulateDisconnection() {
            connectionStateSubject.onNext("DISCONNECTED");
            // Simular reconexión automática
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    connectionStateSubject.onNext("RECONNECTING");
                    Thread.sleep(100);
                    connectionStateSubject.onNext("CONNECTED");
                } catch (InterruptedException e) {
                    // Ignorar
                }
            }).start();
        }
        
        public void sendMessage(String mensaje) {
            messageSubject.onNext(mensaje);
        }
    }

    static class FilteredWebSocketManager {
        private final PublishSubject<String> messageSubject = PublishSubject.create();
        
        public Observable<String> getMessageStream() {
            return messageSubject;
        }
        
        public void sendMessage(String mensaje) {
            messageSubject.onNext(mensaje);
        }
    }

    static class RateLimitedWebSocketManager {
        private final PublishSubject<String> messageSubject = PublishSubject.create();
        private long lastMessageTime = 0;
        private final long RATE_LIMIT_MS = 100; // 100ms entre mensajes
        
        public Observable<String> getMessageStream() {
            return messageSubject;
        }
        
        public void sendMessage(String mensaje) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMessageTime >= RATE_LIMIT_MS) {
                messageSubject.onNext(mensaje);
                lastMessageTime = currentTime;
            } else {
                System.out.println("  [RATE_LIMITED] Mensaje descartado: " + mensaje);
            }
        }
    }

    static class MetricsWebSocketManager {
        private final PublishSubject<String> messageSubject = PublishSubject.create();
        private final PublishSubject<String> metricsSubject = PublishSubject.create();
        private int messageCount = 0;
        
        public Observable<String> getMessageStream() {
            return messageSubject;
        }
        
        public Observable<String> getMetricsStream() {
            return metricsSubject;
        }
        
        public void sendMessage(String mensaje) {
            messageCount++;
            messageSubject.onNext(mensaje);
            metricsSubject.onNext("Total mensajes: " + messageCount);
        }
    }

    static class PersistentWebSocketManager {
        private final PublishSubject<String> messageSubject = PublishSubject.create();
        
        public Observable<String> getMessageStream() {
            return messageSubject;
        }
        
        public void sendMessage(String mensaje) {
            messageSubject.onNext(mensaje);
            // Simular persistencia
            System.out.println("  [PERSIST] Guardando: " + mensaje);
        }
    }

    static class AuthenticatedWebSocketManager {
        private final PublishSubject<String> messageSubject = PublishSubject.create();
        private boolean authenticated = false;
        
        public Observable<String> getMessageStream() {
            return messageSubject;
        }
        
        public void authenticate(String usuario, String password) {
            if ("usuario123".equals(usuario) && "password123".equals(password)) {
                authenticated = true;
                System.out.println("  [AUTH] Usuario autenticado: " + usuario);
            } else {
                System.out.println("  [AUTH] Error de autenticación");
            }
        }
        
        public void logout() {
            authenticated = false;
            System.out.println("  [AUTH] Usuario deslogueado");
        }
        
        public void sendMessage(String mensaje) {
            if (authenticated) {
                messageSubject.onNext(mensaje);
            } else {
                System.out.println("  [AUTH] Mensaje rechazado - no autenticado");
            }
        }
    }

    static class TypedMessage {
        String tipo;
        String contenido;
        
        TypedMessage(String tipo, String contenido) {
            this.tipo = tipo;
            this.contenido = contenido;
        }
    }
}
