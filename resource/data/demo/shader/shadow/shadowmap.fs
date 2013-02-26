uniform sampler2DShadow shadowMap;

void main()
{
    float depth = shadow2DProj(shadowMap, gl_TexCoord[1]).r;
    float shadeFactor = depth != 1.0 ? 0.5 : 1.0;
    gl_FragColor = vec4(shadeFactor * gl_Color.rgb, gl_Color.a);
}