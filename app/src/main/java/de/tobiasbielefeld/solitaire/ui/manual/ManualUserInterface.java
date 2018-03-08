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

package de.tobiasbielefeld.solitaire.ui.manual;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tobiasbielefeld.solitaire.R;

/**
 * Just show a textView for the user interface page
 */

public class ManualUserInterface extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual_user_interface, container, false);

        TextView textView1 = (TextView) view.findViewById(R.id.text_view_manual_ui_1);
        TextView textView2 = (TextView) view.findViewById(R.id.text_view_manual_ui_2);

        CharSequence strings1[] = new CharSequence[]{
                getText(R.string.manual_ui_text_part_2), getText(R.string.manual_ui_text_part_3),
                getText(R.string.manual_ui_text_part_4), getText(R.string.manual_ui_text_part_5)
        };

        CharSequence strings2[] = new CharSequence[]{
                getText(R.string.manual_ui_text_part_7), getText(R.string.manual_ui_text_part_8),
                getText(R.string.manual_ui_text_part_9)
        };

        SpannableString spanns1[] = new SpannableString[strings1.length];
        SpannableString spanns2[] = new SpannableString[strings2.length];

        for (int i=0;i<strings1.length;i++){
            spanns1[i] = new SpannableString(strings1[i]);
            spanns1[i].setSpan(new BulletSpan(15), 0, strings1[i].length(), 0);
        }

        for (int i=0;i<strings2.length;i++){
            spanns2[i] = new SpannableString(strings2[i]);
            spanns2[i].setSpan(new BulletSpan(15), 0, strings2[i].length(), 0);
        }

        textView1.setText(TextUtils.concat(spanns1));
        textView2.setText(TextUtils.concat(spanns2));

        return view;
    }
}
