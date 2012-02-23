uniform vec3 SkyColor;
uniform vec3 GroundColor;
uniform vec3 LightPosition;
uniform float LightRatio;

varying vec3  ReflectDir;
varying vec3  DiffuseColor;

void main()
{
    gl_Position     = ftransform();
    
    // reflection
    vec3 normal    = normalize(gl_NormalMatrix * gl_Normal);
    vec4 pos       = gl_ModelViewMatrix * gl_Vertex;
    vec3 eyeDir    = pos.xyz;
    ReflectDir     = reflect(eyeDir, normal);

    // texture
    vec4 myTexCoord = gl_MultiTexCoord0;
    myTexCoord.t = gl_MultiTexCoord0.t * -1.0;
    gl_TexCoord[0] = myTexCoord;

    // light
    vec3 ecPosition = vec3(gl_ModelViewMatrix * gl_Vertex);
    vec3 tnorm      = normalize(gl_NormalMatrix * gl_Normal);
    vec3 lightVec   = normalize(LightPosition - ecPosition);
    float costheta  = dot(tnorm, lightVec);
    float a         = LightRatio + (1.0 - LightRatio) * costheta;
    DiffuseColor    = mix(GroundColor, SkyColor, a);
}