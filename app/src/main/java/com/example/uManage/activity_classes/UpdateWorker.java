package com.example.uManage.activity_classes;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uManage.R;
import com.example.uManage.database.WorkersDatabase;

import java.io.ByteArrayOutputStream;

public class UpdateWorker extends AppCompatActivity {
    Button button;
    EditText name;
    EditText age;
    EditText salary;
    TextView imagename;
    ImageView img;
    String username;
    ImageButton imgbtn;
    WorkersDatabase db;
    int id;
    String name1;
    int age1;
    int salary1;
    byte[] image;
    Bitmap instancebitmap;

    Uri selectedImage;
    //χρησιμοποιώ Launcher για την είσοδο στην συλλογή, όταν η συλλογή κλείσει τότε κάνω μετατροπές στο αποτέλεσμα που πήρα και αποθηκεύω την φωτογραφία σε ένα αόρατο Imageview
    ActivityResultLauncher<Intent> Launcher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== RESULT_OK && result.getData() != null)
                    {
                        selectedImage=result.getData().getData();
                        Cursor returnCursor = getContentResolver().query(selectedImage, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        returnCursor.moveToFirst();
                        imagename.setText(returnCursor.getString(nameIndex));
                        img.setImageURI(selectedImage);
                        instancebitmap=((BitmapDrawable) img.getDrawable()).getBitmap();
                    }
                }
            }
    );


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        //παίρνω σαν extra το ID
        if(extras!=null)
        {
            id=extras.getInt("ID");
        }
        //δημιουργώ ενα instance της βάσης των εργαζόμενων
        db=new WorkersDatabase(this);
        setContentView(R.layout.updateworker_tab);
        //init components
         button=findViewById(R.id.button_update_tab);
         name=findViewById(R.id.person_name_update_tab);
         age =findViewById(R.id.age_update_tab);
         salary=findViewById(R.id.salary_update_tab);
         img=findViewById(R.id.image_for_bitmap_update_tab);
         imagename=findViewById(R.id.camera_text_update_tab);
         imgbtn=findViewById(R.id.camera_update_tab);
         //αν το savedInstanceState είναι null τότε κάνει καλεί την συνάρτηση initiatevariables() και εισάγει τιμές στις μεταβλητές,διαφορετικά παίρνει από το savedInstanceState
         if(savedInstanceState==null) {
             initiatevariables();
         }
         else
         {
             id=savedInstanceState.getInt("wid");
             name.setText(savedInstanceState.getString("wname"));
             age.setText(savedInstanceState.getString("wage"));
             salary.setText(savedInstanceState.getString("wsalary"));
             if(savedInstanceState.getParcelable("wimguri")!=null) {
                 instancebitmap = savedInstanceState.getParcelable("wimguri");
                 img.setImageBitmap(instancebitmap);
                 imagename.setText(savedInstanceState.getString("wimgname"));
             }
             else
             {
                 img=null;
             }
             username=savedInstanceState.getString("wusername");
         }
        super.onCreate(savedInstanceState);

        //onclicklistener για το imagebutton που οδηγεί στην συλλογη
         imgbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 Launcher.launch(intent);
             }
         });

        //onclicklistener για το addbutton που αν όλοι οι έλεγχοι είναι οκ κάνει ενημέρωση του εργαζόμενου στην βάση
         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (name.getText().toString().equals("") || age.getText().toString().equals("") || salary.getText().toString().equals("")) {
                     Toast.makeText(UpdateWorker.this, "Please fill all the gaps!", Toast.LENGTH_SHORT).show();
                 } else {
                     if (Integer.parseInt(age.getText().toString()) <= 17) {
                         Toast.makeText(UpdateWorker.this, "Age must be higher than 17!", Toast.LENGTH_SHORT).show();
                     } else {
                         bitmaptobyte();
                         name1 = name.getText().toString();
                         age1 = Integer.parseInt(age.getText().toString());
                         salary1 = Integer.parseInt(salary.getText().toString());
                         db.update(id, name1, age1, salary1, imagename.getText().toString(), image, username);
                         setResult(8);
                         finish();
                     }
                 }
             }
         });
    }

private void bitmaptobyte()
{

    //η διαδικασία μετατροπής της εικόνας σε byte[]
    if(img != null) {
        Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
        int nh = (int) ( bitmap.getHeight() * (1024.0 / bitmap.getWidth()) );
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
        scaled.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        image = byteArrayOutputStream.toByteArray();
    }
    else
    {
        image= null;
    }
}
    private void initiatevariables() {
        Cursor cursor = db.allEntries();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                if(id==Integer.parseInt(cursor.getString(0)))
                {
                    name.setText(cursor.getString(1));
                    age.setText(cursor.getString(2));
                    salary.setText(cursor.getString(3));
                    imagename.setText(cursor.getString(4));
                    if(cursor.getBlob(5)!=null) {
                        img.setImageBitmap(getImage(cursor.getBlob(5)));
                        Log.d(TAG, "initiatevariables: "+img);
                        instancebitmap=((BitmapDrawable) img.getDrawable()).getBitmap();
                    }
                    else
                    {
                        img=null;
                    }
                    username=cursor.getString(6);
                }
            }
        }

    }
    //μετατροπή απο byte[] σε bitmap για να πάρω την αρχική φωτογραφία από την βάση αν αυτή υπάρχει
    public Bitmap getImage(byte[] imgByte){
        if (imgByte != null) {
            return Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imgByte, 0 ,imgByte.length),50,50,false);
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("wid",id);
        outState.putString("wname",name.getText().toString());
        outState.putString("wage", String.valueOf(age.getText()));
        outState.putString("wsalary", String.valueOf(salary.getText()));
        outState.putParcelable("wimguri",instancebitmap);
        outState.putString("wimgname",imagename.getText().toString());
        outState.putString("wusername",username);
        super.onSaveInstanceState(outState);
    }

}
