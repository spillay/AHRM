'''
Created on 02 Aug 2018

@author: suresh
'''
from WebSrv.TokenMgr import TokenMgr
import datetime

def write_text_file(text, fn):
    f = open(fn, 'w')
    f.write(text)
    f.close()
    return

def getMesg(fn):
    f = open(fn, 'r')
    txt = f.read()
    f.close()
    return txt

def doall(text):
    #text = getMesg("mesg.txt")
    o =  TokenMgr()
    print('stopwords',o.getStopWords())
    starttok = datetime.datetime.now()
    toks = o.getTokens(text)
    endtok = datetime.datetime.now()
    tok = endtok-starttok
    write_text_file(toks.__str__(), "tokens.txt")
    print("Tokenize Time",tok.total_seconds())
    start = datetime.datetime.now()
    ed, ef, et = o.extract(toks)
    end = datetime.datetime.now()
    diff = end - start
    print("Emotion Extract",diff.total_seconds())
    print("Total Time",(tok+diff).total_seconds())
    print(ed)
    print(ef)
    print(et)

def doExtract():
    print("Extracting")
    f = open("tokens.txt", 'r')
    txt = f.read()
    f.close()
    toks = eval(txt)
    o =  TokenMgr()
    ed, ef, et = o.extract(toks)
    print(ed)
    print(ef)
    print(et)
    
if __name__ == '__main__':
    #doall()
    #doExtract()
    text = "i sure hope we make good use of the bad news about skilling's resignation and do some house cleaning -- can we write down some problem assets and unwind raptor"
    doall(text)
