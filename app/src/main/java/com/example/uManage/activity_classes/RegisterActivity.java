package com.example.uManage.activity_classes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uManage.R;
import com.example.uManage.database.UsersDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText username,password,repassword,email;
    String  name,pass,repass,emaill;
    Button button;
    UsersDatabase usersDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_tab);
        //δημιουργώ βάση
        usersDatabase=new UsersDatabase(this);
        //init components
        username=(EditText) findViewById(R.id.editTextTextPersonName);
        password=(EditText)findViewById(R.id.editTextTextPassword);
        repassword=(EditText)findViewById(R.id.editTextTextPassword2);
        email=(EditText)findViewById(R.id.EmailAddressLogin);
        button=(Button) findViewById(R.id.submitButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = username.getText().toString();
                pass = password.getText().toString();
                repass = repassword.getText().toString();
                emaill = email.getText().toString();
                //απαραίτητοι έλεγχοι για τα inputs
                if (name.equals("") || pass.equals("") || repass.equals(""))
                    Toast.makeText(RegisterActivity.this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();
                else {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
                        if (pass.equals(repass)) {
                            if (!usersDatabase.checkusername(name)) {
                                if (pass.length() < 8) {
                                    Toast.makeText(RegisterActivity.this, "Password is less than 8 digits!", Toast.LENGTH_SHORT).show();
                                } else {
                                    //αν όλα είναι οκ βάζω στην βάση
                                    usersDatabase.addEntry(name, pass, emaill);
                                    setResult(9, getIntent());
                                    //εντολή finish ώστε να επιστρέφω με το launcher στην Login και να καταστρέψω αυτό το instance της register activity
                                    finish();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Not valid email!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}