uniform sampler2D Tex0, Tex1;

void main (void)
{
	vec4 Fullres = texture2D(Tex0, gl_TexCoord[0].st);
	vec4 Blurred = texture2D(Tex1, gl_TexCoord[0].st);

	// HLSL linear interpolation function
	gl_FragColor = Fullres + Fullres.a * (Blurred - Fullres);
}
