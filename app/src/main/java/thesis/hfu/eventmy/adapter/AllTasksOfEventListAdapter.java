package thesis.hfu.eventmy.adapter;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import thesis.hfu.eventmy.R;
import thesis.hfu.eventmy.database.DBfunctions;
import thesis.hfu.eventmy.dialogs.EditCostsDialog;
import thesis.hfu.eventmy.dialogs.EditPercentageDialog;
import thesis.hfu.eventmy.functions.CheckSharedPreferences;
import thesis.hfu.eventmy.functions.StartActivityFunctions;
import thesis.hfu.eventmy.objects.Task;

import java.util.ArrayList;


public class AllTasksOfEventListAdapter extends
        RecyclerView.Adapter<AllTasksOfEventListAdapter.MyViewHolder>  {

    private ArrayList<Task> tasks;
    private Context context;
    private MyViewHolder viewHolder;
    private TextView eventNameTextView, eventDateTextView, eventTotalOrganizersTextView, eventTotalCostsTextView, eventTotalPercentageTextView,eventLocationTextView;
    private int event_id;
    private final int typeOfUpdate=1;
    private FragmentManager fragmentManager;

    public AllTasksOfEventListAdapter(Activity context,ArrayList<Task> list,TextView eventNameTextView,TextView eventDateTextView,TextView eventTotalOrganizersTextView,TextView eventTotalCostsTextView,TextView eventTotalPercentageTextView,TextView eventLocationTextView,int event_id) {
        this.tasks = list;
        this.context=context;
        this.eventNameTextView = eventNameTextView;
        this.event_id=event_id;
        this.eventDateTextView = eventDateTextView;
        this.eventTotalCostsTextView = eventTotalCostsTextView;
        this.eventTotalOrganizersTextView = eventTotalOrganizersTextView;
        this.eventTotalPercentageTextView = eventTotalPercentageTextView;
        this.eventLocationTextView=eventLocationTextView;
        this.fragmentManager=context.getFragmentManager();
    }
    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final Task task = this.tasks.get(position);

        viewHolder.taskTextView.setText(task.getTask());
        viewHolder.quantityTextView.setText(task.getQuantity());
        viewHolder.costTextView.setText(String.valueOf(task.getCostOfTask()));
        viewHolder.percentageTextView.setText(String.valueOf(task.getPercentage()));
        viewHolder.editorTextView.setText(task.getEditor_name());

        viewHolder.editorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewHolder(viewHolder);
                if (CheckSharedPreferences.getInstance().isLoggedIn(context.getApplicationContext())) {
                    DBfunctions.getInstance().changeEditorOfTask(context.getApplicationContext(),getViewHolder().editorTextView,CheckSharedPreferences.getInstance().getAdmin_id(), task.getTask_id());
                } else {
                    CheckSharedPreferences.getInstance().endSession(context.getApplicationContext());
                }
            }
        });

        viewHolder.percentageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewHolder(viewHolder);
                EditPercentageDialog.getInstance().startEditPercentageDialog(getFragmentManager(), viewHolder.percentageTextView, getEventTotalOrganizersTextView(), getEventTotalCostsTextView(), getEventTotalPercentageTextView(), getEventNameTextView(), getEventDateTextView(),getEventLocationTextView(), task.getTask_id(),getEvent_id(),getTypeOfUpdate());
            }
        });

        viewHolder.costButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewHolder(viewHolder);
                EditCostsDialog.getInstance().startEditTaskDialog(getFragmentManager(), viewHolder.costTextView, getEventTotalOrganizersTextView(), getEventTotalCostsTextView(), getEventTotalPercentageTextView(), getEventNameTextView(), getEventDateTextView(),getEventLocationTextView(),getEvent_id(),task.getTask_id(),getTypeOfUpdate());
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        View itemView = LayoutInflater.from(arg0.getContext()).inflate(
                R.layout.list_task_of_event_row, arg0, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        ImageButton costButton,percentageButton,editorButton;
        TextView costTextView, percentageTextView, editorTextView, taskTextView, quantityTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            costButton = (ImageButton) itemView.findViewById(R.id.imageButtonListRowTaskOfEventCosts);
            percentageButton = (ImageButton) itemView.findViewById(R.id.imageButtonListRowTaskOfEventPercentage);
            editorButton = (ImageButton) itemView.findViewById(R.id.imageButtonlistRowTaskOfEventPercentage);
            costTextView = (TextView) itemView.findViewById(R.id.textViewListRowTaskOfEventCosts);
            percentageTextView = (TextView) itemView.findViewById(R.id.textViewListRowTaskOfEventPercentage);
            editorTextView = (TextView) itemView.findViewById(R.id.textViewlistRowTaskOfEventOrganizer);
            taskTextView = (TextView) itemView.findViewById(R.id.textViewListRowTaskOfEventName);
            quantityTextView = (TextView) itemView.findViewById(R.id.textViewListRowTaskOfEventDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(CheckSharedPreferences.getInstance().isLoggedIn(context.getApplicationContext())){
            StartActivityFunctions.getInstance().startEditTaskActivity(context.getApplicationContext(),getTasks().get(getPosition()).getTask_id(),getEvent_id());
            }else{
                CheckSharedPreferences.getInstance().endSession(context.getApplicationContext());
            }
        }
    }

    //----------------------------------------------------------------------
    //-----------------Getter and Setter-------------------------------------
    //----------------------------------------------------------------------

    public void setViewHolder(MyViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }
    public ArrayList<Task> getTasks(){
        return this.tasks;
    }
    public MyViewHolder getViewHolder() {
        return viewHolder;
    }
    public TextView getEventNameTextView() {
        return eventNameTextView;
    }
    public TextView getEventDateTextView() {
        return eventDateTextView;
    }
    public TextView getEventTotalOrganizersTextView() {
        return eventTotalOrganizersTextView;
    }
    public TextView getEventTotalCostsTextView() {
        return eventTotalCostsTextView;
    }
    public TextView getEventTotalPercentageTextView() {
        return eventTotalPercentageTextView;
    }
    public int getEvent_id() {
        return event_id;
    }
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }
    public int getTypeOfUpdate() {
        return typeOfUpdate;
    }
    public TextView getEventLocationTextView() {
        return eventLocationTextView;
    }
}