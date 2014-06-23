package de.thtp.dapp;

import android.content.SharedPreferences;

public class DAppPrefs {

	public static int MIN_PLAYERS = 4;
	public static boolean HIDE_INACTIVE_PLAYERS = true;
	public static int MAX_PLAYERS = 7;
	public static int MAX_BOECKE = 0;
	public static boolean COUNT_MINUS = true;
	public static boolean CUT_BOECKE = true;
	public static int DEALER_POS_DIFF = 0;

	public static void updateDroikoSettings(SharedPreferences prefs) {
		
		DAppPrefs.MAX_BOECKE = Integer.parseInt(prefs.getString(
				"maxboecke_preference", "" + DAppPrefs.MAX_BOECKE));
		DAppPrefs.MAX_PLAYERS = Integer.parseInt(prefs.getString(
				"playersize_list_preference", "" + DAppPrefs.MAX_PLAYERS));
		DAppPrefs.MIN_PLAYERS = Integer.parseInt(prefs.getString(
				"min_playersize_list_preference", "" + DAppPrefs.MIN_PLAYERS));
		DAppPrefs.COUNT_MINUS = prefs.getBoolean("count_minus_preference",
				DAppPrefs.COUNT_MINUS);
		DAppPrefs.CUT_BOECKE = prefs.getBoolean("cut_boecke_preference",
				DAppPrefs.CUT_BOECKE);
		DAppPrefs.HIDE_INACTIVE_PLAYERS = prefs.getBoolean(
				"hide_inactive_players_preference",
				DAppPrefs.HIDE_INACTIVE_PLAYERS);
		int dealer_pos_diff = 0;
		try{
			dealer_pos_diff = Integer.parseInt(prefs.getString(
					"dealer_pos_diff_preference", "" + DAppPrefs.DEALER_POS_DIFF));
		} catch (NumberFormatException e){
			
		}
		DAppPrefs.DEALER_POS_DIFF = dealer_pos_diff;
	}
}
