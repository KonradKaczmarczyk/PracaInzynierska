package fx.javafx.Controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InfoOrderController {

    @FXML
    private Button buttonOKInfo;

    @FXML
    void buttonOKInfoOnAction() {
        Stage stage = (Stage) buttonOKInfo.getScene().getWindow();
        stage.close();

    }

}
