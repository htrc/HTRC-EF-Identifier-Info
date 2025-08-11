FROM --platform=linux/amd64 openjdk:11-jre-slim-bullseye

# Set working directory
WORKDIR /opt/docker

# Copy the staged application
COPY --chown=daemon:daemon target/universal/stage /opt/docker

# Switch to daemon user (already exists in the base image)
USER daemon

# Expose port
EXPOSE 9000

# Set entrypoint
ENTRYPOINT ["/opt/docker/bin/ef-identifier-info"]
CMD []