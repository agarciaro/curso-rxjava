package com.formadoresit.rxjava.tema5;

import io.reactivex.Observable;

/**
 * TEMA 5: Combinando Observables
 * Ejemplo 03: zip() - Combina elementos por pares
 * 
 * zip toma un elemento de cada Observable y los combina usando una función
 * Espera a que todos emitan antes de combinar
 */
public class Ejemplo03_Zip {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo 03: zip() ===\n");

        // 1. zip básico con dos Observables
        System.out.println("--- zip() básico ---");
        Observable<String> nombres = Observable.just("Juan", "María", "Pedro");
        Observable<Integer> edades = Observable.just(25, 30, 28);
        
        Observable.zip(nombres, edades, (nombre, edad) -> nombre + " tiene " + edad + " años")
            .subscribe(resultado -> System.out.println(resultado));

        // 2. zip con tres Observables
        System.out.println("\n--- zip() con tres fuentes ---");
        Observable<String> nombres2 = Observable.just("Ana", "Carlos");
        Observable<Integer> edades2 = Observable.just(22, 35);
        Observable<String> ciudades = Observable.just("Madrid", "Barcelona");
        
        Observable.zip(nombres2, edades2, ciudades, 
            (nombre, edad, ciudad) -> nombre + ", " + edad + " años, vive en " + ciudad)
            .subscribe(info -> System.out.println(info));

        // 3. zip con longitudes diferentes
        System.out.println("\n--- zip() con longitudes diferentes ---");
        Observable<Integer> corto = Observable.just(1, 2, 3);
        Observable<String> largo = Observable.just("A", "B", "C", "D", "E");
        
        Observable.zip(corto, largo, (numero, letra) -> numero + "-" + letra)
            .subscribe(resultado -> System.out.println(resultado));
        System.out.println("(Nota: zip termina cuando el más corto completa)");

        // 4. zipWith() - Versión de instancia
        System.out.println("\n--- zipWith() ---");
        Observable.just(1, 2, 3, 4, 5)
            .zipWith(Observable.just("A", "B", "C", "D", "E"), 
                (numero, letra) -> letra + numero)
            .subscribe(resultado -> System.out.println(resultado));

        // 5. zipIterable - zip con colección
        System.out.println("\n--- zipIterable() ---");
        Observable<String> letras = Observable.just("X", "Y", "Z");
        Observable.zip(
            java.util.Arrays.asList(
                Observable.just(1),
                Observable.just(2),
                Observable.just(3)
            ),
            valores -> {
                StringBuilder sb = new StringBuilder();
                for (Object v : valores) {
                    sb.append(v).append(",");
                }
                return sb.toString();
            }
        )
        .subscribe(resultado -> System.out.println("Zipped: " + resultado));

        // Ejemplo práctico: Combinar resultados de múltiples APIs
        System.out.println("\n--- Ejemplo práctico: Combinar APIs ---");
        Observable<Usuario> usuario = obtenerUsuario(123);
        Observable<Perfil> perfil = obtenerPerfil(123);
        Observable<Configuracion> config = obtenerConfiguracion(123);
        
        Observable.zip(usuario, perfil, config, 
            (u, p, c) -> new VistaCompleta(u, p, c))
            .subscribe(vista -> System.out.println(vista));

        // Ejemplo práctico: Combinar coordenadas
        System.out.println("\n--- Ejemplo práctico: Coordenadas ---");
        Observable<Integer> coordX = Observable.just(10, 20, 30);
        Observable<Integer> coordY = Observable.just(5, 15, 25);
        
        Observable.zip(coordX, coordY, (x, y) -> new Punto(x, y))
            .subscribe(punto -> System.out.println(punto));

        System.out.println("\n=== CONCEPTOS CLAVE ===");
        System.out.println("• zip: Combina elementos por índice");
        System.out.println("• Espera a que todos los Observables emitan");
        System.out.println("• Termina cuando el más corto completa");
        System.out.println("• Útil para combinar resultados relacionados");
    }

    private static Observable<Usuario> obtenerUsuario(int id) {
        return Observable.just(new Usuario(id, "Juan Pérez"));
    }

    private static Observable<Perfil> obtenerPerfil(int id) {
        return Observable.just(new Perfil(id, "Admin"));
    }

    private static Observable<Configuracion> obtenerConfiguracion(int id) {
        return Observable.just(new Configuracion("dark", "es"));
    }

    static class Usuario {
        int id;
        String nombre;
        Usuario(int id, String nombre) { this.id = id; this.nombre = nombre; }
    }

    static class Perfil {
        int userId;
        String rol;
        Perfil(int userId, String rol) { this.userId = userId; this.rol = rol; }
    }

    static class Configuracion {
        String tema;
        String idioma;
        Configuracion(String tema, String idioma) { this.tema = tema; this.idioma = idioma; }
    }

    static class VistaCompleta {
        Usuario usuario;
        Perfil perfil;
        Configuracion config;
        
        VistaCompleta(Usuario u, Perfil p, Configuracion c) {
            this.usuario = u;
            this.perfil = p;
            this.config = c;
        }

        @Override
        public String toString() {
            return String.format("Vista[%s, rol=%s, tema=%s]", 
                usuario.nombre, perfil.rol, config.tema);
        }
    }

    static class Punto {
        int x, y;
        Punto(int x, int y) { this.x = x; this.y = y; }
        
        @Override
        public String toString() {
            return "Punto(" + x + ", " + y + ")";
        }
    }
}

