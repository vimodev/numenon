#version 330 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 pass_position;
out vec3 pass_normal;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform float time;

const float density = 0.007;
const float gradient = 1.5;

void main() {
    vec3 modifier = vec3(0, sin(position.x + time) + cos(position.z + time), 0) * 0.1;
    vec4 world_position = transformationMatrix * vec4(position + modifier, 1);
    vec4 camera_position = viewMatrix * world_position;
    gl_Position = projectionMatrix * camera_position;
    pass_textureCoords = textureCoords;
    pass_normal = (transformationMatrix * vec4(normal, 0)).xyz;
    pass_position = world_position.xyz;

    // Calculate visibility for fog
    float distance = length(camera_position.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0, 1);
}

