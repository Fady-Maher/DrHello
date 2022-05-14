import requests
from urllib.parse import quote_plus
from requests.structures import CaseInsensitiveDict
# nltk.download('stopwords')
# the stemmer requires a language parameter
##snow_stemmer = SnowballStemmer(language='english')
##stopword=set(stopwords.words('english'))
from spellchecker import SpellChecker
from deep_translator import GoogleTranslator


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
    return response['prediction'] +"@"+ response['probability']


# for text
def modelText(string):
    string_old = string
    url = "https://chat-model.herokuapp.com/predict_text"
    headers = CaseInsensitiveDict()
    headers["accept"] = "application/json"
    headers["Content-Type"] = "application/json"

    string = method_spellchecker(string).lower()
    if "and" in string:
        print("")
    else:
        str_general = ['"' + string + '",' + '"' + "0" + '"']
        str_aid = ['"' + string + '",' + '"' + "1" + '"']

        str_general = """{}""".format("\n".join(str_general))
        str_general = "[" + str_general + "]"
        res_general = requests.post(url, headers=headers, data=str_general)
        print(res_general.status_code)
        js_general = res_general.json()
        ch_general = js_general['prediction'][-1]

        str_aid = """{}""".format("\n".join(str_aid))
        str_aid = "[" + str_aid + "]"
        response_aid = requests.post(url, headers=headers, data=str_aid)
        print(response_aid.status_code)
        js_aid = response_aid.json()
        ch_aid = js_aid['prediction'][-1]

        if js_general['prediction'] == js_aid['prediction']:
            print("==   " + js_aid['prediction'])
            return js_aid['prediction']
        elif js_general['prediction'] == "ChatBot":
            print("==ChatBot:  " + js_aid['prediction'])
            return js_aid['prediction']
        else:
            return js_general['prediction']


def model_classifer(new_sysmptom):
    url = "https://chat-model.herokuapp.com/predict_text"
    headers = CaseInsensitiveDict()
    headers["accept"] = "application/json"
    headers["Content-Type"] = "application/json"
    list_strings = new_sysmptom.split("@")
    str_disease = []
    for s in list_strings:
        s = s.lower().replace("_", " ").replace("—", " ")
        if s == list_strings[-1]:
            sr = "\"" + s + "\""
        else:
            sr = "\"" + s + "\","
        str_disease.append(sr)
    str_disease = """{}""".format("\n".join(str_disease))
    str_disease = "[" + str_disease + "]"
    res_disease = requests.post(url, headers=headers, data=str_disease)
    print(res_disease.status_code)
    js_disease = res_disease.json()
    print(js_disease)
    # res_knn +"@"+res_svm + "@" + res_log+"0"
    return js_disease['prediction']

