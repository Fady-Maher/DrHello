import requests
from requests.structures import CaseInsensitiveDict
from spellchecker import SpellChecker
from deep_translator import GoogleTranslator
from langdetect import detect


def method_translate(sentence):
    return GoogleTranslator(source='en', target='ar').translate(sentence)


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

    response = requests.post(base_url, files=files)
    print(response.status_code, response.text)
    response = response.json()
    return response['prediction'] + "@" + response['probability']

def model_classifer(text):
    url = "https://chat-model.herokuapp.com/predict_text"
    headers = CaseInsensitiveDict()
    headers["accept"] = "application/json"
    headers["Content-Type"] = "application/json"
    res_disease = requests.post(url, headers=headers, data=text)
    print(res_disease.status_code)
    js_disease = res_disease.json()
    print(js_disease)
    # res_knn +"@"+res_svm + "@" + res_log+"0"
    return js_disease['prediction']


def modelCommentAndPost(text):
    headers = {'accept': 'application/json', 'content-type': 'application/x-www-form-urlencoded', }
    if text.isdigit():
        return 0.1
    else:
        lang = detect(text)
        print(lang)
        params = {'text': text}
        if lang == 'ar':
            url = 'https://hate-detection-model.herokuapp.com/predict_text'
        else:
            url = 'https://hate-detection-model-en.herokuapp.com/predict_text'
        response = requests.post(url, params=params, headers=headers)
        if response.status_code != 200:
            return 'error'

        response = response.json()
        return response['prediction']
