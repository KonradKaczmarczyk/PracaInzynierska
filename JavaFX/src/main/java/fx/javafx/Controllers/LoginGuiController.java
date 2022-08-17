package fx.javafx.Controllers;

import fx.javafx.dba.BDConnect;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginGuiController implements Initializable{
    public double xOffset = 0;
    public double yOffset = 0;
    private Connection conn = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    /*--------------------------------------*/

    @FXML
    private Button exitButton;
    @FXML
    private PasswordField hasloPassField;
    @FXML
    private TextField loginTextField;
    @FXML
    private Button loginButtonGui;
    @FXML
    private Label loginGUIMessageLabel;
    @FXML
    public Button openAdmin;
    /*--------------------------------------*/
    /*--------------------------------------*/

    public void setExitButton(){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        System.out.println("Zamknięto panel gui");
    }
    /*--------------------------------------*/

    public void setOpenAdmin() throws Exception{
        Stage stage = (Stage) openAdmin.getScene().getWindow();
        Stage stage1 = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fx/javafx/fxml/LoginAdmin.fxml"));
        stage1.setScene(new Scene(root));
        stage1.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed(event1 -> {
            xOffset = event1.getSceneX();
            yOffset = event1.getSceneY();
        });
        root.setOnMouseDragged(event12 -> {
            stage1.setX(event12.getScreenX() - xOffset);
            stage1.setY(event12.getScreenY() - yOffset);
        });
        stage1.show();
        System.out.println("Otwarto okno logowania admina");
        stage.hide();
        System.out.println("Zamknięto panel gui");
    }
    /*--------------------------------------*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conn = BDConnect.fxConnection();
    }
    /*--------------------------------------*/

    @FXML
    private void loginButtonGuiOnAction() throws IOException {
        if (loginTextField.getText().equals(getLogin()) && hasloPassField.getText().equals(getPassword())){
            Stage stage = (Stage) loginButtonGui.getScene().getWindow();
            Stage stage1 = new Stage();
            Parent root1 = FXMLLoader.load(getClass().getResource("/fx/javafx/fxml/WorkMain.fxml"));
            stage1.setScene(new Scene(root1));
            stage1.initStyle(StageStyle.UNDECORATED);
            root1.setOnMousePressed(event1 -> {
                xOffset = event1.getSceneX();
                yOffset = event1.getSceneY();
            });
            root1.setOnMouseDragged(event12 -> {
                stage1.setX(event12.getScreenX() - xOffset);
                stage1.setY(event12.getScreenY() - yOffset);
            });
            stage1.show();
            System.out.println("Otwarto okno pracy");
            stage.hide();
            System.out.println("Zamknięto panel gui");
        }
        else if (loginTextField.getText().isBlank() || hasloPassField.getText().isBlank())
        {
            loginGUIMessageLabel.setText("Brak loginu lub hasła");
        }
        else if (!loginTextField.getText().equals(getLogin()) || !hasloPassField.getText().equals(getPassword())) {
            loginGUIMessageLabel.setText("Wprowadzono złe dane");
            loginTextField.clear();
            hasloPassField.clear();
        }
    }
    /*--------------------------------------*/

    private String getLogin(){
        String login = null;
        try {
            statement = conn.prepareStatement("Select Login from Users where login = ? ");
            statement.setString(1, loginTextField.getText());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                login = resultSet.getString(1);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger(LoginGuiController.class.getName()).log(Level.SEVERE, null, e);
        }
        return login;
    }

    /*--------------------------------------*/
    private String getPassword(){
        String haslo = null;
        try {
            statement = conn.prepareStatement("Select Password from Users where login = ? ");
            statement.setString(1, loginTextField.getText());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                haslo = resultSet.getString(1);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger(LoginGuiController.class.getName()).log(Level.SEVERE, null, e);
        }
        return haslo;
    }
}