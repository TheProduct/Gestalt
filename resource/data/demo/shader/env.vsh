//
// Vertex shader for cube map environment mapping
//
// Author: Randi Rost
//
// Copyright (c) 2003-2006: 3Dlabs, Inc.
//
// See 3Dlabs-License.txt for license information
//

varying vec3  ReflectDir;
varying float LightIntensity;

uniform vec3  LightPos;

void main() 
{
    gl_Position    = ftransform();
    vec3 normal    = normalize(gl_NormalMatrix * gl_Normal);
    vec4 pos       = gl_ModelViewMatrix * gl_Vertex;
    vec3 eyeDir    = pos.xyz;
    ReflectDir     = reflect(eyeDir, normal);
    LightIntensity = max(dot(normalize(LightPos - eyeDir), normal),0.0);
}