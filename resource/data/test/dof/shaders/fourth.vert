varying vec2 Tap[4], TapNeg[3];
uniform int Height;

void main(void)
{
	vec2 vertTapOffs[7];
	vec2 TexCoord = gl_MultiTexCoord0.st;

	const float dy = 1.0/float(Height);
	vertTapOffs[0] = vec2(0.0, 0.0);
	vertTapOffs[1] = vec2(0.0, 1.3366 * dy);
	vertTapOffs[2] = vec2(0.0, 3.4295 * dy);
	vertTapOffs[3] = vec2(0.0, 5.4264 * dy);
	vertTapOffs[4] = vec2(0.0, 7.4359 * dy);
	vertTapOffs[5] = vec2(0.0, 9.4436 * dy);
	vertTapOffs[6] = vec2(0.0, 11.4401 * dy);

	Tap[0] = TexCoord;
	Tap[1] = TexCoord + vertTapOffs[1];
	Tap[2] = TexCoord + vertTapOffs[2];
	Tap[3] = TexCoord + vertTapOffs[3];

	TapNeg[0] = TexCoord - vertTapOffs[1];
	TapNeg[1] = TexCoord - vertTapOffs[2];
	TapNeg[2] = TexCoord - vertTapOffs[3];

	gl_Position = ftransform();
	gl_TexCoord[0] = gl_MultiTexCoord0;
}
