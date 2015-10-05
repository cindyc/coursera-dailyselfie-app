package course.labs.dailyselfie;

import java.io.File;

import android.widget.ImageView;
import android.graphics.Bitmap;


public class Selfie {
    private String mName;
    private ImageView mImageView;
    private String mImagePath;
    private Bitmap mThumbnail;

    public Selfie(String name, String imagePath, Bitmap thumbnail) {
        this.mName = name;
        this.mImagePath = imagePath;
        this.mThumbnail = thumbnail;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public String getName() {
        return mName;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

	@Override
	public String toString(){
		return "Name: " + mName + " ImagePath: " + mImagePath;
		
	}
}
