package Problema_Viajante;
import java.util.*;

class EstrategiaVoraz {
    private Grafo grafo;

    // Variables de medición
    private long tiempoInicio, tiempoFin;
    private int contadorAsignaciones, contadorComparaciones;
    private long memoriaUsadaInicial, memoriaUsadaFinal;

    // Contador de memoria
    private int memoriaConsumida;

    public EstrategiaVoraz(Grafo grafo) {
        this.grafo = grafo;
    }

    /**
     * Inicia la medición de recursos.
     */
    private void iniciarMedicion() {
        contadorAsignaciones = 0;
        contadorComparaciones = 0;
        memoriaUsadaInicial = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        tiempoInicio = System.nanoTime();
        memoriaConsumida = 0;
    }

    /**
     * Finaliza la medición de recursos.
     */
    private void finalizarMedicion() {
        tiempoFin = System.nanoTime();
        memoriaUsadaFinal = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    /**
     * Calcula la memoria consumida en bits.
     */
    private void calcularMemoriaConsumida() {
        // Añade memoria para cada tipo de variable, considerando su tamaño en bits
        memoriaConsumida += 64; // tiempoInicio (long)
        memoriaConsumida += 64; // tiempoFin (long)
        memoriaConsumida += 32; // contadorAsignaciones (int)
        memoriaConsumida += 32; // contadorComparaciones (int)
        memoriaConsumida += 64; // memoriaUsadaInicial (long)
        memoriaConsumida += 64; // memoriaUsadaFinal (long)
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
        System.out.printf("Memoria consumida en bits (contando variables): %d bits\n", memoriaConsumida);
    }

    public List<String> encontrarRutaVoraz() {
        // Obtener ciudad inicial aleatoria desde el grafo
        iniciarMedicion();
        String ciudadInicial = grafo.getCiudades().get(0);

        // Añadir memoria de variables locales
        memoriaConsumida += 64; // ciudadInicial (String - referencia)

        Set<String> visitado = new HashSet<>();
        List<String> ruta = new ArrayList<>();
        String ciudadActual = ciudadInicial;
        int distanciaTotal = 0;

        memoriaConsumida += 64; // visitado (Set - referencia)
        memoriaConsumida += 64; // ruta (List - referencia)
        memoriaConsumida += 64; // ciudadActual (String - referencia)
        memoriaConsumida += 32; // distanciaTotal (int)

        visitado.add(ciudadInicial);
        ruta.add(ciudadInicial);
        contadorAsignaciones++; //asignacion de ciudadInicial

        while (visitado.size() < grafo.getCiudades().size()) {
            String ciudadMasCercana = null;
            int distanciaMinima = Integer.MAX_VALUE;

            memoriaConsumida += 64; // ciudadMasCercana (String - referencia)
            memoriaConsumida += 32; // distanciaMinima (int)

            for (Map.Entry<String, Integer> adyacente : grafo.getAdyacentes(ciudadActual)) {
                String ciudadDestino = adyacente.getKey();
                int distancia = adyacente.getValue();
                contadorComparaciones++; //comparacion de ciudadDestino

                memoriaConsumida += 64; // ciudadDestino (String - referencia)
                memoriaConsumida += 32; // distancia (int)

                if (!visitado.contains(ciudadDestino) && distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    ciudadMasCercana = ciudadDestino;
                    contadorAsignaciones++; //asignacion de ciudadMasCercana
                }
            }

            if (ciudadMasCercana == null) {
                System.out.println("No se puede completar la ruta.");
                return null;
            }

            ruta.add(ciudadMasCercana);
            visitado.add(ciudadMasCercana);
            distanciaTotal += distanciaMinima;

            ciudadActual = ciudadMasCercana;
            contadorAsignaciones++; //asignacion de ciudadActual
        }

        boolean puedeCerrarCiclo = false;
        for (Map.Entry<String, Integer> adyacente : grafo.getAdyacentes(ciudadActual)) {
            if (adyacente.getKey().equals(ciudadInicial)) {
                int distancia = adyacente.getValue();
                distanciaTotal += distancia;
                ruta.add(ciudadInicial);
                System.out.println("Cerrando ciclo: " + ciudadActual + " -> " + ciudadInicial + " (distancia: " + distancia + ")");
                puedeCerrarCiclo = true;
                break;
            }
        }

        if (!puedeCerrarCiclo) {
            System.out.println("No se puede cerrar el ciclo con la ciudad inicial.");
            return null;
        }

        System.out.println("\nRuta completa encontrada:");
        for (int i = 0; i < ruta.size() - 1; i++) {
            String origen = ruta.get(i);
            String destino = ruta.get(i + 1);
            int distancia = grafo.getDistancia(origen, destino);
        }
        System.out.println("Distancia total: " + distanciaTotal);

        finalizarMedicion();
        calcularMemoriaConsumida();
        imprimirResultadosMedicion();

        return ruta;
    }
}
