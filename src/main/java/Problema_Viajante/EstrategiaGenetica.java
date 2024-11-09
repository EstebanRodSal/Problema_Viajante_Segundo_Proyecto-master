package Problema_Viajante;

import java.util.*;

public class EstrategiaGenetica {
    private Grafo grafo;
    private List<List<String>> poblacion;
    private int tamanioPoblacion;
    private int numCiudades;

    // Variables de medición
    private long tiempoInicio, tiempoFin;
    private int contadorAsignaciones, contadorComparaciones;
    private long memoriaUsadaInicial, memoriaUsadaFinal;

    public EstrategiaGenetica(Grafo grafo, int numCiudades, int tamanioPoblacion) {
        this.grafo = grafo;
        this.numCiudades = numCiudades;
        this.tamanioPoblacion = tamanioPoblacion;
        this.poblacion = generarPoblacionInicial();
    }

    /**
     * Genera la población inicial sin rutas duplicadas.
     */
    private List<List<String>> generarPoblacionInicial() {
        Set<List<String>> poblacionSet = new HashSet<>();
        List<String> ciudades = new ArrayList<>(grafo.getCiudades());

        while (poblacionSet.size() < tamanioPoblacion) {
            Collections.shuffle(ciudades); // Baraja para crear un cromosoma aleatorio
            List<String> cromosoma = new ArrayList<>(ciudades);
            cromosoma.add(ciudades.get(0)); // Regresar a la ciudad de inicio
            poblacionSet.add(new ArrayList<>(cromosoma));
            contadorAsignaciones++;
        }

        return new ArrayList<>(poblacionSet);
    }

    /**
     * Función de aptitud: calcula la distancia total del recorrido.
     */
    private int calcularFitness(List<String> cromosoma) {
        int distanciaTotal = 0;
        for (int i = 0; i < cromosoma.size() - 1; i++) {
            String ciudadOrigen = cromosoma.get(i);
            String ciudadDestino = cromosoma.get(i + 1);
            distanciaTotal += grafo.getDistancia(ciudadOrigen, ciudadDestino);
            contadorComparaciones++;
            contadorAsignaciones++;
        }
        return distanciaTotal;
    }

    /**
     * Realiza el cruce entre dos padres utilizando Order Crossover para generar hijos sin duplicados e imprime el resultado.
     */
    private List<String> cruzar(List<String> padre1, List<String> padre2) {
        int numGenes = padre1.size() - 1;
        Random random = new Random();

        int puntoInicio = random.nextInt(numGenes);
        int puntoFin = random.nextInt(numGenes - puntoInicio) + puntoInicio;

        List<String> hijo = new ArrayList<>(Collections.nCopies(numGenes, null));
        for (int i = puntoInicio; i <= puntoFin; i++) {
            hijo.set(i, padre1.get(i));
            contadorAsignaciones++;
        }

        int idx = 0;
        for (String gen : padre2) {
            if (!hijo.contains(gen)) {
                while (hijo.get(idx) != null) {
                    idx++;
                }
                hijo.set(idx, gen);
                contadorAsignaciones++;
            }
            contadorComparaciones++;
        }

        hijo.add(hijo.get(0));

        // Imprimir padres e hijos con sus puntuaciones
        System.out.printf("Padre1: %s, puntuación: %d\n", padre1, calcularFitness(padre1));
        System.out.printf("Padre2: %s, puntuación: %d\n", padre2, calcularFitness(padre2));
        System.out.printf("Hijo: %s, puntuación: %d\n", hijo, calcularFitness(hijo));

        return hijo;
    }

    /**
     * Aplica una mutación aleatoria y dirigida e imprime los resultados.
     */
    private void mutar(List<String> cromosoma) {
        int puntuacionAntes = calcularFitness(cromosoma);
        System.out.printf("Individuo antes de mutación: %s, puntuación: %d\n", cromosoma, puntuacionAntes);

        mutacionAleatoria(cromosoma);
        mutacionDirigida(cromosoma);

        int puntuacionDespues = calcularFitness(cromosoma);
        System.out.printf("Individuo después de mutación: %s, puntuación: %d\n", cromosoma, puntuacionDespues);
    }

    /**
     * Mutación aleatoria: intercambia dos posiciones al azar en el cromosoma.
     */
    private void mutacionAleatoria(List<String> cromosoma) {
        Random random = new Random();
        int idx1 = random.nextInt(numCiudades - 1) + 1; // No incluir la ciudad inicial
        int idx2 = random.nextInt(numCiudades - 1) + 1;

        int fitnessOriginal = calcularFitness(cromosoma);
        contadorComparaciones++; // Comparación al evaluar si la mutación mejora el fitness
        Collections.swap(cromosoma, idx1, idx2); // Intercambio (mutación aleatoria)
        contadorAsignaciones += 2; // Dos asignaciones por el swap

        int fitnessNuevo = calcularFitness(cromosoma);
        contadorComparaciones++; // Comparación al evaluar si revertimos el cambio

        // Si no mejora, revertimos el cambio
        if (fitnessNuevo >= fitnessOriginal) {
            Collections.swap(cromosoma, idx1, idx2);
            contadorAsignaciones += 2; // Dos asignaciones por el swap de reversión
        }
    }

    /**
     * Mutación dirigida: intercambia el par de ciudades con la mayor distancia.
     */
    private void mutacionDirigida(List<String> cromosoma) {
        int maxDistancia = -1;
        int idx1 = 0;
        int idx2 = 0;

        // Buscar el par de ciudades con la mayor distancia
        for (int i = 0; i < cromosoma.size() - 1; i++) {
            int distancia = grafo.getDistancia(cromosoma.get(i), cromosoma.get(i + 1));
            contadorComparaciones++; // Comparación para ver si la distancia es la mayor encontrada
            if (distancia > maxDistancia) {
                maxDistancia = distancia;
                idx1 = i;
                idx2 = i + 1;
                contadorAsignaciones += 2; // Asignaciones para idx1 e idx2
            }
        }

        // Intercambiar las ciudades seleccionadas
        int fitnessOriginal = calcularFitness(cromosoma);
        contadorComparaciones++; // Comparación al evaluar si la mutación mejora el fitness
        Collections.swap(cromosoma, idx1, idx2);
        contadorAsignaciones += 2; // Dos asignaciones por el swap

        int fitnessNuevo = calcularFitness(cromosoma);
        contadorComparaciones++; // Comparación para decidir si revertimos el cambio

        // Si no mejora, revertimos el cambio
        if (fitnessNuevo >= fitnessOriginal) {
            Collections.swap(cromosoma, idx1, idx2);
            contadorAsignaciones += 2; // Dos asignaciones por el swap de reversión
        }
    }


    /**
     * Ejecuta una generación completa con cruce, mutación y selección de los mejores.
     */
    public void ejecutarGeneracion() {
        List<List<String>> nuevaGeneracion = new ArrayList<>();
        Random random = new Random();

        while (nuevaGeneracion.size() < tamanioPoblacion) {
            List<String> padre1 = poblacion.get(random.nextInt(poblacion.size()));
            List<String> padre2 = poblacion.get(random.nextInt(poblacion.size()));
            List<String> hijo1 = cruzar(padre1, padre2);
            List<String> hijo2 = cruzar(padre2, padre1);

            if (random.nextDouble() < 0.1) {
                mutar(hijo1);
                mutar(hijo2);
            }
            nuevaGeneracion.add(hijo1);
            nuevaGeneracion.add(hijo2);
        }

        nuevaGeneracion.addAll(poblacion);
        nuevaGeneracion.sort(Comparator.comparingInt(this::calcularFitness));

        poblacion = nuevaGeneracion.subList(0, tamanioPoblacion);
    }

    /**
     * Imprime las 5 mejores poblaciones con sus puntuaciones.
     */
    public void imprimirTopPoblaciones() {
        poblacion.sort(Comparator.comparingInt(this::calcularFitness));
        System.out.println("Top 5 mejores poblaciones:");
        for (int i = 0; i < 5 && i < poblacion.size(); i++) {
            List<String> mejorRuta = poblacion.get(i);
            int mejorDistancia = calcularFitness(mejorRuta);
            System.out.printf("Cromosoma #%d: %s, Puntuación: %d\n", i + 1, mejorRuta, mejorDistancia);
        }
    }

    /**
     * Imprime la mejor ruta con detalle de arcos.
     */
    public void imprimirMejorRuta() {
        poblacion.sort(Comparator.comparingInt(this::calcularFitness));
        List<String> mejorRuta = poblacion.get(0);
        int mejorDistancia = calcularFitness(mejorRuta);

        System.out.println("Mejor ruta encontrada:");
        for (int i = 0; i < mejorRuta.size() - 1; i++) {
            String origen = mejorRuta.get(i);
            String destino = mejorRuta.get(i + 1);
            int distancia = grafo.getDistancia(origen, destino);
            System.out.println("Arco: " + origen + " -> " + destino + " | Distancia: " + distancia);
        }
        System.out.println("Distancia total: " + mejorDistancia);
    }

    /**
     * Ejecuta múltiples ciclos de generaciones y mide recursos.
     */
    public void ejecutarCicloGeneraciones(int numGeneraciones) {
        iniciarMedicion(); // Comenzar a medir antes de ejecutar generaciones
        for (int i = 0; i < numGeneraciones; i++) {
            ejecutarGeneracion();
        }
        finalizarMedicion(); // Terminar de medir después de ejecutar generaciones
        imprimirResultadosMedicion();
    }

    /**
     * Inicia la medición de recursos.
     */
    private void iniciarMedicion() {
        contadorAsignaciones = 0;
        contadorComparaciones = 0;
        memoriaUsadaInicial = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        tiempoInicio = System.nanoTime();
    }

    /**
     * Finaliza la medición de recursos.
     */
    private void finalizarMedicion() {
        tiempoFin = System.nanoTime();
        memoriaUsadaFinal = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    /**
     * Imprime los resultados de medición de recursos.
     */
    public void imprimirResultadosMedicion() {
        double tiempoSegundos = (tiempoFin - tiempoInicio) / 1_000_000_000.0; // Convertir nanosegundos a segundos
        double memoriaMB = (memoriaUsadaFinal - memoriaUsadaInicial) / (1024.0 * 1024.0); // Convertir bytes a MB

        System.out.println("\n--- Medición de recursos ---");
        System.out.printf("Tiempo de ejecución (s): %.3f\n", tiempoSegundos);
        System.out.printf("Memoria utilizada (MB): %.3f\n", memoriaMB);
        System.out.println("Asignaciones totales: " + contadorAsignaciones);
        System.out.println("Comparaciones totales: " + contadorComparaciones);
    }
}