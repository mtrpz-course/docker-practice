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