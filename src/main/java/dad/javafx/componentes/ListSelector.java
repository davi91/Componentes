package dad.javafx.componentes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class ListSelector<T> extends GridPane implements Initializable {

	// Model
	
	private ListProperty<T> left = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
	private ListProperty<T> right = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
	private StringProperty leftTitle = new SimpleStringProperty();
	private StringProperty rightTitle = new SimpleStringProperty();
	
	// FXML : View
	
    @FXML
    private ListView<T> leftList, rightList;

    @FXML
    private Button moveToRightBt,  moveAllToRightBt, moveToLeftBt, moveAllToLeftBt;


    @FXML
    private Label leftLbl, rightLbl;

    
	public ListSelector() {
		super();
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListSelectorView.fxml"));
			loader.setController(this);
			loader.setRoot(this);  // <- Diferencia, establecemos la raiz
			loader.load();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		leftLbl.textProperty().bind(leftTitle);
		rightLbl.textProperty().bind(rightTitle);

		// Podemos seleccionar varios elementos
		leftList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		rightList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		leftList.itemsProperty().bind(left);
		rightList.itemsProperty().bind(right);
		
		moveAllToLeftBt.disableProperty().bind(right.emptyProperty());
		moveAllToRightBt.disableProperty().bind(left.emptyProperty());
		moveToLeftBt.disableProperty().bind(rightList.getSelectionModel().selectedItemProperty().isNull());
		moveToRightBt.disableProperty().bind(leftList.getSelectionModel().selectedItemProperty().isNull());
	}
	
    @FXML
    void moveAllLeftAction(ActionEvent event) {

    	left.addAll(right);
    	right.clear();
    }

    @FXML
    void moveAllRightAction(ActionEvent event) {

    	right.addAll(left);
    	left.clear();
    }

    @FXML
    void moveLeftAction(ActionEvent event) {

    	left.addAll(rightList.getSelectionModel().getSelectedItems());
    	right.removeAll(rightList.getSelectionModel().getSelectedItems());
    }

    @FXML
    void moveRightAction(ActionEvent event) {

    	right.addAll(leftList.getSelectionModel().getSelectedItems());
    	left.removeAll(leftList.getSelectionModel().getSelectedItems());
    }

	public final ListProperty<T> leftProperty() {
		return this.left;
	}
	

	public final ObservableList<T> getLeft() {
		return this.leftProperty().get();
	}
	

	public final void setLeft(final ObservableList<T> left) {
		this.leftProperty().set(left);
	}
	

	public final ListProperty<T> rightProperty() {
		return this.right;
	}
	

	public final ObservableList<T> getRight() {
		return this.rightProperty().get();
	}
	

	public final void setRight(final ObservableList<T> right) {
		this.rightProperty().set(right);
	}
	

	public final StringProperty leftTitleProperty() {
		return this.leftTitle;
	}
	

	public final String getLeftTitle() {
		return this.leftTitleProperty().get();
	}
	

	public final void setLeftTitle(final String leftTitle) {
		this.leftTitleProperty().set(leftTitle);
	}
	

	public final StringProperty rightTitleProperty() {
		return this.rightTitle;
	}
	

	public final String getRightTitle() {
		return this.rightTitleProperty().get();
	}
	

	public final void setRightTitle(final String rightTitle) {
		this.rightTitleProperty().set(rightTitle);
	}
	

}
