#version 330 core

in vec3 colour;

out vec4 pixel_colour;

void main() {
    pixel_colour = vec4(colour, 1);
}