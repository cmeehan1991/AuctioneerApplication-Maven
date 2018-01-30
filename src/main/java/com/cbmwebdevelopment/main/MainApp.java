package com.cbmwebdevelopment.main;

import static com.cbmwebdevelopment.main.Values.IS_SIGNED_IN;
import static com.cbmwebdevelopment.main.Values.OS;
import com.cbmwebdevelopment.menus.CustomMenuBar;
import com.sibvisions.rad.ui.javafx.ext.FXToolBarRT39866;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXDesktopPane;
import java.security.SecureRandom;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Group group;
    public static FXToolBarRT39866 toolbar;
    public static final String ERROR_LABEL = "-fx-text-fill:#ff0000";
    public static FXDesktopPane desktopPane;
    @Override
    public void start(Stage stage) throws Exception {
        if (IS_SIGNED_IN) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainFXML.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            MainFXMLController controller = (MainFXMLController) loader.getController();
            desktopPane = controller.desktopPane;
            toolbar = controller.toolbar;     

            CustomMenuBar customMenuBar = new CustomMenuBar();
            customMenuBar.group = group;
            MenuBar menuBar = customMenuBar.menuBar();
            root.getChildren().add(0, menuBar);
            menuBar.toFront();
            if (OS.contains("mac")) {
                menuBar.setUseSystemMenuBar(true);
            } else {
                AnchorPane.setLeftAnchor(menuBar, 0.0);
                AnchorPane.setRightAnchor(menuBar, 0.0);
            }

            Scene scene = new Scene(root);
            scene.setFill(Color.DARKGRAY);
            stage.setTitle("Auctioneer");
            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest((value) -> {
                Platform.exit();
            });
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserSignInFXML.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);            
            
            stage.setTitle("User Sign In | Auctioneer");
            stage.setScene(scene);
            stage.show();
        }
    }

    public static String randomPasswordGenerator(int length) {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()_";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(rnd.nextInt(characters.length())));
        }
        return sb.toString();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
