#version 330 core

in vec2 pass_textureCoords;

out vec4 pixel_colour;

uniform sampler2D textureSample;

void main() {
    pixel_colour = texture(textureSample, pass_textureCoords);
}