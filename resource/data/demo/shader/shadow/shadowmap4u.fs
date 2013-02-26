uniform sampler2DShadow shadowMap;
uniform float epsilon;
uniform float shadowedVal;

float lookup(float x, float y)
{
    float depth = shadow2DProj(shadowMap, gl_TexCoord[1] + vec4(x, y, 0, 0) * epsilon).x;
    return depth != 1.0 ? shadowedVal : 1.0;
}

 
void main()
{
    float sum = 0.0;
    sum += lookup(-0.5, -0.5);
    sum += lookup( 0.5, -0.5);
    sum += lookup(-0.5,  0.5);
    sum += lookup( 0.5,  0.5);

    gl_FragColor = vec4(sum * 0.25 * gl_Color.rgb, gl_Color.a);
}