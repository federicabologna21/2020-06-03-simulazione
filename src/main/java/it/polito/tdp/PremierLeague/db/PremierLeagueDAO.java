package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public void listAllPlayers(Map<Integer, Player> idMap){
		String sql = "SELECT * FROM Players";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				if(!idMap.containsKey(res.getInt("PlayerID"))) {
					Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
					idMap.put(player.getPlayerID(), player);
				}
				
			}
			conn.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Player> getVertici(double x, Map<Integer,Player>idMap){
		String sql="SELECT a.PlayerID AS playerID,  AVG(a.Goals) AS mediaGoal "
				+ "FROM actions a "
				+ "GROUP BY a.PlayerID "
				+ "HAVING AVG(a.Goals)>?";
		List<Player> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, x);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
					result.add(idMap.get(res.getInt("playerID")));
				
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return  null;
		}
	}
	
	public List<Adiacenza> getAdiacenza(Map<Integer, Player> idMap){
		String sql="SELECT a1.PlayerID AS a1, a2.PlayerID AS a2, SUM(a1.TimePlayed)AS tempo1, SUM(a2.TimePlayed) AS tempo2 "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.PlayerID > a2.PlayerID AND a1.TeamID != a2.TeamID AND a1.MatchID = a2.MatchID "
				+ "AND a1.Starts=1 AND a2.Starts=1  "
				+ "GROUP BY a1.PlayerID, a2.PlayerID";
		
		List<Adiacenza> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			double delta;
			
			while (res.next()) {
				
				Player gioc1 = idMap.get(res.getInt("a1"));
				Player gioc2 = idMap.get(res.getInt("a2"));

				if(gioc1 != null && gioc2!=null) {
					delta = res.getDouble("tempo1")-res.getDouble("tempo2");
					
					
					if(delta !=  0) {
						// se il delta non è uguale a zero => creo l'arco
						Adiacenza a = new Adiacenza(idMap.get(res.getInt("a1")), idMap.get(res.getInt("a2")), delta);
						result.add(a);
						
					
						
	 				}else  {
	 					// non creo l'arco
	 					
	 					System.out.println("NON ESISTE ARCO TRA DUE VERTICI PERCHE' DELTA = 0");
	 					
	 				}
				}else {
					System.out.println("Errore in getAdiacenze");
					
				}
					
				
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return  null;
		}
	
	}	
	
	
}
