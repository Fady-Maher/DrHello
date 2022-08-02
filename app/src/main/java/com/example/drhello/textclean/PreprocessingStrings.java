package com.example.drhello.textclean;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PreprocessingStrings {
    String[] closes = {"(",")","{","}"};
    String[] stopwords= {"a", "about", "above", "after", "again", "against", "ain", "all", "am", "an", "and", "any", "are",
            "aren", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between",
            "both", "but", "by", "can", "couldn", "couldn't", "d", "did", "didn", "didn't", "do", "does",
            "doesn", "doesn't", "doing", "don", "don't", "down", "during", "each", "few", "for", "from",
            "further", "had", "hadn", "hadn't", "has", "hasn", "hasn't", "have", "haven", "haven't",
            "having", "he", "her", "here", "hers", "herself", "him", "himself", "his", "how", "i",
            "if", "in", "into", "is", "isn", "isn't", "it", "it's", "its", "itself", "just", "ll",
            "m", "ma", "me", "mightn", "mightn't", "more", "most", "mustn", "mustn't", "my", "myself",
            "needn", "needn't", "no", "nor", "not", "now", "o", "of", "off", "on", "once", "only", "or",
            "other", "our", "ours", "ourselves", "out", "over", "own", "re", "s", "same", "shan", "shan't",
            "she", "she's", "should", "should've", "shouldn", "shouldn't", "so", "some", "such", "t",
            "than", "that", "that'll", "the", "their", "theirs", "them", "themselves", "then", "there",
            "these", "they", "this", "those", "through", "to", "too", "under", "until",
            "up", "ve", "very", "was", "wasn", "wasn't", "we", "were", "weren", "weren't",
            "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "won", "won't", "wouldn",
            "wouldn't", "y", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"};

    String[] punctuation = {"!", "#", "$", "%", "&", "\"" , "'", "(", ")", "*", "+", ",", "-", ".", "/", ":", ";",
            "<", "=", ">", "?", "@", "[", "\\", "]", "^", "_", "`", "{", "|", "}", "~"};

    Context context;
    public PreprocessingStrings(Context context) {
        this.context = context;

    }

    public String preprocessingEN(String oldstring){

        oldstring = oldstring.toLowerCase().trim();

        String[] strings = oldstring.split(" ");

        String outputs = "";
        for(int i =0 ; i<strings.length;i++){
            if(strings[i].startsWith("http")){
                continue;
            }else if(strings[i].equals("\n")){
                continue;
            }else if(strings[i].startsWith("[") && strings[i].endsWith("]")){

                continue;
            }else if(strings[i].equals(" ")){
                continue;
            }else if(strings[i].startsWith("<") && strings[i].endsWith(">")){

                continue;
            }else{
                Log.e("[i]:" , outputs+"    ");
                outputs = outputs.trim() + " " + strings[i];
            }
        }

        //to remove numbers
        for(int i =0;i<10;i++){
            outputs =  outputs.replace(i+"", "");
        }

        //to remove closes
        for(int i =0;i<closes.length;i++){
            outputs =  outputs.replace(closes[i], "");
        }

        //to remove punctuation
        for(int i =0;i<punctuation.length;i++){
            outputs =  outputs.replace(punctuation[i], "");
        }

        outputs = outputs.trim();

        return outputs;
    }
    public String preprocessingAR(String oldstring){

        String[] punctuation =  {" ّ"," َ"," ً"," ُ"," ٌ"," ِ"," ٍ"," ْ","ـ"};
        String[] EMOJI = {"\\U0001F600-\\U0001F64F", "\\U0001F300-\\U0001F5FF",  "\\U0001F680-\\U0001F6FF",  "\\U0001F1E0-\\U0001F1FF"};
        oldstring = oldstring.replaceAll("[a-zA-Z0-9]+","");
        String[] strings = oldstring.split(" ");
        String outputs = "";
        for(int i =0 ; i<strings.length;i++){
            if(strings[i].startsWith("http")){
                continue;
            }else if(strings[i].equals("\n")){
                continue;
            }else if(strings[i].startsWith("[") && strings[i].endsWith("]")){
                continue;
            }else if(strings[i].equals(" ")){
                continue;
            }else if(strings[i].startsWith("<") && strings[i].endsWith(">")){
                continue;
            }else if(strings[i].contains("@")){
                continue;
            }else{
                strings[i] = strings[i].replace("ا","ا");
                strings[i] = strings[i].replace("آ","ا");
                strings[i] = strings[i].replace("أ","ا");
                strings[i] = strings[i].replace("إ","ا");
                strings[i] = strings[i].replace("ى","ي");
                strings[i] = strings[i].replace("ؤ","ء");
                strings[i] = strings[i].replace("ئ","ء");
                strings[i] = strings[i].replace("ة","ه");
                strings[i] = strings[i].replace("گ","ك");
                strings[i] = strings[i].replace("[a-zA-Z]+","");
                outputs = outputs.trim() + " " + strings[i];
                Log.e("[i]:" , strings[i]+"    "+ i);
            }
        }

        //to remove closes
        for(int i =0;i<EMOJI.length;i++){
            outputs =  outputs.replace(EMOJI[i], "");
        }

        //to remove punctuation
        for(int i =0;i<punctuation.length;i++){
            outputs =  outputs.replace(punctuation[i], "");
        }

        return outputs;
    }

    public ArrayList<String> tokensAndStemmingAR(String outputs){
        String[] liststrings = outputs.split(" ");

        StandardTokenizer standardTokenizer = new StandardTokenizer(new StringReader(outputs));
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i = 0 ; i<liststrings.length ; i++){
            String token = standardTokenizer.getNextToken().toString();
            boolean flag= false;
            for(int l = 0 ; l< stopwords.length;l++){
                if(token.equals(stopwords[l])){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                arrayList.add(token);
            }
        }

        return arrayList;
    }

    public ArrayList<String> tokensAndStemming(String outputs){
        String[] liststrings = outputs.split(" ");

        StandardTokenizer standardTokenizer = new StandardTokenizer(new StringReader(outputs));
        StandardTokenizer standardTokenizer2 = new StandardTokenizer(new StringReader(outputs));
        TokenStream tokenStream = new PorterStemFilter(standardTokenizer2);
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i = 0 ; i<liststrings.length ; i++){
            Log.e("liststrings: " , liststrings[i]+" " + i);
        }

        for(int i = 0 ; i<liststrings.length ; i++){
            String token = standardTokenizer.getNextToken().toString();
            String tokenstreaming = null;
            try {
                tokenstreaming =  tokenStream.next().termText();
                Log.e("tokenstreaming: " , tokenstreaming+"    ");
            } catch (IOException e) {
                e.printStackTrace();
            }
            boolean flag= false;
            for(int l = 0 ; l< stopwords.length;l++){
                if(token.equals(stopwords[l])){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                arrayList.add(tokenstreaming);
            }
        }

        return arrayList;
    }

    private String JsonDataFromAsset(String name){
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open(name);
            int sizeOfFile  = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public float[] loadModel(ArrayList<String> arr,String name_json){
        float pad[] = new float[300];
        try {
            ArrayList<Float> res=new ArrayList<>();
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(JsonDataFromAsset(name_json)));
            HashMap map = new Gson().fromJson(jsonObject.toString(), HashMap.class);
            int j=0;
            for(int i=0;i<300;i++){
                if(arr.size()+i<300) {
                    pad[i] = 0;
                }
                else{
                    if(map.containsKey(arr.get(j))){
                        String m = map.get(arr.get(j)).toString();
                        pad[i]=Float.parseFloat(m);
                        j++;
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return pad;
    }

}
