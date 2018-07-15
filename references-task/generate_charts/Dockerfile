FROM python:3.6-stretch

WORKDIR /usr/src/app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt
RUN apt-get -y update && apt-get -y install postgresql-client-9.6

COPY generate_charts.py ./
COPY start_notebook.sh ./

CMD ['python3', 'generate_charts.py']