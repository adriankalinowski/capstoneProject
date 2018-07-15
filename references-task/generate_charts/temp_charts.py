#articles updated by year
rows = retrieve_rows("""select date_part('year', publication_date), count(DISTINCT pubmed_id) from reference.article join reference.update_article using (pubmed_id) group by 1 order by 1;""")
layout = go.Layout(title='Articles Updated by Year', xaxis=dict(title='Year'), yaxis=dict(title='Number of Article'))
data = [go.Bar(x=[row[0] for row in rows], y=[rows[1] for row in rows])]
py.offline.iplot(go.Figure(data=data, layout=layout), filename=graphs_path+'articles_updated_by_year', image='png')