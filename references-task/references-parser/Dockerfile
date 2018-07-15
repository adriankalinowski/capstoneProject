FROM python:3.6-stretch

WORKDIR /usr/src/app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt
RUN apt-get -y update && apt-get -y install postgresql-client-9.6 pgloader parallel

COPY script.py ./
COPY journal_script.py ./
COPY journal_cache_script.py ./
COPY ./sql_scripts/ ./
COPY ./sql_scripts_updates/ ./
COPY helper_functions ./helper_functions
COPY ./bash_scripts/ /opt/scripts/
COPY stats_script.py ./

CMD [ "python", "./script.py" ]