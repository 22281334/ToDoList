package comp5216.sydney.edu.au.todolist;

import android.support.v7.app.AppCompatActivity;
public class ToDoList extends AppCompatActivity {

    private String toDoList;
    private String createTime;
    private String modifyTime;

    public ToDoList(String toDoList, String createTime, String modifyTime) {
        this.toDoList = toDoList;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
    }

    public String getToDoList() {
        return toDoList;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setToDoList(String toDoList) {
        this.toDoList = toDoList;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
}
