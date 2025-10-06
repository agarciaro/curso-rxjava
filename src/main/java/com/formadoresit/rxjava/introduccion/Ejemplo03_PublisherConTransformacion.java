package com.formadoresit.rxjava.introduccion;

import java.util.function.Function;

/**
 * INTRODUCCIÓN - Ejemplo 03: Publisher con Transformaciones (Operadores)
 * 
 * Implementa el concepto de operadores que transforman el flujo de datos.
 * Esto introduce la idea de "pipeline" reactivo.
 * 
 * CONCEPTOS:
 * - Operadores: Funciones que transforman el flujo de datos
 * - Composición: Encadenar múltiples operadores
 * - Pipeline: Flujo de transformaciones secuenciales
 * - Inmutabilidad: Cada operador crea un nuevo Publisher
 */
public class Ejemplo03_PublisherConTransformacion {

    interface Subscriber<T> {
        void onNext(T item);
        void onComplete();
        void onError(Throwable error);
    }

    interface Publisher<T> {
        void subscribe(Subscriber<T> subscriber);
        
        // Operador MAP: Transforma cada elemento
        <R> Publisher<R> map(Function<T, R> mapper);
        
        // Operador FILTER: Filtra elementos según una condición
        Publisher<T> filter(Function<T, Boolean> predicate);
    }

    /**
     * Implementación base de Publisher con operadores
     */
    static abstract class BasePublisher<T> implements Publisher<T> {
        
        @Override
        public <R> Publisher<R> map(Function<T, R> mapper) {
            BasePublisher<T> source = this;
            
            return new BasePublisher<R>() {
                @Override
                public void subscribe(Subscriber<R> subscriber) {
                    // Suscribirse al publisher fuente con un subscriber intermedio
                    source.subscribe(new Subscriber<T>() {
                        @Override
                        public void onNext(T item) {
                            try {
                                // Aplicar la transformación
                                R transformed = mapper.apply(item);
                                System.out.println("  🔄 Transformado: " + item + " → " + transformed);
                                subscriber.onNext(transformed);
                            } catch (Exception e) {
                                subscriber.onError(e);
                            }
                        }

                        @Override
                        public void onComplete() {
                            subscriber.onComplete();
                        }

                        @Override
                        public void onError(Throwable error) {
                            subscriber.onError(error);
                        }
                    });
                }
            };
        }

        @Override
        public Publisher<T> filter(Function<T, Boolean> predicate) {
            BasePublisher<T> source = this;
            
            return new BasePublisher<T>() {
                @Override
                public void subscribe(Subscriber<T> subscriber) {
                    source.subscribe(new Subscriber<T>() {
                        @Override
                        public void onNext(T item) {
                            try {
                                // Aplicar el filtro
                                if (predicate.apply(item)) {
                                    System.out.println("  ✅ Pasó el filtro: " + item);
                                    subscriber.onNext(item);
                                } else {
                                    System.out.println("  ❌ Filtrado: " + item);
                                }
                            } catch (Exception e) {
                                subscriber.onError(e);
                            }
                        }

                        @Override
                        public void onComplete() {
                            subscriber.onComplete();
                        }

                        @Override
                        public void onError(Throwable error) {
                            subscriber.onError(error);
                        }
                    });
                }
            };
        }
    }

    /**
     * Publisher que emite números
     */
    static class NumberPublisher extends BasePublisher<Integer> {
        private final int start;
        private final int end;

        public NumberPublisher(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void subscribe(Subscriber<Integer> subscriber) {
            System.out.println("📤 Emitiendo números de " + start + " a " + end);
            
            try {
                for (int i = start; i <= end; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onComplete();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }
    }

    /**
     * Subscriber simple para imprimir resultados
     */
    static class PrintSubscriber<T> implements Subscriber<T> {
        @Override
        public void onNext(T item) {
            System.out.println("    📥 Resultado final: " + item);
        }

        @Override
        public void onComplete() {
            System.out.println("    ✅ Completado\n");
        }

        @Override
        public void onError(Throwable error) {
            System.out.println("    ❌ Error: " + error.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Publisher con Transformaciones (Operadores) ===\n");

        // EJEMPLO 1: Operador MAP
        System.out.println("--- Ejemplo 1: MAP (multiplicar por 2) ---");
        Publisher<Integer> publisher1 = new NumberPublisher(1, 5);
        Publisher<Integer> doubled = publisher1.map(x -> x * 2);
        doubled.subscribe(new PrintSubscriber<>());

        // EJEMPLO 2: Operador FILTER
        System.out.println("--- Ejemplo 2: FILTER (solo pares) ---");
        Publisher<Integer> publisher2 = new NumberPublisher(1, 10);
        Publisher<Integer> evenOnly = publisher2.filter(x -> x % 2 == 0);
        evenOnly.subscribe(new PrintSubscriber<>());

        // EJEMPLO 3: Composición de operadores
        System.out.println("--- Ejemplo 3: MAP + FILTER (pipeline) ---");
        Publisher<Integer> publisher3 = new NumberPublisher(1, 10);
        Publisher<Integer> pipeline = publisher3
            .filter(x -> x % 2 == 0)     // Solo pares
            .map(x -> x * x);             // Elevar al cuadrado
        
        pipeline.subscribe(new PrintSubscriber<>());

        // EJEMPLO 4: Transformación de tipo
        System.out.println("--- Ejemplo 4: MAP con cambio de tipo ---");
        Publisher<Integer> publisher4 = new NumberPublisher(1, 5);
        Publisher<String> strings = publisher4.map(x -> "Número: " + x);
        strings.subscribe(new PrintSubscriber<>());

        // EJEMPLO 5: Pipeline complejo
        System.out.println("--- Ejemplo 5: Pipeline complejo ---");
        Publisher<Integer> publisher5 = new NumberPublisher(1, 20);
        Publisher<String> complex = publisher5
            .filter(x -> x % 3 == 0)              // Divisibles entre 3
            .map(x -> x * 2)                       // Multiplicar por 2
            .filter(x -> x > 10)                   // Mayores que 10
            .map(x -> "Resultado: " + x);          // Convertir a String
        
        complex.subscribe(new PrintSubscriber<>());

        System.out.println("📚 CONCEPTOS CLAVE:");
        System.out.println("1. Los OPERADORES transforman el flujo de datos");
        System.out.println("2. Cada operador crea un NUEVO Publisher (inmutabilidad)");
        System.out.println("3. Los operadores se pueden ENCADENAR (composición)");
        System.out.println("4. Forman un PIPELINE de transformaciones");
        System.out.println("5. RxJava tiene más de 300 operadores predefinidos");
        System.out.println("6. Esta es la base de la PROGRAMACIÓN REACTIVA FUNCIONAL");
    }
}

