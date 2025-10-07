package com.formadoresit.rxjava.practicas.practica03;

import io.reactivex.Observable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * PRÁCTICA 03 - Ejercicio 3.3: Transacciones Bancarias
 * 
 * Aplicar operadores de reducción: reduce, count, toMap, toSortedList, all, any
 */
public class Ejercicio03_TransaccionesBancarias {

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3.3: Transacciones Bancarias ===\n");

        // Datos de prueba
        List<Transaccion> transacciones = Arrays.asList(
            new Transaccion("deposito", 1000.0, LocalDateTime.now().minusDays(5)),
            new Transaccion("retiro", 150.0, LocalDateTime.now().minusDays(4)),
            new Transaccion("deposito", 500.0, LocalDateTime.now().minusDays(3)),
            new Transaccion("retiro", 75.5, LocalDateTime.now().minusDays(3)),
            new Transaccion("deposito", 250.0, LocalDateTime.now().minusDays(2)),
            new Transaccion("retiro", 200.0, LocalDateTime.now().minusDays(1)),
            new Transaccion("deposito", 800.0, LocalDateTime.now()),
            new Transaccion("retiro", 324.5, LocalDateTime.now())
        );

        Observable<Transaccion> transaccionesObs = Observable.fromIterable(transacciones);

        // 1. Balance total usando reduce
        System.out.println("--- 1. Balance Total ---");
        transaccionesObs
            .map(t -> t.tipo.equals("deposito") ? t.monto : -t.monto)
            .reduce(0.0, Double::sum)
            .subscribe(balance -> System.out.printf("Saldo final: %.2f €\n\n", balance));

        // 2. Número total de transacciones usando count
        System.out.println("--- 2. Estadísticas ---");
        transaccionesObs.count()
            .subscribe(total -> System.out.println("Total transacciones: " + total));

        Observable.fromIterable(transacciones)
            .filter(t -> t.tipo.equals("deposito"))
            .count()
            .subscribe(total -> System.out.println("Total depósitos: " + total));

        Observable.fromIterable(transacciones)
            .filter(t -> t.tipo.equals("retiro"))
            .count()
            .subscribe(total -> System.out.println("Total retiros: " + total));
        
        System.out.println();

        // 3. Agrupar por tipo usando toMap
        System.out.println("--- 3. Transacciones por Tipo ---");
        Observable.fromIterable(transacciones)
            .groupBy(t -> t.tipo)
            .flatMapSingle(grupo -> 
                grupo.reduce(0.0, (sum, t) -> sum + t.monto)
                     .map(total -> Map.entry(grupo.getKey(), total))
            )
            .subscribe(entry -> 
                System.out.printf("%s: %.2f €\n", 
                    entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1), 
                    entry.getValue())
            );
        
        System.out.println();

        // 4. Lista de montos ordenados
        System.out.println("--- 4. Montos Ordenados ---");
        Observable.fromIterable(transacciones)
            .map(t -> t.monto)
            .toSortedList()
            .subscribe(montos -> {
                System.out.println("Montos ordenados (menor a mayor):");
                montos.forEach(m -> System.out.printf("  %.2f €\n", m));
            });
        
        System.out.println();

        // 5. Verificar si todas las transacciones son > 0
        System.out.println("--- 5. Validaciones ---");
        Observable.fromIterable(transacciones)
            .map(t -> t.monto)
            .all(monto -> monto > 0)
            .subscribe(todas -> 
                System.out.println("¿Todas las transacciones > 0? " + todas)
            );

        // 6. Verificar si hay algún retiro > 1000
        Observable.fromIterable(transacciones)
            .filter(t -> t.tipo.equals("retiro"))
            .any(t -> t.monto > 1000)
            .subscribe(hay -> 
                System.out.println("¿Algún retiro > 1000? " + hay)
            );

        // Estadísticas adicionales
        System.out.println("\n--- Estadísticas Adicionales ---");
        Observable.fromIterable(transacciones)
            .map(t -> t.monto)
            .reduce(new EstadisticasTransacciones(), (stats, monto) -> {
                stats.count++;
                stats.sum += monto;
                stats.min = Math.min(stats.min, monto);
                stats.max = Math.max(stats.max, monto);
                return stats;
            })
            .subscribe(stats -> {
                System.out.printf("Monto mínimo: %.2f €\n", stats.min);
                System.out.printf("Monto máximo: %.2f €\n", stats.max);
                System.out.printf("Promedio: %.2f €\n", stats.sum / stats.count);
            });
    }
}

class Transaccion {
    String tipo;
    double monto;
    LocalDateTime fecha;

    Transaccion(String tipo, double monto, LocalDateTime fecha) {
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
    }
}

class EstadisticasTransacciones {
    int count = 0;
    double sum = 0;
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
}

