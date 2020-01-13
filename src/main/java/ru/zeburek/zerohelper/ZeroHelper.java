package ru.zeburek.zerohelper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.zeburek.zerohelper.controllers.AnalyticsController;
import ru.zeburek.zerohelper.controllers.FXMLDocumentController;
import ru.zeburek.zerohelper.controllers.ResourcesController;
import ru.zeburek.zerohelper.controlls.Alert;
import ru.zeburek.zerohelper.providers.IdentificationProvider;
import ru.zeburek.zerohelper.providers.UpdateProvider;
import ru.zeburek.zerohelper.tray.TrayIconProvider;
import ru.zeburek.zerohelper.utils.RegistryController;
import ru.zeburek.zerohelper.utils.Version;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ZeroHelper extends Application {
    public volatile static Version VERSION = new Version();

    public volatile static String HOME_DIR = System.getProperty("user.home");
    public volatile static String SETTINGS_FILE =
            HOME_DIR + File.separator + "AppData" + File.separator + "Local" +
                    File.separator + "THelper" + File.separator + "THelper.properties";
    public volatile static String APP_DATA_DIR = HOME_DIR + "/AppData/Local/THelper";
    public volatile static String VER_ID = Version.VERSION;
    public volatile static boolean ENABLE_DEBUG = false;

    private final static IdentificationProvider ID_PROV = new IdentificationProvider(APP_DATA_DIR);
    public final static UpdateProvider UP_PROVIDER = new UpdateProvider(VER_ID,APP_DATA_DIR);
    public final static String UUID = ID_PROV.getIdentificator();
    public final static AnalyticsController AN_SEND = new AnalyticsController();
    public final static ResourcesController RESOURCES_CONTROLLER = new ResourcesController();
    public final static RegistryController REGISTRY = new RegistryController();
    public static TrayIconProvider TRAY_ICON;

    @Override
    public void start(Stage stage) throws Exception {
        Runtime.getRuntime().addShutdownHook(exitThread());

        //UP_PROVIDER.updateIfNeeded();
        //if(!UP_PROVIDER.isUpToDate()){UP_PROVIDER.getUpdatedSource();}

        FXMLLoader loader = new FXMLLoader(ZeroHelper.class.getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        FXMLDocumentController controller = loader.<FXMLDocumentController>getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("ru/zeburek/zerohelper/css/text-area-background.css");

        stage.getIcons().add(new Image(ZeroHelper.class.getResourceAsStream("Logo_ZH.png")));
        stage.setTitle("Zero Helper");
        stage.setScene(scene);
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            controller.handleOnWindowShown(getHostServices(),stage,getParameters());
        });
        stage.setOnCloseRequest((event) -> {
            controller.thsp.saveSettings();
            controller.killAdbServer();
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AN_SEND.trackStatistic("MainWindow", "Closed");
            ID_PROV.saveUUID();
            if(!RESOURCES_CONTROLLER.removeAllFilesExists()){
                Alert alert =
                        new Alert(
                                javafx.scene.control.Alert.AlertType.INFORMATION,
                                "Error",
                                "Something went wrong",
                                "Couldn't remove all temp files, take attention",
                                ButtonType.OK);
                alert.showAndWait();
            }
            Platform.exit();
            System.exit(0);
        });
//        onlyOneInstanceCanBeStarted(stage,controller,getParameters());
        TRAY_ICON = new TrayIconProvider(stage, controller);
        stage.show();

        AN_SEND.trackStatistic("MainWindow", "Shown");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } catch (Throwable ex){
            exitThread(ex);
        }
    }

    // handler listener
    private Thread exitThread(){
        return new Thread(() -> {
            try {

            }catch (Throwable ex){
                exitThread(ex);
            }
        });

    }
    // handler listener
    private Thread exitThread(Throwable ex){
        return new Thread(() -> {
            AN_SEND.trackStatistic("Application", "Error", "Crash occurred: "+ex.getMessage());
        });

    }
}
