package com.formadoresit.rxjava.introduccion;

import java.util.ArrayList;
import java.util.List;

/**
 * INTRODUCCIÓN - Ejemplo 01: Patrón Publisher/Subscriber Básico
 * 
 * Implementación manual del patrón Publisher/Subscriber usando solo Java.
 * Este es el patrón fundamental de la programación reactiva.
 * 
 * CONCEPTOS:
 * - Publisher: Productor de datos que emite elementos
 * - Subscriber: Consumidor que recibe y procesa los elementos
 * - Suscripción: Conexión entre Publisher y Subscriber
 * - Push-based: El Publisher empuja datos al Subscriber (vs Pull-based)
 */
public class Ejemplo01_PublisherSubscriberBasico {

    /**
     * Interfaz Subscriber - Define cómo un consumidor recibe datos
     */
    interface Subscriber<T> {
        void onNext(T item);           // Recibe un nuevo elemento
        void onComplete();             // Se notifica que terminó el stream
        void onError(Throwable error); // Se notifica un error
    }

    /**
     * Interfaz Publisher - Define cómo un productor emite datos
     */
    interface Publisher<T> {
        void subscribe(Subscriber<T> subscriber);
    }

    /**
     * Implementación concreta de un Publisher de números
     */
    static class NumberPublisher implements Publisher<Integer> {
        private final int count;

        public NumberPublisher(int count) {
            this.count = count;
        }

        @Override
        public void subscribe(Subscriber<Integer> subscriber) {
            System.out.println("🔗 Subscriber conectado al Publisher");
            
            try {
                // Emitir números del 1 al count
                for (int i = 1; i <= count; i++) {
                    System.out.println("📤 Publisher emitiendo: " + i);
                    subscriber.onNext(i);
                }
                
                // Notificar que terminó
                System.out.println("✅ Publisher completado");
                subscriber.onComplete();
                
            } catch (Exception e) {
                System.out.println("❌ Error en Publisher");
                subscriber.onError(e);
            }
        }
    }

    /**
     * Implementación concreta de un Subscriber que imprime números
     */
    static class PrintSubscriber implements Subscriber<Integer> {
        private final String name;

        public PrintSubscriber(String name) {
            this.name = name;
        }

        @Override
        public void onNext(Integer item) {
            System.out.println("  📥 [" + name + "] recibió: " + item);
        }

        @Override
        public void onComplete() {
            System.out.println("  ✅ [" + name + "] completado");
        }

        @Override
        public void onError(Throwable error) {
            System.out.println("  ❌ [" + name + "] error: " + error.getMessage());
        }
    }

    /**
     * Subscriber que suma los números recibidos
     */
    static class SumSubscriber implements Subscriber<Integer> {
        private int sum = 0;

        @Override
        public void onNext(Integer item) {
            sum += item;
            System.out.println("  📥 Sumando: " + item + " (total: " + sum + ")");
        }

        @Override
        public void onComplete() {
            System.out.println("  ✅ Suma final: " + sum);
        }

        @Override
        public void onError(Throwable error) {
            System.out.println("  ❌ Error: " + error.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Patrón Publisher/Subscriber Básico ===\n");

        // EJEMPLO 1: Un Publisher, un Subscriber
        System.out.println("--- Ejemplo 1: Publisher básico ---");
        Publisher<Integer> publisher1 = new NumberPublisher(5);
        Subscriber<Integer> subscriber1 = new PrintSubscriber("Subscriber-1");
        
        publisher1.subscribe(subscriber1);
        
        System.out.println("\n--- Ejemplo 2: Múltiples Subscribers ---");
        // EJEMPLO 2: Un Publisher, múltiples Subscribers
        Publisher<Integer> publisher2 = new NumberPublisher(3);
        
        publisher2.subscribe(new PrintSubscriber("Subscriber-A"));
        System.out.println();
        publisher2.subscribe(new PrintSubscriber("Subscriber-B"));
        
        System.out.println("\n--- Ejemplo 3: Subscriber que procesa datos ---");
        // EJEMPLO 3: Subscriber que hace operaciones
        Publisher<Integer> publisher3 = new NumberPublisher(10);
        publisher3.subscribe(new SumSubscriber());
        
        System.out.println("\n📚 CONCEPTOS CLAVE:");
        System.out.println("1. El Publisher EMPUJA datos al Subscriber (push-based)");
        System.out.println("2. El Subscriber REACCIONA a los datos cuando llegan");
        System.out.println("3. La comunicación es asíncrona y basada en eventos");
        System.out.println("4. Cada suscripción es independiente");
        System.out.println("5. Este patrón es la BASE de RxJava y programación reactiva");
    }
}

