package GUI;

import DBManager.DBManager;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import threadManager.ThreadController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

public class Controller
{
    @FXML
    private AnchorPane pane;

    @FXML
    private HBox titleBar;

    @FXML
    private ImageView minimizeButton;

    @FXML
    private ImageView closeButton;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Text statusText;

    @FXML
    private ImageView toggleButton;

    @FXML
    private Text statusMessage = new Text();

    private Stage window;

    private final Mouse mouse = new Mouse();

    private boolean isTrayIconExist = false;

    private static final String stylePath = "GUI/style/";

    ThreadController thController = null;

    Thread connectionAcceptTh = new Thread();

    private static Controller thisController = null;

    private ReentrantLock errorLogLock = new ReentrantLock();

    public Controller()
    {
        thController = new ThreadController();
        thisController = this;

        try
        {
            FileOutputStream outputStream = new FileOutputStream("serverLog.txt", false);
            outputStream.write("".getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        }
        catch (IOException e)
        {
            statusMessage.setText("Can't write to log file.");
            statusMessage.setVisible(true);
        }
    }

    @FXML
    private void onCloseClicked(MouseEvent event)
    {
        window = (Stage) (closeButton).getScene().getWindow();
        if (thController.getIsServerToggledOff())
        {
            closeApp();
        } else
        {
            window.hide();
            if (!isTrayIconExist)
            {
                javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
                isTrayIconExist = true;
            }
        }
    }

    @FXML
    private void onMinimizeClicked(MouseEvent event)
    {
        ((Stage) (minimizeButton).getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void onToggleSwitch(MouseEvent event)
    {
        toggleSwitch();
    }

    public static Controller getInstance()
    {
        return thisController;
    }

    public String getLogin()
    {
        if (loginField != null)
        {
            return loginField.getText();
        } else
        {
            showStatusMessage("Oops! Login is null");
            return null;
        }
    }

    public String getPassword()
    {
        return passwordField.getText();
    }

    public void showStatusMessage(String error)
    {
        showStatusMessage(error, Paint.valueOf("#f8902f"));
        errorLogLock.lock();
        try
        {
            FileOutputStream outputStream = new FileOutputStream("serverLog.txt", true);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd',' HH:mm:ss ");
            String singleRecord = LocalDateTime.now().format(formatter) + "\t" + error + "\n";
            outputStream.write(singleRecord.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        }
        catch(IOException e)
        {
        }
        errorLogLock.unlock();
    }

    public void showStatusMessage(String message, Paint paint)
    {
        if (statusMessage != null)
        {
            statusMessage.setFill(paint);
            statusMessage.setText(message);
            statusMessage.setVisible(true);
        }
    }

    //Modified source https://gist.github.com/jonyfs/b279b5e052c3b6893a092fed79aa7fbe#file-javafxtrayiconsample-java-L86
    private void addAppToTray()
    {
        try
        {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported())
            {
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
            toggleItem.addActionListener(event ->
            {
                toggleSwitch();
            });

            //Tray icon listener. Used to show tray label correctly - toggle off when switched on and vise versa
            trayIcon.addMouseMotionListener(new MouseAdapter()
            {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e)
                {
                    if (thController.getIsServerToggledOff())
                    {
                        toggleItem.setLabel("Turn on");
                    } else
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
            exitItem.addActionListener(event ->
            {
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
        } catch (AWTException | IOException e)
        {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    private void showStage()
    {
        if (window != null)
        {
            window.show();
            window.toFront();
        }
    }

    public void closeApp()
    {
        try
        {
            thController.sendClose();
        } catch (IOException e)
        {
            showStatusMessage("Selector is null not initialized. And could not close connection correctly");
        }
        Properties.serializeProperties();
        Platform.exit();
        System.exit(0);
    }

    //GUI changes when big orange button is pressed
    public void onTurnedOn()
    {
        statusText.setFill(Paint.valueOf("#9de05c"));
        statusText.setText("Turn off");
        toggleButton.setImage(new Image(stylePath + "turnOnButtonSmall.png"));
        DropShadow greenShadow = new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(157, 224, 92, 0.5), 10, 0, 0, 0);
        toggleButton.setEffect(greenShadow);
        loginField.setDisable(true);
        passwordField.setDisable(true);
    }

    //GUI changes when big green button is pressed
    public void onTurnedOff()
    {
        statusText.setFill(Paint.valueOf("#f8902f"));
        statusText.setText("Turn on");
        toggleButton.setImage(new Image(stylePath + "turnOffButtonSmall.png"));
        DropShadow orangeShadow = new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(221, 157, 102, 0.5), 10, 0, 0, 0);
        toggleButton.setEffect(orangeShadow);
        loginField.setDisable(false);
        passwordField.setDisable(false);
    }

    private void toggleSwitch()
    {
        if (thController.getIsServerToggledOff())
        {
            try
            {
                DBManager manager = new DBManager();
            }
            catch (Exception e)
            {
                Controller.getInstance().showStatusMessage("Test connection to DB failed.");
                return;
            }
            onTurnedOn();
            Thread.UncaughtExceptionHandler h = (th, ex) ->
            {
                try
                {
                    thController.sendClose();
                    showStatusMessage("Uncaught error");
                } catch (IOException e)
                {
                    showStatusMessage("Uncaught error\nClosing connection failed");
                }
                onTurnedOff();
            };
            connectionAcceptTh = new Thread()
            {
                @Override
                public void run()
                {
                    int port;
                    if (Properties.getInstance().isPortValid())
                    {
                        port = Properties.getInstance().getPort();
                    }
                    else
                    {
                        port = Properties.getDEFAULTPORT();
                    }
                    try
                    {
                        thController.launchService(port);
                    } catch (PrettyException e)
                    {
                        showStatusMessage(e.getPrettyMessage());
                        try
                        {
                            thController.sendClose();
                            onTurnedOff();
                        } catch (IOException exception)
                        {
                            showStatusMessage(e.getPrettyMessage() + "\n" + "Also closing connection failed");
                        } catch (Exception unusualException)
                        {
                            throw new RuntimeException();
                        }
                        //System.out.println(e.toString());
                        //I hate this thing =(
                        //Was stuck here for hours
                        //throw new RuntimeException();
                    }
                }
            };
            connectionAcceptTh.setUncaughtExceptionHandler(h);
            connectionAcceptTh.start();
        } else
        {
            onTurnedOff();
            try
            {
                thController.sendClose();
            } catch (IOException e)
            {
                showStatusMessage("Selector is invalid. And closing connection failed");
            }
        }
    }

    public boolean isContain(MouseEvent mouseEvent, ImageView image)
    {
        if (mouseEvent.getX() >= image.getBoundsInParent().getCenterX() - (image.getFitWidth() / 2) && mouseEvent.getX() <= image.getBoundsInParent().getCenterX() + (image.getFitWidth() / 2) && mouseEvent.getY() >= image.getBoundsInParent().getCenterY() - (image.getFitHeight() / 2) && mouseEvent.getY() <= image.getBoundsInParent().getCenterY() + (image.getFitHeight() / 2))
        {
            return true;
        }
        return false;
    }

    public void initialize()
    {
        titleBar.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
                if (isContain(t, minimizeButton) || isContain(t, closeButton))
                {
                    mouse.setX(-1);
                    mouse.setY(-1);
                } else
                {
                    mouse.setX(t.getX());
                    mouse.setY(t.getY());
                }
            }
        });

        titleBar.setOnMouseDragged(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
                if (mouse.getX() != -1)
                {
                    titleBar.getScene().getWindow().setX(t.getScreenX() - mouse.getX());
                    titleBar.getScene().getWindow().setY(t.getScreenY() - mouse.getY());
                }
            }
        });

        pane.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
                statusMessage.setVisible(false);
            }
        });

        Properties.deserializeProperties();
        loginField.setText(Properties.getInstance().getLogin());
    }
}