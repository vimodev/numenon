#version 330 core

in vec2 pass_textureCoords;
in vec3 pass_position;
in vec3 pass_normal;
in float visibility;

out vec4 pixel_colour;

// Blendmap texture stuff
uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

// World stuff
uniform float waterLevel;
uniform vec3 skyColour;

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
    vec2 tiledTextureCoords = mod(pass_position.xz / 5, 1);
    // Sand near water
    float randomOffset = ((sin(pass_position.x / 1.25f) + cos(pass_position.z)) + 2) / 5 - 0.5f;
    float sandness = min(1, max(0, waterLevel + 1 - (pass_position.y + randomOffset)));
    vec4 finalTextureColour = sandness * texture(gTexture, tiledTextureCoords) + (1 - sandness) * texture(backgroundTexture, tiledTextureCoords);
    // Steep slopes are rocky
    float rockiness = abs(dot(normalize(pass_normal), vec3(0, 1, 0)));
    rockiness = min(1 - rockiness + 0.45f, 1);
    rockiness = pow(rockiness, 15);
    finalTextureColour = rockiness * texture(bTexture, tiledTextureCoords) + (1 - rockiness) * finalTextureColour;

    pixel_colour = pixel_colour * finalTextureColour;
    pixel_colour = pixel_colour * visibility + vec4(skyColour, 1.0) * (1 - visibility);
}