#extension GL_ARB_texture_rectangle : enable 

uniform sampler2DRect DATA_IN_0;
uniform sampler2DRect DATA_IN_1;
uniform float deltatime;

varying vec2 vTexCoord;

const int DATA_OUT_0 = 0;
const int DATA_OUT_1 = 1;

/* verlet integration step */
void integrate(inout vec3 x, vec3 oldx, vec3 a, float timestep, float damping) {    x = x + damping*(x - oldx) + a * timestep*timestep;}

void main() {

	/* read data */
	vec3 data_in_0 = texture2DRect(DATA_IN_0, vTexCoord).xyz;
	vec3 data_in_1 = texture2DRect(DATA_IN_1, vTexCoord).xyz;

	/* forces */
	vec3 gravity = vec3(0.0, -1.0, 0.0);

	/* integrate */
	vec3 p = vec3(data_in_0);
	vec3 oldP = vec3(data_in_1);
	const float damping = 0.99;
	integrate(p, oldP, gravity, deltatime, damping);

	/* constraint */
	vec3 contrainedP = vec3(p);

	/* write simulation data */
	gl_FragData[DATA_OUT_0] = vec4(contrainedP.xyz, 1.0);
	gl_FragData[DATA_OUT_1] = vec4(data_in_0.xyz, 1.0);
}
