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

void main() {
    vec4 world_position = transformationMatrix * vec4(position, 1);
    gl_Position = projectionMatrix * viewMatrix * world_position;
    pass_textureCoords = textureCoords;
    pass_normal = (transformationMatrix * vec4(normal, 0)).xyz;
    pass_position = world_position.xyz;
}

