public enum MoveDirection {
    N, N_E, E, S_E, S, S_W, W, N_W;

    Vector2d toUnitVector(){
        switch (this){
            case N -> {
                return new Vector2d(0, -1);
            }
            case N_E -> {
                return new Vector2d(1, -1);
            }
            case E -> {
                return new Vector2d(1, 0);
            }
            case S_E -> {
                return new Vector2d(1, 1);
            }
            case S -> {
                return new Vector2d(0, 1);
            }
            case S_W -> {
                return new Vector2d(-1, 1);
            }
            case W -> {
                return new Vector2d(-1, 0);
            }
            case N_W -> {
                return new Vector2d(-1, -1);
            }
            default -> {
                return new Vector2d(0, 0);
            }
        }
    }
}
