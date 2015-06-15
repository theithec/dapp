package de.thtp.dapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
public class SessionPlayersActivityB extends DappActivity {
    Spinner playernamesSpinner;
    ArrayAdapter<String> playernamesAdapter;
    PlayerList selectedPlayers;
    TableLayout tableLayout;
    String sessionName;
    ArrayAdapter playerorderAdapter;
    ArrayList<String> nums;
    ArrayList<PlayerRow> playerRows;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessionplayers);
        selectedPlayers = Session.getSessionPlayers();
        playernamesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, updateAvailPlayerNames());
        playernamesSpinner = (Spinner) findViewById(R.id.playernamesSpinner);
        playernamesSpinner.setAdapter(playernamesAdapter);
        setOnNamesItemSelectedListener();
        tableLayout = (TableLayout) findViewById(R.id.playerstablelayout);
        sessionName = this.getIntent().getExtras()
                .getString(Const.K_SESSION_NAME);

        if (!Session.isReady()) {
            Session.setIDB(new DB(this));
            Session.start(sessionName, selectedPlayers);
        }
        nums = new ArrayList();
        playerorderAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nums);
        playerRows = new ArrayList<>();
    }



    class PlayerRow {
        Player player;
        TableRow tableRow;
        Spinner spinner;
        TextView nameTextView;

        public PlayerRow(Context context, Player p) {
            player = p;
            tableRow = new TableRow(context);
            nameTextView = new TextView(context);
            spinner = new Spinner(context);
            tableRow.addView(spinner);
            nameTextView.setText(player.name);
            tableRow.addView(nameTextView);
            spinner.setAdapter(playerorderAdapter);
            playerorderAdapter.clear();
            for (int i = 0; i < selectedPlayers.size(); i++) {
                nums.add("" + (i + 1));
                //System.out.println("uu " +i);
            }
            //playerorderAdapter.addAll(nums);

            //int selIndex = selectedPlayers.indexOf(player);
            setOnNumItemsSelectedListener(this);
        }

        public void add(){
            tableLayout.addView(tableRow);
            int i =playerRows.indexOf(this);
            spinner.setSelection(0); //playerRows.indexOf(this) );


        }

        public void remove() {
            //table
            tableLayout.removeView(tableRow);
        }

    }

    private void addSessionPlayer(String name) {
        Player p = new Player(name);
        selectedPlayers.add(p);
        PlayerRow r = new PlayerRow(this, p);
        playerRows.add(r);
        r.add();


        updateAvailPlayerNames();
    }

    public void setOnNumItemsSelectedListener(final PlayerRow playerRow) {

        playerRow.spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    //Spinner spinner = fs;
                    boolean firstTime = true;

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TextView tv = (TextView) view;
                        if (!firstTime) {
                            int pos1 = Integer.parseInt(playerRow.spinner.getSelectedItem().toString());
                            playerRow.remove();
                            playerRows.remove(playerRow);
                            playerRows.add(pos1 - 1, playerRow);
                            for(PlayerRow pr: playerRows){
                                pr.remove();
                            }
                            playerorderAdapter.clear();
                            for (int i = 0; i < selectedPlayers.size(); i++) {
                                nums.add("" + (i + 1));
                                //System.out.println("uu " +i);
                            }
                            playerorderAdapter.addAll(nums);
                            for(PlayerRow pr: playerRows){
                                pr.add();
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

    public void setOnNamesItemSelectedListener() {
        //final SessionPlayersActivity _this = this;
        playernamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                String txt = tv.getText().toString();
                switch (txt) {
                    case "-":
                        playernamesAdapter.remove(txt);
                        //availPlaye.remove(0);
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
        PlayerList availPlayers = (PlayerList) Session.getKnownPlayers().clone();
        //availPlayers.removeAll(selectedPlayers);
        for (Player sessionPlayer : selectedPlayers) {
            Player found = availPlayers.getByName(sessionPlayer.name);
            if (null != found) {
                //availPlayers.remove(found);
                playernamesAdapter.remove(found.name);
            }
        }
        ArrayList<String> availPlayerNames = availPlayers.getNames();
        availPlayerNames.add(0, "+");
        availPlayerNames.add(0, "-"); // dummy to be rm'd -> "layout event"
        if (null != playernamesAdapter) {

            playernamesAdapter.notifyDataSetChanged();
        }
        return availPlayerNames;

    }

    class PlayerNameAddDialog extends PlayerNameDialog {

        public PlayerNameAddDialog(final SessionPlayersActivity dappActivity) {
            super(dappActivity, "Add Player", "");
            setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    String name = input.getText().toString();
                    Player p = new Player(name);
                    selectedPlayers.add(p);
                    Session.updatePlayers(selectedPlayers);
                    addSessionPlayer(name);
                    //Collections.sort(availPlayerNames);

                    //Session.a
                    //knownPlayerNames.add(name);
                    //updateSpinners();
                }
            });
        }
    }

    /*
    class PlayerNameSpinner extends Spinner {
        private int index;
        public PlayerNumSpinner(Context context, int index) {
            super(context);
            this.index = index;
            //setOnItemSelectedListener();
        }
    }
    */
}