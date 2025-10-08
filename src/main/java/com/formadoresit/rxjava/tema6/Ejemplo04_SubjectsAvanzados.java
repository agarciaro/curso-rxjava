package com.formadoresit.rxjava.tema6;

import io.reactivex.subjects.*;
import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;

/**
 * TEMA 6: Multicast
 * Ejemplo 04: Subjects Avanzados
 * 
 * Configuraciones avanzadas de Subjects: BehaviorSubject con valores por defecto,
 * ReplaySubject con límites, AsyncSubject con timeout, y combinaciones complejas
 */
public class Ejemplo04_SubjectsAvanzados {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 04: Subjects Avanzados ===\n");

        // 1. BehaviorSubject con valor por defecto
        System.out.println("--- BehaviorSubject con valor por defecto ---");
        BehaviorSubject<String> behaviorWithDefault = BehaviorSubject.createDefault("Estado inicial");
        
        System.out.println("Suscriptor 1 (recibe valor por defecto):");
        behaviorWithDefault.subscribe(state -> System.out.println("  Sub1: " + state));
        
        behaviorWithDefault.onNext("Estado 1");
        behaviorWithDefault.onNext("Estado 2");
        
        System.out.println("Suscriptor 2 (recibe último valor):");
        behaviorWithDefault.subscribe(state -> System.out.println("  Sub2: " + state));

        // 2. BehaviorSubject con valor inicial
        System.out.println("\n--- BehaviorSubject con valor inicial ---");
        BehaviorSubject<Integer> behaviorWithInitial = BehaviorSubject.createDefault(0);
        
        behaviorWithInitial.subscribe(count -> System.out.println("  Contador: " + count));
        
        behaviorWithInitial.onNext(1);
        behaviorWithInitial.onNext(2);
        behaviorWithInitial.onNext(3);

        // 3. ReplaySubject con límite de tiempo
        System.out.println("\n--- ReplaySubject con límite de tiempo ---");
        ReplaySubject<String> replayWithTime = ReplaySubject.createWithTime(500, TimeUnit.MILLISECONDS, io.reactivex.schedulers.Schedulers.computation());
        
        // Emitir algunos valores
        replayWithTime.onNext("Tiempo 1");
        Thread.sleep(200);
        replayWithTime.onNext("Tiempo 2");
        Thread.sleep(200);
        replayWithTime.onNext("Tiempo 3");
        Thread.sleep(200);
        replayWithTime.onNext("Tiempo 4");
        
        System.out.println("Suscriptor (recibe solo últimos 500ms):");
        replayWithTime.subscribe(item -> System.out.println("  Sub: " + item));

        // 4. ReplaySubject con límite de tamaño y tiempo
        System.out.println("\n--- ReplaySubject con límite de tamaño y tiempo ---");
        ReplaySubject<String> replayWithSizeAndTime = ReplaySubject.createWithSize(2);
        
        replayWithSizeAndTime.onNext("A");
        Thread.sleep(100);
        replayWithSizeAndTime.onNext("B");
        Thread.sleep(100);
        replayWithSizeAndTime.onNext("C");
        Thread.sleep(100);
        replayWithSizeAndTime.onNext("D");
        
        System.out.println("Suscriptor (máximo 2 elementos de últimos 300ms):");
        replayWithSizeAndTime.subscribe(item -> System.out.println("  Sub: " + item));

        // 5. AsyncSubject con timeout
        System.out.println("\n--- AsyncSubject con timeout ---");
        AsyncSubject<String> asyncWithTimeout = AsyncSubject.create();
        
        asyncWithTimeout.subscribe(
            result -> System.out.println("  Resultado: " + result),
            error -> System.out.println("  Error: " + error.getMessage())
        );
        
        // Simular operación que puede fallar
        new Thread(() -> {
            try {
                Thread.sleep(200);
                asyncWithTimeout.onNext("Operación exitosa");
                asyncWithTimeout.onComplete();
            } catch (InterruptedException e) {
                asyncWithTimeout.onError(e);
            }
        }).start();

        // 6. PublishSubject con filtrado
        System.out.println("\n--- PublishSubject con filtrado ---");
        PublishSubject<Evento> eventSubject = PublishSubject.create();
        
        // Suscriptor 1: Solo eventos de error
        eventSubject
            .filter(evento -> evento.tipo.equals("ERROR"))
            .subscribe(evento -> System.out.println("  [ERROR] " + evento.mensaje));
        
        // Suscriptor 2: Solo eventos de info
        eventSubject
            .filter(evento -> evento.tipo.equals("INFO"))
            .subscribe(evento -> System.out.println("  [INFO] " + evento.mensaje));
        
        // Emitir eventos
        eventSubject.onNext(new Evento("INFO", "Sistema iniciado"));
        eventSubject.onNext(new Evento("ERROR", "Error de conexión"));
        eventSubject.onNext(new Evento("INFO", "Usuario conectado"));
        eventSubject.onNext(new Evento("ERROR", "Error de validación"));

        // 7. BehaviorSubject para estado de aplicación
        System.out.println("\n--- BehaviorSubject para estado de aplicación ---");
        BehaviorSubject<EstadoAplicacion> appState = BehaviorSubject.createDefault(new EstadoAplicacion("INICIALIZANDO", 0));
        
        // Suscriptor 1: UI
        appState.subscribe(estado -> System.out.println("  [UI] Estado: " + estado.estado + ", Usuarios: " + estado.usuariosConectados));
        
        // Suscriptor 2: Logger
        appState.subscribe(estado -> System.out.println("  [LOG] Cambio de estado: " + estado.estado));
        
        // Cambiar estados
        appState.onNext(new EstadoAplicacion("CONECTANDO", 0));
        appState.onNext(new EstadoAplicacion("CONECTADO", 5));
        appState.onNext(new EstadoAplicacion("CONECTADO", 12));
        appState.onNext(new EstadoAplicacion("DESCONECTANDO", 8));

        // 8. ReplaySubject para historial de comandos
        System.out.println("\n--- ReplaySubject para historial de comandos ---");
        ReplaySubject<String> commandHistory = ReplaySubject.createWithSize(5);
        
        // Agregar comandos
        commandHistory.onNext("ls -la");
        commandHistory.onNext("cd /home");
        commandHistory.onNext("mkdir proyecto");
        commandHistory.onNext("cd proyecto");
        commandHistory.onNext("git init");
        commandHistory.onNext("npm init");
        commandHistory.onNext("code .");
        
        System.out.println("Historial de comandos (últimos 5):");
        commandHistory.subscribe(comando -> System.out.println("  " + comando));

        // 9. AsyncSubject para operaciones asíncronas
        System.out.println("\n--- AsyncSubject para operaciones asíncronas ---");
        AsyncSubject<ResultadoOperacion> operacionAsync = AsyncSubject.create();
        
        // Suscriptor
        operacionAsync.subscribe(
            resultado -> System.out.println("  Operación completada: " + resultado.mensaje),
            error -> System.out.println("  Error en operación: " + error.getMessage())
        );
        
        // Simular operación
        simularOperacionAsincrona(operacionAsync);

        // 10. Combinación de Subjects
        System.out.println("\n--- Combinación de Subjects ---");
        PublishSubject<String> inputSubject = PublishSubject.create();
        BehaviorSubject<String> stateSubject = BehaviorSubject.createDefault("IDLE");
        
        // Procesar input y cambiar estado
        inputSubject
            .doOnNext(input -> System.out.println("  Procesando: " + input))
            .map(input -> "PROCESANDO")
            .subscribe(stateSubject);
        
        // Suscriptor de estado
        stateSubject.subscribe(estado -> System.out.println("  Estado actual: " + estado));
        
        // Emitir inputs
        inputSubject.onNext("Comando 1");
        Thread.sleep(100);
        inputSubject.onNext("Comando 2");
        Thread.sleep(100);
        inputSubject.onNext("Comando 3");

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• BehaviorSubject: Ideal para estado de aplicación");
        System.out.println("• ReplaySubject: Perfecto para historial y cache");
        System.out.println("• AsyncSubject: Útil para operaciones asíncronas");
        System.out.println("• PublishSubject: Ideal para event buses");
        System.out.println("• Límites de tiempo y tamaño optimizan memoria");
        System.out.println("• Combinaciones de Subjects crean patrones complejos");
    }

    private static void simularOperacionAsincrona(AsyncSubject<ResultadoOperacion> subject) {
        new Thread(() -> {
            try {
                Thread.sleep(300);
                subject.onNext(new ResultadoOperacion("Operación exitosa", true));
                subject.onComplete();
            } catch (InterruptedException e) {
                subject.onError(e);
            }
        }).start();
    }

    static class Evento {
        String tipo;
        String mensaje;
        
        Evento(String tipo, String mensaje) {
            this.tipo = tipo;
            this.mensaje = mensaje;
        }
    }

    static class EstadoAplicacion {
        String estado;
        int usuariosConectados;
        
        EstadoAplicacion(String estado, int usuariosConectados) {
            this.estado = estado;
            this.usuariosConectados = usuariosConectados;
        }
    }

    static class ResultadoOperacion {
        String mensaje;
        boolean exitoso;
        
        ResultadoOperacion(String mensaje, boolean exitoso) {
            this.mensaje = mensaje;
            this.exitoso = exitoso;
        }
    }
}
