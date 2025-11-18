import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.opencv.core.Core.NATIVE_LIBRARY_NAME;

public class TrouveurDeLegooos extends Application {

    static {
        System.loadLibrary(NATIVE_LIBRARY_NAME);
    }

    public Image inputImage;
    public Image outputImage;
    ImageView inputImageView;
    ImageView outputImageView;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Récupération du chemin vers l'image depuis les arguments du programme
        String imagePath = getParameters().getRaw().get(0);

        // Chargement de l'image au format PNG (ou autre)
        inputImage = new Image("file:" + imagePath, 600, 0, true, true);
        outputImage = new Image("file:" + imagePath, 600, 0, true, true);
        outputImageView = new ImageView(outputImage);
        //outputImageView.setFitWidth(600);
        outputImageView.preserveRatioProperty().set(true);

        inputImageView = new ImageView(inputImage);
        //inputImageView.setFitWidth(600);
        inputImageView.preserveRatioProperty().set(true);

        // Création d'une mise en page pour placer les éléments ImageView
        HBox imageBox = new HBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPadding(new Insets(10));
        imageBox.setSpacing(10);
        imageBox.getChildren().addAll(inputImageView, outputImageView);

        // Création de la scène et ajout de la mise en page
        Scene scene = new Scene(imageBox);

        //mouse event
        //Creating the mouse event handler
        EventHandler<javafx.scene.input.MouseEvent> eventHandler = new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent e) {
                System.out.println("Mouse event detected");
                System.out.println("X : "+e.getX()+"\nY : "+e.getY());
                Color color = inputImageView.getImage().getPixelReader().getColor((int) e.getX(), (int) e.getY());
                System.out.println("Color : "+inputImageView.getImage().getPixelReader().getColor((int) e.getX(), (int) e.getY()));
                outputImage = ouSontLesLegos(inputImage, color);
                outputImageView.setImage(outputImage);
            }
        };

        inputImageView.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandler);
        inputImageView.setSmooth(true);

        primaryStage.setTitle("IMAGEU CHOISIR COULEUR");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Image ouSontLesLegos(Image imageEntree, javafx.scene.paint.Color couleurBase) {
        Mat image = imageToMat(imageEntree);
        Mat masque = new Mat();
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

        int r = (int)(255 * couleurBase.getRed());
        int g = (int)(255 * couleurBase.getGreen());
        int b = (int)(255 * couleurBase.getBlue());

        float[] hsl = RgbToHsl(r, g, b);
        System.out.println("RGB: "+r+","+g+","+b);
        System.out.println("HSL: "+hsl[0]+","+hsl[1]+","+hsl[2]);

        Scalar hsvColor = rgbToHsv(r, g, b);
        double h = hsvColor.val[0];
        double s = hsvColor.val[1];
        double v = hsvColor.val[2];

        Scalar limBasse = new Scalar(
                Math.max(h - 10, 0),
                Math.max(s - 50, 0),
                Math.max(v - 50, 0)
        );
        Scalar limHaute = new Scalar(
                Math.min(h + 10, 180),
                Math.min(s + 50, 255),
                Math.min(v + 50, 255)
        );
        Core.inRange(image, limBasse, limHaute, masque);

        // pourcentage -> on compte les pixels pas noirs dans le masque
        int nonZeroPixels = Core.countNonZero(masque);
        double percentage = (nonZeroPixels * 100.0) / masque.total();
        System.out.println("pourcentage de px :"+ percentage);

        return mat2Image(masque);
    }

    /**
     * Convertit une couleur RVB en TSL (HSL).
     *
     * @param r Composante rouge (0-255)
     * @param g Composante verte (0-255)
     * @param b Composante bleue (0-255)
     * @return Un tableau de float contenant [H, S, L] où H est en degrés [0, 360),
     *         S et L sont en pourcentage [0, 100]
     */
    public static float[] RgbToHsl(int r, int g, int b) {
        float rf = r / 255f;
        float gf = g / 255f;
        float bf = b / 255f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float h, s, l;
        l = (max + min) / 2f;

        if (delta == 0) {
            h = 0;
            s = 0;
        } else {
            s = delta / (1 - Math.abs(2 * l - 1));

            if (max == rf) {
                h = 60 * (((gf - bf) / delta) % 6);
            } else if (max == gf) {
                h = 60 * (((bf - rf) / delta) + 2);
            } else {
                h = 60 * (((rf - gf) / delta) + 4);
            }
        }

        if (h < 0) {
            h += 360;
        }

        return new float[]{h, s * 100, l * 100};
    }

    private static Scalar rgbToHsv(int r, int g, int b) {
        Mat rgbMat = new Mat(1, 1, CvType.CV_8UC3, new Scalar(b, g, r));
        Mat hsvMat = new Mat();
        Imgproc.cvtColor(rgbMat, hsvMat, Imgproc.COLOR_BGR2HSV);
        return new Scalar(hsvMat.get(0, 0));
    }

    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame
     *            the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    public static Image mat2Image(Mat frame)
    {
        try
        {
            return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
        }
        catch (Exception e)
        {
            System.err.println("Cannot convert the Mat object: " + e);
            return null;
        }
    }

    /**
     * Convert a Image FX object in the corresponding Mat object (OpenCV)
     * @param image the javaFx image to be converted
     * @return the opencv Mat representation of the image
     */
    public static Mat imageToMat(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] buffer = new byte[width * height * 4];

        PixelReader reader = image.getPixelReader();
        WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
        reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        mat.put(0, 0, buffer);
        return mat;
    }

    private static BufferedImage matToBufferedImage(Mat original)
    {
        // init
        BufferedImage image = null;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1)
        {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        }
        else
        {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Veuillez spécifier le chemin vers l'image en argument.");
            System.exit(0);
        }
        launch(args);
    }
}

