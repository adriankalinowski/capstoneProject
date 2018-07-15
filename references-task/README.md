# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* XML Extraction
* Pandas to CSV
* CSV to database
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

#### Summary of Docker set up ####

* Install Docker Toolbox (https://docs.docker.com/toolbox/overview/)
* Clone repository to local file

#### Summary of local set up for parser ####

Something to note. Some of the commands may be different depending on whether or not Python 2 is
currently installed. Start by using the commands that end with 3 (i.e. python3/pip3) and continue.
If it doesn't work correctly, go back and use the normal commands (python/pip)

* Install Python 3 (https://www.python.org/downloads/)
* Open a command line terminal
* Install Pandas 'pip3 install pandas' or 'pip install pandas'
* Install Line_Profiler 'pip3 install line_profiler' or 'pip install line_profiler'

#### Configuration ####

#### Dependencies ####

#### Database configuration ####

#### How to run tests ####

Download specific number of baseline files at a time:

* Open annual_baseline.sh
* Add '-f' to the end of the first docker statement that calls download_baseline_files.sh
* If you wish to download the entire baseline, enter 'pubmed18n*'
* The * is a wildcard, and will download all files on the ftp server directory with a filename that contains the characters given
* If you only want to download a specific amount of files, use REGEX
* Example: 'pubmed18n00[0-2][0-9]' will download files 1-29.
* Example: 'pubmed18n00[0-2]*' will also download files 1-29.


Run a line profiler on parsing script:

* Open the Docker Quickstart Terminal, or any other bash terminal
* Navigate to the directory where parsing script is located
* Open parsing script in a text editor and import line_profiler 'from line_profiler import LineProfiler'
* One line above a function you want to test, write '@Profile'
* Move back to the command line and run the profiler 'kernprof -l nameOfScript.py'
* View formatted results in terminal 'python3 -m line_profiler nameOfScript.py.lprof' or 'python -m line_profiler nameOfScript.py.lprof' OR
* Export results to a .txt file 'python3 -m line_profiler nameOfScript.py.lprof > test.txt' or 'python -m line_profiler nameOfScripy.py.lprof > test.txt'

#### Deployment instructions ####

* Open the Docker Quickstart Terminal that was installed with Docker Toolbox
* Move to the directory where Dockerfile is
* Build Docker container with `docker build --rm -t advaitacapstone/dpu-references .`
* Run Docker contatiner with `docker run -it advaitacapstone/references python3 script.py`

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact - Mithell Conte and Kalie Humbarger
