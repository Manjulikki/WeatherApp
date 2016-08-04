package manjunn.weather;

/**
 * Created by manjunn on 10/17/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomGrid extends BaseAdapter {
    private Context mContext;
    private final String[] text1, text2, text3, text4;
    int textId1, textId2, textId3, textId4;
    int gridName;

    public CustomGrid(Context c, String[] text1, String[] text2, String[] text3, String[] text4, int textid1, int textid2, int textid3, int textid4, int grid) {
        mContext = c;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;
        textId1 = textid1;
        textId2 = textid2;
        textId3 = textid3;
        textId4 = textid4;
        gridName = grid;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return text1.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(gridName, null);
        } else {
            grid = (View) convertView;
        }
        TextView imageButton = (TextView) grid.findViewById(R.id.deleteCity);
        imageButton.setTag(position);
        imageButton.setOnClickListener(clickListener);

        TextView textView1, textView2, textView3, textView4;
        textView1 = (TextView) grid.findViewById(textId1);
        textView2 = (TextView) grid.findViewById(textId2);
        textView3 = (TextView) grid.findViewById(textId3);
        textView4 = (TextView) grid.findViewById(textId4);
        textView1.setText(text1[position]);
        textView2.setText(text2[position]);
        if (text3 != null && !text3.equals("")) {
            imageButton.setVisibility(View.INVISIBLE);
            textView3.setText(text3[position]);
        }
        if (text4 != null && !text4.equals(""))
            textView4.setText(text4[position]);
        return grid;
    }

    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String selCity = CityList.citiesSel[(int) view.getTag()];
            SqliteDatabaseOperations sqliteDatabaseOperations = new SqliteDatabaseOperations(mContext);
            if (MainActivity.curCity.equals(selCity)) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.delete_city), Toast.LENGTH_SHORT).show();
            } else {
                sqliteDatabaseOperations.deleteCity(sqliteDatabaseOperations, selCity);  // delete selected city to delete
                if (MainActivity.city.equals(selCity)) {
                    MainActivity.city = MainActivity.curCity;    // changing current city has selected city
                    MainActivity.selectedCities = sqliteDatabaseOperations.getSavedCitiesDetails(sqliteDatabaseOperations);
                    CityList.flag = true;  // to be in the cities list page
                    Intent intent = new Intent();
                    intent.setClass(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                }
            }
        }
    };
}

