package com.example.textocr;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;

import java.util.ArrayList;
import java.util.List;

public class TextRecognitionProcessor extends VisionProcessorBase<Text>{
    private static final String TAG = "TextRecProcessor";
    public ArrayList<String> listmots = new ArrayList<String>();
    private String value;
    public static String lien_menu = "";
    public static String lien_actu = "";
    public static String lien_localisation="";
    private String lien_menu_macdo = "https://www.mcdonalds.fr/nos-produits/menus";
    private String lien_actu_macdo = "https://www.mcdonalds.fr/espace-presse/actualites";
    private String lien_localisation_macdo="https://www.mcdonalds.fr/restaurants";
    private String lien_menu_kfc = "https://www.kfc.fr/notre-carte/en-ce-moment";
    private String lien_actu_kfc = "https://www.kfc.fr/notre-carte/en-ce-moment";
    private String lien_localisation_kfc = "https://www.kfc.fr/nos-restaurants";
    
    private final TextRecognizer textRecognizer;
    private final Boolean shouldGroupRecognizedTextInBlocks;
    private final Boolean showLanguageTag;

    public TextRecognitionProcessor(
            Context context, TextRecognizerOptionsInterface textRecognizerOptions) {
        super(context);
        shouldGroupRecognizedTextInBlocks = PreferenceUtils.shouldGroupRecognizedTextInBlocks(context);
        showLanguageTag = PreferenceUtils.showLanguageTag(context);
        textRecognizer = TextRecognition.getClient(textRecognizerOptions);
    }

    @Override
    public void stop() {
        super.stop();
        textRecognizer.close();
    }

    @Override
    protected Task<Text> detectInImage(InputImage image) {
        return textRecognizer.process(image);
    }

    @Override
    protected void onSuccess(@NonNull Text text, @NonNull GraphicOverlay graphicOverlay) {
        Log.d(TAG, "On-device Text detection successful");
        logExtrasForTesting(text);
        graphicOverlay.add(
                new TextGraphic(graphicOverlay, text, shouldGroupRecognizedTextInBlocks, showLanguageTag));
        value = text.getText();
        //System.out.println(value);
        listmots.add(value);

        for (String element : listmots){
            if(element.equals("Macdo") || element.equals("M") || element.equals("MacDonald's")){
                System.out.println(element);
                lien_menu = lien_menu_macdo;
                lien_actu = lien_actu_macdo;
                lien_localisation=lien_localisation_macdo;
            }
            else if(element.equals("KFC")){
                lien_menu = lien_menu_kfc;
                lien_actu = lien_actu_kfc;
                lien_localisation=lien_localisation_kfc;
            }
        }
    }

    private static void logExtrasForTesting(Text text) {
        if (text != null) {
            Log.v(MANUAL_TESTING_LOG, "Detected text has : " + text.getTextBlocks().size() + " blocks");
            for (int i = 0; i < text.getTextBlocks().size(); ++i) {
                List<Text.Line> lines = text.getTextBlocks().get(i).getLines();
                Log.v(
                        MANUAL_TESTING_LOG,
                        String.format("Detected text block %d has %d lines", i, lines.size()));
                for (int j = 0; j < lines.size(); ++j) {
                    List<Text.Element> elements = lines.get(j).getElements();
                    Log.v(
                            MANUAL_TESTING_LOG,
                            String.format("Detected text line %d has %d elements", j, elements.size()));
                    for (int k = 0; k < elements.size(); ++k) {
                        Text.Element element = elements.get(k);
                        Log.v(
                                MANUAL_TESTING_LOG,
                                String.format("Detected text element %d says: %s", k, element.getText()));
                        Log.v(
                                MANUAL_TESTING_LOG,
                                String.format(
                                        "Detected text element %d has a bounding box: %s",
                                        k, element.getBoundingBox().flattenToString()));
                        Log.v(
                                MANUAL_TESTING_LOG,
                                String.format(
                                        "Expected corner point size is 4, get %d", element.getCornerPoints().length));
                        for (Point point : element.getCornerPoints()) {
                            Log.v(
                                    MANUAL_TESTING_LOG,
                                    String.format(
                                            "Corner point for element %d is located at: x - %d, y = %d",
                                            k, point.x, point.y));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Text detection failed." + e);
    }
}
