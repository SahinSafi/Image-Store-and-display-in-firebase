package com.example.imagestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
//this process called retrieve
public class ImageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MyAdapter myAdapter;
    private List<Upload> uploadList;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        recyclerView = findViewById(R.id.recyclerViewID);
        progressBar = findViewById(R.id.progressID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        uploadList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Upload");
        firebaseStorage = FirebaseStorage.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploadList.clear();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Upload upload = dataSnapshot1.getValue(Upload.class);
                    upload.setKey(dataSnapshot1.getKey());
                    uploadList.add(upload);
                }
                myAdapter = new MyAdapter(ImageActivity.this, uploadList);
                recyclerView.setAdapter(myAdapter);


                //this interface create in MyAdapter class. this is create for click item
                myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String text = uploadList.get(position).getImageName();
                        Toast.makeText(getApplicationContext(),text+"is selected "+position,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onDoAnyTask(int position) {
                        Toast.makeText(ImageActivity.this,"On Do Any Task",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onDelete(int position) {
                        try{
                            Upload selectedItem = uploadList.get(position);
                            final String key = selectedItem.getKey();
                            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(selectedItem.getImageUrl());
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    databaseReference.child(key).removeValue();
                                    Toast.makeText(ImageActivity.this,"Deleted",Toast.LENGTH_LONG).show();
                                }
                            });
                        }catch (Exception e){
                            Toast.makeText(ImageActivity.this,e+" Exception ",Toast.LENGTH_LONG).show();
                        }


                    }
                });

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImageActivity.this,"Error: "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


}
