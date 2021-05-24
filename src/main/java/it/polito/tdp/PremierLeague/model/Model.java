package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Player, DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao;
	private Map<Integer, Player> idMap;
	
	
	private List<Player> percorsoMigliore;
	private int gradoMigliore; // lo dichiaro qui per fae il GETTER
	
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
		public Set<DefaultWeightedEdge> getAvversariTopPlayer(double x){
			Player top = this.getTopPlayer(x);
			Set<DefaultWeightedEdge> avversari = this.grafo.outgoingEdgesOf(top);
			
			return avversari;
		}
		
		/**
		 * METODO PER RESTITUIRE AVVERSARI IN ORDINE DI PESO DECRESCENTE
		 */
	
		public LinkedHashMap <Double, Player> getSconfittiMappa(Player p) {
			
			//in ordine di peso decrescente per chiave
						//peso DELTA, sconfitto 
			LinkedHashMap <Double, Player> sconfitti = new LinkedHashMap <Double, Player>();
			TreeMap<Double, Player> sconfittiCresc = new TreeMap <Double, Player>();
			
			for(DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(p)) {
				sconfittiCresc.put(this.grafo.getEdgeWeight(e), this.grafo.getEdgeTarget(e));
			}
				
			// ora ho la mappa in ordine crescente
			
			
			return sconfitti;
		}
		
	// METODO PER ALGORITMO RICORSIVO (VISITA)
		public List<Player> trovaPercorso(int k){
			this.percorsoMigliore = new ArrayList<>();
			List<Player> parziale = new ArrayList<>();
			
			cerca(parziale, new ArrayList<Player>( this.grafo.vertexSet()), k);
			return this.percorsoMigliore;
		}

	private void cerca(List<Player> parziale, ArrayList<Player> giocatori, int k) {
		
		if ( parziale.size() == k ) {
			int grado = this.getGrado(parziale);
			if(grado > gradoMigliore) {
				this.percorsoMigliore = new LinkedList<>(parziale);
				gradoMigliore = grado;
			}
				
			
		}
		
		// per ogni giocatore calcolo il suo grado di titolarità
		
		for(Player p: giocatori) {
			if (!parziale.contains(p)) {
				// aggiungo il giocatore
				parziale.add(p);
				// creo la lista di giocatori che possono entrare
				// prima la riempio con tutti i giocatori passati nella lista da parametro
				ArrayList<Player> giocatoriRimanenti = new ArrayList<>(giocatori);
				// poi rimuovo i successori di questo vertice (cioè gli avversari battuti)
				giocatoriRimanenti.removeAll(Graphs.successorListOf(grafo, p));
				// ATTENZIONE!! -- GRAPHS E' LA CLASSE CHE DISPONE DEI METODI
				
				cerca(parziale, giocatoriRimanenti, k);
				parziale.remove(p);
			}
		}
	}
		

	

	private int getGrado(List<Player> team) {
		int gradoE = 0;
		int gradoU = 0;
		int grado = 0;
		
		
		for (Player p: team) {
				// conto pesi USCENTI
				for (DefaultWeightedEdge arcoU: this.grafo.outgoingEdgesOf(p)) {
						
					gradoU += this.grafo.getEdgeWeight(arcoU);
					
				}
				// conto pesi ENTRANTI
				for (DefaultWeightedEdge arcoE: this.grafo.incomingEdgesOf(p)) {
							
					gradoE += this.grafo.getEdgeWeight(arcoE);
						
				}
						
				// calcolo titolarià 
				grado = gradoU - gradoE;
			
			}
			
		return grado;
	}
	
	public int getGradoMigliore() {
		return gradoMigliore;
	}
		
}
