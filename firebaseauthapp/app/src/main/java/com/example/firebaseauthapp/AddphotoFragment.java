package com.example.firebaseauthapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddphotoFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private LinearLayout checkboxContainer;
    public String firstLabel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addphoto, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        checkboxContainer = view.findViewById(R.id.checkboxContainer);

        Button captureButton = view.findViewById(R.id.captureButton);
        captureButton.setOnClickListener(v -> dispatchTakePictureIntent());

        // Firestore'dan verileri al ve checkbox'ları oluştur
        fetchFirestoreDataAndCreateCheckboxes();

        return view;
    }

    private void fetchFirestoreDataAndCreateCheckboxes() {
        firestore.collection("MyData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> labels = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String label = document.getString("label");
                                if (label != null && !label.isEmpty()) {
                                    labels.add(label);
                                }
                            }
                            createCheckboxes(labels);
                        }
                    }
                });
    }

    private void createCheckboxes(List<String> labels) {
        for (String label : labels) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(label);
            checkboxContainer.addView(checkBox);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Checkbox seçildiyse, resmin ismini al ve firstLabel değişkenine ata
                    firstLabel = label;
                }
            });
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            // Yeni bir isimle resmi Firebase Storage'a yükle
            uploadImageWithNewName(imageBitmap, firstLabel+".jpg");

            ImageView capturedImageView = requireView().findViewById(R.id.capturedImageView);
            capturedImageView.setImageBitmap(imageBitmap);
        }
    }

    private void uploadImageWithNewName(Bitmap imageBitmap, String newImageName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        StorageReference storageReference = storage.getReference().child("images/" + newImageName);

        storageReference.putBytes(imageData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateImageNameInFirestore(newImageName);
                    } else {
                        Log.e("FirebaseStorage", "Fotoğraf Yüklenirken Hata Oluştu!", task.getException());
                    }
                });
    }

    private void updateImageNameInFirestore(String newImageName) {
        DocumentReference documentReference = firestore.collection("your_collection").document("your_document");

        Map<String, Object> updates = new HashMap<>();
        updates.put("image_name", newImageName);

        documentReference.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Resmi Yüklendi Ve İsim Başarıyla Güncellendi");
                    } else {
                        Log.e("Firestore", "Resmin İsmi Güncellenirken Hata Verdi!", task.getException());
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
