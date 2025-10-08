package com.formadoresit.rxjava.tema8;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * TEMA 8: Flujos y Backpressure
 * Ejemplo 01: Introducción a Backpressure
 * 
 * Backpressure: Mecanismo para controlar el flujo cuando el productor
 * emite más rápido de lo que el consumidor puede procesar
 * 
 * Estrategias:
 * - BUFFER: Almacena todos (puede causar OutOfMemory)
 * - DROP: Descarta los más nuevos
 * - LATEST: Solo mantiene el más reciente
 * - ERROR: Lanza MissingBackpressureException
 * - MISSING: Sin backpressure (responsabilidad del consumidor)
 */
public class Ejemplo01_BackpressureIntro {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 01: Backpressure ===\n");

        // 1. Problema: Productor rápido, consumidor lento
        System.out.println("--- Flowable con control de backpressure ---");
        Flowable.range(1, 20)
            .observeOn(Schedulers.io())
            .subscribe(new SlowSubscriber());
        
        Thread.sleep(300);

        // 2. BackpressureStrategy.BUFFER
        System.out.println("\n--- BUFFER Strategy ---");
        Flowable.range(1, 10)
            .onBackpressureBuffer()
            .observeOn(Schedulers.io())
            .subscribe(n -> {
                System.out.println("Procesando: " + n);
                Thread.sleep(50);  // Consumidor lento
            });

        Thread.sleep(600);

        // 3. BackpressureStrategy.DROP
        System.out.println("\n--- DROP Strategy ---");
        Flowable.interval(1, java.util.concurrent.TimeUnit.MILLISECONDS)
            .onBackpressureDrop(dropped -> System.out.println("  Descartado: " + dropped))
            .observeOn(Schedulers.io())
            .subscribe(n -> {
                System.out.println("Recibido: " + n);
                Thread.sleep(10);  // Consumidor muy lento
            });

        Thread.sleep(100);

        // 4. BackpressureStrategy.LATEST
        System.out.println("\n--- LATEST Strategy ---");
        Flowable.interval(1, java.util.concurrent.TimeUnit.MILLISECONDS)
            .onBackpressureLatest()
            .observeOn(Schedulers.io())
            .subscribe(n -> {
                System.out.println("Recibido: " + n);
                Thread.sleep(10);
            });

        Thread.sleep(100);

        // 5. Control manual con request()
        System.out.println("\n--- Control manual con request() ---");
        Flowable.range(1, 100)
            .subscribe(new Subscriber<Integer>() {
                private Subscription subscription;
                private int count = 0;

                @Override
                public void onSubscribe(Subscription s) {
                    this.subscription = s;
                    subscription.request(5);  // Solicitar solo 5 inicialmente
                }

                @Override
                public void onNext(Integer n) {
                    System.out.println("Procesando: " + n);
                    count++;
                    if (count % 5 == 0 && count < 20) {
                        System.out.println("  Solicitando 5 más...");
                        subscription.request(5);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Error: " + t);
                }

                @Override
                public void onComplete() {
                    System.out.println("Completado");
                }
            });

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• Backpressure: Control de flujo productor-consumidor");
        System.out.println("• BUFFER: Almacena todos (cuidado con memoria)");
        System.out.println("• DROP: Descarta nuevos cuando buffer lleno");
        System.out.println("• LATEST: Solo mantiene el más reciente");
        System.out.println("• ERROR: Falla si no se puede procesar");
        System.out.println("• Flowable soporta backpressure, Observable no");
    }

    static class SlowSubscriber implements Subscriber<Integer> {
        private Subscription subscription;

        @Override
        public void onSubscribe(Subscription s) {
            this.subscription = s;
            subscription.request(10);  // Solo pide 10
        }

        @Override
        public void onNext(Integer integer) {
            try {
                Thread.sleep(10);  // Procesamiento lento
                System.out.println("Procesado: " + integer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable t) {
            System.err.println("Error: " + t.getMessage());
        }

        @Override
        public void onComplete() {
            System.out.println("Completado");
        }
    }
}

