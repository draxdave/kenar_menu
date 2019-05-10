package ir.drax.kenar_menu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ir.drax.kenar_menu.interfaces.RecyclerInteraction;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    private ArrayList<Object> items = new ArrayList<>();
    private ArrayList<Object> hiddenItems=new ArrayList<>();
    private RecyclerStates recyclerStates;
    private boolean interactionsEnabled = true;
    private int listItemLayout;

    class MyViewHolder extends RecyclerView.ViewHolder {

        SwipeRevealLayout revealLayout;
        ViewGroup root;
        MyViewHolder(View v) {
            super(v);
            root = v.findViewById(R.id.mainContainer);
            revealLayout = v.findViewById(R.id.root);
            revealLayout.findViewById(R.id.mainContainer)
                    .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!interactionsEnabled){
                        return;
                    }

                    recyclerStates.onItemLayoutLoaded(items.get(getAdapterPosition()) , getAdapterPosition());
                }
            });
            revealLayout.setInteraction(new RecyclerInteraction() {
                @Override
                public void onSwipe(int id) {
                    if (!interactionsEnabled){
                        revealLayout.close(true);
                        return;
                    }

                    hiddenItems.add(items.get(getAdapterPosition()));
                    items.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    checkState();
                }
            });
        }
    }

    ListAdapter(ArrayList<Object> reserveItems, int listItem, RecyclerStates recyclerStates) {
        items = reserveItems;
        this.recyclerStates = recyclerStates;
        this.listItemLayout =listItem;

        checkState();
        Log.e("ListAdapter",items.size()+"");
    }

    private void checkState() {

        if (recyclerStates!=null)
            if (getItemCount()==0) recyclerStates.onListEmpty();
            else recyclerStates.onListNotEmpty();
    }


    @NonNull
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sider_reserve_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Object item = items.get(position);
        holder.revealLayout.close(false);
        holder.revealLayout.setTag(item.hashCode());
        holder.root.removeAllViews();
        holder.root.addView(
                recyclerStates.onItemLayoutLoaded(item,position, listItemLayout)
        );
    }

    void disableInteractions(){
        interactionsEnabled = false;
    }

    void enableInteractions(){
        interactionsEnabled = true;
    }

    @Override
    public int getItemCount() {
        if (items.size()==0 && recyclerStates!=null) recyclerStates.onListEmpty();
        return items.size();
    }

    ArrayList<Object> getHiddenItems() {
        return hiddenItems;
    }

    void updateList() {
        notifyDataSetChanged();
        checkState();
    }

    public interface RecyclerStates {
        void onListEmpty();
        void onListNotEmpty();
        View onItemLayoutLoaded(Object item, int position, int listItemLayou);
        void onItemLayoutLoaded(Object reserveItem, int position);
    }

    public int getListItemLayoutId() {
        return listItemLayout;
    }

}