uniform sampler2D textureunit;
uniform float offset[16];
uniform float weight[16];
uniform float spread;

void main(void)
{
	vec4 color = vec4(0.0);
	vec2 myOffset;
	for ( int i = 0; i < 16; i++ ) {
	   /* x */
	   myOffset = vec2(offset[i] * spread, 0);
	   color += weight[i] * texture2D(textureunit, gl_TexCoord[0].st + myOffset);
	   /* y */
	   myOffset = vec2(0, offset[i] * spread);
	   color += weight[i] * texture2D(textureunit, gl_TexCoord[0].st + myOffset);
	}
	
	//color = vec4(1.0,0,0,1);
	//color = vec4(texture2D(textureunit, gl_TexCoord[0].st));
	gl_FragColor = color * 0.5;
}
