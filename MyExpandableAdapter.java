package com.example.orestis.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MyExpandableAdapter extends BaseExpandableListAdapter
{
    public static String perioxiSelected,perioxiOnlySelected;
    public static String serverReply;
    private Activity activity;
    public static boolean justselected;
    private ArrayList<Object> childtems;
    private LayoutInflater inflater;
    private ArrayList<String> parentItems, child;

    // constructor
    public MyExpandableAdapter(ArrayList<String> parents, ArrayList<Object> childern)
    {
        this.parentItems = parents;
        this.childtems = childern;
    }

    public void setInflater(LayoutInflater inflater, Activity activity)
    {
        this.inflater = inflater;
        this.activity = activity;
    }

    // method getChildView is called automatically for each child view.
    //  Implement this method as per your requirement
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {

        child = (ArrayList<String>) childtems.get(groupPosition);

        TextView textView;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_view, null);
        }

        // get the textView reference and set the value
        textView = (TextView) convertView.findViewById(R.id.textViewChild);
        textView.setText(child.get(childPosition));

        // set the ClickListener to handle the click event on child item
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                perioxiSelected = parentItems.get(groupPosition) + " : " + child.get(childPosition);
                perioxiOnlySelected = child.get(childPosition);
                if(ExpandableListMainActivity.signup==false) {
                    if(ExpandableListMainActivity.requestType.equals("livePerioxi")) {
                        requestTop100PerioxiDaily req = new requestTop100PerioxiDaily();
                        req.execute();
                    }else if(ExpandableListMainActivity.requestType.equals("generalPerioxi")){
                        requestTop100Perioxi req = new requestTop100Perioxi();
                        req.execute();
                    }
                }else {
                    justselected = true;
                }
            }
        });
        return convertView;
    }
    private class requestTop100PerioxiDaily extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        try {
            PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
            printwriter.println("requestTop100PerioxiDaily---"+perioxiSelected); // write the message to output stream

            InputStreamReader inputStreamReader = new InputStreamReader(SocketHandler.getSocket().getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            do{
                serverReply=bufferedReader.readLine();
            }while(!serverReply.contains("replyTop100PerioxiDaily:"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        justselected = true;
    }
}

    private class requestTop100Perioxi extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PrintWriter printwriter = new PrintWriter(SocketHandler.getSocket().getOutputStream(), true);
                printwriter.println("requestTop100Perioxi---"+perioxiSelected); // write the message to output stream

                InputStreamReader inputStreamReader = new InputStreamReader(SocketHandler.getSocket().getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                do{
                    serverReply=bufferedReader.readLine();
                }while(!serverReply.contains("replyTop100Perioxi:"));

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            justselected = true;
        }
    }
    // method getGroupView is called automatically for each parent item
    // Implement this method as per your requirement
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.parent_view, null);
        }

        ((CheckedTextView) convertView).setText(parentItems.get(groupPosition));
        ((CheckedTextView) convertView).setChecked(isExpanded);

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return ((ArrayList<String>) childtems.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return null;
    }

    @Override
    public int getGroupCount()
    {
        return parentItems.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition)
    {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition)
    {
        super.onGroupExpanded(groupPosition);


    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }

}
