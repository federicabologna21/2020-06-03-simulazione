/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnTopPlayer"
    private Button btnTopPlayer; // Value injected by FXMLLoader

    @FXML // fx:id="btnDreamTeam"
    private Button btnDreamTeam; // Value injected by FXMLLoader

    @FXML // fx:id="txtK"
    private TextField txtK; // Value injected by FXMLLoader

    @FXML // fx:id="txtGoals"
    private TextField txtGoals; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	txtResult.clear();
    	String goalS = txtGoals.getText();
    	double goal;
    	
    	if (goalS == null) {
			txtResult.setText("Inserire un valore");
		}
    	
    	try {
    		goal = Double.parseDouble(goalS);
    		
    			
    	}catch(NumberFormatException e) {
    		txtResult.setText("Il valore inserito deve essere un numero");
    		return; 
    	}
    	
    	// richiamo grafo
    	this.model.creaGrafo(goal);
    	txtResult.appendText("GRAFO CREATO!"+"\n");
    	txtResult.appendText("# VERTICI: "+this.model.getNumVertici()+"\n");
    	txtResult.appendText("# ARCHI: "+this.model.getNumArchi()+"\n");

    	btnTopPlayer.setDisable(false);
    }

    @FXML
    void doDreamTeam(ActionEvent event) {

    	
    	String kS = txtK.getText();
    	int k;
    	
    	try {
    		k=Integer.parseInt(kS);
    	}catch(NumberFormatException ne){
    		txtResult.appendText("Errore: inserire un numero!");
    		return; 
    	}
    	
    	this.doCreaGrafo(event);
    	
    	List<Player> dreamTeam= this.model.trovaPercorso(k);
    	txtResult.appendText("IL DREAM TEAM E' COMPOSTO DA: "+"\n");
    	txtResult.appendText(dreamTeam+"\n");
    	txtResult.appendText("IL GRADO DI TITOLARITA' E': "+this.model.getGradoMigliore());
    }

    @FXML
    void doTopPlayer(ActionEvent event) {
    	
    	
    	String goalS = txtGoals.getText();
    	double goal = Double.parseDouble(goalS);
    	
    // SE HO POTUTO PREMERE IL BOTTONE TOP-PLAYER E' PERCHE' LA CREAZIONE
    // DEL GRAFO E' ANDATA A BUON FINE (QUINDI IL PARAMETRO INSERITO E' CORRETTO)
    	// richiamo grafo
    	this.model.creaGrafo(goal);
    	txtResult.appendText("IL TOP-PLAYER E': "+this.model.getTopPlayer(goal)+"\n");
    	txtResult.appendText("Gli avversari battuti (con peso decrescente) sono: "
    			+this.model.getAvversariTopPlayer(goal).toString()+"\n");
    	
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnTopPlayer != null : "fx:id=\"btnTopPlayer\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnDreamTeam != null : "fx:id=\"btnDreamTeam\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtK != null : "fx:id=\"txtK\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtGoals != null : "fx:id=\"txtGoals\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
