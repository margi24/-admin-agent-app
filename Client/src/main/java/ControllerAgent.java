import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Produs;
import model.User;
import validator.ValidationException;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ControllerAgent extends UnicastRemoteObject implements Initializable,IObserver {
    @FXML
    private TableView<Produs> table;

    @FXML
    private TableColumn<Produs, String> numeCol;

    @FXML
    private TableColumn<Produs, Integer> pretCol;

    @FXML
    private TableColumn<Produs, Integer> cantCol;

    @FXML
    private TextField numeTxt;

    @FXML
    private TextField canText;
    private IServer service;
    private ObservableList<Produs> observableList;

    private double xOffset=0;
    private double yOffset=0;
    private User user;
    public ControllerAgent() throws RemoteException {
    }

    public void setService(IServer service, User user) {
        this.service = service;
        observableList = FXCollections.observableList(StreamSupport.stream(service.getAllProducts().spliterator(), false).collect(Collectors.toList()));
        this.user=user;
        loadTables();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        numeCol.setCellValueFactory(new PropertyValueFactory<Produs, String>("denumire"));
        pretCol.setCellValueFactory(new PropertyValueFactory<Produs, Integer>("pret"));
        cantCol.setCellValueFactory(new PropertyValueFactory<Produs, Integer>("cantitate"));
    }


    private void loadTables() {
        table.setItems(observableList);
        table.getSelectionModel().selectedItemProperty().addListener((observer, oldData, newData) -> showDetails(newData));
    }

    private void showDetails(Produs t) {
        if (t != null) {
            numeTxt.setText(t.getDenumire());
        }
    }

    public void clickLogOut(ActionEvent event) throws IOException,ServerEx {
        table.getScene().getWindow().hide();
        Parent root;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(ControllerAgent.class.getResource("gui/login.fxml"));
        root = loader.load();
        ControllerLogin controller = loader.getController();
        controller.setService(service);
        service.logout(user);
        root.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset=event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX()-xOffset);
                stage.setY(event.getScreenY()-yOffset);
            }
        });
        stage.initStyle(StageStyle.TRANSPARENT);
        Scene scene=new Scene(root, 345,442);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public synchronized void clickComanda(ActionEvent event) {
        try{
            if(Integer.valueOf(canText.getText())<0){
                showAllert(new Exception("Cantitatea nu poate fi negativa"));
            }
            else {
                Produs p = service.findProdus(numeTxt.getText());
                service.updateProdus(new Produs(p.getDenumire(), p.getPret(), p.getCantitate() - Integer.valueOf(canText.getText())));
            }
            observableList = FXCollections.observableList(StreamSupport.stream(service.getAllProducts().spliterator(), false).collect(Collectors.toList()));
            table.setItems(observableList);
        }catch (ValidationException | NumberFormatException ex){
            showAllert(ex);
        } catch (ServerEx serverEx) {
            serverEx.printStackTrace();
        }

    }
    public void showAllert(Exception e){
        Alert alert ;
        alert=new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText(null);
        alert.setContentText("Comanda nu se poate realiza. Verificati datele");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/cssDesign/myDialogs.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        dialogPane.setHeader(null);
        alert.show();
    }

    @Override
    public synchronized void update(Produs m) throws ServerEx, RemoteException {
        int i = 0;
        int gas = 0;
        Produs produs = null;
        for (Produs p : observableList
        ) {
            if (m.getDenumire().equals(p.getDenumire())) {
                gas = i;
                produs = p;
            }
            i++;
        }
        Produs p2 = new Produs(produs.getDenumire(), produs.getPret(), m.getCantitate());
        observableList.set(gas, p2);
        this.table.setItems(observableList);

    }
}