uniform sampler2D FirstTexture;
uniform sampler2D SecondTexture;

void main()
{   
    vec4 lightColor = vec4(texture2D(FirstTexture, gl_TexCoord[0].st));
    vec4 SecondColor = vec4(texture2D(SecondTexture, gl_TexCoord[0].st));
    vec4 color = mix(lightColor, SecondColor, 0.5);
    gl_FragColor = color;
}