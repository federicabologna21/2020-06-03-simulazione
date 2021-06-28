package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Player, DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao;
	private Map<Integer, Player> idMap;
	
	private List<Player> percorsoMigliore;
	private int gradoMigliore = 0;
	
	
	public Model() {
		dao = new PremierLeagueDAO();
		idMap = new HashMap <Integer, Player>();
		dao.listAllPlayers(idMap);
	}
	
	public void creaGrafo(double x) {
		grafo = new SimpleDirectedWeightedGraph <>(DefaultWeightedEdge.class);
		
		// aggiungo i vertici 
		Graphs.addAllVertices(grafo, dao.getVertici(x, idMap));
		
		// aggiungo gli archi 
		for (Adiacenza a: dao.getAdiacenze(idMap)) {
			if(this.grafo.containsVertex(a.getP1()) && this.grafo.containsVertex(a.getP2())) {
				if(a.getPeso()>0) {
					//p1 ha giocato più di p2
					Graphs.addEdgeWithVertices(grafo, a.getP1(), a.getP2(), a.getPeso());
					
				}else if (a.getPeso()<0) {
					// p2 ha giocato più di p1
					Graphs.addEdgeWithVertices(grafo, a.getP2(), a.getP1(), Math.abs(a.getPeso()));
				
				}
			}
		}
	}

	public int getNumVertici() {
		if(this.grafo != null) {
			return this.grafo.vertexSet().size();
		}
		return 0;
	}
	public int getNumArchi() {
		if(this.grafo != null) {
			return this.grafo.edgeSet().size();
		}
		return 0;
	}
	
	public Player getTopPlayer() {
		Player pTop = null;
		
		int numAvv = 0;
		for (Player p: this.grafo.vertexSet()) {
	
			if (numAvv < this.grafo.outDegreeOf(p)) {
				numAvv = this.grafo.outDegreeOf(p);
				pTop = p;
			}
		}
		return pTop;
		
	}
	
	 public List<AvversariBattuti> getAvversariTop (Player p){
		 List<AvversariBattuti> result = new LinkedList<AvversariBattuti>();
		 double peso = 0;
		 for (DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(p)) {
			 Player pp = this.grafo.getEdgeTarget(e);
			 peso = this.grafo.getEdgeWeight(e);
			 
			 AvversariBattuti a = new AvversariBattuti (pp, peso);
			 result.add(a);
			 
		 }
		 Collections.sort(result, new Comparator<AvversariBattuti>(){

			@Override
			public int compare(AvversariBattuti o1, AvversariBattuti o2) {
				// TODO Auto-generated method stub
				Double d1 = o1.getPeso();
				Double d2 = o2.getPeso();
				return d2.compareTo(d1);
			}
			 
		 });
		 return result;
	 }
	 
	 public List<Player> trovaPercorso(int k){
		 this.percorsoMigliore = new ArrayList<>();
		
		 
		 List<Player> parziale = new ArrayList<>();
		 cerca(parziale, k, new ArrayList<Player> (this.grafo.vertexSet()));
		 return this.percorsoMigliore;
	 }

	private void cerca(List<Player> parziale, int k, ArrayList<Player> giocatori) {
		// caso terminale 
		if (k == parziale.size()) {
			if (gradoMigliore < this.calcolaGrado(parziale)) {
				gradoMigliore = this.calcolaGrado(parziale);
				this.percorsoMigliore = new ArrayList<> (parziale);
			}
		}
		// altrimenti ... 
		for (Player p: giocatori) {
			if (!parziale.contains(p)) {
				parziale.add(p);
				
				ArrayList<Player> giocatoriRimasti = new ArrayList<>(giocatori);
				giocatoriRimasti.removeAll(Graphs.successorListOf(grafo, p));
				cerca(parziale, k, giocatoriRimasti);
				parziale.remove(p);
			}
		}
		 
	}

	private int calcolaGrado(List<Player> parziale) {
		double  pesoU = 0;
		double pesoE = 0;
		int pesofinale = 0;
		for (Player p: parziale) {
			for (DefaultWeightedEdge entranti : this.grafo.incomingEdgesOf(p)) {
				pesoE = pesoE + this.grafo.getEdgeWeight(entranti);
			}
			for (DefaultWeightedEdge uscenti: this.grafo.outgoingEdgesOf(p)) {
				pesoU = pesoU + this.grafo.getEdgeWeight(uscenti);
				
			}
			pesofinale = (int) (pesoU - pesoE);
		}
		
		return pesofinale;
		
	}
}
