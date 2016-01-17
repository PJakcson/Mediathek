package com.marcn.mediathek.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.LiveStreams;
import com.marcn.mediathek.ui_fragments.LiveStreamFragment.OnListFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LiveStreamAdapter extends RecyclerView.Adapter<LiveStreamAdapter.ViewHolder> {

    private final LiveStreams mValues;
    private final OnListFragmentInteractionListener mListener;

    public LiveStreamAdapter(LiveStreams items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void updateValue(LiveStream l) {
        int index = mValues.mLiveStreams.indexOf(l);
        mValues.pushLiveStream(l);
        if (index < 0)
            notifyDataSetChanged();
        else
            notifyItemChanged(index);
    }

    public void updateValues(ArrayList<LiveStream> ls) {
        mValues.pushLiveStreams(ls);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_livestream, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitle.setText(holder.mItem.channel);
        Context context = holder.mView.getContext();

        // Thumbnail Image
        String thumb = holder.mItem.getThumb_url();
        if (thumb == null || thumb.isEmpty())
            holder.mThumb.setImageResource(R.drawable.placeholder_stream);
        else
            Picasso.with(context)
                    .load(thumb)
                    .placeholder(R.drawable.placeholder_stream)
                    .config(Bitmap.Config.RGB_565)
                    .into(holder.mThumb);

        // Logo Image
        int logo = holder.mItem.getLogoResId();
        if (logo > 0)
            holder.mLogo.setImageResource(logo);

        // OnClick
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumb, mLogo;
        public final TextView mTitle;
        public LiveStream mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumb = (ImageView) view.findViewById(R.id.imageThumbnail);
            mLogo = (ImageView) view.findViewById(R.id.imageLogo);
            mTitle = (TextView) view.findViewById(R.id.textTitle);
        }
    }
}
