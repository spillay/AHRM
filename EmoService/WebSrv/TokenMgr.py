'''
Created on 02 Aug 2018

@author: suresh
'''

from WebSrv.linguistic import tokenize_sentence_stop_to_tokens_enhanced
from WebSrv.staticlib import pos_tagging_nltk, nltk_stopwords
from WebSrv.utilities import read_from_json_file,read_from_pickle_file
from WebSrv.applications import extract_basic_emotion_from_tokens
from WebSrv.utilities import is_token_word
import re
import nltk

class TokenMgr(object):
    '''
    classdocs
    '''

    def __init__(self):
        '''
        Constructor
        '''
        print("Initialize TokenMgr")
        #self.tokens = tokenize_sentence_stop_to_tokens_enhanced(text)
        
    def getTokens(self,text):
        tokens = tokenize_sentence_stop_to_tokens_enhanced(text)
        key_tokens = []
        for t in tokens:
            if t not in nltk_stopwords:
                print(t)
                key_tokens.append(t)
                
        return key_tokens
    def getStopWords(self):
        sp_nltk_stopwords = nltk.corpus.stopwords.words('english')
        return sp_nltk_stopwords

    def extractbasic(self,tokens, emotion_word_dict):
        emotions = list(emotion_word_dict.keys())
        #print("emotions",emotions)
        
        text = ' '.join(tokens)
        text = ' ' + text + ' '
        text = text.lower()
        #print("text",text)
        # output
        emotion_dict = {}
        emotion_found_dict = {}
        emotion_target_dict = {}
    
        for e in emotions:
            wordlist = emotion_word_dict[e]
            count = 0
            found = []
            target = []
            for word in wordlist:
                word_token = word
                if word_token.endswith('*'):
                    word_token = word_token[0:-1]
                    #print("word_token",word_token,"word",word)
                if not is_token_word(word_token):
                    continue
                reg = r'\W({0})\W'.format(word)
                if word.endswith('*'):
                    reg = r'\W({0}[a-z]+)\W'.format(word_token)
                    #print("reg",reg)
                rs = re.findall(reg, text)
                found_len = len(rs)
                count += found_len
                #print("count",count)
                found.extend(rs)
                if found_len > 0:
                    target.append(word)
            if count > 0:
                emotion_dict[e] = count
                emotion_found_dict[e] = found
                emotion_target_dict[e] = target
    
        return emotion_dict, emotion_found_dict, emotion_target_dict
        
    def extract(self,key_tokens):
        core_dict = read_from_json_file("/Data/emo-store/dict-data/twelve_emotions_liwc.json")
        extend_dict = read_from_pickle_file("/Data/emo-store/dict-data/extended_emotions.pkl")
        ed, ef, et = self.extractbasic(key_tokens, core_dict)
        tagged_words = []
        for e in ef:
            tagged_words.extend(ef[e])
    
        filterd_tokens = [item for item in key_tokens if item not in tagged_words]
        for w in filterd_tokens:
            for e in extend_dict:
                if w in extend_dict[e]:
                    if e in ed:
                        ed[e] += 1
                    else:
                        ed[e]  = 1
                    if e in ef:
                        ef[e].append(w)
                    else:
                        ef[e] = [w]
                    if e in et:
                        et[e].append(w)
                    else:
                        et[e] = [w]
    
        return ed, ef, et
        
    def process(self,text):
        core_dict = read_from_json_file("/Data/emo-store/dict-data/twelve_emotions_liwc.json")
        extend_dict = read_from_pickle_file("/Data/emo-store/dict-data/extended_emotions.pkl")
        tokens = tokenize_sentence_stop_to_tokens_enhanced(text)
        key_tokens = []
        for t in tokens:
            if t not in nltk_stopwords:
                print(t)
                key_tokens.append(t)
    
        ed, ef, et = extract_basic_emotion_from_tokens(key_tokens, core_dict)
        print(ef)
        tagged_words = []
        for e in ef:
            tagged_words.extend(ef[e])
    
        filterd_tokens = [item for item in key_tokens if item not in tagged_words]
        for w in filterd_tokens:
            for e in extend_dict:
                if w in extend_dict[e]:
                    if e in ed:
                        ed[e] += 1
                    else:
                        ed[e]  = 1
                    if e in ef:
                        ef[e].append(w)
                    else:
                        ef[e] = [w]
                    if e in et:
                        et[e].append(w)
                    else:
                        et[e] = [w]
    
        return ed, ef, et
    
    def show(self):
        print(self.tokens)