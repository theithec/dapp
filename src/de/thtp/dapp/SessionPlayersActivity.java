package de.thtp.dapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

/**
 * Created by lotek on 14.06.15.
 */
public class SessionPlayersActivity extends DappActivity {
    ArrayAdapter playernamesAdapter;
    Spinner playernamesSpinner;
    PlayerList selectedPlayers;
    ArrayList<PlayerRow> playerRows;
    TableLayout tableLayout;
    ArrayList<String> positions;
    Button btnSessionPlayersDone;
    String sessionName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessionplayers);
        tableLayout = (TableLayout) findViewById(R.id.playerstablelayout);
        btnSessionPlayersDone = (Button) findViewById(R.id.btnSessionPlayersDone);
        playerRows = new ArrayList<>();
        selectedPlayers = Session.getSessionPlayers();
        sessionName = this.getIntent().getExtras()
                .getString(Const.K_SESSION_NAME);
        if (!Session.isReady()) {
            Session.setIDB(new DB(this));
            Session.start(sessionName, selectedPlayers);
        }
        positions = new ArrayList<>();
        fillPositionsArray();
        playernamesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,  Session.getKnownPlayers().getNames());
        updateAvailPlayerNames();
        playernamesSpinner = (Spinner) findViewById(R.id.playernamesSpinner);
        playernamesSpinner.setAdapter(playernamesAdapter);
        setOnNamesItemSelectedListener();
        for (Player p: selectedPlayers){
            PlayerRow pr = new PlayerRow(this, p);
            pr.spinner.setSelection(selectedPlayers.indexOf(selectedPlayers.getByName(p.name)));
            playerRows.add(pr);
        }
    }

    public void startSessionWithPlayers(View v) {
        Session.updatePlayers(selectedPlayers);
        Intent i = new Intent(this, SessionResultActivity.class);
        startActivity(i);
        finish();
    }


    private void fillPositionsArray(){
        positions.clear();
        for (int i=0; i<selectedPlayers.size(); i++){
            positions.add("" +(i+1));
        }
    }



    private void addSessionPlayer(String name) {
        Player p = new Player(name);
        p.isActive = true;
        selectedPlayers.add(p);
        fillPositionsArray();
        playerRows.add(new PlayerRow(this, p));
        updateAvailPlayerNames();
    }

    public void setOnNamesItemSelectedListener() {
        playernamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                String txt = tv.getText().toString();
                switch (txt) {
                    case "-":
                        playernamesAdapter.remove(txt);
                        playernamesAdapter.notifyDataSetChanged();
                        break;
                    case "+":
                        //              new PlayerNameAddDialog(_this).show();
                        break;
                    default:
                        addSessionPlayer(txt);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ArrayList<String> updateAvailPlayerNames() {
        PlayerList availPlayers = (PlayerList) Session.getKnownPlayers();
        for (Player sessionPlayer : selectedPlayers) {
            Player found = availPlayers.getByName(sessionPlayer.name);
            if (null != found) {
                playernamesAdapter.remove(found.name);
            }
        }
        ArrayList<String> availPlayerNames = availPlayers.getNames();
        availPlayerNames.add(0, "+");
        availPlayerNames.add(0, "-"); // dummy to be rm'd -> "layout event"
        if (null != playernamesAdapter) {

            playernamesAdapter.notifyDataSetChanged();
        }

        btnSessionPlayersDone.setEnabled(selectedPlayers.size() >=  DAppPrefs.MIN_PLAYERS &&
        selectedPlayers.size() <= DAppPrefs.MAX_PLAYERS);
        return availPlayerNames;
    }

    public void setOnNumItemsSelectedListener(final PlayerRow _playerRow) {

        _playerRow.spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    //Spinner spinner = fs;
                    boolean firstTime = true;
                    PlayerRow playerRow = _playerRow;

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TextView tv = (TextView) view;
                        if (!firstTime) {
                            int pos1 = Integer.parseInt(playerRow.spinner.getSelectedItem().toString());
                            //playerRow.remove();
                            playerRows.remove(playerRow);
                            playerRows.add(pos1 - 1, playerRow);
                            for(PlayerRow pr: playerRows){
                                tableLayout.removeView(pr.row);
                            }
                            fillPositionsArray();
                            int i = 0;
                            for(PlayerRow pr: playerRows){
                                //pr.add();
                                tableLayout.addView(pr.row);
                                pr.spinner.setSelection(i++);
                            }
                        }
                        firstTime = false;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }

        );

    }

    class PlayerRow {
        TableRow row;
        Spinner spinner;
        TextView nameView;
        ArrayAdapter spinnerAdapter;
        public PlayerRow(Context context, Player player){
            row = new TableRow(context);
            spinner = new Spinner(context);
            nameView = new TextView(context);
            nameView.setText(player.name);
            spinnerAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, positions);
            spinner.setAdapter(spinnerAdapter);
            row.addView(spinner);
            row.addView(nameView);
            tableLayout.addView(row);
            spinner.setSelection(positions.size() - 1);
            setOnNumItemsSelectedListener(this);

        }
    }


}