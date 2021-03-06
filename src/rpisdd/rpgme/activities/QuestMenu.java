package rpisdd.rpgme.activities;

import java.util.ArrayList;

import rpisdd.rpgme.R;
import rpisdd.rpgme.gamelogic.player.Player;
import rpisdd.rpgme.gamelogic.player.StatType;
import rpisdd.rpgme.gamelogic.quests.Quest;
import rpisdd.rpgme.gamelogic.quests.QuestDifficulty;
import rpisdd.rpgme.gamelogic.quests.QuestManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

//The quest menu is the "hub" for all quest-related activities.
//Players can create, update, delete or view quests here.
//CreateQuest and UpdateQuest (for later iteration) are their own menus,
//but viewing, deleting and completing can all be done in this menu.
public class QuestMenu extends ListFragment implements OnClickListener {

	public QuestMenu() {
	}

	Button createQuest;
	Button deleteQuest;
	Button completeQuest;
	
	View selectedQuest;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		selectedQuest = null;

		View v = inflater.inflate(R.layout.quest_menu, container, false);

		createQuest = (Button) v.findViewById(R.id.createQuestButton);
		createQuest.setOnClickListener(this);
		deleteQuest = (Button) v.findViewById(R.id.deleteQuestButton);
		deleteQuest.setOnClickListener(this);
		completeQuest = (Button) v.findViewById(R.id.completeQuestButton);
		completeQuest.setOnClickListener(this);

		updateButtons();
		
		return v;
	}
	
	//Will enable or disable buttons, depending on the situation.
	public void updateButtons(){
		if (Player.getPlayer(this).questManager.atMaxNumQuests()) {
			createQuest.setEnabled(false);
		} else {
			createQuest.setEnabled(true);
		}
		if (selectedQuest != null){
			deleteQuest.setEnabled(true);
			completeQuest.setEnabled(true);
		} else {
			deleteQuest.setEnabled(false);
			completeQuest.setEnabled(false);
		}
	}

	@Override
	public void onResume() {

		fillListView(Player.getPlayer(this).questManager, getView());

		super.onResume();
	}

	// Take a list of quests, and fill up the list view with those entries
	public void fillListView(QuestManager quests, View v) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, quests.getQuestNames());

		setListAdapter(adapter);
		
		updateButtons();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		if (selectedQuest != null)
			selectedQuest.setBackgroundColor(Color.TRANSPARENT);

		v.setBackgroundColor(Color.GRAY);

		v.setSelected(true);
		selectedQuest = v;
		
		updateButtons();
	}

	public String getSelectedQuestName() {
		return ((TextView) selectedQuest).getText().toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.createQuestButton: {
			Log.i("Debug:", "created");
			// Make the parent activity change the fragment
			((MainActivity) getActivity())
					.changeFragment(new CreateQuestMenu());
			break;
		}
		case R.id.deleteQuestButton: {
			Log.i("Debug:", "destroy");
			
			/*
			confirmDelete = new PopupWindow(
					LayoutInflater.from(getActivity()).inflate(R.layout.confirmation, null, false),
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			confirmDelete.showAtLocation(getView(), Gravity.CENTER, 0, 0);
			confirmDelete.getContentView().findViewById(R.id.yesButton).setOnClickListener(this);
			confirmDelete.getContentView().findViewById(R.id.noButton).setOnClickListener(this);
			confirmDelete.setFocusable(true);
			//confirmDelete.setOutsideTouchable(true);
			confirmDelete.update();
			*/
			
			AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage("Are you sure you want to delete the selected quest?");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    removeQuest();
                }
            });
            
            builder1.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            
			break;
		}
		case R.id.completeQuestButton: {
			Log.i("Debug:", "complete");
		
			//Bring up a confirmatin prompt first
			AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage("Are you sure you want to complete the selected quest?");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	
                	//On clicking yes: Create a new alert that they completed the quest.
                    dialog.cancel();

        			AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    //builder2.setMessage("Are you sure?");
                    builder2.setCancelable(true);
                    builder2.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder2.setView(LayoutInflater.from(getActivity()).inflate(R.layout.complete_quest,null));
                    
                    AlertDialog alert2 = builder2.create();
                    alert2.show();

                    completeQuest();
        			
                }
            });
            
            builder1.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            
            AlertDialog alert11 = builder1.create();
            alert11.show();
			
			break;
		}
		default:
			break;
		}
	}
	
	public void removeQuest(){

		Player player = Player.getPlayer(this);
		Quest quest = player.questManager
				.getQuestFromName(getSelectedQuestName());
		player.questManager.removeQuest(quest);
		selectedQuest = null;
		fillListView(player.questManager, getView());
	}
	
	public void completeQuest(){
		Player player = Player.getPlayer(this);
		Quest quest = player.questManager
				.getQuestFromName(getSelectedQuestName());
		player.questManager.completeQuest(player, quest);
		selectedQuest = null;
		fillListView(player.questManager, getView());
		
	}
}
