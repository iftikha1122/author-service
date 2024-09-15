#!/bin/bash

# Step 1: Pre-pull Docker image for Ryuk
echo "Pre-pulling the Ryuk Docker image..."
docker pull testcontainers/ryuk:0.7.0

# Step 2: Set environment variables to disable Ryuk and use a different registry if needed
export TESTCONTAINERS_RYUK_DISABLED=true
export DOCKER_REGISTRY="https://your-custom-registry.com"

# Step 3: Run Gradle tests
echo "Running Gradle tests..."
./gradlew clean test

# Step 4: Capture exit code and output result
if [ $? -eq 0 ]; then
    echo "Tests ran successfully!"
else
    echo "Tests failed!"
    exit 1
fi
