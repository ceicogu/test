package com.qihao.toy.console.web.module.user.screen;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

/**
 * <img alt="条码" src="${pageContext.request.contextPath}/barCode?keycode=RT100200300400"></img>
 * @Description: 生成条码（CODE128格式）
 *
 */
public class BarCode1DServlet extends HttpServlet {

    /**
     * @Fields serialVersionUID : default serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private static final String KEY = "keycode";
    private static final String WIDTH = "mwidth";
    private static final String HEIGHT = "mheight";
    private static final String IMAGETYPE = "JPEG";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keycode = req.getParameter(KEY);
        if (keycode != null && !"".equals(keycode)) {
            ServletOutputStream stream = null;
            try {
                Code128Writer writer = new Code128Writer();
                int width=180;
                int height=60;
                String mwidth = req.getParameter(WIDTH);
                if (mwidth != null && !"".equals(mwidth.trim())) {
                    try{
                        width=Integer.valueOf(mwidth);
                    } catch (NumberFormatException e) {
                        //TODO output to log
                    }
                }
                String mheight = req.getParameter(HEIGHT);
                if (mheight != null && !"".equals(mheight.trim())) {
                    try{
                        height = Integer.valueOf(mheight);
                    } catch (NumberFormatException e) {
                        //TODO output to log
                    }
                }
                stream = resp.getOutputStream();
                BitMatrix m = writer.encode(keycode, BarcodeFormat.CODE_128, width, height);
                MatrixToImageWriter.writeToStream(m, IMAGETYPE, stream);
            } catch (WriterException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    stream.flush();
                    stream.close();
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.doGet(req, resp);
    }



}