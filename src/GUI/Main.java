package GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Main extends Application
{

    public static Stage window;
    private static final String stylePath = "GUI/style/";

    @Override
    public void start(Stage window) throws Exception
    {
        Platform.setImplicitExit(false);
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        window.getIcons().add(new Image(stylePath + "knifeGoose.png"));
        window.initStyle(StageStyle.TRANSPARENT);

        Scene windowScene = new Scene(root, 400, 230);
        window.setResizable(false);
        window.setScene(windowScene);

        window.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent t)
            {
                if (Controller.getInstance() != null)
                {
                    Controller.getInstance().closeApp();
                } else
                {
                    Platform.exit();
                    System.exit(0);
                }
            }
        });
        window.show();
    }

    public static void main(String[] args)
    {
        launch(args);
        Controller.getInstance().initialize();
    }
}
