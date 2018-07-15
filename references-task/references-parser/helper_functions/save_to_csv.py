import csv, os, json
import pandas as pd

def save_to_csv(article_df, file_name, compression_type=None):
    
    try:
        article_df.to_csv(file_name, sep='\t', line_terminator='\n', header=False, index=False, float_format='%.f', quoting=csv.QUOTE_NONNUMERIC, quotechar='"', doublequote="'", encoding='UTF-8', compression=compression_type)
    except Exception as e:
        try:
            print("Exception encountered, unable to write to " + file_name + ": "+str(e)+"\n")
            print("Deleting CSVs....")
            os.remove(file_name)
        except Exception as e:
            print("Exception encountered, unable to remove " + file_name + ": "+str(e)+"\n")
