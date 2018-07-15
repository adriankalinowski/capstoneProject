import os, sys, json
from lxml import etree as ET
import pandas as pd
import argparse

from helper_functions.save_to_csv import save_to_csv

def get_text(node, path):
    child_node = node.find(path)
    return child_node.text if child_node is not None else None

def get_issn(column):
    issn_list = journal.iterfind('Issn')
    if issn_list is not None:
        for issn in issn_list:
            if issn.attrib.get('type') == column:
                return issn.text

def get_alias():
    alias_list = []
    all_alias = journal.iterfind('Alias')
    if all_alias is not None:
        for alias in all_alias:
            alias_list.append(alias.text)
    return json.dumps({'Alias': alias_list}) if alias_list is not None else None

parser = argparse.ArgumentParser()
    
parser.add_argument("-i", "--input", help="Full path to input xml file", default="./downloaded_data/jourcache.xml")
parser.add_argument("-j", "--journal", help="File name you want to ouput the article CSV as", default="./data/journal_cache.csv")
    
arguments = parser.parse_args()
    
#check if input file exists
print(arguments.input)
if not (os.path.isfile(arguments.input) and os.access(arguments.input, os.R_OK)):
    print("file not found, process terminating...")
    sys.exit()
        
with open(arguments.input, 'rb') as f:

    parser = ET.XMLParser(dtd_validation=True)
    tree = ET.parse(f, parser)
        
    #Create Pandas dataframe base for journal_cache.csv
    journal_columns = ['NlmUniqueID', 'Name', 'print', 'electronic', 'IsoAbbr', 'MedAbbr', 'jrid', 'StartYear', 'EndYear', 'ActivityFlag', 'Alias']
    journal_xml = []
        
    #Start Extraction
    journals = tree.iterfind('Journal')
    for journal in journals:
        issn_online = None
        issn_print = None
        journal_list = []
        alias_list = []
        for column in journal_columns:
            if column=="print" or column=="electronic":
                journal_list.append(get_issn(column))
            elif column=="Alias":
                journal_list.append(get_alias())
            elif column=="jrid":
                journal_list.append(journal.attrib.get(column))
            else:
                journal_list.append(get_text(journal, column))
        journal_xml.append(journal_list)               
    try:
        save_to_csv(pd.DataFrame.from_records(journal_xml, columns=journal_columns), arguments.journal)
    except Exception as e:
        print("Couldn't export to csv, error: "+str(e))
