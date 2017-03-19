/*
 * Copyright (C) 2016  Tobias Bielefeld
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you want to contact me, send me an e-mail at tobias.bielefeld@gmail.com
 */

package de.tobiasbielefeld.solitaire.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomAppCompatActivity;
import de.tobiasbielefeld.solitaire.ui.manual.Manual;
import de.tobiasbielefeld.solitaire.ui.settings.Settings;

import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_CURRENT_GAME;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_ICON_THEME;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_MENU_COLUMNS_LANDSCAPE;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_MENU_COLUMNS_PORTRAIT;
import static de.tobiasbielefeld.solitaire.SharedData.GAME;
import static de.tobiasbielefeld.solitaire.SharedData.MENU_COLUMNS_LANDSCAPE;
import static de.tobiasbielefeld.solitaire.SharedData.MENU_COLUMNS_PORTRAIT;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_CURRENT_GAME;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_MENU_GAMES;
import static de.tobiasbielefeld.solitaire.SharedData.bitmaps;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedBoolean;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedInt;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedIntList;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedString;
import static de.tobiasbielefeld.solitaire.SharedData.lg;
import static de.tobiasbielefeld.solitaire.SharedData.loadStrings;
import static de.tobiasbielefeld.solitaire.SharedData.putSharedInt;
import static de.tobiasbielefeld.solitaire.SharedData.savedSharedData;

 /*
  * This is the main menu with the buttons to load a game
  */

public class GameSelector extends CustomAppCompatActivity {

    ArrayList<LinearLayout> gameLayouts;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
         * initialize stuff and if the corresponding setting is set to true, load the last played game
         */
        super.onCreate(savedInstanceState);
        savedSharedData = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_game_selector_main);

        tableLayout = (TableLayout) findViewById(R.id.tableLayoutGameChooser);
        gameLayouts = lg.loadLayouts(this);

        bitmaps.setResources(getResources());
        loadStrings(this);
        loadGameList();

        if (!getSharedBoolean(getString(R.string.pref_key_start_menu), false)) {
            int savedGame;

            try {
                savedGame = getSharedInt(PREF_KEY_CURRENT_GAME, DEFAULT_CURRENT_GAME);
            } catch (Exception e) {
                savedSharedData.edit().remove(PREF_KEY_CURRENT_GAME).apply();
                savedGame = 0;
            }

            if (savedGame != 0) {
                Intent intent = new Intent(getApplicationContext(), GameManager.class);
                intent.putExtra(GAME, savedGame);
                startActivityForResult(intent, 0);
            }
        } else {
            putSharedInt(PREF_KEY_CURRENT_GAME, DEFAULT_CURRENT_GAME);
        }
    }

    private void loadGameList() {
        ArrayList<Integer> result;

        result = getSharedIntList(PREF_KEY_MENU_GAMES);

        TableRow row = new TableRow(this);
        int counter = 0;
        int columns;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columns = Integer.parseInt(getSharedString(MENU_COLUMNS_LANDSCAPE, DEFAULT_MENU_COLUMNS_LANDSCAPE));
        else
            columns = Integer.parseInt(getSharedString(MENU_COLUMNS_PORTRAIT, DEFAULT_MENU_COLUMNS_PORTRAIT));

        //clear the complete layout first
        tableLayout.removeAllViewsInLayout();

        for (LinearLayout gameLayout : gameLayouts) {
            TableRow parent = (TableRow) gameLayout.getParent();

            if (parent != null)
                parent.removeView(gameLayout);
        }

        //add games to list for older versions of the app
        if (result.size() == 12) {                                                                     //new canfield
            result.add(1, 1);
        }

        //add the game buttons
        for (int i = 0; i < gameLayouts.size(); i++) {

            if (counter % columns == 0) {
                row = new TableRow(this);
                tableLayout.addView(row);
            }

            if (result.size() == 0 || result.size() < (i + 1) || result.get(i) == 1) {
                gameLayouts.get(i).setVisibility(View.VISIBLE);
                ImageView imageView = (ImageView) gameLayouts.get(i).getChildAt(1);
                imageView.setImageBitmap(bitmaps.getMenu(i % 6, i / 6));
                row.addView(gameLayouts.get(i));
                counter++;
            } else {
                gameLayouts.get(i).setVisibility(View.GONE);
            }
        }

        //add some dummies to the last row, if necessary
        while (row.getChildCount() < columns) {
            FrameLayout dummy = new FrameLayout(this);
            dummy.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            row.addView(dummy);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if the player returns from a game to the main menu, save it.
        putSharedInt(PREF_KEY_CURRENT_GAME, DEFAULT_CURRENT_GAME);
    }

    public void onClick(View view) {
        /*
         *  load a game when clicking a button
         */
        //avoid loading two games at once when pressing two buttons at once
        if (getSharedInt(PREF_KEY_CURRENT_GAME, DEFAULT_CURRENT_GAME) != 0)
            return;

        //prepare the sharedPreferences and start the GameManager with the game name as a intent
        putSharedInt(PREF_KEY_CURRENT_GAME, view.getId());
        Intent intent = new Intent(getApplicationContext(), GameManager.class);
        intent.putExtra(GAME, view.getId());
        startActivityForResult(intent, 0);//*/
    }

    public void onClick2(View view) {
        if (view.getId() == R.id.buttonStartSettings)
            startActivity(new Intent(getApplicationContext(), Settings.class));
        else if (view.getId() == R.id.buttonStartManual)
            startActivity(new Intent(getApplicationContext(), Manual.class));
    }

    public void onResume() {
        super.onResume();
        loadGameList();
        updateIcons();
    }

    public void updateIcons() {
        ImageView manual, settings;

        manual = (ImageView) findViewById(R.id.game_selector_button_manual);
        settings = (ImageView) findViewById(R.id.game_selector_button_settings);

        switch (getSharedString(getString(R.string.pref_key_icon_theme), DEFAULT_ICON_THEME)) {
            case "Material":
                manual.setImageResource(R.drawable.icon_material_manual);
                settings.setImageResource(R.drawable.icon_material_settings);
                break;
            case "Old":
                manual.setImageResource(R.drawable.icon_old_manual);
                settings.setImageResource(R.drawable.icon_old_settings);
                break;
        }

    }
}
