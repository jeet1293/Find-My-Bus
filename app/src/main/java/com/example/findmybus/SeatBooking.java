package com.example.findmybus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeet on 29-Apr-17.
 */

public class SeatBooking extends AppCompatActivity implements OnSeatSelected {
    private static final int COLUMNS = 5;
    private TextView txtSeatSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_booking);

        txtSeatSelected = (TextView)findViewById(R.id.txt_seat_selected);

        List<AbstractItem> items = new ArrayList<>();
        for (int i=0; i<30; i++) {

            if (i%COLUMNS==0 || i%COLUMNS==4) {
                items.add(new EdgeItem(String.valueOf(i)));
            } else if (i%COLUMNS==1 || i%COLUMNS==3) {
                items.add(new CenterItem(String.valueOf(i)));
            } else {
                items.add(new EmptyItem(String.valueOf(i)));
            }
        }

        GridLayoutManager manager = new GridLayoutManager(this, COLUMNS);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lst_items);
        recyclerView.setLayoutManager(manager);

        AirplaneAdapter adapter = new AirplaneAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSeatSelected(int count) {

        txtSeatSelected.setText("Book "+count+" seats");
    }
}
