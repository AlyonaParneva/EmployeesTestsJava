import office.Service;

public class InitOfficeDB {
    public static void main(String[] args) {
        Service.createDB();
        System.out.println("База данных Office успешно создана!");
    }
}

