package com.mnemonicizer.mnemonicizer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mnemonicizer.mnemonicizer.Model.Word;
import com.mnemonicizer.mnemonicizer.R;
import com.mnemonicizer.mnemonicizer.UI.WordViewActivity;
import com.mnemonicizer.mnemonicizer.utils.succesCallback;

import java.util.ArrayList;
import java.util.List;




public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>  implements Filterable,succesCallback {

    private LayoutInflater inflater;
    private Context ctx;
    private List<Word> wordsFiltered,words;
    private AdapterCallback mAdapterCallback;
    //private List<Word> words = new ArrayList<>();

    public CustomAdapter(Context ctx,List<Word> words,AdapterCallback callback) {
        this.mAdapterCallback = callback;
        this.words = words;
        this.wordsFiltered = words;
        inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.word_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final CustomAdapter.MyViewHolder holder, final int position) {

        final Word wrd = wordsFiltered.get(position);
            holder.word.setText(wrd.getName());
            holder.mng.setText(wrd.getMeaning());
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
// generate random color
            int color1 = generator.getRandomColor();
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(wordsFiltered.get(position).getName().charAt(0) + "", color1);
            holder.imageView.setImageDrawable(drawable);
            if(wordsFiltered.get(position).getCmplt_in() == 1){

                holder.tick.setImageDrawable(ctx.getResources().getDrawable(R.drawable.tick));
            }else{

                holder.tick.setImageDrawable(ctx.getResources().getDrawable(R.drawable.tick_red));
            }


        //holder.tvnumber.setText(String.valueOf(MainActivity.words.get(position).getNumber()));

    }

    @Override
    public int getItemCount() {
        return wordsFiltered.size();
    }

    @Override
    public void recSucces(View v) {



       // notifyDataSetChanged();
    }

    public void setItems(List<Word> v) {
        this.wordsFiltered = v;
        notifyDataSetChanged();
    }

    public void setFilter(List<Word> s) {
       // Toast.makeText(ctx, ""+s.size(), Toast.LENGTH_SHORT).show();
        wordsFiltered = new ArrayList<>();
        wordsFiltered.addAll(s);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected ImageButton play,rec, tick;
        private TextView word, mng;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            play = itemView.findViewById(R.id.play);
            rec = itemView.findViewById(R.id.rec);
            tick = itemView.findViewById(R.id.tick);
            word = (TextView) itemView.findViewById(R.id.word);
            mng = (TextView) itemView.findViewById(R.id.mng);
            imageView = itemView.findViewById(R.id.image_view_word);
             play.setOnClickListener(this);
             rec.setOnClickListener(this);
             word.setOnClickListener(this);


        }

        // onClick Listener for view
        @Override
        public void onClick(View v) {

            if (v.getId() == play.getId()){
                mAdapterCallback.onPlayCallback(wordsFiltered.get(getAdapterPosition()).getName());
                Toast.makeText(ctx, "PLAY "+wordsFiltered.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();

            } else if(v.getId() == rec.getId()) {
                mAdapterCallback.onRecCallback(getAdapterPosition(),wordsFiltered,wordsFiltered.get(getAdapterPosition()).getId(),wordsFiltered.get(getAdapterPosition()).getName());
                Toast.makeText(ctx, "REC "+wordsFiltered.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            }else if(v.getId() == word.getId()){
                Intent intent = new Intent(ctx,WordViewActivity.class);
                intent.putExtra("word",wordsFiltered.get(getAdapterPosition()));
                ctx.startActivity(intent);
            }
        }

    }
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    wordsFiltered = words;
                }
                else {
                    List<Word> filteredList = new ArrayList<>();
                    for (Word row : words) {

                        if ( row.getName().startsWith(charString) || row.getName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }

                    }

                    wordsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = wordsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                wordsFiltered = (ArrayList<Word>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
    public void myFilter(char c)
    {
        List<Word> filteredList = new ArrayList<>();
        for (Word row : words) {

            if ( row.getName().startsWith(c+"")) {
                filteredList.add(row);
            }
        }

        wordsFiltered = filteredList;
    }

    public interface AdapterCallback {
        void onPlayCallback( String text);
        void onRecCallback(int adapterPosition, List<Word> wordsFiltered, int id, String text);
    }


}


