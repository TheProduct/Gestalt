varying vec3 normal;
uniform vec4 thresholds;

void main()
{
	float intensity;
	vec4 color;
	vec3 n = normalize(normal);

	intensity = dot(vec3(gl_LightSource[0].position),n);

	if (intensity > thresholds[3])
		color = vec4(thresholds[3], thresholds[3], thresholds[3], 1.0);
	else if (intensity > thresholds[2])
		color = vec4(thresholds[2], thresholds[2], thresholds[2], 1.0);
	else if (intensity > thresholds[1])
		color = vec4(thresholds[1], thresholds[1], thresholds[1], 1.0);
	else
		color = vec4(thresholds[0], thresholds[0], thresholds[0], 1.0);

	gl_FragColor = color;
}
