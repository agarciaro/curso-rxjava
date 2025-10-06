package com.formadoresit.rxjava.introduccion;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * INTRODUCCIÓN - Ejemplo 06: Imperativa vs Reactiva - Comparación de Performance
 * 
 * Compara el rendimiento y la diferencia conceptual entre:
 * - Programación IMPERATIVA (tradicional, bloqueante)
 * - Programación REACTIVA (asíncrona, no bloqueante)
 * 
 * CASO DE USO: Procesamiento de transacciones bancarias
 * - Filtrar transacciones mayores a un monto
 * - Aplicar comisión
 * - Calcular total
 * 
 * MEDICIONES:
 * - Tiempo de ejecución
 * - Uso de threads
 * - Throughput (transacciones por segundo)
 */
public class Ejemplo06_ImperativaVsReactivaPerformance {

    // Modelo de datos
    static class Transaction {
        final int id;
        final double amount;
        final String type;

        Transaction(int id, double amount, String type) {
            this.id = id;
            this.amount = amount;
            this.type = type;
        }

        @Override
        public String toString() {
            return String.format("Tx#%d: $%.2f (%s)", id, amount, type);
        }
    }

    // ==================== SOLUCIÓN IMPERATIVA ====================

    static class ImperativeSolution {
        
        /**
         * Procesa transacciones de forma IMPERATIVA (bloqueante)
         */
        public static ProcessResult process(List<Transaction> transactions, double minAmount) {
            long startTime = System.nanoTime();
            
            List<Transaction> filtered = new ArrayList<>();
            
            // 1. FILTRAR transacciones mayores al mínimo
            for (Transaction tx : transactions) {
                if (tx.amount > minAmount) {
                    filtered.add(tx);
                }
            }
            
            // 2. APLICAR comisión (2%)
            List<Double> withCommission = new ArrayList<>();
            for (Transaction tx : filtered) {
                double amountWithCommission = tx.amount * 1.02;
                withCommission.add(amountWithCommission);
            }
            
            // 3. CALCULAR total
            double total = 0;
            for (Double amount : withCommission) {
                total += amount;
            }
            
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // ms
            
            return new ProcessResult(
                filtered.size(),
                total,
                duration,
                Thread.currentThread().getName()
            );
        }

        /**
         * Versión con Streams (más funcional pero sigue siendo síncrona)
         */
        public static ProcessResult processWithStreams(List<Transaction> transactions, double minAmount) {
            long startTime = System.nanoTime();
            
            double total = transactions.stream()
                .filter(tx -> tx.amount > minAmount)
                .mapToDouble(tx -> tx.amount * 1.02)
                .sum();
            
            long count = transactions.stream()
                .filter(tx -> tx.amount > minAmount)
                .count();
            
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // ms
            
            return new ProcessResult(
                (int) count,
                total,
                duration,
                Thread.currentThread().getName()
            );
        }
    }

    // ==================== SOLUCIÓN REACTIVA (RxJava) ====================

    static class ReactiveSolution {
        
        /**
         * Procesa transacciones de forma REACTIVA con RxJava (no bloqueante)
         * 
         * Usa Observable de RxJava con:
         * - fromIterable() para crear el Observable
         * - subscribeOn() para procesamiento asíncrono
         * - filter() para filtrar transacciones
         * - map() para aplicar comisión
         * - reduce() para sumar total
         */
        public static ProcessResult process(List<Transaction> transactions, double minAmount) 
                throws InterruptedException {
            
            long startTime = System.nanoTime();
            
            CountDownLatch latch = new CountDownLatch(1);
            AtomicInteger count = new AtomicInteger(0);
            AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
            AtomicReference<String> threadName = new AtomicReference<>("");
            
            // ⚡ PROGRAMACIÓN REACTIVA CON RxJava
            Observable.fromIterable(transactions)
                // Ejecutar en thread pool de IO
                .subscribeOn(Schedulers.io())
                // Filtrar transacciones mayores al mínimo
                .filter(tx -> tx.amount > minAmount)
                // Aplicar comisión del 2%
                .map(tx -> {
                    count.incrementAndGet();
                    return tx.amount * 1.02;
                })
                // Sumar todas las cantidades
                .reduce(0.0, (acc, amount) -> acc + amount)
                // Suscribirse y obtener resultado
                .subscribe(
                    total -> {
                        totalAmount.set(total);
                        threadName.set(Thread.currentThread().getName());
                        latch.countDown();
                    },
                    error -> {
                        error.printStackTrace();
                        latch.countDown();
                    }
                );
            
            // Esperar a que termine (en producción usarías bloqueo o callbacks)
            latch.await(5, TimeUnit.SECONDS);
            
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // ms
            
            return new ProcessResult(
                count.get(),
                totalAmount.get(),
                duration,
                "RxJava-" + threadName.get()
            );
        }
        
        /**
         * Versión con procesamiento paralelo (más operaciones concurrentes)
         */
        public static ProcessResult processParallel(List<Transaction> transactions, double minAmount) 
                throws InterruptedException {
            
            long startTime = System.nanoTime();
            
            CountDownLatch latch = new CountDownLatch(1);
            AtomicInteger count = new AtomicInteger(0);
            AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
            
            // ⚡ PROCESAMIENTO PARALELO con RxJava
            Observable.fromIterable(transactions)
                // flatMap para procesamiento paralelo
                .flatMap(tx -> 
                    Observable.just(tx)
                        .subscribeOn(Schedulers.computation())
                        .filter(t -> t.amount > minAmount)
                        .map(t -> {
                            count.incrementAndGet();
                            return t.amount * 1.02;
                        })
                )
                .reduce(0.0, (acc, amount) -> acc + amount)
                .subscribe(
                    total -> {
                        totalAmount.set(total);
                        latch.countDown();
                    },
                    error -> {
                        error.printStackTrace();
                        latch.countDown();
                    }
                );
            
            latch.await(5, TimeUnit.SECONDS);
            
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // ms
            
            return new ProcessResult(
                count.get(),
                totalAmount.get(),
                duration,
                "RxJava-Parallel"
            );
        }
    }

    // ==================== RESULTADO ====================

    static class ProcessResult {
        final int transactionsProcessed;
        final double totalAmount;
        final long durationMs;
        final String threadName;

        ProcessResult(int transactionsProcessed, double totalAmount, long durationMs, String threadName) {
            this.transactionsProcessed = transactionsProcessed;
            this.totalAmount = totalAmount;
            this.durationMs = durationMs;
            this.threadName = threadName;
        }

        @Override
        public String toString() {
            return String.format(
                "Transacciones: %d | Total: $%.2f | Tiempo: %dms | Thread: %s",
                transactionsProcessed, totalAmount, durationMs, threadName
            );
        }
    }

    // ==================== GENERADOR DE DATOS ====================

    static List<Transaction> generateTransactions(int count) {
        List<Transaction> transactions = new ArrayList<>();
        Random random = new Random(42); // Seed fija para resultados reproducibles
        
        String[] types = {"DEPOSIT", "WITHDRAWAL", "TRANSFER", "PAYMENT"};
        
        for (int i = 1; i <= count; i++) {
            double amount = 10 + random.nextDouble() * 990; // Entre $10 y $1000
            String type = types[random.nextInt(types.length)];
            transactions.add(new Transaction(i, amount, type));
        }
        
        return transactions;
    }

    // ==================== BENCHMARK ====================

    static void runBenchmark(int iterations, int transactionsPerIteration) throws InterruptedException {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           BENCHMARK: IMPERATIVA VS REACTIVA                ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  Iteraciones: " + String.format("%-45d", iterations) + "║");
        System.out.println("║  Transacciones por iteración: " + String.format("%-29d", transactionsPerIteration) + "║");
        System.out.println("║  Total transacciones: " + String.format("%-37d", iterations * transactionsPerIteration) + "║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        double minAmount = 500.0;

        // WARM-UP (precalentar JVM)
        System.out.println("⏳ Precalentando JVM...");
        List<Transaction> warmupData = generateTransactions(1000);
        for (int i = 0; i < 10; i++) {
            ImperativeSolution.process(warmupData, minAmount);
            ImperativeSolution.processWithStreams(warmupData, minAmount);
        }
        System.out.println("✅ JVM precalentada\n");

        // ==================== TEST IMPERATIVA ====================
        System.out.println("🔵 ========== SOLUCIÓN IMPERATIVA (Tradicional) ==========");
        
        long imperativeStartTime = System.currentTimeMillis();
        long imperativeTotalTime = 0;
        int imperativeTotalTx = 0;
        
        for (int i = 0; i < iterations; i++) {
            List<Transaction> data = generateTransactions(transactionsPerIteration);
            ProcessResult result = ImperativeSolution.process(data, minAmount);
            imperativeTotalTime += result.durationMs;
            imperativeTotalTx += result.transactionsProcessed;
            
            if (i == 0) {
                System.out.println("  Primera iteración: " + result);
            }
        }
        
        long imperativeEndTime = System.currentTimeMillis();
        long imperativeRealTime = imperativeEndTime - imperativeStartTime;
        double imperativeAvgTime = imperativeTotalTime / (double) iterations;
        double imperativeThroughput = (iterations * transactionsPerIteration) / (imperativeRealTime / 1000.0);
        
        System.out.println("\n📊 Resultados Imperativa:");
        System.out.println("  ⏱️  Tiempo real total: " + imperativeRealTime + "ms");
        System.out.println("  📈 Tiempo promedio por iteración: " + String.format("%.2f", imperativeAvgTime) + "ms");
        System.out.println("  🚀 Throughput: " + String.format("%.0f", imperativeThroughput) + " tx/s");
        System.out.println("  📦 Total procesadas: " + imperativeTotalTx + " transacciones");
        System.out.println("  🧵 Thread: " + Thread.currentThread().getName() + " (bloqueante)");
        
        // ==================== TEST IMPERATIVA CON STREAMS ====================
        System.out.println("\n🔷 ========== SOLUCIÓN CON STREAMS (Funcional) ==========");
        
        long streamsStartTime = System.currentTimeMillis();
        long streamsTotalTime = 0;
        int streamsTotalTx = 0;
        
        for (int i = 0; i < iterations; i++) {
            List<Transaction> data = generateTransactions(transactionsPerIteration);
            ProcessResult result = ImperativeSolution.processWithStreams(data, minAmount);
            streamsTotalTime += result.durationMs;
            streamsTotalTx += result.transactionsProcessed;
            
            if (i == 0) {
                System.out.println("  Primera iteración: " + result);
            }
        }
        
        long streamsEndTime = System.currentTimeMillis();
        long streamsRealTime = streamsEndTime - streamsStartTime;
        double streamsAvgTime = streamsTotalTime / (double) iterations;
        double streamsThroughput = (iterations * transactionsPerIteration) / (streamsRealTime / 1000.0);
        
        System.out.println("\n📊 Resultados Streams:");
        System.out.println("  ⏱️  Tiempo real total: " + streamsRealTime + "ms");
        System.out.println("  📈 Tiempo promedio por iteración: " + String.format("%.2f", streamsAvgTime) + "ms");
        System.out.println("  🚀 Throughput: " + String.format("%.0f", streamsThroughput) + " tx/s");
        System.out.println("  📦 Total procesadas: " + streamsTotalTx + " transacciones");
        
        // ==================== TEST REACTIVA (RxJava) ====================
        System.out.println("\n🟢 ========== SOLUCIÓN REACTIVA (RxJava) ==========");
        
        long reactiveStartTime = System.currentTimeMillis();
        long reactiveTotalTime = 0;
        int reactiveTotalTx = 0;
        
        for (int i = 0; i < iterations; i++) {
            List<Transaction> data = generateTransactions(transactionsPerIteration);
            ProcessResult result = ReactiveSolution.process(data, minAmount);
            reactiveTotalTime += result.durationMs;
            reactiveTotalTx += result.transactionsProcessed;
            
            if (i == 0) {
                System.out.println("  Primera iteración: " + result);
            }
        }
        
        long reactiveEndTime = System.currentTimeMillis();
        long reactiveRealTime = reactiveEndTime - reactiveStartTime;
        double reactiveAvgTime = reactiveTotalTime / (double) iterations;
        double reactiveThroughput = (iterations * transactionsPerIteration) / (reactiveRealTime / 1000.0);
        
        System.out.println("\n📊 Resultados Reactiva (RxJava):");
        System.out.println("  ⏱️  Tiempo real total: " + reactiveRealTime + "ms");
        System.out.println("  📈 Tiempo promedio por iteración: " + String.format("%.2f", reactiveAvgTime) + "ms");
        System.out.println("  🚀 Throughput: " + String.format("%.0f", reactiveThroughput) + " tx/s");
        System.out.println("  📦 Total procesadas: " + reactiveTotalTx + " transacciones");
        System.out.println("  🧵 Threads: RxJava Schedulers.io() (no bloqueante)");
        System.out.println("  💡 Operadores: fromIterable → filter → map → reduce");
        
        // ==================== COMPARACIÓN ====================
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    COMPARACIÓN FINAL                       ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        System.out.println("║                                                            ║");
        System.out.println("║  📊 TIEMPO DE EJECUCIÓN:                                   ║");
        System.out.println(String.format("║    Imperativa:    %8dms (baseline)                    ║", imperativeRealTime));
        System.out.println(String.format("║    Streams:       %8dms (%+.1f%%)                      ║", 
            streamsRealTime, 
            ((streamsRealTime - imperativeRealTime) / (double) imperativeRealTime) * 100));
        System.out.println(String.format("║    Reactiva:      %8dms (%+.1f%%)                      ║", 
            reactiveRealTime,
            ((reactiveRealTime - imperativeRealTime) / (double) imperativeRealTime) * 100));
        
        System.out.println("║                                                            ║");
        System.out.println("║  🚀 THROUGHPUT (transacciones/segundo):                    ║");
        System.out.println(String.format("║    Imperativa:    %8.0f tx/s                           ║", imperativeThroughput));
        System.out.println(String.format("║    Streams:       %8.0f tx/s (%.1fx)                     ║", 
            streamsThroughput,
            streamsThroughput / imperativeThroughput));
        System.out.println(String.format("║    Reactiva:      %8.0f tx/s (%.1fx)                     ║", 
            reactiveThroughput,
            reactiveThroughput / imperativeThroughput));
        
        System.out.println("║                                                            ║");
        System.out.println("║  🏆 GANADOR:                                               ║");
        
        if (reactiveRealTime < imperativeRealTime && reactiveRealTime < streamsRealTime) {
            double improvement = ((imperativeRealTime - reactiveRealTime) / (double) imperativeRealTime) * 100;
            System.out.println(String.format("║    🟢 REACTIVA (%.1f%% más rápida)                       ║", improvement));
        } else if (streamsRealTime < imperativeRealTime) {
            double improvement = ((imperativeRealTime - streamsRealTime) / (double) imperativeRealTime) * 100;
            System.out.println(String.format("║    🔷 STREAMS (%.1f%% más rápida)                        ║", improvement));
        } else {
            System.out.println("║    🔵 IMPERATIVA (más simple para casos simples)          ║");
        }
        
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    // ==================== MAIN ====================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  IMPERATIVA vs REACTIVA - COMPARACIÓN DE RENDIMIENTO      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Ejemplo simple para entender la diferencia
        System.out.println("=== EJEMPLO SIMPLE (5 transacciones) ===\n");
        
        List<Transaction> sampleData = generateTransactions(5);
        System.out.println("💰 Transacciones de ejemplo:");
        sampleData.forEach(tx -> System.out.println("  " + tx));
        System.out.println();

        double minAmount = 500.0;
        System.out.println("🔍 Filtro: Transacciones > $" + minAmount);
        System.out.println("💵 Comisión: 2%\n");

        // Imperativa
        System.out.println("🔵 IMPERATIVA:");
        ProcessResult imperativeResult = ImperativeSolution.process(sampleData, minAmount);
        System.out.println("  " + imperativeResult);
        System.out.println("  • Código: Secuencial, bloqueante, fácil de leer");
        System.out.println("  • Thread: " + Thread.currentThread().getName());
        System.out.println();

        // Reactiva con RxJava
        System.out.println("🟢 REACTIVA (RxJava):");
        ProcessResult reactiveResult = ReactiveSolution.process(sampleData, minAmount);
        System.out.println("  " + reactiveResult);
        System.out.println("  • Código: Observable → filter → map → reduce");
        System.out.println("  • Threads: RxJava Schedulers (asíncrono)");
        System.out.println("  • Operadores: Composición funcional declarativa");
        System.out.println();

        Thread.sleep(1000);

        // BENCHMARK con muchas iteraciones
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           BENCHMARK CON MUCHAS ITERACIONES");
        System.out.println("=".repeat(60) + "\n");

        // Diferentes tamaños para ver el comportamiento
        System.out.println("📊 Test 1: Muchas iteraciones, pocas transacciones");
        System.out.println("-".repeat(60));
        runBenchmark(1000, 100);  // 1000 iteraciones x 100 tx = 100,000 tx

        Thread.sleep(1000);
        
        System.out.println("\n\n📊 Test 2: Pocas iteraciones, muchas transacciones");
        System.out.println("-".repeat(60));
        runBenchmark(10, 10000);  // 10 iteraciones x 10,000 tx = 100,000 tx

        Thread.sleep(1000);
        
        System.out.println("\n\n📊 Test 3: Balance entre iteraciones y transacciones");
        System.out.println("-".repeat(60));
        runBenchmark(100, 1000);  // 100 iteraciones x 1,000 tx = 100,000 tx

        // Conclusiones
        System.out.println("\n\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                      CONCLUSIONES                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                            ║");
        System.out.println("║  📌 IMPERATIVA:                                            ║");
        System.out.println("║    ✅ Código más simple y legible                         ║");
        System.out.println("║    ✅ Fácil de debuggear                                   ║");
        System.out.println("║    ✅ Bueno para operaciones simples y síncronas          ║");
        System.out.println("║    ❌ Bloquea el thread actual                            ║");
        System.out.println("║    ❌ No aprovecha múltiples cores                        ║");
        System.out.println("║                                                            ║");
        System.out.println("║  📌 REACTIVA (RxJava):                                     ║");
        System.out.println("║    ✅ Código declarativo con operadores                   ║");
        System.out.println("║    ✅ No bloqueante, asíncrona                            ║");
        System.out.println("║    ✅ Aprovecha múltiples threads con Schedulers         ║");
        System.out.println("║    ✅ Escalable para muchas operaciones concurrentes     ║");
        System.out.println("║    ✅ Ideal para I/O, APIs, streams de datos             ║");
        System.out.println("║    ✅ +300 operadores predefinidos en RxJava             ║");
        System.out.println("║    ❌ Curva de aprendizaje inicial                        ║");
        System.out.println("║                                                            ║");
        System.out.println("║  🎯 CUÁNDO USAR CADA UNA:                                  ║");
        System.out.println("║    • Imperativa: Scripts, batch jobs, operaciones simples ║");
        System.out.println("║    • Streams: Procesamiento en memoria, pipelines         ║");
        System.out.println("║    • Reactiva (RxJava): APIs REST, microservicios, I/O   ║");
        System.out.println("║                         streaming, eventos en tiempo real  ║");
        System.out.println("║                                                            ║");
        System.out.println("║  💡 CÓDIGO RXJAVA DEL EJEMPLO:                            ║");
        System.out.println("║                                                            ║");
        System.out.println("║    Observable.fromIterable(transactions)                   ║");
        System.out.println("║        .subscribeOn(Schedulers.io())                       ║");
        System.out.println("║        .filter(tx -> tx.amount > minAmount)                ║");
        System.out.println("║        .map(tx -> tx.amount * 1.02)                        ║");
        System.out.println("║        .reduce(0.0, (acc, amt) -> acc + amt)               ║");
        System.out.println("║        .subscribe(total -> ...)                            ║");
        System.out.println("║                                                            ║");
        System.out.println("║    ✨ Solo 6 líneas para toda la lógica reactiva!         ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        System.out.println("\n🎓 RxJava hace la programación reactiva elegante y poderosa!");
        System.out.println("   • Operadores expresivos y componibles");
        System.out.println("   • Manejo automático de threads con Schedulers");
        System.out.println("   • Gestión de errores integrada");
        System.out.println("   • Backpressure para control de flujo");
    }
}

