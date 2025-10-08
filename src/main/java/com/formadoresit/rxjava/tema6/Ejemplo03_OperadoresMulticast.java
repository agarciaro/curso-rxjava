package com.formadoresit.rxjava.tema6;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 6: Multicast
 * Ejemplo 03: Operadores de Multicast
 * 
 * share(), replay(), cache() y otros operadores que facilitan el multicast
 * Útil para optimizar recursos y compartir datos entre múltiples suscriptores
 */
public class Ejemplo03_OperadoresMulticast {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 03: Operadores de Multicast ===\n");

        // 1. share() - Convierte a multicast automáticamente
        System.out.println("--- share() ---");
        Observable<String> sharedObservable = Observable.just("A", "B", "C")
            .doOnNext(item -> System.out.println("  Procesando: " + item))
            .share(); // Convierte a multicast

        System.out.println("Suscriptor 1:");
        sharedObservable.subscribe(item -> System.out.println("  Sub1: " + item));
        
        System.out.println("Suscriptor 2:");
        sharedObservable.subscribe(item -> System.out.println("  Sub2: " + item));

        // 2. replay() - ReplaySubject automático
        System.out.println("\n--- replay() ---");
        Observable<String> replayedObservable = Observable.just("X", "Y", "Z")
            .doOnNext(item -> System.out.println("  Emitiendo: " + item))
            .replay(); // Convierte a ReplaySubject

        // Emitir primero
        replayedObservable.subscribe(item -> System.out.println("  Sub1: " + item));
        
        // Luego suscribirse (recibe todos los valores anteriores)
        Thread.sleep(100);
        replayedObservable.subscribe(item -> System.out.println("  Sub2: " + item));

        // 3. replay() con límite de tiempo
        System.out.println("\n--- replay() con límite de tiempo ---");
        Observable<String> timeReplayed = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> "Item-" + i)
            .doOnNext(item -> System.out.println("  Emitiendo: " + item))
            .replay(200, TimeUnit.MILLISECONDS); // Replay últimos 200ms

        timeReplayed.subscribe(item -> System.out.println("  Sub1: " + item));
        
        Thread.sleep(300); // Esperar un poco
        
        timeReplayed.subscribe(item -> System.out.println("  Sub2: " + item));
        
        Thread.sleep(500);

        // 4. replay() con límite de elementos
        System.out.println("\n--- replay() con límite de elementos ---");
        Observable<String> sizeReplayed = Observable.just("1", "2", "3", "4", "5")
            .doOnNext(item -> System.out.println("  Emitiendo: " + item))
            .replay(2); // Solo últimos 2 elementos

        sizeReplayed.subscribe(item -> System.out.println("  Sub1: " + item));
        
        Thread.sleep(100);
        
        sizeReplayed.subscribe(item -> System.out.println("  Sub2: " + item));

        // 5. cache() - Almacena todos los valores
        System.out.println("\n--- cache() ---");
        Observable<String> cachedObservable = Observable.just("Cache1", "Cache2", "Cache3")
            .doOnNext(item -> System.out.println("  Procesando: " + item))
            .cache(); // Almacena todos los valores

        System.out.println("Primera suscripción:");
        cachedObservable.subscribe(item -> System.out.println("  Sub1: " + item));
        
        System.out.println("Segunda suscripción (desde cache):");
        cachedObservable.subscribe(item -> System.out.println("  Sub2: " + item));

        // 6. cache() con Observable de red
        System.out.println("\n--- cache() con Observable de red ---");
        Observable<String> networkObservable = simularLlamadaRed()
            .doOnNext(item -> System.out.println("  Llamada de red: " + item))
            .cache();

        System.out.println("Primera llamada (real):");
        networkObservable.subscribe(item -> System.out.println("  Sub1: " + item));
        
        Thread.sleep(100);
        
        System.out.println("Segunda llamada (desde cache):");
        networkObservable.subscribe(item -> System.out.println("  Sub2: " + item));

        // 7. share() con refCount()
        System.out.println("\n--- share() con refCount() ---");
        Observable<String> sharedRefCount = Observable.interval(100, TimeUnit.MILLISECONDS)
            .take(4)
            .map(i -> "Shared-" + i)
            .doOnNext(item -> System.out.println("  Emitiendo: " + item))
            .doOnDispose(() -> System.out.println("  Disposed"))
            .share(); // share() ya incluye refCount automáticamente

        System.out.println("Suscriptor 1:");
        var sub1 = sharedRefCount.subscribe(item -> System.out.println("  Sub1: " + item));
        
        Thread.sleep(150);
        
        System.out.println("Suscriptor 2:");
        var sub2 = sharedRefCount.subscribe(item -> System.out.println("  Sub2: " + item));
        
        Thread.sleep(200);
        
        System.out.println("Cancelando sub1:");
        sub1.dispose();
        
        Thread.sleep(200);
        
        System.out.println("Cancelando sub2:");
        sub2.dispose();
        
        Thread.sleep(100);

        // 8. publish() con operadores
        System.out.println("\n--- publish() con operadores ---");
        ConnectableObservable<String> published = Observable.just("A", "B", "C", "D", "E")
            .doOnNext(item -> System.out.println("  Procesando: " + item))
            .publish();

        // Aplicar operadores después de publish
        published
            .filter(item -> item.length() == 1)
            .subscribe(item -> System.out.println("  Filtrado: " + item));

        published
            .map(String::toLowerCase)
            .subscribe(item -> System.out.println("  Minúsculas: " + item));

        System.out.println("Conectando...");
        published.connect();

        // 9. Ejemplo práctico: API compartida
        System.out.println("\n--- Ejemplo práctico: API compartida ---");
        Observable<String> apiData = simularAPICall()
            .doOnNext(item -> System.out.println("  API call: " + item))
            .share(); // Compartir entre múltiples componentes

        // Componente 1: Mostrar datos
        apiData.subscribe(data -> System.out.println("  [UI] Mostrando: " + data));

        // Componente 2: Logging
        apiData.subscribe(data -> System.out.println("  [LOG] Registrando: " + data));

        // Componente 3: Cache
        apiData.subscribe(data -> System.out.println("  [CACHE] Almacenando: " + data));

        // 10. Ejemplo práctico: Sensor compartido
        System.out.println("\n--- Ejemplo práctico: Sensor compartido ---");
        Observable<Double> sensorData = simularSensor()
            .doOnNext(data -> System.out.println("  Sensor: " + data))
            .replay(1); // Solo último valor

        // Monitor 1: Alerta si temperatura > 30
        sensorData
            .filter(temp -> temp > 30)
            .subscribe(temp -> System.out.println("  [ALERTA] Temperatura alta: " + temp));

        // Monitor 2: Promedio
        sensorData
            .buffer(3)
            .map(temps -> temps.stream().mapToDouble(Double::doubleValue).average().orElse(0))
            .subscribe(avg -> System.out.println("  [PROMEDIO] " + avg));

        // Monitor 3: Histórico
        sensorData.subscribe(temp -> System.out.println("  [HISTÓRICO] " + temp));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• share(): Convierte a multicast automáticamente");
        System.out.println("• replay(): ReplaySubject con configuración automática");
        System.out.println("• cache(): Almacena todos los valores para suscriptores tardíos");
        System.out.println("• publish(): Control manual del multicast");
        System.out.println("• refCount(): Conecta/desconecta automáticamente");
        System.out.println("• Útil para optimizar recursos y compartir datos");
    }

    private static Observable<String> simularLlamadaRed() {
        return Observable.just("Datos de red")
            .delay(200, TimeUnit.MILLISECONDS);
    }

    private static Observable<String> simularAPICall() {
        return Observable.just("Usuario", "Productos", "Pedidos")
            .delay(100, TimeUnit.MILLISECONDS);
    }

    private static Observable<Double> simularSensor() {
        return Observable.interval(150, TimeUnit.MILLISECONDS)
            .take(6)
            .map(i -> 25.0 + Math.random() * 10); // Temperatura entre 25-35
    }
}
