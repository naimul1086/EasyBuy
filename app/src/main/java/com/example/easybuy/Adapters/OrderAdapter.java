package com.example.easybuy.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easybuy.Models.CartModel;
import com.example.easybuy.Models.OrderModel;
import com.example.easybuy.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final Context context;
    private List<OrderModel> orderList = new ArrayList<>();

    public interface OrderClickListener {
        void onDetailsClick(OrderModel order);
    }

    private OrderClickListener clickListener;

    public void setOrderClickListener(OrderClickListener listener) {
        this.clickListener = listener;
    }

    public OrderAdapter(Context context, List<OrderModel> list) {
        this.context   = context;
        this.orderList = list != null ? list : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        OrderModel order = orderList.get(position);
        
        String id = order.getOrderId();
        h.tvOrderId.setText("Order #" + (id != null && id.length() >= 8
                ? id.substring(0, 8).toUpperCase() : id));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy · hh:mm a", Locale.getDefault());
        h.tvOrderDate.setText(sdf.format(new Date(order.getOrderTimestamp())));

        String status = order.getStatus() != null ? order.getStatus().toLowerCase() : "pending";
        h.tvOrderStatus.setText(capitalize(status));
        applyStatusStyle(h.tvOrderStatus, status);

        applyTimeline(h, status);

        h.tvOrderTotal.setText(String.format("$%.2f", order.getTotalPrice()));

        int itemCount = order.getItems() != null ? order.getItems().size() : 0;
        h.tvItemCount.setText(itemCount + (itemCount == 1 ? " item" : " items"));

        String promo = order.getPromoCode();
        if (promo != null && !promo.isEmpty()) {
            h.tvPromoApplied.setVisibility(View.VISIBLE);
            h.tvPromoApplied.setText("🏷 " + promo + " applied");
        } else {
            h.tvPromoApplied.setVisibility(View.GONE);
        }

        buildThumbnails(h, order.getItems());

        h.btnOrderDetails.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onDetailsClick(order);
        });
    }

    private void applyTimeline(ViewHolder h, String status) {
        int step = statusToStep(status);

        setStepActive(h.dotPlaced,     step >= 0, R.drawable.bg_circle_white_alpha, true);
        setStepActive(h.dotProcessing, step >= 1, R.drawable.ic_shipping,     false);
        setStepActive(h.dotShipped,    step >= 2, R.drawable.ic_shipping,     false);
        setStepActive(h.dotDelivered,  step >= 3, R.drawable.bg_circle_white_alpha, false);

        setLineActive(h.line1, step >= 1);
        setLineActive(h.line2, step >= 2);
        setLineActive(h.line3, step >= 3);

        setStepLabelColor(h.tvPlaced,      step >= 0);
        setStepLabelColor(h.tvProcessing,  step >= 1);
        setStepLabelColor(h.tvShipped,     step >= 2);
        setStepLabelColor(h.tvDelivered,   step >= 3);
    }

    private int statusToStep(String status) {
        switch (status) {
            case "processing": return 1;
            case "shipped":    return 2;
            case "delivered":  return 3;
            case "cancelled":  return -1;
            default:           return 0;
        }
    }

    private void setStepActive(FrameLayout dot, boolean active,
                               int iconRes, boolean isFirst) {
        if (active) {
            dot.setBackgroundResource(R.drawable.bg_step_active);
            ImageView icon = (ImageView) dot.getChildAt(0);
            if (icon != null) {
                icon.setImageResource(iconRes);
                icon.setColorFilter(Color.WHITE);
            }
        } else {
            dot.setBackgroundResource(R.drawable.bg_step_inactive);
            ImageView icon = (ImageView) dot.getChildAt(0);
            if (icon != null) {
                icon.setImageResource(iconRes);
                icon.clearColorFilter();
                icon.setColorFilter(ContextCompat.getColor(context,
                        android.R.color.darker_gray));
            }
        }
    }

    private void setLineActive(View line, boolean active) {
        if (active) {
            line.setBackgroundColor(ContextCompat.getColor(context, R.color.MainColor));
        } else {
            line.setBackgroundColor(ContextCompat.getColor(context,
                    com.google.android.material.R.color.material_on_surface_stroke));
        }
    }

    private void setStepLabelColor(TextView tv, boolean active) {
        if (tv == null) return;
        tv.setTextColor(active
                ? ContextCompat.getColor(context, R.color.MainColor)
                : ContextCompat.getColor(context, android.R.color.darker_gray));
        tv.setTypeface(null, active
                ? android.graphics.Typeface.BOLD
                : android.graphics.Typeface.NORMAL);
    }

    private void applyStatusStyle(TextView tv, String status) {
        switch (status) {
            case "delivered":
                tv.setTextColor(Color.parseColor("#1B7B3A"));
                tv.setBackgroundResource(R.drawable.bg_stock_badge);
                break;
            case "processing":
            case "shipped":
                tv.setTextColor(Color.parseColor("#B45309"));
                tv.setBackgroundColor(Color.parseColor("#FEF3C7"));
                break;
            case "cancelled":
                tv.setTextColor(Color.parseColor("#B91C1C"));
                tv.setBackgroundColor(Color.parseColor("#FEE2E2"));
                break;
            default:
                tv.setTextColor(Color.parseColor("#1D4ED8"));
                tv.setBackgroundColor(Color.parseColor("#DBEAFE"));
                break;
        }
        tv.setPaddingRelative(
                dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5));
    }

    private static final int MAX_THUMBNAILS = 4;

    private void buildThumbnails(ViewHolder h, List<CartModel> items) {
        h.llItemThumbnails.removeAllViews();
        if (items == null || items.isEmpty()) return;

        int showCount = Math.min(items.size(), MAX_THUMBNAILS);
        for (int i = 0; i < showCount; i++) {
            CartModel item = items.get(i);

            com.google.android.material.card.MaterialCardView card =
                    new com.google.android.material.card.MaterialCardView(context);
            int size = dpToPx(56);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(size, size);
            lp.setMarginEnd(dpToPx(8));
            card.setLayoutParams(lp);
            card.setRadius(dpToPx(10));
            card.setCardElevation(0);
            card.setStrokeWidth(dpToPx(1));
            card.setStrokeColor(ContextCompat.getColor(context,
                    com.google.android.material.R.color.material_on_surface_stroke));

            ImageView iv = new ImageView(context);
            iv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(context)
                    .load(item.getImage())
                    .placeholder(R.drawable.ic_cart)
                    .into(iv);
            card.addView(iv);

            h.llItemThumbnails.addView(card);
        }

        int overflow = items.size() - MAX_THUMBNAILS;
        if (overflow > 0) {
            h.tvMoreItems.setVisibility(View.VISIBLE);
            h.tvMoreItems.setText("+" + overflow + " more item" + (overflow > 1 ? "s" : ""));
        } else {
            h.tvMoreItems.setVisibility(View.GONE);
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void updateOrders(List<OrderModel> newList) {
        orderList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus;
        TextView tvOrderTotal, tvItemCount, tvPromoApplied, tvMoreItems;
        FrameLayout dotPlaced, dotProcessing, dotShipped, dotDelivered;
        View line1, line2, line3;
        TextView tvPlaced, tvProcessing, tvShipped, tvDelivered;
        LinearLayout llItemThumbnails;
        MaterialButton btnOrderDetails;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvOrderId       = v.findViewById(R.id.tvOrderId);
            tvOrderDate     = v.findViewById(R.id.tvOrderDate);
            tvOrderStatus   = v.findViewById(R.id.tvOrderStatus);
            tvOrderTotal    = v.findViewById(R.id.tvOrderTotal);
            tvItemCount     = v.findViewById(R.id.tvItemCount);
            tvPromoApplied  = v.findViewById(R.id.tvPromoApplied);
            tvMoreItems     = v.findViewById(R.id.tvMoreItems);
            dotPlaced       = v.findViewById(R.id.dotPlaced);
            dotProcessing   = v.findViewById(R.id.dotProcessing);
            dotShipped      = v.findViewById(R.id.dotShipped);
            dotDelivered    = v.findViewById(R.id.dotDelivered);
            line1           = v.findViewById(R.id.line1);
            line2           = v.findViewById(R.id.line2);
            line3           = v.findViewById(R.id.line3);
            llItemThumbnails= v.findViewById(R.id.llItemThumbnails);
            btnOrderDetails = v.findViewById(R.id.btnOrderDetails);

            try {
                LinearLayout timeline = (LinearLayout)
                        ((LinearLayout) v).getChildAt(3);
                tvPlaced      = (TextView) ((LinearLayout) timeline.getChildAt(0)).getChildAt(1);
                tvProcessing  = (TextView) ((LinearLayout) timeline.getChildAt(2)).getChildAt(1);
                tvShipped     = (TextView) ((LinearLayout) timeline.getChildAt(4)).getChildAt(1);
                tvDelivered   = (TextView) ((LinearLayout) timeline.getChildAt(6)).getChildAt(1);
            } catch (Exception ignored) {}
        }
    }
}
