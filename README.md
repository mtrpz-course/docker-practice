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

4) Upgrading of `app.py` 

* Build: $ docker build . -t py-fastapi:4.0
* Run: $ docker run -p 8080:8080 --rm py-fastapi:4.0
* Build time: 1.4s
* Build size: 168MB (same)
* Description: Faster build time because the base image `python:slim-bullseye` was cached.
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/4d4c67f0aed3cb083e92adb94083990bf577c6cb)

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
CMD ["uvicorn", "spaceship.main:app", "--host=0.0.0.0", "--port=8080"]```

* Build: $ docker build . -t py-fastapi:5.0
* Run: $ docker run -p 8080:8080 --rm py-fastapi:5.0
* Build time: 125.5s
* Build size: 415MB
* Description: At the build based on alpine, not all dependencies were installed for successful numpy build (I wanted to die during this struggle), so I added an instruction in the Dockerfile that installs the necessary dependencies (g++, gcc, musl-dev, ...). The library download resulted in long connection time and a large image size.
* [Commit link](https://github.com/mtrpz-course/docker-practice/commit/f80afcbdf8ef22f4895c29596abb66a6c824efc8)





