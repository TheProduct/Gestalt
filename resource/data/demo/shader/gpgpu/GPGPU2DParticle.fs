#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect texturePosition; 
uniform sampler2DRect textureHeightfield; 
uniform float deltatime;
uniform float width;
uniform float height;

const float clampVelocity = 100.0;
const float STRENGTH = 500.0;

varying vec2 vTexCoord;void main(){
	// get old position	vec2 myPosition = vec2(texture2DRect(texturePosition, vTexCoord).rg);	vec2 myVelocity = vec2(texture2DRect(texturePosition, vTexCoord).ba);

	// get acceleration from heightfield
	vec2 myScaledPosition = vec2(myPosition);
    	myScaledPosition.x += width * 0.5;
    	myScaledPosition.y += height * 0.5;

	vec2 myAcceleration = vec2(texture2DRect(textureHeightfield, myScaledPosition).rg);
	myAcceleration -= 0.5;
	myAcceleration *= STRENGTH;
	myAcceleration *= deltatime;

	// integrate acceleration
	vec2 myNewVelocity = myVelocity + myAcceleration;

 	// clamp velocity
	myNewVelocity.x = min(myNewVelocity.x, clampVelocity);
	myNewVelocity.x = max(myNewVelocity.x, -clampVelocity);
	myNewVelocity.y = min(myNewVelocity.y, clampVelocity);
	myNewVelocity.y = max(myNewVelocity.y, -clampVelocity);

	// integrate velocity 
	vec2 myNewPosition = myPosition + myVelocity * deltatime;

	// teleport
	if (myNewPosition.x > width / 2.0) {
	   myNewPosition.x = width / -2.0;
	}
	if (myNewPosition.x < width / -2.0) {
	   myNewPosition.x = width / 2.0;
	}
	if (myNewPosition.y > height / 2.0) {
	   myNewPosition.y = height / -2.0;
	}
	if (myNewPosition.y < height / -2.0) {
	   myNewPosition.y = height / 2.0;
	}

	// store data in fragment
	gl_FragColor.r = myNewPosition.x;
	gl_FragColor.g = myNewPosition.y;
	gl_FragColor.b = myNewVelocity.x;
	gl_FragColor.a = myNewVelocity.y;
}

