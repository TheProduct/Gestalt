varying vec3 normal;

void main()
{
	gl_TexCoord[0] = gl_MultiTexCoord0;
  normal = gl_NormalMatrix * gl_Normal;
	gl_Position = ftransform();
}
