package GUI;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import threadManager.ThreadController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    private AnchorPane pane;

    @FXML
    private HBox titleBar;

    @FXML
    private ImageView minimizeButton;

    @FXML
    private ImageView closeButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField portField;

    @FXML
    private Text statusText;

    @FXML
    private ImageView toggleButton;

    @FXML
    private Text errorMessage;

    private Stage window;

    private final Mouse mouse = new Mouse();

    private boolean isTrayIconExist = false;

    private static final String stylePath = "GUI/style/";

    private static int port = 5020;

    private static final int DEFAULTPORT = 5020;

    private String bufErrorMessage = null;

    ThreadController thController = null;

    Thread connectionAcceptTh = new Thread();

    private static Controller thisController = null;

    public Controller() {
        thController = new ThreadController();
        thisController = this;
    }

    @FXML
    private void onCloseClicked(MouseEvent event) {
        window = (Stage) (closeButton).getScene().getWindow();
        if (thController.getIsServerToggledOff())
        {
            closeApp();
        }
        else
        {
            window.hide();
            if (!isTrayIconExist) {
                javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
                isTrayIconExist = true;
            }
        }
    }

    @FXML
    private void onMinimizeClicked(MouseEvent event) {
        ((Stage) (minimizeButton).getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void onToggleSwitch(MouseEvent event) {
        toggleSwitch();
    }

    public static Controller getInstance() {
        return thisController;
    }

    public String getBufErrorMessage() {
        return bufErrorMessage;
    }

    public void setBufErrorMessage(String buff) {
        bufErrorMessage = buff;
    }

    public void showErrorMessage(String error) {
        if (errorMessage == null) {
            bufErrorMessage = error;
            return;
        }
        errorMessage.setText(error);
        errorMessage.setVisible(true);
    }

    public void showErrorMessage(String error, Paint paint) {
        errorMessage.setFill(paint);
        showErrorMessage(error);
    }

    //Modified source https://gist.github.com/jonyfs/b279b5e052c3b6893a092fed79aa7fbe#file-javafxtrayiconsample-java-L86
    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                closeApp();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            File imageFile = new File("src/" + stylePath + "trayIconSmallAngry.png");
            java.awt.Image image = ImageIO.read(imageFile);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // if the user selects the default menu item (which includes the app name),
            // show the main app stage.
            java.awt.MenuItem openItem = new java.awt.MenuItem("Pop up");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            // toggle logging via tray, add item to popup menu, change it's displayed label
            java.awt.MenuItem toggleItem = new java.awt.MenuItem("Turn off");
            toggleItem.addActionListener(event -> { toggleSwitch(); });

            //Tray icon listener. Used to show tray label correctly - toggle off when switched on and vise versa
            trayIcon.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    if (thController.getIsServerToggledOff())
                    {
                        toggleItem.setLabel("Turn on");
                    }
                    else
                    {
                        toggleItem.setLabel("Turn off");
                    }
                }
            });

            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                tray.remove(trayIcon);
                closeApp();
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(toggleItem);
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // add the application tray icon to the system tray.
            tray.add(trayIcon);
        } catch (AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    private void showStage() {
        if (window != null) {
            window.show();
            window.toFront();
        }
    }

    public void closeApp() {
        try {
            thController.sendClose();
        }catch (IOException e){
            showErrorMessage("Selector is null not initialized. And could not close connection correctly");
        }
        Platform.exit();
        System.exit(0);
    }

    public void onTurnedOn(){
        statusText.setFill(Paint.valueOf("#9de05c"));
        statusText.setText("Turn off");
        toggleButton.setImage(new Image(stylePath + "turnOnButtonSmall.png"));
        nameField.setDisable(true);
        if (!portField.getText().equals(""))
            port = Integer.parseInt(portField.getText());
        else{
            port = DEFAULTPORT;
        }
        portField.setDisable(true);
    }

    public void onTurnedOff(){
        statusText.setFill(Paint.valueOf("#f8902f"));
        statusText.setText("Turn on");
        toggleButton.setImage(new Image(stylePath + "turnOffButtonSmall.png"));
        nameField.setDisable(false);
        portField.setDisable(false);
    }

    private void toggleSwitch()
    {
        if (thController.getIsServerToggledOff()) {
            onTurnedOn();
            Thread.UncaughtExceptionHandler h = (th, ex) -> {
                //try {
                try {
                    thController.sendClose();
                }catch (IOException e){
                    showErrorMessage("Selector is invalid. And closing connection failed");
                }
                showErrorMessage("Can't launch server: no internet connection");
                onTurnedOff();
            };
            connectionAcceptTh = new Thread() {
                @Override
                public void run() {
                    try{
                    thController.launchService(port);
                    } catch (PrettyException e) {
                        showErrorMessage(e.getPrettyMessage());
                        try {
                            thController.closeService();
                        } catch (IOException ioException) {
                            showErrorMessage(e.getPrettyMessage() + "\n" + "Also closing connection failed");
                        }
                        //System.out.println(e.toString());
                        //I hate this thing =(
                        //Was stuck here for hours
                        throw new RuntimeException();
                    }catch (RuntimeException e) {
                        showErrorMessage(e.getMessage());
                        try {
                            thController.closeService();
                        } catch (IOException ioException) {
                            showErrorMessage(e.getMessage() + "\n" + "Also closing connection failed");
                        }
                        //System.out.println(e.toString());
                        //I hate this thing =(
                        //Was stuck here for hours
                        throw new RuntimeException();
                    }
                }
            };
            connectionAcceptTh.setUncaughtExceptionHandler(h);
            connectionAcceptTh.start();
        }
        else {
            onTurnedOff();
            try {
                thController.sendClose();
            }catch (IOException e){
                showErrorMessage("Selector is invalid. And closing connection failed");
            }
        }
    }

    public boolean isContain (MouseEvent mouseEvent, ImageView image) {
        if (mouseEvent.getX() >= image.getBoundsInParent().getCenterX() - (image.getFitWidth() / 2) && mouseEvent.getX() <= image.getBoundsInParent().getCenterX() + (image.getFitWidth() / 2) && mouseEvent.getY() >= image.getBoundsInParent().getCenterY() - (image.getFitHeight() / 2) && mouseEvent.getY() <= image.getBoundsInParent().getCenterY() + (image.getFitHeight() / 2)) {
            return true;
        }
        return false;
    }

    public void initialize()
    {
        titleBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (isContain(t, minimizeButton) || isContain(t, closeButton)) {
                    mouse.setX(-1);
                    mouse.setY(-1);
                }
                else {
                    mouse.setX(t.getX());
                    mouse.setY(t.getY());
                }
            }
        });

        titleBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (mouse.getX() != -1) {
                    titleBar.getScene().getWindow().setX(t.getScreenX() - mouse.getX());
                    titleBar.getScene().getWindow().setY(t.getScreenY() - mouse.getY());
                }
            }
        });

        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                errorMessage.setVisible(false);
            }
        });
    }
}