public class Subtask extends Task {

    protected int idEpic;

    public Subtask(String name, String description, int id, Status status, int idEpic) {
        super(name, description, id, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return " id - " + id +
                ", " + getClass() +" - " + name +
                ", Статус - " + status +
                ", 'Описание' - " + description +
                ", Принадлежит Эпику: - " + idEpic +"\n";
    }
}