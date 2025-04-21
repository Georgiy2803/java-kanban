import java.util.ArrayList;

public class Epic extends Task{

    protected ArrayList<Integer> idSubtask = new ArrayList<>();

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);

    }

    public ArrayList<Integer> getIdSubtask() {
        return idSubtask;
    }

    public void setIdSubtask(ArrayList<Integer> idSubtask) {
        this.idSubtask = idSubtask;
    }

    /* Вопрос к ревьюеру.
    Я опереопределил toString() в классе родителя (Task), однако в классах наследниках (Epic и Subtask) мне не хватает
    некоторых полей для отображения той информации, которая появилась.
    Вопрос. Можно ли с toString() сделать также как с классами, наследовать "Основу", но поменять какие поля или добавить
    другие?
     */
    @Override
    public String toString() {
        return " id - " + id +
                ", " + getClass() +" - " + name +
                ", Статус - " + status +
                ", 'Описание' - " + description +
                ", 'Какие подзадачи входят:' " +  idSubtask + "\n";
    }
}