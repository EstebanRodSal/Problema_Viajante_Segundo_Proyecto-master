package Problema_Viajante;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        //Codigos de colores ANSI para imprimir en la terminal
        final String RESET = "\u001B[0m";
        final String rojo = "\u001B[31m";
        final String verde = "\u001B[32m";
        final String amarillo = "\u001B[33m";
        final String azul = "\u001B[34m";

        int[] numCiudades = {10, 20, 40, 80, 100}; // Cantidad de ciudades para cada grafo (red)
        String archivoCiudades = "Recursos/Ciudades.txt"; // Nombre del archivo que contiene las ciudades

        for (int n : numCiudades) {
            System.out.println(rojo + "\nGrafo generado con " + n + " ciudades:");
            Grafo grafo = generarGrafoConCiudades(archivoCiudades, n);
            grafo.imprimirGrafo();

            if (grafo.esConexo()) {
                System.out.println("El grafo es conexo.");
            } else {
                System.out.println("El grafo no es conexo.");
            }

            System.out.print(RESET);
            // Inicializar y ejecutar Estrategia Voraz
            EstrategiaVoraz voraz = new EstrategiaVoraz(grafo);
            String ciudadInicial = grafo.getCiudades().get(0); // Selecciona la primera ciudad como inicial
            System.out.println("\nEjecutando Estrategia Voraz desde " + ciudadInicial + ":");
            List<String> rutaVoraz = voraz.encontrarRutaVoraz();

            if (rutaVoraz != null) {
                System.out.println(verde + "Ruta Voraz: " + rutaVoraz + RESET);
            } else {
                System.out.println(rojo + "No se pudo encontrar una ruta completa usando Estrategia Voraz." + RESET);
            }


            // Inicializar la estrategia genética con el grafo generado
            // Inicializar la estrategia genética con el grafo generado
            int tamanioPoblacion = obtenerTamanioPoblacion(n);
            EstrategiaGenetica genetico = new EstrategiaGenetica(grafo, n, tamanioPoblacion);

            // Ejecutar y medir 1 generación del algoritmo genético
            System.out.println("\n--- Resultados después de 1 generación ---");
            genetico.ejecutarCicloGeneraciones(1);
            genetico.imprimirMejorRuta();
            genetico.imprimirTopPoblaciones();

            // Ejecutar y medir 20 generaciones del algoritmo genético
            System.out.println("\n--- Resultados después de 20 generaciones ---");
            genetico.ejecutarCicloGeneraciones(20);
            genetico.imprimirMejorRuta();
            genetico.imprimirTopPoblaciones();

            // Ejecutar 20 generaciones adicionales (total 40)
            System.out.println("\n--- Resultados después de 40 generaciones ---");
            genetico.ejecutarCicloGeneraciones(20);
            System.out.print(azul);
            genetico.imprimirMejorRuta();
            System.out.print(RESET);
            genetico.imprimirTopPoblaciones();
        }
    }

    /**
     * Genera un grafo con una cantidad específica de ciudades a partir de un archivo de nombres.
     *
     * @param archivoCiudades archivo con los nombres de las ciudades
     * @param numCiudades     cantidad de ciudades (vértices) para el grafo (red)
     * @return grafo completo
     */


    public static Grafo generarGrafoConCiudades(String archivoCiudades, int numCiudades) {
        List<String> nombresCiudades = leerCiudadesDesdeArchivo(archivoCiudades);
        Collections.shuffle(nombresCiudades); // Barajar nombres para evitar repetición

        Grafo grafo = new Grafo();
        Set<String> ciudadesUsadas = new HashSet<>();

        for (int i = 0; i < numCiudades; i++) {
            String nombreCiudad = nombresCiudades.get(i);
            grafo.agregarCiudad(nombreCiudad);
            ciudadesUsadas.add(nombreCiudad);
        }

        Random random = new Random();
        List<String> listaCiudades = new ArrayList<>(ciudadesUsadas);

        for (int i = 0; i < numCiudades; i++) {
            for (int j = i + 1; j < numCiudades; j++) {
                int distancia = random.nextInt(50) + 1; // Genera una distancia aleatoria entre 1 y 50
                grafo.agregarArco(listaCiudades.get(i), listaCiudades.get(j), distancia);
            }
        }

        return grafo;
    }

    /**
     * Lee los nombres de las ciudades desde un archivo.
     *
     * @param archivo archivo con los nombres de las ciudades
     * @return lista de nombres de ciudades
     */
    public static List<String> leerCiudadesDesdeArchivo(String archivo) {
        List<String> ciudades = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                ciudades.add(linea.trim());
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return ciudades;
    }

    /**
     * Obtiene el tamaño de la población inicial según la cantidad de ciudades.
     *
     * @param numCiudades cantidad de ciudades en el grafo
     * @return tamaño de la población inicial
     */
    public static int obtenerTamanioPoblacion(int numCiudades) {
        switch (numCiudades) {
            case 10:
                return 5;  // Tamaño de población para grafo de 10 ciudades
            case 20:
                return 10; // Tamaño de población para grafo de 20 ciudades
            case 40:
                return 20; // Tamaño de población para grafo de 40 ciudades
            case 80:
                return 40; // Tamaño de población para grafo de 80 ciudades
            case 100:
                return 50; // Tamaño de población para grafo de 100 ciudades
            default:
                throw new IllegalArgumentException("Número de ciudades no soportado: " + numCiudades);
        }
    }
}