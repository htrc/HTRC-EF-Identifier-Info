#!/bin/bash

# Default tag if not provided
IMAGE_TAG=${1:-latest}
IMAGE_NAME="docker.htrc.illinois.edu/services/ef-identifier-info"

echo "Building Docker image with tag: $IMAGE_TAG"

# Stage the application
sbt stage

# Build the Docker image
docker buildx build --platform linux/amd64 \
  -t ${IMAGE_NAME}:${IMAGE_TAG} .

echo "Built image: ${IMAGE_NAME}:${IMAGE_TAG}"

#Push image
docker push ${IMAGE_NAME}:${IMAGE_TAG}

echo "Pushed image: ${IMAGE_NAME}:${IMAGE_TAG}"
