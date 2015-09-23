package com.qihao.toy.biz.utils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * ZXing工具类
 * -----------------------------------------------------------------------------------------------------------------------
 * 首页--https://code.google.com/p/zxing
 * 介绍--用于解析多种格式条形码(EAN-13)和二维码(QRCode)的开源Java类库,其提供了多种应用的类库,如javase/jruby/cpp/csharp/android
 * 说明--下载到的ZXing-2.2.zip是它的源码,我们在JavaSE中使用时需用到其core和javase两部分
 * 可直接引入它俩的源码到项目中,或将它俩编译为jar再引入,这是我编译好的：http://download.csdn.net/detail/jadyer/6245849
 * -----------------------------------------------------------------------------------------------------------------------
 * 经测试:用微信扫描GBK编码的中文二维码时出现乱码,用UTF-8编码时微信可正常识别
 * 并且MultiFormatWriter.encode()时若传入hints参数来指定UTF-8编码中文时,微信压根就不识别所生成的二维码
 * 所以这里使用的是这种方式new String(content.getBytes("UTF-8"), "ISO-8859-1")
 * -----------------------------------------------------------------------------------------------------------------------
 * 将logo图片加入二维码中间时,需注意以下几点
 * 1)生成二维码的纠错级别建议采用最高等级H,这样可以增加二维码的正确识别能力(我测试过,不设置级别时,二维码工具无法读取生成的二维码图片)
 * 2)头像大小最好不要超过二维码本身大小的1/5,而且只能放在正中间部位,这是由于二维码本身结构造成的(你就把它理解成图片水印吧)
 * 3)在仿照腾讯微信在二维码四周增加装饰框,那么一定要在装饰框和二维码之间留出白边,这是为了二维码可被识别
 * -----------------------------------------------------------------------------------------------------------------------
 *
 * @version v1.0
 * @history v1.0-->方法新建,目前仅支持二维码的生成和解析,生成二维码时支持添加logo头像
 */
public class ZXingUtils {
    private ZXingUtils() {
    }

    /**
     * 设置 logo
     *
     * @param matrixImage 源二维码图片
     * @return 返回带有logo的二维码图片
     * @throws IOException
     * @author
     */
    public static BufferedImage LogoMatrix(BufferedImage matrixImage, String logoPath) throws IOException {
        /**
         * 读取二维码图片，并构建绘图对象
         */
        Graphics2D graphics = matrixImage.createGraphics();

        int matrixWidth = matrixImage.getWidth();
        int matrixHeigh = matrixImage.getHeight();

        /**
         * 读取Logo图片
         */
        BufferedImage logo = ImageIO.read(new File(logoPath));

        //开始绘制图片
        graphics.drawImage(logo, matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, null);//绘制
        BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(stroke);// 设置笔画对象
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, 20, 20);
        graphics.setColor(Color.white);
        graphics.draw(round);// 绘制圆弧矩形

        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(stroke2);// 设置笔画对象
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth / 5 * 2 + 2, matrixHeigh / 5 * 2 + 2, matrixWidth / 5 - 4, matrixHeigh / 5 - 4, 20, 20);
        graphics.setColor(new Color(128, 128, 128));
        graphics.draw(round2);// 绘制圆弧矩形

        graphics.dispose();
        matrixImage.flush();
        return matrixImage;
    }

    /**
     * 设置 code
     *
     * @param matrixImage 源条形码图片
     * @return 返回带有code的条形码图片
     * @throws IOException
     * @author
     */
    public static BufferedImage codeMatrix(BufferedImage matrixImage, String code) throws IOException {
        /**
         * 读取条形码图片，并构建绘图对象
         */
        int matrixWidth = matrixImage.getWidth();
        int matrixHeigh = matrixImage.getHeight();

        final Font mFont = new Font("宋体", Font.PLAIN, 20);

        //获取font的样式应用在str上的整个矩形
        Rectangle2D r = mFont.getStringBounds(code, new FontRenderContext(AffineTransform.getScaleInstance(1, 1), false, false));
        int unitHeight = (int) Math.floor(r.getHeight());//获取单个字符的高度
        //获取整个str用了font样式的宽度这里用四舍五入后+1保证宽度绝对能容纳这个字符串作为图片的宽度
        int strWidth = (int) Math.round(r.getWidth()) + 1;
        int strHeight = unitHeight + 1;//把单个字符的高度+1保证高度绝对能容纳字符串作为图片的高度
        //创建图片
//        BufferedImage image=new BufferedImage(strWidth,strHeight,BufferedImage.TYPE_INT_BGR);
//        Graphics g=image.getGraphics();
//        g.setColor(Color.WHITE);
//        g.fillRect(0, 0, strWidth, strHeight);//先用白色填充整张图片,也就是背景
//        g.setColor(Color.black);//在换成黑色
//        g.setFont(mFont);//设置画笔字体
//        g.drawString(code, 0, mFont.getSize());//画出字符串
//        g.dispose();

        int logoX = (matrixWidth - strWidth) / 2; //设置logo图片的位置,这里令其居中


        Graphics2D graphics = matrixImage.createGraphics();
        graphics.fillRect(logoX, matrixHeigh - strHeight, matrixWidth - logoX, matrixHeigh);
        graphics.setColor(new Color(102, 102, 102)); //设字体为黑色,否则就是白色
        graphics.setFont(mFont);

        graphics.drawString(code, logoX, matrixHeigh); //绘制

        graphics.dispose();
        matrixImage.flush();
        return matrixImage;
    }


    /**
     * 为二维码图片增加logo头像(其原理类似于图片加水印)
     *
     * @param imagePath 二维码图片存放路径(含文件名)
     * @param logoPath  logo头像存放路径(含文件名)
     */
    private static void overlapImage(String imagePath, String logoPath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        int logoWidth = image.getWidth() / 5; //设置logo图片宽度为二维码图片的五分之一
        int logoHeight = image.getHeight() / 5; //设置logo图片高度为二维码图片的五分之一
        int logoX = (image.getWidth() - logoWidth) / 2; //设置logo图片的位置,这里令其居中
        int logoY = (image.getHeight() - logoHeight) / 2; //设置logo图片的位置,这里令其居中
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(ImageIO.read(new File(logoPath)), logoX, logoY, logoWidth, logoHeight, null);
        graphics.dispose();
        ImageIO.write(image, imagePath.substring(imagePath.lastIndexOf(".") + 1), new File(imagePath));
    }


    /**
     * 生成二维码
     *
     * @param content   二维码内容
     * @param charset   编码二维码内容时采用的字符集(传null时默认采用UTF-8编码)
     * @param imagePath 二维码图片存放路径(含文件名)
     * @param width     生成的二维码图片宽度
     * @param height    生成的二维码图片高度
     * @param logoPath  logo头像存放路径(含文件名,若不加logo则传null即可)
     * @return 生成二维码结果(true or false)
     */
    public static boolean encodeQRCodeImage(String content, String charset, String imagePath, int width, int height, String logoPath) {
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        //指定编码格式
        //hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //指定纠错级别(L--7%,M--15%,Q--25%,H--30%)
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 内容所使用字符集编码
        hints.put(EncodeHintType.CHARACTER_SET, charset == null ? "UTF-8" : charset);
//		hints.put(EncodeHintType.MAX_SIZE, 350);//设置图片的最大值
//	    hints.put(EncodeHintType.MIN_SIZE, 100);//设置图片的最小值
        hints.put(EncodeHintType.MARGIN, 1);//设置二维码边的空度，非负数

        //编码内容,编码类型(这里指定为二维码),生成图片宽度,生成图片高度,设置参数
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    new String(content.getBytes(charset == null ? "UTF-8" : charset), "ISO-8859-1"), //要编码的内容
                    BarcodeFormat.QR_CODE,
                    width, //条形码的宽度
                    height,//条形码的宽度
                    hints);//生成条形码时的一些配置,此项可选
        } catch (Exception e) {
            System.out.println("编码待生成二维码图片的文本时发生异常,堆栈轨迹如下");
            e.printStackTrace();
            return false;
        }
        //生成的二维码图片默认背景为白色,前景为黑色,但是在加入logo图像后会导致logo也变为黑白色,至于是什么原因还没有仔细去读它的源码
        //所以这里对其第一个参数黑色将ZXing默认的前景色0xFF000000稍微改了一下0xFF000001,最终效果也是白色背景黑色前景的二维码,且logo颜色保持原有不变
        MatrixToImageConfig config = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
        //这里要显式指定MatrixToImageConfig,否则还会按照默认处理将logo图像也变为黑白色(如果打算加logo的话,反之则不须传MatrixToImageConfig参数)
        try {
            Path path = FileSystems.getDefault().getPath(imagePath);
            MatrixToImageWriter.writeToPath(bitMatrix, imagePath.substring(imagePath.lastIndexOf(".") + 1), path, config);// 输出图像
            //MatrixToImageWriter.writeToFile(bitMatrix, imagePath.substring(imagePath.lastIndexOf(".") + 1), new File(imagePath), config);
        } catch (IOException e) {
            System.out.println("生成二维码图片[" + imagePath + "]时遇到异常,堆栈轨迹如下");
            e.printStackTrace();
            return false;
        }
        //此时二维码图片已经生成了,只不过没有logo头像,所以接下来根据传入的logoPath参数来决定是否加logo头像
        if (null == logoPath) {
            return true;
        } else {
            //如果此时最终生成的二维码不是我们想要的,那么可以扩展MatrixToImageConfig类(反正ZXing提供了源码)
            //扩展时可以重写其writeToFile方法,令其返回toBufferedImage()方法所生成的BufferedImage对象(尽管这种做法未必能解决为题,故需根据实际情景测试)
            //然后替换这里overlapImage()里面的第一行BufferedImage image = ImageIO.read(new File(imagePath));
            //即private static void overlapImage(BufferedImage image, String imagePath, String logoPath)
            try {
                //这里不需要判断logoPath是否指向了一个具体的文件,因为这种情景下overlapImage会抛IO异常
                overlapImage(imagePath, logoPath);
                return true;
            } catch (IOException e) {
                System.out.println("为二维码图片[" + imagePath + "]添加logo头像[" + logoPath + "]时遇到异常,堆栈轨迹如下");
                e.printStackTrace();
                return false;
            }
        }
    }


    /**
     * 解析二维码
     *
     * @param imagePath 二维码图片存放路径(含文件名)
     * @param charset   解码二维码内容时采用的字符集(传null时默认采用UTF-8编码)
     * @return 解析成功后返回二维码文本, 否则返回空字符串
     */
    public static String decodeQRCodeImage(String imagePath, String charset) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if (null == image) {
            System.out.println("Could not decode QRCodeImage");
            return "";
        }
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, charset == null ? "UTF-8" : charset);
        Result result = null;
        try {
            result = new MultiFormatReader().decode(bitmap, hints);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("二维码图片[" + imagePath + "]解析失败,堆栈轨迹如下");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean encodeEAN13CodeImage(String content, String imagePath, int width, int height) {
        int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.EAN_13, codeWidth, height,
                    null);
            MatrixToImageWriter.writeToFile(bitMatrix, "png", new File(imagePath));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean encodeEAN13CodeImage(String content,String imagePath, int width, int height, boolean displayCode) {

        int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.EAN_13, codeWidth, height,
                    null);

            if (displayCode) {
                BufferedImage bufferedImage = codeMatrix(MatrixToImageWriter.toBufferedImage(bitMatrix), content);
                ImageIO.write(bufferedImage, "png", new File(imagePath));
            } else {
//                Path path = FileSystems.getDefault().getPath(imagePath);
//                MatrixToImageWriter.writeToPath(bitMatrix, "png", path);// 输出图像
                MatrixToImageWriter
                        .writeToFile(bitMatrix, "png", new File(imagePath));
            }
        } catch (Exception e) {
            System.out.println("编码待生成条形码图片的文本时发生异常,堆栈轨迹如下");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static String decodeEAN13CodeImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if (null == image) {
            System.out.println("Could not decode QRCodeImage");
            return "";
        }
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap, null);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("条形图片[" + imagePath + "]解析失败,堆栈轨迹如下"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        //
//        String contents = "http://182.92.162.223/api/account/getMyToyFriends.json?toy_user_id=37&auth_token=QablbSYU%2BmtaEkauWDg6dtLj%2FtyKg%2Fxl9Hpsm922608%3D\n";
//        encodeQRCodeImage(contents, "UTF-8", "/tmp/myQRCodeImage.png", 300, 300, null);
//        System.out.println(decodeQRCodeImage("/tmp/myQRCodeImage.png", null));

        //条形码
        String imgPath = "/tmp/zxing_EAN133.png";
        // 益达无糖口香糖的条形码
        String content = "6923450657713";

        //int width = 200, height = 100;
        int width = 205, height = 100;
        encodeEAN13CodeImage(content,  imgPath, width, height);

        System.out.println("Michael ,you have finished zxing EAN13 encode.");
        //条形码解析
        System.out.println(decodeEAN13CodeImage(imgPath));

        encodeEAN13CodeImage(content,  "/tmp/zxing_EAN13_code.png", width, height, true);
    }
}