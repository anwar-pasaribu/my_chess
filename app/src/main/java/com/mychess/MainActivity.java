package com.mychess;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.mychess.ServerThread.OnReadListener;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {

	private static final String TAG = "chess_home";
	ServerThread st = null;
	private ImageView gambar;

    // Chess Needs
    private ArrayList<ImageButton> listImageButton = new ArrayList<ImageButton>();
    char[] char_pos = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generateBoard();

    }

    private void generateBoard() {
        TableLayout tableLayout = (TableLayout) this.findViewById(R.id.table_main);
        for (int row = 0; row < 8; row++) {

            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams params =
                    new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            tableRow.setLayoutParams(params);

            for (int col = 0; col < 8; col++) {

                View chess_cell = getLayoutInflater().inflate(R.layout.chess_cell, tableRow, false);
                FrameLayout fl_cell = (FrameLayout) chess_cell.findViewById(R.id.fl_cell);
                ImageButton ib_cell = (ImageButton) chess_cell.findViewById(R.id.ib_cellButton);

                if (row % 2 != 0 && col % 2 == 0) {
                    fl_cell.setBackgroundColor(getResources().getColor(R.color.colorDarkCell));
                } else if (row % 2 == 0 && col % 2 != 0) {
                    fl_cell.setBackgroundColor(getResources().getColor(R.color.colorDarkCell));
                } else {
                    fl_cell.setBackgroundColor(getResources().getColor(R.color.colorLightCell));
                }
                chess_cell.setLayoutParams(params);
                tableRow.addView(chess_cell);

                // List for chess position
                String str_pos = String.valueOf(char_pos[col]) + "" + (8-row);
                ib_cell.setTag(str_pos);
                listImageButton.add(ib_cell);
            }

            tableLayout.addView(tableRow,
                    new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        }
    }

    private void setPiecePos(String pawnPos) {

        String[] posisi = pawnPos.split("\\s+");

        for (int i = 0; i < posisi.length; i++) {

            char bidak = posisi[i].charAt(0);
            char vertikal = posisi[i].charAt(2);
            String cell = posisi[i].substring(1);

            // Devide image button list to only 8 items
            ArrayList<ImageButton> listImageBtnTemp = new ArrayList<ImageButton>();
            int int_vertikal = Character.getNumericValue(vertikal);
            int listStartAt = ((8 - int_vertikal) * 8);
            int listEndAt = ((8 - (int_vertikal-1) ) * 8);
            listImageBtnTemp.addAll(listImageButton.subList(listStartAt, listEndAt));

            for (int indexTemp = 0; indexTemp < listImageBtnTemp.size(); indexTemp++) {
                String targetCellTag = listImageBtnTemp.get(indexTemp).getTag().toString();
                if(cell.equals(targetCellTag)) {
                    String namaFile = getNamaFile(bidak);
                    int file = getResources().getIdentifier(namaFile, "drawable", getPackageName());
                    listImageBtnTemp.get(indexTemp).setImageResource(file);
                    break;
                }

            }

            if(!listImageBtnTemp.isEmpty())
                listImageBtnTemp.clear();
        }
    }


	@Override
    public void onResume() {
        startMonitoring();

        super.onResume();
    }

    @Override
    public void onPause() {
        stopMonitoring();
        super.onPause();
    }

    private void startMonitoring() {
        st = new ServerThread("xinuc.org", 7387);
        st.setListener(new OnReadListener() {
			@Override
			public void onRead(ServerThread serverThread, final String response) {
				runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
		            	// mengatur ulang gambar papan catur
		            	aturUlangGambar();

						Log.i(TAG, "run: Response: " + response);

                        setPiecePos(response);

		            }
		        });
			}
		});
        st.start();
    }
    
    private void aturUlangGambar(){
    	for (int indexTemp = 0; indexTemp < listImageButton.size(); indexTemp++) {
            int file = getResources().getIdentifier("trans", "drawable", getPackageName());
            listImageButton.get(indexTemp).setImageResource(file);
        }
    }
    
    private String getNamaFile(char bidak){
		String hasil = null;
		
		//jenis bidak
		switch(bidak){
			case 'K':
				hasil = "w_king";
				break;
			case 'k':
				hasil = "b_king";
				break;
			case 'Q':
				hasil = "w_queen";
				break;
			case 'q':
				hasil = "b_queen";
				break;
			case 'N':
				hasil = "w_knight";
				break;
			case 'n':
				hasil = "b_knight";
				break;
			case 'B':
				hasil = "w_bishop";
				break;
			case 'b':
				hasil = "b_bishop";
				break;
			case 'R':
				hasil = "w_rook";
				break;
			case 'r':
				hasil = "b_rook";
				break;
			default:
				break;
		}
		return hasil;
    }
    
    private void stopMonitoring() {
        if (st != null) {
            try {
				st.socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
