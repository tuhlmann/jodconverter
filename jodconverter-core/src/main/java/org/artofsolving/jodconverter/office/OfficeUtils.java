//
// JODConverter - Java OpenDocument Converter
// Copyright 2004-2012 Mirko Nasato and contributors
//
// JODConverter is Open Source software, you can redistribute it and/or
// modify it under either (at your option) of the following licenses
//
// 1. The GNU Lesser General Public License v3 (or later)
//    -> http://www.gnu.org/licenses/lgpl-3.0.txt
// 2. The Apache License, Version 2.0
//    -> http://www.apache.org/licenses/LICENSE-2.0.txt
//
// Contributors:
//     Laurent Doguin (Nuxeo), Julien Carsique (Nuxeo)
package org.artofsolving.jodconverter.office;

import java.io.File;
import java.util.Map;

import org.artofsolving.jodconverter.util.PlatformUtils;

import com.sun.star.beans.PropertyValue;
import com.sun.star.uno.UnoRuntime;

public class OfficeUtils {

    public static final String SERVICE_DESKTOP = "com.sun.star.frame.Desktop";

    private OfficeUtils() {
        throw new AssertionError("utility class must not be instantiated");
    }

    public static <T> T cast(Class<T> type, Object object) {
        return (T) UnoRuntime.queryInterface(type, object);
    }

    public static PropertyValue property(String name, Object value) {
        PropertyValue propertyValue = new PropertyValue();
        propertyValue.Name = name;
        propertyValue.Value = value;
        return propertyValue;
    }

    public static PropertyValue[] toUnoProperties(Map<String, ?> properties) {
        PropertyValue[] propertyValues = new PropertyValue[properties.size()];
        int i = 0;
        for (Map.Entry<String, ?> entry : properties.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> subProperties = (Map<String, Object>) value;
                value = toUnoProperties(subProperties);
            }
            propertyValues[i++] = property(entry.getKey(), value);
        }
        return propertyValues;
    }

    public static String toUrl(File file) {
        String path = file.toURI().getRawPath();
        String url = path.startsWith("//") ? "file:" + path : "file://" + path;
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * Search for an (Open/Libre)Office install.
     * If the System property "office.home" is defined, it takes precedence.
     *
     * @see PlatformUtils#findOfficeHome()
     *
     * @return Office home found
     */
    public static File getDefaultOfficeHome() {
        return new File(System.getProperty("office.home",
                PlatformUtils.findOfficeHome()));
    }

    /**
     * Search for an (Open/Libre)Office profile.
     * If the System property "office.profile" is defined, it takes precedence.
     *
     * @see PlatformUtils#findOfficeProfileDir()
     *
     * @return Office profile found
     */
    public static File getDefaultProfileDir() {
        return new File(System.getProperty("office.profile",
                PlatformUtils.findOfficeProfileDir()));
    }

    public static File getOfficeExecutable(File officeHome) {
        if (PlatformUtils.isMac()) {
            return new File(officeHome, "MacOS/soffice.bin");
        } else {
            return new File(officeHome, "program/soffice.bin");
        }
    }

}
