package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Player, DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao;
	private Map<Integer, Player> idMap;
	
	
	private List<Player> percorsoMigliore;
	
	public Model() {
		dao = new PremierLeagueDAO();
		idMap = new HashMap<Integer, Player>();
		dao.listAllPlayers(idMap);
	}
	
	public void creaGrafo(double x) {
		
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiungo i vertici filtrati
		Graphs.addAllVertices(grafo, dao.getVertici(x, idMap));
		
		// aggiungo gli archi 
		for(Adiacenza a: dao.getAdiacenza(idMap)) {
			if(this.grafo.containsVertex(a.getP1()) && this.grafo.containsVertex(a.getP2())) {
				if(a.getPeso() > 0) {
					Graphs.addEdgeWithVertices(grafo, a.getP1(), a.getP2(), a.getPeso());
				}else if(a.getPeso()<0) {
					Graphs.addEdgeWithVertices(grafo, a.getP2(), a.getP1(), Math.abs(a.getPeso()));
				}
			}
		}
		
		
	}
	
	public List<Adiacenza> getAdiacenza(){
		return dao.getAdiacenza(idMap);
	}
	
	public int getNumVertici() {
		if(this.grafo!=null) {
			return this.grafo.vertexSet().size();
		}
		return 0;
	}
	
	public int getNumArchi() {
		if(this.grafo!=null) {
			return this.grafo.edgeSet().size();
		}
		return 0;
	}

	// METODO PER STAMPARE IL TOP-PLAYER
		public Player getTopPlayer(double x) {
			int max = 0;
			Map <Integer, Player> mappa = new HashMap<Integer, Player>();
			if(grafo!= null) {
				for (Player p: dao.getVertici(x, idMap)) {
					
					if(grafo.outDegreeOf(p) > max) {
						max = grafo.outDegreeOf(p);
						mappa.put(max, p);
					}
				}
			Player top = mappa.get(max);
			return top;
			}
			return null;
		}
		
		// METODO PER PRENDERE LA LISTA DEGLI AVVERSARI DEL TOP-PLAYER
		public Set<Adiacenza> getAvversariTopPlayer(double x){
			Player top = this.getTopPlayer(x);
			Set<Adiacenza> lista = new HashSet<>();
			lista.add( (Adiacenza) grafo.outgoingEdgesOf(top));
			return lista;
		}
	
		/*
	// METODO PER ALGORITMO RICORSIVO (VISITA)
		public List<Player> trovaPercorso(int k){
			this.percorsoMigliore = new ArrayList<>();
			List<Player> parziale = new ArrayList<>();
			
			cerca( k, parziale);
			return this.percorsoMigliore;
		}

	private void cerca( int k, List<Player> parziale) {
		double gradoE = 0;
		double gradoU = 0;
		double grado = 0;
		double max = 0;
		
		if ( parziale.size() == k ) {
			
			if (max > grado) {
				this.percorsoMigliore = new LinkedList<>(parziale);
			}
		}
		
		// per ogni giocatore calcolo il suo grado di titolarità
		
		for(Player p: this.grafo.vertexSet()) {
			for (Adiacenza a: this.getAdiacenza()) {
				if(a.getP1().equals(p) || a.getP2().equals(p)) {
			
					// conto pesi USCENTI
					for (DefaultWeightedEdge arcoU: this.grafo.outgoingEdgesOf(p)) {
					
						gradoU = gradoU + a.getPeso();
				
					}
					// conto pesi ENTRANTI
					for (DefaultWeightedEdge arcoE: this.grafo.outgoingEdgesOf(p)) {
						
						gradoE = gradoE + a.getPeso();
					
					}
					
					// calcolo titolarià 
					grado = gradoU - gradoE;
					
					
				}
			}
			if (! parziale.contains(p)) {
				parziale.add(p);
				cerca(k,parziale);
				parziale.remove(parziale.size()-1);
				
			}
		}
	
	}

	*/	
}
