#!/bin/sh 
curl -i -H "Content-type: application/json" -X POST -d "{\"emotions\":\"[{'Agreeableness':0,'Anger':0,'Anxiety':0,'Contentment':0.5,'Disgust':0,'Fear':0,'Interest':0,'Joy':0.5,'Pride':0,'Relief':0,'Sadness':0,'Shame':0,'Word-Agreeableness':'','Word-Anger':'','Word-Anxiety':'','Word-Contentment':'','Word-Disgust':'','Word-Fear':'','Word-Interest':'','Word-Joy':'','Word-Pride':'','Word-Relief':'','Word-Sadness':'','Word-Shame':'','date':'2001-12-31','time':'2001-12-31 12:12:00','neg':0,'sender':'s..shively@enron.com'},{'Agreeableness':0,'Anger':0,'Anxiety':0,'Contentment':0,'Disgust':0,'Fear':0,'Interest':0,'Joy':0,'Pride':0,'Relief':0,'Sadness':0,'Shame':0,'Word-Agreeableness':'','Word-Anger':'','Word-Anxiety':'','Word-Contentment':'','Word-Disgust':'','Word-Fear':'','Word-Interest':'','Word-Joy':'','Word-Pride':'','Word-Relief':'','Word-Sadness':'','Word-Shame':'','date':'2002-01-01','time':'2002-01-01 12:12:00','neg':0,'sender':'s..shively@enron.com'},{'Agreeableness':0,'Anger':0,'Anxiety':0,'Contentment':0,'Disgust':0,'Fear':0,'Interest':0,'Joy':0,'Pride':0,'Relief':0,'Sadness':0,'Shame':0,'Word-Agreeableness':'','Word-Anger':'','Word-Anxiety':'','Word-Contentment':'','Word-Disgust':'','Word-Fear':'','Word-Interest':'','Word-Joy':'','Word-Pride':'','Word-Relief':'','Word-Sadness':'','Word-Shame':'','date':'2002-02-01','time':'2002-02-01 15:01:00','neg':0,'sender':'s..shively@enron.com'},{'Agreeableness':0,'Anger':0,'Anxiety':0,'Contentment':0,'Disgust':0,'Fear':0,'Interest':0,'Joy':0,'Pride':0,'Relief':0,'Sadness':0,'Shame':0,'Word-Agreeableness':'','Word-Anger':'','Word-Anxiety':'','Word-Contentment':'','Word-Disgust':'','Word-Fear':'','Word-Interest':'','Word-Joy':'','Word-Pride':'','Word-Relief':'','Word-Sadness':'','Word-Shame':'','date':'2002-03-01','time':'2002-03-01 15:01:00','neg':0,'sender':'s..shively@enron.com'}]\"}" http://localhost:9000/entropy
