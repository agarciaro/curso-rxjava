package com.formadoresit.rxjava.tema6;

import io.reactivex.subjects.*;

/**
 * TEMA 6: Multicast
 * Ejemplo 02: Subjects
 * 
 * Subject actúa como Observable y Observer simultáneamente
 * Tipos: PublishSubject, BehaviorSubject, ReplaySubject, AsyncSubject
 */
public class Ejemplo02_Subjects {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 02: Subjects ===\n");

        // 1. PublishSubject - Emite solo nuevos elementos
        System.out.println("--- PublishSubject ---");
        PublishSubject<String> publishSubject = PublishSubject.create();

        publishSubject.subscribe(s -> System.out.println("Sub1: " + s));
        
        publishSubject.onNext("A");
        publishSubject.onNext("B");
        
        publishSubject.subscribe(s -> System.out.println("Sub2: " + s));
        
        publishSubject.onNext("C");
        publishSubject.onComplete();

        // 2. BehaviorSubject - Emite el último valor y los nuevos
        System.out.println("\n--- BehaviorSubject ---");
        BehaviorSubject<String> behaviorSubject = BehaviorSubject.createDefault("Inicial");

        behaviorSubject.subscribe(s -> System.out.println("Sub1: " + s));
        
        behaviorSubject.onNext("A");
        behaviorSubject.onNext("B");
        
        behaviorSubject.subscribe(s -> System.out.println("Sub2: " + s));  // Recibe "B"
        
        behaviorSubject.onNext("C");

        // 3. ReplaySubject - Emite todos los valores anteriores
        System.out.println("\n--- ReplaySubject ---");
        ReplaySubject<String> replaySubject = ReplaySubject.create();

        replaySubject.onNext("1");
        replaySubject.onNext("2");
        replaySubject.onNext("3");
        
        replaySubject.subscribe(s -> System.out.println("Sub1: " + s));  // Recibe 1, 2, 3
        
        replaySubject.onNext("4");
        
        replaySubject.subscribe(s -> System.out.println("Sub2: " + s));  // Recibe 1, 2, 3, 4

        // 4. ReplaySubject con límite de tamaño
        System.out.println("\n--- ReplaySubject con límite ---");
        ReplaySubject<Integer> replayLimitado = ReplaySubject.createWithSize(2);  // Solo últimos 2

        replayLimitado.onNext(1);
        replayLimitado.onNext(2);
        replayLimitado.onNext(3);
        replayLimitado.onNext(4);
        
        replayLimitado.subscribe(n -> System.out.println("Sub: " + n));  // Solo recibe 3, 4

        // 5. AsyncSubject - Emite solo el último valor al completar
        System.out.println("\n--- AsyncSubject ---");
        AsyncSubject<String> asyncSubject = AsyncSubject.create();

        asyncSubject.subscribe(s -> System.out.println("Sub1: " + s));
        
        asyncSubject.onNext("A");
        asyncSubject.onNext("B");
        asyncSubject.onNext("C");
        
        asyncSubject.subscribe(s -> System.out.println("Sub2: " + s));
        
        asyncSubject.onNext("D");
        asyncSubject.onComplete();  // Solo ahora emite "D" a todos

        // Ejemplo práctico: Event Bus simple
        System.out.println("\n--- Ejemplo práctico: Event Bus ---");
        PublishSubject<String> eventBus = PublishSubject.create();

        // Suscriptor 1: Logger
        eventBus.subscribe(evento -> System.out.println("[LOG] " + evento));

        // Suscriptor 2: UI
        eventBus
            .filter(e -> e.startsWith("UI:"))
            .subscribe(evento -> System.out.println("[UI] " + evento));

        // Emitir eventos
        eventBus.onNext("Sistema iniciado");
        eventBus.onNext("UI: Mostrar login");
        eventBus.onNext("Usuario autenticado");
        eventBus.onNext("UI: Mostrar dashboard");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• PublishSubject: Solo emite nuevos elementos");
        System.out.println("• BehaviorSubject: Emite último valor + nuevos");
        System.out.println("• ReplaySubject: Emite todos los valores anteriores");
        System.out.println("• AsyncSubject: Emite solo el último al completar");
        System.out.println("• Subjects útiles para event buses y estado compartido");
    }
}

