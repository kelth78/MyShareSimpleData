package com.example.mysharesimpledata;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

public class MainActivity extends /*AppCompatActivity*/Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                //handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            Log.v(TAG, "Handle other intents, such as being started from the home screen");
        }
    }

    // To handle text content delivered by other content
    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            Log.v(TAG, "handleSendText");
        }
    }

    // To handle image content delivered by other content
    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            Log.v(TAG, "handleSendImage");
        }
    }


    private ShareActionProvider mShareActionProvider;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.sharemenu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        // Return true to display menu
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onAttachedToWindow() {
        //super.onAttachedToWindow();
        openOptionsMenu();
    }

    private static final String TAG = MainActivity.class.getSimpleName();

    // Share simple text data with other activities. System will automatically show chooser if
    // more than one activity is available
    public void shareTextDataButtonClicked(View view) {
        Log.v(TAG, "shareTextDataButtonClicked");
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    // Share simple text data with other activities. Always show chooser even though
    // user has previously selected default activity to use
    public void shareTextDataAlwaysDisplayChooserButtonClicked(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Always display chooser :).");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

    // Share binary content (e.g. image) with other activities
    public void shareBinaryContentButtonClicked(View view) {
        Uri uriToImage = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + "ic_launcher.png");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }


    private final int REQ_CODE_FILE = 1;
    private Intent askIntent = new Intent();

    public void requestAShareFileButtonClicked(View view) {
        Log.v(TAG, "requestAShareFileButtonClicked");
        askIntent.setAction(Intent.ACTION_PICK);
        askIntent.setType("image/jpg");
        startActivityForResult(askIntent, REQ_CODE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult, requestCode: " + requestCode + " , res: " + resultCode);

        if (requestCode == REQ_CODE_FILE) {
            if (resultCode == RESULT_OK) {
                ParcelFileDescriptor mInputPFD;
                // Get the file's content URI from the incoming Intent
                Uri returnUri = data.getData();
                // Get MIME type
                String mimeType = getContentResolver().getType(returnUri);
                Log.v(TAG, "MIME Type: " + mimeType);
                /*
                * Try to open the file for "read" access using the
                * returned URI. If the file isn't found, write to the
                * error log and return.
                */
                try {
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageURI(returnUri);
                    /*
                    * Get the content resolver instance for this context, and use it
                    * to get a ParcelFileDescriptor for the file.
                    */
                    mInputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("MainActivity", "File not found.");
                    return;
                }
                // Get a regular file descriptor for the file
                FileDescriptor fd = mInputPFD.getFileDescriptor();
            } else {
                Log.e(TAG, "Something is wrong: " + resultCode);
            }
        }
    }
}
