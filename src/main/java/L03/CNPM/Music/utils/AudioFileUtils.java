package L03.CNPM.Music.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AudioFileUtils {

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws Exception {
        File tempFile = File.createTempFile("temp", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

    public boolean isValidAudioFile(String fileType, String fileName) {
        List<String> validMimeTypes = Arrays.asList(
                "audio/mpeg", // .mp3
                "audio/wav", // .wav
                "audio/x-wav", // .wav
                "audio/aac", // .aac
                "video/mp4" // .mp4
        );

        List<String> validExtensions = Arrays.asList(
                ".mp3", ".wav", ".aac", ".mp4");

        if (fileType == null || !validMimeTypes.contains(fileType.toLowerCase())) {
            return false;
        }

        if (fileName != null) {
            String fileExtension = getFileExtension(fileName);
            return validExtensions.contains(fileExtension.toLowerCase());
        }

        return false;
    }

    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        return (lastIndex != -1) ? fileName.substring(lastIndex) : "";
    }

}
