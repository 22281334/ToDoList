package comp5216.sydney.edu.au.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class ListAdapter extends BaseAdapter {

    private LinkedList<ToDoList> linkedListData;
    private Context myContext;

    public ListAdapter(LinkedList<ToDoList> linkedListData, Context myContext) {
        this.linkedListData = linkedListData;
        this.myContext = myContext;
    }


    @Override
    public int getCount() {
        return linkedListData.size();
    }

    @Override
    public Object getItem(int position) {
        return linkedListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(myContext).inflate(R.layout.activity_list_adapter, parent, false);
        TextView txt_toDoList = (TextView) convertView.findViewById(R.id.txt_toDoList);
        TextView txt_createTime = (TextView) convertView.findViewById(R.id.txt_createTime);
        TextView txt_modifyTime = (TextView) convertView.findViewById(R.id.txt_modifyTime);
        txt_toDoList.setText(linkedListData.get(position).getToDoList());
        txt_createTime.setText(linkedListData.get(position).getCreateTime());
        txt_modifyTime.setText(linkedListData.get(position).getModifyTime());
        return convertView;
    }

}
