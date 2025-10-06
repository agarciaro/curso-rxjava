package com.formadoresit.rxjava.introduccion;

/**
 * INTRODUCCIÓN - Ejemplo 02: Publisher con Cancelación (Subscription)
 * 
 * Implementa el concepto de Subscription que permite cancelar el flujo de datos.
 * Este es un concepto crucial en programación reactiva.
 * 
 * CONCEPTOS:
 * - Subscription: Representa la conexión activa entre Publisher y Subscriber
 * - Cancelación: Permite detener el flujo de datos en cualquier momento
 * - Control de flujo: El Subscriber puede controlar cuándo recibir datos
 */
public class Ejemplo02_PublisherConCancelacion {

    /**
     * Interfaz Subscription - Representa una suscripción activa
     */
    interface Subscription {
        void cancel();  // Cancela la suscripción
        boolean isCancelled();
    }

    /**
     * Implementación de Subscription
     */
    static class SimpleSubscription implements Subscription {
        private boolean cancelled = false;

        @Override
        public void cancel() {
            if (!cancelled) {
                cancelled = true;
                System.out.println("🛑 Suscripción cancelada");
            }
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }
    }

    /**
     * Subscriber mejorado que recibe una Subscription
     */
    interface Subscriber<T> {
        void onSubscribe(Subscription subscription);
        void onNext(T item);
        void onComplete();
        void onError(Throwable error);
    }

    /**
     * Publisher mejorado
     */
    interface Publisher<T> {
        void subscribe(Subscriber<T> subscriber);
    }

    /**
     * Publisher que emite números infinitamente (hasta que se cancele)
     */
    static class InfiniteNumberPublisher implements Publisher<Integer> {
        private final int delayMs;

        public InfiniteNumberPublisher(int delayMs) {
            this.delayMs = delayMs;
        }

        @Override
        public void subscribe(Subscriber<Integer> subscriber) {
            SimpleSubscription subscription = new SimpleSubscription();
            
            // Notificar al subscriber que se suscribió
            subscriber.onSubscribe(subscription);
            
            try {
                int number = 1;
                while (!subscription.isCancelled()) {
                    System.out.println("📤 Emitiendo: " + number);
                    subscriber.onNext(number);
                    number++;
                    
                    // Simulamos delay
                    if (delayMs > 0) {
                        Thread.sleep(delayMs);
                    }
                }
                
                System.out.println("⏹️  Publisher detenido por cancelación");
                
            } catch (InterruptedException e) {
                subscriber.onError(e);
            }
        }
    }

    /**
     * Subscriber que se auto-cancela después de N elementos
     */
    static class LimitedSubscriber implements Subscriber<Integer> {
        private final int limit;
        private int count = 0;
        private Subscription subscription;

        public LimitedSubscriber(int limit) {
            this.limit = limit;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            System.out.println("🔗 Suscrito (límite: " + limit + " elementos)");
        }

        @Override
        public void onNext(Integer item) {
            count++;
            System.out.println("  📥 Recibido: " + item + " (" + count + "/" + limit + ")");
            
            // Auto-cancelar al alcanzar el límite
            if (count >= limit) {
                System.out.println("  ⚠️  Límite alcanzado, cancelando...");
                subscription.cancel();
            }
        }

        @Override
        public void onComplete() {
            System.out.println("  ✅ Completado");
        }

        @Override
        public void onError(Throwable error) {
            System.out.println("  ❌ Error: " + error.getMessage());
        }
    }

    /**
     * Subscriber que se cancela en una condición específica
     */
    static class ConditionalSubscriber implements Subscriber<Integer> {
        private Subscription subscription;

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            System.out.println("🔗 Suscrito (se cancelará cuando llegue un número > 5)");
        }

        @Override
        public void onNext(Integer item) {
            System.out.println("  📥 Recibido: " + item);
            
            // Cancelar si el número es mayor que 5
            if (item > 5) {
                System.out.println("  ⚠️  Número > 5 detectado, cancelando...");
                subscription.cancel();
            }
        }

        @Override
        public void onComplete() {
            System.out.println("  ✅ Completado");
        }

        @Override
        public void onError(Throwable error) {
            System.out.println("  ❌ Error: " + error.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Publisher con Cancelación (Subscription) ===\n");

        // EJEMPLO 1: Subscriber con límite
        System.out.println("--- Ejemplo 1: Limitar a 5 elementos ---");
        Publisher<Integer> publisher1 = new InfiniteNumberPublisher(0);
        publisher1.subscribe(new LimitedSubscriber(5));

        System.out.println("\n--- Ejemplo 2: Cancelación condicional ---");
        Publisher<Integer> publisher2 = new InfiniteNumberPublisher(0);
        publisher2.subscribe(new ConditionalSubscriber());

        System.out.println("\n📚 CONCEPTOS CLAVE:");
        System.out.println("1. Subscription permite CONTROLAR el flujo de datos");
        System.out.println("2. El Subscriber puede CANCELAR en cualquier momento");
        System.out.println("3. Esto previene emisión innecesaria de datos");
        System.out.println("4. Es fundamental para manejo eficiente de recursos");
        System.out.println("5. RxJava usa Disposable, que es similar a Subscription");
    }
}

