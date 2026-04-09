FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
COPY angular.json tsconfig*.json ./
COPY .postcssrc.json ./
COPY public ./public
COPY src ./src

RUN npm ci
RUN npm run build

FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/homeflex-web/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
