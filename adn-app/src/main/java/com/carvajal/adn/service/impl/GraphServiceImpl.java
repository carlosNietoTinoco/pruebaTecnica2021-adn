package com.carvajal.adn.service.impl;

import com.carvajal.adn.model.AdnDTO;
import com.carvajal.adn.service.GraphService;
import com.carvajal.adn.util.estructuras.NodeStructure;
import lombok.RequiredArgsConstructor;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GraphServiceImpl implements GraphService {

    private final static int tamanoParaMalFormacion = 4;

    @Override
    public boolean contieneMalformacionesGeneticas(AdnDTO adnDTO) {

        int cantidadDeMalFormaciones = 0;
        int maximoDeMalFormaciones = 2;
        Graph<NodeStructure, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        crearGrafo(graph, adnDTO);

        List<Set<NodeStructure>> listaDeNodosAgrupadosPorSubGrafos =
                new ConnectivityInspector<>(graph).connectedSets();

        for (int i = 0; i<listaDeNodosAgrupadosPorSubGrafos.size() &&
                cantidadDeMalFormaciones < maximoDeMalFormaciones; i++) {
            if(listaDeNodosAgrupadosPorSubGrafos.get(i).size() >= tamanoParaMalFormacion) {
                cantidadDeMalFormaciones+=cantidadDeMalFormacionesEnSubGrafo(
                        new AsSubgraph<NodeStructure, DefaultEdge>(
                                graph, listaDeNodosAgrupadosPorSubGrafos.get(i)));
            }
        }
        return cantidadDeMalFormaciones >= maximoDeMalFormaciones;
    }

    private int cantidadDeMalFormacionesEnSubGrafo(Graph<NodeStructure, DefaultEdge> graph){

        int cantidadDeMalFormaciones = 0;
        int profundidad = tamanoParaMalFormacion-1;

        while (graph.vertexSet().size()>=tamanoParaMalFormacion){
            NodeStructure nodoRaiz = nodoConMenorCantidadDeAristas(graph);
            Set<NodeStructure> nodosResultado;
            //Se usa una busqueda por profundidad limitada iterativa
            nodosResultado = contruirListaDirigidaDeBasesNitrogenadas(graph, nodoRaiz, profundidad);
            if (nodosResultado.size() == tamanoParaMalFormacion)
                if (comprobarSolucion(graph, nodosResultado)) {
                    graph = borrarNodos(graph, nodosResultado);
                    cantidadDeMalFormaciones++;
                } else
                    graph = borrarNodos(graph, new HashSet<>(Arrays.asList(nodoRaiz)));
            else
                graph = borrarNodos(graph, new HashSet<>(Arrays.asList(nodoRaiz)));
        }
        return cantidadDeMalFormaciones;
    }

    private Set<NodeStructure> contruirListaDirigidaDeBasesNitrogenadas(Graph<NodeStructure, DefaultEdge> graph, NodeStructure nodoRaiz, int  profundidad){

        Set<NodeStructure> nodosAdyacentes = new HashSet<NodeStructure>(Graphs.neighborListOf(graph, nodoRaiz));
        profundidad--;
        for (NodeStructure nodoAdyacente:nodosAdyacentes){
            int direccion =  determinarDireccion(nodoRaiz, nodoAdyacente);
            Set<NodeStructure> posibleSolucion = exploracionDeNodosAdyacentes(
                    graph, nodoAdyacente, nodoRaiz, direccion, profundidad);
            if(posibleSolucion.size() == tamanoParaMalFormacion-1){
                posibleSolucion.add(nodoRaiz);
                if(comprobarSolucion(graph, posibleSolucion))
                    return posibleSolucion;
            }
        }
        return new HashSet<NodeStructure>();
    }

    private Set<NodeStructure> exploracionDeNodosAdyacentes(
            Graph<NodeStructure, DefaultEdge> graph, NodeStructure nodoRaiz, NodeStructure nodoPadre, int direccion, int profundidad) {
        profundidad--;
        Set<NodeStructure> posibleSolucion = new HashSet<NodeStructure>();
        posibleSolucion.add(nodoRaiz);
        Set<NodeStructure> nodosAdyacentes = new HashSet<NodeStructure>(Graphs.neighborListOf(graph, nodoRaiz));
        nodosAdyacentes.remove(nodoPadre);
        for(NodeStructure nodoAdyacente:nodosAdyacentes){
            int direccionNodoAdyacente = determinarDireccion(nodoRaiz, nodoAdyacente);
            if (direccion == direccionNodoAdyacente){
                if (profundidad == 0){
                    posibleSolucion.add(nodoAdyacente);
                    return posibleSolucion;
                }
                posibleSolucion.addAll(exploracionDeNodosAdyacentes(graph,nodoAdyacente,nodoRaiz,direccion,profundidad));
            }
        }
        return posibleSolucion;
    }

    private boolean comprobarSolucion(Graph<NodeStructure, DefaultEdge> graph, Set<NodeStructure> nodos){

        if (nodos.size() != tamanoParaMalFormacion)
            return false;
        Graph<NodeStructure, DefaultEdge> grafoSoloConLaPosibleSolucion = new SimpleGraph<>(DefaultEdge.class);
        grafoSoloConLaPosibleSolucion = new AsSubgraph<NodeStructure, DefaultEdge>(graph, nodos);

        NodeStructure nodoInicial = nodoConMenorCantidadDeAristas(grafoSoloConLaPosibleSolucion);
        List<NodeStructure> listaNodos = new ArrayList<NodeStructure>();
        listaNodos.add(nodoInicial);

        for(int i = 1; i<tamanoParaMalFormacion; i++){
            nodoInicial = new ArrayList<>(new HashSet<NodeStructure>(
                    Graphs.neighborListOf(grafoSoloConLaPosibleSolucion, nodoInicial))).get(0);
            listaNodos.add(nodoInicial);
            grafoSoloConLaPosibleSolucion.removeVertex(listaNodos.get(i-1));
        }
        int direccion = determinarDireccion(listaNodos.get(0),listaNodos.get(1));
        for(int i = 1; i<nodos.size()-1; i++){
            if (direccion != determinarDireccion(listaNodos.get(i),listaNodos.get(i+1)))
                return false;
        }
        return true;
    }

    private Graph<NodeStructure, DefaultEdge> borrarNodos (Graph<NodeStructure, DefaultEdge> graph, Set<NodeStructure> nodosParaBorrar){

        Set<DefaultEdge> aristasAEliminar = new HashSet<DefaultEdge>();

        for(NodeStructure nodoAlQueSeLeEliminaraLasAristas:nodosParaBorrar){
            aristasAEliminar.addAll(graph.edgesOf(nodoAlQueSeLeEliminaraLasAristas));
        }
        for(DefaultEdge aristaAEliminar:aristasAEliminar){
            graph.removeEdge(aristaAEliminar);
        }
        Graph<NodeStructure, DefaultEdge> nuevoGrafo = new SimpleGraph<>(DefaultEdge.class);

        List<Set<NodeStructure>> listaDeNodosAgrupadosPorSubGrafos =
                new ConnectivityInspector<>(graph).connectedSets();
        for (int i = 0; i<listaDeNodosAgrupadosPorSubGrafos.size(); i++) {
            if(listaDeNodosAgrupadosPorSubGrafos.get(i).size() >= tamanoParaMalFormacion)
                Graphs.addGraph(nuevoGrafo, new AsSubgraph<NodeStructure, DefaultEdge>(graph, listaDeNodosAgrupadosPorSubGrafos.get(i)));
        }
        return nuevoGrafo;
    }

    private int determinarDireccion(NodeStructure nodoRaiz, NodeStructure nodoHijo) {
        if (nodoRaiz.fila-1 == nodoHijo.fila && nodoRaiz.columna-1 == nodoHijo.columna)
            return 1;
        else if (nodoRaiz.fila-1 == nodoHijo.fila && nodoRaiz.columna == nodoHijo.columna)
            return 2;
        else if (nodoRaiz.fila-1 == nodoHijo.fila && nodoRaiz.columna+1 == nodoHijo.columna)
            return 3;
        else if (nodoRaiz.fila == nodoHijo.fila && nodoRaiz.columna+1 == nodoHijo.columna)
            return 4;
        else if (nodoRaiz.fila+1 == nodoHijo.fila && nodoRaiz.columna+1 == nodoHijo.columna)
            return 5;
        else if (nodoRaiz.fila+1 == nodoHijo.fila && nodoRaiz.columna == nodoHijo.columna)
            return 6;
        else if (nodoRaiz.fila+1 == nodoHijo.fila && nodoRaiz.columna-1 == nodoHijo.columna)
            return 7;
        else if (nodoRaiz.fila == nodoHijo.fila && nodoRaiz.columna-1 == nodoHijo.columna)
            return 8;
        return 0;
    }

    private NodeStructure nodoConMenorCantidadDeAristas(Graph<NodeStructure, DefaultEdge> graph) {

        Set<NodeStructure> nodos = new HashSet<>(graph.vertexSet());
        if(nodos == null)
            return null;
        int menorCantidadDeAristas = Integer.MAX_VALUE;
        NodeStructure nodoConMenorCantidadDeAristas = new ArrayList<>(nodos).get(0);
        for (NodeStructure nodo : nodos) {
            int cantidadDeAristasQueTocanEsteNodo = graph.degreeOf(nodo);
            if(cantidadDeAristasQueTocanEsteNodo < menorCantidadDeAristas){
                menorCantidadDeAristas = cantidadDeAristasQueTocanEsteNodo;
                nodoConMenorCantidadDeAristas = nodo;
            }
        }
        return nodoConMenorCantidadDeAristas;
    }

    private void crearGrafo(Graph<NodeStructure, DefaultEdge> graph, AdnDTO adnDTO) {

        String[] filas = adnDTO.getDna();
        Integer numeroDeColumnas = filas[0].length();
        Integer numeroDeFilas = filas.length;
        NodeStructure matrizDeNodos[][] = new NodeStructure[numeroDeFilas][numeroDeColumnas];

        for (int nueroFilaFor = 1; nueroFilaFor<=numeroDeFilas; nueroFilaFor++) {
            for (int nueroColumnaFor = 1; nueroColumnaFor<=numeroDeColumnas; nueroColumnaFor++) {
                char letra = filas[nueroFilaFor-1].toCharArray()[nueroColumnaFor-1];
                NodeStructure nodeStructure = crearNodo(letra, nueroColumnaFor, nueroFilaFor);
                matrizDeNodos[nueroFilaFor-1][nueroColumnaFor-1] = nodeStructure;
                graph.addVertex(nodeStructure);
            }
        }
        crearAristas(graph, matrizDeNodos, numeroDeColumnas, numeroDeFilas);
    }

    private NodeStructure crearNodo(char letra, int nueroColumnaFor, int nueroFilaFor) {
        NodeStructure nodeStructure = new NodeStructure();
        nodeStructure.nombre = String.valueOf(nueroFilaFor) +"-"+ String.valueOf(nueroColumnaFor);
        nodeStructure.fila = nueroFilaFor;
        nodeStructure.columna = nueroColumnaFor;
        nodeStructure.baseNitrogenada = letra;
        return nodeStructure;
    }

    private void crearAristas(Graph<NodeStructure, DefaultEdge> graph, NodeStructure[][] matrizDeNodos, Integer numeroDeColumnas, Integer numeroDeFilas) {
        for (int nueroFilaFor = 0; nueroFilaFor<numeroDeFilas; nueroFilaFor++) {
            for (int nueroColumnaFor = 0; nueroColumnaFor<numeroDeColumnas; nueroColumnaFor++) {
                // Se agregaran solo las aristas de nodos adyacentes que compartan la misma base nitrogenada
                char baseNitrogenada = matrizDeNodos[nueroFilaFor][nueroColumnaFor].baseNitrogenada;
                try { if(baseNitrogenada == matrizDeNodos[nueroFilaFor-1][nueroColumnaFor-1].baseNitrogenada)
                        graph.addEdge(matrizDeNodos[nueroFilaFor][nueroColumnaFor],
                        matrizDeNodos[nueroFilaFor-1][nueroColumnaFor-1]);
                } catch (ArrayIndexOutOfBoundsException e){}
                try {if(baseNitrogenada == matrizDeNodos[nueroFilaFor-1][nueroColumnaFor].baseNitrogenada)
                        graph.addEdge(matrizDeNodos[nueroFilaFor][nueroColumnaFor],
                        matrizDeNodos[nueroFilaFor-1][nueroColumnaFor]);
                } catch (ArrayIndexOutOfBoundsException e){}
                try {if(baseNitrogenada == matrizDeNodos[nueroFilaFor-1][nueroColumnaFor+1].baseNitrogenada)
                        graph.addEdge(matrizDeNodos[nueroFilaFor][nueroColumnaFor],
                        matrizDeNodos[nueroFilaFor-1][nueroColumnaFor+1]);
                } catch (ArrayIndexOutOfBoundsException e){}
                try {if(baseNitrogenada == matrizDeNodos[nueroFilaFor][nueroColumnaFor+1].baseNitrogenada)
                        graph.addEdge(matrizDeNodos[nueroFilaFor][nueroColumnaFor],
                        matrizDeNodos[nueroFilaFor][nueroColumnaFor+1]);
                } catch (ArrayIndexOutOfBoundsException e){}
                try {if(baseNitrogenada == matrizDeNodos[nueroFilaFor+1][nueroColumnaFor+1].baseNitrogenada)
                        graph.addEdge(matrizDeNodos[nueroFilaFor][nueroColumnaFor],
                        matrizDeNodos[nueroFilaFor+1][nueroColumnaFor+1]);
                } catch (ArrayIndexOutOfBoundsException e){}
                try {if(baseNitrogenada == matrizDeNodos[nueroFilaFor+1][nueroColumnaFor].baseNitrogenada)
                        graph.addEdge(matrizDeNodos[nueroFilaFor][nueroColumnaFor],
                        matrizDeNodos[nueroFilaFor+1][nueroColumnaFor]);
                } catch (ArrayIndexOutOfBoundsException e){}
                try {if(baseNitrogenada == matrizDeNodos[nueroFilaFor+1][nueroColumnaFor-1].baseNitrogenada)
                        graph.addEdge(matrizDeNodos[nueroFilaFor][nueroColumnaFor],
                        matrizDeNodos[nueroFilaFor+1][nueroColumnaFor-1]);
                } catch (ArrayIndexOutOfBoundsException e){}
                try {if(baseNitrogenada == matrizDeNodos[nueroFilaFor][nueroColumnaFor-1].baseNitrogenada)
                        graph.addEdge(matrizDeNodos[nueroFilaFor][nueroColumnaFor],
                        matrizDeNodos[nueroFilaFor][nueroColumnaFor-1]);
                } catch (ArrayIndexOutOfBoundsException e){}
            }
        }
    }
}
