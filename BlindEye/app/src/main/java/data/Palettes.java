package data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

//Static methods for dealing with Palette(s)
public final class Palettes {
    //Key for saving the Palettes belonging to the user
    private static final String SAVED_COLOUR_KEY_PALETTES = "ColourPalettes.Keys.SAVED_COLOUR_ITEMS";

    //A Gson for serializing/deserializing objects
    //Gson is a Java library that can be used to convert Java Objects into their JSON representation.
    private static final Gson GSON = new Gson();

    //A Type instance of a List of ColourItem(s)
    private static final Type COLOUR_PALETTE_LIST_TYPE = new TypeToken<List<Palette>>() {

    }.getType();

    //A Comparator for sorting ColourItem(s) in order of creation
    public static final Comparator<Palette> SEQUENTIAL_COMPARATOR = new Comparator<Palette>() {
        @Override
        public int compare(Palette o1, Palette o2) {
            return (int) (o2.getID() - o1.getID());

        }
    };

    //Get the SharedPreferences used for saving/restoring data
    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    //Get the Palette(s) of the user
    public static List<Palette> getSavedPalette(Context context) {
        return getSavedPalette(getSharedPreferences(context));
    }

    //Getting the Palette(s) belonging to the user
    @SuppressWarnings("unchecked")
    public static List<Palette> getSavedPalette(SharedPreferences sharedPreferences) {
        final String jsonPalettes = sharedPreferences.getString(SAVED_COLOUR_KEY_PALETTES, "");

        //No saved palettes found
        //Returning an empty list
        if ("".equals(jsonPalettes)) {
            return new ArrayList<>();
        }

        //Parsing the json into ColourItems
        final List<Palette> palettes = GSON.fromJson(jsonPalettes, COLOUR_PALETTE_LIST_TYPE);

        //Sorting the ColourItem(s) in sequential order
        Collections.sort(palettes, SEQUENTIAL_COMPARATOR);
        return palettes;
    }

    //Saving a Palette(s)
    public static boolean savePalette(Context context, Palette paletteToBeSaved) {
        if (paletteToBeSaved == null) {
            throw new IllegalArgumentException("Can't save an empty/null palette");
        }
        final List<Palette> savedPalette = getSavedPalette(context);
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        final List<Palette> paletteList = new ArrayList<>(savedPalette.size() + 1);

        //Adding saved colour items except those with matching IDs, they will be overridden
        final int size = savedPalette.size();
        for (int i = 0; i < size; i++) {
            final Palette palette = savedPalette.get(i);
            if (palette.getID() != paletteToBeSaved.getID()) {
                paletteList.add(palette);
            }
        }

        //Adding the new palette to save
        paletteList.add(paletteToBeSaved);
        editor.putString(SAVED_COLOUR_KEY_PALETTES, GSON.toJson(paletteList));
        return editor.commit();
    }

    //Deleting a Palette
    public static boolean deletePalette(Context context, Palette paletteToBeDeleted) {
        if (paletteToBeDeleted == null) {
            throw new IllegalArgumentException("Cannot delete a palette that does not exist!");
        }

        final SharedPreferences sharedPreferences = getSharedPreferences(context);
        final List<Palette> palettesSaved = getSavedPalette(sharedPreferences);

        for (Iterator<Palette> iterator = palettesSaved.iterator(); iterator.hasNext(); ) {
            final Palette palette = iterator.next();
            if (palette.getID() == paletteToBeDeleted.getID()) {
                iterator.remove();
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SAVED_COLOUR_KEY_PALETTES, GSON.toJson(palettesSaved));
                return editor.commit();
            }
        }
        return false;
    }

    //Registering an OnPaletteChangeListener
    //Issues with this is that no Strong references are currently stored to the listener
    //If you do not store a Strong reference, it ban be susceptible to garbage collection
    public static void registerListener(Context context, OnPaletteChangeListener onPaletteChangeListener) {
        getSharedPreferences(context).registerOnSharedPreferenceChangeListener(onPaletteChangeListener);
    }

    //Unregister a ColourItems.OnColourItemChangeListener
    public static void unregisterListener(Context context, OnPaletteChangeListener onPaletteChangeListener) {
        getSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(onPaletteChangeListener);
    }

    //A class for listening to the changes of the ColourItems belonging to the user
    public abstract static class OnPaletteChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (SAVED_COLOUR_KEY_PALETTES.equals(key)) {
                onChangedColourItem(getSavedPalette(sharedPreferences));
            }
        }

        //Method called when the ColourItem(s) of the user change.
        public abstract void onChangedColourItem(List<Palette> palettes);
    }

    //Default constructor as to not be instantiable
    private Palettes() {
    }

}
