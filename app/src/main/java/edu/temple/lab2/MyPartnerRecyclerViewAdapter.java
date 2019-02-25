package edu.temple.lab2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyPartnerRecyclerViewAdapter extends RecyclerView.Adapter<MyPartnerRecyclerViewAdapter.ViewHolder> {

    private final List<Partner> mValues;
    private double mlat;
    private double mlon;

    public MyPartnerRecyclerViewAdapter(List<Partner> items, double mlat, double mlon) {
        mValues = items;
        this.mlat = mlat;
        this.mlon = mlon;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_partner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mDistanceView.setText(String.valueOf(mValues.get(position).getDistance(mlat, mlon)));
        holder.mNameView.setText(mValues.get(position).id);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDistanceView;
        public final TextView mNameView;
        public Partner mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDistanceView = (TextView) view.findViewById(R.id.distance);
            mNameView = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public String toString() {
            return super.toString() + " 'Username: " + mNameView.getText() + " Distance: " + "'";
        }
    }
}
