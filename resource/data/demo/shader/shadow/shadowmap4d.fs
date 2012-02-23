uniform sampler2DShadow shadowMap;
uniform float epsilon;
uniform float shadowedVal;

float lookup(vec2 p)
{
    float depth = shadow2DProj(shadowMap, gl_TexCoord[1] + vec4(p, 0, 0) * epsilon).x;
    return depth != 1.0 ? shadowedVal : 1.0;
}

 
void main()
{
    // use modulo to vary the sample pattern
    vec2 o = mod(floor(gl_FragCoord.xy), 2.0);

    float sum = 0.0;

    sum += lookup(vec2(-1.5,  1.5) + o);
    sum += lookup(vec2( 0.5,  1.5) + o);
    sum += lookup(vec2(-1.5, -0.5) + o);
    sum += lookup(vec2( 0.5, -0.5) + o);

    gl_FragColor = vec4(sum * 0.25 * gl_Color.rgb, gl_Color.a);
}