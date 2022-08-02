import requests
from requests.structures import CaseInsensitiveDict
from deep_translator import GoogleTranslator
import random



def method_translate(sentence):
    try:
        return GoogleTranslator(source='en', target='ar').translate(sentence)
    except:
        return "error"


def method_translateAR(sentence):
    try:
        return GoogleTranslator(source='ar', target='en').translate(sentence)
    except:
            return "error"

def method_spellchecker(sentence):
    res = []
    spell = SpellChecker()
    for word in sentence.split():
        res.append(spell.correction(word))
    res = ' '.join(res)
    return res


# for images
def model(path, model):
    files = {'file': open(path, 'rb')}
    if model == 'Corona':
        base_url = 'https://chest-model.herokuapp.com/predict_disease'
    elif model == 'Skin':
        base_url = 'https://skin-canncer-model.herokuapp.com/predict_disease'
    elif model == 'Heart':
        base_url = 'https://heart-beat-model.herokuapp.com/predict_disease'
    elif model == 'Brain':
        base_url = 'https://brain-model.herokuapp.com/predict_disease'
    elif model == 'Optical':
        base_url = 'https://eye-model.herokuapp.com/predict_disease'
    elif model == 'General':
         base_url = 'https://general-model.herokuapp.com/predict_disease'

    response = requests.post(base_url, files=files)
    if response.status_code != 200:
         return 'error'
    print(response.status_code, response.text)
    response = response.json()
    return response['prediction'] + "@" + response['probability']


def diseasePrediction2(base_url,psymptoms,refused):
        params = {'psymptoms': psymptoms,'refused': refused}
        url = base_url+'/disease-prediction'
        response = requests.get(url, params=params)
        if response.status_code != 200:
            return 'error'
        print('diseasePrediction2: ',response.text)
        return response.text

def chatbot(base_url,text):
    params = {'qu': text}
    url = base_url+'/Chat-Bot'
    response = requests.get(url, params=params)
    if response.status_code != 200:
        return 'error'
    print('chatbot: ',response.text)
    return response.text

def predictComment(base_url,text,lang):
    params = {'text': text}
    url = base_url+'/predict_hate'
    print('url: ',url)
    response = requests.get(url, params=params)
    if response.status_code != 200:
        return 'error'
    response = response.json()
    print('predictComment: ',response['prediction'])
    return response['prediction']




