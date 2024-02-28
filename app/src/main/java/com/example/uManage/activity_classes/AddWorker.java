package com.example.uManage.activity_classes;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.uManage.R;
import com.example.uManage.database.WorkersDatabase;

import java.io.ByteArrayOutputStream;

public class AddWorker extends AppCompatActivity {

    public Uri selectedImage;
    Button button;
    ImageButton imageButton;
    TextView textView;
    ImageView img;
    EditText name;
    EditText age;
    EditText salary;
    WorkersDatabase db;
    String name1;
    String username;
    int age1;
    int salary1;
    int t = 0;

    //χρησιμοποιώ Launcher για την είσοδο στην συλλογή, όταν η συλλογή κλείσει τότε κάνω μετατροπές στο αποτέλεσμα που πήρα και αποθηκεύω την φωτογραφία σε ένα αόρατο Imageview
    ActivityResultLauncher<Intent> Launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        t = 1;
                        selectedImage = result.getData().getData();
                        Cursor returnCursor = getContentResolver().query(selectedImage, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        returnCursor.moveToFirst();
                        //αρχικοποιώ το textview με το όνομα της φωτογραφίας που χρησιμοποίησα
                        textView.setText(returnCursor.getString(nameIndex));
                        textView.setVisibility(View.VISIBLE);
                        img.setImageURI(selectedImage);
                        Log.d(TAG, "onActivityResult: " + img);
                    }
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.addworker_tab);
        //παίρνω σαν extra το όνομα της εταιρίας για να κάνω τις εισαγωγές στην βάση
        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            username = extras.getString("name");
        }
        //init τα components
        textView = findViewById(R.id.camera_text_add_tab);
        imageButton = findViewById(R.id.camera_add_tab);
        img = findViewById(R.id.image_for_bitmap_add_tab);
        button = findViewById(R.id.button_add_tab);
        name = findViewById(R.id.person_name_add_tab);
        age = findViewById(R.id.age_add_tab);
        salary = findViewById(R.id.salary_add_tab);
        //δημιουργώ instance της βάσης εργαζόμενων
        db = new WorkersDatabase(this);
        //savedInstanceState σε περίπτωση που γυρίσει η οθόνη ή κλείσουμε προσωρινά την εφαρμογή χωρίς να την τερματίσουμε
        if (savedInstanceState != null) {
            name.setText(savedInstanceState.getString("wname"));
            age.setText(savedInstanceState.getString("wage"));
            salary.setText(savedInstanceState.getString("wsalary"));
            if (savedInstanceState.getParcelable("wimguri") != null) {
                selectedImage = savedInstanceState.getParcelable("wimguri");
                img.setImageURI(selectedImage);
                textView.setText(savedInstanceState.getString("wimgname"));
                textView.setVisibility(View.VISIBLE);
                t = 1;
            }
        }
        super.onCreate(savedInstanceState);
        //onclicklistener για το imagebutton που οδηγεί στην συλλογη
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sunarthsh();
            }
        });
        //onclicklistener για το addbutton που αν όλοι οι έλεγχοι είναι οκ κάνει εισαγωγή του εργαζόμενου στην βάση
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("") || age.getText().toString().equals("") || salary.getText().toString().equals("")) {
                    Toast.makeText(AddWorker.this, "Please fill all the gaps!", Toast.LENGTH_SHORT).show();
                } else {
                    if (Integer.parseInt(age.getText().toString()) <= 17) {
                        Toast.makeText(AddWorker.this, "Age must be higher than 17!", Toast.LENGTH_SHORT).show();
                    } else {
                        //μετατρέπω την εικόνα σε byte[] για να την αποθηκεύσω στην βάση
                        byte[] image = bitmaptobyte();
                        name1 = name.getText().toString();
                        age1 = Integer.parseInt(age.getText().toString());
                        salary1 = Integer.parseInt(salary.getText().toString());
                        db.addEntry(name1, age1, salary1, textView.getText().toString(), image, username);
                        finish();
                    }
                }
            }
        });
    }

    //η διαδικασία μετατροπής της εικόνας σε byte[]
    private byte[] bitmaptobyte() {
        byte[] image = null;

        if (t == 1) {
            Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
            int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
            scaled.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
            image = byteArrayOutputStream.toByteArray();
        } else {
            textView.setText("");
        }
        return image;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: " + selectedImage);
        outState.putString("wname", name.getText().toString());
        outState.putString("wage", String.valueOf(age.getText()));
        outState.putString("wsalary", String.valueOf(salary.getText()));
        outState.putParcelable("wimguri", selectedImage);
        outState.putString("wimgname", textView.getText().toString());
        super.onSaveInstanceState(outState);
    }


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    private void sunarthsh() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Launcher.launch(intent);
        } else
        {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
}
