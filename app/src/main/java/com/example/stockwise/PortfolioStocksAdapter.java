package com.example.stockwise;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.List;

public class PortfolioStocksAdapter extends RecyclerView.Adapter<PortfolioStocksAdapter.PortfolioStocksViewHolder> {

    private List<String> portfolioStocks;

    private OnItemClickListener onItemClickListener;


    public PortfolioStocksAdapter(List<String> portfolioStocks, OnItemClickListener onItemClickListener) {
        this.portfolioStocks = portfolioStocks;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public PortfolioStocksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_item2, parent, false);
        return new PortfolioStocksViewHolder(view);
    }
    public interface OnItemClickListener {
        void onItemClick(String stockSymbol);
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioStocksViewHolder holder, int position) {

        String combined = portfolioStocks.get(position);
        String[] parts = combined.split("\n");

        holder.textStockSymbol.setText(parts[0]);
        holder.textStockName.setText(parts[1]);
        Log.d("PortfolioAdapter", "Binding view: " + combined);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(combined);
            }
        });

        String selectedSymbol = parts[0]; // Get the stock symbol

        // Fetch close price for the current symbol and update the respective TextView
        Double[] recentClosePriceData = getRecentClosePrice(selectedSymbol);
        Double closePrice = recentClosePriceData[0];
        Double priceChange = recentClosePriceData[1];

        if (closePrice != null) {
            double roundedPriceChange = Math.round(priceChange * 100.0) / 100.0;
            holder.textStockClosePrice.setText(String.format("%.2f", closePrice));

            if (roundedPriceChange > 0) {
                holder.textStockClosePrice.setTextColor(Color.parseColor("#008000"));
                holder.imgPriceChange2.setImageResource(R.drawable.baseline_arrow_upward_24);
            } else if (roundedPriceChange < 0) {
                holder.textStockClosePrice.setTextColor(Color.parseColor("#FF0000"));
                holder.imgPriceChange2.setImageResource(R.drawable.baseline_arrow_downward_24);
            } else {
                holder.textStockClosePrice.setTextColor(Color.parseColor("#000000")); // Reset to default color
                holder.imgPriceChange2.setImageResource(R.drawable.neutral_symbol);
            }
        } else {
            holder.textStockClosePrice.setText("N/A");
            holder.textStockClosePrice.setTextColor(Color.parseColor("#000000")); // Reset to default color
            holder.imgPriceChange2.setImageResource(R.drawable.neutral_symbol);
        }
    }


    private Double[] getRecentClosePrice(String symbol) {
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("myscript");
        PyObject recentClosePriceData = pyObject.callAttr("get_recent_close_price", symbol);

        Double[] closePriceData = new Double[2];
        try {
            String RCPDstring = recentClosePriceData.toString().replace("(", "").replace(")", "");
            if (!"None, None".equals(RCPDstring)) {
                String[] tokens = RCPDstring.split(",");
                closePriceData[0] = Double.parseDouble(tokens[0]);
                closePriceData[1] = Double.parseDouble(tokens[1]);
            } else {
                closePriceData[0] = null; // Assign null instead of -1
                closePriceData[1] = 0.0;
            }
        } catch (PyException e) {
            e.printStackTrace(); // Handle any exceptions here
            closePriceData[0] = null;
            closePriceData[1] = 0.0;
        }

        return closePriceData;
    }


    public static class PortfolioStocksViewHolder extends RecyclerView.ViewHolder {
        TextView textStockSymbol;
        TextView textStockName;
        TextView textStockClosePrice;
        ImageView imgPriceChange2;

        public PortfolioStocksViewHolder(View itemView) {
            super(itemView);
            textStockSymbol = itemView.findViewById(R.id.textStockSymbol);
            textStockName = itemView.findViewById(R.id.textStockName);
            textStockClosePrice = itemView.findViewById(R.id.textStockClosePrice);
            imgPriceChange2 = itemView.findViewById(R.id.imgPriceChange2);
        }
    }

    @Override
    public int getItemCount() {
        return portfolioStocks.size();
    }

    public String getStockAtPosition(int position) {
        if (position >= 0 && position < portfolioStocks.size()) {
            return portfolioStocks.get(position);
        } else {
            Log.d("SwipeDebug", "Item to delete is null or position is out of bounds");
            return null;
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < portfolioStocks.size()) {
            portfolioStocks.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount()); // Notify any remaining items have changed
        }
    }





    public void setData(List<String> newData) {
        this.portfolioStocks = newData;
        notifyDataSetChanged();
    }



}