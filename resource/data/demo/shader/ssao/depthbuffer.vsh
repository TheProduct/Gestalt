uniform float far;
varying vec4 color;

void main(void) {
	gl_Position = ftransform();
	gl_FrontColor = gl_Color;
	float myDepthRatio = gl_Position.z / far;
	color = vec4(myDepthRatio, myDepthRatio, myDepthRatio,1.0);
}