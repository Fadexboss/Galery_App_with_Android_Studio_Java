package com.example.firebaseauthapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class AboutFragment extends Fragment {

    private ImageView backgroundImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        backgroundImageView = view.findViewById(R.id.backgroundImageView);

        // Glide kütüphanesi ile resmi yükle ve ImageView'e set et
        Glide.with(requireContext())
                .load(R.drawable.background_image)  // drawable klasöründeki resmi kullanabilirsiniz
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(backgroundImageView);

        return view;
    }
}
