
###detect 50 words each using PPMC order 0 and show the detect result in a new window
import nltk 
from string import *
import os
import math
import cPickle as p
#from Gui import *

#the path of the test file's 
testfilepath='longtext.txt'
# choose the number of words for each piece
textlong=50

DECEPTIVE_DIST_PATH="deceptive_dist.dat"
TRUTH_DIST_PATH="truth_dist.dat"

def propesssinglefile(text):
    """process a single file"""
    #print filelocation
    corpus=text
    newlist=nltk.tokenize.wordpunct_tokenize(corpus) #tokenize the text
    neword=[word.lower() for word in newlist]#lower all the strings
    stemmer = nltk.PorterStemmer() #stem the words
    afterstem=[]
    for token in neword:
        afterstem.append(stemmer.stem(token))
    return afterstem

def getentropy(afterstem,fdist):
    """get the entropy of the test file"""
    Hentropy=0;
    total=fdist.N()
    escapecount=len(fdist)
    for i in afterstem:
        count=fdist[i]
        probability=float(count)/(total+escapecount)
        if (probability==0.0):
            probability=float(escapecount)/(total+escapecount)*1.0/(30000-escapecount)
        Hentropy=Hentropy-math.log(probability,2)
    return Hentropy

def testdata(text,deceptive_fdist,truth_fdist):
        afterstem=propesssinglefile(text)
        deceptive_entropy=getentropy(afterstem,deceptive_fdist)
        truth_entropy=getentropy(afterstem,truth_fdist)
        p1 = 0.6499
        p2 = 4.313

        #0.1044    0.2093    0.3126    0.4167    0.5225    0.6043    0.7152    0.8347    1.0222    4.2115
        
        import math
        from nltk.tokenize import word_tokenize
        import re
        nonPunct = re.compile('.*[A-Za-z0-9].*')
        wordList = word_tokenize(text)
        filtered = [w for w in wordList if nonPunct.match(w)]
        xdata_1 = deceptive_entropy/len(filtered)
        xdata_2 = truth_entropy / len(filtered)
        d = math.fabs(xdata_2- xdata_1*p1 - p2)/math.sqrt(p1*p1 + 1);
        score = 0
        if d < 0.1044:
            score = 1
        elif d < 0.2093 :
            score = 2
        elif d < 0.3126 :
            score = 3
        elif d < 0.4167 :
            score = 4 
        elif d < 0.5225 :
            score = 5
        elif d < 0.6043 :
            score = 6
        elif d < 0.7152 :
            score = 7
        elif d < 0.8347 :
            score = 8
        elif d < 1.0222 :
            score = 9
        else:
            score = 10
        if deceptive_entropy<truth_entropy:
            result='deceptive'
        else: result='truth'
        return result,score


def ppmc_detect(filepath):
    #load the dictionary
    f=file(DECEPTIVE_DIST_PATH)
    deceptive_fdist=p.load(f)
    ff=file(TRUTH_DIST_PATH)
    truth_fdist=p.load(ff)
    result,score=testdata(filepath,deceptive_fdist,truth_fdist)
    return result,score
      



