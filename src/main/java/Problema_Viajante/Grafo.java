package Problema_Viajante;

import java.util.*;

public class Grafo {
    private Map<String, List<Map.Entry<String, Integer>>> adjList;
    private String ciudadInicial; // Añadir atributo para la ciudad inicial

    // Constructor
    public Grafo() {
        adjList = new HashMap<>();
    }

    //agregrado para corregir error con estrategiq voraz
    // Método público para acceder a las ciudades adyacentes de una ciudad dada
    public List<Map.Entry<String, Integer>> getAdyacentes(String ciudad) {
        return adjList.getOrDefault(ciudad, new LinkedList<>());
    }


    /**
     * Agregar ciudad.
     * Se encarga de agregar al grafo creando un nuevo vértice (ciudad).
     *
     * @param ciudad nombre de la ciudad
     */
    public void agregarCiudad(String ciudad) {
        adjList.putIfAbsent(ciudad, new LinkedList<>());
    }

    /**
     * Agregar arco.
     * Agrega una conexión entre dos vértices (ciudad y ciudad) con una distancia específica.
     *
     * @param origen    ciudad de origen
     * @param destino   ciudad de destino
     * @param distancia distancia entre origen y destino
     */
    public void agregarArco(String origen, String destino, int distancia) {
        adjList.putIfAbsent(origen, new LinkedList<>());
        adjList.putIfAbsent(destino, new LinkedList<>());
        adjList.get(origen).add(new AbstractMap.SimpleEntry<>(destino, distancia));
        adjList.get(destino).add(new AbstractMap.SimpleEntry<>(origen, distancia)); // Como es un grafo no dirigido
    }

    /**
     * Devuelve una lista de nombres de todas las ciudades (vértices) en el grafo.
     *
     * @return lista de nombres de ciudades
     */
    public List<String> getCiudades() {
        return new ArrayList<>(adjList.keySet());
    }

    /**
     * Obtiene la distancia entre dos ciudades si existe un arco entre ellas.
     *
     * @param origen  ciudad de origen
     * @param destino ciudad de destino
     * @return distancia entre las ciudades o Integer.MAX_VALUE si no hay conexión directa
     */
    public int getDistancia(String origen, String destino) {
        List<Map.Entry<String, Integer>> adyacentes = adjList.get(origen);
        if (adyacentes != null) {
            for (Map.Entry<String, Integer> arco : adyacentes) {
                if (arco.getKey().equals(destino)) {
                    return arco.getValue();
                }
            }
        }
        return Integer.MAX_VALUE; // Retorna un valor alto si no hay conexión directa
    }

    /**
     * Es conexo boolean.
     * Determina si el grafo es conexo.
     *
     * @return true si el grafo es conexo, false en caso contrario
     */
    public boolean esConexo() {
        if (adjList.isEmpty()) return true;

        Set<String> visitado = new HashSet<>();
        String ciudadInicial = adjList.keySet().iterator().next();
        dfs(ciudadInicial, visitado);

        return visitado.size() == adjList.size();
    }

    /**
     * dfs.
     * Método de búsqueda en profundidad para encontrar nodos adyacentes.
     *
     * @param ciudad   ciudad de origen
     * @param visitado ciudades ya visitadas
     */
    private void dfs(String ciudad, Set<String> visitado) {
        visitado.add(ciudad);
        for (Map.Entry<String, Integer> adyacente : adjList.get(ciudad)) {
            if (!visitado.contains(adyacente.getKey())) {
                dfs(adyacente.getKey(), visitado);
            }
        }
    }

    public void seleccionarCiudadInicialAleatoria() {
        List<String> ciudades = new ArrayList<>(adjList.keySet());
        Random random = new Random();
        this.ciudadInicial = ciudades.get(random.nextInt(ciudades.size())); // Selecciona una ciudad aleatoria
    }

    // Método para obtener la ciudad inicial
    //public String getCiudadInicial() {
    //    return ciudadInicial;
    // }


    public void setCiudadInicial(String ciudadInicial) {
        this.ciudadInicial = ciudadInicial;
    }

    /**
     * Imprimir grafo.
     * Imprime el grafo.
     */
    public void imprimirGrafo() {
        for (String ciudad : adjList.keySet()) {
            System.out.print("Ciudad " + ciudad + ":");
            for (Map.Entry<String, Integer> adyacente : adjList.get(ciudad)) {
                System.out.print(" -> " + adyacente.getKey() + " (distancia: " + adyacente.getValue() + ")");
            }
            System.out.println();
        }
    }
}