package comp5216.sydney.edu.au.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // edit request code
    public final int EDIT_ITEM_REQUEST_CODE = 647;
    // add item request code
    public final int ADD_ITEM_REQUEST_CODE = 100;

    // Define variables
    ToDoItemDB db;
    ToDoItemDao toDoItemDao;
    private List<ToDoList> myListData = null;
    private Context myContext;
    private ListAdapter myAdapter = null;
    private ListView listView;
    private String createTime;
    private String modifyTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = ToDoItemDB.getDatabase(this.getApplication().getApplicationContext());
        toDoItemDao = db.toDoItemDao();
        super.onCreate(savedInstanceState);

        // Use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main);
        myContext = MainActivity.this;
        listView = (ListView) findViewById(R.id.lstView);
        myListData = new LinkedList<ToDoList>();
        // read data from local database
        readItemsFromDatabase();
        myAdapter = new ListAdapter((LinkedList<ToDoList>) myListData, myContext);
        listView.setAdapter(myAdapter);
        setupListViewListener();
    }

/**
 *
 * This method put extras data into AddEditItem page
 * Link to Add New Button on activity_main page
 *
 */
    public void onAddItemClick(View view) {
        Intent intent = new Intent(MainActivity.this, AddEditItem.class);
        if (intent != null) {
            // put "extras" into the bundle for access in the edit activity
            intent.putExtra("item", "");
            intent.putExtra("createTime", createTime);
            intent.putExtra("modifyTime", modifyTime);
            intent.putExtra("requestCode", ADD_ITEM_REQUEST_CODE);
            // brings up the second activity
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
            myAdapter.notifyDataSetChanged();
        }

    }

    private void setupListViewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int
                    position, long rowId) {
                Log.i("MainActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.delete, new
                                DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // remove item from the ToDoList
                                        myListData.remove(position);
                                        myAdapter.notifyDataSetChanged();
                                        saveItemsToDatabase();
                                    }
                                })
                        .setNegativeButton(R.string.cancel, new
                                DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // User cancelled the dialog
                                        // Nothing happens
                                    }
                                });
                builder.create().show();
                return true;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToDoList updateItem1 = (ToDoList) myAdapter.getItem(position);
                String updateItem = updateItem1.getToDoList();
                Intent intent = new Intent(MainActivity.this, AddEditItem.class);
                if (intent != null) {
                    // put "extras" into the bundle for access in the edit activity
                    intent.putExtra("item", updateItem);
                    intent.putExtra("modifyTime", modifyTime);
                    intent.putExtra("position", position);
                    intent.putExtra("requestCode", EDIT_ITEM_REQUEST_CODE);
                    // brings up the second activity
                    startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
                    myAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_ITEM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Extract name value from result extras
                String editedItem = data.getExtras().getString("item");
                int position = data.getIntExtra("position", -1);
                modifyTime = data.getExtras().getString("modifyTime");

                ToDoList updateItem1 = (ToDoList) myAdapter.getItem(position);
                updateItem1.setToDoList(editedItem);
                // change the modify time
                updateItem1.setModifyTime(modifyTime);
                // update Log
                Toast.makeText(this, "updated:" + editedItem, Toast.LENGTH_SHORT).show();
                sort();
                myAdapter.notifyDataSetChanged();
                saveItemsToDatabase();
            }
        } else if (requestCode == ADD_ITEM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Extract name value from result extras
                String editedItem = data.getExtras().getString("item");
                createTime = data.getExtras().getString("createTime");
                modifyTime = data.getExtras().getString("modifyTime");
                // add items create time and last modify time
                // create time only change in here
                myListData.add(new ToDoList(editedItem, createTime, modifyTime));
                //sort modify time in correct order
                sort();
                myAdapter.notifyDataSetChanged();
                saveItemsToDatabase();
            }
        }
    }

    private void sort() {
        // set up date time format
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // collections to sort the items by time order
        Collections.sort(myListData, new Comparator<ToDoList>() {
            @Override
            public int compare(ToDoList toDoItemOne, ToDoList toDoItemTwo) {
                try {
                    Date toDoItemOneDate = sdf.parse(getDateString(toDoItemOne));
                    Date toDoItemTwoDate = sdf.parse(getDateString(toDoItemTwo));
                    // return 1 for result is true, -1 for false
                    if (toDoItemOneDate.before(toDoItemTwoDate)) {
                        return 1;
                    } else {
                        return -1;
                    }
                } catch (ParseException e) {
                    Log.i("String convert to Date:", toDoItemOne.getModifyTime()+","+toDoItemTwo.getModifyTime());
                }
                return -1;
            }
        });
        saveItemsToDatabase();
    }

    private void readItemsFromDatabase() {
        //Use asynchronous task to run query on the background and wait for result
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    //read items from database
                    List<ToDoItem> itemsFromDB = toDoItemDao.listAll();
                    myListData = new LinkedList<ToDoList>();
                    // throw AsyncTask to main thread
                    Looper.prepare();
                    if (itemsFromDB != null & itemsFromDB.size() > 0) {
                        for (ToDoItem item : itemsFromDB) {
                            String toDoItem = getToDoItem(item.getToDoItemName(), 0);
                            String createTime = getToDoItem(item.getToDoItemName(), 1);
                            String modifyTime = getToDoItem(item.getToDoItemName(), 2);
                            myListData.add(new ToDoList(toDoItem, createTime, modifyTime));
                        }
                    }
                    return null;
                }
            }.execute().get();
        } catch (Exception ex) {
            Log.e("readItemsFromDatabase", ex.getStackTrace().toString());
        }
    }

    private void saveItemsToDatabase() {
        //Use asynchronous task to run query on the background to avoid locking UI
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //delete all items and re-insert
                toDoItemDao.deleteAll();
                for (ToDoList todo : myListData) {
                    String toDoItemInfo = todo.getToDoList() + "," + todo.getCreateTime() + "," + todo.getModifyTime();
                    ToDoItem item = new ToDoItem(toDoItemInfo);
                    toDoItemDao.insert(item);
                    Log.i("SQLite saved item", toDoItemInfo);
                }
                return null;
            }
        }.execute();
    }

    private String getToDoItem(String toDoItem, int num) {
        int count = 0;
        for (String str : toDoItem.split(",")) {
            if (count == num) {
                return str;
            }
            count += 1;
        }
        return null;
    }


    private String getDateString(ToDoList todo) {
        return todo.getModifyTime().substring(7);
    }
}
