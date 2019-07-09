package com.ocr;

import com.asprise.ocr.Ocr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@RestController
public class MainController {



    @Value("${files.dir}")
    private String baseDir;



    @GetMapping
    public ResponseEntity upload(
            @RequestParam MultipartFile file
    ) throws IOException {
        byte[] bytes = file.getBytes();
        System.out.println("file bytes = "+ bytes.length);

        File input = new File(baseDir);
        file.transferTo(input);
        BufferedImage image = ImageIO.read(input);

        BufferedImage result = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D graphic = result.createGraphics();
        graphic.drawImage(image, 0, 0, Color.WHITE, null);
        graphic.dispose();

        File output = new File(baseDir+"java-duke-black-white.png");
        ImageIO.write(result, "png", output);
//
//        Tesseract tesseract = new Tesseract();  // JNA Interface Mapping
//        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping
////        instance.setDatapath("tessdata"); // path to tessdata directory
//
//
//            String s = tesseract.doOCR(output);
//            System.out.println(s);


        Ocr.setUp();
        Ocr ocr = new Ocr();
        ocr.startEngine("eng", Ocr.SPEED_FASTEST);
        String s = ocr.recognize(new File[]{new File(baseDir + "java-duke-black-white.png")}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_PLAINTEXT);
        return new ResponseEntity("file bytes = "+bytes.length+"\n "+s,HttpStatus.OK);
    }
}
