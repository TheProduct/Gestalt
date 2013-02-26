uniform sampler2D Tex0;

void main (void)
{
	vec4 Fullres = texture2D(Tex0, gl_TexCoord[0].st);

	gl_FragColor = vec4(vec3(Fullres.a), 1.0);
}
