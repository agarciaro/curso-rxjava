package com.formadoresit.rxjava.tema6;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.Observable;

/**
 * TEMA 6: Multicast
 * Ejemplo 10: Casos Reales
 * 
 * Casos de uso reales de multicast en aplicaciones:
 * sistemas de notificaciones, dashboards, chat, etc.
 */
public class Ejemplo10_CasosReales {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 10: Casos Reales ===\n");

        // 1. Sistema de notificaciones en tiempo real
        System.out.println("--- Sistema de notificaciones en tiempo real ---");
        NotificationSystem notificationSystem = new NotificationSystem();
        
        // Suscriptores (diferentes dispositivos/usuarios)
        notificationSystem.getNotificationStream()
            .subscribe(notif -> System.out.println("  [MOBILE] " + notif));
        
        notificationSystem.getNotificationStream()
            .subscribe(notif -> System.out.println("  [DESKTOP] " + notif));
        
        notificationSystem.getNotificationStream()
            .subscribe(notif -> System.out.println("  [EMAIL] " + notif));
        
        // Enviar notificaciones
        notificationSystem.sendNotification("Nuevo mensaje de Juan");
        notificationSystem.sendNotification("Recordatorio: Reunión a las 3pm");
        notificationSystem.sendNotification("Sistema actualizado exitosamente");

        // 2. Dashboard de métricas en tiempo real
        System.out.println("\n--- Dashboard de métricas en tiempo real ---");
        MetricsDashboard dashboard = new MetricsDashboard();
        
        // Suscriptores (diferentes widgets del dashboard)
        dashboard.getCpuStream()
            .subscribe(cpu -> System.out.println("  [CPU_WIDGET] " + cpu + "%"));
        
        dashboard.getMemoryStream()
            .subscribe(mem -> System.out.println("  [MEMORY_WIDGET] " + mem + "%"));
        
        dashboard.getDiskStream()
            .subscribe(disk -> System.out.println("  [DISK_WIDGET] " + disk + "%"));
        
        // Simular métricas
        dashboard.updateCpu(45.0);
        dashboard.updateMemory(60.0);
        dashboard.updateDisk(30.0);
        dashboard.updateCpu(50.0);
        dashboard.updateMemory(65.0);

        // 3. Sistema de chat en tiempo real
        System.out.println("\n--- Sistema de chat en tiempo real ---");
        ChatSystem chatSystem = new ChatSystem();
        
        // Suscriptores (diferentes usuarios)
        chatSystem.getMessageStream()
            .subscribe(msg -> System.out.println("  [USER1] " + msg.usuario + ": " + msg.mensaje));
        
        chatSystem.getMessageStream()
            .subscribe(msg -> System.out.println("  [USER2] " + msg.usuario + ": " + msg.mensaje));
        
        chatSystem.getMessageStream()
            .subscribe(msg -> System.out.println("  [USER3] " + msg.usuario + ": " + msg.mensaje));
        
        // Enviar mensajes
        chatSystem.sendMessage("Juan", "Hola a todos");
        chatSystem.sendMessage("María", "¿Cómo están?");
        chatSystem.sendMessage("Pedro", "Todo bien, gracias");

        // 4. Sistema de trading en tiempo real
        System.out.println("\n--- Sistema de trading en tiempo real ---");
        TradingSystem tradingSystem = new TradingSystem();
        
        // Suscriptores (diferentes traders)
        tradingSystem.getPriceStream()
            .subscribe(price -> System.out.println("  [TRADER1] Precio: $" + price));
        
        tradingSystem.getPriceStream()
            .subscribe(price -> System.out.println("  [TRADER2] Precio: $" + price));
        
        tradingSystem.getPriceStream()
            .subscribe(price -> System.out.println("  [TRADER3] Precio: $" + price));
        
        // Simular cambios de precio
        tradingSystem.updatePrice(100.50);
        tradingSystem.updatePrice(101.25);
        tradingSystem.updatePrice(99.75);
        tradingSystem.updatePrice(102.00);

        // 5. Sistema de monitoreo de aplicaciones
        System.out.println("\n--- Sistema de monitoreo de aplicaciones ---");
        ApplicationMonitor appMonitor = new ApplicationMonitor();
        
        // Suscriptores (diferentes sistemas de monitoreo)
        appMonitor.getLogStream()
            .subscribe(log -> System.out.println("  [LOG_ANALYZER] " + log));
        
        appMonitor.getErrorStream()
            .subscribe(error -> System.out.println("  [ERROR_TRACKER] " + error));
        
        appMonitor.getPerformanceStream()
            .subscribe(perf -> System.out.println("  [PERFORMANCE_MONITOR] " + perf));
        
        // Simular eventos
        appMonitor.logEvent("Usuario autenticado");
        appMonitor.logError("Error de conexión a BD");
        appMonitor.logPerformance("Tiempo de respuesta: 150ms");
        appMonitor.logEvent("Operación completada");

        // 6. Sistema de geolocalización
        System.out.println("\n--- Sistema de geolocalización ---");
        LocationSystem locationSystem = new LocationSystem();
        
        // Suscriptores (diferentes servicios)
        locationSystem.getLocationStream()
            .subscribe(loc -> System.out.println("  [MAP_SERVICE] Lat: " + loc.lat + ", Lon: " + loc.lon));
        
        locationSystem.getLocationStream()
            .subscribe(loc -> System.out.println("  [WEATHER_SERVICE] Ubicación: " + loc.lat + ", " + loc.lon));
        
        locationSystem.getLocationStream()
            .subscribe(loc -> System.out.println("  [NAVIGATION_SERVICE] Destino: " + loc.lat + ", " + loc.lon));
        
        // Simular cambios de ubicación
        locationSystem.updateLocation(40.4168, -3.7038);
        locationSystem.updateLocation(41.3851, 2.1734);
        locationSystem.updateLocation(43.2627, -2.9253);

        // 7. Sistema de inventario en tiempo real
        System.out.println("\n--- Sistema de inventario en tiempo real ---");
        InventorySystem inventorySystem = new InventorySystem();
        
        // Suscriptores (diferentes departamentos)
        inventorySystem.getInventoryStream()
            .subscribe(inv -> System.out.println("  [SALES] Stock: " + inv.stock + ", Precio: $" + inv.precio));
        
        inventorySystem.getInventoryStream()
            .subscribe(inv -> System.out.println("  [WAREHOUSE] Stock: " + inv.stock + ", Precio: $" + inv.precio));
        
        inventorySystem.getInventoryStream()
            .subscribe(inv -> System.out.println("  [ACCOUNTING] Stock: " + inv.stock + ", Precio: $" + inv.precio));
        
        // Simular cambios de inventario
        inventorySystem.updateInventory("Producto A", 100, 25.99);
        inventorySystem.updateInventory("Producto A", 80, 29.99);
        inventorySystem.updateInventory("Producto A", 60, 29.99);

        // 8. Sistema de configuración dinámica
        System.out.println("\n--- Sistema de configuración dinámica ---");
        ConfigurationSystem configSystem = new ConfigurationSystem();
        
        // Suscriptores (diferentes módulos)
        configSystem.getConfigStream()
            .subscribe(config -> System.out.println("  [UI_MODULE] Tema: " + config.tema + ", Idioma: " + config.idioma));
        
        configSystem.getConfigStream()
            .subscribe(config -> System.out.println("  [API_MODULE] Tema: " + config.tema + ", Idioma: " + config.idioma));
        
        configSystem.getConfigStream()
            .subscribe(config -> System.out.println("  [DB_MODULE] Tema: " + config.tema + ", Idioma: " + config.idioma));
        
        // Cambiar configuración
        configSystem.updateConfig("oscuro", "español");
        configSystem.updateConfig("claro", "inglés");
        configSystem.updateConfig("auto", "francés");

        // 9. Sistema de eventos de usuario
        System.out.println("\n--- Sistema de eventos de usuario ---");
        UserEventSystem userEventSystem = new UserEventSystem();
        
        // Suscriptores (diferentes analíticos)
        userEventSystem.getEventStream()
            .subscribe(event -> System.out.println("  [ANALYTICS] " + event.tipo + ": " + event.datos));
        
        userEventSystem.getEventStream()
            .subscribe(event -> System.out.println("  [AUDIT] " + event.tipo + ": " + event.datos));
        
        userEventSystem.getEventStream()
            .subscribe(event -> System.out.println("  [RECOMMENDATION] " + event.tipo + ": " + event.datos));
        
        // Simular eventos de usuario
        userEventSystem.trackEvent("LOGIN", "usuario123");
        userEventSystem.trackEvent("PURCHASE", "producto456");
        userEventSystem.trackEvent("LOGOUT", "usuario123");

        // 10. Sistema de alertas
        System.out.println("\n--- Sistema de alertas ---");
        AlertSystem alertSystem = new AlertSystem();
        
        // Suscriptores (diferentes canales de alerta)
        alertSystem.getAlertStream()
            .subscribe(alert -> System.out.println("  [SMS] " + alert.tipo + ": " + alert.mensaje));
        
        alertSystem.getAlertStream()
            .subscribe(alert -> System.out.println("  [EMAIL] " + alert.tipo + ": " + alert.mensaje));
        
        alertSystem.getAlertStream()
            .subscribe(alert -> System.out.println("  [PUSH] " + alert.tipo + ": " + alert.mensaje));
        
        // Simular alertas
        alertSystem.sendAlert("CRITICAL", "Sistema caído");
        alertSystem.sendAlert("WARNING", "Memoria baja");
        alertSystem.sendAlert("INFO", "Backup completado");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Notificaciones: PublishSubject para eventos en tiempo real");
        System.out.println("• Dashboards: BehaviorSubject para estado actual");
        System.out.println("• Chat: PublishSubject para mensajes instantáneos");
        System.out.println("• Trading: PublishSubject para precios en tiempo real");
        System.out.println("• Monitoreo: Múltiples Subjects para diferentes tipos de eventos");
        System.out.println("• Geolocalización: BehaviorSubject para ubicación actual");
        System.out.println("• Inventario: BehaviorSubject para estado del inventario");
        System.out.println("• Configuración: BehaviorSubject para configuración actual");
        System.out.println("• Eventos: PublishSubject para tracking de eventos");
        System.out.println("• Alertas: PublishSubject para notificaciones críticas");
    }

    // Implementaciones de diferentes sistemas

    static class NotificationSystem {
        private final PublishSubject<String> notificationSubject = PublishSubject.create();
        
        public Observable<String> getNotificationStream() {
            return notificationSubject;
        }
        
        public void sendNotification(String mensaje) {
            notificationSubject.onNext(mensaje);
        }
    }

    static class MetricsDashboard {
        private final BehaviorSubject<Double> cpuSubject = BehaviorSubject.createDefault(0.0);
        private final BehaviorSubject<Double> memorySubject = BehaviorSubject.createDefault(0.0);
        private final BehaviorSubject<Double> diskSubject = BehaviorSubject.createDefault(0.0);
        
        public Observable<Double> getCpuStream() {
            return cpuSubject;
        }
        
        public Observable<Double> getMemoryStream() {
            return memorySubject;
        }
        
        public Observable<Double> getDiskStream() {
            return diskSubject;
        }
        
        public void updateCpu(double cpu) {
            cpuSubject.onNext(cpu);
        }
        
        public void updateMemory(double memory) {
            memorySubject.onNext(memory);
        }
        
        public void updateDisk(double disk) {
            diskSubject.onNext(disk);
        }
    }

    static class ChatSystem {
        private final PublishSubject<ChatMessage> messageSubject = PublishSubject.create();
        
        public Observable<ChatMessage> getMessageStream() {
            return messageSubject;
        }
        
        public void sendMessage(String usuario, String mensaje) {
            messageSubject.onNext(new ChatMessage(usuario, mensaje));
        }
    }

    static class TradingSystem {
        private final PublishSubject<Double> priceSubject = PublishSubject.create();
        
        public Observable<Double> getPriceStream() {
            return priceSubject;
        }
        
        public void updatePrice(double precio) {
            priceSubject.onNext(precio);
        }
    }

    static class ApplicationMonitor {
        private final PublishSubject<String> logSubject = PublishSubject.create();
        private final PublishSubject<String> errorSubject = PublishSubject.create();
        private final PublishSubject<String> performanceSubject = PublishSubject.create();
        
        public Observable<String> getLogStream() {
            return logSubject;
        }
        
        public Observable<String> getErrorStream() {
            return errorSubject;
        }
        
        public Observable<String> getPerformanceStream() {
            return performanceSubject;
        }
        
        public void logEvent(String evento) {
            logSubject.onNext(evento);
        }
        
        public void logError(String error) {
            errorSubject.onNext(error);
        }
        
        public void logPerformance(String performance) {
            performanceSubject.onNext(performance);
        }
    }

    static class LocationSystem {
        private final BehaviorSubject<Location> locationSubject = BehaviorSubject.createDefault(new Location(0.0, 0.0));
        
        public Observable<Location> getLocationStream() {
            return locationSubject;
        }
        
        public void updateLocation(double lat, double lon) {
            locationSubject.onNext(new Location(lat, lon));
        }
    }

    static class InventorySystem {
        private final BehaviorSubject<InventoryItem> inventorySubject = BehaviorSubject.createDefault(new InventoryItem("", 0, 0.0));
        
        public Observable<InventoryItem> getInventoryStream() {
            return inventorySubject;
        }
        
        public void updateInventory(String producto, int stock, double precio) {
            inventorySubject.onNext(new InventoryItem(producto, stock, precio));
        }
    }

    static class ConfigurationSystem {
        private final BehaviorSubject<Configuration> configSubject = BehaviorSubject.createDefault(new Configuration("", ""));
        
        public Observable<Configuration> getConfigStream() {
            return configSubject;
        }
        
        public void updateConfig(String tema, String idioma) {
            configSubject.onNext(new Configuration(tema, idioma));
        }
    }

    static class UserEventSystem {
        private final PublishSubject<UserEvent> eventSubject = PublishSubject.create();
        
        public Observable<UserEvent> getEventStream() {
            return eventSubject;
        }
        
        public void trackEvent(String tipo, String datos) {
            eventSubject.onNext(new UserEvent(tipo, datos));
        }
    }

    static class AlertSystem {
        private final PublishSubject<Alert> alertSubject = PublishSubject.create();
        
        public Observable<Alert> getAlertStream() {
            return alertSubject;
        }
        
        public void sendAlert(String tipo, String mensaje) {
            alertSubject.onNext(new Alert(tipo, mensaje));
        }
    }

    // Clases de datos

    static class ChatMessage {
        String usuario;
        String mensaje;
        
        ChatMessage(String usuario, String mensaje) {
            this.usuario = usuario;
            this.mensaje = mensaje;
        }
    }

    static class Location {
        double lat;
        double lon;
        
        Location(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    static class InventoryItem {
        String producto;
        int stock;
        double precio;
        
        InventoryItem(String producto, int stock, double precio) {
            this.producto = producto;
            this.stock = stock;
            this.precio = precio;
        }
    }

    static class Configuration {
        String tema;
        String idioma;
        
        Configuration(String tema, String idioma) {
            this.tema = tema;
            this.idioma = idioma;
        }
    }

    static class UserEvent {
        String tipo;
        String datos;
        
        UserEvent(String tipo, String datos) {
            this.tipo = tipo;
            this.datos = datos;
        }
    }

    static class Alert {
        String tipo;
        String mensaje;
        
        Alert(String tipo, String mensaje) {
            this.tipo = tipo;
            this.mensaje = mensaje;
        }
    }
}
