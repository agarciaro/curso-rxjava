package com.formadoresit.rxjava.tema6;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.Observable;

/**
 * TEMA 6: Multicast
 * Ejemplo 08: Data Sharing
 * 
 * Compartir datos entre componentes usando multicast
 * para evitar duplicación de datos y optimizar recursos
 */
public class Ejemplo08_DataSharing {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 08: Data Sharing ===\n");

        // 1. Compartir datos de usuario
        System.out.println("--- Compartir datos de usuario ---");
        UserDataManager userDataManager = new UserDataManager();
        
        // Componentes que necesitan datos de usuario
        userDataManager.getUserStream()
            .subscribe(usuario -> System.out.println("  [UI] Usuario: " + usuario.nombre));
        
        userDataManager.getUserStream()
            .subscribe(usuario -> System.out.println("  [LOGGER] Logging usuario: " + usuario.nombre));
        
        userDataManager.getUserStream()
            .subscribe(usuario -> System.out.println("  [CACHE] Cacheando usuario: " + usuario.nombre));
        
        // Cambiar usuario
        userDataManager.setUser(new Usuario("Juan", "juan@email.com"));
        userDataManager.setUser(new Usuario("María", "maria@email.com"));

        // 2. Compartir datos de configuración
        System.out.println("\n--- Compartir datos de configuración ---");
        ConfigDataManager configDataManager = new ConfigDataManager();
        
        // Componentes que necesitan configuración
        configDataManager.getConfigStream()
            .subscribe(config -> System.out.println("  [THEME] Tema: " + config.tema));
        
        configDataManager.getConfigStream()
            .subscribe(config -> System.out.println("  [LANGUAGE] Idioma: " + config.idioma));
        
        configDataManager.getConfigStream()
            .subscribe(config -> System.out.println("  [NOTIFICATIONS] Notificaciones: " + config.notificaciones));
        
        // Cambiar configuración
        configDataManager.setConfig(new Configuracion("oscuro", "español", true));
        configDataManager.setConfig(new Configuracion("claro", "inglés", false));

        // 3. Compartir datos de sesión
        System.out.println("\n--- Compartir datos de sesión ---");
        SessionDataManager sessionDataManager = new SessionDataManager();
        
        // Componentes que necesitan datos de sesión
        sessionDataManager.getSessionStream()
            .subscribe(sesion -> System.out.println("  [AUTH] Sesión: " + sesion.estado));
        
        sessionDataManager.getSessionStream()
            .subscribe(sesion -> System.out.println("  [NAV] Navegación: " + sesion.estado));
        
        sessionDataManager.getSessionStream()
            .subscribe(sesion -> System.out.println("  [PERMISSIONS] Permisos: " + sesion.estado));
        
        // Cambiar estado de sesión
        sessionDataManager.setSession(new Sesion("INICIANDO", "usuario123"));
        sessionDataManager.setSession(new Sesion("AUTENTICADO", "usuario123"));
        sessionDataManager.setSession(new Sesion("ACTIVO", "usuario123"));

        // 4. Compartir datos de inventario
        System.out.println("\n--- Compartir datos de inventario ---");
        InventoryDataManager inventoryDataManager = new InventoryDataManager();
        
        // Componentes que necesitan datos de inventario
        inventoryDataManager.getInventoryStream()
            .subscribe(inventario -> System.out.println("  [STOCK] Stock: " + inventario.stock));
        
        inventoryDataManager.getInventoryStream()
            .subscribe(inventario -> System.out.println("  [PRICE] Precio: " + inventario.precio));
        
        inventoryDataManager.getInventoryStream()
            .subscribe(inventario -> System.out.println("  [ALERTS] Alertas: " + inventario.stock));
        
        // Cambiar inventario
        inventoryDataManager.setInventory(new Inventario("Producto A", 100, 25.99));
        inventoryDataManager.setInventory(new Inventario("Producto A", 50, 29.99));
        inventoryDataManager.setInventory(new Inventario("Producto A", 5, 29.99));

        // 5. Compartir datos de métricas
        System.out.println("\n--- Compartir datos de métricas ---");
        MetricsDataManager metricsDataManager = new MetricsDataManager();
        
        // Componentes que necesitan métricas
        metricsDataManager.getMetricsStream()
            .subscribe(metric -> System.out.println("  [DASHBOARD] " + metric));
        
        metricsDataManager.getMetricsStream()
            .subscribe(metric -> System.out.println("  [ALERTS] " + metric));
        
        metricsDataManager.getMetricsStream()
            .subscribe(metric -> System.out.println("  [REPORTS] " + metric));
        
        // Actualizar métricas
        metricsDataManager.updateMetrics("CPU: 45%");
        metricsDataManager.updateMetrics("Memoria: 60%");
        metricsDataManager.updateMetrics("Disco: 30%");

        // 6. Compartir datos de notificaciones
        System.out.println("\n--- Compartir datos de notificaciones ---");
        NotificationDataManager notificationDataManager = new NotificationDataManager();
        
        // Componentes que necesitan notificaciones
        notificationDataManager.getNotificationStream()
            .subscribe(notif -> System.out.println("  [UI] " + notif.mensaje));
        
        notificationDataManager.getNotificationStream()
            .subscribe(notif -> System.out.println("  [SOUND] " + notif.mensaje));
        
        notificationDataManager.getNotificationStream()
            .subscribe(notif -> System.out.println("  [HISTORY] " + notif.mensaje));
        
        // Enviar notificaciones
        notificationDataManager.sendNotification(new Notificacion("INFO", "Sistema iniciado"));
        notificationDataManager.sendNotification(new Notificacion("WARNING", "Memoria baja"));
        notificationDataManager.sendNotification(new Notificacion("ERROR", "Error de conexión"));

        // 7. Compartir datos de geolocalización
        System.out.println("\n--- Compartir datos de geolocalización ---");
        LocationDataManager locationDataManager = new LocationDataManager();
        
        // Componentes que necesitan ubicación
        locationDataManager.getLocationStream()
            .subscribe(loc -> System.out.println("  [MAP] Lat: " + loc.latitud + ", Lon: " + loc.longitud));
        
        locationDataManager.getLocationStream()
            .subscribe(loc -> System.out.println("  [WEATHER] Ubicación: " + loc.latitud + ", " + loc.longitud));
        
        locationDataManager.getLocationStream()
            .subscribe(loc -> System.out.println("  [NAVIGATION] Destino: " + loc.latitud + ", " + loc.longitud));
        
        // Cambiar ubicación
        locationDataManager.setLocation(new Ubicacion(40.4168, -3.7038));
        locationDataManager.setLocation(new Ubicacion(41.3851, 2.1734));
        locationDataManager.setLocation(new Ubicacion(43.2627, -2.9253));

        // 8. Compartir datos de chat
        System.out.println("\n--- Compartir datos de chat ---");
        ChatDataManager chatDataManager = new ChatDataManager();
        
        // Componentes de chat
        chatDataManager.getMessageStream()
            .subscribe(msg -> System.out.println("  [CHAT_UI] " + msg.usuario + ": " + msg.mensaje));
        
        chatDataManager.getMessageStream()
            .subscribe(msg -> System.out.println("  [CHAT_HISTORY] " + msg.usuario + ": " + msg.mensaje));
        
        chatDataManager.getMessageStream()
            .subscribe(msg -> System.out.println("  [CHAT_MODERATION] " + msg.usuario + ": " + msg.mensaje));
        
        // Enviar mensajes
        chatDataManager.sendMessage(new MensajeChat("Juan", "Hola a todos"));
        chatDataManager.sendMessage(new MensajeChat("María", "¿Cómo están?"));
        chatDataManager.sendMessage(new MensajeChat("Pedro", "Todo bien"));

        // 9. Compartir datos de transacciones
        System.out.println("\n--- Compartir datos de transacciones ---");
        TransactionDataManager transactionDataManager = new TransactionDataManager();
        
        // Componentes que necesitan transacciones
        transactionDataManager.getTransactionStream()
            .subscribe(txn -> System.out.println("  [PAYMENT] " + txn.id + " - $" + txn.monto));
        
        transactionDataManager.getTransactionStream()
            .subscribe(txn -> System.out.println("  [ACCOUNTING] " + txn.id + " - $" + txn.monto));
        
        transactionDataManager.getTransactionStream()
            .subscribe(txn -> System.out.println("  [AUDIT] " + txn.id + " - $" + txn.monto));
        
        // Procesar transacciones
        transactionDataManager.processTransaction(new Transaccion("TXN001", 100.00));
        transactionDataManager.processTransaction(new Transaccion("TXN002", 250.50));
        transactionDataManager.processTransaction(new Transaccion("TXN003", 75.25));

        // 10. Compartir datos de configuración de red
        System.out.println("\n--- Compartir datos de configuración de red ---");
        NetworkDataManager networkDataManager = new NetworkDataManager();
        
        // Componentes que necesitan configuración de red
        networkDataManager.getNetworkStream()
            .subscribe(net -> System.out.println("  [CONNECTION] " + net.estado));
        
        networkDataManager.getNetworkStream()
            .subscribe(net -> System.out.println("  [MONITORING] " + net.estado));
        
        networkDataManager.getNetworkStream()
            .subscribe(net -> System.out.println("  [LOGGING] " + net.estado));
        
        // Cambiar estado de red
        networkDataManager.setNetworkState(new NetworkState("CONNECTING"));
        networkDataManager.setNetworkState(new NetworkState("CONNECTED"));
        networkDataManager.setNetworkState(new NetworkState("DISCONNECTED"));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• BehaviorSubject: Ideal para datos de estado");
        System.out.println("• PublishSubject: Para eventos y notificaciones");
        System.out.println("• Compartir datos: Evitar duplicación y optimizar recursos");
        System.out.println("• Múltiples suscriptores: Diferentes componentes pueden usar los mismos datos");
        System.out.println("• Actualizaciones automáticas: Todos los suscriptores reciben cambios");
        System.out.println("• Tipos de datos: Usuario, configuración, sesión, inventario, etc.");
        System.out.println("• Patrones comunes: Data managers para diferentes tipos de datos");
    }

    // Implementaciones de diferentes tipos de Data Managers

    static class UserDataManager {
        private final BehaviorSubject<Usuario> userSubject = BehaviorSubject.createDefault(new Usuario("", ""));
        
        public Observable<Usuario> getUserStream() {
            return userSubject;
        }
        
        public void setUser(Usuario usuario) {
            userSubject.onNext(usuario);
        }
    }

    static class ConfigDataManager {
        private final BehaviorSubject<Configuracion> configSubject = BehaviorSubject.createDefault(new Configuracion("", "", false));
        
        public Observable<Configuracion> getConfigStream() {
            return configSubject;
        }
        
        public void setConfig(Configuracion config) {
            configSubject.onNext(config);
        }
    }

    static class SessionDataManager {
        private final BehaviorSubject<Sesion> sessionSubject = BehaviorSubject.createDefault(new Sesion("", ""));
        
        public Observable<Sesion> getSessionStream() {
            return sessionSubject;
        }
        
        public void setSession(Sesion sesion) {
            sessionSubject.onNext(sesion);
        }
    }

    static class InventoryDataManager {
        private final BehaviorSubject<Inventario> inventorySubject = BehaviorSubject.createDefault(new Inventario("", 0, 0.0));
        
        public Observable<Inventario> getInventoryStream() {
            return inventorySubject;
        }
        
        public void setInventory(Inventario inventario) {
            inventorySubject.onNext(inventario);
        }
    }

    static class MetricsDataManager {
        private final PublishSubject<String> metricsSubject = PublishSubject.create();
        
        public Observable<String> getMetricsStream() {
            return metricsSubject;
        }
        
        public void updateMetrics(String metric) {
            metricsSubject.onNext(metric);
        }
    }

    static class NotificationDataManager {
        private final PublishSubject<Notificacion> notificationSubject = PublishSubject.create();
        
        public Observable<Notificacion> getNotificationStream() {
            return notificationSubject;
        }
        
        public void sendNotification(Notificacion notificacion) {
            notificationSubject.onNext(notificacion);
        }
    }

    static class LocationDataManager {
        private final BehaviorSubject<Ubicacion> locationSubject = BehaviorSubject.createDefault(new Ubicacion(0.0, 0.0));
        
        public Observable<Ubicacion> getLocationStream() {
            return locationSubject;
        }
        
        public void setLocation(Ubicacion ubicacion) {
            locationSubject.onNext(ubicacion);
        }
    }

    static class ChatDataManager {
        private final PublishSubject<MensajeChat> messageSubject = PublishSubject.create();
        
        public Observable<MensajeChat> getMessageStream() {
            return messageSubject;
        }
        
        public void sendMessage(MensajeChat mensaje) {
            messageSubject.onNext(mensaje);
        }
    }

    static class TransactionDataManager {
        private final PublishSubject<Transaccion> transactionSubject = PublishSubject.create();
        
        public Observable<Transaccion> getTransactionStream() {
            return transactionSubject;
        }
        
        public void processTransaction(Transaccion transaccion) {
            transactionSubject.onNext(transaccion);
        }
    }

    static class NetworkDataManager {
        private final BehaviorSubject<NetworkState> networkSubject = BehaviorSubject.createDefault(new NetworkState(""));
        
        public Observable<NetworkState> getNetworkStream() {
            return networkSubject;
        }
        
        public void setNetworkState(NetworkState estado) {
            networkSubject.onNext(estado);
        }
    }

    // Clases de datos

    static class Usuario {
        String nombre;
        String email;
        
        Usuario(String nombre, String email) {
            this.nombre = nombre;
            this.email = email;
        }
    }

    static class Configuracion {
        String tema;
        String idioma;
        boolean notificaciones;
        
        Configuracion(String tema, String idioma, boolean notificaciones) {
            this.tema = tema;
            this.idioma = idioma;
            this.notificaciones = notificaciones;
        }
    }

    static class Sesion {
        String estado;
        String usuarioId;
        
        Sesion(String estado, String usuarioId) {
            this.estado = estado;
            this.usuarioId = usuarioId;
        }
    }

    static class Inventario {
        String producto;
        int stock;
        double precio;
        
        Inventario(String producto, int stock, double precio) {
            this.producto = producto;
            this.stock = stock;
            this.precio = precio;
        }
    }

    static class Notificacion {
        String tipo;
        String mensaje;
        
        Notificacion(String tipo, String mensaje) {
            this.tipo = tipo;
            this.mensaje = mensaje;
        }
    }

    static class Ubicacion {
        double latitud;
        double longitud;
        
        Ubicacion(double latitud, double longitud) {
            this.latitud = latitud;
            this.longitud = longitud;
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

    static class Transaccion {
        String id;
        double monto;
        
        Transaccion(String id, double monto) {
            this.id = id;
            this.monto = monto;
        }
    }

    static class NetworkState {
        String estado;
        
        NetworkState(String estado) {
            this.estado = estado;
        }
    }
}
