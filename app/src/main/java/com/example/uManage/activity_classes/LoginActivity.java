package com.example.uManage.activity_classes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uManage.R;
import com.example.uManage.database.UsersDatabase;

public class LoginActivity extends AppCompatActivity {
    Button register,login;
    EditText name,password;
    UsersDatabase usersDatabase;

    /*Συνάρτηση που καλείται όταν θέλουμε να ανοίξουμε ένα νέο activity και περιμένουμε επιστροφή σε αυτό από το οποίο ανοίχτηκε. Καθώς ψάχναμε πληροφορίες για την
    StartActivityForResult() είδαμε πως είναι deprecated και παντού προτείνοταν η χρήση αυτής της συνάρτησης, έτσι και την χρησιμοποιήσαμε. Ο τρόπος που λειτουργεί είναι ως εξής,
    δημιουργούμε ένα object ActivityResultLauncher<Intent> και έπειτα οπουδήποτε μέσα στην κλάση θέλουμε να ανοίξουμε ένα νέο activity το ανοίγουμε με την Launcher.launch( κάποιο Intent).
    Το μόνο που χρειάζεται στην άλλη κλάση είναι σε κάποιο σημείο να υπάρχει η εντολή finish(). Ωστόσο για να κάνουμε authenticate με τη χρήση του result πρέπει να δώσουμε τιμή και στο result
    στο activity το οποίο καλούμε. Εμείς εδώ δεν χρειαζόμαστε κάτι τέτοιο ωστόσο γιατί ο έλεγχος της εκχώρησης χρήστη γίνεται στην βάση οπότε είμαστε καλυμένοι.
     */
    final ActivityResultLauncher<Intent> Launcher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
        }
        }
        );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_tab);
        //δημιουργω βαση
        usersDatabase= new UsersDatabase(this);
        //initialize τα components
        register = findViewById(R.id.submitButton);
        login = findViewById(R.id.loginbutton);
        name = findViewById(R.id.editTextTextPersonName);
        password= findViewById(R.id.editTextTextPassword);
        //onclick listener για το login button,τσεκαρω τα inputs
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("") || password.getText().toString().equals(""))
                {
                    Toast.makeText(LoginActivity.this, "You must fill all the gaps!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //χρησιμοποιώ συνάρτηση της βάσης για να δω αν υπάρχει ο χρήστης σε αυτήν
                    if (usersDatabase.checkusernamepassword(name.getText().toString(),password.getText().toString()))
                    {
                        Intent intent = new Intent(LoginActivity.this,ListActivity.class);
                        //βάζω σαν extra το όνομα της εταιρίας με την οποία κάνω login για να κάνω αναζητήσεις στην βάση δεδομένων των εργαζόμενων
                        intent.putExtra("name",name.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //onclicklistener για το register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                //χρησιμοποιω το launcher για να ανοιξω το register activity
                Launcher.launch(intent);
            }
        });
    }

}
