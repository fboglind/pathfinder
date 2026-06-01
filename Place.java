// PROG2 VT2023, Inlämmningsuppgift, del 2
// Grupp 028
// Gabriel Bendezu gabe3137
// Fredrik Boglind frbo5627
// Maria Fernanda Esquivel Hidalgo maes3583

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Map marker for a named place.
 */
public class Place extends Circle {

    private final String name;

    /**
     * Creates a place marker.
     *
     * @param name place name
     * @param x x coordinate
     * @param y y coordinate
     */
    public Place(String name, double x, double y) {
        super(x, y, 10, Color.BLUE);
        this.name = name;
    }

    /**
     * Returns the place name.
     *
     * @return place name
     */
    public String getName() {
        return name;
    }

    /**
     * Marks the place as selected.
     */
    public void select() {
        setFill(Color.RED);
    }

    /**
     * Marks the place as not selected.
     */
    public void deselect() {
        setFill(Color.BLUE);
    }

    @Override
    public String toString() {
        return getName();
    }

}
