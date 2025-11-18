import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Fuuusionn extends Application {
    @Override
    public void start(Stage stage) {
        Image im1 = new Image(
                "https://i1.rgstatic.net/ii/profile.image/272481147682825-1441975976698_Q128/Gilles-Perrot.jpg"
        );

        Image im2 = new Image(
                "https://media.licdn.com/dms/image/v2/C4E03AQFB-e_1q9qUAg/profile-displayphoto-shrink_800_800/profile-displayphoto-shrink_800_800/0/1517428553790?e=1763596800&v=beta&t=5iwg4Z10MoNWG7qmplmO4Vt1FqNNu1_Hic3udrmyNOs"
        );

        ImageView bottom = new ImageView(im1);
        ImageView top = new ImageView(im2);
        top.setBlendMode(BlendMode.DIFFERENCE);

        Group blend = new Group(
                bottom,
                top
        );

        HBox layout = new HBox(10);
        layout.getChildren().addAll(
                new ImageView(im1),
                blend,
                new ImageView(im2)
        );
        layout.setPadding(new Insets(10));
        stage.setScene(new Scene(layout));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}