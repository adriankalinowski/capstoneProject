import urllib.request, os, gzip, sys, datetime
from lxml import etree as ET
from io import StringIO
import pandas as pd
import json, argparse, pathlib

from helper_functions.pubmed_data_utils import determine_pubdate
from helper_functions.pubmed_data_utils import sanitize_article_title
from helper_functions.save_to_csv import save_to_csv

def get_text_attribute(parent_node, child_name, attrib_name):
    child_node = parent_node.find(child_name)
    if (child_node is None) or (child_node.attrib.get(attrib_name) is ""):
        return None
    else:
        return [child_node.attrib.get(attrib_name), ''.join(child_node.itertext())]

def get_text(parent_node, node_path):
    if node_path is not None:
        child_node = parent_node.find(path=node_path)
        return ''.join(child_node.itertext()) if child_node is not None else None

    return ''.join(parent_node.itertext()) if parent_node is not None else None

def get_dict(node, node_name, attrib_list):
    if node is None or node.text is None:
        return None

    ret_dict = {node_name: ''.join(node.itertext())}
    for attrib in attrib_list:
        ret_dict[attrib] = node.attrib.get(attrib)
    return ret_dict

def extract_publication_date(article):
    pubdate = article.find(path= './Journal/JournalIssue/PubDate')
    medline = get_text(pubdate, 'MedlineDate')
    articledate = article.find('ArticleDate')
    published_date = determine_pubdate(articledate, None)
    #Grab pubdate if article date is not present
    if published_date is None:
        published_date = determine_pubdate(pubdate, None)
        #Grab medline if pubdate is not present
        if medline is not None and published_date is None:
            published_date = determine_pubdate(None, medline)

    return published_date, medline
    
def extract_article_info(pubmed, pmid_version, article_xml, mesh_xml, chemical_compound_xml, abstract_xml):

    auth_list = []
    abstract_list = []
    keyword_list = []
    articleid_list = []
    locationid_list = []
    keyword_dict = {}
    
    articleIds = pubmed.find('PubmedData/ArticleIdList')
    for articleId in articleIds:
        articleid_dict = get_dict(articleId, 'ArticleId', ['IdType'])
        if articleId is not None: 
            articleid_list.append(articleid_dict)

    medcite = pubmed.find('MedlineCitation')
    pmid = get_text(medcite, 'PMID')
    
    for article in medcite.iter('Article'):
        article_title = get_text(article, 'ArticleTitle')
        article_result = sanitize_article_title(article_title)
        article_title = article_result[0]
        is_translated = article_result[1]
        
        #Grab variables for journal JSON
        cited_medium = article.find('./Journal/JournalIssue').attrib.get('CitedMedium')
        pagination = get_text(article, 'Pagination/MedlinePgn')
        for locationid in article.iterfind('ELocationID'):
            locationid_dict = get_dict(locationid, 'ELocationID', ['EIdType', 'ValidYN'])
            if locationid_dict is not None: 
                locationid_list.append(locationid_dict)
            
        vernacular_title = get_text(article, 'VernacularTitle')
        volume = get_text(article, './Journal/JournalIssue/Volume')
        issue = get_text(article, './Journal/JournalIssue/Issue')

        #Grab article date
        published_date, medline = extract_publication_date(article)
        
        abstracts = article.iterfind('Abstract/AbstractText')
        for abstract in abstracts:
            abstract_dict = get_dict(abstract, 'Abstract', ['Category', 'Label'])
            if abstract_dict is not None:
                abstract_list.append(abstract_dict)
        
        for author in article.iterfind(path= './AuthorList/Author'):
            #add forename and lastname to a dictionary and add it to the list
            forename = get_text(author, 'ForeName')
            lastname = get_text(author, 'LastName')
            collective_name = get_text(author, 'CollectiveName')
            if lastname is not None:
                auth_list.append({'ForeName': forename, 'LastName': lastname})
            if collective_name is not None:
                auth_list.append({'CollectiveName': collective_name})

    nlm = medcite.find('MedlineJournalInfo/NlmUniqueID')
    nlmID = get_text(nlm, None)

    for mesh_heading in medcite.iterfind(path='./MeshHeadingList/MeshHeading'):
        try:
            mesh = get_text_attribute(mesh_heading,'DescriptorName','UI')
            if mesh is not None:
                mesh.append(pmid)
                mesh.append(pmid_version)
                mesh_xml.append(mesh)
        except Exception as e:
            print("Unable to append to mesh_xml: " + str(e))
    
    for chemical in medcite.iterfind(path='./ChemicalList/Chemical'):
        try:
            chem = get_text_attribute(chemical, 'NameOfSubstance', 'UI')
            if chem is not None:
                chem.append(pmid)
                chem.append(pmid_version)
                chemical_compound_xml.append(chem)
        except Exception as e:
            print("Unable to append to chemical_compound_xml: " + str(e))

    k_list = medcite.find('KeywordList')
    if k_list is not None:
        keyword_owner = k_list.attrib.get('Owner') if k_list is not None else None
        keywords = k_list.iterfind('Keyword')
        for keyword in keywords:
            keyword_dict = get_dict(keyword, 'Keyword', ['MajorTopicYN'])
            keyword_list.append(keyword_dict)
        keyword_dict = {'Keywords': keyword_list, 'Owner': keyword_owner}
            
    #Append abstract list
    if abstract_list is not None and len(abstract_list) > 0:
        abstract_xml.append([json.dumps(abstract_list), pmid, pmid_version])
    #Append article list
    try:
        journal_dict = {'Volume': volume, 'Issue': issue}
        if pagination is not None:
            journal_dict['Pagination'] = pagination
        if vernacular_title is not None:
            journal_dict['VernacularTitle'] = vernacular_title
        if cited_medium is not None:
            journal_dict['CitedMedium'] = cited_medium
        if locationid_list is not None and len(locationid_list) > 0:
            journal_dict['ELocationIDs'] = locationid_list
        
        journal_info = json.dumps(journal_dict)
        article_xml.append([pmid, pmid_version, article_title, is_translated, published_date, medline, nlmID, journal_info, json.dumps(auth_list), json.dumps(keyword_dict), json.dumps(articleid_list)])
    except Exception as e:
        print("Unable to append to article_xml: " + str(e))
        
    
def parse(arguments):
    with gzip.open(arguments.input, 'rb') as f:

        dtd = ET.DTD(open(arguments.dtd_input))
        
        tree = ET.parse(f)

        if not dtd.validate(tree):
            print("DTD is invalidated. Check structure of the DTD to ensure there will be no parsing erros")
            sys.exit(1)

        print("DTD validated. Continuing with parsing...")
            
        #Create Pandas dataframe base for article.csv
        article_columns = ['PMID', 'Version', 'Title', 'IsTranslated', 'PubDate', 'MedlineDate', 'Nlm ID', 'Journal', 'Authors', 'Keywords', 'ArticleIds']
        article_xml = []
        article_xml_version = []
        #Create Pandas dataframe base for mesh.csv
        mesh_columns = ['MeshID', 'MeshName', 'PMID', 'Version']
        mesh_xml = []
        mesh_xml_version = []
        #Create Pandas dataframe base for chemical_compound.csv
        chemical_compound_columns = ['ChemicalID', 'ChemicalName', 'PMID', 'Version']
        chemical_compound_xml = []
        chemical_compound_xml_version = []
        #Create Pandas dataframe base for abstract.csv
        abstract_columns = ['Abstract', 'PMID', 'Version']
        abstract_xml = []
        abstract_xml_version = []
        #Create Pandas dataframe base for deleted.csv
        deleted_columns = ['PMID', 'Version']
        deleted_xml = []
        deleted_xml_version = []
        
        for delete in tree.iterfind('DeleteCitation/PMID'):
            deleted_pmid = delete.text if delete is not None else None
            deleted_version = int(delete.attrib.get('Version'))
            if deleted_version == 1:
                deleted_xml.append([deleted_pmid, deleted_version])
            else:
                deleted_xml_version.append([deleted_pmid, deleted_version])
            
        #Start Extraction
        for pubmed in tree.iter('PubmedArticle'):
            pmid_version = int(pubmed.find('MedlineCitation/PMID').attrib.get('Version'))
            if (pmid_version == 1):
                extract_article_info(pubmed, pmid_version, article_xml, mesh_xml, chemical_compound_xml, abstract_xml)
            else:
                extract_article_info(pubmed, pmid_version, article_xml_version, mesh_xml_version, chemical_compound_xml_version, abstract_xml_version)

        try:
        
            #first versions save to csv
            save_to_csv(pd.DataFrame.from_records(mesh_xml, columns=mesh_columns), arguments.mesh, arguments.compression)
            save_to_csv(pd.DataFrame.from_records(chemical_compound_xml, columns=chemical_compound_columns), arguments.chemical, arguments.compression)
            save_to_csv(pd.DataFrame.from_records(article_xml, columns=article_columns), arguments.article, arguments.compression)
            save_to_csv(pd.DataFrame.from_records(abstract_xml, columns=abstract_columns), arguments.abstract, arguments.compression)
            save_to_csv(pd.DataFrame.from_records(deleted_xml, columns=deleted_columns), arguments.deleted, arguments.compression)
            
            #versions greater than one save to csv
            save_to_csv(pd.DataFrame.from_records(mesh_xml_version, columns=mesh_columns), arguments.mesh_versions, arguments.compression)
            save_to_csv(pd.DataFrame.from_records(chemical_compound_xml_version, columns=chemical_compound_columns), arguments.chemical_versions, arguments.compression)
            save_to_csv(pd.DataFrame.from_records(article_xml_version, columns=article_columns), arguments.article_versions, arguments.compression)
            save_to_csv(pd.DataFrame.from_records(abstract_xml_version, columns=abstract_columns), arguments.abstract_versions, arguments.compression)
            save_to_csv(pd.DataFrame.from_records(deleted_xml_version, columns=deleted_columns), arguments.deleted_versions, arguments.compression)
        
        except Exception as e:
            print("Couldn't export to csv, error: "+str(e))
# Main Function
if __name__=="__main__":
    
    parser = argparse.ArgumentParser()
    
    parser.add_argument("-i", "--input", help="Full path to input xml file", default="./downloaded_data/pubmedsample18n0001.xml.gz")
    parser.add_argument("-dt", "--dtd_input", help="Full path to dtd file", default="./downloaded_data/pubmed_180101.dtd")
    parser.add_argument("-a", "--article", help="File name you want to ouput the article CSV as", default="./data/article.csv")
    parser.add_argument("-m", "--mesh", help="File name you want to output the mesh CSV as", default="./data/mesh.csv")
    parser.add_argument("-c", "--chemical", help="File name you want to output the chemicals CSV as", default="./data/chemical_compound.csv")
    parser.add_argument("-ab", "--abstract", help="File name you want to output the abstract CSV as", default="./data/abstract.csv")
    parser.add_argument("-d", "--deleted", help="File name you want to output the deleted CSV as", default="./data/deleted.csv")
    parser.add_argument("-av", "--article_versions", help="File name you want to ouput the article CSV as", default="./data/versions_article.csv")
    parser.add_argument("-mv", "--mesh_versions", help="File name you want to output the mesh CSV as", default="./data/versions_mesh.csv")
    parser.add_argument("-cv", "--chemical_versions", help="File name you want to output the chemicals CSV as", default="./data/versions_chemical.csv")
    parser.add_argument("-abv", "--abstract_versions", help="File name you want to output the abstract CSV as", default="./data/versions_abstract.csv")
    parser.add_argument("-dv", "--deleted_versions", help="File name you want to output the deleted CSV as", default="./data/versions_deleted.csv")
    parser.add_argument("-cm", "--compression", choices=['gzip'], help="Compression type for CSV files", default=None)
    
    
    arguments = parser.parse_args()
    
    #check if input file exists
    print(arguments.input)
    if not (os.path.isfile(arguments.input) and os.access(arguments.input, os.R_OK)):
        print("file not found, process terminating...")
        sys.exit()
    
    parse(arguments)
