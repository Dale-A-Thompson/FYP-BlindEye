package data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

//Static methods for dealing with ColourItem(s)
public final class ColourItems {
    //Key for saving the ColourItem(s) of the user
    private static final String SAVED_COLOUR_KEY_ITEMS = "Colours.Keys.SAVED_COLOUR_ITEMS";

    //Key for saving last picked colour
    private static final String LAST_PICKED_COLOUR_KEY = "Colours.Keys.LAST_PICKED_COLOUR";

    //Default last pocked colour
    private static final int LAST_PICKED_COLOUR_DEFAULT = Color.WHITE;

    //A Gson for serializing/deserializing objects
    //Gson is a Java library that can be used to convert Java Objects into their JSON representation.
    private static final Gson GSON = new Gson();

    //A Type instance of a List of ColourItem(s)
    private static final Type COLOUR_ITEM_LIST_TYPE = new TypeToken<List<ColourItem>>() {

    }.getType();

    //A Comparator for sorting ColourItem(s) in order of creation
    public static final Comparator<ColourItem> SEQUENTIAL_COMPARATOR = new Comparator<ColourItem>() {
        @Override
        public int compare(ColourItem o1, ColourItem o2) {
            return (int) (o2.getID() - o1.getID());
        }
    };

    //Get the SharedPreferences used for saving/restoring data
    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    //Get the last picked colour
    public static int getLastPickedColour(Context context) {
        return getSharedPreferences(context).getInt(LAST_PICKED_COLOUR_KEY, LAST_PICKED_COLOUR_DEFAULT);
    }

    //Saving last colour
    public static boolean saveLastColour(Context context, int lastPickedColour) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(LAST_PICKED_COLOUR_KEY, lastPickedColour);
        return editor.commit();
    }

    //Get the ColourItem(s) of the user
    public static List<ColourItem> getSavedColour(Context context) {
        return getSavedColour(getSharedPreferences(context));
    }

    //Getting the ColourItem(s) belonging to the user
    @SuppressWarnings("unchecked")
    public static List<ColourItem> getSavedColour(SharedPreferences sharedPreferences) {
        final String jsonColourItems = sharedPreferences.getString(SAVED_COLOUR_KEY_ITEMS, "");

        //No saved colours found
        //Returning an empty list
        if ("".equals(jsonColourItems)) {
            return new ArrayList<>();
        }

        //Parsing the json into ColourItems
        final List<ColourItem> colourItems = GSON.fromJson(jsonColourItems, COLOUR_ITEM_LIST_TYPE);

        //Sorting the ColourItem(s) in sequential order
        Collections.sort(colourItems, SEQUENTIAL_COMPARATOR);
        return colourItems;
    }

    //Saving a ColourItem(s)
    public static boolean saveColourItem(Context context, ColourItem colourItemToBeSaved) {
        if (colourItemToBeSaved == null) {
            throw new IllegalArgumentException("Can't save an empty/null colour");
        }
        final List<ColourItem> savedColours = getSavedColour(context);
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        final List<ColourItem> colourItems = new ArrayList<>(savedColours.size() + 1);

        //Adding saved colour items except those with matching IDs, they will be overridden
        final int size = savedColours.size();
        for (int i = 0; i < size; i++) {
            final ColourItem colourItem = savedColours.get(i);
            if (colourItem.getID() != colourItemToBeSaved.getID()) {
                colourItems.add(colourItem);
            }
        }

        //Adding the new colour to save
        colourItems.add(colourItemToBeSaved);
        editor.putString(SAVED_COLOUR_KEY_ITEMS, GSON.toJson(colourItems));
        return editor.commit();
    }

    //Deleting a ColourItem
    public static boolean deleteColourItem(Context context, ColourItem colourItemToBeDeleted) {
        if (colourItemToBeDeleted == null) {
            throw new IllegalArgumentException("Cannot delete a colour that does not exist!");
        }

        final SharedPreferences sharedPreferences = getSharedPreferences(context);
        final List<ColourItem> colourItemsSaved = getSavedColour(sharedPreferences);

        for (Iterator<ColourItem> iterator = colourItemsSaved.iterator(); iterator.hasNext(); ) {
            final ColourItem colourItem = iterator.next();
            if (colourItem.getID() == colourItemToBeDeleted.getID()) {
                iterator.remove();
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SAVED_COLOUR_KEY_ITEMS, GSON.toJson(colourItemsSaved));
                return editor.commit();
            }
        }
        return false;
    }

    //Registering a ColourItems.OnColourItemChangeListener
    //Issues with this is that no Strong references are currently stored to the listener
    //If you do not store a Strong reference, it ban be susceptible to garbage collection
    public static void registerListener(Context context, OnColourItemChangeListener onColourItemChangeListener) {
        getSharedPreferences(context).registerOnSharedPreferenceChangeListener(onColourItemChangeListener);
    }

    //Unregister a ColourItems.OnColourItemChangeListener
    public static void unregisterListener(Context context, OnColourItemChangeListener onColourItemChangeListener) {
        getSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(onColourItemChangeListener);
    }

    //A class for listening to the changes of the ColourItems belonging to the user
    public abstract static class OnColourItemChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (SAVED_COLOUR_KEY_ITEMS.equals(key)) {
                onChangedColourItem(getSavedColour(sharedPreferences));
            }
        }

        //Method called when the ColourItem(s) of the user change.
        public abstract void onChangedColourItem(List<ColourItem> colourItems);
    }

    //Default constructor as to not be instantiable
    private ColourItems() {

    }
}
