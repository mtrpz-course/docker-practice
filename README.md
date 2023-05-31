# docker-practice

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
