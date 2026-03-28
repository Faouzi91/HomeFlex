# Build Stage — full Node for compilation
FROM node:20-alpine AS build
WORKDIR /app

# Copy dependency manifests first (layer caching — dependencies rarely change)
COPY package.json package-lock.json ./
RUN npm ci

# Copy source and build
COPY . .
RUN npm run build:prod

# Serve Stage — Nginx reverse proxy
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx config and static files
RUN rm -rf /etc/nginx/conf.d/default.conf ./*

# Copy custom nginx config and built Angular app
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/rental-app-frontend .

# Non-root user for security (SRS SEC-10)
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    touch /var/run/nginx.pid && chown nginx:nginx /var/run/nginx.pid

USER nginx

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -q -O - http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
