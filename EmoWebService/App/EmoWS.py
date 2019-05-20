from flask import Flask
from flask import jsonify
from flask import request
import json
import os
from flask_cors import CORS, cross_origin

from textmining import appemotions
from emomodeling import *

app = Flask(__name__)
CORS(app, resources=r'/web/*')
#CORS(app)

@app.route('/emo/entropy',methods=['POST'])
def getEntropy():
    print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    print(request)
    text = request.json['text']
    print(text)
    ndata = json.loads(text)
    df_emo12 = pandas.DataFrame(ndata)
   
    col_sender = 'sender'
    col_date   = 'date'
    col_time   = 'time'
    col_neg    = 'neg'

    df_emo12_daily = df_emo12.groupby([col_sender,col_date]).sum()
    

    emo12_cols = list(df_emo12_daily)
    #emo12_cols.remove(col_neg)
    emo12_cols.sort()
    

    prime_df = construct_prime_df(df_emo12_daily, emo12_cols)

   
    guilty_players = ["sender"]
    prime_guilty_dict = {}
   

    guilty_prime_df = prime_df
    guilty_prime_df = guilty_prime_df.sort_values(by = ['date'])
    prime_guilty_dict["sender"] = guilty_prime_df['prime'].tolist()


    order_seq = emo12_cols+['NA']
    steady_guilty_df,entropy_guilty = compute_steady_and_entropy(guilty_players, prime_guilty_dict, order_seq)

    result = {"entropy": entropy_guilty,"steady": steady_guilty_df.to_json()}
    return jsonify(result)
    #return jsonify(entropy_guilty)

@app.route('/emo/getColley',methods=['POST'])
def extractColley(): 
    """
        compute Colley's rating with prime emotional states
    
        upper level function
    
        Parameters
        ----------
        emotion_list_person_dict : dict of lists
            dict as person: prime emotional states as a list
            {
                'mike': ['Joy', 'Joy', 'Joy', 'Joy', 'Joy'],
                'judy': ['Joy', 'Joy', 'Joy', 'Anger', 'Anger', 'Joy', 'Anger'],
                'jade': ['Joy', 'Joy', 'Joy', 'Anger','Joy', 'Joy'],
                'mark': ['Joy', 'Joy', 'Anger', 'Anger','Anger', 'Anger', 'Joy'],
            }
        persons : list of str
            emotion_list_person_dict.keys()
    
        Returns
        -------
        colleyr: numpy array
            Colley's rating
    
        masseyr
            Massey's rating
    """
    emo = request.json['emotionset']
    twelve_emotions = appemotions.read_from_json_file(
        os.path.join('', '/Data/emo-store/dict-data/', 'twelve_emotions_liwc.json')
    )
    res = appemotions.emotion_colley_rating_prime_emotion_count(emo, twelve_emotions)
    return jsonify(res)

@app.route('/emo/getburst',methods=['POST'])
def extractEMOBurst(): 
    emo = request.json['emotions']
    print(emo)
    res = appemotions.extract_emotion_burst(emo)
    return jsonify(res)

@app.route('/web/get',methods=['POST'])
def extractWebEMO(): 
    #dat = {
    #'text':request.json['text']
    #}
    #return request.data
    #print("in extract")
    text = request.json['text']
    #print(text)
    #main_path = os.path.join(os.path.dirname(__file__))
    #liwc_data = appemotions.read_from_json_file(
    #os.path.join(main_path, 'textmining/data', 'liwc2015.json')
    #)
    twelve_emotions = appemotions.read_from_json_file(
        os.path.join('', '/Data/emo-store/dict-data/', 'twelve_emotions_liwc.json')
    )
    
    extended_emotion_dict = appemotions.read_from_pickle_file(
        os.path.join('', '/Data/emo-store/dict-data/', 'extended_emotions.pkl')
    )
    ec, ep, ew = appemotions.extract_extended_emotion(text, twelve_emotions, extended_emotion_dict)
    cnt = 0
    for k in ec:
        cnt = 1
        
    if cnt == 1:
        prime = appemotions.prime_in_dict(ec)
        norm = appemotions.normalize_emotion_count(ec)
        
        print(ec)
        print(ep)
        print(ew)
        try:
            if prime is None:
                nprime = 'Unknown'
            else:
                nprime = json.dumps(prime)
        except NameError:
            print("name error")
            nprime = 'Unknown'
        try:
            if norm is None:
                norm = 'Unknown'
            else:
                nnorm = json.dumps(norm)
        except NameError:
            print("name error")
            nnorm = 'Unknown'
        res = {
            'ec': json.dumps(ec),
            'prime': nprime,
            'norm':nnorm
        }
    else:
        res = {
            'ec': json.dumps(ec),
            'prime':'Unknown',
            'norm':''
        }   
    return jsonify(res)

@app.route('/emo/get',methods=['POST'])
def extractEMO(): 
    text = request.json['text']
    #main_path = os.path.join(os.path.dirname(__file__))
    #liwc_data = appemotions.read_from_json_file(
    #os.path.join(main_path, 'textmining/data', 'liwc2015.json')
    #)
    twelve_emotions = appemotions.read_from_json_file(
        os.path.join('', '/Data/emo-store/dict-data/', 'twelve_emotions_liwc.json')
    )
    
    extended_emotion_dict = appemotions.read_from_pickle_file(
        os.path.join('', '/Data/emo-store/dict-data/', 'extended_emotions.pkl')
    )
    ec, ep, ew = appemotions.extract_extended_emotion(text, twelve_emotions, extended_emotion_dict)
    cnt = 0
    for k in ec:
        cnt = 1
        
    if cnt == 1:
        prime = appemotions.prime_in_dict(ec)
        norm = appemotions.normalize_emotion_count(ec)
        
        print(ec)
        print(ep)
        print(ew)
        try:
            if prime is None:
                nprime = 'Unknown'
            else:
                nprime = json.dumps(prime)
        except NameError:
            print("name error")
            nprime = 'Unknown'
        try:
            if norm is None:
                norm = 'Unknown'
            else:
                nnorm = json.dumps(norm)
        except NameError:
            print("name error")
            nnorm = 'Unknown'
        res = {
            'ec': json.dumps(ec),
            'prime': nprime,
            'norm':nnorm
        }
    else:
        res = {
            'ec': json.dumps(ec),
            'prime':'Unknown',
            'norm':''
        }   
    return jsonify(res)


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)
