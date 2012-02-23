import processing.opengl.*;

import gestalt.processing.G5;
import gestalt.shape.Cuboid;

Cuboid mCube;

void setup() {
  size(640, 480, OPENGL);
  G5.setup(this);

  mCube = G5.cuboid();
  mCube.scale(100, 100, 100);
  mCube.position(width / 2, height / 2);
}

void draw() {
  background(0, 127, 255);
  mCube.rotation().x += 0.01;
  mCube.rotation().y += 0.003;
}

