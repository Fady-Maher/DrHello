import requests
from requests.structures import CaseInsensitiveDict
from spellchecker import SpellChecker
from deep_translator import GoogleTranslator
from langdetect import detect
import joblib
from os.path import dirname, join
import random
import re


def method_translate(sentence):
    return GoogleTranslator(source='en', target='ar').translate(sentence)

def method_translateAR(sentence):
    return GoogleTranslator(source='ar', target='en').translate(sentence)

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


############## chatbot #####################
def getRes(model,psymptoms,symp):

    l2=[]
    for i in range(0,len(symp)):
        l2.append(0)
    for k in range(0,len(symp)):
        for z in psymptoms:
            if(z==symp[k]):
                l2[k]=1
    inputtest = [l2]
    predict = model.predict(inputtest)
    predicted=predict[0]
    probs = model.predict_proba(inputtest)
    prop_index = []
    prop_sort = sorted(-probs[0])
    prop_sort = [ -x for x in prop_sort]
    probs = [ x for x in probs[0]]
    for i in prop_sort:
        prop_index.append(probs.index(i))
    newDis = []
    for i in range(len(prop_index)):
        x=round(prop_sort[i]*1000)
        if(x>=1):
            newDis.append(prop_index[i])
    return newDis


def diseasePrediction2(psymptoms,refused):

    blockDis=[]
    allS=[]
    allS2=[]

    #read data of pkl
    dis = join(dirname(__file__), 'dis.pkl')
    mlp = join(dirname(__file__), 'mlp.sav')
    symp = join(dirname(__file__), 'symp.pkl')
    symptoms4 = join(dirname(__file__), 'symptoms4.pkl')

    dis =joblib.load(dis)
    MLP =joblib.load(mlp)
    symp =joblib.load(symp)
    symptoms4 =joblib.load(symptoms4)
    print('psym: ', psymptoms)
    psymptoms = psymptoms[:-1]
    psymptoms = psymptoms.split("&")
    refused = refused[:-1]
    refused = list(set(refused.split("&")))
    print('psymptoms: ', psymptoms)
    print('refused: ', refused)

    newDis = getRes(MLP,set(psymptoms),symp)

    for i in newDis:
        b=0
        for j in symptoms4[i]:
            if j in refused:
                b=b+1
        if b/len(symptoms4[i])*100>70:
            blockDis.append(i)

    for i in newDis:
        if psymptoms[-1] in symptoms4[i]and i not in blockDis:
            allS.append(symptoms4[i])
    for i in range(len(allS)):
        for j in range(len(allS[i])):
            if allS[i][j].lower() not in psymptoms and allS[i][j].lower() not in refused:
                 allS2.append(allS[i][j].lower())
    random.shuffle(allS2)
    if len(allS2)==0:
        return "i cant't detect what you are sufferd from so please try to go to real doctor :) 0"
    else:
        m=0
        for i in psymptoms:
            if i in symptoms4[newDis[0]]:
                m=m+1
        acc=(m/len(symptoms4[newDis[0]]))
        if acc*100 >=70:
           # print('you are suffer from ',dis[newDis[0]],'with acc ',acc)
            res2={}
            for i in range(len(newDis)):
                m=0
                for j in psymptoms:
                    if j in symptoms4[newDis[i]]:
                        m=m+1
                acc=(m/len(symptoms4[newDis[i]]))
                res2[dis[newDis[i]]]=acc

            list_text = ''
            x = 0
            for disease,val in res2.items():
                if x == 3:
                    break
                x = x +1
                if val == 0 :
                    continue
                list_text = list_text + 'you are suffer from ' + disease +' with acc ' + str(val) + '&'
            return list_text + '6'
        else:
            print('allS2: ', allS2)
            return '&'.join(allS2) + '7'


def chatbot(text):
    chatBot = join(dirname(__file__), 'chatBot.pkl')
    symp = join(dirname(__file__), 'symp.pkl')
    symp =joblib.load(symp)
    chatBot =joblib.load(chatBot)
    ans= str(chatBot.predict([text])).replace('[','').replace(']','').replace('\'','')
    if ans in symp:
        print(ans)
        return ans + '1'
    else:
        print(ans)
        return ans + '0'


###########################################################  comment ################################

def remove_diacritics(text):
    text = text.replace('@User.IDX',' ')
    text = text.replace('@user',' ')

    arabic_diacritics = re.compile(""" ّ    | # Tashdid
                             َ    | # Fatha
                             ً    | # Tanwin Fath
                             ُ    | # Damma
                             ٌ    | # Tanwin Damm
                             ِ    | # Kasra
                             ٍ    | # Tanwin Kasr
                             ْ    | # Sukun
                             ـ     # Tatwil/Kashida
                         """, re.VERBOSE)
    text = re.sub(arabic_diacritics, '', str(text))
    return text

def remove_emoji(text):
    regrex_pattern = re.compile(pattern = "["
                                          u"\U0001F600-\U0001F64F"  # emoticons
                                          u"\U0001F300-\U0001F5FF"  # symbols & pictographs
                                          u"\U0001F680-\U0001F6FF"  # transport & map symbols
                                          u"\U0001F1E0-\U0001F1FF"  # flags (iOS)
                                          "]+", flags = re.UNICODE)
    return regrex_pattern.sub(r'',text)

def remove_urls (text):
    text = re.sub(r'(https|http)?:\/\/(\w|\.|\/|\?|\=|\&|\%)*\b', '', text, flags=re.MULTILINE)
    return text

def remove_emails(text):
    text = re.sub(r"(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)", "",  text, flags=re.MULTILINE)
    return text

def remove_english(text):
    text = re.sub(r'[a-zA-Z]',"",text,flags=re.MULTILINE)
    return text

#Text Normalization
def normalize_arabic(text):
    text = re.sub("[إأآا]", "ا", text)
    text = re.sub("ى", "ي", text)
    text = re.sub("ؤ", "ء", text)
    text = re.sub("ئ", "ء", text)
    text = re.sub("ة", "ه", text)
    text = re.sub("گ", "ك", text)
    return text


punc=['!', '"', '#', '$', '%', '&', "'", '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~']
stopWord=['لستم', 'إليكم', 'خلا', 'ا', 'شبه', 'فإذا', 'تخذ', 'ترك', 'دينار', 'إليك', 'عامة', 'اللتيا', 'سبعون', 'خاء', 'ماذا', 'عاد', 'واحد', 'ثلاثة', 'عل', 'تشرين', 'أجمع', 'كأنما', 'ثالث', 'غير', 'كأنّ', 'إياكم', 'حتى', 'أُفٍّ', 'بمن', 'أبدا', 'أعلم', 'إى', 'سمعا', 'طَق', 'لم', 'خمسون', 'رابع', 'ثمَّ', 'إلا', 'واهاً', 'أنى', 'فلا', 'صراحة', 'إياي', 'بماذا', 'مئتان', 'ض', 'التي', 'أطعم', 'الذين', 'عسى', 'آه', 'ذين', 'خمسمائة', 'لكما', 'ذو', 'كان', 'جلل', 'بكن', 'غالبا', 'ما', 'فمن', 'لهما', 'لام', 'يناير', 'ولا', 'ذال', 'هاء', 'همزة', 'ع', 'ثمة', 'ثمان', 'بئس', 'تينك', 'وراءَك', 'غين', 'جانفي', 'أسكن', 'بين', 'مكانكما', 'ش', 'اللذين', 'أيلول', 'خامس', 'شتان', 'ص', 'أوشك', 'بك', 'ج', 'أربع', 'آناء', 'وُشْكَانَ', 'بضع', 'أخذ', 'تلقاء', 'ذي', 'انقلب', 'بكم', 'ن', 'علق', 'ء', 'دونك', 'ّأيّان', 'أمامكَ', 'ثلاثاء', 'بغتة', 'أما', 'كليهما', 'صاد', 'فرادى', 'تجاه', 'ديسمبر', 'فيها', 'كيف', 'جوان', 'تعسا', 'مليم', 'ذهب', 'آمينَ', 'هذه', 'آهاً', 'ذلكن', 'لن', 'أنبأ', 'وجد', 'أربعة', 'فيفري', 'خمسين', 'خبَّر', 'عوض', 'إلّا', 'ليسوا', 'كليكما', 'عشرون', 'حمدا', 'أنتِ', 'نفس', 'عشرة', 'إليكنّ', 'حاي', 'مازال', 'مئة', 'أكثر', 'يوان', 'ظاء', 'أمامك', 'اتخذ', 'قاطبة', 'أوه', 'تسعة', 'إليكما', 'مهما', 'فيه', 'سوف', 'فضلا', 'سبعمائة', 'غدا', 'كأين', 'هنالك', 'لا سيما', 'إياهم', 'م', 'فيما', 'بهما', 'حدَث', 'عند', 'نون', 'إليكَ', 'ذان', 'ظلّ', 'جويلية', 'مايو', 'ما انفك', 'بسّ', 'آذار', 'آ', 'متى', 'آهٍ', 'كلا', 'على', 'بي', 'حبذا', 'ذلك', 'إنَّ', 'ز', 'ذات', 'لهن', 'أخبر', 'سنتيم', 'تين', 'طاء', 'أربعمائة', 'مساء', 'شرع', 'هلّا', 'عشر', 'رجع', 'وإن', 'جمعة', 'هَاتانِ', 'بها', 'جيم', 'اللتان', 'إي', 'كي', 'إحدى', 'خمس', 'ر', 'كسا', 'ثمانين', 'اربعون', 'ق', 'هلم', 'هَؤلاء', 'أمسى', 'ريث', 'أفريل', 'هلا', 'إليكن', 'لست', 'سحقا', 'هَاتَيْنِ', 'الآن', 'ذاك', 'ذَيْنِ', 'كى', 'كرب', 'بكما', 'لو', 'أنًّ', 'هما', 'آض', 'أكتوبر', 'كلما', 'مافتئ', 'عشرين', 'تموز', 'لكيلا', 'لكم', 'ذانك', 'ماي', 'إلى', 'اثنان', 'يونيو', 'أمد', 'هاك', 'ألفى', 'سرا', 'ابتدأ', 'بلى', 'اخلولق', 'عليك', 'ستمائة', 'لعلَّ', 'إن', 'اللتين', 'هذين', 'أصبح', 'تفعلين', 'خال', 'جنيه', 'هَاتِي', 'عيانا', 'إنا', 'نيسان', 'أغسطس', 'هيهات', 'دون', 'أصلا', 'أيا', 'أين', 'ة', 'يا', 'هكذا', 'جعل', 'بنا', 'مع', 'ليت', 'ه', 'حيث', 'رأى', 'بهن', 'سبعين', 'ل', 'آها', 'ثاني', 'ذا', 'ذيت', 'كلَّا', 'عجبا', 'ذواتي', 'هللة', 'أينما', 'تسع', 'أول', 'بؤسا', 'مرّة', 'ذواتا', 'راء', 'كيت', 'إلَيْكَ', 'هبّ', 'ط', 'إما', 'تلكم', 'لكنَّ', 'لسن', 'مه', 'وا', 'ظ', 'فوق', 'عين', 'ذوا', 'نحن', 'إيانا', 'رويدك', 'هيت', 'ذِي', 'لك', 'يمين', 'ممن', 'كذا', 'ث', 'تَيْنِ', 'يفعلان', 'إياه', 'لولا', 'ثلاثمئة', 'مثل', 'هَذَيْنِ', 'ريال', 'ليسا', 'أنا', 'سرعان', 'أيضا', 'صبرا', 'لدن', 'كاف', 'هذان', 'آهِ', 'ف', 'كاد', 'ؤ', 'كانون', 'أوت', 'مائة', 'حجا', 'ستة', 'إذ', 'ثلاث', 'إيه', 'أربعمئة', 'كثيرا', 'ح', 'نعم', 'كأيّن', 'إذما', 'ليرة', 'أنت', 'فإن', 'منها', 'ته', 'ميم', 'ى', 'تعلَّم', 'هيّا', 'صباح', 'نا', 'والذين', 'مكانكنّ', 'خلف', 'نبَّا', 'أمس', 'ليس', 'حيثما', 'عدا', 'إنه', 'حرى', 'طفق', 'هنا', 'علًّ', 'أن', 'هَجْ', 'رُبَّ', 'دواليك', 'ذ', 'كما', 'شين', 'حزيران', 'هاتان', 'بخٍ', 'كيفما', 'ورد', 'إنما', 'به', 'أهلا', 'ومن', 'إياكما', 'هَذِه', 'وهو', 'سوى', 'أنشأ', 'لمّا', 'أمّا', 'ذه', 'قد', 'ب', 'حار', 'حمٌ', 'تاء', 'غداة', 'أفعل به', 'ستون', 'س', 'تحوّل', 'أربعاء', 'راح', 'لها', 'تبدّل', 'في', 'رزق', 'لستما', 'ليست', 'أي', 'أولاء', 'درهم', 'أ', 'أبو', 'ليستا', 'قاف', 'غادر', 'انبرى', 'لئن', 'كذلك', 'بيد', 'اثنا', 'هيا', 'يفعلون', 'د', 'هَيْهات', 'ثلاثون', 'مادام', 'عَدَسْ', 'ست', 'لدى', 'كأي', 'درى', 'قام', 'فاء', 'اللذان', 'لا', 'لسنا', 'لوما', 'ضاد', 'اللاتي', 'حقا', 'ءَ', 'ثلاثمائة', 'كلتا', 'باء', 'واو', 'طالما', 'نَّ', 'أولالك', 'لبيك', 'أمام', 'الألى', 'مذ', 'حَذارِ', 'بات', 'خ', 'ين', 'بَسْ', 'اللائي', 'ثمنمئة', 'ألا', 'لكن', 'ذِه', 'عدَّ', 'هاتي', 'غ', 'إذاً', 'صدقا', 'هذا', 'فبراير', 'ذينك', 'بخ', 'وإذ', 'خمسة', 'بطآن', 'معاذ', 'ستين', 'وَيْ', 'هو', 'دال', 'لكي', 'كلّما', 'إيهٍ', 'شتانَ', 'أنتم', 'فو', 'سبعة', 'أبريل', 'ولكن', 'أضحى', 'أقبل', 'اثني', 'تفعلون', 'شباط', 'ضحوة', 'قطّ', 'ما أفعله', 'لعمر', 'ياء', 'زعم', 'تارة', 'إياكن', 'ما برح', 'ئ', 'عاشر', 'هناك', 'صار', 'أخٌ', 'ثان', 'إياك', 'بعد', 'يوليو', 'سين', 'الألاء', 'هاهنا', 'آنفا', 'بما', 'هم', 'تسعين', 'صبر', 'علم', 'أيّ', 'طاق', 'أوّهْ', 'إزاء', 'إمّا', 'إياهما', 'مما', 'حاء', 'أيار', 'كلاهما', 'ثمانية', 'ذانِ', 'كِخ', 'كأن', 'مكانَك', 'اللواتي', 'هل', 'إياهن', 'أل', 'مكانكم', 'ارتدّ', 'عليه', 'خميس', 'عما', 'كل', 'قرش', 'بل', 'أيها', 'حادي', 'إياها', 'تِي', 'قلما', 'أحد', 'حبيب', 'أخو', 'سبعمئة', 'ثمّ', 'لما', 'أبٌ', 'شَتَّانَ', 'سبحان', 'هي', 'خاصة', 'سبتمبر', 'ثمانمئة', 'صهْ', 'ألف', 'نحو', 'لهم', 'منه', 'اثنين', 'ك', 'نيف', 'ثمانون', 'ذلكما', 'حسب', 'تحت', 'أنتن', 'خلافا', 'تاسع', 'هاكَ', 'بَلْهَ', 'دولار', 'تسعون', 'ي', 'حمو', 'مارس', 'إذن', 'سادس', 'أو', 'وما', 'ثاء', 'فيم', 'تانِ', 'قبل', 'شمال', 'عن', 'لستن', 'سبت', 'بهم', 'زاي', 'ظنَّ', 'أولئك', 'بعض', 'استحال', 'بس', 'هن', 'لي', 'ت', 'تسعمئة', 'طرا', 'هَذِي', 'أعطى', 'سابع', 'أف', 'لعل', 'اربعين', 'آب', 'كأيّ', 'تفعلان', 'زود', 'لاسيما', 'ثم', 'جير', 'هؤلاء', 'حيَّ', 'منذ', 'أجل', 'ثمّة', 'هَاتِه', 'لنا', 'تي', 'ولو', 'فلان', 'أى', 'تانِك', 'الذي', 'حاشا', 'هاتين', 'جميع', 'ذلكم', 'ثلاثين', 'له', 'ساء', 'تِه', 'ثامن', 'لكنما', 'كن', 'سقى', 'أرى', 'أنّى', 'ثماني', 'لات', 'هَذا', 'نَخْ', 'فلس', 'كم', 'ستمئة', 'آي', 'وهب', 'أم', 'أنتما', 'من', 'تلكما', 'والذي', 'نوفمبر', 'تلك', 'صهٍ', 'حين', 'ها', 'شيكل', 'أفٍّ', 'هَذانِ', 'تسعمائة', 'هذي', 'سبع', 'أيّان', 'أقل', 'هاته', 'بعدا', 'يورو', 'و', 'وإذا', 'إذا', 'خمسمئة']
def clean_text(text):
    text = "".join([word for word in text if word not in punc])
    text = remove_emoji(text)
    text = remove_diacritics(text)
    text = remove_urls(text)
    text = remove_emails(text)
    text = normalize_arabic(text)
    text = remove_english(text)
    tokens = text.split(' ')
    text = ' '.join([word for word in tokens if word not in stopWord])
    return text

######################################
def clean_textNE(text):
    punctuation=''.join(['!', '"', '#', '$', '%', '&', "'", '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'])
    text = "".join([word for word in text if word not in punctuation])
    text = re.sub('\[.*?\]', '', text)
    text = re.sub('https?://\S+|www\.\S+', '', text)
    text = re.sub('<.*?>+', '', text)
    text = re.sub('[%s]' % re.escape(punctuation), '', text)
    text = re.sub('\n', '', text)
    text = re.sub('\w*\d\w*', '', text)
    stopword=['whom', 'to', "it's", 'a', 'our', 'haven', 'with', 'where', 'as', 'if', 'its', 'during', 'in', 'then', 'couldn', 're', 'after', 'by', 'so', "shouldn't", "didn't", 'all', 'be', "hadn't", 'don', 'before', "hasn't", 'below', 'more', 'themselves', 'isn', 'while', 'or', 'them', "aren't", 'they', 'have', 'is', 'only', 'aren', 'an', 'above', 'yours', 'd', 'ma', 'his', 'was', "should've", 'and', 'her', 'other', 'itself', "doesn't", 'o', 'herself', 'too', "don't", 'both', 'just', 'that', 'himself', "couldn't", 'll', "needn't", 'most', 'few', 'through', 'nor', 'did', 'can', 'no', 'hadn', 'than', 'further', 'you', 'has', 'these', 'such', 'm', 'of', 'mustn', 'same', 'against', 'y', 'am', 'who', "mustn't", 'ourselves', 'we', 'ours', 'does', 'any', 'shan', "shan't", 'because', "you'll", "haven't", "isn't", 'at', 've', 'wouldn', 'over', 'weren', 'mightn', 'on', 'why', "you'd", 's', 'yourselves', 'didn', "weren't", 'doing', 'now', 'shouldn', 'been', 'will', "that'll", 'about', 'between', "wasn't", 'him', 'own', "you're", 'down', 'what', 'theirs', 'the', 'off', 'hasn', 'me', "mightn't", 'he', 'hers', 'which', 'this', 'there', 'under', 'until', 'into', 'ain', 'i', 'had', 'yourself', 'she', 'not', 'again', "won't", 'having', 'being', 'myself', 'from', 'those', 'should', 'when', 'my', 'but', 'were', 'each', 'do', 'up', 'here', "she's", 'it', 'some', 'how', 'wasn', 'very', 'once', "wouldn't", 't', 'your', 'are', 'their', 'doesn', 'needn', 'won', 'out', "you've", 'for']
    text = [word for word in text.split(' ') if word not in stopword]
    text=" ".join(text)
    return text



def predictComment(text,lang):
    if lang == 'AR':
        print('AR')
        tokenizerHate = join(dirname(__file__), 'saved_dictionaryAr.pkl')
        tokenizerHate =joblib.load(tokenizerHate)
        text=clean_text(text)
    else:
        print('EN')
        tokenizerHate = join(dirname(__file__), 'saved_dictionaryEn.pkl')
        tokenizerHate =joblib.load(tokenizerHate)
        text=clean_textNE(text)
    sp=text.split()
    seq=[]
    for i in sp:
        if i in tokenizerHate:
            seq.append(tokenizerHate[i])
    seq = [seq[::-1]]
    padded = [0]*(300-len(seq[0]))
    for i in range(len(seq[0])):
        padded.append(seq[0][i])
    return str([padded])







