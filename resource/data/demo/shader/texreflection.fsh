uniform sampler2D FirstTexture;
uniform samplerCube ReflectionTexture;
uniform float ReflectionMixIntensity;

varying vec3  ReflectDir;
varying vec3  DiffuseColor;

void main()
{   
    vec3 lightColor = vec3(texture2D(FirstTexture, gl_TexCoord[0].st));
    vec3 finalColor = vec3(lightColor * DiffuseColor);

    vec3 SecondColor = vec3(textureCube(ReflectionTexture, ReflectDir));
    vec3 color = mix(finalColor, SecondColor, ReflectionMixIntensity);

    gl_FragColor    = vec4(color,1.0);
}