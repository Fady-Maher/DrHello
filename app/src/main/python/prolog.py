import nltk
from nltk.stem.snowball import SnowballStemmer
import re
import string
from nltk.corpus import stopwords
from com.chaquo.python import Python
import requests
from urllib.parse import quote_plus
#nltk.download('stopwords')
#the stemmer requires a language parameter
##snow_stemmer = SnowballStemmer(language='english')
##stopword=set(stopwords.words('english'))

def clean_text(text):
    text = str(text).lower()
    text = re.sub('\[.*?\]', '', text)
    text = re.sub('https?://\S+|www\.\S+', '', text)
    text = re.sub('<.*?>+', '', text)
    text = re.sub('[%s]' % re.escape(string.punctuation), '', text)
    text = re.sub('\n', '', text)
    text = re.sub('\w*\d\w*', '', text)
    #text = [snow_stemmer.stem(word) for word in text.split(' ') if word not in stopword]
    text = " ".join(text)
    return text

def model(url,model):
    if model == 'Corona':
         base_url = ''
    elif model == 'Skin':
         base_url = 'https://skin-canncer-model.herokuapp.com/predict_disease?link=' +quote_plus(url)
    elif model == 'Heart':
         base_url = 'https://heart-beat-model.herokuapp.com/predict_disease?link=' +quote_plus(url)
    else:
         base_url = ''
    print('base_url : ',base_url)
    response = requests.get(base_url)
    return response.text