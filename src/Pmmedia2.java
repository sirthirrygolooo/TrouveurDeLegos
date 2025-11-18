import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;

public class Pmmedia2 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String imagePath = getParameters().getRaw().get(0);
        Image image = new Image("file:" + imagePath);
        ImageView imageView = new ImageView(image);

        primaryStage.setTitle("LIMAGEU");

        ColorInput colorInput = new ColorInput(0, 0, image.getWidth(), image.getHeight(), Color.RED);
        Blend blend = new Blend();
        blend.setMode(BlendMode.MULTIPLY);
        blend.setTopInput(colorInput);
        imageView.setEffect(blend);

        Timeline colorTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> colorInput.setPaint(Color.RED)),
                new KeyFrame(Duration.seconds(0.5), e -> colorInput.setPaint(Color.YELLOW)),
                new KeyFrame(Duration.seconds(1), e -> colorInput.setPaint(Color.LIME)),
                new KeyFrame(Duration.seconds(1.5), e -> colorInput.setPaint(Color.AQUA)),
                new KeyFrame(Duration.seconds(2), e -> colorInput.setPaint(Color.BLUE)),
                new KeyFrame(Duration.seconds(2.5), e -> colorInput.setPaint(Color.MAGENTA)),
                new KeyFrame(Duration.seconds(3), e -> colorInput.setPaint(Color.RED))
        );
        colorTimeline.setCycleCount(Timeline.INDEFINITE);

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), imageView);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(colorTimeline, rotateTransition);
        parallelTransition.play();

        StackPane root = new StackPane();
        root.getChildren().add(imageView);

        Scene scene = new Scene(root, image.getWidth(), image.getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
