import os, argparse, sys
import pandas as pd

from helper_functions.save_to_csv import save_to_csv

parser = argparse.ArgumentParser()

parser.add_argument("-i", "--input", help="File name and path of the journal list (ex: ./downloaded_data/J_Medline.txt)", default="./downloaded_data/J_Entrez.txt")
parser.add_argument("-o", "--output", help="File name you want the stats output data to save as (ex: journal.csv)", default="./data/journal.csv")

arguments = parser.parse_args()

if not (os.path.isfile(arguments.input) and os.access(arguments.input, os.R_OK)):
    print("file not found, process terminating...")
    sys.exit()

journal_columns = ['NlmId:', 'JournalTitle:','ISSN (Print):', 'ISSN (Online):', 'IsoAbbr:']
prefix_columns = {}
journal_xml = []
df = pd.DataFrame(columns=journal_columns)
                  
with open(arguments.input, "r") as f:
    for line in f:
        for prefix in journal_columns:
            if line.startswith(prefix): prefix_columns[prefix] = line.partition(prefix)[2].strip() if line[2] is not None else None
        if line.startswith("---------"):
            if ('NlmId:' in prefix_columns):
                journal_xml.append([prefix_columns[k] for k in df.columns.values])
                prefix_columns.clear()

# txt file doesn't end with -------- so this is necessary to include the last journal entry
    journal_xml.append([prefix_columns[k] for k in df.columns.values])

save_to_csv(pd.DataFrame.from_records(journal_xml, columns=prefix_columns), arguments.output)
