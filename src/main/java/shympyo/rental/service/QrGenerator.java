package shympyo.rental.service;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class QrGenerator {

    public static byte[] toPng(String content, int size) {
        try {
            var hints = Map.of(EncodeHintType.MARGIN, 1);
            BitMatrix m = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < size; y++)
                for (int x = 0; x < size; x++)
                    img.setRGB(x, y, m.get(x, y) ? 0x000000 : 0xFFFFFF);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "PNG", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String enterUrl(String publicBaseUrl, String placeCode) {
        // 예: https://app.example.com/api/enter-code?c=PLACE-A-001
        return publicBaseUrl + "/api/enter-code?c=" + URLEncoder.encode(placeCode, StandardCharsets.UTF_8);
    }

}
