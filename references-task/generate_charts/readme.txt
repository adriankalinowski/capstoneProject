To generate the charts:

---FIRST TIME RUN---
1. open a docker terminal and navigat to the /generate_charts/ directory
2. run ./run_generate_charts.sh
	a. this script will build the docker image, run the docker container, and start the jupyter notebook
3. In your terminal, you will see a message which says "Copy/paste this URL into your broswer when you connect for the first time...". Copy this URL and paste it into your broswer. Change the IP address from 0.0.0.0 to 192.168.99.100 and hit enter. You should now be able to view the main page of the jupyter notebook
4. In the jupyter notebook, navigate to the /graphs/ directory and select capstone_data_graphs.ipynb
5. Once the capstone_data_graphs notebook is opened, select "run". The graphs will be displayed in the browser and downloaded to your local machine

---SECOND RUN +---
1. start the stats_notebook container
2. the jupyter notebook should be accessible in your browser at 192.168.99.100:8890