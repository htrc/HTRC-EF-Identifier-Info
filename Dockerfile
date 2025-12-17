FROM --platform=linux/amd64 eclipse-temurin:11-jre

# Set working directory
WORKDIR /opt/docker

# Copy the staged application
COPY --chown=daemon:daemon target/universal/stage /opt/docker

# Add user daemon to htrc-staff group
RUN usermod -a -G nogroup daemon

# Switch to daemon user (already exists in the base image)
USER daemon

# Expose port
EXPOSE 9000

# Set entrypoint
ENTRYPOINT ["/opt/docker/bin/ef-identifier-info"]
CMD []