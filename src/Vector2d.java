import java.util.Objects;

public class Vector2d {
    final public int x;
    public final int y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }
    boolean precedes(Vector2d other){
        return this.x <= other.x && this.y <= other.y;
    }
    boolean follows(Vector2d other){
        return this.x >= other.x && this.y >= other.y;
    }

    Vector2d upperRight(Vector2d other){
        return new Vector2d(Math.max(this.x, other.x), Math.max(this.y, other.y));
    }

    Vector2d lowerLeft(Vector2d other){
        return new Vector2d(Math.min(this.x, other.x), Math.min(this.y, other.y));
    }

    Vector2d add(Vector2d other){
        return new Vector2d(this.x + other.x, this.y + other.y);
    }

    Vector2d substract(Vector2d other){
        return new Vector2d(this.x - other.x, this.y - other.y);
    }

    Vector2d opposite(){
        return new Vector2d(this.x * (-1), this.y * (-1));
    }

    Vector2d multiply(int x, int y){
        return new Vector2d(this.x * x, this.y * y);
    }

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2d vector2d = (Vector2d) o;
        return x == vector2d.x && y == vector2d.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
