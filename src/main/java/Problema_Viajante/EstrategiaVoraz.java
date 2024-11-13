package Problema_Viajante;
import java.util.*;

/**
 * Estrategia voraz.
 */
class EstrategiaVoraz {
    private Grafo grafo;

    // Variables de medición
    private long tiempoInicio, tiempoFin;
    private int contadorAsignaciones, contadorComparaciones;
    private long memoriaUsadaInicial, memoriaUsadaFinal;

    // Contador de memoria
    private int memoriaConsumida;

    /**
     * Constructor.
     *
     * @param grafo the grafo
     */
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
     * Calcular la memoria consumida en bits.
     */
    private void calcularMemoriaConsumida() {
        memoriaConsumida += 64; // tiempoInicio (long)
        memoriaConsumida += 64; // tiempoFin (long)
        memoriaConsumida += 32; // contadorAsignaciones (int)
        memoriaConsumida += 32; // contadorComparaciones (int)
        memoriaConsumida += 64; // memoriaUsadaInicial (long)
        memoriaConsumida += 64; // memoriaUsadaFinal (long)
    }

    /**
     * Imprimir los resultados de medición de recursos.
     */
    public void imprimirResultadosMedicion() {
        double tiempoSegundos = (tiempoFin - tiempoInicio) / 1_000_000_000.0;
        double memoriaMB = (memoriaUsadaFinal - memoriaUsadaInicial) / (1024.0 * 1024.0);

        System.out.println("\n--- Medición de recursos ---");
        System.out.printf("Tiempo de ejecución (s): %.3f\n", tiempoSegundos);
        System.out.printf("Memoria utilizada (MB): %.3f\n", memoriaMB);
        System.out.println("Asignaciones totales: " + contadorAsignaciones);
        System.out.println("Comparaciones totales: " + contadorComparaciones);
        System.out.printf("Memoria consumida en bits (contando variables): %d bits\n", memoriaConsumida);
    }

    /**
     * Encontrar ruta voraz.
     *
     * @return la mejor ruta
     */
    public List<String> encontrarRutaVoraz() {
        iniciarMedicion();
        String ciudadInicial = grafo.getCiudades().get(0);

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

        System.out.println("\nIniciando recorrido desde: " + ciudadInicial);

        while (visitado.size() < grafo.getCiudades().size()) {
            String ciudadMasCercana = null;
            int distanciaMinima = Integer.MAX_VALUE;

            memoriaConsumida += 64; // ciudadMasCercana (String - referencia)
            memoriaConsumida += 32; // distanciaMinima (int)

            System.out.println("\nDesde " + ciudadActual + ", alternativas disponibles:");

            // Lista para almacenar todas las alternativas y ordenarlas
            List<Map.Entry<String, Integer>> alternativas = new ArrayList<>();

            for (Map.Entry<String, Integer> adyacente : grafo.getAdyacentes(ciudadActual)) {
                String ciudadDestino = adyacente.getKey();
                int distancia = adyacente.getValue();

                if (!visitado.contains(ciudadDestino)) {
                    alternativas.add(adyacente);
                }

                contadorComparaciones++;
                memoriaConsumida += 64; // ciudadDestino (String - referencia)
                memoriaConsumida += 32; // distancia (int)
            }

            // Ordenar alternativas por distancia
            alternativas.sort(Map.Entry.comparingByValue());

            // Mostrar todas las alternativas ordenadas
            for (Map.Entry<String, Integer> alt : alternativas) {
                String estado = visitado.contains(alt.getKey()) ? "(ya visitada)" : "";
                System.out.printf("  → %s: %d km %s\n", alt.getKey(), alt.getValue(), estado);

                if (!visitado.contains(alt.getKey()) && alt.getValue() < distanciaMinima) {
                    distanciaMinima = alt.getValue();
                    ciudadMasCercana = alt.getKey();
                    contadorAsignaciones++;
                }
            }

            if (ciudadMasCercana == null) {
                System.out.println("\nNo se puede completar la ruta - no hay ciudades disponibles.");
                return null;
            }

            System.out.println("Seleccionada: " + ciudadMasCercana + " (distancia: " + distanciaMinima + " km)");

            ruta.add(ciudadMasCercana);
            visitado.add(ciudadMasCercana);
            distanciaTotal += distanciaMinima;

            ciudadActual = ciudadMasCercana;
            contadorAsignaciones++;
        }

        System.out.println("\nBuscando retorno a " + ciudadInicial + ":");
        boolean puedeCerrarCiclo = false;

        for (Map.Entry<String, Integer> adyacente : grafo.getAdyacentes(ciudadActual)) {
            String ciudadDestino = adyacente.getKey();
            int distancia = adyacente.getValue();

            if (ciudadDestino.equals(ciudadInicial)) {
                System.out.printf("  → %s: %d km\n", ciudadDestino, distancia);
                distanciaTotal += distancia;
                ruta.add(ciudadInicial);
                System.out.println("Cerrando ciclo: " + ciudadActual + " → " + ciudadInicial + " (distancia: " + distancia + " km)");
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
            System.out.printf("%s → %s: %d km\n", origen, destino, distancia);
        }
        System.out.println("Distancia total recorrida: " + distanciaTotal + " km");

        finalizarMedicion();
        calcularMemoriaConsumida();
        imprimirResultadosMedicion();

        return ruta; //Devolución de la mejor ruta encontrada.
    }
}