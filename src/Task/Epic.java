package Task;

import java.util.ArrayList;

public class Epic extends Task{

    protected ArrayList<Integer> subtaskIds = new ArrayList<>(); // subtaskIds было idSubtask

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
    }

    public ArrayList<Integer> getsubtaskIds() {
        return subtaskIds;
    }

    public void setsubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

}