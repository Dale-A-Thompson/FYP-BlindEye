package data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

//class that represents a palette consisting of colours from colouritem(s)
public class Palette implements Parcelable {
    //Palette ID
    private final long mId;

    //Palette name
    private String mName;

    //ColourItem that creates the Palette
    private List<ColourItem> mColourItems;

    //Creating Palette name
    public Palette(String name) {
        mId = System.currentTimeMillis();
        mName = name;
        mColourItems = new ArrayList<>();
    }

    //Creating Palette of ColourItems
    public Palette(String name, List<ColourItem> colourItems) {
        mId = System.currentTimeMillis();
        mName = name;
        mColourItems = new ArrayList<>(colourItems);
    }

    //Creating new Palette from a Parcel which is implemented with the class
    private Palette(Parcel parcel) {
        mColourItems = new ArrayList<>();
        mId = parcel.readLong();
        mName = parcel.readString();
        parcel.readTypedList(mColourItems, ColourItem.CREATOR);
    }

    //Adding a link for ColourItem to the Palette
    public void colourToPalette(ColourItem colourItem) {
        if (colourItem == null) {
            throw new IllegalStateException("Colour item cannot be null");
        }
        mColourItems.add(colourItem);
    }

    //Getting the ID of the Palette
    public long getID() {
        return mId;
    }

    //Getting the Name of the Palette
    public String getName() {
        return mName;
    }

    //Setting the name of the Palette
    public void setName(String name) {
        mName = name;
    }

    //Getting the List of ColourItems that create(s) the Palette
    //Returning the List of ColourItems that create(s) the Palette
    public List<ColourItem> getColours() {
        return new ArrayList<>(mColourItems);
    }

    //Pre-existing methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mName);
        dest.writeTypedList(this.mColourItems);
    }

    //A Creator used to create a Palette from the Parcel
    public static final Creator<Palette> CREATOR = new Creator<Palette>() {
        @Override
        public Palette createFromParcel(Parcel source) {
            return new Palette(source);
        }

        @Override
        public Palette[] newArray(int size) {
            return new Palette[size];
        }
    };
}
