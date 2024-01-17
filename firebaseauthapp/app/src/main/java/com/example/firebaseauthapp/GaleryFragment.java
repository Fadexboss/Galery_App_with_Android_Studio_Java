package com.example.firebaseauthapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;


public class GaleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_galery, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1)); // Sadece bir sütunlu bir grid

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("images");

        // Google Storage'dan resimleri çek ve RecyclerView'e ekle
        fetchImages();

        return view;
    }

    private void fetchImages() {
        storageRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<String> imageUrls = new ArrayList<>();
                    for (StorageReference item : listResult.getItems()) {
                        // Her resmin URL'sini al
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            imageUrls.add(imageUrl);

                            // RecyclerView için adapter'ı oluştur ve resimleri göster
                            imageAdapter = new ImageAdapter(imageUrls);
                            recyclerView.setAdapter(imageAdapter);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Hata durumunda işlemleri burada ele alabilirsiniz
                    e.printStackTrace();
                });
    }

    private static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

        private List<String> imageUrls;

        public ImageAdapter(List<String> imageUrls) {
            this.imageUrls = imageUrls;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String imageUrl = imageUrls.get(position);

            // Glide kütüphanesi ile resmi yükle ve ImageView'e set et
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return imageUrls.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }
}
