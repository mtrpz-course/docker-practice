FROM golang:latest
MAINTAINER Michael Chirozidi chirozidi.m@gmail.com
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN go build -o build/fizzbuzz
EXPOSE 8080
CMD ["./build/fizzbuzz", "serve"]