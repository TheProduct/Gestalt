uniform float focalDistance, focalRange;
varying float Blur;

void main(void)
{
	vec4 PosWV = gl_ModelViewMatrix * gl_Vertex;

	Blur = clamp(abs(-PosWV.z - focalDistance) / focalRange, 0.0, 1.0);

	gl_Position = ftransform();
	gl_TexCoord[0] = gl_MultiTexCoord0;
}
