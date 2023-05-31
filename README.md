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
-------------
2) Changing of `api.py`

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
