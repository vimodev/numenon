#version 330 core

in vec2 pass_textureCoords;
in vec3 pass_position;
in vec3 pass_normal;

out vec4 pixel_colour;

uniform sampler2D textureSampler;
// Light stuff
uniform int numberOfLights;
uniform vec3 lightPositions[256];
uniform vec3 lightAmbients[256];
uniform vec3 lightDiffuses[256];
uniform vec3 lightAttenuations[256];
// Material stuff
uniform vec3 materialAmbient;
uniform vec3 materialDiffuse;
// Camera's world position
uniform vec3 cameraPosition;

vec4 lighting(int light_index) {
    // Initialize to applying no light
    vec4 colour_to_apply = vec4(0, 0, 0, 1);
    // Add ambient light
    colour_to_apply += vec4(materialAmbient * lightAmbients[light_index], 0);
    // Diffuse
    vec3 direction_to_light = normalize(lightPositions[light_index] - pass_position);
    vec3 normalized_normal = normalize(pass_normal);
    float diffuse_brightness = max(dot(normalized_normal, direction_to_light), 0.0);
    colour_to_apply += diffuse_brightness * vec4(materialDiffuse * lightDiffuses[light_index], 0);
    return colour_to_apply;
}

float attenuation(int light_index) {
    float distance = length(lightPositions[light_index] - pass_position);
    vec3 attenuation = lightAttenuations[light_index];
    return max(exp(-pow(distance * attenuation.x - attenuation.y, attenuation.z)), 1);
}

void main() {
    // Initialize pixel colour to black
    pixel_colour = vec4(0, 0, 0, 1);
    // Apply lighting for all lights
    for (int i = 0; i < numberOfLights; i++) {
        pixel_colour = pixel_colour + attenuation(i) * lighting(i);
    }
    // Apply texture
    pixel_colour = pixel_colour * texture(textureSampler, mod(pass_position.xz / 25, 1));
}