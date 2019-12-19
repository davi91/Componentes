package dad.javafx.componentes;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class DateChooser extends HBox implements Initializable {

	// FXML : View
	//--------------------------------------------------------------
	
    @FXML
    private ComboBox<Integer> dayC;

    @FXML
    private ComboBox<String> monthC;

    @FXML
    private ComboBox<String> yearC;
	
	//--------------------------------------------------------------
	
    // Variables y modelo
    private static String[] MONTHS = {"ENERO", "FEBRERO", "MARZO", "ABRIL",
    								  "MAYO", "JUNIO", "JULIO", "AGOSTO",
    								  "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE",
    								  "DICIEMBRE"};
    
    private ListProperty<Integer> days = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    
    private static int minYear = 1900;
    private int maxYear = minYear;
    
    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    
    private IntegerProperty day = new SimpleIntegerProperty();
    
    private ChangeListener<String> yearChanged = new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			
			try {
				
				if( !newValue.isEmpty() ) {
					
					int year = Integer.parseInt(newValue);
					
					// Si hemos llegado hasta aquí, entonces lo introducido es un número : TO IMPROVE
					if( year > maxYear ) {
						yearC.getSelectionModel().selectFirst();
					} else if( year < minYear ) {
						yearC.getSelectionModel().selectLast();
					} else {
						yearC.getSelectionModel().select(year);
					}
				}
				
			} catch( NumberFormatException e ) {
				yearC.getEditor().textProperty().removeListener(this); // Lo quitamos temporalmente para evitar una llamada innecesaria
				yearC.getEditor().setText(oldValue); // Entonces lo introducido no es un número, no cambiamos
				yearC.getEditor().textProperty().addListener(this); // Lo volvemos a añadir
			}
			
		}
	};
    
	public DateChooser() {
		
		super();
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DateChooserView.fxml"));
			loader.setRoot(this);
			loader.setController(this);
			loader.load();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// A los meses le asignamos meses
		monthC.getItems().addAll(MONTHS);
		
		// Los años van de 1900 a el año actual
		maxYear = LocalDate.now().getYear();
		
		// Los ponemos en orden inverso
		String[] years = IntStream.range(1900,maxYear+1).boxed().sorted(Collections.reverseOrder()).map( year -> String.valueOf(year) ).toArray(String[]::new);
		yearC.getItems().addAll(years);

		// Los días se calculan automáticamente atendiendo al mes y año seleccionados
		dayC.itemsProperty().bind(days);
		
		// Por otro lado, en el ComboBox del año sólo podemos escribir números del 0 al 9
		yearC.getEditor().textProperty().addListener(yearChanged);
		
		// Cada vez que cambia el año, necesitamos recalcular los dias
		yearC.getSelectionModel().selectedItemProperty().addListener( (o, ov, nv) -> onChangeYear(nv));
		
		// Cada vez que cambia el mes, necesitamos recalcular los dias
		monthC.getSelectionModel().selectedItemProperty().addListener( (o, ov, nv) -> OnChangeMonth());
		
		day.bind( dayC.getSelectionModel().selectedItemProperty() );
		
		
		// Por defecto seleccionamos el primer año y el primer mes del array.
		yearC.getSelectionModel().selectFirst();
		monthC.getSelectionModel().selectFirst();
		
		// Inicializamos nuestra fecha
		setDate(LocalDate.of(Integer.parseInt(yearC.getSelectionModel().getSelectedItem()), 
											  Month.of(monthC.getSelectionModel().getSelectedIndex()+1),
											  dayC.getSelectionModel().getSelectedItem()));
		
		day.addListener( (o, ov, nv) -> {
			try {
					if( getDate() != null && nv.intValue() != 0) {
						setDate(LocalDate.of(getDate().getYear(), getDate().getMonthValue(), nv.intValue()));		
					}
					
			} catch( Exception e ) {
				e.printStackTrace();
			}  // A veces da excecpión por los múltiples cambios, pero se ajusta correctamente									
		});
		
	}
	


	private void OnChangeMonth() {
		
		boolean recalculateDay = false;
		
		YearMonth ym = YearMonth.of(Integer.parseInt(yearC.getSelectionModel().getSelectedItem()), Month.of(monthC.getSelectionModel().getSelectedIndex()+1));
		Integer currentDays[] = IntStream.range(1, ym.lengthOfMonth()+1).boxed().toArray( Integer[]::new );
		
		int dayS = days.getSize() > 0 ? dayC.getSelectionModel().getSelectedIndex() : 0; // Seleccionamos el mismo dia que antes

		// Ahora necesitamos recalcular si estábamos en el último dia del mes
		if( dayC.getSelectionModel().getSelectedIndex() == days.getSize() - 1 && days.getSize() > 0) {
			recalculateDay = true;
		}
		
		days.clear();
		days.addAll(currentDays);
		
		if( recalculateDay ) {
			dayC.getSelectionModel().selectLast();
		} else {
			dayC.getSelectionModel().select(dayS);
		}
		
		// Si el mes cambia, debemos actualizar nuestra fecha
		if( getDate() != null )
			setDate(LocalDate.of(getDate().getYear(), ym.getMonth(), dayC.getSelectionModel().getSelectedItem()));
		
	}

	private void onChangeYear(String nv) {
			
		// Necesitamos recalcular los dias en el caso de estar en febrero
		if( monthC.getSelectionModel().getSelectedIndex() + 1 == Month.FEBRUARY.getValue()) {
			
			boolean recalculateDay = false;
			
			YearMonth ym = YearMonth.of(Integer.parseInt(nv), Month.of(monthC.getSelectionModel().getSelectedIndex()+1));;
			
			// Comprobamos si es bisiesto o no
			Integer currentDays[] = IntStream.range(1, ym.lengthOfMonth()+1).boxed().toArray( Integer[]::new );
			
			int dayS = days.getSize() > 0 ? dayC.getSelectionModel().getSelectedIndex() : 0; // Seleccionamos el mismo dia que antes
			// Ahora necesitamos recalcular si teníamos el último día seleccionado
			if( dayC.getSelectionModel().getSelectedIndex() == days.getSize() - 1 && days.getSize() > 0) {
				recalculateDay = true;
			}
			
			days.clear();
			days.addAll(currentDays);
			
			if( recalculateDay ) {
				dayC.getSelectionModel().selectLast();
			} else {
				dayC.getSelectionModel().select(dayS);
			}
		}
		
		// Si el año cambia, debemos actualizar nuestra fecha
		if( getDate() != null )
			setDate(LocalDate.of(Integer.parseInt(nv), getDate().getMonth(), dayC.getSelectionModel().getSelectedItem()));

	}

	public final ObjectProperty<LocalDate> dateProperty() {
		return this.date;
	}
	

	public final LocalDate getDate() {
		return dateProperty().get();
	}
	

	/**
	 * Lo restringimos a uso privado, puesto que lo cambian los
	 * propios componetens
	 * @param date Fecha a ajustar
	 */
	private final void setDate(final LocalDate date) {
		dateProperty().set(date);

	}
	
	/**
	 * Ajustamos la fecha
	 * @param date Fecha nueva
	 */
	public final void adjustDate(final LocalDate date) {
		
		// Ajustamos la fecha poniendo los datos en los ComboBox, cuyos cambios
		// ya afectan a nuestro objeto, en lugar de modificarlo directamente
		dayC.getSelectionModel().select(date.getDayOfMonth()-1);
		yearC.getSelectionModel().select(String.valueOf(date.getYear()));
		monthC.getSelectionModel().select(date.getMonthValue()-1);	
	}
	

}
