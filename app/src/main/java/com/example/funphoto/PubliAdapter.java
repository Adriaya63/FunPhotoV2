package com.example.funphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PubliAdapter extends RecyclerView.Adapter<PubliAdapter.ImageViewHolder> {

    private List<Publicacion> imageList;

    public PubliAdapter(List<Publicacion> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foto, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Publicacion image = imageList.get(position);
        holder.profileName.setText(image.getProfileName());
        holder.fotoDesc.setText(image.getPhotoDescription());
        Bitmap bitmap = BitmapFactory.decodeFile(image.getImagePath());
        holder.imageViewProfile.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView profileName;
        ImageView imageViewProfile;
        TextView fotoDesc;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.profileName);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            fotoDesc = itemView.findViewById(R.id.fotoDesc);
        }
    }
}

