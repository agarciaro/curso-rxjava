package com.formadoresit.rxjava.introduccion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * INTRODUCCIÓN - Ejemplo 04: Publisher Asíncrono
 * 
 * Implementa emisión asíncrona de datos usando threads.
 * Esto demuestra la naturaleza no bloqueante de la programación reactiva.
 * 
 * CONCEPTOS:
 * - Emisión asíncrona: Los datos se emiten en un thread diferente
 * - No bloqueante: El hilo principal no espera por los datos
 * - Concurrencia: Múltiples suscripciones pueden ejecutarse en paralelo
 * - Schedulers: Control de en qué thread se ejecuta el código
 */
public class Ejemplo04_PublisherAsincrono {

    interface Subscriber<T> {
        void onNext(T item);
        void onComplete();
        void onError(Throwable error);
    }

    interface Publisher<T> {
        void subscribe(Subscriber<T> subscriber);
    }

    /**
     * Publisher que emite datos SÍNCRONAMENTE (en el thread del caller)
     */
    static class SyncPublisher implements Publisher<Integer> {
        private final int count;

        public SyncPublisher(int count) {
            this.count = count;
        }

        @Override
        public void subscribe(Subscriber<Integer> subscriber) {
            String threadName = Thread.currentThread().getName();
            System.out.println("🔵 [SYNC] Emitiendo en thread: " + threadName);
            
            try {
                for (int i = 1; i <= count; i++) {
                    System.out.println("  📤 [" + threadName + "] Emitiendo: " + i);
                    subscriber.onNext(i);
                    Thread.sleep(100); // Simular trabajo
                }
                subscriber.onComplete();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }
    }

    /**
     * Publisher que emite datos ASÍNCRONAMENTE (en un thread diferente)
     */
    static class AsyncPublisher implements Publisher<Integer> {
        private final int count;
        private final ExecutorService executor = Executors.newCachedThreadPool();

        public AsyncPublisher(int count) {
            this.count = count;
        }

        @Override
        public void subscribe(Subscriber<Integer> subscriber) {
            String callerThread = Thread.currentThread().getName();
            System.out.println("🟢 [ASYNC] Suscripción llamada desde: " + callerThread);
            System.out.println("  ⚡ Emitiendo en thread diferente...");
            
            // Ejecutar la emisión en un thread diferente
            executor.submit(() -> {
                String emitterThread = Thread.currentThread().getName();
                System.out.println("  🟢 [ASYNC] Emitiendo en thread: " + emitterThread);
                
                try {
                    for (int i = 1; i <= count; i++) {
                        System.out.println("    📤 [" + emitterThread + "] Emitiendo: " + i);
                        subscriber.onNext(i);
                        Thread.sleep(100); // Simular trabajo
                    }
                    subscriber.onComplete();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            });
        }

        public void shutdown() {
            executor.shutdown();
        }
    }

    /**
     * Subscriber que muestra en qué thread recibe los datos
     */
    static class ThreadAwareSubscriber implements Subscriber<Integer> {
        private final String name;

        public ThreadAwareSubscriber(String name) {
            this.name = name;
        }

        @Override
        public void onNext(Integer item) {
            String threadName = Thread.currentThread().getName();
            System.out.println("      📥 [" + name + "] Recibió " + item + " en thread: " + threadName);
        }

        @Override
        public void onComplete() {
            String threadName = Thread.currentThread().getName();
            System.out.println("      ✅ [" + name + "] Completado en thread: " + threadName);
        }

        @Override
        public void onError(Throwable error) {
            System.out.println("      ❌ [" + name + "] Error: " + error.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Publisher Asíncrono ===\n");
        System.out.println("Thread principal: " + Thread.currentThread().getName() + "\n");

        // EJEMPLO 1: Publisher Síncrono (bloquea el thread principal)
        System.out.println("--- Ejemplo 1: Publisher SÍNCRONO ---");
        Publisher<Integer> syncPublisher = new SyncPublisher(3);
        
        System.out.println("⏱️  Antes de suscribirse...");
        syncPublisher.subscribe(new ThreadAwareSubscriber("Sync-Sub"));
        System.out.println("⏱️  Después de suscribirse (BLOQUEÓ el thread principal)\n");

        Thread.sleep(500);

        // EJEMPLO 2: Publisher Asíncrono (NO bloquea el thread principal)
        System.out.println("--- Ejemplo 2: Publisher ASÍNCRONO ---");
        AsyncPublisher asyncPublisher = new AsyncPublisher(3);
        
        System.out.println("⏱️  Antes de suscribirse...");
        asyncPublisher.subscribe(new ThreadAwareSubscriber("Async-Sub"));
        System.out.println("⏱️  Después de suscribirse (NO BLOQUEÓ - continúa inmediatamente)");
        System.out.println("💡 El thread principal sigue ejecutándose mientras se emiten datos\n");

        // Esperar a que termine la emisión asíncrona
        Thread.sleep(1000);

        // EJEMPLO 3: Múltiples suscripciones asíncronas en paralelo
        System.out.println("--- Ejemplo 3: Múltiples suscripciones en PARALELO ---");
        AsyncPublisher parallelPublisher = new AsyncPublisher(5);
        
        parallelPublisher.subscribe(new ThreadAwareSubscriber("Sub-1"));
        parallelPublisher.subscribe(new ThreadAwareSubscriber("Sub-2"));
        parallelPublisher.subscribe(new ThreadAwareSubscriber("Sub-3"));
        
        System.out.println("💡 Tres suscripciones ejecutándose en paralelo\n");

        // Esperar a que terminen
        Thread.sleep(1500);

        asyncPublisher.shutdown();
        parallelPublisher.shutdown();

        System.out.println("\n📚 CONCEPTOS CLAVE:");
        System.out.println("1. SÍNCRONO: Emite en el mismo thread (BLOQUEA)");
        System.out.println("2. ASÍNCRONO: Emite en thread diferente (NO BLOQUEA)");
        System.out.println("3. La programación reactiva es inherentemente ASÍNCRONA");
        System.out.println("4. Permite CONCURRENCIA sin bloquear threads");
        System.out.println("5. RxJava usa SCHEDULERS para controlar la concurrencia");
        System.out.println("6. subscribeOn() y observeOn() controlan los threads");
    }
}

