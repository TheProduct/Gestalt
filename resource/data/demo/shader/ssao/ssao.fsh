uniform sampler2D depthUnit;
uniform sampler2D randomMapUnit;
uniform float sampleRadius;
uniform float distanceScale;
uniform float maxSampleDelta;
const int NUMBER_OF_SAMPLES = 16;
uniform float random[NUMBER_OF_SAMPLES];
varying mat4 projectionMatrix;

void main(void)
{	

	vec4 randomMapColor = texture2D(randomMapUnit, gl_Color.xy);
	randomMapColor -= vec4(0.5);
	float pixelDepth = texture2D(depthUnit, gl_Color.xy).r;
	float distance = gl_TexCoord[0].z;//length(gl_TexCoord[0].xyz);
	vec3 pixelPosEyeSpace = vec3(pixelDepth * gl_TexCoord[0].xyz / distance);
	
	float result = 0.0;
	float maxDelta = 0.8;

	for (int i = 0; i < NUMBER_OF_SAMPLES * 3; i+=3) {
		vec3 myRandomVector = vec3(random[i],random[i+1], random[i+2]);
		myRandomVector *= sampleRadius;
		myRandomVector = reflect(myRandomVector, randomMapColor.xyz);
		
		vec4 samplePointEyeSpace = vec4(pixelPosEyeSpace + myRandomVector, 1.0);
		vec2 ss = samplePointEyeSpace.xy / samplePointEyeSpace.z;
		vec2 sn = ss + vec2(.5);
		sn.y = 1.0 - sn.y;
		
		float sampleDepth = texture2D(depthUnit, sn).r;
		float ZD = distanceScale * max(pixelDepth - sampleDepth, 0.0);       	float NO_OCC = 1.0;        	result += (ZD < distanceScale * maxSampleDelta ?(1.0 / (1.0 + ZD)) : NO_OCC);
	}
	result /= float(NUMBER_OF_SAMPLES);

	//gl_FragColor = vec4(pixelDepth* result, pixelDepth* result, pixelDepth* result,1.0);
	//gl_FragColor = vec4(pixelDepth, pixelDepth, pixelDepth,1.0);
	gl_FragColor = vec4(result, result, result, 1.0);
	//gl_FragColor = vec4(randomMapColor);
	//gl_FragColor = vec4(pixelPosEyeSpace,1.0);
}
	