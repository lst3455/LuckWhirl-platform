
# Build a Docker image that is compatible with the system architecture (e.g., amd64/arm64)
docker build -t linsitian/big-market-back-app:3.0 -f ./Dockerfile .

# Uncomment the following line to build and push a multi-platform image (compatible with both amd64 and arm64)
# docker buildx build --load --platform linux/amd64,linux/arm64 -t your-dockerhub-username/big-market-back-app:1.1 -f ./Dockerfile . --push
