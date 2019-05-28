'''
Created on 03 Aug 2018

@author: suresh
'''
from flask import Flask
from flask import jsonify
from flask import request
import json
from flask_cors import CORS, cross_origin
from WebSrv.TokenMgr import TokenMgr



app = Flask(__name__)
CORS(app, resources=r'/py/*')

@app.route('/emo/tokens',methods=['POST'])
def getTokens():
    text = request.json['text']
    o =  TokenMgr()
    toks = o.getTokens(text)
    res = json.dumps(toks)
    return jsonify({"tokens": res})

@app.route('/emo/extract',methods=['POST'])
def extract():
    tokens = request.json['tokens']
    print(tokens)
    toks = json.loads(tokens)
    o =  TokenMgr()
    ed,ef,et = o.extract(toks)
    print(ed)
    return jsonify({"ed": ed,"ef":ef,"et":et})

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5001)
