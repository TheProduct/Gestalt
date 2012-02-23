//#extension GL_ARB_draw_buffers : enable
#extension GL_ARB_texture_rectangle : enable 

uniform sampler2DRect texturePosition; 
uniform sampler2DRect textureVelocity; 
uniform sampler2DRect textureHeightfield; 
uniform float deltatime;
uniform float width;
uniform float height;
uniform float speed;
uniform vec3 flowdirection;
uniform float resetposition;

const float FLOWFIELD_SCALE = 20000.0;
const int POSITION_MODE = 0;
const int VELOCITY_MODE = 1;
const float DRAG = 0.95;

varying vec2 vTexCoord;

vec3 reset(vec3 myNewPosition, float width, float height);

void main()
{
		vec3 myPosition = vec3(texture2DRect(texturePosition, vTexCoord).xyz);
		vec3 myVelocity = vec3(texture2DRect(textureVelocity, vTexCoord).xyz);
		float myMass = texture2DRect(textureVelocity, vTexCoord).w;

		// get acceleration from 2D heightfield
		vec2 myScaledPosition = vec2(myPosition.xy);
		myScaledPosition.x += width * 0.5;
		myScaledPosition.y += height * 0.5;

		float myCenter = texture2DRect(textureHeightfield, vec2(myScaledPosition.x + 0.0, myScaledPosition.y + 0.0)).r;
		float myUp     = texture2DRect(textureHeightfield, vec2(myScaledPosition.x + 0.0, myScaledPosition.y + 1.0)).r;
		float myLeft   = texture2DRect(textureHeightfield, vec2(myScaledPosition.x - 1.0, myScaledPosition.y + 0.0)).r;
		float myDown   = texture2DRect(textureHeightfield, vec2(myScaledPosition.x + 0.0, myScaledPosition.y - 1.0)).r;
		float myRight  = texture2DRect(textureHeightfield, vec2(myScaledPosition.x + 1.0, myScaledPosition.y + 0.0)).r;

		vec2 myFlowVector = 
			vec2(0.0, myCenter - myUp) * -1.0 + 
			vec2(myLeft - myCenter, 0.0) * -1.0 + 
			vec2(0.0, myDown - myCenter) * -1.0 + 
			vec2(myCenter - myRight, 0.0) * -1.0;
		myFlowVector *= 0.25;
		myFlowVector *= FLOWFIELD_SCALE;

		float myFlowV = texture2DRect(textureHeightfield, vTexCoord).g * 150.0 + 50.0;
		vec3 myFlow = vec3(myFlowV * flowdirection.x,myFlowV * flowdirection.y, 0.0);
		myFlow *= speed;
		
		vec3 myRandom = vec3(sin(myMass * 2.0 * 3.141) - 1.0, cos(myMass * 2.0 * 3.141), 0.0);
		myRandom *= 10.0;

		vec3 myAcceleration = vec3(myFlowVector.x, myFlowVector.y, 0.0) + myFlow + myRandom;
		myAcceleration *= deltatime / myMass;

		// integrate acceleration
		vec3 myNewVelocity = myVelocity * DRAG + myAcceleration;

		// integrate velocity 
		vec3 myNewPosition = myPosition + myNewVelocity * deltatime;

		// teleport
		const float myReset = 2.0;

		// teleport
		myNewPosition = reset(myNewPosition, width, height);

		// store data in fragment
		gl_FragData[VELOCITY_MODE].x = myNewVelocity.x;
		gl_FragData[VELOCITY_MODE].y = myNewVelocity.y;
		gl_FragData[VELOCITY_MODE].z = 0.0;//myNewVelocity.z;
		gl_FragData[VELOCITY_MODE].w = myMass;

		gl_FragData[POSITION_MODE].x = myNewPosition.x;
		gl_FragData[POSITION_MODE].y = myNewPosition.y;
		gl_FragData[POSITION_MODE].z = 0.0;//myNewPosition.z;
		gl_FragData[POSITION_MODE].w = 1.0;
}


