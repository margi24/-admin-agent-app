import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class ControllerLogin extends UnicastRemoteObject {
    @FXML
    private TextField lblNume;
    @FXML
    private TextField lblParola;

    private double xOffset=0;
    private double yOffset=0;
    private IServer service;

    public ControllerLogin() throws RemoteException {
    }

    public void setService(IServer service){
        this.service=service;
    }
    public void clickLogin() {
               if (lblNume.getText().equals("") || lblParola.getText().equals("")) {
            showAllert(new Exception("ceva"));
        } else {
                   try {
                       User user = service.findByUsername(lblNume.getText());
                       if (user != null) {
                           if (user.getTip().equals("admin")) {
                               Parent root;
                               FXMLLoader loader = new FXMLLoader(ControllerLogin.class.getResource("gui/admin.fxml"));
                               root = loader.load();
                               ControllerAdmin ctrl = loader.getController();
                               ctrl.setService(service,user);
                               service.login(user, ctrl);
                               Stage primaryStage = (Stage) lblNume.getScene().getWindow();
                               Scene scene = new Scene(root);
                               move(root, primaryStage);
                               scene.setFill(Color.TRANSPARENT);
                               primaryStage.setScene(scene);
                           }
                           if (user.getTip().equals("agent")) {
                               Parent root;
                               FXMLLoader loader = new FXMLLoader(ControllerLogin.class.getResource("gui/Agent.fxml"));
                               root = loader.load();
                               ControllerAgent ctrl = loader.getController();
                               ctrl.setService(service,user);
                               service.login(user, ctrl);
                               Stage primaryStage = (Stage) lblNume.getScene().getWindow();
                               Scene scene = new Scene(root);
                               move(root, primaryStage);
                               scene.setFill(Color.TRANSPARENT);
                               primaryStage.setScene(scene);

                           }
                       }
                   } catch (Exception e) {
                       showAllert(e);
                   }
               }
        }




    private void showAllert(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroaree");
        alert.setHeaderText(null);
        alert.setContentText("Numele sau parola incorecta");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/cssDesign/myDialogs.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        dialogPane.setHeader(null);
        alert.show();
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
}
