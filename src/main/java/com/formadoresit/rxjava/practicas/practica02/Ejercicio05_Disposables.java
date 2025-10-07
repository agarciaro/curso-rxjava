package com.formadoresit.rxjava.practicas.practica02;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.TimeUnit;

/**
 * PRÁCTICA 02 - Ejercicio 2.5: Disposables
 * 
 * Gestionar suscripciones con Disposables y CompositeDisposable
 */
public class Ejercicio05_Disposables {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 2.5: Disposables ===\n");

        // 1. Disposable simple - Cancelar después de 5 elementos
        System.out.println("--- Disposable simple ---");
        Observable<Long> intervalObservable = Observable
            .interval(100, TimeUnit.MILLISECONDS)
            .doOnNext(n -> System.out.println("  Emitido: " + n))
            .doOnDispose(() -> System.out.println("  [Disposed] Suscripción cancelada"));

        Disposable disposable = intervalObservable.subscribe(
            numero -> {
                System.out.println("Recibido: " + numero);
                // Después de recibir 5 elementos, cancelar
                if (numero >= 4) {
                    System.out.println("\n→ Recibimos 5 elementos, cancelando...");
                }
            }
        );

        // Esperar a recibir 5 elementos
        Thread.sleep(500);
        
        // Cancelar suscripción
        disposable.dispose();
        System.out.println("isDisposed: " + disposable.isDisposed());
        
        // Esperar un poco más para confirmar que no llegan más elementos
        System.out.println("Esperando para confirmar que no hay más emisiones...");
        Thread.sleep(500);
        System.out.println("✓ Confirmado: No se recibieron más elementos después de dispose()\n");

        // 2. CompositeDisposable - Gestionar múltiples suscripciones
        System.out.println("\n--- CompositeDisposable ---");
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        // Suscripción 1: Números
        Disposable sub1 = Observable.interval(150, TimeUnit.MILLISECONDS)
            .take(10)
            .subscribe(n -> System.out.println("  Stream 1 (números): " + n));

        // Suscripción 2: Letras
        Disposable sub2 = Observable.interval(200, TimeUnit.MILLISECONDS)
            .take(10)
            .map(n -> (char) ('A' + n))
            .subscribe(letra -> System.out.println("  Stream 2 (letras): " + letra));

        // Suscripción 3: Mensaje periódico
        Disposable sub3 = Observable.interval(300, TimeUnit.MILLISECONDS)
            .take(10)
            .subscribe(n -> System.out.println("  Stream 3 (ping): ping #" + n));

        // Agregar todas al composite
        compositeDisposable.addAll(sub1, sub2, sub3);

        System.out.println("Tres streams ejecutándose en paralelo...\n");
        Thread.sleep(1000);

        // Cancelar todas las suscripciones a la vez
        System.out.println("\n→ Cancelando todas las suscripciones...");
        compositeDisposable.dispose();
        
        System.out.println("CompositeDisposable.isDisposed: " + compositeDisposable.isDisposed());
        System.out.println("sub1.isDisposed: " + sub1.isDisposed());
        System.out.println("sub2.isDisposed: " + sub2.isDisposed());
        System.out.println("sub3.isDisposed: " + sub3.isDisposed());

        Thread.sleep(500);
        System.out.println("\n✓ Todas las suscripciones canceladas correctamente");

        // 3. Ejemplo práctico: Sistema de monitoreo
        System.out.println("\n\n--- Ejemplo práctico: Sistema de monitoreo ---");
        SistemaMonitoreo sistema = new SistemaMonitoreo();
        
        System.out.println("Iniciando monitoreo...\n");
        sistema.iniciar();
        
        Thread.sleep(2000);
        
        System.out.println("\nDetiendo monitoreo...");
        sistema.detener();
        
        Thread.sleep(500);
        System.out.println("Sistema detenido");
    }
}

/**
 * Ejemplo de sistema que gestiona múltiples suscripciones
 */
class SistemaMonitoreo {
    private CompositeDisposable disposables = new CompositeDisposable();

    public void iniciar() {
        // Monitor de CPU
        Disposable monitorCPU = Observable.interval(500, TimeUnit.MILLISECONDS)
            .subscribe(n -> System.out.println("  [CPU] Uso: " + (int)(Math.random() * 100) + "%"));

        // Monitor de memoria
        Disposable monitorMemoria = Observable.interval(700, TimeUnit.MILLISECONDS)
            .subscribe(n -> System.out.println("  [MEM] Uso: " + (int)(Math.random() * 100) + "%"));

        // Monitor de red
        Disposable monitorRed = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .subscribe(n -> System.out.println("  [NET] Tráfico: " + (int)(Math.random() * 1000) + " KB/s"));

        disposables.addAll(monitorCPU, monitorMemoria, monitorRed);
    }

    public void detener() {
        disposables.dispose();
        disposables = new CompositeDisposable(); // Resetear para poder reiniciar
    }
}

