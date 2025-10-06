package com.formadoresit.rxjava.tema1;

import java.util.ArrayList;
import java.util.List;

/**
 * TEMA 1: Fundamentos sobre Programación Funcional y Reactiva
 * Ejemplo 05: Introducción a la Programación Reactiva
 * 
 * Programación Reactiva: Paradigma basado en flujos de datos y propagación de cambios
 * - Asíncrona y basada en eventos
 * - Los datos fluyen a través del sistema
 * - Los componentes reaccionan a los cambios
 */
public class Ejemplo05_IntroduccionReactiva {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejemplo 05: Introducción a la Programación Reactiva ===\n");

        // Ejemplo 1: Enfoque tradicional (pull - solicitar datos)
        System.out.println("--- Enfoque TRADICIONAL (Pull) ---");
        System.out.println("El consumidor solicita datos cuando los necesita\n");
        
        List<String> datos = List.of("Dato1", "Dato2", "Dato3");
        for (String dato : datos) {
            System.out.println("Procesando: " + dato);
        }

        // Ejemplo 2: Enfoque reactivo (push - los datos se empujan)
        System.out.println("\n--- Enfoque REACTIVO (Push) ---");
        System.out.println("Los datos se empujan al consumidor cuando están disponibles\n");
        
        ProductorDatos productor = new ProductorDatos();
        ConsumidorDatos consumidor = new ConsumidorDatos();
        
        productor.suscribir(consumidor);
        productor.producirDatos();

        // Ejemplo 3: Concepto de flujo reactivo
        System.out.println("\n--- Concepto de FLUJO REACTIVO ---");
        System.out.println("Publisher (productor) -> Stream de datos -> Subscriber (consumidor)\n");
        
        // Simulación simple de flujo reactivo
        FuenteDatos fuente = new FuenteDatos();
        
        System.out.println("Suscriptor 1:");
        fuente.suscribir(dato -> System.out.println("  Recibido: " + dato));
        
        System.out.println("\nSuscriptor 2 (con transformación):");
        fuente.suscribir(dato -> System.out.println("  Recibido en MAYÚSCULAS: " + dato.toUpperCase()));
        
        System.out.println("\nEmitiendo datos...");
        fuente.emitir("Hola");
        fuente.emitir("Mundo");
        fuente.emitir("Reactivo");

        // Conceptos clave
        System.out.println("\n\n=== CONCEPTOS CLAVE DE PROGRAMACIÓN REACTIVA ===");
        System.out.println("1. ASÍNCRONA: Las operaciones no bloquean el hilo de ejecución");
        System.out.println("2. BASADA EN EVENTOS: Los componentes reaccionan a eventos");
        System.out.println("3. STREAMS: Secuencia de datos que fluyen en el tiempo");
        System.out.println("4. BACKPRESSURE: Control de flujo cuando el productor es más rápido que el consumidor");
        System.out.println("5. NO BLOQUEANTE: Mejor utilización de recursos");
    }

    // Clase productora tradicional con patrón observer
    static class ProductorDatos {
        private List<ConsumidorDatos> suscriptores = new ArrayList<>();

        public void suscribir(ConsumidorDatos consumidor) {
            suscriptores.add(consumidor);
        }

        public void producirDatos() {
            List<String> datos = List.of("Evento1", "Evento2", "Evento3");
            for (String dato : datos) {
                notificar(dato);
            }
        }

        private void notificar(String dato) {
            for (ConsumidorDatos consumidor : suscriptores) {
                consumidor.recibir(dato);
            }
        }
    }

    static class ConsumidorDatos {
        public void recibir(String dato) {
            System.out.println("Consumidor recibe: " + dato);
        }
    }

    // Simulación simple de fuente reactiva
    static class FuenteDatos {
        private List<java.util.function.Consumer<String>> suscriptores = new ArrayList<>();

        public void suscribir(java.util.function.Consumer<String> suscriptor) {
            suscriptores.add(suscriptor);
        }

        public void emitir(String dato) {
            for (java.util.function.Consumer<String> suscriptor : suscriptores) {
                suscriptor.accept(dato);
            }
        }
    }
}

