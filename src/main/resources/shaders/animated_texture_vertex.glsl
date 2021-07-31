#version 330 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in ivec3 joints;
in vec3 weights;

out vec2 pass_textureCoords;
out vec3 pass_position;
out vec3 pass_normal;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform mat4 jointTransforms[50];

const float density = 0.007;
const float gradient = 1.5;

void main() {
    vec4 totalLocalPos = vec4(0.0);
    vec4 totalNormal = vec4(0.0);

    for(int i=0;i<3;i++){
        mat4 jointTransform = jointTransforms[joints[i]];
        vec4 posePosition = jointTransform * vec4(position, 1.0);
        totalLocalPos += posePosition * normalize(weights)[i];

        vec4 worldNormal = jointTransform * vec4(normal, 0.0);
        totalNormal += worldNormal * normalize(weights)[i];
    }

    vec4 worldPos = transformationMatrix * totalLocalPos;
    vec4 camera_position = viewMatrix * worldPos;
    gl_Position = projectionMatrix * camera_position;
    pass_position = worldPos.xyz;
    pass_normal = (transformationMatrix * totalNormal).xyz;
    pass_textureCoords = textureCoords;

    // Calculate visibility for fog
    float distance = length(camera_position.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0, 1);

}

