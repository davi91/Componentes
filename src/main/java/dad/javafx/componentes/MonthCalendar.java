package dad.javafx.componentes;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

public class MonthCalendar extends VBox implements Initializable {

	// FXML : View
	// ----------------------------------------------
	
    @FXML
    private VBox view;

    @FXML
    private Label monthTxt;

    @FXML
    private GridPane mainGrid;
    
    // ----------------------------------------------
    
    // Model
    // ----------------------------------------------
    private IntegerProperty monthProperty = new SimpleIntegerProperty();
    private IntegerProperty yearProperty = new SimpleIntegerProperty();
    private ListProperty<Node> daysList = new SimpleListProperty<Node>(FXCollections.observableArrayList(new ArrayList<>()));
    // ----------------------------------------------
    
    private String[] months = { "Enero", "Febrero", "Marzo", "Abril",
    							"Mayo", "Junio", "Julio", "Agosto",
    							"Septiembre", "Octubre", "Noviembre",
    							"Diciembre" };
	public MonthCalendar() {
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MonthView.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// Eventos
		monthProperty.addListener( (o, ov, nv ) -> onMonthChanged(nv) );
		yearProperty.addListener( (o, ov, nv) -> onYearChanged(nv) );

	}


	/**
	 * Hemos cambiado el año, por tanto modificamos los datos
	 * del calendario
	 * @param nv
	 */
	private void onYearChanged(Number nv) {
		
		for( int i = 1; i < mainGrid.getChildren().size(); i++ ) {
			mainGrid.getChildren().removeAll(daysList.get());
		}
		
		mainGrid.getRowConstraints().clear();
		
		if( getMonthProperty() != 0 ) {
			onMonthChanged(getMonthProperty()); // Reajustamos los datos
		}
	}

	/**
	 * Cada vez que cambiamos el mes, debemos reajustar los dias acorde a 
	 * ese mes
	 * @param nv Nuevo mes entrante
	 */
	private void onMonthChanged(Number nv) {
		
		int year = yearProperty.get();
		
		monthTxt.setText( months[nv.intValue()-1]);
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(year, nv.intValue()-1, 0);
		int dayInit = calendar.get(Calendar.DAY_OF_WEEK);
		
		int d = 1;
		int row = 1;
		int totalDays = YearMonth.of(getYearProperty(), nv.intValue()).lengthOfMonth();
		while( d <= totalDays ) {
			
			int i = 0;
			
			for ( i = dayInit ; i <= 7 && d <= totalDays; i++) {

				Label day = new Label(String.valueOf(d));
				daysList.add(day);
				day.setPrefHeight(USE_COMPUTED_SIZE);
				mainGrid.add(day, i-1, row);
				d++;
			}
			
			dayInit = 1; // Volvemos a ponerlo desde el principio
			
			RowConstraints r = new RowConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
			r.setFillHeight(true);
			r.setVgrow(Priority.ALWAYS);
			mainGrid.getRowConstraints().add(r);
			
			row++;
		}
	}

	public final IntegerProperty monthPropertyProperty() {
		return this.monthProperty;
	}
	

	public final int getMonthProperty() {
		return this.monthPropertyProperty().get();
	}
	

	public final void setMonthProperty(final int monthProperty) {
		this.monthPropertyProperty().set(monthProperty);
	}
	

	public final IntegerProperty yearPropertyProperty() {
		return this.yearProperty;
	}
	

	public final int getYearProperty() {
		return this.yearPropertyProperty().get();
	}
	

	public final void setYearProperty(final int yearProperty) {
		this.yearPropertyProperty().set(yearProperty);
	}
	

}
