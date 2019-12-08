package io.banj.gong.models;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import io.banj.gong.R;

public class GongRecyclerAdapter extends RecyclerView.Adapter<GongRecyclerAdapter.GongViewHolder> {

    Context ct2004;
    ArrayList<GongModel> gongModelsSource;

    Typeface cooperBoldItalic;


    public GongRecyclerAdapter(Context ct2004, ArrayList<GongModel> gongModelsSource) {
        this.ct2004 = ct2004;
        this.gongModelsSource = gongModelsSource;
        cooperBoldItalic = Typeface.createFromAsset(ct2004.getAssets(), "cooper_std_black_italic.ttf");

    }

    @NonNull
    @Override
    public GongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View receivedGongView = LayoutInflater.from(ct2004).inflate(R.layout.gong_recyclable_element, viewGroup, false);

        GongViewHolder gongViewHolder = new GongViewHolder(receivedGongView);

        gongViewHolder.whoGongD.setTypeface(cooperBoldItalic);
//        gongViewHolder.niceThisGong.setTypeface(cooperBoldItalic);

        gongViewHolder.whoGongD.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        gongViewHolder.niceThisGong.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);


        return gongViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GongViewHolder viewHolder, int i) {

        viewHolder.whoGongD.setText(gongModelsSource.get(i).from);
        viewHolder.niceThisGong.setTag(gongModelsSource.get(i));
        viewHolder.niceThisGong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "", Snackbar.LENGTH_LONG).setAction("NICE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }).show();

//                gongModelsSource.remove();
                int position  = gongModelsSource.indexOf((GongModel) view.getTag());
                gongModelsSource.remove((GongModel) view.getTag());
                notifyItemRemoved(position);
            }
        });

        float scale = Math.abs(Math.min(1, 8.3f *  (float) 1 / (gongModelsSource.get(i).from.hashCode() % 12)));

        viewHolder.timeLeft.setScaleY(scale);
        viewHolder.timeLeft.setScaleX(scale);

    }

    @Override
    public int getItemCount() {
        return gongModelsSource.size();
    }


    class GongViewHolder extends RecyclerView.ViewHolder{

        int index = -1;
        TextView whoGongD;
        View timeLeft;
        Button niceThisGong;

        public GongViewHolder(@NonNull View itemView) {
            super(itemView);
            whoGongD = itemView.findViewById(R.id.whoGongdText);
            timeLeft = itemView.findViewById(R.id.timeLeftCircle);
            niceThisGong = itemView.findViewById(R.id.nice_button);
        }
    }

}
