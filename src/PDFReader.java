import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PDFReader {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        File file = new File("labelForJava.pdf");
        PDDocument document = Loader.loadPDF(file);

        long loadTime = System.currentTimeMillis();
        //System.out.println("PDF loaded in " + (loadTime - startTime) + " milliseconds");

        PDFRenderer renderer = new PDFRenderer(document);
        int x = 190;
        int y = 114;
        int width = 217;
        int height = 266;

        BufferedImage fullImage = renderer.renderImage(0, 1.0f, ImageType.RGB);

        long renderTime = System.currentTimeMillis();
        //System.out.println("PDF rendered in " + (renderTime - loadTime) + " milliseconds");


        BufferedImage croppedImage = cropImage(fullImage, x, y, width, height);
        long cropTime = System.currentTimeMillis();
        //System.out.println("Image cropped in " + (cropTime - renderTime) + " milliseconds");

        ImageIO.write(croppedImage, "JPEG", new File("croppedImage.jpg"));

        long saveTime = System.currentTimeMillis();
        //System.out.println("Image saved in " + (saveTime - cropTime) + " milliseconds");

        int x2 = 31;
        int y2 = 296;
        int width2 = 523;
        int height2 = 310;

        BufferedImage labelFull = renderer.renderImage(1, 1.0f, ImageType.ARGB);
        BufferedImage croppedLabel = cropImage(labelFull, x2, y2, width2, height2);
        ImageIO.write(croppedLabel, "PNG", new File("label.png"));

        // Create a new PDF document
        PDDocument newDocument = new PDDocument();

        // Create a new page
        PDPage page = new PDPage();
        page.setRotation(90);
        newDocument.addPage(page);

        // Create a content stream for the page
        PDPageContentStream contentStream = new PDPageContentStream(newDocument, page);


        PDImageXObject pdLabel = LosslessFactory.createFromImage(newDocument, croppedLabel);

        // Draw the image onto the page
        contentStream.drawImage(pdLabel, 50, 50); // Adjust the coordinates as needed
        // Close the content stream
        contentStream.close();

        // Save the new PDF document
        newDocument.save("output.pdf");

        // Close the new PDF document
        newDocument.close();

        // Close the original PDF document
        document.close();


        document.close();

        long endTime = System.currentTimeMillis();
        System.out.println("Total time taken: " + (endTime - startTime) + " milliseconds");

    }

    private static BufferedImage cropImage(BufferedImage originalImage, int x, int y, int width, int height) {

        if (x < 0 || y < 0 || x + width > originalImage.getWidth() || y + height > originalImage.getHeight()) {
            throw new IllegalArgumentException("Invalid crop dimensions");
        }
        return originalImage.getSubimage(x, y, width, height);
    }
}