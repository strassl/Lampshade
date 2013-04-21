package eu.prismsw.tropeswrapper;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/** A simple link wrapper with title and url **/
public class TropesLink implements Parcelable{
	public String title;
	public Uri url;
	
	public TropesLink(String title, Uri url) {
		this.title = title;
		this.url = url;
	}

    private TropesLink(Parcel parcel) {
        this.title = parcel.readString();
        this.url = parcel.readParcelable(Uri.class.getClassLoader());
    }
	
	@Override
	public String toString() {
		return this.title;
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeParcelable(url, flags);
    }
}
