package fx.javafx.Controllers;

import fx.javafx.ListRow.ListRow;
import fx.javafx.dba.SGTConnect;
import fx.javafx.dba.BDConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class WorkMainController {
    public double xOffset = 0;
    public double yOffset = 0;
    private Connection connectBD = null;
    private Connection conn = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private ObservableList<ListRow> dane = FXCollections.observableArrayList();
    String idOrderChecker = null;
    public int towartowar = 1;
    /*--------------------------------------*/
    /*--------------------------------------*/
    @FXML
    private Button ClosePackage;

    @FXML
    private TextField IdOrder;

    @FXML
    private Button WExitButton;

    @FXML
    private Button WLogOutButton;

    @FXML
    private Label workMessage;

    @FXML
    private Label workMessage1;

    @FXML
    private TableView<ListRow> tableProductSGT;

    @FXML
    private TableColumn<?, ?> ob_Ilosc;

    @FXML
    private TableColumn<?, ?> tw_Nazwa;

    @FXML
    private TableColumn<?, ?> tw_Opis;

    @FXML
    private TableColumn<?, ?> tw_Symbol;

    @FXML
    private TextArea tableCheckProduct;

    @FXML
    private TextField IdProduct;

    @FXML
    private TextField finalNumberField;

    @FXML
    private TextField numberField;
    /*--------------------------------------*/
    /*--------------------------------------*/
    @FXML
    private void initialize() {
        conn = SGTConnect.sgtConnection();
        connectBD = BDConnect.fxConnection();
        System.out.println("udane połączenie");
        ClosePackage.setDisable(true);
    }
    /*--------------------------------------*/
    /*--------------------------------------*/
    private void setCellTable() {
        tw_Symbol.setCellValueFactory(new PropertyValueFactory<>("nrTowaru"));
        ob_Ilosc.setCellValueFactory(new PropertyValueFactory<>("ilTowaru"));
        tw_Opis.setCellValueFactory(new PropertyValueFactory<>("miejsceTowaru"));
        tw_Nazwa.setCellValueFactory(new PropertyValueFactory<>("nazwaTowaru"));
    }

    /*--------------------------------------*/
    public void closePackageOnAction(){
        String order = IdOrder.getText();
        java.util.Date date=new java.util.Date();
        java.sql.Date sqlDate=new java.sql.Date(date.getTime());
        java.sql.Timestamp sqlTime=new java.sql.Timestamp(date.getTime());
        String queryOrder = "Insert into LogInfo (ID_ORDER, DATE, TIME) values (?,?,?)";
        try {
            statement = connectBD.prepareStatement(queryOrder);
            statement.setString(1, order);
            statement.setDate(2, sqlDate);
            statement.setTimestamp(3, sqlTime);
            statement.executeUpdate();
            statement.close();
            connectBD.close();
        } catch (SQLException e) {
            Logger.getLogger(WorkMainController.class.getName()).log(Level.SEVERE, null, e);
        }
        System.out.println("wysłano log");

        IdOrder.clear();
        IdOrder.setDisable(false);
        IdProduct.clear();
        IdProduct.setDisable(false);
        workMessage.setText("");
        workMessage1.setText("");
        tableProductSGT.getItems().clear();
        tableCheckProduct.clear();
        finalNumberField.clear();
        numberField.clear();
        initialize();
        towartowar = 1;
    }
    /*--------------------------------------*/
    public void WExitButton() {
        Stage stage = (Stage) WExitButton.getScene().getWindow();
        stage.close();
        System.out.println("Zamknięto aplikacje");

    }
    /*--------------------------------------*/
    public void WLogOutButton() throws IOException {
        Stage stage = (Stage) WLogOutButton.getScene().getWindow();
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
        System.out.println("Wyłączono panel pakowanie paczek");
    }

    /*--------------------------------------*/
    @FXML
    public void checkOrder() {
        String query = "SELECT [dok_NrPelnyOryg] FROM [Sklep].[dbo].[dok__Dokument] WHERE dok_NrPelnyOryg= ?";
        try {
            statement = conn.prepareStatement(query);
            statement.setString(1, IdOrder.getText());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                idOrderChecker = resultSet.getString(1);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger(LoginGuiController.class.getName()).log(Level.SEVERE, null, e);
        }

        if (Objects.equals(IdOrder.getText(), "")) {
            workMessage.setText("Nie podano nr zamówienia");
        } else if (Objects.equals(idOrderChecker, IdOrder.getText())) {
            System.out.println("Zamówienie istnieje w bazie SGT");
            workMessage.setText("Pobrano z bazy SGT");
            dane = FXCollections.observableArrayList();
            loadDataFromDBSGT();
            setCellTable();
            getNumberOfItemsAllFromOrder();
            finalNumberField.setText(String.valueOf(getNumberOfItemsAllFromOrder()));
            IdOrder.setDisable(true);
        } else {
            workMessage.setText("Brak w bazie danych");
            IdOrder.clear();
        }
    }
    /*--------------------------------------*/

    private void loadDataFromDBSGT() {
        String query2 = """
                SELECT [Sklep].[dbo].[tw__Towar].[tw_Symbol],
                [Sklep].[dbo].[dok_Pozycja].[ob_Ilosc],
                [Sklep].[dbo].[tw__Towar].[tw_Opis],
                [Sklep].[dbo].[tw__Towar].[tw_Nazwa]
                FROM [Sklep].[dbo].[dok__Dokument]
                LEFT JOIN [Sklep].[dbo].[dok_Pozycja]
                ON [dok_Id]=[Sklep].[dbo].[dok_Pozycja].[ob_DokHanId]
                LEFT JOIN [Sklep].[dbo].[tw__Towar]
                ON [Sklep].[dbo].[dok_Pozycja].[ob_TowId]=[Sklep].[dbo].[tw__Towar].[tw_Id]
                LEFT JOIN [Sklep].[dbo].[fl_Wartosc]
                ON [Sklep].[dbo].[fl_Wartosc].[flw_IdObiektu]=[dok_Id]
                LEFT JOIN [Sklep].[dbo].[pw_Dane]
                ON [Sklep].[dbo].[pw_Dane].[pwd_IdObiektu]=[dok_Id]
                WHERE dok_NrPelnyOryg= ?""";
        try {
            statement = conn.prepareStatement(query2);
            statement.setString(1, IdOrder.getText());
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                dane.addAll(new ListRow(resultSet.getString("tw_Symbol"), resultSet.getInt("ob_Ilosc"), resultSet.getString("tw_Opis"), resultSet.getString("tw_Nazwa")));
            }
            resultSet.close();
            statement.close();
            System.out.println("Pomyślne pobranie danych");

        } catch (SQLException e) {
            Logger.getLogger(WorkMainController.class.getName()).log(Level.SEVERE, null, e);
        }
        tableProductSGT.setItems(dane);

    }
    /*--------------------------------------*/
        @FXML
       public void checkProduct() throws Exception {

            if (IdOrder.getText().isEmpty() ) {
                workMessage.setText("Nie podano nr zamówienia");
            }
            else if (IdProduct.getText().isEmpty()){
                workMessage1.setText("Brak numeru towaru");

            }
            else if (Objects.equals(idOrderChecker, IdOrder.getText()) && !IdProduct.getText().isEmpty() && IdProduct.getText().equals(getItemFromOrder()))

            {
                if (tableCheckProduct.getText().trim().equals(IdProduct.getText())){
                    System.out.println("powtórzenie");
                    workMessage1.setText("Towar już skompletowany");
                    IdProduct.clear();
                }
                else {
                    upNumber();
                    System.out.println("to samo");
                    tableCheckProduct.appendText(IdProduct.getText() + "\n");
                    workMessage1.setText("Poprawnie odczytano");

                    if (getNumberItemFromOrder() > 1) {
                        IdProduct.setDisable(true);
                        openInfo();
                        System.out.println("więcej niż jeden towar");
                    }
                    IdProduct.clear();
                    IdProduct.setDisable(false);
                    checkNumberOfItems();

                }

            }

            else if (Objects.equals(IdProduct.getText(),"")){
                workMessage1.setText("nie podano nr towaru lub zam");
                System.out.println("nie podano nr towaru");
            }
            else {
                System.out.println("to nie to samo");
                workMessage1.setText("Brak towaru w zamówieniu ");
                IdProduct.clear();
            }

    }
    /*--------------------------------------*/
    private String getItemFromOrder(){
            String itemfromsgtforcheck = null;
            String query3 = """
                    SELECT [Sklep].[dbo].[tw__Towar].[tw_Symbol]
                    FROM [Sklep].[dbo].[dok__Dokument]
                    LEFT JOIN [Sklep].[dbo].[dok_Pozycja]
                    ON [dok_Id]=[Sklep].[dbo].[dok_Pozycja].[ob_DokHanId]
                    LEFT JOIN [Sklep].[dbo].[tw__Towar]
                    ON [Sklep].[dbo].[dok_Pozycja].[ob_TowId]=[Sklep].[dbo].[tw__Towar].[tw_Id]
                    WHERE dok_NrPelnyOryg= ? AND tw_Symbol= ?""";
            try {
                statement = conn.prepareStatement(query3);
                statement.setString(1, IdOrder.getText());
                statement.setString(2, IdProduct.getText());
                resultSet = statement.executeQuery();
                if (resultSet.next())
                    itemfromsgtforcheck = resultSet.getString(1);
                resultSet.close();
                statement.close();
            }
            catch (SQLException e) {
                Logger.getLogger(WorkMainController.class.getName()).log(Level.SEVERE, null, e);
                return null;
            }
        return itemfromsgtforcheck;

    }
    /*--------------------------------------*/
    private Integer getNumberItemFromOrder(){
            int numberfromsgtforcheck = 0;
            String query4 = """
                    SELECT [Sklep].[dbo].[dok_Pozycja].[ob_Ilosc]
                    FROM [Sklep].[dbo].[dok__Dokument]
                    LEFT JOIN [Sklep].[dbo].[dok_Pozycja]
                    ON [dok_Id]=[Sklep].[dbo].[dok_Pozycja].[ob_DokHanId]
                    LEFT JOIN [Sklep].[dbo].[tw__Towar]
                    ON [Sklep].[dbo].[dok_Pozycja].[ob_TowId]=[Sklep].[dbo].[tw__Towar].[tw_Id]
                    WHERE dok_NrPelnyOryg= ? AND tw_Symbol= ?\s""";
            try {
                statement = conn.prepareStatement(query4);
                statement.setString(1, IdOrder.getText());
                statement.setString(2, IdProduct.getText());
                resultSet = statement.executeQuery();
                if (resultSet.next())
                    numberfromsgtforcheck = resultSet.getInt(1);
                resultSet.close();
                statement.close();

            } catch (SQLException e) {
                Logger.getLogger(WorkMainController.class.getName()).log(Level.SEVERE, null, e);
                return null;
            }
            return numberfromsgtforcheck;
    }
    /*--------------------------------------*/
    public Integer getNumberOfItemsAllFromOrder(){
        int numberoffitemsfromsgtforcheck = 0;
            String query5 = """
                    SELECT COUNT(*) as count
                    FROM [Sklep].[dbo].[dok__Dokument]
                    LEFT JOIN [Sklep].[dbo].[dok_Pozycja]
                    ON [dok_Id]=[Sklep].[dbo].[dok_Pozycja].[ob_DokHanId]
                    LEFT JOIN [Sklep].[dbo].[tw__Towar]
                    ON [Sklep].[dbo].[dok_Pozycja].[ob_TowId]=[Sklep].[dbo].[tw__Towar].[tw_Id]
                    LEFT JOIN [Sklep].[dbo].[fl_Wartosc]
                    ON [Sklep].[dbo].[fl_Wartosc].[flw_IdObiektu]=[dok_Id]
                    LEFT JOIN [Sklep].[dbo].[pw_Dane]
                    ON [Sklep].[dbo].[pw_Dane].[pwd_IdObiektu]=[dok_Id]
                    WHERE dok_NrPelnyOryg=  ?\s""";
            try {
                statement = conn.prepareStatement(query5);
                statement.setString(1, IdOrder.getText());
                resultSet = statement.executeQuery();
                if (resultSet.next())
                    numberoffitemsfromsgtforcheck = resultSet.getInt(1);
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                Logger.getLogger(WorkMainController.class.getName()).log(Level.SEVERE, null, e);
                return null;
            }
            return numberoffitemsfromsgtforcheck;
            
    }
    /*--------------------------------------*/
    public void openInfo() throws Exception{
        IdProduct.setDisable(true);
        Stage stage = (Stage) IdProduct.getScene().getWindow();
        Stage stage1 = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fx/javafx/fxml/InfoProduct.fxml")));
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
        stage1.showAndWait();
        System.out.println("Otwarto okno informacji");

    }
    /*--------------------------------------*/
    public void upNumber(){
          numberField.getText();
          numberField.clear();
          numberField.setText(String.valueOf(towartowar++));
    }
    /*--------------------------------------*/
    public void checkNumberOfItems() throws Exception {
        if (towartowar > getNumberOfItemsAllFromOrder()){
            System.out.println("Wszystkie towary zeskanowane");
            IdProduct.setDisable(true);
            openInfoOrder();

        }
    }
    /*--------------------------------------*/
    public void openInfoOrder() throws Exception{
        Stage stage = (Stage) numberField.getScene().getWindow();
        Stage stage1 = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fx/javafx/fxml/InfoOrder.fxml")));
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
        stage1.showAndWait();
        System.out.println("Otwarto okno informacji zamówienia");
        IdProduct.setDisable(true);
        ClosePackage.setDisable(false);

    }
        /*--------------------------------------*/
        /*--------------------------------------*/
}
