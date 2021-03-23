package com.mbld.jigsly.service;

import com.mbld.jigsly.constant.ImageConstant;
import com.mbld.jigsly.model.puzzle.PuzzleImage;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@Service
public class PuzzleImageService {

    private final HashMap<String, PuzzleImage> encodedImages;

    public PuzzleImageService() throws IOException {
        encodedImages = new HashMap<>();

        loadDefaultImages();
    }
    private void loadDefaultImages() throws IOException {
        for(String imageName : ImageConstant.DEFAULT_IMAGES){
            encodedImages.put(imageName, createDefaultPuzzleImage(imageName));
        }
    }

    private PuzzleImage createDefaultPuzzleImage(String imageName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("default_images/"+imageName+".jpg");
        BufferedImage image = ImageIO.read(inputStream);
        String base64 = encodeImageToBase64(image);

        return new PuzzleImage("data:image/jpeg;base64, "+base64, image.getWidth(), image.getHeight());
    }

    private String encodeImageToBase64(BufferedImage bufferedImage) throws IOException {
        byte[] imageBytes;
        try(ByteArrayOutputStream os = new ByteArrayOutputStream()){
            ImageIO.write(bufferedImage, "jpg", os);
            imageBytes = os.toByteArray();
        }

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public PuzzleImage createPuzzleImageFromBase64(String base64, int width, int height){
        return new PuzzleImage(base64, width, height);
    }

    public PuzzleImage getPuzzleImage(String name){
        return encodedImages.get(name);
    }

    private String getImageBase64(String name){
        return encodedImages.get(name).getImageBase64();
    }

    public List<String> getDefaultImages() {
        ArrayList<String> images = new ArrayList<>();
        for(String imageName : ImageConstant.DEFAULT_IMAGES){
            images.add(getImageBase64(imageName));
        }
        return images;
    }
}
