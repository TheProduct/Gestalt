// super simple vertex shader

uniform float myX;
uniform float myY;

void main(void)
{
   gl_FrontColor = gl_Color;
   gl_Position = ftransform() + vec4(myX, myY, 0, 1);
}
