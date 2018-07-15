import psycopg2
import argparse
import os
import pandas as pd
import plotly as py
import plotly.graph_objs as go
py.offline.init_notebook_mode()

#grab connection parameters from environment variables
db_name = os.environ['PGDATABASE']
db_user = os.environ['PGUSER']
db_host = os.environ['PGHOST']
db_password = os.environ['PGPASSWORD']
graphs_path = os.environ['GRAPHSPATH']

#functions
def create_table(columns, rows, file_name, title):
    layout = go.Layout(title=title)
    data = go.Figure(data=[go.Table(header=dict(values=columns), cells=dict(values=list(zip(*rows))))])
    py.offline.iplot(data, filename=graphs_path+file_name, image='png')

def retrieve_rows(query):
    pg_cursor.execute(query)
    return pg_cursor.fetchall()

#connect to postgres
try:
    pg = psycopg2.connect("dbname=%s user=%s host=%s password=%s"%(db_name, db_user, db_host, db_password))
except Exception as e:
    print("Can not connect to postgres, exception: "+str(e))

pg_cursor = pg.cursor()

#number of references per year
rows = retrieve_rows("select date_part('year',publication_date), count(*) from reference.article group by 1 order by 1 asc;")
layout = go.Layout(title='Reference Count Per Year', xaxis=dict(title='Year'), yaxis=dict(title='References'))
data = [go.Bar(x=[row[0] for row in rows], y=[row[1] for row in rows])]
py.offline.iplot(go.Figure(data=data, layout=layout), filename=graphs_path+'references_per_year', image='png')

#after 2000
#number of references per year
rows = retrieve_rows("select date_part('year',publication_date), count(*) from reference.article WHERE publication_date>'2000-01-01' group by 1 order by 1 asc;")
layout = go.Layout(title='Reference Count Per Year After 2000', xaxis=dict(title='Year'), yaxis=dict(title='References'))
data = [go.Bar(x=[row[0] for row in rows], y=[row[1] for row in rows])]
py.offline.iplot(go.Figure(data=data, layout=layout), filename=graphs_path+'references_per_year', image='png')

#after 1950-2000
#number of references per year
rows = retrieve_rows("select date_part('year',publication_date), count(*) from reference.article WHERE publication_date>'1950-01-01' AND publication_date<'2000-01-01' group by 1 order by 1 asc;")
layout = go.Layout(title='Reference Count Per Year 1950-2000', xaxis=dict(title='Year'), yaxis=dict(title='References'))
data = [go.Bar(x=[row[0] for row in rows], y=[row[1] for row in rows])]
py.offline.iplot(go.Figure(data=data, layout=layout), filename=graphs_path+'references_per_year', image='png')

#before 1950
#number of references per year
rows = retrieve_rows("select date_part('year',publication_date), count(*) from reference.article WHERE publication_date<'1950-01-01' group by 1 order by 1 asc;")
layout = go.Layout(title='Reference Count Per Year Before 1950', xaxis=dict(title='Year'), yaxis=dict(title='References'))
data = [go.Bar(x=[row[0] for row in rows], y=[row[1] for row in rows])]
py.offline.iplot(go.Figure(data=data, layout=layout), filename=graphs_path+'references_per_year', image='png')

#top 10 most referenced journals
rows = retrieve_rows("""select journal_title, reference_count
from (
	select J.journal_title, COUNT(A.nlm_unique_id) as reference_count
    from reference.article A, reference.journal J
    WHERE A.nlm_unique_id=J.nlm_unique_id
    GROUP BY A.nlm_unique_id, J.journal_title
    ORDER BY COUNT(A.nlm_unique_id) DESC LIMIT 10
) as c""")
create_table(['Journal Title', 'Number of References to Journal'], rows, 'top_10_journals', 'Top 10 Most Referenced Journals')

#number of references with a version greater than 1 by year

rows = retrieve_rows("""SELECT date_part('year', A.publication_date), count(pubmed_id) FROM reference.article A WHERE version>1 GROUP BY 1 ORDER BY 1 asc;""")

layout = go.Layout(title='Versions Greater than One by Year', xaxis=dict(title='Year'), yaxis=dict(title='References'))

data = [go.Bar(x=[row[0] for row in rows], y=[row[1] for row in rows])]

py.offline.iplot(go.Figure(data=data, layout=layout), filename=graphs_path+'references_greather_than_one_per_year', image='png')

#how many inserts/updates/deletes for 50 update files


#AVG/MAX/MIN associations for chemicals, mesh, keywords
#chemicals
chem = retrieve_rows("""SELECT MIN(chemical_associations), MAX(chemical_associations), AVG(chemical_associations) 
FROM (SELECT COUNT(chemical_compound_id) as chemical_associations
     FROM reference.article_chemical_compound
     GROUP BY pubmed_id) as chemical_associations;""")

#mesh
mesh = retrieve_rows("""SELECT MIN(mesh_associations), MAX(mesh_associations), AVG(mesh_associations) 
FROM (SELECT COUNT(term_id) as mesh_associations
     FROM reference.mesh_article
     GROUP BY pubmed_id) as mesh_associations;""")

#keywords	
keywords = retrieve_rows("""select min(article_keyword), max(article_keyword), avg(article_keyword) from
  (select pubmed_id, jsonb_array_length(keywords->'Keywords') as article_keyword
  from reference.article
  where keywords IS NOT NULL
  ) k;""")
	
#create table
layout=go.Layout(title='Article Association Information')
data = [go.Table(header=dict(values=['Type', 'Min', 'Max', 'Avg']), cells=dict(values=[['Chemical Compounds', 'Mesh', 'Keywords'], [chem[0][0], mesh[0][0], keywords[0][0]], [chem[0][1], mesh[0][1], keywords[0][1]], [chem[0][2], mesh[0][2], keywords[0][2]]]))]
py.offline.iplot(go.Figure(data=data, layout=layout), filename=graphs_path+'associations', image='png')

#number of references with an abstract
num_abstracts = (retrieve_rows("""SELECT COUNT(pubmed_id) FROM reference.abstract_article;"""))[0]
num_abstracts = "{:,}".format(num_abstracts[0])
print("Number of articles with an abstract: "+str(num_abstracts))

#number of references without an abstract
num_no_abstracts = (retrieve_rows("""SELECT COUNT(pubmed_id) FROM (SELECT pubmed_id FROM reference.article EXCEPT(SELECT pubmed_id FROM reference.abstract_article)) AS T;"""))[0]
num_no_abstracts = "{:,}".format(num_no_abstracts[0])
print("Number of articles with no abstract: "+str(num_abstracts))

#number of deleted articles
num_deleted = (retrieve_rows("""SELECT COUNT(pubmed_id) FROM reference.article WHERE is_deleted=TRUE;"""))[0]
num_deleted = "{:,}".format(num_deleted[0])
print("Number of articles marked deleted: "+str(num_deleted))

#number of references without a publication date
num_missing_date = (retrieve_rows("""SELECT COUNT(pubmed_id) FROM reference.article WHERE publication_date IS NULL;"""))[0]
num_missing_date = "{:,}".format(num_missing_date[0])
print("Number of articles which have no publicaiton date: "+str(num_missing_date))

#how many articles are translated
num_translated = (retrieve_rows("""SELECT COUNT(pubmed_id) FROM reference.article WHERE is_translated=TRUE;"""))[0]
num_translated = "{:,}".format(num_translated[0])
print("Number of articles which have been translated: "+str(num_translated))

#how many articles have no authors
num_authorless = (retrieve_rows("""SELECT COUNT(pubmed_id) FROM reference.article WHERE author_list IS NULL;"""))[0]
num_authorless = "{:,}".format(num_authorless[0])
print("Number of articles which have no authors listed: "+str(num_authorless))

layout=go.Layout(title='Annual Baseline Interesting Facts')
data = [go.Table(header=dict(values=['Description', 'Value']), cells=dict(values=[['Number of articles with an abstract', 'Number of articles without an abstact', 'Number of deleted articles', 'Number of references without a publication date', 'Number of articles which have been translated', 'Number of articles with no author'], [num_abstracts, num_no_abstracts, num_deleted, num_missing_date, num_translated, num_authorless]]))]
py.offline.iplot(go.Figure(data=data, layout=layout), filename=graphs_path+'interesting_facts', image='png')
#what kind of articles don't have authors

#how many journals from each journal source -TBD

#number of references with an abstract
rows = retrieve_rows("select date_part('year',publication_date) as year, (associated_abstract is null), count(*) from reference.article left outer join reference.abstract_article on article.pubmed_id = abstract_article.pubmed_id group by 1,2 order by 1;")


trace1 = go.Bar(
    x=[row[0] for row in list(filter(lambda x: x[1], rows))],
    y=[row[2] for row in list(filter(lambda x: x[1], rows))],
    name='without abstract'
)
trace2 = go.Bar(
    x=[row[0] for row in list(filter(lambda x: not(x[1]), rows))],
    y=[row[2] for row in list(filter(lambda x: not(x[1]), rows))],
    name='with abstract'
)

data = [trace1, trace2]
layout = go.Layout(
    title='References with Abstracts by Year',
    barmode='stack'
)

fig = go.Figure(data=data, layout=layout)
py.offline.iplot(fig, filename=graphs_path+'grouped-bar')

#10 journals with the highest number of references
rows = retrieve_rows("""select J.journal_title, COUNT(A.nlm_unique_id) as reference_count
    from reference.article A, reference.journal J
    WHERE A.nlm_unique_id=J.nlm_unique_id
    GROUP BY A.nlm_unique_id, J.journal_title
    ORDER BY COUNT(A.nlm_unique_id) DESC LIMIT 20""")
layout=go.Layout(title='Top 10 Most Referenced Journals', autosize=True)
data = [go.Bar(x=[row[0] for row in rows], y=[row[1] for row in rows])]
py.offline.iplot(data, filename=graphs_path+'references_per_journal', image='png')

#references with versions
rows = retrieve_rows("""select version, count(pubmed_id) from reference.article where version>1 group by 1 order by 1 asc
""")
layout=go.Layout(title='References by Version', xaxis=dict(title='Version'), yaxis=dict(title='Number of References'))
data = [go.Bar(x=[row[0] for row in rows], y=[row[1] for row in rows])]
py.offline.iplot(go.Figure(data=data, layout=layout), filename=graphs_path+'references_with_versions', image='png')

#medline dates
rows = retrieve_rows("""select (ex_year::integer), array_agg(medline_date) from (
select pubmed_id, article_ids, medline_date, substring(medline_date from '[1-2][0-9][0-9][0-9]') as ex_year from reference.article a
where publication_date is null and  medline_date ~ '[1-2][0-9][0-9][0-9].*' ) t
group by 1""")
layout=go.Layout(title='References by Year of Medline Date', xaxis=dict(title='Year'), yaxis=dict(title='Number of References'))
data = [go.Bar(x=[row[0] for row in rows], y=[len(row[1]) for row in rows])]
py.offline.iplot(go.Figure(data=data, layout=layout), filename=graphs_path+'references_with_versions', image='png')