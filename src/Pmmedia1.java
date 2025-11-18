import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Stack;

public class Pmmedia1 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        int NOMBRE = 5;
        String imagePath = getParameters().getRaw().get(0);
        Image image = new Image("file:" + imagePath);
        ImageView imageView = new ImageView(image);
        primaryStage.setTitle("LIMAGEU");
        StackPane root = new StackPane();
        root.getChildren().add(imageView);

        Bloom blur = new Bloom();
        blur.setThreshold(30);
        imageView.setEffect(blur);



        Scene scene = new Scene(root, image.getWidth(), image.getHeight());
        primaryStage.setScene(scene) ;
        primaryStage.show();
    }
    public static void main(String[] args) {launch(args);}
}