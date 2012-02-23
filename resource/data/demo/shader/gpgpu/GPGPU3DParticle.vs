uniform vec4 lightpos; 
varying vec2 vTexCoord;

void main()
{
   gl_Position = (gl_ModelViewProjectionMatrix * gl_Vertex);
   vTexCoord = gl_MultiTexCoord0.st;
}

