FROM debian:bullseye as build

# Установка зависимостей для сборки
RUN apt-get update && apt-get install -y \
    gcc build-essential libpcre3-dev zlib1g-dev libssl-dev git \
    wget ca-certificates libjansson-dev libjwt-dev

# Скачать nginx
ENV NGINX_VERSION 1.28.0
RUN wget http://nginx.org/download/nginx-${NGINX_VERSION}.tar.gz && \
    tar zxvf nginx-${NGINX_VERSION}.tar.gz

# Скачать модуль
RUN git clone https://github.com/tarachliuk/nginx-module-auth-jwt.git

# Собрать nginx с модулем
RUN cd nginx-${NGINX_VERSION} && \
    ./configure --prefix=/opt/nginx --add-module=../nginx-module-auth-jwt \
        --with-http_ssl_module --with-http_realip_module \
        --with-http_stub_status_module --with-stream \
        --with-compat --with-file-aio && \
    make && make install

# --- Runtime слой ---
FROM debian:bullseye

RUN apt-get update && apt-get install -y libpcre3 zlib1g libssl1.1 libjansson4 libjwt1 && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /opt/nginx /opt/nginx

ENV PATH="/opt/nginx/sbin:${PATH}"

# Окружение
ENV JWT_SECRET=changeme
ENV JWT_DOMAIN=changeme

# Настраиваем передачу переменных окружения в nginx
ENV NGINX_ENV="JWT_SECRET JWT_DOMAIN"

EXPOSE 8080

CMD [ "nginx", "-g", "daemon off;" ]