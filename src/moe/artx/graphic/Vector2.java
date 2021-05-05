package moe.artx.graphic;

/** 2D vector with double values */
public class Vector2
{
    public double x = 0.0, y = 0.0;

    public Vector2() {}
    public Vector2(Vector2 other) { this(other.x, other.y); }
    public Vector2(double x, double y) { this.x = x; this.y = y; }

    public String toString() {
        return String.format("(%s, %s)", x, y);
    }

    /**
     * this -= other .
     * @return this
     */
    public Vector2 sub(Vector2 other)
    {
        x -= other.x; y -= other.y;
        return this;
    }

    /**
     * this *= value .
     * @return this
     */
    public Vector2 mul(double value)
    {
        x *= value; y *= value;
        return this;
    }

    /** length (magnitude) of the vector. */
    public double len() { return Math.sqrt(x * x + y * y); }

    /** dot product between two vectors, correlates with the angle */
    public double dot(Vector2 other) { return x * other.x + y * other.y; }
}
