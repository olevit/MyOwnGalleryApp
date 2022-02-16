package com.example.myowngalleryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public class MyRecyclerViewAdapter extends
            RecyclerView.Adapter<MyRecyclerViewAdapter.ItemHolder> {

        private List<Uri> itemsUri;
        private LayoutInflater layoutInflater;
        private Context context;

        public MyRecyclerViewAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            itemsUri = new ArrayList<Uri>();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            CardView itemCardView = (CardView) layoutInflater.inflate(R.layout.activity_main, viewGroup, false);
            return new ItemHolder(itemCardView, this);
        }

        @Override
        public void onBindViewHolder(ItemHolder itemHolder, int i) {

            Uri targetUri = itemsUri.get(i);
            itemHolder.setItemUri(targetUri.getPath());

            if (targetUri != null) {

                try {
                    //! CAUTION !
                    //I'm not sure is it properly to load bitmap here!
                    itemHolder.setImageView(loadScaledBitmap(targetUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
        reference:
        Load scaled bitmap
        http://android-er.blogspot.com/2013/08/load-scaled-bitmap.html
         */
        private Bitmap loadScaledBitmap(Uri src) throws FileNotFoundException {

            // required max width/height
            final int REQ_WIDTH = 400;
            final int REQ_HEIGHT = 400;

            Bitmap bm = null;

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(src),
                    null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, REQ_WIDTH,
                    REQ_HEIGHT);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeStream(
                    context.getContentResolver().openInputStream(src), null, options);

            return bm;
        }

        public int calculateInSampleSize(BitmapFactory.Options options,
                                         int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                // Calculate ratios of height and width to requested height and
                // width
                final int heightRatio = Math.round((float) height
                        / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);

                // Choose the smallest ratio as inSampleSize value, this will
                // guarantee
                // a final image with both dimensions larger than or equal to the
                // requested height and width.
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }

            return inSampleSize;
        }

        @Override
        public int getItemCount() {
            return itemsUri.size();
        }

        public void add(int location, Uri iUri) {
            itemsUri.add(location, iUri);
            notifyItemInserted(location);
        }

        public void clearAll() {
            int itemCount = itemsUri.size();

            if (itemCount > 0) {
                itemsUri.clear();
                notifyItemRangeRemoved(0, itemCount);
            }
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            private MyRecyclerViewAdapter parent;
            private CardView cardView;
            TextView textItemUri;
            ImageView imageView;

            public ItemHolder(CardView cView, MyRecyclerViewAdapter parent) {
                super(cView);
                cardView = cView;
                this.parent = parent;
                textItemUri = (TextView) cardView.findViewById(R.id.item_uri);
                imageView = (ImageView) cardView.findViewById(R.id.item_image);
            }

            public void setItemUri(CharSequence name) {
                textItemUri.setText(name);
            }

            public void setImageView(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }

        }
    }
}