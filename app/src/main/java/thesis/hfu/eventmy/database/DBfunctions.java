package thesis.hfu.eventmy.database;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import thesis.hfu.eventmy.adapter.*;
import thesis.hfu.eventmy.functions.BuildJSON;
import thesis.hfu.eventmy.functions.CheckSharedPreferences;
import thesis.hfu.eventmy.functions.StartActivityFunctions;
import thesis.hfu.eventmy.objects.Event;
import thesis.hfu.eventmy.objects.Task;
import thesis.hfu.eventmy.objects.User;

import java.sql.Date;
import java.util.ArrayList;

public class DBfunctions {

    private static DBfunctions instance;
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";

    //Login
    private static final String USER_ID = "user_id";
    private static final String URL_LOGIN = "login_check.php";

    //Registration
    private static final String URL_REGISTRATION = "register_user.php";

    //All Events
    private static final String EVENTS = "events";
    private static final String URL_GET_ALL_EVENTS = "get_all_events.php";

    //Create Event
    private static final String URL_CREATE_EVENT = "create_event.php";

    //Create Task
    private static final String URL_CREATE_TASK = "create_task.php";

    //Update all Tasks
    private static final String TASKS = "tasks";
    private static final String URL_GET_ALL_TASKS_OF_EVENT = "get_all_tasks_from_event.php";

    //Search
    private static final String USERS = "users";
    private static final String URL_SEARCH_USER = "search_user.php";

    //Search Friend for Event
    private static final String URL_SEARCH_FRIENDS_EVENT = "search_friends_for_event.php";

    //Get FriendsList
    private static final String URL_GET_FRIENDSLIST = "get_friendlist.php";

    //UPDATE EVENT DETAILS
    private static final String URL_UPDATE_EVENT_DETAILS = "update_event_details.php";




    public static DBfunctions getInstance() {
        if (DBfunctions.instance == null) {
            DBfunctions.instance = new DBfunctions();
        }
        return DBfunctions.instance;
    }

    public void login(final Context context, String email, String password) {
        RequestParams params = BuildJSON.getInstance().loginJSON(email, password);
        DBconnection.post(URL_LOGIN, params, new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(context.getApplicationContext(), response.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    if (response.getInt(STATUS) == 200) {
                        CheckSharedPreferences.getInstance().setPreferances(context.getApplicationContext(), response.getString(USER_ID));
                        StartActivityFunctions.getInstance().startAllEventsActivity(context.getApplicationContext());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void registration(final Context context, String name, String prename, String email, String password) {

        RequestParams params = BuildJSON.getInstance().registrationJSON(name, prename, email, password);
        DBconnection.post(URL_REGISTRATION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(context.getApplicationContext(), response.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    StartActivityFunctions.getInstance().startLoginActivity(context.getApplicationContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getAllEvents(final Context context, final SwipeRefreshLayout swipeRefreshLayout, final RecyclerView recyclerView, String admin_id) {

        RequestParams params = BuildJSON.getInstance().getAllEventsJSON(admin_id);
        DBconnection.post(URL_GET_ALL_EVENTS, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(context.getApplicationContext(), response.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    if (response.getInt(STATUS) == 200) {
                        ArrayList<Event> eventList = BuildJSON.getInstance().getAllEventsJSON(response.getJSONArray(EVENTS));
                        RecyclerView.Adapter<AllEventsListAdapter.MyViewHolder> recAdapter = new AllEventsListAdapter(context.getApplicationContext(), eventList);
                        recyclerView.setAdapter(recAdapter);
                        if(swipeRefreshLayout!=null){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getAllTasks(final Context context, final SwipeRefreshLayout swipeRefreshLayout, final RecyclerView recyclerView, final int event_id, final TextView totalOrg, final TextView totalPerc, final TextView totalCos, final TextView name, final TextView date) {

        RequestParams params = BuildJSON.getInstance().getAllTasksJSON(event_id);
        DBconnection.post(URL_GET_ALL_TASKS_OF_EVENT, params, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Toast.makeText(context.getApplicationContext(), response.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                            if (response.getInt(STATUS) == 200) {
                                ArrayList<Task> taskList = BuildJSON.getInstance().getAllTasksOfEventJSON(response.getJSONArray(TASKS));
                                RecyclerView.Adapter<AllTasksOfEventListAdapter.MyViewHolder> recAdapter = new AllTasksOfEventListAdapter(context.getApplicationContext(), taskList,event_id, name, date, totalOrg, totalCos, totalPerc);
                                recyclerView.setAdapter(recAdapter);
                                if (swipeRefreshLayout != null) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    public void updateEventDetails(final SwipeRefreshLayout swipeRefreshLayout,String admin_id,int event_id, final TextView totalOrg, final TextView totalPercentage, final TextView totalCosts, final TextView event_name, final TextView event_date) {

        RequestParams params = BuildJSON.getInstance().updateEventDetailsJSON(admin_id, event_id);
        DBconnection.post(URL_UPDATE_EVENT_DETAILS, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt(STATUS) == 200) {
                        Event event = BuildJSON.getInstance().getEventJSON(response.getJSONArray(EVENTS));
                        event_name.setText(event.getName());
                        event_date.setText(event.getDate().getDate()+"."+(event.getDate().getMonth()+1)+"."+event.getDate().getYear());
                        totalOrg.setText(event.getNumOrganizers() + "");
                        totalCosts.setText(String.valueOf(event.getCosts()));
                        totalPercentage.setText(String.valueOf(event.getPercentage_of_event()));
                        if(swipeRefreshLayout!=null){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void createEvent(final Context context, String eventName, String location, Date date, String admin_id) {

        RequestParams params = BuildJSON.getInstance().createEventJSON(eventName, location, date, admin_id);
        DBconnection.post(URL_CREATE_EVENT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(context.getApplicationContext(), response.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    StartActivityFunctions.getInstance().startAllEventsActivity(context.getApplicationContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void createTask(final Context context, final int event_id, int editor_id, String task, String description, String quantity) {

        RequestParams params = BuildJSON.getInstance().createTaskJSON(event_id, editor_id, task, description, quantity);
        DBconnection.post(URL_CREATE_TASK, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(context.getApplicationContext(), response.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    StartActivityFunctions.getInstance().startAllTasksActivity(context.getApplicationContext(), event_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void searchAllUsers(final Context context, final SwipeRefreshLayout swipeRefreshLayout, final RecyclerView recyclerView, String search, String admin_id) {

        RequestParams params = BuildJSON.getInstance().searchAllUsersJSON(search, admin_id);
        DBconnection.post(URL_SEARCH_USER, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(context.getApplicationContext(), response.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    if (response.getInt(STATUS) == 200) {
                        ArrayList<User> userList = BuildJSON.getInstance().getAllUsersJSON(response.getJSONArray(USERS));
                        RecyclerView.Adapter<SearchListAdapter.MyViewHolder> recAdapter = new SearchListAdapter(context.getApplicationContext(), userList);
                        recyclerView.setAdapter(recAdapter);
                        if(swipeRefreshLayout!=null){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void searchFriendsForEvent(final Context context,final SwipeRefreshLayout swipeRefreshLayout, final RecyclerView recyclerView, String search, String admin_id, final int event_id) {

        RequestParams params = BuildJSON.getInstance().searchFriendsForEventJSON(admin_id, event_id, search);
        DBconnection.post(URL_SEARCH_FRIENDS_EVENT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(context.getApplicationContext(), response.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    if (response.getInt(STATUS) == 200) {
                        ArrayList<User> userList = BuildJSON.getInstance().getAllUsersJSON(response.getJSONArray(USERS));
                        RecyclerView.Adapter<EventOrganizersListAdapter.MyViewHolder> recAdapter = new EventOrganizersListAdapter(context.getApplicationContext(), userList,event_id);
                        recyclerView.setAdapter(recAdapter);
                        if(swipeRefreshLayout!=null){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void searchFriendsList(final Context context, final SwipeRefreshLayout swipeRefreshLayout, final RecyclerView recyclerView, String search, String admin_id) {

        RequestParams params = BuildJSON.getInstance().getFriendsListJSON(search,admin_id);
        DBconnection.post(URL_GET_FRIENDSLIST, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(context.getApplicationContext(), response.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    if (response.getInt(STATUS) == 200) {
                        ArrayList<User> userList = BuildJSON.getInstance().getAllUsersJSON(response.getJSONArray(USERS));
                        RecyclerView.Adapter<FriendsListAdapter.MyViewHolder> recAdapter = new FriendsListAdapter(context.getApplicationContext(), userList);
                        recyclerView.setAdapter(recAdapter);
                        if(swipeRefreshLayout!=null){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
