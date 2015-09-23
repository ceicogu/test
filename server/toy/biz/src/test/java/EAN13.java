import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

/**
 * 条形码
 *
 * @author dong.j
 * @date 2014-2-25 下午5:19:44
 * @version 0.1.0
 */
public class EAN13 {

    /**
     * 编码
     * @param contents
     * @param width
     * @param height
     * @param imgPath
     */
    public void encode(String contents, int width, int height, String imgPath) {
        int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.EAN_13, codeWidth, height,
                    null);
            MatrixToImageWriter.writeToFile(bitMatrix, "png", new File(imgPath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解码
     * @param imgPath
     * @return
     */
    public String decode(String imgPath) {
        BufferedImage image = null;
        Result result = null;
        try {
            image = ImageIO.read(new File(imgPath));
            if (image == null) {
                System.out.println("the decode image may be not exit.");
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            result = new MultiFormatReader().decode(bitmap, null);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String imgPath = "/tmp/zxing_EAN133.png";
        // 益达无糖口香糖的条形码
        String contents = "6923450657713";

        //int width = 105, height = 50;
        int width = 203, height = 100;
        EAN13 handler = new EAN13();
        handler.encode(contents, width, height, imgPath);
        System.out.println("Michael ,you have finished zxing EAN13 encode.");
        String c = handler.decode(imgPath);
        System.out.println(c);
    }
}