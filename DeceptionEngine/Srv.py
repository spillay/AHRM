'''
Created on 06 Sep 2018

@author: suresh
'''
from flask import Flask
from flask import jsonify
from flask import request
import json
from flask_cors import CORS, cross_origin



app = Flask(__name__)
CORS(app, resources=r'/py/*')

@app.route('/deception/get',methods=['POST'])
def getDeception():
    text = request.json['text']
    from ppmc_partdetect import ppmc_detect
    result,tmp2 = ppmc_detect(text)
    return jsonify({"results": result,"score":tmp2})



if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5002)
