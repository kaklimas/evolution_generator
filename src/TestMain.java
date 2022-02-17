import java.util.HashMap;

public class TestMain {
    public static void main(String[] args) {
        HashMap<Vector2d, Animal> hashMap = new HashMap<>();
        Vector2d pos1 = new Vector2d(0, 0);
        Animal a1 = new Animal(pos1, new SimulationPanel(), 20);
    }
}
