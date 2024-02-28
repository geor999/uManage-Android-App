package com.example.uManage.activity_classes;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uManage.R;
import com.example.uManage.adapters.WorkerAdapter;
import com.example.uManage.database.PreferencesDatabase;
import com.example.uManage.database.WorkersDatabase;
import com.example.uManage.object_classes.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListActivity extends AppCompatActivity {

    String sort,theme;//strings που χρησιμοποιούνται για να αποθηκέυσω το sorting pattern, και το theme της εφαρμογής(διαφοροποιείται ανά λογαριασμό)
    PreferencesDatabase preferencesDatabase;

    RecyclerView recyclerView;//recycler view για την εμφανισή των αντικειμένων
    RecyclerView.LayoutManager layoutManager;//layout manager που χρησιμοποιείται για το recycler view
    WorkerAdapter workerAdapter;//adapter που χρησιμοποιείται για το recycler view

    //βάση για την αποθήκευση των εργαζόμενων και μια λίστα για την αποθηκευσή τους και την χρήση τους(sorting,εμφάνιση,επεξεργασία)
    WorkersDatabase workersDatabase;
    final List<Worker> workerList = new ArrayList<>();

    //launcer που όπου και αν κληθεί μετά την επιστροφή του κάνει recreate το συγκεκριμένο activity
    final ActivityResultLauncher<Intent> Launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> recreate());


    String name;



    //inits για τα Analytics
    double avgage=0,avgsal=0,sumsalary;
    int workers;
    TextView avgAge,avgSal,sumSal,numWork;



    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
        //απόδοση τιμής στην μεταβλητή name που θα χρησιμοποιηθεί για τα addworker,updateworker
        if(getIntent().getExtras()!=null)
        {
            Bundle extras = getIntent().getExtras();
            name=extras.getString("name");
        }
        //δημιουργία instance βάσης preferences για sorting και theme
        preferencesDatabase = new PreferencesDatabase(this);
        //συνάρτηση preferences που παίρνει απτήν βάση πληροφορίες για το sorting και το theme
        preferences();

        //δημιουργία instance βάσης workers
        workersDatabase = new WorkersDatabase(this);
        //παίρνω όλους τους εργαζόμενους που δουλεύουν στην εταιρία με όνομα name
        Cursor cursor = workersDatabase.allEntries();
        if(cursor.getCount()!=0) {
            while (cursor.moveToNext()) {
                if (Objects.equals(cursor.getString(6), name)) {
                    Worker newWorker = new Worker(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), cursor.getString(4), cursor.getString(6), (cursor.getBlob(5)));
                    workerList.add(newWorker);
                }
                //αν δεν είναι null κάνω sorting, 1 για descending 0 για ascending
                if (workerList != null) {
                        if (Objects.equals(sort, "1")) {
                            for (int i = 0; i < workerList.size(); i++) {
                                for (int j = 0; j < workerList.size()-i-1; j++) {
                                    Worker tmp;
                                    if (workerList.get(j).getSalary() < workerList.get(j+1).getSalary()) {
                                        tmp = workerList.get(j);
                                        workerList.set(j, workerList.get(j+1));
                                        workerList.set(j+1, tmp);
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < workerList.size(); i++) {
                                for (int j = 0; j < workerList.size() - i - 1; j++) {
                                    Worker tmp;
                                    if (workerList.get(j).getSalary() > workerList.get(j + 1).getSalary()) {
                                        tmp = workerList.get(j);
                                        workerList.set(j, workerList.get(j + 1));
                                        workerList.set(j + 1, tmp);
                                    }
                                }
                            }
                        }
                }
            }
        }
        //κλήση της analytics για την δημιουργία του field των analytics
        analytics();

        //layoutmanager,adapter για το recycleview
        recyclerView = findViewById(R.id.recycler_view_main_tab);
        recyclerView.hasFixedSize();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        workerAdapter = new WorkerAdapter(workerList, workersDatabase, this);
        recyclerView.setAdapter(workerAdapter);
    }

    private void preferences() {
        //παίρνω όλο τον πίνακα των preferences και ψάχνω για την εταιρία με όνομα name,αν αυτή δεν υπάρχει δεν κάνω τίποτα. Ουσιαστικά έχει default τιμή 0 και ascending
        Cursor cursor = preferencesDatabase.allEntries();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                if (Objects.equals(cursor.getString(3), name)) {
                    if (cursor.getString(1) != (null)) {
                        theme = cursor.getString(1);
                    } else {
                        theme = "light";
                    }
                    if (cursor.getString(2) != (null)) {
                        sort = cursor.getString(2);
                    } else {
                        sort = "0";
                    }
                    if (theme.equals("light")) {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                    } else {
                        if (theme.equals("dark")) {
                            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                        }
                    }
                }
            }
        }
    }
    //δημιουργώ το menu που υπάρχει sto menu.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    //μέθοδος που χρησιμοποιείται για την διαχείρηση του μενου, η διαδικασία που ακολουθείται είναι η ίδια οπότε η εξήγηση είναι στο case R.id.ascending_menu_tab
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Cursor cursor=preferencesDatabase.allEntries();
         switch (item.getItemId()) {
             //+ για την προσθήκη του εργαζόμενου
            case R.id.add_new_worker_menu_tab:
                Intent intent = new Intent(this, AddWorker.class);
                //δίνω σαν extra όπως και πρίν το όνομα της εταιρίας
                intent.putExtra("name",name);
                Launcher.launch(intent);
                return true;
                //επιλογή για ascending sorting
             case R.id.ascending_menu_tab:
                 //αν η βάση είναι άδεια τότε δημιουργεί το πρώτο field της, διαφορετικά ψάχνει να δεί αν το username(δηλαδή το όνομα της εταιρίας) είναι ίδιο με το τελευταίο field
                 //αν αυτό είναι τότε κάνει update και επομένως recreate ώστε να πάρουν μορφή οι αλλαγές διαφορετικά δημιουργεί νέο field στη βάση για την εταιρία αυτήν
                 if(cursor.getCount()!=0) {
                     while (cursor.moveToNext())
                     {
                         if (Objects.equals(cursor.getString(3), name))
                         {
                             preferencesDatabase.update(cursor.getInt(0),theme, name,"0");
                             recreate();
                             return true;
                         }
                     }

                 }
                 preferencesDatabase.addEntry(theme, name,"0");
                 recreate();
                 return true;
             case R.id.descending_menu_tab:
                 if(cursor.getCount()!=0) {
                     while (cursor.moveToNext())
                     {
                         if (Objects.equals(cursor.getString(3), name))
                         {
                             preferencesDatabase.update(cursor.getInt(0),theme, name,"1");
                             recreate();
                             return true;
                         }
                     }
                 }
                 preferencesDatabase.addEntry(theme, name,"1");
                 recreate();
                 return true;
                 case R.id.dark_theme_menu_tab:
                 if(cursor.getCount()!=0) {
                     while (cursor.moveToNext())
                     {
                         if (Objects.equals(cursor.getString(3), name))
                         {
                             preferencesDatabase.update(cursor.getInt(0),"dark", name,sort);
                             recreate();
                             return true;
                         }
                     }
                 }
                 preferencesDatabase.addEntry("dark", name,sort);
                 recreate();
                 return true;
                 case R.id.light_theme_menu_tab:
                     if(cursor.getCount()!=0) {
                         while (cursor.moveToNext()) {
                             if (Objects.equals(cursor.getString(3), name)) {
                                 preferencesDatabase.update(cursor.getInt(0), "light", name, sort);
                                 recreate();
                                 return true;
                             }
                         }
                     }
                     preferencesDatabase.addEntry("light", name,sort);
                     recreate();
                     return true;
         }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        workerAdapter.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 8) {
            recreate();
        }
        if (resultCode == 7) {
            recreate();
        }
    }
    @Override
    public void onBackPressed() {
    }

    //init τα analytics και υπολογισμών τιμών για αυτά
    public void analytics(){
        for (int i = 0; i < workerList.size(); i++) {
            avgage+=workerList.get(i).getAge();
            avgsal+=workerList.get(i).getSalary();
        }
        sumsalary=avgsal;
        avgage/=workerList.size();
        avgsal/=workerList.size();
        workers=workerList.size();
        TextView avgAge,avgSal,sumSal,numWork;
        avgAge=findViewById(R.id.avg_age_db_brief_main_tab);
        avgSal=findViewById(R.id.avg_salary_db_brief_main_tab);
        numWork=findViewById(R.id.total_workers_db_brief_main_tab);
        sumSal=findViewById(R.id.salary_sum_db_brief_main_tab);
        avgAge.setText(String.valueOf(avgage));
        avgSal.setText(String.valueOf(avgsal));
        numWork.setText(String.valueOf(workers));
        sumSal.setText(String.valueOf(sumsalary));
    }
}


