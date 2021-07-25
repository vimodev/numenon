#version 330 core

in vec2 pass_textureCoords;
in vec3 pass_position;
in vec3 pass_normal;

out vec4 pixel_colour;

uniform sampler2D textureSampler;
uniform int numberOfLights;
uniform vec3 lightPositions[256];
uniform vec3 lightAmbients[256];
uniform vec3 lightDiffuses[256];
uniform vec3 lightSpeculars[256];

vec4 lighting(int light_index) {
    // Initialize to applying no light
    vec4 colour_to_apply = vec4(0, 0, 0, 1);
    // Add ambient light
    colour_to_apply += vec4(lightAmbients[light_index], 0);
    // Diffuse
    vec3 direction_to_light = normalize(lightPositions[light_index] - pass_position);
    vec3 normalized_normal = normalize(pass_normal);
    float diffuse_brightness = max(dot(normalized_normal, direction_to_light), 0.0);
    colour_to_apply += diffuse_brightness * vec4(lightDiffuses[light_index], 0);
    // Specular

    return colour_to_apply;
}

void main() {
    // Initialize pixel colour to black
    pixel_colour = vec4(0, 0, 0, 1);
    // Apply lighting for all lights
    for (int i = 0; i < numberOfLights; i++) {
        pixel_colour = pixel_colour + lighting(i);
    }
    // Apply texture
    pixel_colour = pixel_colour * texture(textureSampler, pass_textureCoords);
}