package Problema_Viajante;

import java.util.*;

public class EstrategiaGenetica {
    private Grafo grafo;
    private List<List<String>> poblacion;
    private int tamanioPoblacion;
    private int numCiudades;

    // Variables de medición
    private long tiempoInicio, tiempoFin;  // 2 * 64 bits = 128 bits
    private int contadorAsignaciones;      // 32 bits
    private int contadorComparaciones;     // 32 bits
    private long memoriaConsumidaBits;     // 64 bits

    public EstrategiaGenetica(Grafo grafo, int numCiudades, int tamanioPoblacion) {
        this.grafo = grafo;                // referencia = 64 bits
        this.numCiudades = numCiudades;    // 32 bits
        this.tamanioPoblacion = tamanioPoblacion; // 32 bits
        inicializarContadoresMemoria();
        this.poblacion = generarPoblacionInicial(); // referencia = 64 bits
    }

    private void inicializarContadoresMemoria() {
        memoriaConsumidaBits = 0;
        // Contar variables de instancia
        memoriaConsumidaBits += 128; // tiempoInicio, tiempoFin (2 * 64 bits)
        memoriaConsumidaBits += 32;  // contadorAsignaciones
        memoriaConsumidaBits += 32;  // contadorComparaciones
        memoriaConsumidaBits += 64;  // memoriaConsumidaBits
        memoriaConsumidaBits += 64;  // grafo (referencia)
        memoriaConsumidaBits += 64;  // poblacion (referencia)
        memoriaConsumidaBits += 32;  // tamanioPoblacion
        memoriaConsumidaBits += 32;  // numCiudades
    }

    private void contarMemoriaLista(List<?> lista) {
        memoriaConsumidaBits += 128; // Overhead de la lista (referencia + size + capacity)

        for (Object elemento : lista) {
            if (elemento instanceof String) {
                String str = (String) elemento;
                memoriaConsumidaBits += 160; // Overhead del String
                memoriaConsumidaBits += str.length() * 16; // Caracteres
            } else if (elemento instanceof List) {
                contarMemoriaLista((List<?>) elemento);
            }
        }
    }

    private List<List<String>> generarPoblacionInicial() {
        Set<List<String>> poblacionSet = new HashSet<>();
        List<String> ciudades = new ArrayList<>(grafo.getCiudades());
        memoriaConsumidaBits += 128; // Referencias a poblacionSet y ciudades

        String ciudadInicial = grafo.getCiudades().get(0);
        memoriaConsumidaBits += 64; // Referencia a ciudadInicial

        while (poblacionSet.size() < tamanioPoblacion) {
            Collections.shuffle(ciudades);
            List<String> cromosoma = new ArrayList<>();
            memoriaConsumidaBits += 64; // Referencia a cromosoma

            cromosoma.add(ciudadInicial);
            for (String ciudad : ciudades) {
                if (!ciudad.equals(ciudadInicial)) {
                    cromosoma.add(ciudad);
                    contadorComparaciones++;
                }
            }
            cromosoma.add(ciudadInicial);
            poblacionSet.add(cromosoma);
            contadorAsignaciones++;
            contarMemoriaLista(cromosoma);
        }

        List<List<String>> resultado = new ArrayList<>(poblacionSet);
        contarMemoriaLista(resultado);
        return resultado;
    }

    private int calcularFitness(List<String> cromosoma) {
        int distanciaTotal = 0;
        memoriaConsumidaBits += 32; // Variable distanciaTotal

        for (int i = 0; i < cromosoma.size() - 1; i++) {
            String ciudadOrigen = cromosoma.get(i);
            String ciudadDestino = cromosoma.get(i + 1);
            memoriaConsumidaBits += 128; // Referencias a ciudadOrigen y ciudadDestino

            distanciaTotal += grafo.getDistancia(ciudadOrigen, ciudadDestino);
            contadorComparaciones++;
            contadorAsignaciones++;
        }
        return distanciaTotal;
    }

    private List<String> cruzar(List<String> padre1, List<String> padre2) {
        int numGenes = padre1.size() - 1;
        Random random = new Random();
        memoriaConsumidaBits += 96; // numGenes (32) + random reference (64)

        int puntoInicio = random.nextInt(numGenes);
        int puntoFin = random.nextInt(numGenes - puntoInicio) + puntoInicio;
        memoriaConsumidaBits += 64; // puntoInicio y puntoFin

        List<String> hijo = new ArrayList<>(Collections.nCopies(numGenes, null));
        memoriaConsumidaBits += 64; // Referencia a hijo
        contarMemoriaLista(hijo);

        for (int i = puntoInicio; i <= puntoFin; i++) {
            hijo.set(i, padre1.get(i));
            contadorAsignaciones++;
        }

        int idx = 0;
        memoriaConsumidaBits += 32; // Variable idx

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
        return hijo;
    }

    private void mutacionAleatoria(List<String> cromosoma) {
        Random random = new Random();
        memoriaConsumidaBits += 64; // Referencia a random

        int idx1, idx2;
        memoriaConsumidaBits += 64; // Variables idx1 e idx2

        do {
            idx1 = random.nextInt(numCiudades - 1) + 1;
            idx2 = random.nextInt(numCiudades - 1) + 1;
            contadorComparaciones += 2;
        } while (idx1 == idx2);

        int fitnessOriginal = calcularFitness(cromosoma);
        memoriaConsumidaBits += 32; // Variable fitnessOriginal

        contadorComparaciones++;
        Collections.swap(cromosoma, idx1, idx2);
        contadorAsignaciones += 2;

        int fitnessNuevo = calcularFitness(cromosoma);
        memoriaConsumidaBits += 32; // Variable fitnessNuevo
        contadorComparaciones++;

        if (fitnessNuevo >= fitnessOriginal) {
            Collections.swap(cromosoma, idx1, idx2);
            contadorAsignaciones += 2;
        }
    }

    private void mutacionDirigida(List<String> cromosoma) {
        int maxDistancia = -1;
        int idx1 = 0;
        int idx2 = 0;
        memoriaConsumidaBits += 96; // Variables maxDistancia, idx1, idx2

        for (int i = 0; i < cromosoma.size() - 1; i++) {
            int distancia = grafo.getDistancia(cromosoma.get(i), cromosoma.get(i + 1));
            memoriaConsumidaBits += 32; // Variable distancia

            contadorComparaciones++;
            if (distancia > maxDistancia) {
                maxDistancia = distancia;
                idx1 = i;
                idx2 = i + 1;
                contadorAsignaciones += 2;
            }
        }

        int fitnessOriginal = calcularFitness(cromosoma);
        memoriaConsumidaBits += 32; // Variable fitnessOriginal

        contadorComparaciones++;
        Collections.swap(cromosoma, idx1, idx2);
        contadorAsignaciones += 2;

        int fitnessNuevo = calcularFitness(cromosoma);
        memoriaConsumidaBits += 32; // Variable fitnessNuevo

        contadorComparaciones++;
        if (fitnessNuevo >= fitnessOriginal) {
            Collections.swap(cromosoma, idx1, idx2);
            contadorAsignaciones += 2;
        }
    }

    private void mutar(List<String> cromosoma) {
        int puntuacionAntes = calcularFitness(cromosoma);
        memoriaConsumidaBits += 32; // Variable puntuacionAntes

        mutacionAleatoria(cromosoma);
        mutacionDirigida(cromosoma);

        int puntuacionDespues = calcularFitness(cromosoma);
        memoriaConsumidaBits += 32; // Variable puntuacionDespues
    }

    public void ejecutarGeneracion() {
        List<List<String>> nuevaGeneracion = new ArrayList<>();
        Random random = new Random();
        memoriaConsumidaBits += 128; // Referencias a nuevaGeneracion y random

        while (nuevaGeneracion.size() < tamanioPoblacion) {
            List<String> padre1 = poblacion.get(random.nextInt(poblacion.size()));
            List<String> padre2 = poblacion.get(random.nextInt(poblacion.size()));
            memoriaConsumidaBits += 128; // Referencias a padre1 y padre2

            List<String> hijo1 = cruzar(padre1, padre2);
            List<String> hijo2 = cruzar(padre2, padre1);
            memoriaConsumidaBits += 128; // Referencias a hijo1 y hijo2

            if (random.nextDouble() < 0.1) {
                mutar(hijo1);
                mutar(hijo2);
            }
            nuevaGeneracion.add(hijo1);
            nuevaGeneracion.add(hijo2);
            contarMemoriaLista(hijo1);
            contarMemoriaLista(hijo2);
        }

        nuevaGeneracion.addAll(poblacion);
        nuevaGeneracion.sort(Comparator.comparingInt(this::calcularFitness));
        poblacion = nuevaGeneracion.subList(0, tamanioPoblacion);
        contarMemoriaLista(poblacion);
    }

    public void imprimirTopPoblaciones() {
        poblacion.sort(Comparator.comparingInt(this::calcularFitness));
        System.out.println("Top 5 mejores poblaciones:");
        for (int i = 0; i < 5 && i < poblacion.size(); i++) {
            List<String> mejorRuta = poblacion.get(i);
            int mejorDistancia = calcularFitness(mejorRuta);
            memoriaConsumidaBits += 96; // Referencias a mejorRuta y variable mejorDistancia
            System.out.printf("Cromosoma #%d: %s, Puntuación: %d\n", i + 1, mejorRuta, mejorDistancia);
        }
    }

    public void imprimirMejorRuta() {
        poblacion.sort(Comparator.comparingInt(this::calcularFitness));
        List<String> mejorRuta = poblacion.get(0);
        int mejorDistancia = calcularFitness(mejorRuta);
        memoriaConsumidaBits += 96; // Referencias a mejorRuta y variable mejorDistancia

        System.out.println("Mejor ruta encontrada:");
        for (int i = 0; i < mejorRuta.size() - 1; i++) {
            String origen = mejorRuta.get(i);
            String destino = mejorRuta.get(i + 1);
            memoriaConsumidaBits += 128; // Referencias a origen y destino
            int distancia = grafo.getDistancia(origen, destino);
            memoriaConsumidaBits += 32; // Variable distancia
        }
    }

    public void ejecutarCicloGeneraciones(int numGeneraciones) {
        memoriaConsumidaBits += 32; // Variable numGeneraciones
        iniciarMedicion();
        for (int i = 0; i < numGeneraciones; i++) {
            ejecutarGeneracion();
        }
        finalizarMedicion();
        imprimirResultadosMedicion();
    }

    private void iniciarMedicion() {
        contadorAsignaciones = 0;
        contadorComparaciones = 0;
        tiempoInicio = System.nanoTime();
    }

    private void finalizarMedicion() {
        tiempoFin = System.nanoTime();
    }

    public void imprimirResultadosMedicion() {
        double tiempoSegundos = (tiempoFin - tiempoInicio) / 1_000_000_000.0;
        memoriaConsumidaBits += 64; // Variable tiempoSegundos

        System.out.println("\n--- Medición de recursos ---");
        System.out.printf("Tiempo de ejecución (s): %.3f\n", tiempoSegundos);
        System.out.printf("Memoria utilizada (bits): %d\n", memoriaConsumidaBits);
        //System.out.printf("Memoria utilizada (bytes): %.2f\n", memoriaConsumidaBits / 8.0);
        //System.out.printf("Memoria utilizada (KB): %.2f\n", memoriaConsumidaBits / (8.0 * 1024));
        //System.out.printf("Memoria utilizada (MB): %.2f\n", memoriaConsumidaBits / (8.0 * 1024 * 1024));
        System.out.println("Asignaciones totales: " + contadorAsignaciones);
        System.out.println("Comparaciones totales: " + contadorComparaciones);
    }
}