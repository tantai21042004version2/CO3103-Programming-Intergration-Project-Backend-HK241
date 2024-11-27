package L03.CNPM.Music.utils;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class ValidationUtils {
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\d{6,}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return phoneNumber != null && pattern.matcher(phoneNumber).matches();
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 3;
    }

    public String convertAndUpperCase(String input) {
        if (input == null)
            return null;
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        String ascii = normalized.replaceAll("[^\\p{ASCII}]", "");
        return ascii.replaceAll("\\s+", "").toUpperCase();
    }
}
