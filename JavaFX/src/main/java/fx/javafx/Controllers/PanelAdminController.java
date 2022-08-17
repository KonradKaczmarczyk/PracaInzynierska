package fx.javafx.Controllers;

import fx.javafx.dba.BDConnect;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanelAdminController {
    public double xOffset = 0;
    public double yOffset = 0;
    private Connection conn = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    /*--------------------------------------*/

    @FXML
    private Button CreateAccountButton;
    @FXML
    private TextField NewLog;
    @FXML
    private PasswordField NewPass;
    @FXML
    private PasswordField ChangePass;
    @FXML
    private Button ChangePassword;
    @FXML
    private Button aExitButton;
    @FXML
    public Label panelAdminMessage;
    @FXML
    private Label panelAdminMessage1;
    @FXML
    private ComboBox<String> ComboBoxPerm;
    @FXML
    private ComboBox<String> ComboBoxLog;
    @FXML
    private Button logOutButton;
    /*--------------------------------------*/
    @FXML
    private void initialize(){
        conn = BDConnect.fxConnection();
        ComboBoxPerm.setItems(FXCollections.observableArrayList(getInfoPermFromDB()));
        ComboBoxLog.setItems(FXCollections.observableArrayList(getInfoLogFromDB()));
    }
    @FXML
    private void initComboBoxLogAgain(){
        ComboBoxLog.setItems(FXCollections.observableArrayList(getInfoLogFromDB()));
    }
    /*--------------------------------------*/
    /*--------------------------------------*/

    private List<String> getInfoPermFromDB(){
        List<String> permopt = new ArrayList<>();
        try{
            String query = "Select RoleName From Role";
            statement = conn.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                permopt.add(resultSet.getString("RoleName"));
            }
            statement.close();
            resultSet.close();
            return permopt;
        }
        catch (SQLException e) {
            Logger.getLogger(PanelAdminController.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }
    /*--------------------------------------*/

    private List<String> getInfoLogFromDB(){
        List<String> logopt = new ArrayList<>();
        try{
            String query = "Select Login From Users";
            statement = conn.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                logopt.add(resultSet.getString("Login"));
            }
            statement.close();
            resultSet.close();
            return logopt;
        }
        catch (SQLException e) {
            Logger.getLogger(PanelAdminController.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }
    /*--------------------------------------*/
    @FXML
   public void createUserOnAction() {
        String selected = ComboBoxPerm.getSelectionModel().getSelectedItem();
        String newloguser = NewLog.getText();
        String newpassuser = NewPass.getText();
        String permA = "A";
        String permU = "U";
        String query = "Insert into Users (Login, Password, RoleID) values (?,?,?)";
        if (Objects.equals(selected, "Administrator") && !NewLog.getText().isBlank()&&NewLog.getText().length()>5 &&
                !NewPass.getText().isBlank()&&NewPass.getText().length()>5 && !NewLog.getText().equals(checkLogin())) {
            try {
                statement = conn.prepareStatement(query);
                statement.setString(1, newloguser);
                statement.setString(2, newpassuser);
                statement.setString(3, permA);
                statement.executeUpdate();
                panelAdminMessage.setText("Sukces!");
                System.out.println("Użytkownika o uprawnieniach administratora dodano poprawnie");
                statement.close();
                initComboBoxLogAgain();
                NewLog.clear();
                NewPass.clear();
            } catch (SQLException e) {
                Logger.getLogger(LoginAdminController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        else if (Objects.equals(selected, "Pracownik")&& !NewLog.getText().isBlank()&&NewLog.getText().length()>5 &&
                !NewPass.getText().isBlank()&&NewPass.getText().length()>5 && !NewLog.getText().equals(checkLogin())){
            try {
                statement = conn.prepareStatement(query);
                statement.setString(1, newloguser);
                statement.setString(2, newpassuser);
                statement.setString(3, permU);
                statement.executeUpdate();
                panelAdminMessage.setText("Sukces!");
                System.out.println("Użytkownika o uprawnieniach pracownika dodano poprawnie");
                statement.close();
                initComboBoxLogAgain();
                NewLog.clear();
                NewPass.clear();

            } catch (SQLException e) {
                Logger.getLogger(LoginAdminController.class.getName()).log(Level.SEVERE, null, e);
            }

        }
        else if (selected == null || NewLog.getText().isBlank() || NewPass.getText().isBlank() ){
            panelAdminMessage.setText("Brakujące informacje");
            System.out.println("Brak danych");
        }
        else if ((selected.equals("Administrator") || selected.equals("Pracownik")) && NewLog.getText().length()<5 && NewPass.getText().length()<5){
            panelAdminMessage.setText("Login/hasło jest za krótkie");

        }
        else {
            panelAdminMessage.setText("Takie konto już istnieje");
            NewLog.clear();
            NewPass.clear();
            System.out.println("zdublowane dane");
        }
        }
    /*--------------------------------------*/

    private String checkLogin() {
        String clogin = null;
        try {
            statement = conn.prepareStatement("Select Login from Users where login = ?");
            statement.setString(1, NewLog.getText());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                clogin = resultSet.getString(1);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger(LoginAdminController.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return clogin;
    }
    /*--------------------------------------*/
    @FXML
    public void changePassOnAction() {
        String selected2 = ComboBoxLog.getSelectionModel().getSelectedItem();
        String chpassuser = ChangePass.getText();
        String query2 = "Update Users set Password=? where Login=?";
        if (!(selected2 == null) && !ChangePass.getText().isBlank() && ChangePass.getText().length()>5){
            try {statement = conn.prepareStatement(query2);
                statement.setString(1, chpassuser);
                statement.setString(2, selected2);
                statement.executeUpdate();
                panelAdminMessage1.setText("Zmiana hasła");
                System.out.println("Pomyślna zmiana hasła");
                statement.close();
                initComboBoxLogAgain();
                ChangePass.clear();
            } catch (SQLException e) {
                Logger.getLogger(PanelAdminController.class.getName()).log(Level.SEVERE, null, e);
            }

        }
        else if (selected2 == null || ChangePass.getText().isBlank()){
            panelAdminMessage1.setText("Niepełne dane");
        }
        else if (ChangePass.getText().length()<5){
            panelAdminMessage1.setText("Hasło za krótkie");
        }
        else {
            panelAdminMessage1.setText("Coś nie tak");
        }
    }


    public void aExitButton() {
        Stage stage = (Stage) aExitButton.getScene().getWindow();
        stage.close();
        System.out.println("Zamknięto panel admina");
    }

    public void logOutButtonOnAction() throws IOException {
        Stage stage = (Stage) logOutButton.getScene().getWindow();
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
        System.out.println("Wyłączono panel admina");
    }



}
