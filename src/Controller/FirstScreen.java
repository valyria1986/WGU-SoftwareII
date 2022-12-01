package Controller;

import helper.UsersQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller class for FirstScreen.fxml
 * implements 2 lambda expressions for simplified login attempts and simplified alert box creation.
 * */
public class FirstScreen implements Initializable {
    /** Lambda Expression #1
     * Simplifies calling of the log activity text for recording login attempts from multiple calls.
     */
    LogActivity logActivity = () -> {
        return "login_activity.txt";
    };
    /** Lambda Expression #2
     * simplifies creation of alert boxes due to the multitude of alerts required in this program.
     * */
     Alerts alertBox = (title, message) -> {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();

        return alert;
    };
    @FXML
    private Label timeZoneLabel;
    @FXML
    private Label timeZone;
    @FXML
    private Label UsernameLabel;
    @FXML
    private Label PasswordLabel;
    @FXML
    private Button LoginButton;
    @FXML
    private Button ResetButton;
    @FXML
    private Label LanguageLabel;
    @FXML
    private TextField usernameText;
    @FXML
    private PasswordField usernamePassword;

    /**
     * @param url URL to resolve relative paths
     * @param resourceBundle ResourceBundle to localize root object
     * initializes scene with either French text or English text based on users local settings.
     * */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Calendar now = Calendar.getInstance();
        TimeZone zone = now.getTimeZone();
        System.out.println("I am initialized!!!");
        ResourceBundle rb = ResourceBundle.getBundle("Main/RBMain", Locale.getDefault());
        if (Locale.getDefault().getLanguage().equals("fr") || Locale.getDefault().getLanguage().equals("en")){
            UsernameLabel.setText(rb.getString("username"));
            PasswordLabel.setText(rb.getString("password"));
            LanguageLabel.setText(rb.getString("language"));
            LoginButton.setText(rb.getString("login"));
            ResetButton.setText(rb.getString("reset"));
            timeZone.setText(rb.getString("timeZone"));
            timeZoneLabel.setText(zone.getDisplayName());
            createFile();
        }
    }
    /**
     * @param actionEvent ActionEvent activates on Button Login
     * Queries the Users database to check if the entered Username and Password match and exist
     * If Username and Password match and exist closes current scene and opens scene HomeScreen.fxml
     * If no match is found ERROR alert is provided.
     * */
    public void onLoginAction(ActionEvent actionEvent) {
        try {
            if (UsersQuery.login(usernameText.getText(), usernamePassword.getText())) {
                loginSuccess();
                Stage primaryStage = (Stage) LoginButton.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/View/HomeScreen.fxml"));
                primaryStage.setTitle("Appointment Management System Home Screen");
                primaryStage.setScene(new Scene(root, 600, 300));
                primaryStage.show();

            }
            else{
                loginFailure();
                ResourceBundle rb = ResourceBundle.getBundle("Main/RBMain", Locale.getDefault());
                alertBox.alertBox(rb.getString("invalidLogin"), rb.getString("invalidUser"));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        }
    /**
     * @param actionEvent ActionEvent activates on Button Reset
     * Clears Username and Password text boxes.
     * */
    public void onResetAction(ActionEvent actionEvent) {
        usernameText.setText("");
        usernamePassword.setText("");
    }
    /**
     * creates a login_activity.txt if the file does not exist.
     * */
    private void createFile(){
        try {
            File newfile = new File(logActivity.getFileName());
            if (newfile.createNewFile()) {
                System.out.println("File created:" + newfile.getName());
            } else {
                System.out.println("File already exists. Location: "+ newfile.getPath());
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Upon successful login records username, password, and current time of login in login_activity.txt
     * */
    private void loginSuccess() {

        try {
            FileWriter fileWriter = new FileWriter(logActivity.getFileName(), true);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            fileWriter.write("Successful Login: Username=" + usernameText.getText() + " Password=" + usernamePassword.getText() + " Timestamp: " + simpleDateFormat.format(date) + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Upon failure to login records the username and password used and the time of failed login in login_activity.txt
     * */
    private void loginFailure() {
        try {
            FileWriter fileWriter = new FileWriter(logActivity.getFileName(), true);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            fileWriter.write("Failed Login: Username=" + usernameText.getText() + " Password=" + usernamePassword.getText() + " Timestamp: " + simpleDateFormat.format(date) + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

