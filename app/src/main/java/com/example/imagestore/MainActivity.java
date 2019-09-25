package com.example.imagestore;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button chooseButton,saveButton,displayButton;
    private EditText editText;
    private ImageView imageView;
    private ProgressBar progressBar;
    //uri uniform resource identifier
    private Uri imageUri;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    StorageTask storageTask;

    private static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("Upload");
        storageReference = FirebaseStorage.getInstance().getReference("Upload");

        chooseButton = findViewById(R.id.chooseImageButtonID);
        saveButton = findViewById(R.id.saveImageButtonID);
        displayButton = findViewById(R.id.displayImageButtonID);
        editText = findViewById(R.id.imageEditTextID);
        imageView = findViewById(R.id.imageID);
        progressBar = findViewById(R.id.progressBarID);

        chooseButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        displayButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.chooseImageButtonID:
                OpenFileChooser();
                break;

            case R.id.saveImageButtonID:
                if(storageTask!=null && storageTask.isInProgress()){
                    Toast.makeText(MainActivity.this,"Upload is in progress",Toast.LENGTH_LONG).show();
                }
                else {
                    SaveData();
                }

                break;
            case R.id.displayImageButtonID:
                Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                startActivity(intent);
                break;
        }
    }

    //this code for choose
    public void OpenFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    //this code for image load on imageView
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if condition is true then load image on imageView
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(imageView);
        }
    }

    //Image extension getting
    public String GetFileExtension(Uri imageUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    public void SaveData(){
        final String imageName = editText.getText().toString().trim();
        if(imageName.isEmpty()){
            editText.setError("Enter the image name");
            editText.requestFocus();
            return;
        }

        try{
            StorageReference ref = storageReference.child(System.currentTimeMillis()+"."+GetFileExtension(imageUri));

            //upload data
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this,"Saved Successfully",Toast.LENGTH_LONG).show();

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                    Uri downloadUri = uriTask.getResult();

                    Upload upload = new Upload(imageName,downloadUri.toString());
                    String upLoadKey = databaseReference.push().getKey();
                    databaseReference.child(upLoadKey).setValue(upload);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(MainActivity.this,"Image is not save",Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this,"Please choose a image",Toast.LENGTH_LONG).show();
        }
    }




}
