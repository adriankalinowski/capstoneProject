import os, datetime, re

def determine_pubdate(pubdate, medline):

    year = pubdate.find('Year') if pubdate is not None else medline
    month = pubdate.find('Month') if pubdate is not None else None
    day = pubdate.find('Day') if pubdate is not None else None
    
    if (year is None):
        return None
    else:
        medline_year = re.search('[1-2][0-9][0-9][0-9]', medline) if medline is not None else None
        if pubdate is not None: 
            pubYear = int(year.text)
        elif medline_year is not None:
            pubYear = int(medline_year.group(0))
        else:
            return None

        #Check if pubmonth is none
        if month is None: pubMonth = 1
        else:
        #Convert month abbreviations to numbers
            months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']
        #Month can also be a number or abbreviation, so decide which one it is
        #https://www.nlm.nih.gov/bsd/licensee/elements_descriptions.html#pubdate
        #Standardization is with abbreviation, but nothing to explicitly say all are like this
            pubMonth = (months.index(month.text) + 1) if len(month.text)==3 else int(month.text)
        pubDay = int(day.text) if day is not None else 1
        
        
        try:
            return datetime.date(pubYear, pubMonth, pubDay).isoformat()
        except ValueError:
            print("Invalid date for PubDate, returning none...")
            return None
        
def sanitize_article_title(title):
    if (title is not None):
        title = title[:-1] if title.endswith('.') else title
        return (title[1:-1], True) if title.startswith('[') and title.endswith(']') else (title, False)

    return title, False
