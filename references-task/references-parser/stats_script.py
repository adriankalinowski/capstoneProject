import urllib.request, os
import xml.etree.ElementTree as ET
import json, argparse, pathlib
from lxml import etree

parser = argparse.ArgumentParser()

parser.add_argument("-i", "--input", help="File name you want the downloaded data to save as (ex: pubmedStats.html)", default="pubmedStats.html")
parser.add_argument("-o", "--output", help="File name you want the stats output data to save as (ex: stats.csv)", default="stats.txt")
parser.add_argument("-d", "--downpath", help="File path of the downloaded data. Must have trailing '/' (ex: /downloaded_data/)", default="")

arguments = parser.parse_args()

pathlib.Path(arguments.downpath).mkdir(parents=True, exist_ok=True)

path = arguments.downpath + arguments.input

if (os.path.isfile(path) and os.access(path, os.R_OK)):
    print("file exists, continue with parsing ...")


tree=ET.parse(path, etree.HTMLParser())
stats=[]

for tr in tree.findall(".//tr"):
	label = tr.find(".//td[@align='left']").text if tr.find(".//td[@align='left']") is not None else None
	value = int(tr.find(".//td[@align='right']").text) if tr.find(".//td[@align='right']") is not None else None
	stats.append({"label":label, "value":value})

with open(arguments.output, 'w') as f:
	f.write(json.dumps(stats))
