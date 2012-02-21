package mathematik;


import processing.core.PApplet;


/**
 * rotate point p around the linesegment defined by p1 - p2
 */

public class TestRotatePoint
    extends PApplet {

    private static final long serialVersionUID = 1L;

    Vector3f p = new Vector3f();

    Vector3f p1 = new Vector3f();

    Vector3f p2 = new Vector3f();

    float theta;

    public void setup() {
        size(640, 480, P3D);
        p.set(300, 200, 0);
        p1.set(320, 150, 0);
        p2.set(320, 250, 0);
    }


    public void draw() {

        p1.x = mouseX;
        p1.y = mouseY;

        theta += 1 / 30f;
        Vector3f myRotatedPointA = Util.rotatePoint(p, theta, p1, p2);
        Vector3f myRotatedPointB = Util.rotatePoint(p, theta + PI, p1, p2);

        background(255);
        stroke(0);
        line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
        stroke(255, 0, 0);
        line(myRotatedPointB.x, myRotatedPointB.y, myRotatedPointB.z,
             p.x, p.y, p.z);
        stroke(0, 255, 0);
        line(p.x, p.y, p.z,
             myRotatedPointA.x, myRotatedPointA.y, myRotatedPointA.z);
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestRotatePoint.class.getName()});
    }
}
