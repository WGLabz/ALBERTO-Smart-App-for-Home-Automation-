package in.blogspot.weargenius.alberto.notes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.NotesDetailDB;

public class adapterForNotesList extends RecyclerView.Adapter<adapterForNotesList.ViewHolder> {
    private final TypedValue mTypedValue = new TypedValue();
    private ArrayList<String> noteHeaderContent;
    private ArrayList<String> noteBodyContent;
    private ArrayList<String> noteDateContent;
    private ArrayList<String> noteTimeContent;
    private ArrayList<String> noteSnapsCountContent;
    private ArrayList<Integer> noteId;
    private int mBackground;
    private Context notesInterfaceContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public adapterForNotesList(Context con, ArrayList<Integer> notesId, ArrayList<String> noteHeaderData, ArrayList<String> noteBodyData, ArrayList<String> noteDateData, ArrayList<String> noteTimeData, ArrayList<String> noteSnapsCountData) {
        noteHeaderContent = noteHeaderData;
        noteBodyContent = noteBodyData;
        noteDateContent = noteDateData;
        noteTimeContent = noteTimeData;
        noteSnapsCountContent = noteSnapsCountData;
        noteId = notesId;
        notesInterfaceContext = con;
        con.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;

    }

    public void remove(String item) {
        int position = noteHeaderContent.indexOf(item);
        noteHeaderContent.remove(position);
        noteBodyContent.remove(position);
        noteDateContent.remove(position);
        noteTimeContent.remove(position);
        noteSnapsCountContent.remove(position);
        noteId.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public adapterForNotesList.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_interface_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v.setBackgroundResource(mBackground);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.noteHeader.setText(noteHeaderContent.get(position));
        holder.noteBody.setText(noteBodyContent.get(position));
        holder.noteDate.setText(noteDateContent.get(position));
        holder.noteTime.setText(noteTimeContent.get(position));
        holder.noteSnapsCount.setText(noteSnapsCountContent.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("View/Delete : " + noteHeaderContent.get(position));
                builder.setMessage(
                        "Body : " + noteBodyContent.get(position) + " (" + noteDateContent.get(position) + " " + noteTimeContent.get(position) + " )");

                builder.setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(builder.getContext(), DetailNote.class);
                        intent.putExtra("NOTE_TITLE", noteHeaderContent.get(position));
                        intent.putExtra("NOTE_ID", noteId.get(position));
                        intent.putExtra("NOTE_BODY", noteBodyContent.get(position));
                        intent.putExtra("NO_IMAGES", noteSnapsCountContent.get(position));
                        builder.getContext().startActivity(intent);
                        ((Activity) notesInterfaceContext).finish();
                    }
                });
                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //View Notes
                        NotesDetailDB notesDB = new NotesDetailDB(notesInterfaceContext);
                        notesDB.open();
                        notesDB.deleteNote(noteId.get(position));
                        notesDB.close();
                        remove(noteHeaderContent.get(position));
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

            }
        });

        Glide.with(holder.noteImage.getContext())
                .load(R.drawable.aloo_bharta)
                .fitCenter()
                .into(holder.noteImage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return noteHeaderContent.size();
    }

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final View mView;
        private TextView noteHeader;
        private TextView noteBody;
        private TextView noteDate;
        private TextView noteTime;
        private TextView noteSnapsCount;
        private TextView deleteText;
        private ImageView noteImage;
        private ImageView deleteImage;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            noteHeader = (TextView) v.findViewById(R.id.note_heading);
            noteBody = (TextView) v.findViewById(R.id.note_body);
            noteDate = (TextView) v.findViewById(R.id.note_date);
            noteTime = (TextView) v.findViewById(R.id.note_time);
            noteSnapsCount = (TextView) v.findViewById(R.id.snaps_count);
            noteImage = (ImageView) v.findViewById(R.id.noteImage);
            deleteImage = (ImageView) v.findViewById(R.id.delete_image);
            deleteText = (TextView) v.findViewById(R.id.delete_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + noteHeader.getText();
        }
    }

}