import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

// TODO check whether grass field can appear at current animal position

public class SimulationPanel extends JPanel implements ActionListener, Settings {

    // SETTINGS FOR PLANTS
    private int min_j_x = 11;
    private int min_j_y = 9;
    private int max_j_x = 21;
    private int max_j_y = 15;

    int width_frames = Settings.WIDTH / UNIT_SIZE;
    int height_frames = Settings.HEIGHT / UNIT_SIZE;

    int startXPos = (width_frames / 4) * UNIT_SIZE;

    int startYPos = (height_frames / 3) * UNIT_SIZE;

    int width = (width_frames / 2 - 1) * UNIT_SIZE;
    int height = (height_frames / 3 - 1) * UNIT_SIZE;


    private int animalNumber = 0;
    private int plantNumber = 0;

    JButton jButton;

    private ArrayList<Animal> removed;

    private HashMap<Vector2d, HashSet<Animal>> animals;

    private HashMap<Vector2d, Plant> plants;


    Timer timer;
    boolean running;

    public SimulationPanel() {
        jButton = new JButton("Stop");
        jButton.setBounds(20, Settings.HEIGHT + 20, 50, 20);
        jButton.addActionListener(e -> running = !running);

        jButton.setFocusable(true);
        jButton.setHorizontalTextPosition(JButton.CENTER);


        this.removed = new ArrayList<>();
        this.setPreferredSize(new Dimension(Settings.WIDTH, Settings.SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.setLayout(null);
        this.animals = new HashMap<>();
        this.plants = new HashMap<>();

        this.add(jButton);

        generate();

        running = false;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (running) {
            // cleanUp maps
            cleanUp();

            // remove dead animals
            removeDeadAnimals();

            // move animals
            moveAnimals();

            // eat plant
            eatPlant();

            // make a child
            makeChild();

            // add new plant
            for (int i = 0; i < PLANTS_FOR_DAY; i++){
                addPlant();
            }
        }
        repaint();
    }

    // GENERATE FIRST ANIMALS
    public void generate(){
        for (int i = 0; i < START_ANIMALS_NUMBER; i++){
            int rand_x = (int) Math.floor(Math.random() * (Settings.WIDTH / UNIT_SIZE));
            int rand_y = (int) Math.floor(Math.random() * (Settings.HEIGHT / UNIT_SIZE));

            Vector2d pos = new Vector2d(rand_x * UNIT_SIZE, rand_y * UNIT_SIZE);
            placeAnimal(new Animal(pos, this, Settings.START_ENERGY), pos);
            animalNumber++;
        }
    }

    // DRAW THINGS
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

//        for (int i = 0; i < Settings.WIDTH / UNIT_SIZE; i++) {
//            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, Settings.HEIGHT);
//        }
//        for (int j = 0; j < Settings.HEIGHT / UNIT_SIZE; j++) {
//            g.drawLine(0, j * UNIT_SIZE, Settings.WIDTH, j * UNIT_SIZE);
//        }
        // print stats

        g.setColor(Color.GRAY);
        g.fillRect(0, Settings.HEIGHT, Settings.WIDTH, 100);
        g.setColor(Color.BLACK);
        g.drawString("Animals left: " + animalNumber, 20, Settings.HEIGHT + 50);
        g.drawString("Plants left: " + plantNumber, 20, Settings.HEIGHT + 70);

        // draw jungle
        //drawJungle(g);

        // draw plants
        for (Plant plant : plants.values()) {
            plant.draw(g);
        }

        // draw animals
        for (HashSet<Animal> hashSetSet : animals.values()) {
            for (Animal animal : hashSetSet) {
                animal.draw(g);
            }
        }


    }

    private void drawJungle(Graphics g) {
        g.setColor(new Color(86, 234, 69));
        g.fillRect(startXPos, startYPos, width, height);
    }

    // CLEANING DICTS
    public void cleanUp() {
        ArrayList<Vector2d> toRemove = new ArrayList<>();
        for (Vector2d position : animals.keySet()) {
            if (animals.get(position).isEmpty()) {
                toRemove.add(position);
            }
        }
        for (Vector2d v : toRemove) {
            animals.remove(v);
        }
    }


    // CHANGING POSITIONS
    public void positionChanged(HashMap<Vector2d, HashSet<Animal>> movingAnimals) {
        for (Vector2d position : movingAnimals.keySet()) {
            for (Animal animal : movingAnimals.get(position)) {
                animals.get(position).remove(animal);
                placeAnimal(animal, animal.getPosition());
            }
        }
    }

    public void placeAnimal(Animal animal, Vector2d position) {
        animals.computeIfAbsent(position, k -> new HashSet<>());
        animals.get(position).add(animal);
    }

    public boolean placeChild(Vector2d parentPosition, int energy){
        int tmp = 1;
        while (tmp < 4){
            for (int y_pos = parentPosition.y - tmp; y_pos < parentPosition.y + tmp + 1; y_pos++){
                for (int x_pos = parentPosition.x - tmp; x_pos < parentPosition.x + tmp + 1; x_pos++){
                    if (x_pos >= 0 && x_pos <= Settings.WIDTH - UNIT_SIZE && y_pos >= 0 && y_pos <= Settings.HEIGHT - UNIT_SIZE &&
                            x_pos != parentPosition.x && y_pos != parentPosition.y){
                        Vector2d childPos = new Vector2d(x_pos, y_pos);
                        if (animals.get(childPos) == null && plants.get(childPos) == null){
                            placeAnimal(new Animal(childPos, this, energy), childPos);
                            animalNumber++;
                            return true;
                        }
                    }
                }
            }
            tmp++;
        }
        return false;

    }

    private void moveAnimals() {
        HashMap<Vector2d, HashSet<Animal>> movingAnimals = new HashMap<>();

        for (HashSet<Animal> hashSet : animals.values()) {
            for (Animal animal : hashSet) {
                movingAnimals.computeIfAbsent(animal.getPosition(), k -> new HashSet<>());
                movingAnimals.get(animal.getPosition()).add(animal);
                animal.move();
                animal.animalDays++;
            }
        }
        positionChanged(movingAnimals);
    }


    // REMOVE DEAD
    private void removeDeadAnimals() {
        ArrayList<Animal> animalsToRemove = new ArrayList<>();

        for (Vector2d position : animals.keySet()) {
            for (Animal animal : animals.get(position)) {
                if (animal.currEnergy <= 0) {
                    animal.isAlive = false;
                    animalsToRemove.add(animal);
                }
            }

        }
        for (Animal animal : animalsToRemove) {
            animals.get(animal.getPosition()).remove(animal);
            removed.add(animal);
            animalNumber--;
        }

    }

    // ADD CHILD
    private void makeChild(){
        // child can be made if both parents have energy
        HashMap<Vector2d, Integer> childToAdd = new HashMap<>();

        for (Vector2d position : animals.keySet()){
            if (animals.get(position).size() > 1){
                // find two strongest animals
                int se1 = 0;
                int se2 = 0;
                boolean found1 = false, found2 = false;

                for (Animal animal : animals.get(position)){
                    if (animal.currEnergy > MIN_ENERGY_TO_COPPULATE) {
                        if (animal.currEnergy > se1) {
                            se2 = se1;
                            se1 = animal.currEnergy;
                        } else if (animal.currEnergy > se2) {
                            se2 = animal.currEnergy;
                        }
                    }

                }
                if (se1 > MIN_ENERGY_TO_COPPULATE && se2 > MIN_ENERGY_TO_COPPULATE){
                    int energy = 0;
                    for (Animal animal : animals.get(position)){
                        if (animal.currEnergy == se1 && !found1){
                            found1 = true;
                            energy += animal.currEnergy / 4;
                            animal.currEnergy -= animal.currEnergy / 4;
                            animal.kidsNumber++;
                        } else if (animal.currEnergy == se2 && !found2){
                            found2 = true;
                            energy += animal.currEnergy / 4;
                            animal.currEnergy -= animal.currEnergy / 4;
                            animal.kidsNumber++;
                        }
                    }
                    if (energy > 0){
                        childToAdd.put(position, energy);
                    }
                }

            }

        }

        for (Vector2d parentPosition : childToAdd.keySet()){
            placeChild(parentPosition, childToAdd.get(parentPosition));
        }
    }


    // PLANTS
    private void eatPlant() {
        ArrayList<Plant> eatenPlants = new ArrayList<>();

        // find animals that are nearby plant
        for (Vector2d position : animals.keySet()) {

            // if there is a plant at current position
            if (plants.get(position) != null) {

                // find strongest animals and save them in array

                ArrayList<Animal> strongestAnimals = new ArrayList<>();
                int max_energy = 0;

                // find animal with greatest amount of energy
                for (Animal animal : animals.get(position)) {
                    if (animal.currEnergy > max_energy) {
                        max_energy = animal.currEnergy;
                    }
                }
                // find his friends
                for (Animal animal : animals.get(position)) {
                    if (animal.currEnergy == max_energy) {
                        strongestAnimals.add(animal);
                    }
                }


                // eat plant
                for (Animal animal : strongestAnimals) {
                    animal.eat(Settings.PLANT_ENERGY / strongestAnimals.size());

                }
                eatenPlants.add(plants.get(position));
            }


        }

        // remove eaten plants
        for (Plant plant : eatenPlants) {
            plants.remove(plant.position);
            plantNumber--;
        }
    }

    private void addPlant() {

        // find random not taken position for plant
        addPlantToJungle();

        addPlantNotToJungle();


    }

    private void addPlantNotToJungle() {

        int random_x, random_y;
        Vector2d pos = new Vector2d(-1, -1);
        boolean isTaken = true;
        int counter = 0;

        while (isTaken && counter < 10) {
            int min_nj = 0;
            int max_nj_x = Settings.WIDTH / UNIT_SIZE - 1;
            random_x = (int) Math.floor(Math.random() * (max_nj_x - min_nj + 1) + min_nj);
             int max_nj_y = Settings.HEIGHT / UNIT_SIZE - 1;
            random_y = (int) Math.floor(Math.random() * (max_nj_y - min_nj + 1) + min_nj);

            pos = new Vector2d(random_x * UNIT_SIZE, random_y * UNIT_SIZE);
            isTaken = animals.get(pos) != null && !animals.get(pos).isEmpty() && plants.get(pos) != null && !inJungle(random_x, random_y);
            counter++;
        }
        if (pos.x != -1 && pos.y != -1 && !isTaken) {
            plantNumber++;
            plants.put(pos, new Plant(pos));
        }
    }

    private void addPlantToJungle() {

        int random_x, random_y;
        Vector2d pos = new Vector2d(-1, -1);
        boolean isTaken = true;
        int counter = 0;

        // place in jungle
        while (isTaken && counter < 10) {
            random_x = (int) Math.floor(Math.random() * (startXPos/UNIT_SIZE + width / UNIT_SIZE - startXPos/UNIT_SIZE + 1) + startXPos/UNIT_SIZE);
            random_y = (int) Math.floor(Math.random() * (startYPos/UNIT_SIZE + height / UNIT_SIZE - startYPos/UNIT_SIZE + 1) + startYPos/UNIT_SIZE);
            pos = new Vector2d(random_x * UNIT_SIZE, random_y * UNIT_SIZE);
            isTaken = animals.get(pos) != null && !animals.get(pos).isEmpty() && plants.get(pos) != null;
            counter++;
        }
        if (pos.x != -1 && pos.y != -1 && !isTaken) {
            plants.put(pos, new Plant(pos));
            plantNumber++;
        }
    }

    private boolean inJungle(int x, int y) {
        return x >= startXPos/UNIT_SIZE && x < startXPos/UNIT_SIZE + width/UNIT_SIZE && y >= startYPos/UNIT_SIZE && y < startYPos/UNIT_SIZE + height/UNIT_SIZE;
    }


}
