import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)



import re

from textmining.resourcelib import latin_abbreviations, writing_abbreviations, split_rules, token_rules
from textmining.staticlib import nltk_stopwords, nltk_sent_tokenizer



def split_tokens(token):
    if token in latin_abbreviations:
        return [token, '.']
    if token in writing_abbreviations:
        return [token, '.']
    match = None
    for reg in split_rules:
        r = re.search(reg, token)
        if r is not None:
            match = r
    if match is None:
        return [token]
    rs = match.groups(0)
    return rs



def token_confirm(token):
    if len(token) == 1:
        return True
    if (token in latin_abbreviations) or (token in writing_abbreviations):
        return True

    match = None
    for reg in token_rules:
        r = re.search(reg, token)
        if r is not None:
            match = r
    if match is None:
        return False
    return True



def justify_tokens(tokens):
    length_token = len(tokens)
    token_list = []
    cut_index = length_token - 1
    for t in tokens[0:cut_index]:
        if not token_confirm(t):
            token_list.extend(split_tokens(t))
        else:
            token_list.append(t)
    token_list.extend(split_tokens(tokens[cut_index]))
    return token_list



def tokenize_linebreak_to_paragraph(article):
    paragraphs = article.split('\n')
    paragraph_tokens = []
    for para in paragraphs:
        paragraph_tokens.append(para.strip())
    return paragraph_tokens



def tokenize_sentence_stop_to_sentence(paragraph):
    sentences = nltk_sent_tokenizer.tokenize(paragraph)
    return sentences



def tokenize_word_stop_to_tokens(sentence):
    tokens = sentence.split()
    tokens = justify_tokens(tokens)
    return tokens



def tokenize_sentence_stop_to_tokens_enhanced(paragraph):
    ss = tokenize_sentence_stop_to_sentence(paragraph)
    tokens = []
    for s in ss:
        ts = tokenize_word_stop_to_tokens(s)
        tokens.extend(ts)
    return tokens



def tokenize_sentence_stop_to_tokens_nested(paragraph):
    ss = tokenize_sentence_stop_to_sentence(paragraph)
    setences = []
    for s in ss:
        ts = tokenize_word_stop_to_tokens(s)
        setences.append(ts)
    return setences



def remove_stopwords(token, stwds=nltk_stopwords):
    result = []
    for t in tokens:
        if t not in stwds:
            result.append(t)
    return result
