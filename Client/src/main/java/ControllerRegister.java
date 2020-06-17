import animatefx.animation.FadeIn;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import model.User;
import validator.ValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControllerRegister {
    @FXML
    private ComboBox<String> combo;

    @FXML
    private TextField numeTxt;

    @FXML
    private PasswordField parolaTxt;
    @FXML
    private Label lbl;

    public ControllerRegister(){}
    IServer service;

    private double xOffset=0;
    private double yOffset=0;

    @FXML
    private ImageView logImg;

    User user;
    void setService(IServer service, User user){
        this.service=service;
        lbl.setVisible(false);
        this.user=user;
        loadComboBox();
    }
    public void loadComboBox(){
        List<String> listaNume=new ArrayList<>();
        listaNume.add("Admin");
        listaNume.add("Agent");
        ObservableList<String> observable= FXCollections.observableList(listaNume);
        combo.setItems(observable);
    }

    public void clickLogOut(MouseEvent event) throws IOException {
        logImg.getScene().getWindow().hide();
        Parent root;
        Stage stage;
        stage= new Stage();
        FXMLLoader loader = new FXMLLoader(ControllerAgent.class.getResource("gui/admin.fxml"));
        root = loader.load();
        ControllerAdmin controller = loader.getController();
        controller.setService(service,user);
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
        Scene scene=new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public void imgJump5(MouseEvent event) {
        new FadeIn(logImg).setSpeed(0.5).play();
    }

    public void clickAdd(ActionEvent event) {

        if (numeTxt.getText().equals("") || parolaTxt.equals("") ) {
            allert(new RuntimeException("Date incorecte"));
        } else {
            try {
                if (combo.getValue() == "Admin") {
                    User u = new User(numeTxt.getText(), parolaTxt.getText(), "admin");
                    service.saveUser(u);
                }
                if (combo.getValue() == "Agent")
                    service.saveUser(new User(numeTxt.getText(), parolaTxt.getText(), "agent"));

                lbl.setVisible(true);
                PauseTransition visiblePause = new PauseTransition(
                        Duration.seconds(2)
                );
                visiblePause.setOnFinished(
                        event2 -> lbl.setVisible(false)
                );
                visiblePause.play();
            } catch (ValidationException | NullPointerException ex) {
                allert(ex);
            }
        }


    }

    private void allert(RuntimeException ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare sign up");
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText("Datele introduse nu sunt corecte sau deja exista");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/cssDesign/myDialogs.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        dialogPane.setHeader(null);
        alert.show();
    }

}
