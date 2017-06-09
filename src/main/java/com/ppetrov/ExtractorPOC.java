package com.ppetrov;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by ppetrov on 10.06.2017.
 */
public class ExtractorPOC {

    public static void main(String[] args) throws URISyntaxException, IOException {
        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();

            URL modernUrl = ExtractorPOC.class.getClassLoader().getResource("Modern.js");
            if (modernUrl != null) {
                File modernFile = new File(modernUrl.toURI());
                if (modernFile.exists()) {
                    cx.evaluateReader(
                            scope,
                            new FileReader(modernFile),
                            "Modern",
                            0,
                            null
                    );

                    NativeObject label = (NativeObject) cx.evaluateString(
                            scope,
                            "var states = new Object();"
                                    + "states.disabled = true;"
                                    + "appearances.label.style(states);",
                            "root",
                            0,
                            null
                    );

                    System.out.println(label.get("textColor"));

                    NativeObject button = (NativeObject) cx.evaluateString(
                            scope,
                            "appearances.button.style();",
                            "root",
                            0,
                            null
                    );

                    NativeObject splitbuttonArrow = (NativeObject) cx.evaluateString(
                            scope,
                            "var states = new Object();"
                                    + convertPropertiesObjectToObjectDefinition(button, "superStyles")
                                    // Normally we need to extract these paddings from button-frame (via include).
                                    // Looks doable, but it's a PoC, so I'll just hardcode it.
                                    + "superStyles.padding = [3, 9];"
                                    + "appearances[\"splitbutton/arrow\"].style(states, superStyles);",
                            "splitbutton/arrow",
                            0,
                            null
                    );

                    System.out.println(((NativeArray) splitbuttonArrow.get("padding")).get(0));
                }
            }
        } finally {
            Context.exit();
        }
    }

    private static String convertPropertiesObjectToObjectDefinition(NativeObject object, String varName) {
        String result = "var " + varName + " = new Object();";
        for (Object key : object.keySet()) {
            result +=  varName + "[\"" + key + "\"] = " + object.get(key) + ";";
        }
        return result;
    }

}
