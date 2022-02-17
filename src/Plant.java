import java.awt.*;

public class Plant implements Settings{
    Vector2d position;

    public Plant(Vector2d position) {
        this.position = position;
    }

    public void draw(Graphics g){
        g.setColor(new Color(19, 90, 30, 255));
        g.fillRect(position.x, position.y, UNIT_SIZE, UNIT_SIZE);
    }
}
