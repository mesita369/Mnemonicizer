package com.mnemonicizer.mnemonicizer.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mnemonicizer.mnemonicizer.Model.Word;
import com.mnemonicizer.mnemonicizer.R;

import java.util.ArrayList;
import java.util.List;




public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>  implements Filterable{

    private LayoutInflater inflater;
    private Context ctx;
    private List<Word> wordsFiltered,words;
    //private List<Word> words = new ArrayList<>();

    public CustomAdapter(Context ctx,List<Word> words) {

        this.words = words;
        this.wordsFiltered = words;
        Toast.makeText(ctx, "C Called", Toast.LENGTH_SHORT).show();
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

        //holder.tvnumber.setText(String.valueOf(MainActivity.words.get(position).getNumber()));

    }

    @Override
    public int getItemCount() {
        return wordsFiltered.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected Button btn_plus, btn_minus;
        private TextView word, mng;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            word = (TextView) itemView.findViewById(R.id.word);
            mng = (TextView) itemView.findViewById(R.id.mng);
            imageView = itemView.findViewById(R.id.image_view_word);



        }

        // onClick Listener for view
        @Override
        public void onClick(View v) {

            if (v.getId() == btn_plus.getId()){

                View tempview = (View) btn_plus.getTag(R.integer.btn_plus_view);
                TextView tv = (TextView) tempview.findViewById(R.id.number);
                int number = Integer.parseInt(tv.getText().toString()) + 1;
                tv.setText(String.valueOf(number));
                words.get(getAdapterPosition()).setName(number+"");

            } else if(v.getId() == btn_minus.getId()) {

                View tempview = (View) btn_minus.getTag(R.integer.btn_minus_view);
                TextView tv = (TextView) tempview.findViewById(R.id.number);
                int number = Integer.parseInt(tv.getText().toString()) - 1;
                tv.setText(String.valueOf(number));
                words.get(getAdapterPosition()).setName(number+"");
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
                } else {
                    List<Word> filteredList = new ArrayList<>();
                    for (Word row : words) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().startsWith(charString)) {
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

}


