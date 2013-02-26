uniform float resetposition;

vec3 reset(vec3 myNewPosition, float width, float height) 
{
	float myFrame = 5.0;
	float myWidthMin = width / -2.0 + myFrame;
	float myWidthMax = width / 2.0 - myFrame;
	float myHeightMin = height / -2.0 + myFrame;
	float myHeightMax = height / 2.0 - myFrame;

	if (myNewPosition.x > myWidthMax) {
		myNewPosition.x = resetposition;
	}
	if (myNewPosition.x < myWidthMin) {
		myNewPosition.x = resetposition;
	}
	if (myNewPosition.y > myHeightMax) {
		myNewPosition.y = myHeightMin + 2.0;
	     myNewPosition.x = resetposition;
	}
	if (myNewPosition.y < myHeightMin) {
		myNewPosition.y = myHeightMax - 2.0;
		myNewPosition.x = resetposition;
	}
	return myNewPosition;
}
