package com.alc.challenge2.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alc.challenge2.travelmantics.models.TravelDealModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DealActivity extends AppCompatActivity {

    private static final String TAG = "DealActivity";
    private Context mContext = DealActivity.this;
    private DatabaseReference mDatabaseReference;
    private static final int PICTURE_RESULT = 42;
    AppCompatEditText etTitle, etDescription, etPrice;
    TextInputLayout titleInputLayout, descriptionInputLayout, priceInputLayout;
    AppCompatImageView imageView;
    AppCompatButton uploadButton;
    TravelDealModel deal = null;

    Uri imageUri;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        etTitle = findViewById(R.id.et_Title);
        etDescription = findViewById(R.id.et_Description);
        etPrice = findViewById(R.id.et_Price);
        imageView =  findViewById(R.id.image);

        titleInputLayout = findViewById(R.id.titleInputLayout);
        descriptionInputLayout = findViewById(R.id.descriptionInputLayout);
        priceInputLayout = findViewById(R.id.priceInputLayout);

        View view = findViewById(R.id.deal_activity);

        snackbar = Snackbar.make(view,"", Snackbar.LENGTH_INDEFINITE);

        Intent intent = getIntent();
        TravelDealModel travel_deal = intent.getParcelableExtra("Travel_Deal");
        if (travel_deal!=null) {
            deal = new TravelDealModel();
            deal = travel_deal;
            etTitle.setText(deal.getTitle());
            etDescription.setText(deal.getDescription());
            etPrice.setText(deal.getPrice());
            showImage(deal.getImageUrl());
        }

        uploadButton = findViewById(R.id.btnLoadImage);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent,
                        "Insert Picture"), PICTURE_RESULT);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                if(saveDeal()){
                    Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                    clean();
                    finish();
                }

                return true;
            case R.id.delete_menu:
                if(deleteDeal()){
                    Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        }
        else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }


        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            imageUri = data.getData();
            uploadImage(imageUri);
        }

    }

    private boolean saveDeal() {

        if(!validateTitle() || !validateDescription() || !validatePrice()){
            if(!validateTitle() && !validateDescription() && !validatePrice())
                return false;

            return false;
        }else {
            deal.setTitle(etTitle.getText().toString());
            deal.setDescription(etDescription.getText().toString());
            deal.setPrice(etPrice.getText().toString());

            if(deal.getId()==null) {
                mDatabaseReference.push().setValue(deal);
            }
            else {
                mDatabaseReference.child(deal.getId()).setValue(deal);
            }
            return true;
        }

    }

    private boolean validateTitle() {

        if(etTitle.getText().toString().isEmpty()){
            titleInputLayout.setError(getString(R.string.error_title_required));

            return false;
        }else{
            titleInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateDescription() {

        if(etDescription.getText().toString().isEmpty()){
            descriptionInputLayout.setError(getString(R.string.error_description_required));

            return false;
        }else{
            descriptionInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePrice() {
        String title = etTitle.getText().toString().trim();

        if(etPrice.getText().toString().isEmpty()){
            priceInputLayout.setError(getString(R.string.error_price_required));

            return false;
        }else if(etPrice.getText().toString().equals("0")){
            priceInputLayout.setError(getString(R.string.error_price_0));
            return false;
        }else{
            priceInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(deal.getId() == null && deal.getImageName()!= null){
            StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image", "Image Successfully Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image", e.getMessage());
                }
            });
            return true;
        }else {
            mDatabaseReference.child(deal.getId()).removeValue();

            if(deal.getImageName() != null && !deal.getImageName().isEmpty()) {
                StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Delete Image", "Image Successfully Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Delete Image", e.getMessage());
                    }
                });
            }
            return true;
        }

    }

    private void clean() {
        etTitle.setText("");
        etPrice.setText("");
        etDescription.setText("");
        etTitle.requestFocus();
    }
    private void enableEditTexts(boolean isEnabled) {
        etTitle.setEnabled(isEnabled);
        etDescription.setEnabled(isEnabled);
        etPrice.setEnabled(isEnabled);
        if(isEnabled){
            uploadButton.setVisibility(View.VISIBLE);
        }else {
            uploadButton.setVisibility(View.GONE);
        }
    }
    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {

            Glide.with(this)
                    .load(url)
                    .fitCenter()
                    .into(imageView);
        }
    }

    private void uploadImage(Uri uri){
        deal = new TravelDealModel();
        final StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());

        final String[] pictureN = new String[1];

        ref.putFile(uri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                ProgressBar progressBar = findViewById(R.id.progressBar2);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);

                if(snackbar.isShown()){
                    snackbar.setText("Image upload progress: " + progress + "%");
                }else {
                    snackbar.setText("Image upload progress: " + progress + "%");
                    snackbar.show();
                }

            }
        }).onSuccessTask(new SuccessContinuation<UploadTask.TaskSnapshot, Uri>() {
            @NonNull
            @Override
            public Task<Uri> then(@Nullable UploadTask.TaskSnapshot taskSnapshot) throws Exception {
                pictureN[0] = taskSnapshot.getStorage().getPath();
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                ProgressBar progressBar = findViewById(R.id.progressBar2);
                progressBar.setVisibility(View.GONE);
                snackbar.dismiss();
                Toast.makeText(mContext, "Image has uploaded successfully", Toast.LENGTH_SHORT).show();
                String url = task.getResult().toString();
                deal.setImageUrl(url);
                deal.setImageName(pictureN[0]);

                showImage(url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                View view = findViewById(R.id.deal_activity);
                Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                Snackbar.make(view, R.string.upload_image_failed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Respond to the click, such as by undoing the modification that caused
                                // this message to be displayed
                                uploadImage(imageUri);
                            }
                        }).show();

            }
        });



    }

}
