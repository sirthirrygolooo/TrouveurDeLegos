import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.NATIVE_LIBRARY_NAME;


public class HistoDemoGL extends Application {

    static {
        System.loadLibrary(NATIVE_LIBRARY_NAME);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        String imagePath = getParameters().getRaw().get(0);
        Image inputImage = new Image("file:" + imagePath);
        Mat ocvImage = imageToMat(inputImage);

        //image --> niveaux de gris
        Imgproc.cvtColor(ocvImage, ocvImage, Imgproc.COLOR_BGR2GRAY);

        Mat histImage = new Mat();
        // Calcul de l'histogramme
        Mat hist = new Mat();
        Imgproc.calcHist(java.util.Collections.singletonList(ocvImage), new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(0, 256));
        // Création de l'image de l'histogramme
        histImage = Mat.zeros(400, 512, CvType.CV_8UC3);
        for (int i = 1; i < 256; i++) {
            Imgproc.line(histImage,
                    new org.opencv.core.Point((i - 1) * 2, 400 - Math.round(hist.get(i - 1, 0)[0])),
                    new org.opencv.core.Point(i * 2, 400 - Math.round(hist.get(i, 0)[0])),
                    new org.opencv.core.Scalar(255, 255, 255),2);
        }

        Image histoImageFX = mat2Image(histImage);
        ImageView inputImageView = new ImageView(mat2Image(ocvImage));
        ImageView histoImageView = new ImageView(histoImageFX);

        // Création d'une mise en page pour placer les éléments ImageView
        HBox imageBox = new HBox(10);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPadding(new Insets(10));
        imageBox.getChildren().addAll(inputImageView, histoImageView);

        // Création de la scène et ajout de la mise en page
        Scene scene = new Scene(new StackPane(imageBox));
        // Configuration de la fenêtre principale
        primaryStage.setTitle("Histogramme d'une image");
        primaryStage.setScene(scene);
        primaryStage.show();
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
            System.err.println("Cannot convert the Mat obejct: " + e);
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

    /**
     * Support for the {@link mat2image()} method
     *
     * @param original
     *            the {@link Mat} object in BGR or grayscale
     * @return the corresponding {@link BufferedImage}
     */
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