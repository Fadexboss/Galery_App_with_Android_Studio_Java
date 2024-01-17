package com.example.firebaseauthapp;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
public class AddlabelFragment extends Fragment {

    private EditText editText1, editText2;
    private Button saveButton;
    private FirebaseFirestore firestore;

    private EditText editText;
    private LinearLayout checkboxContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addlabel, container, false);

        editText1 = view.findViewById(R.id.editText1);
        editText2 = view.findViewById(R.id.editText2);
        saveButton = view.findViewById(R.id.saveButton);
        firestore = FirebaseFirestore.getInstance();
        checkboxContainer = view.findViewById(R.id.checkboxContainer);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirestore();
            }
        });

        return view;
    }

    private void saveDataToFirestore() {
        String value1 = editText1.getText().toString().trim();
        String value2 = editText2.getText().toString().trim();

        // data map oluştur
        Map<String, Object> data = new HashMap<>();
        data.put("label", value1);
        data.put("Description", value2);


        if (!value1.isEmpty()) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(value1);

            // CheckBox'ı LinearLayout içine ekle
            checkboxContainer.addView(checkBox);

            // EditText'i temizle
            editText1.getText().clear();
        }

        // Add data to Firestore
        firestore.collection("MyData")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    showToast("Veri Firestore'a Eklendi");
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    showToast("Verİ Firestore Eklenirken Hata verdi! " + e.getMessage());
                });
    }

    private void showToast(String message) {
        // Display a toast message
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
