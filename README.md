# docker-practice

## Navigation:
1) [Python](#python)
2) [Go](#golang)
3) [Kotlin](#kotlin)
4) [Conclusion](#conclusion)

### PYTHON
------------

1) Unoptimized build

```Dockerfile
 FROM ubuntu:latest
 MAINTAINER Michael Chirozidi 'chirozidi.m@gmail.com'
 RUN apt-get update -qy
 RUN apt-get install -qy python3.10 python3-pip python3.10-dev
 COPY . /app
 WORKDIR /app
 RUN pip install pipreqs
 RUN pipreqs &&  echo 'uvicorn[standard]==0.21.1' >> requirements.txt
 RUN pip install -r requirements.txt
 CMD ["uvicorn", "spaceship.main:app", "--host=0.0.0.0","--port=8080"] 
```

* Build: $ docker build . -t py-fastapi:1.0
* Run: $ docker run -p 8080:8080 --rm py-fastapi:1.0
* Build time: 29.2s
* Build size: 484MB
* Description: Regular unoptimized build.
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/fd71f0a985e521de1bafd3aeebe8e3a55b8e5362)
-------------
2) Changing of `app.py`

* Build: $ docker build . -t py-fastapi:2.0
* Run: $ docker run -p 8080:8080 --rm py-fastapi:2.0
* Build time: 8s
* Build size: 484MB (same)
* Description: Faster build time because the base image `ubuntu:latest` was cached.
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/78d7e9ca7868aad39d553cec22905c9d67f5448c)
------------
3) Dockerfile optimization

```Dockerfile
 FROM python:slim-bullseye
 MAINTAINER Michael Chirozidi chirozidi.m@gmail.com
 COPY requirements/backend.in .
 RUN pip install --no-cache-dir -r backend.in
 COPY . .
 CMD ["uvicorn", "spaceship.main:app", "--host=0.0.0.0", "--port=8080"] 
```

* Build: $ docker build . -t py-fastapi:3.0
* Run: $ docker run -p 8080:8080 --rm py-fastapi:3.0
* Build time: 8.7s
* Build size: 168MB
* Description: First, the python dependency file is copied separately from other directory contents, dependencies are installed. Docker caches the layer with dependencies separately, so if the dependencies don't change, it will be used from the cache. Second, the basic image is more compact and do have neccessary packets only.
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/4d4c67f0aed3cb083e92adb94083990bf577c6cb)
----------------------------
4) Upgrading of `app.py` 

* Build: $ docker build . -t py-fastapi:4.0
* Run: $ docker run -p 8080:8080 --rm py-fastapi:4.0
* Build time: 1.4s
* Build size: 168MB (same)
* Description: Faster build time because the base image `python:slim-bullseye` was cached.
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/4d4c67f0aed3cb083e92adb94083990bf577c6cb)
------------------
5) NumPy
```bash
 NumPy==1.24.3
 fastapi==0.95.0
 pydantic==1.10.7
 starlette==0.26.1
 uvicorn[standard]==0.21.1
 pyproject.toml==0.0.10 
```
  * **With Bullseye**

* Build: $ docker build . -t py-fastapi:5.0
* Run: $ docker run -p 8080:8080 --rm py-fastapi:5.0
* Build time: 1.4s
* Build size: 168MB (same)
* Description: More slowly build because new library was added
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/cfef91b2a31b82327f888ba1077ad43399a272c8)


  * **With Alpine**
 ```Dockerfile
 FROM python:alpine
 MAINTAINER Michael Chirozidi chirozidi.m@gmail.com
 RUN apk add --no-cache musl-dev g++ gcc lapack-dev
 WORKDIR /app
 COPY requirements/backend.in .
 RUN pip install --no-cache-dir -r backend.in
 COPY . .
 CMD ["uvicorn", "spaceship.main:app", "--host=0.0.0.0", "--port=8080"]
 ```

* Build: $ docker build . -t py-fastapi:5.0
* Run: $ docker run -p 8080:8080 --rm py-fastapi:5.0
* Build time: 125.5s
* Build size: 415MB
* Description: At the build based on alpine, not all dependencies were installed for successful numpy build (I wanted to die during this struggle), so I added an instruction in the Dockerfile that installs the necessary dependencies (g++, gcc, musl-dev, ...). The library download resulted in long connection time and a large image size.
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/f80afcbdf8ef22f4895c29596abb66a6c824efc8)

----------
### GOLANG 

1) Default build
```Dockerfile
 FROM golang:latest
 MAINTAINER Michael Chirozidi chirozidi.m@gmail.com
 WORKDIR /app
 COPY go.mod go.sum ./
 RUN go mod download
 COPY . .
 RUN go build -o build/fizzbuzz
 EXPOSE 8080
 CMD ["./build/fizzbuzz", "serve"] 
```
* Build: $ docker build . -t golang:1.0
* Run: $ docker run -p 8080:8080 --rm golang:1.0
* Build time: 137.7s
* Build size: 837.42MB
* Description: Minimal Dockerfile
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/ffc87006f562788cd691731f932d317fd9653af4)
---------
2) Multi-stage building with Scratch

```Dockerfile
 FROM golang:latest AS builder
 MAINTAINER Michael Chirozidi chirozidi.m@gmail.com
 WORKDIR /app
 COPY go.mod go.sum ./
 RUN go mod download
 COPY . .
 RUN CGO_ENABLED=0 go build -ldflags "-w -s -extldflags '-static'" -o build/fizzbuzz
 
 FROM scratch
 COPY --from=builder /app/build/fizzbuzz /
 COPY --from=builder /app/templates/index.html /templates/
 EXPOSE 8080
 CMD ["./build/fizzbuzz", "serve"] 
```
* Build: $ docker build . -t golang:2.0
* Run: $ docker run -p 8080:8080 --rm golang:2.0
* Build time: 9.5s
* Build size: 6.55MB
* Description: First, an intermediate image is created with the Go dependencies, and the application code is compiled into a binary file. Then, a final image is created, and the HTML page from the intermediate image and the executable file are copied into it.
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/ffc87006f562788cd691731f932d317fd9653af4)

---------
3) Multi-stage building with Distorless

```Dockerfile
 FROM golang:latest AS builder
 MAINTAINER Michael Chirozidi chirozidi.m@gmail.com
 WORKDIR /app
 COPY go.mod go.sum ./
 RUN go mod download
 COPY . .
 RUN go build -o build/fizzbuzz
 FROM gcr.io/distroless/base
 COPY --from=builder /app/build/fizzbuzz /
 COPY --from=builder /app/templates/index.html /templates/
 EXPOSE 8080
 CMD ["./build/fizzbuzz", "serve"] 
```
* Build: $ docker build . -t golang:2.0
* Run: $ docker run -p 8080:8080 --rm golang:2.0
* Build time: 1.8s
* Build size: 27MB
* Description: In this case, we compile the executable file using the "go build" command, which uses dynamic libraries by default, unlike the previous image where it was necessary to compile a static binary file.
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/e1c93204a4ca91fe7bca78f35d76ea23bec59f82)

If we compare using a Scratch image and a Distroless image as the base, Scratch allows us to create a highly optimized and lightweight image that contains only the necessary components and dependencies. However, it requires manually installing all the dependencies and libraries inside the image, which takes time and expertise, and may not be the best choice during development.

Distroless images are designed to be used with specific programming languages and frameworks such as Java, Python, or Node.js, and already include dependencies and libraries for those languages and frameworks. Therefore, if you don't want to deal with manually installing dependencies and libraries and prefer to use a ready-made image that already contains all the necessary components to run your program, a Distroless image can be the best choice.

It should be noted that using a multi-stage build, regardless of the choice of the base image, is more optimal compared to using a golang-based image for the reasons mentioned earlier.

------------
### KOTLIN
1) Code of simple server on Ktor framework for Kotlin language

* Application.kt:
```Kotlin
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
```
* Serialization.kt:
```Kotlin
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
    helloWorldJson()
}
```
* Routing.kt: 
```Kotlin
fun Application.configureRouting() {
    helloWorldRouting()
}
```
* HelloWorldRemote.kt:
```Kotlin
fun Application.helloWorldRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!", ContentType.Text.Plain, HttpStatusCode.OK)
        }
    }
}

fun Application.helloWorldJson(){
    routing {
        get("/json/hello") {
            call.respond(HttpStatusCode.OK, mapOf("hello" to "world"))
        }
    }
}
```
2) Dockerfile for mutli-stage building image
``` Dockerfile
FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /server
COPY --from=build /app/build/libs/*.jar /server/kotlin-all.jar
ENTRYPOINT ["java","-jar","/server/kotlin-all.jar"]
```

* Build: $ docker build . -t kotlin:1.0
* Run: $ docker run -p 8080:8080 --rm kotlin:1.0
* Build time: 682.8s
* Build size: 660.5MB
* Description: Such a long build of the image is due to the fact that the build-system Gradle, after installing all dependencies, generates a special FAT JAR file for the successful operation of the server in runtime.
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/f4e743327f48c637ace14f5fe720a8ccf203d2a6)

------------
### CONCLUSION

After familiarizing myself with working with Docker, the following conclusions can be drawn:

* *Docker is a powerful tool for managing containerized applications. It allows you to create, deploy, and manage application containers that provide an isolated environment for software execution.*
* *Using Docker allows you to ensure the portability and consistency of the environment. You can create an image with your application and all dependencies, and then run it on any environment where Docker is installed. This avoids problems associated with configuration differences between different systems.*
* *Docker allows you to deploy applications quickly and efficiently. You can easily install and run containers with applications, which allows you to speed up software development, testing, and deployment.*
* *Docker allows you to create multi-stage builds, which makes it easier to optimize and reduce the size of images. You can divide the build process into several stages to minimize the number of dependencies and include only the necessary components in the final image.*
* *The Docker community is active and growing. There are a large number of publicly available images and tools to help you work with Docker. You can find many resources, documentation, and examples online to help you learn and use Docker effectively.*


**In general, working with Docker allows you to simplify the deployment and management of applications, provides portability and consistency of the environment, and allows you to speed up the development process. The choice between different types of images, such as Scratch and Distroless, depends on your needs and project requirements. With Docker, you can create an infrastructure that is easily scalable and maintainable, which contributes to the efficient operation of the software.**

-----------------------
