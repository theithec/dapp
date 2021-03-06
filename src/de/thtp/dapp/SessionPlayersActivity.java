package de.thtp.dapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private ArrayAdapter playernamesAdapter;
    private Spinner playernamesSpinner;
    private ArrayList<String> availPlayerNames;
    private PlayerList selectedPlayers;
    private ArrayList<PlayerRow> playerRows;
    private TableLayout tableLayout;
    private ArrayList<String> positions;
    private Button btnSessionPlayersDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessionplayers);
        tableLayout = (TableLayout) findViewById(R.id.players_table_layout);
        btnSessionPlayersDone = (Button) findViewById(R.id.btnSessionPlayersDone);
        playerRows = new ArrayList<>();
        selectedPlayers = Session.getSessionPlayers();
        String sessionName = this.getIntent().getExtras()
                .getString(Const.K_SESSION_NAME);
        if (!Session.isReady()) {
            Session.setIDB(new DB(this));
            Session.start(sessionName, selectedPlayers);
        }
        positions = new ArrayList<>();
        fillPositionsArray();
        availPlayerNames = Session.getKnownPlayers().getNames();
        playernamesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, availPlayerNames);
        playernamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        updateAvailPlayerNames();
        playernamesSpinner = (Spinner) findViewById(R.id.player_names_spinner);
        playernamesSpinner.setAdapter(playernamesAdapter);
        setOnNamesItemSelectedListener(this);
        for (Player p: selectedPlayers){
            PlayerRow pr = new PlayerRow(this, p);
            playerRows.add(pr);

        }
    }

    public void startSessionWithPlayers(View v) {
        for (PlayerRow playerRow: playerRows) {
            int diff = Integer.parseInt("" +playerRow.diffView.getText());
            playerRow.player.diff = diff;
        }
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
        positions.add("-");
    }



    private void addSessionPlayer(String name) {
        if( selectedPlayers.getByName(name) == null) {
            Player p = new Player(name);
            p.isActive = true;
            selectedPlayers.add(p);
            Session.updatePlayers(selectedPlayers);
            fillPositionsArray();
            playerRows.add(new PlayerRow(this, p));
            updateAvailPlayerNames();
            playernamesSpinner.setSelection(0);
        }
    }



    private void updateAvailPlayerNames() {
        PlayerList availPlayers = Session.getKnownPlayers();
        for (Player sessionPlayer : selectedPlayers) {
            Player found = availPlayers.getByName(sessionPlayer.name);
            if (null != found) {
                availPlayers.remove(found);
            }
        }

        availPlayerNames.clear();
        availPlayerNames.addAll(availPlayers.getNames());
        availPlayerNames.add(0, "+");
        availPlayerNames.add(0, "choose");
        if (null != playernamesAdapter) {

            playernamesAdapter.notifyDataSetChanged();
        }

        int activePlayersSize = Session.getActivePlayers().size();
        btnSessionPlayersDone.setEnabled(activePlayersSize >= DAppPrefs.MIN_PLAYERS &&
                activePlayersSize <= DAppPrefs.MAX_PLAYERS);
    }

    private void setOnNamesItemSelectedListener(final SessionPlayersActivity _activity) {

        playernamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstTime = true;
            final SessionPlayersActivity activity = _activity;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView tv = (TextView) view;
                String txt = tv.getText().toString();
                if (!firstTime) {
                    switch (txt) {
                        case "choose":
                            break;
                        case "+":
                            new PlayerNameAddDialog(activity).show();
                            break;
                        default:
                            addSessionPlayer(txt);
                    }
                }
                firstTime = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setOnNumItemsSelectedListener(final PlayerRow _playerRow) {

        _playerRow.spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    //Spinner spinner = fs;
                    boolean firstTime = true;
                    final PlayerRow playerRow = _playerRow;

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!firstTime) {
                            //TextView tv = (TextView) view;
                            String txt = playerRow.spinner.getSelectedItem().toString();
                            if (txt.equals("-")) {
                                playerRow.player.isActive = false;
                                selectedPlayers.getByName(playerRow.player.name).isActive = false;

                                //selectedPlayers.
                            } else {
                                playerRow.player.isActive = true;
                                int pos1 = Integer.parseInt(txt);
                                selectedPlayers.remove(playerRow.player);
                                playerRows.remove(playerRow);
                                playerRows.add(pos1 - 1, playerRow);
                                selectedPlayers.add(pos1 - 1, playerRow.player);
                                for (PlayerRow pr : playerRows) {
                                    tableLayout.removeView(pr.row);
                                }
                                fillPositionsArray();
                                int i = 0;
                                for (PlayerRow pr : playerRows) {
                                    //pr.add();
                                    tableLayout.addView(pr.row);
                                    //pr
                                    pr.spinner.setSelection(i++);
                                }
                            }
                            Session.updatePlayers(selectedPlayers);
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
        final TableRow row;
        final Spinner spinner;
        final TextView nameView;
        final ArrayAdapter spinnerAdapter;
        final EditText diffView;
        final Player player;
        public PlayerRow(Context context, Player player){
            row = new TableRow(context);
            spinner = new Spinner(context);
            nameView = new TextView(context);
            diffView = new EditText(context);
            diffView.setText("" + player.diff);
            diffView.setMinEms(3);
            this.player = player;
            nameView.setText(player.name);
            spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, positions);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(spinnerAdapter);
            row.addView(spinner);
            row.addView(nameView);
            row.addView(diffView);
            tableLayout.addView(row);

            int selection = Session.getActivePlayers().indexOf(selectedPlayers.getByName(player.name));// positions.size() - 2;
            if (!player.isActive) {
                selection = positions.size()-1;
            }
            spinner.setSelection(selection);
            setOnNumItemsSelectedListener(this);

        }
    }

    class PlayerNameAddDialog extends PlayerNameDialog {

        public PlayerNameAddDialog(final SessionPlayersActivity dappActivity) {
            super(dappActivity, "Add Player", "");
            setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    String name = input.getText().toString();
                    addSessionPlayer(name);
                }
            });
        }
    }


}