package course.labs.dailyselfie;

import java.util.ArrayList;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.app.AlarmManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuInflater;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class MainActivity extends ListActivity {
    private static final String TAG = "Daily_Selfie";

    private PhotosViewAdapter mAdapter;

    private ArrayList<Selfie> mSelfies = new ArrayList<Selfie>();

    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;

    private static final long TWO_MINS = 2 * 60 * 1000L;//2 minutes
    private static final int THUMBNAIL_WIDTH = 100;
    private static final int THUMBNAIL_HEIGHT = 100;

    private static final String PHOTO_TIME_FORMAT = "yyyyMMdd_HHmmss";
    private static final int REQUEST_IMAGE_CAPTURE = 1;


    private static final File PHOTO_STORAGE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAlarm();
        loadSelfies(mSelfies);
        final ListView selfieListView = getListView();
        Log.i(TAG, "selfieListView: " + selfieListView);


        selfieListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Log.i(TAG, "Selfie at position " + position + " clicked");
                Intent fullPhotoIntent = new Intent(Intent.ACTION_VIEW);
                Selfie selected = (Selfie) mAdapter.getItem(position);
                fullPhotoIntent.setDataAndType(Uri.parse("file://" + selected.getImagePath()), "image/*");
                Log.i(TAG, "Intent data set to " + selected.getName());
                startActivity(fullPhotoIntent);
            }
        });
        selfieListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Selfie selfie = mSelfies.get(i);
                File selfieFile = new File(selfie.getName());
                Log.i(TAG, "selfieFile is: " + selfie);

                if (selfieFile.exists()) {
                    String toastMsg = selfieFile.delete() ? "Successfully deleted " + selfie.getName() : "Delete failed";
                    Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
                    mSelfies.remove(selfie);
                    //mSelfies.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });

        mAdapter = new PhotosViewAdapter(getApplicationContext());
        for (Selfie selfie : mSelfies) {
            mAdapter.add(selfie);
        }
        setListAdapter(mAdapter);
        Log.i(TAG, "setListAdapter done");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: R.menu.top_menu: " + R.menu.top_menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.take_photo) {
            takeSelfie();
            return true;
        } else {

            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadSelfies(mSelfies);
        Log.i(TAG, "Reloaded selfies: " + mSelfies);
        mAdapter.setList(mSelfies);
        mAdapter.notifyDataSetChanged();
    }


    private void createAlarm(){
        mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        mNotificationReceiverIntent = new Intent(MainActivity.this, AlarmNotificationReceiver.class);
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, mNotificationReceiverIntent, 0);

        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                                   SystemClock.elapsedRealtime() + TWO_MINS,
                                   TWO_MINS,
                                   mNotificationReceiverPendingIntent);

    }

    private void takeSelfie() {
        Log.i(TAG, "Taking a new selfie");
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;

            try {
                imageFile = createImageFile();
                Log.i(TAG, "New selfie image file is " + imageFile.getAbsoluteFile());
            } catch (IOException e) {
                Log.e(TAG, "Error when capturing selfie", e);
            }

            if (imageFile != null) {
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(PHOTO_TIME_FORMAT).format(new Date());
        Log.i(TAG, "createImageFile: timeStamp: " + timeStamp);
        String imageName = "SELFIE_"  + timeStamp + "_";
        File image = File.createTempFile(imageName, ".jpg", PHOTO_STORAGE);
        Log.i(TAG, "Image file created: " + image);
        return image;
    }

    private class SelfiePhotoFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            return file.getName().contains("SELFIE_");
        }
    }

/**
    private Bitmap getThumbnail(File image) {
        Log.i(TAG, "Converting image " + image.getAbsoluteFile() + " to thumbnail");
        String imagePath = image.getAbsolutePath();
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bitmapOptions);
        int bmWidth = bitmapOptions.outWidth;
        int bmHeight = bitmapOptions.outHeight;

        int scaleFactor = Math.min(bmWidth/THUMBNAIL_WIDTH, bmHeight/THUMBNAIL_HEIGHT);

        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;
        bitmapOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(imagePath, bitmapOptions);
    }
*/

private Bitmap getThumbnail(String imagePath) {
    Log.i(TAG, "Getting selfie thumbnails");
    // Get the dimensions of the View
    int targetW = THUMBNAIL_WIDTH;
    int targetH = THUMBNAIL_HEIGHT;

    // Get the dimensions of the bitmap
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    bmOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(imagePath, bmOptions);
    int photoW = bmOptions.outWidth;
    int photoH = bmOptions.outHeight;

    // Determine how much to scale down the image
    int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

    // Decode the image file into a Bitmap sized to fill the View
    bmOptions.inJustDecodeBounds = false;
    bmOptions.inSampleSize = scaleFactor;
    bmOptions.inPurgeable = true;

    return BitmapFactory.decodeFile(imagePath, bmOptions);
}

    private void loadSelfies(ArrayList<Selfie> selfies){
        Log.i(TAG, "Loading selfies....");
        selfies.clear();
        SelfiePhotoFilter selfiePhotoFilter = new SelfiePhotoFilter();

        if (PHOTO_STORAGE.exists()) {
            File[] imageFiles = PHOTO_STORAGE.listFiles(selfiePhotoFilter);
            if (imageFiles != null) {
                for (File file : PHOTO_STORAGE.listFiles(selfiePhotoFilter)) {
                    selfies.add(new Selfie(file.getName(), file.getAbsolutePath(),
                                getThumbnail(file.getAbsolutePath())));
                }
            }
        }
        Log.i(TAG, "Selfies are: " + selfies);
    }

}