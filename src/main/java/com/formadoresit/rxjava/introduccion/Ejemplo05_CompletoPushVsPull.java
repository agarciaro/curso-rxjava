package com.formadoresit.rxjava.introduccion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * INTRODUCCIÓN - Ejemplo 05: Push vs Pull - Comparación Completa
 * 
 * Compara el modelo tradicional PULL (iteradores) con el modelo PUSH (reactivo).
 * Esta es la diferencia fundamental entre programación imperativa y reactiva.
 * 
 * CONCEPTOS:
 * - PULL: El consumidor SOLICITA datos cuando los necesita (Iterator)
 * - PUSH: El productor ENVÍA datos cuando están disponibles (Publisher)
 * - Programación Imperativa vs Reactiva
 * - Blocking vs Non-blocking
 */
public class Ejemplo05_CompletoPushVsPull {

    // ==================== MODELO PULL (Tradicional) ====================

    /**
     * Fuente de datos tradicional (modelo PULL)
     */
    static class DataSourcePull {
        private List<Integer> data = new ArrayList<>();

        public DataSourcePull() {
            // Simular obtención de datos
            for (int i = 1; i <= 5; i++) {
                data.add(i);
            }
        }

        /**
         * El consumidor PIDE datos usando un Iterator
         */
        public Iterator<Integer> getIterator() {
            return data.iterator();
        }
    }

    /**
     * Consumidor tradicional (modelo PULL)
     */
    static class DataConsumerPull {
        public void process(DataSourcePull source) {
            System.out.println("🔵 [PULL] El CONSUMIDOR controla el flujo");
            System.out.println("    El consumidor PIDE datos cuando está listo\n");
            
            Iterator<Integer> iterator = source.getIterator();
            
            while (iterator.hasNext()) {
                // El CONSUMIDOR pide el siguiente dato
                Integer data = iterator.next();
                System.out.println("    👈 [PULL] Consumidor PIDIÓ y recibió: " + data);
                
                // El consumidor decide cuándo procesar
                processData(data);
            }
            
            System.out.println("    ✅ [PULL] Procesamiento completado\n");
        }

        private void processData(Integer data) {
            // Simular procesamiento
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== MODELO PUSH (Reactivo) ====================

    interface Subscriber<T> {
        void onNext(T item);
        void onComplete();
    }

    interface Publisher<T> {
        void subscribe(Subscriber<T> subscriber);
    }

    /**
     * Fuente de datos reactiva (modelo PUSH)
     */
    static class DataSourcePush implements Publisher<Integer> {
        private List<Integer> data = new ArrayList<>();

        public DataSourcePush() {
            // Simular obtención de datos
            for (int i = 1; i <= 5; i++) {
                data.add(i);
            }
        }

        @Override
        public void subscribe(Subscriber<Integer> subscriber) {
            System.out.println("🟢 [PUSH] El PRODUCTOR controla el flujo");
            System.out.println("    El productor ENVÍA datos cuando están listos\n");
            
            // El PRODUCTOR envía datos al consumidor
            for (Integer item : data) {
                System.out.println("    👉 [PUSH] Productor ENVIÓ: " + item);
                subscriber.onNext(item);
            }
            
            subscriber.onComplete();
        }
    }

    /**
     * Consumidor reactivo (modelo PUSH)
     */
    static class DataConsumerPush implements Subscriber<Integer> {
        @Override
        public void onNext(Integer item) {
            System.out.println("       📥 [PUSH] Consumidor REACCIONÓ a: " + item);
            processData(item);
        }

        @Override
        public void onComplete() {
            System.out.println("       ✅ [PUSH] Procesamiento completado\n");
        }

        private void processData(Integer data) {
            // Simular procesamiento
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== COMPARACIÓN ====================

    public static void main(String[] args) {
        System.out.println("=== PUSH vs PULL: La Diferencia Fundamental ===\n");

        // MODELO PULL (Tradicional - Imperativo)
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║     MODELO PULL (Tradicional)         ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        DataSourcePull pullSource = new DataSourcePull();
        DataConsumerPull pullConsumer = new DataConsumerPull();
        pullConsumer.process(pullSource);

        // MODELO PUSH (Reactivo)
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║      MODELO PUSH (Reactivo)           ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        Publisher<Integer> pushSource = new DataSourcePush();
        Subscriber<Integer> pushConsumer = new DataConsumerPush();
        pushSource.subscribe(pushConsumer);

        // TABLA COMPARATIVA
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║               PULL vs PUSH - DIFERENCIAS CLAVE                     ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                    ║");
        System.out.println("║  MODELO PULL (Iterator - Tradicional)                             ║");
        System.out.println("║  ----------------------------------------                          ║");
        System.out.println("║  ❌ El CONSUMIDOR controla el flujo                               ║");
        System.out.println("║  ❌ El consumidor PIDE datos (hasNext(), next())                  ║");
        System.out.println("║  ❌ Síncrono y bloqueante                                          ║");
        System.out.println("║  ❌ El consumidor debe esperar por los datos                       ║");
        System.out.println("║  ❌ Difícil de hacer asíncrono                                     ║");
        System.out.println("║  ❌ No hay concepto de 'completado' o 'error'                      ║");
        System.out.println("║                                                                    ║");
        System.out.println("║  MODELO PUSH (Publisher/Subscriber - Reactivo)                    ║");
        System.out.println("║  ----------------------------------------------                    ║");
        System.out.println("║  ✅ El PRODUCTOR controla el flujo                                ║");
        System.out.println("║  ✅ El productor ENVÍA datos (onNext())                           ║");
        System.out.println("║  ✅ Asíncrono y no bloqueante                                      ║");
        System.out.println("║  ✅ El consumidor REACCIONA cuando llegan datos                    ║");
        System.out.println("║  ✅ Diseñado para operaciones asíncronas                           ║");
        System.out.println("║  ✅ Manejo explícito de completado y errores                       ║");
        System.out.println("║                                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");

        System.out.println("\n📚 CONCEPTOS CLAVE:");
        System.out.println("1. PULL = Iterator, Stream tradicional, for loops");
        System.out.println("2. PUSH = Observable, Publisher, programación reactiva");
        System.out.println("3. PULL es IMPERATIVO: 'Dame el siguiente dato'");
        System.out.println("4. PUSH es REACTIVO: 'Te aviso cuando haya datos'");
        System.out.println("5. RxJava implementa el modelo PUSH");
        System.out.println("6. El modelo PUSH es ideal para:");
        System.out.println("   - Eventos de UI");
        System.out.println("   - Streams de datos en tiempo real");
        System.out.println("   - Operaciones asíncronas");
        System.out.println("   - Composición de operaciones");
    }
}

