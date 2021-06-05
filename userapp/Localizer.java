package userapp;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Localizer {

    private static final ObjectProperty<Locale> LOCALE_PROPERTY;
    
    private static final Map<String, Locale> SUPPORTED_LOCALES;

    private static ClassLoader LOADER;
    private static String bundleName;

    private static DateTimeFormatter dateTimeFormatter;
    private static final String DATE_TIME_FORMAT_PROPERTY = "datetime.format";

    static {
        LOCALE_PROPERTY = new SimpleObjectProperty<>(Locale.ENGLISH);
        SUPPORTED_LOCALES = new HashMap<>();
        SUPPORTED_LOCALES.put("ru", new Locale("ru")); // russian
        SUPPORTED_LOCALES.put("de", new Locale("de")); // germany
        SUPPORTED_LOCALES.put("en", new Locale("en")); // english (United Kingdom)
        SUPPORTED_LOCALES.put("bg", new Locale("bg")); // bulgarian
        dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM, dd, yyyy HH:mm:ss");
    }

    public static void loadLocales(String path, String bundleName) throws MalformedURLException {
        Localizer.bundleName = bundleName;
        File file = new File(path);
        URL[] urls = new URL[]{file.toURI().toURL()};
        LOADER = new URLClassLoader(urls);
    }

    public static void setLocale(String name) {
        Locale locale = SUPPORTED_LOCALES.get(name);
        LOCALE_PROPERTY.set(locale);
        dateTimeFormatter = DateTimeFormatter.ofPattern(get(DATE_TIME_FORMAT_PROPERTY), locale);
    }

    public static String get(String key) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, LOCALE_PROPERTY.get(), LOADER);
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
        
    }

    public static StringBinding getBinging(String key) {
        return Bindings.createStringBinding(() -> get(key), LOCALE_PROPERTY);
    }

    public static String get(LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter);
    }
}
