import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Animal implements Settings{
    public Vector2d position;
    private final ArrayList<String> genes;
    private final Random rand;
    private final ArrayList<MoveDirection> directions;
    public boolean isAlive;
    public boolean ifDisplay = true;

    public int kidsNumber = 0;
    public int animalDays = 0;

    private SimulationPanel sm;
    private final int maxEnergy = 50;
    public int currEnergy;

    private final int r=255,g=28, b=3;
    public Color color = new Color(r, g, b);

    public Animal(Vector2d position, SimulationPanel sm, int currEnergy) {
        this.isAlive = true;
        this.sm = sm;
        this.currEnergy = currEnergy;

        this.position = position;
        this.genes = new ArrayList<>();
        genes.add("0");
        genes.add("1");
        genes.add("2");
        genes.add("3");
        genes.add("4");
        genes.add("5");
        genes.add("6");
        genes.add("7");
        genes.add("8");


        rand = new Random();

        this.directions = new ArrayList<>();
        convert();
    }

    public void move(){
        animalDays++;
        MoveDirection direction = directions.get(rand.nextInt(directions.size()));

        Vector2d newPos = position.add(direction.toUnitVector().multiply(UNIT_SIZE, UNIT_SIZE));

        if (newPos.x >= 0 && newPos.x <= WIDTH - UNIT_SIZE && newPos.y >= 0 && newPos.y <= HEIGHT - UNIT_SIZE){
            setPosition(position.add(direction.toUnitVector().multiply(UNIT_SIZE, UNIT_SIZE)));

            currEnergy--;

        }
        changeColor();
    }

    private void displayInfo(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRoundRect(position.x + 5, position.y - 25, 40, 20, 10, 10);
        g.setColor(Color.BLACK);
        g.drawString(" " + animalDays, position.x + 8, position.y - 14);
    }

    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval(position.x, position.y, UNIT_SIZE, UNIT_SIZE);
        //g.setColor(Color.white);
        //g.drawString("" + currEnergy, position.x + 6, position.y + 16);
//        if (ifDisplay){
//            displayInfo(g);
//        }
    }

    private void convert(){
        for (String dir : genes){
            switch (dir){
                case "1" -> {
                    directions.add(MoveDirection.N);
                }
                case "2" -> {
                    directions.add(MoveDirection.N_E);
                }
                case "3" -> {
                    directions.add(MoveDirection.E);
                }
                case "4" -> {
                    directions.add(MoveDirection.S_E);
                }
                case "5" -> {
                    directions.add(MoveDirection.S);
                }
                case "6" -> {
                    directions.add(MoveDirection.S_W);
                }
                case "7" -> {
                    directions.add(MoveDirection.W);
                }case "8" -> {
                    directions.add(MoveDirection.N_W);
                }

            }
        }
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public Vector2d getPosition() {
        return position;
    }

    public void changeColor(){
        if (currEnergy >= 50){
            color = new Color(255, 0, 0);
        } else if (currEnergy >= 40){
            color = new Color(255, 29, 29);
        } else if (currEnergy >= 30){
            color = new Color(253, 90, 90);
        } else if (currEnergy >= 20){
            color = new Color(253, 133, 133);
        } else if (currEnergy >= 10){
            color = new Color(255, 179, 179);
        } else if (currEnergy >= 0){
            color = new Color(255, 248, 248);
        }

    }

    public void eat(int energy){
        currEnergy += energy;
        changeColor();
    }

}
