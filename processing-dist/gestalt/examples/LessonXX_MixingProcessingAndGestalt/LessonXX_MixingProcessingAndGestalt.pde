import processing.opengl.*;

import gestalt.processing.G5;
import gestalt.shape.Sphere;

Sphere mCube;

void setup() {
  size(640, 480, OPENGL);
  smooth();
  G5.setup(this);

  mCube = G5.sphere();
  mCube.scale(100, 100, 100);
  mCube.position(width / 2, height / 2);
  mCube.material().color4f().set(0, 0.5, 1.0);
  mCube.setSegments(8);
}

void draw() {
  background(0, 127, 255);

  fill(255);
  noStroke();
  ellipse(width / 2, height / 2, 201, 201);

  mCube.rotation().x += 0.03;
  mCube.rotation().y += 0.00513;
}

