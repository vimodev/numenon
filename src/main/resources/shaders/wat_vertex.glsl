#version 330 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 pass_position;
out vec3 pass_normal;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform float time;

void main() {
    vec3 modifier = vec3(0, sin(position.x + time) + cos(position.z + time), 0) * 0.1;
    vec4 world_position = transformationMatrix * vec4(position + modifier, 1);
    gl_Position = projectionMatrix * viewMatrix * world_position;
    pass_textureCoords = textureCoords;
    pass_normal = (transformationMatrix * vec4(normal, 0)).xyz;
    pass_position = world_position.xyz;
}

