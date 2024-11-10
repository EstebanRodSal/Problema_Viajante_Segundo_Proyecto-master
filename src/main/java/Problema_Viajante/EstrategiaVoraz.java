package Problema_Viajante;
import java.util.*;

class EstrategiaVoraz {
    private Grafo grafo;

    // Variables de medición
    private long tiempoInicio, tiempoFin;
    private int contadorAsignaciones, contadorComparaciones;
    private long memoriaUsadaInicial, memoriaUsadaFinal;



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


    public List<String> encontrarRutaVoraz() {
        // Obtener ciudad inicial aleatoria desde el grafo
        iniciarMedicion();
        String ciudadInicial = grafo.getCiudades().get(0);

        //System.out.println("Iniciando estrategia voraz desde la ciudad: "+ciudadInicial);
        Set<String> visitado = new HashSet<>();
        List<String> ruta = new ArrayList<>();
        String ciudadActual = ciudadInicial;
        int distanciaTotal = 0;

        visitado.add(ciudadInicial);
        ruta.add(ciudadInicial);
        contadorAsignaciones++; //asignacion de ciudadInicial

        while (visitado.size() < grafo.getCiudades().size()) {
            String ciudadMasCercana = null;
            int distanciaMinima = Integer.MAX_VALUE;

            //System.out.println("Opciones desde " + ciudadActual + ":");

            // Buscar la ciudad más cercana no visitada y mostrar las alternativas
            for (Map.Entry<String, Integer> adyacente : grafo.getAdyacentes(ciudadActual)) {
                String ciudadDestino = adyacente.getKey();
                int distancia = adyacente.getValue();
                contadorComparaciones++; //comparacion de ciudadDestino

                //System.out.println(" -> " + ciudadDestino + " (distancia: " + distancia + ")");

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

            // System.out.println("Seleccionado: " + ciudadActual + " -> " + ciudadMasCercana + " (distancia: " + distanciaMinima + ")");
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
            //System.out.println(" - " + origen + " -> " + destino + " (distancia: " + distancia + ")");
        }
        System.out.println("Distancia total: " + distanciaTotal);

        // Finalizar las mediciones de recursos
        finalizarMedicion();

        // Imprimir los resultados de las mediciones
        imprimirResultadosMedicion();

        return ruta;
    }
}
