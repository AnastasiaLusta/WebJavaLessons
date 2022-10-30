package step.learning.services;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class MimeService {
    private final Map<String, String> imageTypes;

    public MimeService() {
        imageTypes = new HashMap<>(); // create HashMap of different image types and their MIME types
        imageTypes.put(".bmp", "image/bmp");
        imageTypes.put(".gif", "image/gif");
        imageTypes.put(".jpg", "image/jpeg");
        imageTypes.put(".jpeg", "image/jpeg");
        imageTypes.put(".png", "image/png");
    }

    /**
     * Checks if extension given corresponds to image type
     *
     * @param extension file extension with dot char '.png'
     * @return boolean
     */
    public boolean isImage(String extension) {
        return imageTypes.containsKey(extension); // check if imageTypes contains the extension
    }

    /**
     * Define MIME type by file extension
     *
     * @param extension file extension with dot char '.png'
     * @return MIME type or null if extension not registered
     */
    public String getMimeByExtension(String extension) {
        if (imageTypes.containsKey(extension)) {
            return imageTypes.get(extension); // return MIME type of image
        }
        return null;
    }
}
