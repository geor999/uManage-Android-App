package com.example.uManage.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uManage.R;
import com.example.uManage.activity_classes.UpdateWorker;
import com.example.uManage.database.WorkersDatabase;
import com.example.uManage.object_classes.Worker;

import java.util.List;

@SuppressWarnings("CanBeFinal")
public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.workerViewholder> {
    //λίστα με εργαζόμενους
    List<Worker> workerList;
    Context context;
    WorkersDatabase workersDatabase;
    public WorkerAdapter(List<Worker> workerList, WorkersDatabase workersDatabase, Context context) {
        this.workerList = workerList;
        this.context = context;
        this.workersDatabase = workersDatabase;
    }


    //δημιουργώ το viewholder
    @NonNull
    @Override
    public workerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_tab, parent, false);
        return new workerViewholder(view);
    }

    //συνδέω το viewholder με το recycler view
    @Override
    public void onBindViewHolder(@NonNull workerViewholder holder, int position) {
        holder.name.setText(workerList.get(position).getName());
        holder.num.setText(String.valueOf(workerList.get(position).getAge()));
        holder.priority.setText(String.valueOf(workerList.get(position).getSalary()));
        holder.imgview.setImageBitmap(getImage(workerList.get(position).getImage()));
        //onlongclicklistener για την δημιουργία του popup μενού για τα αντικείμενα του reycler view
        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //init το popup μενού
                PopupMenu popupMenu=new PopupMenu(context,v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.update:
                                Intent intent = new Intent(context, UpdateWorker.class);
                                //δίνω σαν extra το ID του εργαζομένου στον οποίο έκανα longclick
                                intent.putExtra("ID", workerList.get(holder.getAdapterPosition()).getId());
                                //εδώ δεν μπορούσαμε να χρησιμοποιήσουμε launcher οπότε βάλαμε startactivityforresult,το 8 υπάρχει σαν resultcode στην onActivityResult της ListActivity
                                ((Activity) context).startActivityForResult(intent,8);
                                return false;
                            case R.id.remove:
                                //ψάχνω στον πίνακα και διαγράφω απο την βάση
                                for(int i = 0; i< workerList.size(); i++)
                                {
                                    if (workerList.get(i).getId()== workerList.get(holder.getAdapterPosition()).getId())
                                    {
                                        Worker worker = workerList.get(i);
                                        workersDatabase.delete(worker.getId());
                                    }
                                }
                                ((Activity) context).recreate();
                                return false;
                        }
                        return true;
                    }
                });
                return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return workerList.size();
    }

    //συνάρτηση για μετατροπή απο byte[] σε bitmap
    public Bitmap getImage(byte[] imgByte){
        if (imgByte != null) {
            return Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imgByte, 0 ,imgByte.length),50,50,false);
        }
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @SuppressWarnings("CanBeFinal")
    //init το viewholder
    public static class workerViewholder extends RecyclerView.ViewHolder {
        ImageView imgview;
        TextView name;
        TextView num;
        TextView priority;
        ConstraintLayout constraintLayout;
        public workerViewholder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.name_main_tab);
            num= itemView.findViewById(R.id.age_main_tab);
            priority= itemView.findViewById(R.id.salary_main_tab);
            imgview= itemView.findViewById(R.id.image_main_tab);
            constraintLayout= itemView.findViewById(R.id.constraintlayoutbutton);
        }
    }
}
