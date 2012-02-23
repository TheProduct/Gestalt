uniform sampler2D textureunit;
uniform float threshold;

void main(void)
{
        vec4 color = texture2D(textureunit, gl_TexCoord[0].st);

	float average = (color.r + color.g + color.b ) * 0.33;
	float booleanresult = 0.0;
	if (average > threshold) {
		booleanresult = 1.0;
	}

	gl_FragColor = vec4(booleanresult, booleanresult, booleanresult, color.a);
}
