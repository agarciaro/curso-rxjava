package com.formadoresit.rxjava.introduccion;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * INTRODUCCIÓN - Ejemplo 07: Ventaja Real de Programación Reactiva
 * 
 * CASO DE USO REAL: Sistema de Recomendación de Productos E-commerce
 * 
 * Escenario:
 * - Múltiples llamadas a APIs/servicios (I/O bound)
 * - Necesidad de combinar datos de diferentes fuentes
 * - Timeouts y manejo de errores
 * - Operaciones que pueden ejecutarse en paralelo
 * 
 * Este ejemplo muestra la VERDADERA ventaja de la programación reactiva:
 * - Operaciones I/O no bloqueantes
 * - Ejecución paralela automática
 * - Composición elegante de operaciones asíncronas
 * - Manejo de errores declarativo
 * - Cancelación de operaciones
 * 
 * DIFERENCIA CLAVE:
 * - Imperativa: Secuencial, bloqueante, cada llamada espera a la anterior
 * - Reactiva: Paralela, no bloqueante, todas las llamadas al mismo tiempo
 */
public class Ejemplo07_VentajaReactivaIO {

    // ==================== MODELOS DE DATOS ====================

    static class User {
        final int id;
        final String name;
        final String category;

        User(int id, String name, String category) {
            this.id = id;
            this.name = name;
            this.category = category;
        }

        @Override
        public String toString() {
            return String.format("User{id=%d, name='%s', category='%s'}", id, name, category);
        }
    }

    static class Purchase {
        final int productId;
        final String productName;
        final double price;

        Purchase(int productId, String productName, double price) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
        }

        @Override
        public String toString() {
            return String.format("Purchase{id=%d, name='%s', $%.2f}", productId, productName, price);
        }
    }

    static class Product {
        final int id;
        final String name;
        final String category;
        final double rating;

        Product(int id, String name, String category, double rating) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.rating = rating;
        }

        @Override
        public String toString() {
            return String.format("Product{id=%d, name='%s', cat='%s', rating=%.1f}", 
                id, name, category, rating);
        }
    }

    static class Recommendation {
        final Product product;
        final double score;
        final String reason;

        Recommendation(Product product, double score, String reason) {
            this.product = product;
            this.score = score;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return String.format("💡 %s (score: %.2f) - %s", 
                product.name, score, reason);
        }
    }

    // ==================== SERVICIOS SIMULADOS (APIs) ====================

    /**
     * Simula llamada a API de Usuarios (delay 500ms)
     */
    static class UserService {
        static User getUserProfile(int userId) {
            System.out.println("  🔵 [" + Thread.currentThread().getName() + "] Llamando User API...");
            simulateNetworkDelay(500);
            System.out.println("  ✅ [" + Thread.currentThread().getName() + "] User API respondió");
            return new User(userId, "Juan Pérez", "Electronics");
        }

        static Observable<User> getUserProfileAsync(int userId) {
            return Observable.fromCallable(() -> {
                System.out.println("  🟢 [" + Thread.currentThread().getName() + "] Llamando User API (async)...");
                simulateNetworkDelay(500);
                System.out.println("  ✅ [" + Thread.currentThread().getName() + "] User API respondió");
                return new User(userId, "Juan Pérez", "Electronics");
            }).subscribeOn(Schedulers.io());
        }
    }

    /**
     * Simula llamada a API de Historial de Compras (delay 800ms)
     */
    static class PurchaseHistoryService {
        static List<Purchase> getPurchaseHistory(int userId) {
            System.out.println("  🔵 [" + Thread.currentThread().getName() + "] Llamando Purchase History API...");
            simulateNetworkDelay(800);
            System.out.println("  ✅ [" + Thread.currentThread().getName() + "] Purchase History API respondió");
            return Arrays.asList(
                new Purchase(101, "Laptop HP", 899.99),
                new Purchase(102, "Mouse Logitech", 29.99),
                new Purchase(103, "Teclado Mecánico", 89.99)
            );
        }

        static Observable<List<Purchase>> getPurchaseHistoryAsync(int userId) {
            return Observable.fromCallable(() -> {
                System.out.println("  🟢 [" + Thread.currentThread().getName() + "] Llamando Purchase History API (async)...");
                simulateNetworkDelay(800);
                System.out.println("  ✅ [" + Thread.currentThread().getName() + "] Purchase History API respondió");
                return Arrays.asList(
                    new Purchase(101, "Laptop HP", 899.99),
                    new Purchase(102, "Mouse Logitech", 29.99),
                    new Purchase(103, "Teclado Mecánico", 89.99)
                );
            }).subscribeOn(Schedulers.io());
        }
    }

    /**
     * Simula llamada a API de Productos Populares (delay 600ms)
     */
    static class ProductCatalogService {
        static List<Product> getPopularProducts(String category) {
            System.out.println("  🔵 [" + Thread.currentThread().getName() + "] Llamando Product Catalog API...");
            simulateNetworkDelay(600);
            System.out.println("  ✅ [" + Thread.currentThread().getName() + "] Product Catalog API respondió");
            return Arrays.asList(
                new Product(201, "Monitor 4K Samsung", "Electronics", 4.7),
                new Product(202, "Webcam Logitech", "Electronics", 4.5),
                new Product(203, "Auriculares Sony", "Electronics", 4.8),
                new Product(204, "SSD 1TB", "Electronics", 4.6)
            );
        }

        static Observable<List<Product>> getPopularProductsAsync(String category) {
            return Observable.fromCallable(() -> {
                System.out.println("  🟢 [" + Thread.currentThread().getName() + "] Llamando Product Catalog API (async)...");
                simulateNetworkDelay(600);
                System.out.println("  ✅ [" + Thread.currentThread().getName() + "] Product Catalog API respondió");
                return Arrays.asList(
                    new Product(201, "Monitor 4K Samsung", "Electronics", 4.7),
                    new Product(202, "Webcam Logitech", "Electronics", 4.5),
                    new Product(203, "Auriculares Sony", "Electronics", 4.8),
                    new Product(204, "SSD 1TB", "Electronics", 4.6)
                );
            }).subscribeOn(Schedulers.io());
        }
    }

    /**
     * Simula llamada a API de Tendencias (delay 700ms)
     */
    static class TrendingService {
        static List<Product> getTrendingProducts() {
            System.out.println("  🔵 [" + Thread.currentThread().getName() + "] Llamando Trending API...");
            simulateNetworkDelay(700);
            System.out.println("  ✅ [" + Thread.currentThread().getName() + "] Trending API respondió");
            return Arrays.asList(
                new Product(301, "iPhone 15", "Electronics", 4.9),
                new Product(302, "AirPods Pro", "Electronics", 4.7)
            );
        }

        static Observable<List<Product>> getTrendingProductsAsync() {
            return Observable.fromCallable(() -> {
                System.out.println("  🟢 [" + Thread.currentThread().getName() + "] Llamando Trending API (async)...");
                simulateNetworkDelay(700);
                System.out.println("  ✅ [" + Thread.currentThread().getName() + "] Trending API respondió");
                return Arrays.asList(
                    new Product(301, "iPhone 15", "Electronics", 4.9),
                    new Product(302, "AirPods Pro", "Electronics", 4.7)
                );
            }).subscribeOn(Schedulers.io());
        }
    }

    // ==================== SOLUCIÓN IMPERATIVA SÍNCRONA ====================

    static class ImperativeSyncSolution {
        
        /**
         * Genera recomendaciones de forma IMPERATIVA SÍNCRONA
         * Problema: Cada llamada BLOQUEA hasta que termine
         * Total: 500 + 800 + 600 + 700 = 2600ms mínimo
         */
        static List<Recommendation> generateRecommendations(int userId) {
            long startTime = System.currentTimeMillis();
            System.out.println("\n🔵 SOLUCIÓN IMPERATIVA SÍNCRONA - Secuencial y Bloqueante");
            System.out.println("─".repeat(60));

            // Llamada 1: Obtener perfil de usuario (500ms)
            User user = UserService.getUserProfile(userId);

            // Llamada 2: Obtener historial (800ms) - ESPERA a que termine la anterior
            List<Purchase> purchases = PurchaseHistoryService.getPurchaseHistory(userId);

            // Llamada 3: Obtener catálogo (600ms) - ESPERA a que termine la anterior
            List<Product> catalog = ProductCatalogService.getPopularProducts(user.category);

            // Llamada 4: Obtener trending (700ms) - ESPERA a que termine la anterior
            List<Product> trending = TrendingService.getTrendingProducts();

            // Procesar recomendaciones
            List<Recommendation> recommendations = processRecommendations(user, purchases, catalog, trending);

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("\n⏱️  Tiempo total: " + duration + "ms");
            System.out.println("📊 Eficiencia: Ejecutó 4 llamadas SECUENCIALMENTE");
            
            return recommendations;
        }
    }

    // ==================== SOLUCIÓN IMPERATIVA ASÍNCRONA ====================

    static class ImperativeAsyncSolution {
        
        /**
         * Genera recomendaciones de forma IMPERATIVA ASÍNCRONA con CompletableFuture
         * Ventaja: Las llamadas se ejecutan en PARALELO
         * Problema: Código verboso, difícil de leer, propenso a errores
         * Total: max(500, 800, 600, 700) = 800ms (similar a reactiva)
         */
        static List<Recommendation> generateRecommendations(int userId) throws ExecutionException, InterruptedException {
            long startTime = System.currentTimeMillis();
            System.out.println("\n🟡 SOLUCIÓN IMPERATIVA ASÍNCRONA - Paralela con CompletableFuture");
            System.out.println("─".repeat(60));

            ExecutorService executor = Executors.newFixedThreadPool(4);

            try {
                // Iniciar todas las llamadas EN PARALELO usando CompletableFuture
                CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> {
                    System.out.println("  🟡 [" + Thread.currentThread().getName() + "] Llamando User API (async)...");
                    simulateNetworkDelay(500);
                    System.out.println("  ✅ [" + Thread.currentThread().getName() + "] User API respondió");
                    return new User(userId, "Juan Pérez", "Electronics");
                }, executor);

                CompletableFuture<List<Purchase>> purchasesFuture = CompletableFuture.supplyAsync(() -> {
                    System.out.println("  🟡 [" + Thread.currentThread().getName() + "] Llamando Purchase History API (async)...");
                    simulateNetworkDelay(800);
                    System.out.println("  ✅ [" + Thread.currentThread().getName() + "] Purchase History API respondió");
                    return Arrays.asList(
                        new Purchase(101, "Laptop HP", 899.99),
                        new Purchase(102, "Mouse Logitech", 29.99),
                        new Purchase(103, "Teclado Mecánico", 89.99)
                    );
                }, executor);

                // Catálogo depende del usuario, usar thenCompose
                CompletableFuture<List<Product>> catalogFuture = userFuture.thenComposeAsync(user -> 
                    CompletableFuture.supplyAsync(() -> {
                        System.out.println("  🟡 [" + Thread.currentThread().getName() + "] Llamando Product Catalog API (async)...");
                        simulateNetworkDelay(600);
                        System.out.println("  ✅ [" + Thread.currentThread().getName() + "] Product Catalog API respondió");
                        return Arrays.asList(
                            new Product(201, "Monitor 4K Samsung", "Electronics", 4.7),
                            new Product(202, "Webcam Logitech", "Electronics", 4.5),
                            new Product(203, "Auriculares Sony", "Electronics", 4.8),
                            new Product(204, "SSD 1TB", "Electronics", 4.6)
                        );
                    }, executor)
                , executor);

                CompletableFuture<List<Product>> trendingFuture = CompletableFuture.supplyAsync(() -> {
                    System.out.println("  🟡 [" + Thread.currentThread().getName() + "] Llamando Trending API (async)...");
                    simulateNetworkDelay(700);
                    System.out.println("  ✅ [" + Thread.currentThread().getName() + "] Trending API respondió");
                    return Arrays.asList(
                        new Product(301, "iPhone 15", "Electronics", 4.9),
                        new Product(302, "AirPods Pro", "Electronics", 4.7)
                    );
                }, executor);

                // Combinar todos los resultados - CÓDIGO VERBOSO
                CompletableFuture<List<Recommendation>> combinedFuture = 
                    CompletableFuture.allOf(userFuture, purchasesFuture, catalogFuture, trendingFuture)
                        .thenApply(v -> {
                            try {
                                System.out.println("\n  🎯 Todas las APIs respondieron - Procesando...");
                                User user = userFuture.get();
                                List<Purchase> purchases = purchasesFuture.get();
                                List<Product> catalog = catalogFuture.get();
                                List<Product> trending = trendingFuture.get();
                                
                                return processRecommendations(user, purchases, catalog, trending);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });

                List<Recommendation> recommendations = combinedFuture.get();

                long duration = System.currentTimeMillis() - startTime;
                System.out.println("\n⏱️  Tiempo total: " + duration + "ms");
                System.out.println("📊 Eficiencia: Ejecutó 4 llamadas EN PARALELO");
                System.out.println("⚠️  Complejidad: ~40 líneas de código, verboso, propenso a errores");

                return recommendations;

            } finally {
                executor.shutdown();
            }
        }
    }

    /**
     * Método helper para procesar recomendaciones (usado por todas las soluciones)
     */
    private static List<Recommendation> processRecommendations(
            User user, List<Purchase> purchases, List<Product> catalog, List<Product> trending) {
        
        List<Recommendation> recommendations = new ArrayList<>();
        
        Set<Integer> purchasedIds = purchases.stream()
            .map(p -> p.productId)
            .collect(Collectors.toSet());

        catalog.stream()
            .filter(p -> !purchasedIds.contains(p.id))
            .forEach(p -> recommendations.add(new Recommendation(
                p, p.rating * 10, "Popular en " + user.category
            )));

        trending.stream()
            .filter(p -> !purchasedIds.contains(p.id))
            .forEach(p -> recommendations.add(new Recommendation(
                p, p.rating * 12, "Trending ahora"
            )));

        return recommendations;
    }

    // ==================== SOLUCIÓN REACTIVA ====================

    static class ReactiveSolution {
        
        /**
         * Genera recomendaciones de forma REACTIVA con RxJava
         * Ventaja: Todas las llamadas en PARALELO con código ELEGANTE
         * Total: max(500, 800, 600, 700) = 800ms (similar a CompletableFuture)
         * Diferencia: CÓDIGO SIMPLE, COMPOSABLE, MANTENIBLE
         */
        static List<Recommendation> generateRecommendations(int userId) throws InterruptedException {
            long startTime = System.currentTimeMillis();
            System.out.println("\n🟢 SOLUCIÓN REACTIVA (RxJava) - Paralela, Elegante y Declarativa");
            System.out.println("─".repeat(60));

            // ⚡ ELEGANCIA DE RxJava: Todas las llamadas en PARALELO con código simple
            List<Recommendation> recommendations = Observable.zip(
                UserService.getUserProfileAsync(userId),
                PurchaseHistoryService.getPurchaseHistoryAsync(userId),
                Observable.defer(() -> 
                    UserService.getUserProfileAsync(userId)
                        .flatMap(user -> ProductCatalogService.getPopularProductsAsync(user.category))
                ),
                TrendingService.getTrendingProductsAsync(),
                
                // Combinar resultados - CÓDIGO LIMPIO Y LEGIBLE
                (user, purchases, catalog, trending) -> {
                    System.out.println("\n  🎯 Todas las APIs respondieron - Procesando...");
                    return processRecommendations(user, purchases, catalog, trending);
                }
            )
            .timeout(5, TimeUnit.SECONDS)
            .onErrorReturn(error -> {
                System.err.println("  ❌ Error: " + error.getMessage());
                return Collections.emptyList();
            })
            .blockingFirst();

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("\n⏱️  Tiempo total: " + duration + "ms");
            System.out.println("📊 Eficiencia: Ejecutó 4 llamadas EN PARALELO");
            System.out.println("✨ Elegancia: ~15 líneas de código, simple, mantenible");
            
            return recommendations;
        }
    }

    // ==================== UTILIDADES ====================

    private static void simulateNetworkDelay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ==================== MAIN ====================

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  VENTAJA REAL DE PROGRAMACIÓN REACTIVA                     ║");
        System.out.println("║  Caso: Sistema de Recomendación de Productos              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        int userId = 12345;
        
        System.out.println("\n📋 Escenario:");
        System.out.println("  • 4 llamadas a diferentes APIs/servicios");
        System.out.println("  • User API: 500ms");
        System.out.println("  • Purchase History API: 800ms");
        System.out.println("  • Product Catalog API: 600ms");
        System.out.println("  • Trending API: 700ms");
        System.out.println("\n❓ ¿Cuál es más rápida?");

        // ==================== TEST 1: IMPERATIVA SÍNCRONA ====================
        long syncStart = System.currentTimeMillis();
        List<Recommendation> syncResults = ImperativeSyncSolution.generateRecommendations(userId);
        long syncDuration = System.currentTimeMillis() - syncStart;

        System.out.println("\n📦 Recomendaciones encontradas: " + syncResults.size());
        syncResults.forEach(r -> System.out.println("  " + r));

        Thread.sleep(1000);

        // ==================== TEST 2: IMPERATIVA ASÍNCRONA ====================
        long asyncStart = System.currentTimeMillis();
        List<Recommendation> asyncResults = ImperativeAsyncSolution.generateRecommendations(userId);
        long asyncDuration = System.currentTimeMillis() - asyncStart;

        System.out.println("\n📦 Recomendaciones encontradas: " + asyncResults.size());
        asyncResults.forEach(r -> System.out.println("  " + r));

        Thread.sleep(1000);

        // ==================== TEST 3: REACTIVA ====================
        long reactiveStart = System.currentTimeMillis();
        List<Recommendation> reactiveResults = ReactiveSolution.generateRecommendations(userId);
        long reactiveDuration = System.currentTimeMillis() - reactiveStart;

        System.out.println("\n📦 Recomendaciones encontradas: " + reactiveResults.size());
        reactiveResults.forEach(r -> System.out.println("  " + r));

        // ==================== COMPARACIÓN FINAL ====================
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                  COMPARACIÓN DE LAS 3 SOLUCIONES           ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                            ║");
        System.out.println("║  1️⃣  IMPERATIVA SÍNCRONA (Secuencial)                      ║");
        System.out.println(String.format("║      Tiempo:  %8dms                                      ║", syncDuration));
        System.out.println("║      Código:  Simple pero LENTO                           ║");
        System.out.println("║      Threads: 1 thread (bloqueante)                       ║");
        System.out.println("║                                                            ║");
        System.out.println("║  2️⃣  IMPERATIVA ASÍNCRONA (CompletableFuture)             ║");
        System.out.println(String.format("║      Tiempo:  %8dms                                      ║", asyncDuration));
        System.out.println("║      Código:  ~40 líneas, VERBOSO, complejo              ║");
        System.out.println("║      Threads: Pool de threads                             ║");
        System.out.println("║                                                            ║");
        System.out.println("║  3️⃣  REACTIVA (RxJava)                                     ║");
        System.out.println(String.format("║      Tiempo:  %8dms                                      ║", reactiveDuration));
        System.out.println("║      Código:  ~15 líneas, ELEGANTE, simple               ║");
        System.out.println("║      Threads: Pool automático con Schedulers              ║");
        System.out.println("║                                                            ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                            ║");
        
        double speedupSync = (double) syncDuration / reactiveDuration;
        double speedupAsync = (double) asyncDuration / reactiveDuration;
        
        System.out.println(String.format("║  🚀 vs Síncrona:  %.2fx más rápida                          ║", speedupSync));
        System.out.println(String.format("║  ⚖️  vs Asíncrona: %.2fx (similar en tiempo)                 ║", speedupAsync));
        System.out.println("║                                                            ║");
        System.out.println("║  💡 VENTAJA DE RxJava:                                     ║");
        System.out.println("║     • Mismo rendimiento que CompletableFuture             ║");
        System.out.println("║     • Pero con 2-3x MENOS código                          ║");
        System.out.println("║     • Código más LEGIBLE y MANTENIBLE                     ║");
        System.out.println("║     • Operadores COMPONIBLES                              ║");
        System.out.println("║     • Manejo de errores DECLARATIVO                       ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              COMPARACIÓN DE COMPLEJIDAD DE CÓDIGO          ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                            ║");
        System.out.println("║  🟡 IMPERATIVA ASÍNCRONA (CompletableFuture):             ║");
        System.out.println("║                                                            ║");
        System.out.println("║  ExecutorService executor = Executors.newFixedThreadPool║");
        System.out.println("║  CompletableFuture<User> userFuture =                     ║");
        System.out.println("║      CompletableFuture.supplyAsync(() -> {...}, executor)║");
        System.out.println("║  CompletableFuture<List<Purchase>> purchasesFuture =      ║");
        System.out.println("║      CompletableFuture.supplyAsync(() -> {...}, executor)║");
        System.out.println("║  CompletableFuture<List<Product>> catalogFuture =         ║");
        System.out.println("║      userFuture.thenComposeAsync(user ->                  ║");
        System.out.println("║          CompletableFuture.supplyAsync(...), executor);   ║");
        System.out.println("║  CompletableFuture<List<Product>> trendingFuture =        ║");
        System.out.println("║      CompletableFuture.supplyAsync(() -> {...}, executor)║");
        System.out.println("║  CompletableFuture.allOf(...).thenApply(v -> {            ║");
        System.out.println("║      try {                                                 ║");
        System.out.println("║          User user = userFuture.get();                     ║");
        System.out.println("║          List<Purchase> purchases = purchasesFuture.get(); ║");
        System.out.println("║          // ... procesar ...                               ║");
        System.out.println("║      } catch (Exception e) { ... }                         ║");
        System.out.println("║  }).get();                                                 ║");
        System.out.println("║  executor.shutdown();                                      ║");
        System.out.println("║                                                            ║");
        System.out.println("║  📊 ~40 líneas, verboso, propenso a errores               ║");
        System.out.println("║                                                            ║");
        System.out.println("║  🟢 REACTIVA (RxJava):                                     ║");
        System.out.println("║                                                            ║");
        System.out.println("║  Observable.zip(                                           ║");
        System.out.println("║      UserService.getUserProfileAsync(userId),              ║");
        System.out.println("║      PurchaseHistoryService.getPurchaseHistoryAsync(...),  ║");
        System.out.println("║      ProductCatalogService.getPopularProductsAsync(...),   ║");
        System.out.println("║      TrendingService.getTrendingProductsAsync(),           ║");
        System.out.println("║      (user, purchases, catalog, trending) -> {             ║");
        System.out.println("║          return processRecommendations(...);               ║");
        System.out.println("║      }                                                      ║");
        System.out.println("║  )                                                          ║");
        System.out.println("║  .timeout(5, TimeUnit.SECONDS)                             ║");
        System.out.println("║  .onErrorReturn(error -> Collections.emptyList())          ║");
        System.out.println("║  .blockingFirst();                                         ║");
        System.out.println("║                                                            ║");
        System.out.println("║  📊 ~15 líneas, elegante, fácil de entender               ║");
        System.out.println("║                                                            ║");
        System.out.println("║  ✨ RxJava: MISMO RENDIMIENTO, MENOS CÓDIGO               ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    VENTAJAS DE RxJava                      ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                            ║");
        System.out.println("║  1. ⚡ PARALELISMO AUTOMÁTICO                              ║");
        System.out.println("║     Observable.zip() ejecuta todo en paralelo             ║");
        System.out.println("║                                                            ║");
        System.out.println("║  2. 🎯 COMPOSICIÓN ELEGANTE                                ║");
        System.out.println("║     Combina múltiples streams con operadores              ║");
        System.out.println("║                                                            ║");
        System.out.println("║  3. 🛡️  MANEJO DE ERRORES DECLARATIVO                     ║");
        System.out.println("║     onErrorReturn() maneja errores sin try-catch          ║");
        System.out.println("║                                                            ║");
        System.out.println("║  4. ⏰ TIMEOUTS INTEGRADOS                                 ║");
        System.out.println("║     timeout() evita esperas infinitas                     ║");
        System.out.println("║                                                            ║");
        System.out.println("║  5. 🧵 SCHEDULERS AUTOMÁTICOS                              ║");
        System.out.println("║     subscribeOn(Schedulers.io()) maneja threads           ║");
        System.out.println("║                                                            ║");
        System.out.println("║  6. 🚫 CANCELACIÓN FÁCIL                                   ║");
        System.out.println("║     dispose() cancela todas las operaciones               ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    CASOS DE USO IDEALES                    ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                            ║");
        System.out.println("║  ✅ APIs REST que llaman a múltiples servicios            ║");
        System.out.println("║  ✅ Microservicios con operaciones I/O                    ║");
        System.out.println("║  ✅ Consultas a bases de datos en paralelo                ║");
        System.out.println("║  ✅ Sistemas de recomendación                             ║");
        System.out.println("║  ✅ Agregación de datos de múltiples fuentes             ║");
        System.out.println("║  ✅ Streaming de datos en tiempo real                     ║");
        System.out.println("║  ✅ Operaciones con timeouts y reintentos                 ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        System.out.println("\n🎓 CONCLUSIÓN:");
        System.out.println("\n   ❓ ¿Por qué RxJava si CompletableFuture también es rápido?");
        System.out.println("\n   ✅ VENTAJAS DE RxJava:");
        System.out.println("      1. CÓDIGO MÁS SIMPLE: 15 líneas vs 40 líneas");
        System.out.println("      2. MÁS LEGIBLE: Estilo declarativo vs imperativo");
        System.out.println("      3. COMPONIBLE: Operadores se encadenan fácilmente");
        System.out.println("      4. MANEJO DE ERRORES: onErrorReturn() vs try-catch");
        System.out.println("      5. TIMEOUTS: Integrado vs manual");
        System.out.println("      6. CANCELACIÓN: dispose() vs complex shutdown logic");
        System.out.println("      7. +300 OPERADORES: map, filter, flatMap, retry, etc.");
        System.out.println("      8. MANTENIBILIDAD: Fácil de modificar y extender");
        System.out.println("\n   💡 RxJava = Performance + Elegancia + Mantenibilidad");
    }
}

