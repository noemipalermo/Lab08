package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private Graph<Airport, DefaultWeightedEdge> grafo;
	private List<Airport> aeroporti;
	private Map<Integer,Airport> aeroportiIdMap;
	private ExtFlightDelaysDAO dao;
	
	public Model() {
		this.aeroporti= new ArrayList<>();
		dao = new ExtFlightDelaysDAO();
		this.aeroportiIdMap = new HashMap<>();
	}
	
	public void creaGrafo(int dMin) {
		
		//1. Creo il grafo
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		
		//2. Aggiungo i vertici
		if(this.aeroporti.isEmpty())
			this.aeroporti = dao.loadAllAirports();
	
		if(this.aeroportiIdMap.isEmpty()) {
			for(Airport a: this.aeroporti)
				this.aeroportiIdMap.put(a.getId(), a);
		}
		
		Graphs.addAllVertices(this.grafo, this.aeroporti);
		
		
		//3. Aggiungo gli archi
		
		
		for(Rotta r: this.dao.getRotte(aeroportiIdMap, dMin)) {
			
			//controllo se esiste già un arco
			//se esiste, aggiorno il peso
			DefaultWeightedEdge edge = grafo.getEdge(r.getPartenza(), r.getArrivo());
			if(edge == null) {
				Graphs.addEdge(grafo, r.getPartenza(), r.getArrivo(), r.getDistanza());
			} else {
				double peso = grafo.getEdgeWeight(edge);
				double newPeso = (peso + r.getDistanza())/2;
				grafo.setEdgeWeight(edge, newPeso);
			}
		}
		
	}
	
	
	//4. Controllo se il grafo è stato creato
	public boolean isGrafoLoaded() {
		return this.grafo.vertexSet().size()>0;
	}
		
	public int getVerticesNum() {
		return this.grafo.vertexSet().size();
	}
	
	public int getEdgesNum() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Rotta> getAllRotte(){
		List<Rotta> rotte= new ArrayList<>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			rotte.add(new Rotta(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
		}
		return rotte;
	}
	
}
