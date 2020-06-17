import animatefx.animation.FadeIn;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;
import model.Produs;
import validator.ValidationException;
import model.User;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ControllerAdmin extends UnicastRemoteObject implements Initializable ,IObserver {

    @FXML
    private TableView<Produs> table;

    @FXML
    private TableColumn<Produs, String> colNume;

    @FXML
    private TableColumn<Produs, Integer> colPret;

    @FXML
    private TableColumn<Produs, Integer> colCantitate;

    @FXML
    private TextField lblNume;

    @FXML
    private TextField lblCant;

    @FXML
    private TextField lblPret;

    @FXML
    private ImageView logImg;

    @FXML
    private ImageView imgCont;

    private ObservableList<Produs> observableList;
    private User user;
    private double xOffset=0;
    private double yOffset=0;
    private IServer service;
    public ControllerAdmin() throws RemoteException{}
    public void setService(IServer service,User user){
        this.service=service;
        this.user=user;
        observableList = FXCollections.observableList(StreamSupport.stream(service.getAllProducts().spliterator(), false).collect(Collectors.toList()));
        loadTables();

    }

    private void loadTables() {
        table.setItems(observableList);
        table.getSelectionModel().selectedItemProperty().addListener((observer, oldData, newData) -> showDetails(newData));
        table.setEditable(true);
        colCantitate.setCellFactory(TextFieldTableCell.forTableColumn(new CustomIntegerStringConverter()));
        colPret.setCellFactory(TextFieldTableCell.forTableColumn(new CustomIntegerStringConverter()));
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNume.setCellValueFactory(new PropertyValueFactory<Produs,String>("denumire"));
        colPret.setCellValueFactory(new PropertyValueFactory<Produs,Integer>("pret"));
        colCantitate.setCellValueFactory(new PropertyValueFactory<Produs,Integer>("cantitate"));

    }

    private void showDetails(Produs t) {if(t!=null){
        lblNume.setText(t.getDenumire());
        lblCant.setText(String.valueOf(t.getCantitate()));
        lblPret.setText(String.valueOf(t.getPret()));
    }

    }

    public void clickAdd(ActionEvent event) {
        try {
            service.saveProduct(new Produs(lblNume.getText(), Integer.parseInt(lblPret.getText()), Integer.parseInt(lblCant.getText())));
            observableList.add(new Produs(lblNume.getText(), Integer.parseInt(lblPret.getText()), Integer.parseInt(lblCant.getText())));
            table.setItems(observableList);
        }catch (ValidationException e){
            showAllert(e);
        }
    }
    public void clickDelete(ActionEvent event) {
        try {
            Produs p = new Produs(lblNume.getText(), Integer.parseInt(lblPret.getText()), Integer.parseInt(lblCant.getText()));
            service.deleteProduct(p);
            observableList.clear();
            observableList = FXCollections.observableList(StreamSupport.stream(service.getAllProducts().spliterator(), false).collect(Collectors.toList()));
            table.setItems(observableList);
        }catch (ValidationException e){
            showAllert(e);
        }
    }

    public synchronized void onPretChange(TableColumn.CellEditEvent<Produs, Integer> pretIntegerCellEditEvent) {

        try{
            Produs produs=table.getSelectionModel().getSelectedItem();
            produs.setPret((Integer) pretIntegerCellEditEvent.getNewValue());
            service.updateProdus(produs);
        }
        catch (ValidationException e){
            showAllert(e);
            observableList.clear();
            observableList.addAll((Collection<? extends Produs>) service.getAllProducts());

        } catch (ServerEx serverEx) {
            serverEx.printStackTrace();
        }
    }

    public synchronized void onCantitateChange(TableColumn.CellEditEvent<Produs, Integer> pretIntegerCellEditEvent) {

        try {
            Produs produs = table.getSelectionModel().getSelectedItem();
            produs.setCantitate((Integer)pretIntegerCellEditEvent.getNewValue());
            service.updateProdus(produs);
        }
           catch (ValidationException e){
            showAllert(e);
            observableList.clear();
            observableList.addAll((Collection<? extends Produs>) service.getAllProducts());
        } catch (ServerEx serverEx) {
            serverEx.printStackTrace();
        }

    }

    public void clickLogOut(MouseEvent event) throws IOException,ServerEx {
        table.getScene().getWindow().hide();
        Parent root;
        Stage stage;
        stage= new Stage();
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


    private void showAllert(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText(null);
        alert.setContentText(e.getMessage());
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/cssDesign/myDialogs.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        dialogPane.setHeader(null);
        alert.show();
    }

    public void imgJump5(MouseEvent event) {
        new FadeIn(logImg).setSpeed(0.5).play();
    }

    public void clickCont(MouseEvent event) throws IOException {
        Parent root;
        FXMLLoader loader = new FXMLLoader(ControllerLogin.class.getResource("gui/Register.fxml"));
        root=loader.load();
        ControllerRegister ctrl=loader.getController();
        ctrl.setService(service,user);
        Stage primaryStage=(Stage) lblNume.getScene().getWindow();
        Scene scene=new Scene(root);
        move(root,primaryStage);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);

    }

    public void contMove(MouseEvent event) {
        new FadeIn(imgCont).setSpeed(0.5).play();

    }

    private void move(Parent root, Stage stage) {
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
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
        Produs p2=null;
        if(produs.getCantitate() != m.getCantitate()) {
            p2 = new Produs(produs.getDenumire(), produs.getPret(), m.getCantitate());
        }

        observableList.set(gas, p2);
        this.table.setItems(observableList);
    }

    private class CustomIntegerStringConverter extends IntegerStringConverter {
        private final IntegerStringConverter converter = new IntegerStringConverter();
        String p;

        @Override
        public String toString(Integer object) {
            try {
                return converter.toString(object);
            } catch (NumberFormatException e) {
                table.setItems(observableList);
            }
            return null;
        }

        @Override
        public Integer fromString(String string) {
            try {
                return converter.fromString(string);
            } catch (NumberFormatException e) {
                table.setItems(observableList);
            }
            return -2;
        }
    }
}
