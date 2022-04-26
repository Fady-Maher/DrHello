import nltk
from nltk.stem.snowball import SnowballStemmer
import re
import string
from nltk.corpus import stopwords
from com.chaquo.python import Python
import numpy as np
from PIL import Image
import io
import cv2
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

def call(imp,name_model):
    pic = Image.open(io.BytesIO(bytes(imp)))
    open_cv_image = np.array(pic)
    if name_model == 'Corona':
        img = cv2.resize(open_cv_image,dsize=(500,500),interpolation=cv2.INTER_CUBIC)
    else:
        img = cv2.resize(open_cv_image,dsize=(400,400),interpolation=cv2.INTER_CUBIC)
    gray = cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)
    gray = cv2.GaussianBlur(gray, (5, 5), 0)
    print(gray.shape)
    return gray

