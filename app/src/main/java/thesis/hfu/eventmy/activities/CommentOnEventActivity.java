package thesis.hfu.eventmy.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import thesis.hfu.eventmy.R;
import thesis.hfu.eventmy.database.DBfunctions;
import thesis.hfu.eventmy.dialogs.*;
import thesis.hfu.eventmy.functions.CheckSharedPreferences;
import thesis.hfu.eventmy.functions.StartActivityFunctions;
import thesis.hfu.eventmy.list_decoration.DividerItemDecoration;


public class CommentOnEventActivity extends ActionBarActivity {

    private TextView totalOrganizersTextView, totalCostsTextView, totalPercentageTextView, eventNameTextView, eventDateTextView,eventLocationTextView;
    private ImageButton addOrganizersButton;
    private int event_id;
    private FloatingActionButton commentOnEventButton;
    private SwipeRefreshLayout syncComments;
    private RecyclerView recyclerComments;

    private static final String EVENT_ID="event_id";
    private final static String COMMENT_ON_EVENT = "comment_on_event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_on_event);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(CheckSharedPreferences.getInstance().isLoggedIn(getApplicationContext())){
            setEvent_id(getIntent().getExtras().getInt(EVENT_ID));
            setRecyclerComments(R.id.recyclerViewAllComments);
            setSyncCommentsView(R.id.swipe_refresh_event_comments);
            setTotalOrganizersTextView(R.id.textViewTaskOfEventTotalOrganizers);
            setTotalCostsTextView(R.id.textViewTaskOfEventTotalCosts);
            setTotalPercentageTextView(R.id.textViewTaskOfEventTotalPercentage);
            setEventNameTextView(R.id.textViewTaskOfEventEventName);
            setEventDateTextView(R.id.textViewTaskOfEventDate);
            setEventLocationTextView(R.id.textViewTaskOfEventLocation);
            setAddOrganizersButton(R.id.imageButtonTasksOfEventOrganizers);
            setCommentOnEventBttuon();
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            getRecyclerComments().setLayoutManager(layoutManager);
            getRecyclerComments().addItemDecoration(new DividerItemDecoration(this));
            getAddOrganizersButton().setOnClickListener(new CustomClickListener());
            getSyncComments().setOnRefreshListener(new CustomSwipeListener());
            getCommentButton().setOnClickListener(new FloatingButtonCustomClickListener());
            DBfunctions.getInstance().getEventComments(getApplicationContext(), getSyncComments(),getRecyclerComments(), event_id);
            DBfunctions.getInstance().updateEventDetails(getApplicationContext(), getSyncComments(), getRecyclerComments(), CheckSharedPreferences.getInstance().getAdmin_id(), getEventTotalOrganizersTextView(), getEventTotalPercentageTextView(), getEventTotalCostsTextView(), getEventNameTextView(), getEventDateTextView(), getEventLocationTextView(), getEvent_id());
        }else{
            CheckSharedPreferences.getInstance().endSession(getApplicationContext());
        }
    }

    //----------------------------------------------------------------------
    //-----------------CUSTOM LISTENER-------------------------------------
    //----------------------------------------------------------------------

    public class CustomClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.imageButtonTasksOfEventOrganizers){
                if(CheckSharedPreferences.getInstance().isLoggedIn(getApplicationContext())){
                    StartActivityFunctions.getInstance().startEventOrganizersActivity(getApplicationContext(), getEvent_id());
                }else{
                    CheckSharedPreferences.getInstance().endSession(getApplicationContext());
                }
            }
        }
    }

    public class FloatingButtonCustomClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v.getTag().equals(COMMENT_ON_EVENT)){
                if(CheckSharedPreferences.getInstance().isLoggedIn(getApplicationContext())){
                    CommentOnEventDialog.getInstance().startCommentDialog(getApplicationContext(),getRecyclerComments(),getSyncComments(),getFragmentManager(), getEvent_id(), CheckSharedPreferences.getInstance().getAdmin_id());
                }else{
                    CheckSharedPreferences.getInstance().endSession(getApplicationContext());
                }
            }
        }
    }

    public class CustomSwipeListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            if (CheckSharedPreferences.getInstance().isLoggedIn(getApplicationContext())) {
                DBfunctions.getInstance().getEventComments(getApplicationContext(), getSyncComments(),getRecyclerComments(), event_id);
                DBfunctions.getInstance().updateEventDetails(getApplicationContext(), getSyncComments(), getRecyclerComments(), CheckSharedPreferences.getInstance().getAdmin_id(), getEventTotalOrganizersTextView(), getEventTotalPercentageTextView(), getEventTotalCostsTextView(), getEventNameTextView(), getEventDateTextView(), getEventLocationTextView(), getEvent_id());
            } else {
                CheckSharedPreferences.getInstance().endSession(getApplicationContext());
            }
        }
    }

    //----------------------------------------------------------------------
    //-----------------ACTION BAR MENU-------------------------------------
    //----------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            StartActivityFunctions.getInstance().startSearchActivity(getApplicationContext());
            return true;
        } else if (item.getItemId() == R.id.action_friends) {
            StartActivityFunctions.getInstance().startFriendsListActivity(getApplicationContext());
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            StartActivityFunctions.getInstance().startAllTasksActivity(getApplicationContext(),getEvent_id());
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            LogoutDialog.getInstance().startLogoutDialog(getFragmentManager());
            return true;
        } else if (item.getItemId() == R.id.action_archived_events) {
            StartActivityFunctions.getInstance().startArchivedEventsActivity(getApplicationContext());
            return true;
        }else if (item.getItemId() == R.id.action_edit) {
            EditEventDialog.getInstance().startEditEventDialog(getFragmentManager(),getApplicationContext(),getEventDateTextView(),getEventNameTextView(),getEventDateTextView(),getEventLocationTextView(),getEvent_id());
            return true;
        }else if (item.getItemId() == R.id.action_delete) {
            DeleteArchivEventDialog.getInstance().startDeleteArchivEventDialog(getFragmentManager(),getApplicationContext(),getEvent_id());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------
    //-----------------Getter and Setter-------------------------------------
    //----------------------------------------------------------------------

    public int getEvent_id() {
        return event_id;
    }
    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }
    public void setCommentOnEventBttuon(){
        ImageView icon = new ImageView(this);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.commenticonbig));

        this.commentOnEventButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setBackgroundDrawable(R.drawable.add_button_shape)
                .build();
        commentOnEventButton.setTag(COMMENT_ON_EVENT);
    }
    public FloatingActionButton getCommentButton(){
        return this.commentOnEventButton;
    }
    public SwipeRefreshLayout getSyncComments() {
        return syncComments;
    }
    public void setSyncCommentsView(int res) {
        this.syncComments = (SwipeRefreshLayout) findViewById(res);
    }
    public void setAddOrganizersButton(int res) {
        this.addOrganizersButton = (ImageButton) findViewById(res);
    }
    public void setTotalOrganizersTextView(int res) {
        this.totalOrganizersTextView = (TextView) findViewById(res);
    }
    public void setTotalCostsTextView(int res) {
        this.totalCostsTextView = (TextView) findViewById(res);
    }
    public void setTotalPercentageTextView(int res) {
        this.totalPercentageTextView = (TextView) findViewById(res);
    }
    public void setEventNameTextView(int res) {
        this.eventNameTextView = (TextView) findViewById(res);
    }
    public void setEventDateTextView(int res) {
        this.eventDateTextView = (TextView) findViewById(res);
    }
    public TextView getEventNameTextView(){
        return this.eventNameTextView;
    }
    public TextView getEventDateTextView(){
        return this.eventDateTextView;
    }
    public TextView getEventTotalOrganizersTextView(){
        return this.totalOrganizersTextView;
    }
    public TextView getEventTotalCostsTextView(){
        return this.totalCostsTextView;
    }
    public TextView getEventTotalPercentageTextView(){
        return this.totalPercentageTextView;
    }
    public ImageButton getAddOrganizersButton() {
        return addOrganizersButton;
    }
    public TextView getEventLocationTextView() {
        return eventLocationTextView;
    }
    public void setEventLocationTextView(int eventLocationTextView) {
        this.eventLocationTextView= (TextView) findViewById(eventLocationTextView);
    }
    public RecyclerView getRecyclerComments() {
        return recyclerComments;
    }
    public void setRecyclerComments(int res) {
        this.recyclerComments = (RecyclerView) findViewById(res);
    }

}
