package com.astrapia.astrapia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.astrapia.astrapia.AttendanceSheetActivity;
import com.astrapia.astrapia.R;
import com.astrapia.astrapia.models.AttendanceSheet;

import java.util.List;

public class AttendanceSheetsAdapter extends RecyclerView.Adapter<AttendanceSheetsAdapter.ViewHolder> {

    private List<AttendanceSheet> attendanceSheetList;
    private Context context;

    // Pass in the array into the constructor
    public AttendanceSheetsAdapter(List<AttendanceSheet> attendanceSheetList, Context context) {
        this.attendanceSheetList = attendanceSheetList;
        this.context = context;
    }

    @NonNull
    @Override
    public AttendanceSheetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View attendanceSheetView = inflater.inflate(R.layout.item_attendance_sheet, parent, false);

        // Return a new holder instance
        return new ViewHolder(attendanceSheetView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final AttendanceSheet attendanceSheet = attendanceSheetList.get(position);

        TextView sheetNameTextView = holder.sheetNameTextView;
        ImageButton openSheetImageButton = holder.openSheetImageButton;

        sheetNameTextView.setText(attendanceSheet.getName());

        openSheetImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AttendanceSheetActivity.class);
                intent.putExtra("title", attendanceSheet.getName());
                intent.putExtra("name", attendanceSheet.getName());
                intent.putExtra("id", attendanceSheet.getId());
                intent.putExtra("date", attendanceSheet.getDate());
                intent.putExtra("scheduleId", attendanceSheet.getScheduleId());
                intent.putExtra("teacherId", attendanceSheet.getTeacherId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendanceSheetList.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView sheetNameTextView;
        ImageButton openSheetImageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            sheetNameTextView = itemView.findViewById(R.id.sheet_name);
            openSheetImageButton = itemView.findViewById(R.id.open_sheet);
        }
    }
}

