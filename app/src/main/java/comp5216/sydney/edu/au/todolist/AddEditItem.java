package comp5216.sydney.edu.au.todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEditItem extends AppCompatActivity {

    public int position = 0;
    EditText etItem;
    int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);
        //populate the screen using the layout

        //Get the data from the main screen
        String editItem = getIntent().getStringExtra("item");
        position = getIntent().getIntExtra("position", -1);
        requestCode = getIntent().getIntExtra("requestCode", -1);
        // show original content in the text field
        etItem = (EditText) findViewById(R.id.etEditItem);
        etItem.setText(editItem);
    }

    /**
     *
     * This function for Cancel button Click
     * Link to Add and Edit page Cancel button
     * Yes for finish this page
     * No for back current page
     *
     */
    public void onCancelClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEditItem.this);
        builder.setTitle(R.string.dialog_cancel_title)
                .setMessage(R.string.dialog_cancel_msg)
                .setPositiveButton(R.string.yes, new
                        DialogInterface.OnClickListener() {
                            // click yes to finish page
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                .setNegativeButton(R.string.no, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled the dialog
                                // Nothing happens
                            }
                        });
        builder.create().show();
    }

    /**
     *
     * This function for get data from previous_item page
     * Link to Save button on activity_add_edit Page
     * Receive the edit code to edit item and send back
     * Receive the add code to add item and send back
     *
     */
    public void onSubmit(View view) {
        if (requestCode == new MainActivity().EDIT_ITEM_REQUEST_CODE) {
            etItem = (EditText) findViewById(R.id.etEditItem);

            // Prepare data intent for sending it back
            Intent data = new Intent();

            // Pass relevant data back as a result
            data.putExtra("item", etItem.getText().toString());
            data.putExtra("modifyTime", "Modify:" + getTime());
            data.putExtra("position", position);

            // Activity finished ok, return the data
            setResult(RESULT_OK, data); // set result code and bundle data for response
            finish(); // closes the activity, pass data to parent
        } else if (requestCode == new MainActivity().ADD_ITEM_REQUEST_CODE) {
            String time = getTime();
            Log.i("Updated Item in list:", "In Add page" + time);
            etItem = (EditText) findViewById(R.id.etEditItem);

            // Prepare data intent for sending it back
            Intent data = new Intent();

            // Pass relevant data back as a result
            data.putExtra("item", etItem.getText().toString());
            data.putExtra("createTime", "Create:" + getTime());
            data.putExtra("modifyTime", "Modify:" + getTime());

            // Activity finished ok, return the data
            setResult(RESULT_OK, data); // set result code and bundle data for response
            finish();
        }
    }

    /**
     *
     * Get the current time
     * Return a String current time
     *
     */
    public String getTime() {
        // get current time
        SimpleDateFormat forMatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String toAddString = forMatter.format(date);
        return toAddString;
    }
}
