#extension GL_ARB_texture_rectangle : enable 

varying vec2 vTexCoord;

void main() {
	vTexCoord = gl_MultiTexCoord0.st;
	gl_Position = ftransform();
}
