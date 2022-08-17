package fx.javafx.Controllers;

import fx.javafx.dba.BDConnect;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginAdminController{
    public double xOffset = 0;
    public double yOffset = 0;
    private Connection conn = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    /*--------------------------------------*/
    @FXML
    public Button LoginAdminButton;
    @FXML
    private TextField adminLoginText;
    @FXML
    private PasswordField adminPasswordField;
    @FXML
    private Label loginADMINMessageLabel;
    @FXML
    private Button exitPbutton;
    @FXML
    private Button backButton;
    @FXML
    private TextField ok;

    /*--------------------------------------*/
    @FXML
    private void initialize(){
        conn = BDConnect.fxConnection();
    }
    /*--------------------------------------*/
    /*--------------------------------------*/

    public void setExitPbutton() {
        Stage stage = (Stage) exitPbutton.getScene().getWindow();
        stage.close();
        System.out.println("Zamknięto panel logowania admina");
    }
    /*--------------------------------------*/

    @FXML
    public void LoginAdminButtonOnAction() throws IOException {
        if (adminLoginText.getText().equals(getAdminLogin()) && adminPasswordField.getText().equals(getAdminPassword())) {
            adminLoginText.clear();
            adminPasswordField.clear();
            Stage stage2 = (Stage) LoginAdminButton.getScene().getWindow();
            Stage stage3 = new Stage();
            Parent root3 = FXMLLoader.load(getClass().getResource("/fx/javafx/fxml/AdminPanel.fxml"));
            stage3.setScene(new Scene(root3));
            stage3.initStyle(StageStyle.UNDECORATED);
            root3.setOnMousePressed(event1 -> {
                xOffset = event1.getSceneX();
                yOffset = event1.getSceneY();
            });
            root3.setOnMouseDragged(event12 -> {
                stage3.setX(event12.getScreenX() - xOffset);
                stage3.setY(event12.getScreenY() - yOffset);
            });
            stage3.show();
            System.out.println("Poprawne logowanie");
            System.out.println("Otwarto panel admina");
            stage2.hide();
            System.out.println("Zamknięto okno logowania admina");
        }
        else if (adminLoginText.getText().isBlank() || adminPasswordField.getText().isBlank()) {
            loginADMINMessageLabel.setText("Brak loginu lub hasła");
        }
        else if (!adminLoginText.getText().equals(getAdminLogin()) || !adminPasswordField.getText().equals(getAdminPassword())) {
            adminLoginText.clear();
            adminPasswordField.clear();
            loginADMINMessageLabel.setText("Wprowadzono złe dane");
        }
    }
        /*--------------------------------------*/

        private String getAdminLogin() {
            String alogin = null;
            try {
                statement = conn.prepareStatement("Select Login from Users where login = ? and RoleID = 'A'");
                statement.setString(1, adminLoginText.getText());
                resultSet = statement.executeQuery();
                if (resultSet.next())
                    alogin = resultSet.getString(1);
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                Logger.getLogger(LoginAdminController.class.getName()).log(Level.SEVERE, null, e);
                return null;
            }
            return alogin;
        }
        /*--------------------------------------*/

        private String getAdminPassword() {
            String ahaslo = null;
            try {
                statement = conn.prepareStatement("Select Password from Users where login = ? and RoleID = 'A'");
                statement.setString(1, adminLoginText.getText());
                resultSet = statement.executeQuery();
                if (resultSet.next())
                    ahaslo = resultSet.getString(1);
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                Logger.getLogger(LoginAdminController.class.getName()).log(Level.SEVERE, null, e);
                return null;
            }
            return ahaslo;
        }
    public void backButtonAction() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        Stage stage1 = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fx/javafx/fxml/LoginGui.fxml")));
        stage1.setScene(new Scene(root));
        stage1.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage1.setX(event.getScreenX() - xOffset);
            stage1.setY(event.getScreenY() - yOffset);
        });
        stage1.show();
        System.out.println("Powrót do LoginGUI");
        stage.close();
        System.out.println("Wyłączono panel logowania admina");
    }

}
