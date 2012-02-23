void main()
{
    gl_FrontColor = gl_Color;
    gl_Position     = ftransform();
    vec4 myTexCoord = gl_MultiTexCoord0;
    myTexCoord.t = gl_MultiTexCoord0.t * -1.0;
    gl_TexCoord[0] = myTexCoord;
}