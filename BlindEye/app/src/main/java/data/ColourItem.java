package data;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ColourItem implements Parcelable {
    //Colour ID
    protected final long mId;

    //Colour value as an int
    protected int mColour;

    //Optional name that can be given to a colour
    protected String mName;

    //Creation time of colour as a long
    protected final long mCreationTime;

    //Readable String of hexadecimal value
    protected transient String mHexString;

    //Readable String of RGB value
    protected transient String mRGBString;

    //Readable String of HSV value
    protected transient String mHSVString;

    //Creating new ColourItem with an ID and Colour
    public ColourItem(long id, int colour) {
        mId = id;
        mColour = colour;
        mCreationTime = System.currentTimeMillis();
    }

    //Creating a new ColourItem from a Parcel which I implemented with this class
    public ColourItem(Parcel parcel) {
        this.mId = parcel.readLong();
        this.mColour = parcel.readInt();
        this.mCreationTime = parcel.readLong();
        this.mName = parcel.readString();
    }

    //Creating a new ColourItem with a Colour
    public ColourItem(int colour) {
        mId = mCreationTime = System.currentTimeMillis();
        mColour = colour;
    }

    //Getting the ID of ColourItem
    public long getID() {
        return mId;
    }

    //Getting Colour value of ColourItem
    public int getColour() {
        return mColour;
    }

    //Setting the Colour Value for the ColourItem
    public void setColour(int colour) {
        if (mColour != colour) {
            mColour = colour;
            mHexString = createHexString(mColour);
            mRGBString = createRGBString(mColour);
            mHSVString = createHSVString(mColour);
        }
    }

    //Getting Creation Time of ColourItem
    public long getCreationTime() {
        return mCreationTime;
    }

    //Getting the Readable Hexadecimal Value of the Colour
    public String getHexString() {
        if (mHexString == null) {
            mHexString = createHexString(mColour);
        }
        return mHexString;
    }

    //Getting the Readable RBG Value of the Colour
    public String getRGBString() {
        if (mRGBString == null) {
            mRGBString = createRGBString(mColour);
        }
        return mRGBString;
    }

    //Getting the Readable Hexadecimal Value of the Colour
    public String getHSVString() {
        if (mHSVString == null) {
            mHSVString = createHSVString(mColour);
        }
        return mHSVString;
    }

    //Get Colour name
    public String getName() {
        return mName;
    }

    //Set Colour name
    public void setName(String name) {
        mName = name;
    }

    //Creating the Readable HSV Value of the Colour
    private String createHSVString(int val) {
        float[] hsv = new float[3];
        Color.colorToHSV(val, hsv);
        return "HSV(" + (int) hsv[0] + "°, " + (int) (hsv[1] * 100) + "%, " + (int) (hsv[2] * 100) + "%)";
    }

    //Creating the Readable RGB Value of the Colour
    private String createRGBString(int val) {
        return "RGB(" + Color.red(val) + ", " + Color.green(val) + ", " + Color.blue(val) + ")";
    }

    //Creating the Readable Hexadecimal Value of the Colour
    private String createHexString(int val) {
        return "#" + Integer.toHexString(val).substring(2);
    }

    //Pre-existing methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeInt(this.mColour);
        dest.writeLong(this.mCreationTime);
        dest.writeString(this.mName);
    }

    //A Creator used to create a ColourItem from the Parcel
    public static final Creator<ColourItem> CREATOR = new Creator<ColourItem>() {
        @Override
        public ColourItem createFromParcel(Parcel source) {
            return new ColourItem(source);
        }

        @Override
        public ColourItem[] newArray(int size) {
            return new ColourItem[size];
        }
    };
}
