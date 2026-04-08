package app.utils;

import app.exceptions.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {

    public static String getPropertyValue(String propName, String resourceName)  {
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw ApiException.configuration(String.format("Resource %s was not found", resourceName));
            }
            Properties prop = new Properties();
            prop.load(is);

            String value = prop.getProperty(propName);
            if (value != null) {
                return value.trim();  // Trim whitespace
            } else {
                throw ApiException.configuration(String.format("Property %s not found in %s", propName, resourceName));
            }
        } catch (IOException ex) {
            throw ApiException.internal(String.format("Could not read property %s.", propName));
        }
    }

    public static String getOptionalPropertyValue(String propName, String resourceName) {
        try {
            return getPropertyValue(propName, resourceName);
        } catch (ApiException e) {
            return null;
        }
    }
}
