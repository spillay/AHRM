import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)

import os
import math
import re
import datetime
import scipy
import numpy as np
import networkx as nx
import pandas as pd

#from matplotlib import pyplot as plt
plt = None

from textmining.linguistic import tokenize_sentence_stop_to_tokens_enhanced, translate_pos
from textmining.staticlib import pos_tagging_nltk, nltk_stopwords
from textmining.resourcelib import pos_names
from textmining.utilities import is_token_word, load_file_list_from_path, read_from_pickle_file, read_from_json_file, save_to_pickle_file, save_to_json_file
from textmining.plotlib import plotly_draw_stacked_lines, plotly_draw_scatter_samples, output_subplots

_DEBUG = False
# Generalized matrix operations:


def extract_basic_emotion_from_tokens(tokens, emotion_word_dict):
    """
    extract emotions from list of tokens using a work dictionary

    extract emotions

    Parameters
    ----------
    tokens : list of strings
        represents a tokenized text sample as list of text tokens, each token should not contain a space
    emotion_word_dict : dict
        emotional word bank, load from package json file

    Returns
    -------
    emotion_dict
        found emotions counted by occurrence {'Anger':1}
    emotion_found_dict
        found motion with associated tokens in the context {'Anger':['angry',]}
    emotion_target_dict
        found motion with target tokens in the word bank {'Anger':['angr*']}
    """
    emotions = list(emotion_word_dict.keys())
    text = ' '.join(tokens)
    text = ' ' + text + ' '
    text = text.lower()

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
            if not is_token_word(word_token):
                continue
            reg = r'\W({0})\W'.format(word)
            if word.endswith('*'):
                reg = r'\W({0}[a-z]+)\W'.format(word_token)
            rs = re.findall(reg, text)
            found_len = len(rs)
            count += found_len
            found.extend(rs)
            if found_len > 0:
                target.append(word)
        if count > 0:
            emotion_dict[e] = count
            emotion_found_dict[e] = found
            emotion_target_dict[e] = target

    return emotion_dict, emotion_found_dict, emotion_target_dict


def extract_basic_emotion(text, emotion_word_dict):
    """
    extract emotions from a text sample (string) using a work dictionary

    extract emotions

    Parameters
    ----------
    tokens : text sample, string
        a string that represents a text sample, not tokenized
    emotion_word_dict : dict
        emotional word bank, load from package json file

    Returns
    -------
    emotion_dict
        found emotions counted by occurrence {'Anger':1}
    emotion_found_dict
        found motion with associated tokens in the context {'Anger':['angry',]}
    emotion_target_dict
        found motion with target tokens in the word bank {'Anger':['angr*']}
    """
    tokens = tokenize_sentence_stop_to_tokens_enhanced(text)
    emotions = list(emotion_word_dict.keys())
    text = ' '.join(tokens)
    text = ' ' + text + ' '
    text = text.lower()

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
            if not is_token_word(word_token):
                continue
            reg = r'\W({0})\W'.format(word)
            if word.endswith('*'):
                reg = r'\W({0}[a-z]+)\W'.format(word_token)
            rs = re.findall(reg, text)
            found_len = len(rs)
            count += found_len
            found.extend(rs)
            if found_len > 0:
                target.append(word)
        if count > 0:
            emotion_dict[e] = count
            emotion_found_dict[e] = found
            emotion_target_dict[e] = target

    return emotion_dict, emotion_found_dict, emotion_target_dict

def sp_extract_extended_emotion(text):
    main_path = os.path.dirname(os.path.dirname(__file__))
    liwc_data = read_from_json_file(
        os.path.join(main_path, 'data', 'liwc2015.json')
    )
    twelve_emotions = read_from_json_file(
        os.path.join(main_path, 'data', 'twelve_emotions_liwc.json')
    )
    extended_emotion_dict = read_from_pickle_file(
        os.path.join(main_path, 'data', 'extended_emotions.pkl')
    )
    r = extract_extended_emotion(text, twelve_emotions, extended_emotion_dict)
    print(r)
    return r
def extract_extended_emotion(text, core_dict, extend_dict):
    """
    extract emotions from a text sample (string) using a work dictionary

    extract emotions

    Parameters
    ----------
    tokens : text sample, string
        a string that represents a text sample, not tokenized
    core_dict : dict
        emotional word bank, load from package json file
        same to emotion_word_dict in 'extract_basic_emotion'
    extend_dict : dict
        extended emotion work bank generated from thesaurus

    Returns
    -------
    emotion_dict
        found emotions counted by occurrence {'Anger':1}
    emotion_found_dict
        found motion with associated tokens in the context {'Anger':['angry',]}
    emotion_target_dict
        found motion with target tokens in the word bank {'Anger':['angr*']}
    """
    tokens = tokenize_sentence_stop_to_tokens_enhanced(text)
    key_tokens = []
    for t in tokens:
        if t not in nltk_stopwords:
            print(t)
            key_tokens.append(t)

    ed, ef, et = extract_basic_emotion_from_tokens(key_tokens, core_dict)
    #print(ed)
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


def merge_dict(dto, d):
    """
    merge two dictionaries as one

    Extended description of function.

    Parameters
    ----------
    dto : dictionary
        destination dictionary
    d : dictionary
        source dictionary

    Returns
    -------
    dictionary
        dto
    """
    for k in d:
        if k in dto:
            dto[k] += d[k]
        else:
            dto[k] = d[k]

    return dto


def prime_in_dict(d):
    """
    extract prime emotion from a dictionary of emotions

    Extended description of function.

    Parameters
    ----------
    d : dictionary
        emotion distribution
        {'Anger':4, 'Joy':6}
        or as probabilities
        {'Anger':0.4, 'Joy':0.6}

    Returns
    -------
    str
        prime emotion
        'Joy'
    """
    
    tuple_list = d.items()
    inverse = [(value, key) for key, value in tuple_list]
    max_count, max_key = max(inverse)
    return max_key


def normalize_emotion_count(emotion_dict, na_key='NA'):
    """
    normalize dictionary of emotion distribution

    normalize

    Parameters
    ----------
    emotion_dict : dictionary
        emotion distribution as count of occurrence
        {'Anger':4, 'Joy':6}
        can be empty as {}
    na_key : str
        emotion label when no emotion is detected

    Returns
    -------
    dictionary
        normalized distribution
        {'Anger':0.4, 'Joy':0.6}
        or for empty input {} output {'NA':1.0}
    """
    emotions = list(emotion_dict.keys())

    emotion_nomalized = {}
    emotion_sum = 0

    for e in emotions:
        emotion_sum += emotion_dict[e]

    if emotion_sum == 0:
        if na_key is None:
            emotion_nomalized = {}
        else:
            emotion_nomalized = {na_key:1.0}
    else:
        for e in emotions:
            emotion_nomalized[e] = float(emotion_dict[e])/float(emotion_sum)

    return emotion_nomalized


def extract_basic_emotion_series(text_list, emotion_word_dict):
    """
    extract emoitons from list of text samples

    extract emotions

    Parameters
    ----------
    text_list : list of strings
        Description of arg1
    emotion_word_dict : dict
        emotional word bank, load from package json file
        same to emotion_word_dict in 'extract_basic_emotion'

    Returns
    -------
    emotion_list: list of dict
        list of emotion dictionaries
        [{'Anger':4, 'Joy':6}, {'Anger':2, 'Joy':8}, {'Anger':8, 'Joy':2}]
    emotion_percent_list
        list of normalized emotion dictionaries
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]
    emotion_prime_list
        list of prime emotions as dictionaries
        [{'Joy':1}, {'Joy':1}, {'Anger':1}]
    emotion_prime_str_list
        list of prime emotions as strings
        ['Joy', 'Joy', 'Anger']
    """
    emotion_list = []
    emotion_percent_list = []
    emotion_prime_list = []
    emotion_prime_str_list = []

    for t in text_list:
        if _DEBUG:
            print(t)
        e, ef, et = extract_basic_emotion(t, emotion_word_dict)
        ep = normalize_emotion_count(e)
        eprime = prime_in_dict(ep)
        emotion_list.append(e)
        emotion_percent_list.append(ep)
        emotion_prime_list.append({eprime:1})
        emotion_prime_str_list.append(eprime)

    return emotion_list, emotion_percent_list, emotion_prime_list, emotion_prime_str_list


def extract_basic_emotion_series_dict_list(text_dict_list, email_key, email_index_key, emotion_word_dict):
    """
    extract emotions from email series

    with additional information

    Parameters
    ----------
    text_dict_list : list of dict
        list of text samples as dict
        [
            {
                'sender': 'mike',
                'email': 'Dear Alex, ...... Mike',
                'time': Datetime(2017,10,31,14,0,0,0),
                'day':Date(2017,10,31),
            }
        ]
    email_key : str
        key of email body in the dict
        'email'
    email_index_key: str
        key of email order in the dict, such as timestamp
        'time'
        timestamp should be as datetime format in python
    emotion_word_dict: dict
        emotional word bank, load from package json file
        same to emotion_word_dict in 'extract_basic_emotion'

    Returns
    -------
    index_list: list of data-type
        list of email sequence in order, like timestamp
        [
            Datetime(2017,10,24,17,0,0,0),
            Datetime(2017,10,25,11,0,0,0),
            Datetime(2017,10,26, 8,0,0,0),
            Datetime(2017,10,26,14,0,0,0),
        ]
    emotion_list: list of dict
        list of emotion dictionaries
        [{'Anger':4, 'Joy':6}, {'Anger':2, 'Joy':8}, {'Anger':8, 'Joy':2}]
    emotion_percent_list
        list of normalized emotion dictionaries
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]
    emotion_prime_list
        list of prime emotions as dictionaries
        [{'Joy':1}, {'Joy':1}, {'Anger':1}]
    emotion_prime_str_list
        list of prime emotions as strings
        ['Joy', 'Joy', 'Anger']
    """
    index_list = []

    emotion_list = []
    emotion_percent_list = []
    emotion_prime_list = []
    emotion_prime_str_list = []

    for t in text_dict_list:
        if _DEBUG:
            print(t)
        index_list.append(t[email_index_key])
        e, ef, et = extract_basic_emotion(t[email_key], emotion_word_dict)
        ep = normalize_emotion_count(e)
        eprime = prime_in_dict(ep)
        e[email_index_key] = t[email_index_key]
        ep[email_index_key] = t[email_index_key]
        emotion_list.append(e)
        emotion_percent_list.append(ep)
        emotion_prime_list.append({eprime:1.0, email_index_key:t[email_index_key]})
        emotion_prime_str_list.append(eprime)

    return index_list, emotion_list, emotion_percent_list, emotion_prime_list, emotion_prime_str_list


def extract_basic_emotion_group_series_dict_list(text_dict_list,
    email_key, email_index_key,
    emotion_word_dict):
    """
    extract emotions from email series

    with additional information

    Parameters
    ----------
    text_dict_list : list of dict
        list of text samples as dict
        [
            {
                'sender': 'mike',
                'email': 'Dear Alex, ...... Mike',
                'time': Datetime(2017,10,31,14,0,0,0),
                'day':Date(2017,10,31),
            }
        ]
    email_key : str
        key of email body in the dict
        'email'
    email_index_key: str
        key of email order in the dict, such as timestamp
        'time'
        timestamp should be as datetime format in python
    emotion_word_dict: dict
        emotional word bank, load from package json file
        same to emotion_word_dict in 'extract_basic_emotion'

    Returns
    -------
    index_list: list of data-type
        list of email sequence in order, like timestamp
        [
            Datetime(2017,10,24,17,0,0,0),
            Datetime(2017,10,25,11,0,0,0),
            Datetime(2017,10,26, 8,0,0,0),
            Datetime(2017,10,26,14,0,0,0),
        ] if 'email_index_key' is timestamp
        grouped/aggregated emotion index
        [
            Date(2017,10,24),
            Date(2017,10,25),
            Date(2017,10,26),
        ] if 'email_index_key' is date
    emotion_list: list of dict
        list of emotion dictionaries
        [{'Anger':4, 'Joy':6}, {'Anger':2, 'Joy':8}, {'Anger':8, 'Joy':2}]
    emotion_percent_list
        list of normalized emotion dictionaries
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]
    emotion_prime_list
        list of prime emotions as dictionaries
        [{'Joy':1}, {'Joy':1}, {'Anger':1}]
    emotion_prime_str_list
        list of prime emotions as strings
        ['Joy', 'Joy', 'Anger']
    """

    index_list = []
    emotion_list = []
    emotion_percent_list = []
    emotion_prime_list = []
    emotion_prime_str_list = []

    ed = {}
    ep = {}
    last_index = None

    for t in text_dict_list:
        if _DEBUG:
            print(t)

        if t[email_index_key] != last_index:
            if last_index is not None:
                index_list.append(last_index)
                ep = normalize_emotion_count(ed)
                eprime = prime_in_dict(ep)
                ed[email_index_key] = t[email_index_key]
                ep[email_index_key] = t[email_index_key]
                emotion_list.append(ed)
                emotion_percent_list.append(ep)
                emotion_prime_list.append({eprime:1.0, email_index_key:t[email_index_key]})
                emotion_prime_str_list.append(eprime)

            last_index = t[email_index_key]
            ed = {}
            ep = {}


            e, ef, et = extract_basic_emotion(t[email_key], emotion_word_dict)
            merge_dict(ed, e)


        else:
            e, ef, et = extract_basic_emotion(t[email_key], emotion_word_dict)
            merge_dict(ed, e)

    index_list.append(last_index)
    ep = normalize_emotion_count(ed)
    eprime = prime_in_dict(ep)
    ed[email_index_key] = t[email_index_key]
    ep[email_index_key] = t[email_index_key]
    emotion_list.append(ed)
    emotion_percent_list.append(ep)
    emotion_prime_list.append({eprime:1.0, email_index_key:t[email_index_key]})
    emotion_prime_str_list.append(eprime)

    return index_list, emotion_list, emotion_percent_list, emotion_prime_list, emotion_prime_str_list



def extract_basic_emotion_mix_series_dict_list(text_dict_list,
    email_key, email_index_key, email_group_index_key,
    emotion_word_dict):
    """
    extract emotions from email series and aggregate by a time unit

    with additional information, output both non-aggregated emotions and aggregated

    Parameters
    ----------
    text_dict_list : list of dict
        list of text samples as dict
        [
            {
                'sender': 'mike',
                'email': 'Dear Alex, ...... Mike',
                'time': Datetime(2017,10,31,14,0,0,0),
                'day':Date(2017,10,31),
            }
        ]
    email_key : str
        key of email body in the dict
        'email'
    email_index_key: str
        key of email order in the dict, such as timestamp
        'time'
        timestamp should be as datetime format in python
    email_group_index_key
        key of email aggregation, aggregated emotions with same group key
        e.g. 'day', or 'date'
        date should be as datetime.date format in python
    emotion_word_dict: dict
        emotional word bank, load from package json file
        same to emotion_word_dict in 'extract_basic_emotion'

    Returns
    -------
    index_list: list of data-type
        list of email sequence in order, like timestamp
        [
            Datetime(2017,10,24,17,0,0,0),
            Datetime(2017,10,25,11,0,0,0),
            Datetime(2017,10,26, 8,0,0,0),
            Datetime(2017,10,26,14,0,0,0),
        ]
    emotion_list: list of dict
        list of emotion dictionaries
        [{'Anger':4, 'Joy':6}, {'Anger':2, 'Joy':8}, {'Anger':8, 'Joy':2}]
    emotion_percent_list
        list of normalized emotion dictionaries
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]
    emotion_prime_list
        list of prime emotions as dictionaries
        [{'Joy':1}, {'Joy':1}, {'Anger':1}]
    emotion_prime_str_list
        list of prime emotions as strings
        ['Joy', 'Joy', 'Anger']

    g_index_list: list of data-type
        grouped/aggregated emotion index
        [
            Date(2017,10,24),
            Date(2017,10,25),
            Date(2017,10,26),
        ]
    g_emotion_list: list of dict
        aggregated list of emotion dictionaries, after aggregation
        [{'Anger':4, 'Joy':6}, {'Anger':2, 'Joy':8}, {'Anger':8, 'Joy':2}]
    g_emotion_percent_list
        list of normalized emotion dictionaries, after aggregation
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]
    g_emotion_prime_list
        list of prime emotions as dictionaries, after aggregation
        [{'Joy':1}, {'Joy':1}, {'Anger':1}]
    g_emotion_prime_str_list
        list of prime emotions as strings, after aggregation
        ['Joy', 'Joy', 'Anger']
    """

    index_list = []
    emotion_list = []
    emotion_percent_list = []
    emotion_prime_list = []
    emotion_prime_str_list = []

    g_index_list = []
    g_emotion_list = []
    g_emotion_percent_list = []
    g_emotion_prime_list = []
    g_emotion_prime_str_list = []

    ed = {}
    last_index = None

    for t in text_dict_list:
        if _DEBUG:
            print(t)

        if t[email_group_index_key] != last_index:
            if last_index is not None:
                gep = normalize_emotion_count(ed)
                geprime = prime_in_dict(gep)
                ed[email_group_index_key] = t[email_group_index_key]
                gep[email_group_index_key] = t[email_group_index_key]
                g_emotion_list.append(ed)
                g_emotion_percent_list.append(gep)
                g_emotion_prime_list.append({geprime:1.0, email_group_index_key:t[email_group_index_key]})
                g_emotion_prime_str_list.append(geprime)
                g_index_list.append(last_index)

            last_index = t[email_group_index_key]
            ed = {}
            e, ef, et = extract_basic_emotion(t[email_key], emotion_word_dict)
            merge_dict(ed, e)


            ep = normalize_emotion_count(e)
            eprime = prime_in_dict(ep)
            e[email_index_key] = t[email_index_key]
            ep[email_index_key] = t[email_index_key]
            index_list.append(t[email_index_key])
            emotion_list.append(e)
            emotion_percent_list.append(ep)
            emotion_prime_list.append({eprime:1.0, email_index_key:t[email_index_key]})
            emotion_prime_str_list.append(eprime)

        else:
            e, ef, et = extract_basic_emotion(t[email_key], emotion_word_dict)
            merge_dict(ed, e)

            ep = normalize_emotion_count(e)
            eprime = prime_in_dict(ep)
            e[email_index_key] = t[email_index_key]
            ep[email_index_key] = t[email_index_key]
            emotion_list.append(e)
            emotion_percent_list.append(ep)
            emotion_prime_list.append({eprime:1.0, email_index_key:t[email_index_key]})
            emotion_prime_str_list.append(eprime)

    gep = normalize_emotion_count(ed)
    geprime = prime_in_dict(gep)
    ed[email_group_index_key] = t[email_group_index_key]
    gep[email_group_index_key] = t[email_group_index_key]
    g_emotion_list.append(ed)
    g_emotion_percent_list.append(gep)
    g_emotion_prime_list.append({geprime:1.0, email_group_index_key:t[email_group_index_key]})
    g_emotion_prime_str_list.append(geprime)
    g_index_list.append(last_index)

    return (index_list, emotion_list, emotion_percent_list, emotion_prime_list, emotion_prime_str_list,
            g_index_list, g_emotion_list, g_emotion_percent_list, g_emotion_prime_list, g_emotion_prime_str_list)


def extract_extended_emotion_series_dict_list(text_dict_list,
    email_key, email_index_key,
    emotion_core_dict, emotion_extend_dict):
    """
    extract emotions from email series

    with additional information

    Parameters
    ----------
    text_dict_list : list of dict
        list of text samples as dict
        [
            {
                'sender': 'mike',
                'email': 'Dear Alex, ...... Mike',
                'time': Datetime(2017,10,31,14,0,0,0),
                'day':Date(2017,10,31),
            }
        ]
    email_key : str
        key of email body in the dict
        'email'
    email_index_key: str
        key of email order in the dict, such as timestamp
        'time'
        timestamp should be as datetime format in python
    emotion_core_dict: dict
        emotional word bank, load from package json file
        same to 'emotion_word_dict' in 'extract_basic_emotion'
    emotion_extend_dict: dict
        extended emotion work bank generated from thesaurus
        same as 'extend_dict' in 'extract_extended_emotion'

    Returns
    -------
    index_list: list of data-type
        list of email sequence in order, like timestamp
        [
            Datetime(2017,10,24,17,0,0,0),
            Datetime(2017,10,25,11,0,0,0),
            Datetime(2017,10,26, 8,0,0,0),
            Datetime(2017,10,26,14,0,0,0),
        ]
    emotion_list: list of dict
        list of emotion dictionaries
        [{'Anger':4, 'Joy':6}, {'Anger':2, 'Joy':8}, {'Anger':8, 'Joy':2}]
    emotion_percent_list
        list of normalized emotion dictionaries
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]
    emotion_prime_list
        list of prime emotions as dictionaries
        [{'Joy':1}, {'Joy':1}, {'Anger':1}]
    emotion_prime_str_list
        list of prime emotions as strings
        ['Joy', 'Joy', 'Anger']
    """
    index_list = []

    emotion_list = []
    emotion_percent_list = []
    emotion_prime_list = []
    emotion_prime_str_list = []

    for t in text_dict_list:
        if _DEBUG:
            print(t)
        index_list.append(t[email_index_key])
        e, ef, et = extract_extended_emotion(t[email_key], emotion_core_dict, emotion_extend_dict)
        ep = normalize_emotion_count(e)
        eprime = prime_in_dict(ep)
        e[email_index_key] = t[email_index_key]
        ep[email_index_key] = t[email_index_key]
        emotion_list.append(e)
        emotion_percent_list.append(ep)
        emotion_prime_list.append({eprime:1.0, email_index_key:t[email_index_key]})
        emotion_prime_str_list.append(eprime)

    return index_list, emotion_list, emotion_percent_list, emotion_prime_list, emotion_prime_str_list


def extract_extended_emotion_group_series_dict_list(text_dict_list,
    email_key, email_index_key,
    emotion_core_dict, emotion_extend_dict):
    """
    extract emotions from email series using extended thesaurus

    with additional information

    Parameters
    ----------
    text_dict_list : list of dict
        list of text samples as dict
        [
            {
                'sender': 'mike',
                'email': 'Dear Alex, ...... Mike',
                'time': Datetime(2017,10,31,14,0,0,0),
                'day':Date(2017,10,31),
            }
        ]
    email_key : str
        key of email body in the dict
        'email'
    email_index_key: str
        key of email order in the dict, such as timestamp
        'time'
        timestamp should be as datetime format in python
    emotion_core_dict: dict
        emotional word bank, load from package json file
        same to 'emotion_word_dict' in 'extract_basic_emotion'
    emotion_extend_dict: dict
        extended emotion work bank generated from thesaurus
        same as 'extend_dict' in 'extract_extended_emotion'

    Returns
    -------
    index_list: list of data-type
        grouped/aggregated emotion index
        [
            Date(2017,10,24),
            Date(2017,10,25),
            Date(2017,10,26),
        ]
    emotion_list: list of dict
        list of emotion dictionaries
        [{'Anger':4, 'Joy':6}, {'Anger':2, 'Joy':8}, {'Anger':8, 'Joy':2}]
    emotion_percent_list
        list of normalized emotion dictionaries
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]
    emotion_prime_list
        list of prime emotions as dictionaries
        [{'Joy':1}, {'Joy':1}, {'Anger':1}]
    emotion_prime_str_list
        list of prime emotions as strings
        ['Joy', 'Joy', 'Anger']
    """

    index_list = []
    emotion_list = []
    emotion_percent_list = []
    emotion_prime_list = []
    emotion_prime_str_list = []

    ed = {}
    last_index = None

    for t in text_dict_list:
        if _DEBUG:
            print(t)

        if t[email_index_key] != last_index:
            if last_index is not None:
                index_list.append(last_index)
                ep = normalize_emotion_count(ed)
                eprime = prime_in_dict(ep)
                ed[email_index_key] = t[email_index_key]
                ep[email_index_key] = t[email_index_key]
                emotion_list.append(ed)
                emotion_percent_list.append(ep)
                emotion_prime_list.append({eprime:1.0, email_index_key:t[email_index_key]})
                emotion_prime_str_list.append(eprime)

            last_index = t[email_index_key]
            ed = {}
            e, ef, et = extract_extended_emotion(t[email_key], emotion_core_dict, emotion_extend_dict)
            merge_dict(ed, e)

        else:
            e, ef, et = extract_extended_emotion(t[email_key], emotion_core_dict, emotion_extend_dict)
            merge_dict(ed, e)

    index_list.append(last_index)
    ep = normalize_emotion_count(ed)
    eprime = prime_in_dict(ep)
    ed[email_index_key] = t[email_index_key]
    ep[email_index_key] = t[email_index_key]
    emotion_list.append(ed)
    emotion_percent_list.append(ep)
    emotion_prime_list.append({eprime:1.0, email_index_key:t[email_index_key]})
    emotion_prime_str_list.append(eprime)

    return index_list, emotion_list, emotion_percent_list, emotion_prime_list, emotion_prime_str_list



def extract_extended_emotion_mix_series_dict_list(text_dict_list,
    email_key, email_index_key, email_group_index_key,
    emotion_core_dict, emotion_extend_dict):
    """
    extract emotions from email series using extended thesaurus

    with additional information

    Parameters
    ----------
    text_dict_list : list of dict
        list of text samples as dict
        [
            {
                'sender': 'mike',
                'email': 'Dear Alex, ...... Mike',
                'time': Datetime(2017,10,31,14,0,0,0),
                'day':Date(2017,10,31),
            }
        ]
    email_key : str
        key of email body in the dict
        'email'
    email_index_key: str
        key of email order in the dict, such as timestamp
        'time'
        timestamp should be as datetime format in python
    email_group_index_key: str
        key of email order in the dict for aggregation, such as date
        'date' or 'day'
        date should be as datetime.date format in python
    emotion_core_dict: dict
        emotional word bank, load from package json file
        same to 'emotion_word_dict' in 'extract_basic_emotion'
    emotion_extend_dict: dict
        extended emotion work bank generated from thesaurus
        same as 'extend_dict' in 'extract_extended_emotion'

    Returns
    -------
    index_list: list of data-type
        list of email sequence in order, like timestamp
        [
            Datetime(2017,10,24,17,0,0,0),
            Datetime(2017,10,25,11,0,0,0),
            Datetime(2017,10,26, 8,0,0,0),
            Datetime(2017,10,26,14,0,0,0),
        ]
    emotion_list: list of dict
        list of emotion dictionaries
        [{'Anger':4, 'Joy':6}, {'Anger':2, 'Joy':8}, {'Anger':8, 'Joy':2}]
    emotion_percent_list
        list of normalized emotion dictionaries
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]
    emotion_prime_list
        list of prime emotions as dictionaries
        [{'Joy':1}, {'Joy':1}, {'Anger':1}]
    emotion_prime_str_list
        list of prime emotions as strings
        ['Joy', 'Joy', 'Anger']

    g_index_list: list of data-type
        grouped/aggregated emotion index
        [
            Date(2017,10,24),
            Date(2017,10,25),
            Date(2017,10,26),
        ]
    g_emotion_list: list of dict
        aggregated list of emotion dictionaries, after aggregation
        [{'Anger':4, 'Joy':6}, {'Anger':2, 'Joy':8}, {'Anger':8, 'Joy':2}]
    g_emotion_percent_list
        list of normalized emotion dictionaries, after aggregation
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]
    g_emotion_prime_list
        list of prime emotions as dictionaries, after aggregation
        [{'Joy':1}, {'Joy':1}, {'Anger':1}]
    g_emotion_prime_str_list
        list of prime emotions as strings, after aggregation
        ['Joy', 'Joy', 'Anger']
    """

    index_list = []
    emotion_list = []
    emotion_percent_list = []
    emotion_prime_list = []
    emotion_prime_str_list = []

    g_index_list = []
    g_emotion_list = []
    g_emotion_percent_list = []
    g_emotion_prime_list = []
    g_emotion_prime_str_list = []

    ed = {}
    last_index = None

    for t in text_dict_list:
        if _DEBUG:
            print(t)

        if t[email_group_index_key] != last_index:
            if last_index is not None:
                gep = normalize_emotion_count(ed)
                geprime = prime_in_dict(gep)
                ed[email_group_index_key] = t[email_group_index_key]
                gep[email_group_index_key] = t[email_group_index_key]
                g_emotion_list.append(ed)
                g_emotion_percent_list.append(gep)
                g_emotion_prime_list.append({geprime:1.0, email_group_index_key:t[email_group_index_key]})
                g_emotion_prime_str_list.append(geprime)
                g_index_list.append(last_index)

            last_index = t[email_group_index_key]
            ed = {}
            e, ef, et = extract_extended_emotion(t[email_key], emotion_core_dict, emotion_extend_dict)
            merge_dict(ed, e)

            ep = normalize_emotion_count(e)
            eprime = prime_in_dict(ep)
            e[email_index_key] = t[email_index_key]
            ep[email_index_key] = t[email_index_key]
            index_list.append(t[email_index_key])
            emotion_list.append(e)
            emotion_percent_list.append(ep)
            emotion_prime_list.append({eprime:1.0, email_index_key:t[email_index_key]})
            emotion_prime_str_list.append(eprime)

        else:
            e, ef, et = extract_extended_emotion(t[email_key], emotion_core_dict, emotion_extend_dict)
            merge_dict(ed, e)

            ep = normalize_emotion_count(e)
            eprime = prime_in_dict(ep)
            e[email_index_key] = t[email_index_key]
            ep[email_index_key] = t[email_index_key]
            emotion_list.append(e)
            emotion_percent_list.append(ep)
            emotion_prime_list.append({eprime:1.0, email_index_key:t[email_index_key]})
            emotion_prime_str_list.append(eprime)

    gep = normalize_emotion_count(ed)
    geprime = prime_in_dict(gep)
    ed[email_group_index_key] = t[email_group_index_key]
    gep[email_group_index_key] = t[email_group_index_key]
    g_emotion_list.append(ed)
    g_emotion_percent_list.append(gep)
    g_emotion_prime_list.append({geprime:1.0, email_group_index_key:t[email_group_index_key]})
    g_emotion_prime_str_list.append(geprime)
    g_index_list.append(last_index)

    return (index_list, emotion_list, emotion_percent_list, emotion_prime_list, emotion_prime_str_list,
            g_index_list, g_emotion_list, g_emotion_percent_list, g_emotion_prime_list, g_emotion_prime_str_list)



def extract_basic_emotion_group_series(text_group_list, emotion_word_dict):
    """
    extract emotions from grouped list of text samples

    also normalize emotions

    Parameters
    ----------
    text_group_list : list of text samples
        [
            'text sample 1',
            'text sample 2',
            '...'
        ]
    emotion_word_dict : dict
        same to 'emotion_word_dict' in 'extract_basic_emotion'

    Returns
    -------
    ls: list
        list of emotion distribution as dictionaries, normalized sum is 1.0
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]
    """
    ls = []
    for t in text_group_list:
        d = {}
        merge_dict(d, extract_basic_emotion(t, emotion_word_dict))
        ls.append(normalize_emotion_count(d))
    return ls


def extract_prime_emotions(emotion_dict_list):
    """
    extract prime emotions from a list of emotion distributions

    each emotion distribution as a dict

    Parameters
    ----------
    emotion_dict_list : list of dict
        list of emotion distributions
        [{'Anger':0.4, 'Joy':0.6}, {'Anger':0.2, 'Joy':0.8}, {'Anger':0.8, 'Joy':0.2}]

    Returns
    -------
    ls: list of str
        each str is the prime emotion
        ['Joy', 'Joy', 'Anger']
    """
    ls = []
    for e in emotion_dict_list:
        p = prime_in_dict(e)
        ls.append(p)

    return ls


def extract_emotion_burst(prime_emotion_list):
    """
    extract emotional bursts from prime emotions

    prime emotions is a list of string

    Parameters
    ----------
    prime_emotion_list : list of str
        ['Joy', 'Joy', 'Anger']

    Returns
    -------
    running: list of (emotion, count)
        running length of emotions
        [
            ('Joy',   1),
            ('Anger', 0),
        ]
    """
    #print('extract_emotion_burst from prime list', prime_emotion_list)
    if len(prime_emotion_list) == 0:
        return

    length = len(prime_emotion_list)
    running = []

    if length == 0:
        return running

    elif length == 1:
        return [(prime_emotion_list[0], 0)]

    current_state_count = 0
    last = None

    #print('extract_emotion_burst length', length)
    for last,i in zip(prime_emotion_list[:length-1], prime_emotion_list[1:]):
        #print('extract_emotion_burst pair', last,i)
        if last == i:
            current_state_count += 1
        else:
            running.append((last, current_state_count))
            last = i
            current_state_count = 0
    running.append((last, current_state_count))

    #print('extract_emotion_burst running', running)
    return running


def extract_prime_emotion_count(prime_emotion_list, emotions):
    """
    count prime emotions

    count from the input list

    Parameters
    ----------
    prime_emotion_list : list of str
        list of prime emotions
        ['Joy', 'Joy', 'Anger']
    emotions : list/set of str/iteratable structure
        no duplicate, all possible emotions
        [
            'Relief'       ,
            'Contentment'  ,
            'Agreeableness',
            'Interest'     ,
            'Pride'        ,
            'Joy'          ,
            'Shame'        ,
            'Disgust'      ,
            'Fear'         ,
            'Anxiety'      ,
            'Sadness'      ,
            'Anger'        ,
            'NA'           ,
        ]

    Returns
    -------
    prime_emotion_dict: dict
        count of emotions for all emotions in the list/set
        {
            'Relief'        : 0,
            'Contentment'   : 0,
            'Agreeableness' : 0,
            'Interest'      : 0,
            'Pride'         : 0,
            'Joy'           : 2,
            'Shame'         : 0,
            'Disgust'       : 0,
            'Fear'          : 0,
            'Anxiety'       : 0,
            'Sadness'       : 0,
            'Anger'         : 1,
            'NA'            : 0,
        }
    """
    prime_emotion_dict = {}

    for e in emotions:
        prime_emotion_dict[e] = prime_emotion_list.count(e)
    return prime_emotion_dict



def extract_prime_emotion_percentage(prime_emotion_list, emotions):
    """
    count prime emotions and normalize as percentage, sum is 1.0

    count from the input list

    Parameters
    ----------
    prime_emotion_list : list of str
        list of prime emotions
        ['Joy', 'Joy', 'Anger']
    emotions : list/set of str/iteratable structure
        no duplicate, all possible emotions
        [
            'Relief'       ,
            'Contentment'  ,
            'Agreeableness',
            'Interest'     ,
            'Pride'        ,
            'Joy'          ,
            'Shame'        ,
            'Disgust'      ,
            'Fear'         ,
            'Anxiety'      ,
            'Sadness'      ,
            'Anger'        ,
            'NA'           ,
        ]

    Returns
    -------
    prime_emotion_dict: dict
        percentage of emotions for all emotions in the list/set
        {
            'Relief'        : 0,
            'Contentment'   : 0,
            'Agreeableness' : 0,
            'Interest'      : 0,
            'Pride'         : 0,
            'Joy'           : 0.66,
            'Shame'         : 0,
            'Disgust'       : 0,
            'Fear'          : 0,
            'Anxiety'       : 0,
            'Sadness'       : 0,
            'Anger'         : 0.34,
            'NA'            : 0,
        }
    """
    s = len(prime_emotion_list)
    prime_emotion_dict = {}

    for e in emotions:
        prime_emotion_dict[e] = float(prime_emotion_list.count(e))/float(s)
    return prime_emotion_dict



def generate_emotion_markov_chain(from_to_list):
    """
    generate networkx graph for transitions between emotional states

    weighted directional graph

    Parameters
    ----------
    from_to_list : list of tuple pair
        [(from state, to state)], each state is a prime emotional state
        for ['Joy', 'Joy', 'Anger'], the list is
        [('Joy', 'Joy'), ('Joy', 'Anger')]

    Returns
    -------
    G: networkx graph
        the graph weighted as count of transitions
        {
            ('Joy', 'Joy')  : 1,
            ('Joy', 'Anger'): 1,
        }
    """
    # create networkx graph
    G=nx.DiGraph()

    relations = set(from_to_list)
    weighted_edges = []
    labels = {}

    for r in relations:
        from_node, to_node = r
        weight = from_to_list.count(r)
        weighted_edges.append((from_node, to_node, weight))
        labels[r] = weight

    G.add_weighted_edges_from(weighted_edges)
    return G


def steady_emotion_state(prime_emotion_list):
    """
    compute steady states from prime emotional states

    steady states is a distribution, using networkx lib

    Parameters
    ----------
    prime_emotion_list : list of str
        list of prime emotions
        ['Joy', 'Joy', 'Anger']

    Returns
    -------
    pr: dict
        steady states, a distribution of emotions
        {'Joy': 0.5, 'Anger': 0.5}
    """
    emotion_length = len(prime_emotion_list)

    first_s = prime_emotion_list[0:emotion_length-1]
    second_s = prime_emotion_list[1:emotion_length]

    state_map = list(zip(first_s, second_s))

    G = generate_emotion_markov_chain(state_map)

    pr = nx.pagerank(G)

    tm = nx.google_matrix(G)
    #print(tm)

    return pr


def get_adjancent_matrix(state_list):
    """
    compute adjancent matrix with list of states

    list of states as prime emotional states

    Parameters
    ----------
    state_list : list of str
        list of prime emotions
        ['Joy', 'Joy', 'Anger']

    Returns
    -------
    adjancent_matrix: dict of dict
        first key as source state
        second key as dest state
        {'Joy': {'Joy':1, 'Anger':1}}
    adjancent_matrix_df: pandas.DataFrame
        convert adjancent_matrix to pandas.DataFrame
        row index as source state
        column index as dest state
    states: list
        available emotional states
    """
    states = list(set(state_list))
    states.sort()

    length = len(state_list)
    pairs = list(zip(state_list[0:length-1], state_list[1:length]))

    items = list(set(pairs))
    items.sort()

    adjancent_matrix = {}

    for si in states:
        adjancent_matrix[si] = {}
        for sj in states:
            adjancent_matrix[si][sj] = 0

    for i in items:
        from_i, to_i = i
        adjancent_matrix[from_i][to_i] = pairs.count(i)

    #print(adjancent_matrix)

    adjancent_matrix_df = pd.DataFrame.from_dict(adjancent_matrix, orient='index')
    adjancent_matrix_df = adjancent_matrix_df[states]

    #print(adjancent_matrix_df)

    return adjancent_matrix, adjancent_matrix_df, states



def get_transition_matrix(adjancent_matrix_df, states):
    """
    convert adjancent matrix to transition matrix

    matrix is in numpy.ndarray format, without header
    normalize adjancent matrix from prime emotional states

    Parameters
    ----------
    adjancent_matrix_df : pandas.DataFrame
        obtained from 'get_adjancent_matrix'
    states : list of str
        emotional states, obtained from 'get_adjancent_matrix'

    Returns
    -------
    transition_matrix: numpy.ndarray
        transition matrix
    transition_matrix_df: pandas.DataFrame
        transition matrix
    """
    transition_matrix_df = adjancent_matrix_df.div(adjancent_matrix_df.sum(axis=1), axis=0)

    transition_matrix = transition_matrix_df.as_matrix(columns=states)

    return transition_matrix, transition_matrix_df




def get_steady_states_from_transition_p_matrix(transition_matrix, s = .85, maxerr = .0001):
    """
    compute steady states withn transition matrix

    Extended description of function.

    Parameters
    ----------
    transition_matrix: matrix representing state transitions
        transition_matrix[i,j] is a binary value representing a transition from state i to j.
    s: probability of following a transition. 1-s probability of teleporting
        to another state.
    maxerr: if the sum of pageranks between iterations is bellow this we will
            have converged.

    Returns
    -------
    float vector
        steady states
    """
    G = transition_matrix

    # number of source states
    n = G.shape[0]

    # transform G into markov matrix A
    A = scipy.sparse.csc_matrix(G,dtype=np.float)
    rsums = np.array(A.sum(1))[:,0]
    ri, ci = A.nonzero()
    A.data /= rsums[ri]

    # bool array of sink states
    sink = rsums==0

    # Compute pagerank r until we converge
    ro, r = np.zeros(n), np.ones(n)
    while np.sum(np.abs(r-ro)) > maxerr:
        ro = r.copy()
        # calculate each pagerank at a time
        for i in range(0,n):
            # inlinks of state i
            Ai = np.array(A[:,i].todense())[:,0]
            # account for sink states
            Di = sink / float(n)
            # account for teleportation to state i
            Ei = np.ones(n) / float(n)

            r[i] = ro.dot( Ai*s + Di*s + Ei*(1-s) )

    # return normalized pagerank
    return r/float(sum(r))



def get_steady_states_from_transition_matrix(M, d=0.85, v_quadratic_error=0.000001):
    """
    compute steady states withn transition matrix

    Extended description of function.

    Parameters
    ----------
    M: matrix representing state transitions
        M[i,j] is a binary value representing a transition from state i to j.
    s: probability of following a transition. 1-s probability of teleporting
        to another state.
    maxerr: if the sum of pageranks between iterations is bellow this we will
            have converged.

    Returns
    -------
    float vector
        steady states
    """
    # nc is equal to either dimension of M and the number of documents
    M = M.T
    nr, nc = M.shape
    v = np.random.rand(nc, 1)
    #v = np.array([[0.7060],
    #    [0.0318],
    #    [0.2769],
    #    [0.0462],
    #    [0.0971]])
    #print(v)
    vv = np.linalg.norm(v, 1)
    #print(vv)
    v = np.dot(v, 1.0/vv)   # This is now L1, not L2
    #print(v)
    last_v = np.ones((nc, 1)) * np.inf
    #print(last_v)
    M_hat = np.dot(d, M) + np.dot(((1 - d) / nc), np.ones((nc, nc)));
    #print(M_hat)
    error = np.inf
    #print(error)
    while error > v_quadratic_error :
        last_v = v
        #print(last_v)
        v = np.matmul(M_hat, v) # removed the L2 norm of the iterated PR
        #print(v)
        error = np.linalg.norm(v - last_v, 2)
        #print(error)
    #print(M_hat)
    return v



def get_steady_states_from_state_list(prime_str_list):
    """
    compute steady states

    upper level function

    Parameters
    ----------
    prime_str_list : list of str
        list of prime emotions
        ['Joy', 'Joy', 'Anger']

    Returns
    -------
    dict
        steady states
    """
    am, amdf, states = get_adjancent_matrix(prime_str_list)
    tm, tmdf = get_transition_matrix(amdf, states)
    steady = get_steady_states_from_transition_matrix(tm)
    steady = steady.flatten()
    steady_states = {}
    for n,v in zip(states,steady):
        steady_states[n] = round(v,5)

    return steady_states



def analyze_markov_chain_entropy_matrix_operation(prime_str_list, precision=5):
    """
    compute entropy with list of prime emotional states

    upper level function

    Parameters
    ----------
    prime_str_list : list of str
        list of prime emotions
        ['Joy', 'Joy', 'Anger']
    precision : int
        number of decimals

    Returns
    -------
    tm: numpy.ndarray
        transition matrix
    tmdf: pandas.DataFrame
        transition matrix
    entropy: float
        entropy
    steady: dict of str:float
        steady states
    """
    #print(prime_str_list)
    am, amdf, states = get_adjancent_matrix(prime_str_list)
    tm, tmdf = get_transition_matrix(amdf, states)
    steady_array = get_steady_states_from_transition_matrix(tm)

    steady_array = steady_array.flatten()
    steady = {}
    for n,v in zip(states,steady_array):
        steady[n] = round(v,precision)

    #print(tmdf)
    #print(steady)

    entropy = 0.0
    for si in states:
        for sj in states:
            if si in steady:
                p = tmdf[sj][si]
                s = steady[si]
                if p > 0.0:
                    #print(si,sj,p,s)
                    entropy -= s*p*math.log(p)

    return tm, tmdf, entropy, steady



def analyze_markov_chain_conditional_entropy(prime_str_list, precision=5):
    """
    computer conditional entropy from a list of prime emotional states

    upper level function

    Parameters
    ----------
    prime_str_list : list of str
        list of prime emotions
        ['Joy', 'Joy', 'Anger']
    precision : int
        number of decimals

    Returns
    -------
    dict of dict as emotion: {emotion: float}
        contitional entropy
    }
    """
    am, amdf, states = get_adjancent_matrix(prime_str_list)
    tm, tmdf = get_transition_matrix(amdf, states)
    steady_array = get_steady_states_from_transition_matrix(tm)

    probability = extract_prime_emotion_percentage(prime_str_list, list(set(prime_str_list)))

    steady_array = steady_array.flatten()
    steady = {}
    for n,v in zip(states,steady_array):
        steady[n] = round(v,precision)

    H = {}

    #print(tmdf)

    for ni in probability:
        H[ni] = {}
        for nj in probability:
            if tmdf[nj][ni] != 0:
                #print(ni, nj, tmdf[nj][ni])
                H[ni][nj] = tmdf[nj][ni] * math.log(probability[ni]/tmdf[nj][ni])

    return H



def construct_emotion_person_count_2d_dict(prime_emotion_dict_person_dict):
    """
    compute emotional matches between people

    prepare data for Colley's rating

    Parameters
    ----------
    prime_emotion_dict_person_dict : dict of dict
        {
            'mike': {'Anger': 0.5, 'Joy': 0.5},
            'judy': {'Anger': 0.2, 'Joy': 0.8},
        }

    Returns
    -------
    emotion_person_count_dict:
        Description of return value
    """
    emotion_person_count_dict = {}

    people = list(prime_emotion_dict_person_dict.keys())
    personal_emotion_dict = prime_emotion_dict_person_dict[people[0]]
    emotions = list(personal_emotion_dict.keys())

    for p in prime_emotion_dict_person_dict:
        emotion_dict = prime_emotion_dict_person_dict[p]
        for e in emotion_dict:
            value = emotion_dict[e]
            if e not in emotion_person_count_dict:
                emotion_person_count_dict[e] = {}
            emotion_person_count_dict[e][p] = value

    return emotion_person_count_dict


def generate_emotion_state_markov_chain(emotion_person_dist_dict, persons):
    """
    prepare networkx graph for Colley's rating

    produce emotional matches bertween people

    Parameters
    ----------
    emotion_person_dist_dict : dict of lists
        {
            'mike': ['Joy', 'Joy', 'Joy', 'Joy', 'Joy'],
            'judy': ['Joy', 'Joy', 'Joy', 'Anger', 'Anger', 'Joy', 'Anger'],
        }
    persons : list, no duplicate
        list of people

    Returns
    -------
    G: networkx graph
        nodes as people
        edges as an emotional match
        weight as score difference
    G_no_tie: networkx graph
        matches without ties
    count_edges: dict
        number of matches between two persons, depends on available emotional states
    count_edges_no_tie: dict
        number of matches between two persons, depends on available emotional states, without a tie
    count_ins: dict
        count of number of matches between two persons, depends on available emotional stateswins
    count_outs: dict
        count of loses
    """
    # create networkx graph
    G = nx.MultiDiGraph()
    G_no_tie = nx.MultiDiGraph()
    #G = nx.DiGraph()
    #G_no_tie = nx.DiGraph()

    count_edges = {}
    count_edges_no_tie = {}
    count_ins  = {}
    count_outs = {}

    # add relations
    weighted_edges = []
    none_zero_edges = []
    length_persons = len(persons)
    for i in range(length_persons-1):
        for j in range(i+1,length_persons):
            kfrom = persons[i]
            kto = persons[j]
            for emotion in emotion_person_dist_dict:
                e_from = 0.0
                e_to = 0.0
                if kfrom in emotion_person_dist_dict[emotion]:
                    e_from = emotion_person_dist_dict[emotion][kfrom]
                if kto in emotion_person_dist_dict[emotion]:
                    e_to = emotion_person_dist_dict[emotion][kto]
                if e_from == 0.0 or e_to == 0.0:
                    continue
                weight = e_from - e_to
                if weight == 0:
                    weighted_edges.append((kfrom, kto, weight))
                    map_key = (kfrom, kto)
                    if map_key in count_edges:
                        count_edges[map_key] += 1
                    else:
                        count_edges[map_key]  = 1

                    if kfrom in count_outs:
                        count_outs[kfrom] += 1
                    else:
                        count_outs[kfrom]  = 1
                    if kto in count_ins:
                        count_ins[kto] += 1
                    else:
                        count_ins[kto]  = 1

                elif weight > 0:
                    none_zero_edges.append((kfrom, kto, weight))
                    weighted_edges.append((kfrom, kto, weight))
                    map_key = (kfrom, kto)
                    if map_key in count_edges:
                        count_edges[map_key] += 1
                    else:
                        count_edges[map_key]  = 1
                    if map_key in count_edges_no_tie:
                        count_edges_no_tie[map_key] += 1
                    else:
                        count_edges_no_tie[map_key]  = 1

                    if kfrom in count_outs:
                        count_outs[kfrom] += 1
                    else:
                        count_outs[kfrom]  = 1
                    if kto in count_ins:
                        count_ins[kto] += 1
                    else:
                        count_ins[kto]  = 1
                else:
                    none_zero_edges.append((kto, kfrom, abs(weight)))
                    weighted_edges.append((kto, kfrom, abs(weight)))
                    map_key = (kto, kfrom)
                    if map_key in count_edges:
                        count_edges[map_key] += 1
                    else:
                        count_edges[map_key]  = 1
                    if map_key in count_edges_no_tie:
                        count_edges_no_tie[map_key] += 1
                    else:
                        count_edges_no_tie[map_key]  = 1

                    if kfrom in count_ins:
                        count_ins[kfrom] += 1
                    else:
                        count_ins[kfrom]  = 1
                    if kto in count_outs:
                        count_outs[kto] += 1
                    else:
                        count_outs[kto]  = 1

    G.add_weighted_edges_from(weighted_edges)
    G_no_tie.add_weighted_edges_from(none_zero_edges)

    #print('weighted edges')
    #print('-----'*12)
    #print(weighted_edges)
    #print('non-zero weighted edges')
    #print('-----'*12)
    #print(none_zero_edges)
    #print('-----'*12)

    #nx.draw_circular(G)
    #plt.show()

    #nx.draw_circular(G_no_tie)
    #plt.show()

    return G,G_no_tie, count_edges, count_edges_no_tie, count_ins, count_outs



def colley_side_vector(G_no_tie):
    """
    compute colley side vector, with emotional matches

    lower level function

    Parameters
    ----------
    G_no_tie : networkx graph
        computed from 'generate_emotion_state_markov_chain'

    Returns
    -------
    b: numpy vector
        Colley side vector
    p: numpy vector
        vector
    """
    keys = list(G_no_tie.nodes)
    print('nodes', keys)
    keys.sort()
    dimension = len(keys)
    print('b size', dimension)
    b = np.zeros((dimension,1))
    p = np.zeros((dimension,1))

    for i in range(dimension):
        win_ls = list(G_no_tie.out_edges(keys[i]))
        lose_ls = list(G_no_tie.in_edges(keys[i]))
        win = len(win_ls)
        lose = len(lose_ls)
        b[i,0] = 1 + 0.5 * (win - lose)
        win_scored = list(G_no_tie.out_edges(keys[i], data='weight'))
        lose_scored = list(G_no_tie.in_edges(keys[i], data='weight'))
        print('win_scored', win_scored)
        print('lose_scored', lose_scored)
        win_s = sum([x for kf,kt,x in win_scored])
        lose_s = sum([x for kf,kt,x in lose_scored])
        p[i,0] = win_s - lose_s
    return b, p


def massey_matrix(G, count_edges, count_ins, count_outs, using_graph=False):
    """
    compute massey matrix for Colley's rating

    low level function

    Parameters
    ----------
    G : networkx graph
        computed from 'generate_emotion_state_markov_chain'
    count_edges : dict
        computed from 'generate_emotion_state_markov_chain'
    count_ins : dict
        computed from 'generate_emotion_state_markov_chain'
    count_outs : dict
        computed from 'generate_emotion_state_markov_chain'
    using_graph : bool
        search values with graph, for test purpose
        for debug purpose

    Returns
    -------
    M
        Massey's matrix
    C
        Colley's matrix
    """
    #print(count_ins)
    #print(count_outs)

    start_time = datetime.datetime.now()

    keys = list(G.nodes)
    print('nodes', keys)
    keys.sort()
    dimension = len(keys)
    M = np.zeros((dimension,dimension))

    # add elements

    print(using_graph)

    if using_graph:
        M = np.zeros((dimension,dimension))
        edges = G.edges()
        for i in range(dimension):
            for j in range(i,dimension):
                if i == j:
                    M[i,j] = len(list(G.out_edges(keys[i])))+len(list(G.in_edges(keys[i])))
                else:
                    M[i,j] = 0 - (
                        len(G[keys[i]][keys[j]]) +
                        len(G[keys[j]][keys[i]])
                        )
                    M[j,i] = 0 - (
                        len(G[keys[j]][keys[i]]) +
                        len(G[keys[i]][keys[j]])
                        )

        np.savetxt('byGraph.out', M, delimiter=' ')

    #if using_graph:
    else:
        M = np.zeros((dimension,dimension))
        index_map = {}

        for i in range(dimension):
            item = keys[i]
            index_map[item] = i
            a = 0
            b = 0
            if item in count_ins:
                a = count_ins[item]
            if item in count_outs:
                b = count_outs[item]
            M[i,i] = a + b

        for map_key in count_edges.keys():
            from_p,to_p = map_key
            i = index_map[from_p]
            j = index_map[to_p]
            reverse_key = (to_p, from_p)

            if reverse_key in count_edges:
                M[i,j] = 0.0 - (count_edges[map_key] + count_edges[reverse_key])
                M[j,i] = 0.0 - (count_edges[reverse_key] + count_edges[map_key])
            else:
                M[i,j] = 0.0 - (count_edges[map_key])
                M[j,i] = 0.0 - (count_edges[map_key])

        np.savetxt('MbyDict.out', M, delimiter=' ')

    C = 2 * np.identity(dimension) + M
    np.savetxt('CbyDict.out', C, delimiter=' ')

    end_time = datetime.datetime.now()

    print('computing massey matrix with time', (end_time - start_time))

    return M, C


def emotion_colley_rating_prime_emotion_count(emotion_list_person_dict, persons=None):
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
    emotion_total = []

    if persons is None:
        persons = list(emotion_list_person_dict.keys())
        persons.sort()

    print('emotion_colley_rating_prime_emotion_count, construct matches')
    #print('-----'*12)
    #print(emotion_list_person_dict)

    for p in emotion_list_person_dict:
        emotion_list = emotion_list_person_dict[p]
        emotion_total.extend(emotion_list)
    emotion_total = set(emotion_total)

    prime_emotion_dict_person_dict = {}
    for p in emotion_list_person_dict:
        emotion_list = emotion_list_person_dict[p]
        prime_count = extract_prime_emotion_count(emotion_list, emotion_total)
        prime_emotion_dict_person_dict[p] = normalize_emotion_count(prime_count)
        #print(prime_emotion_dict_person_dict[p])

    emotion_person_count_dict = construct_emotion_person_count_2d_dict(prime_emotion_dict_person_dict)

    print('normalize emotions')
    #print('-----'*12)
    #print(emotion_person_count_dict)

    emotion_person_normalized_dict = {}
    for p in emotion_person_count_dict:
        emotion_person_normalized_dict[p] = normalize_emotion_count(emotion_person_count_dict[p])

    print('construct markov chain')
    #print('-----'*12)
    #print(emotion_person_normalized_dict)
    #print('-----'*12)

    colleyr = []
    masseyr = []

    G, G_no_tie, count_edges, count_edges_no_tie, count_ins, count_outs = generate_emotion_state_markov_chain(emotion_person_normalized_dict, persons)

    if G.number_of_nodes() < 2:
        print('G.number_of_nodes()', G.number_of_nodes())
        return colleyr, masseyr

    print('construct massey matrix')
    M,C = massey_matrix(G, count_edges, count_ins, count_outs)
    np.savetxt('PrimeM.out', M, delimiter=' ')
    np.savetxt('PrimeC.out', C, delimiter=' ')

    print('construct colley side vector')
    b,p = colley_side_vector(G_no_tie)

    rated_persons = list(G_no_tie.nodes)
    rated_persons.sort()

    nrc, ncc = C.shape
    nrm, ncm = M.shape

    print('Colley\'s rating for prime emotions C', type(C), C.shape)
    print('Colley\'s rating for prime emotions M', type(M), M.shape)

    if nrc * ncc != 0:
        try:
            print('solving linear equation for Colley')
            colleyr = np.linalg.solve(C,b)
            print('solved linear equation for Colley')
            nx, ny = colleyr.shape
            print('Colley matrix shape', nx, ny)
            colleyr = colleyr.reshape(nx)
            colleyr = list(zip(rated_persons[0:nx], colleyr))
        except:
            print('no solution for colley rating')
            pass

    if nrm * ncm != 0:
        try:
            print('solving linear equation for Massey')
            masseyr = np.linalg.solve(M,p)
            print('solved linear equation for Massey')
            nx, ny = masseyr.shape
            print('Massey matrix shape', nx, ny)
            masseyr = masseyr.reshape(nx)
            masseyr = list(zip(rated_persons[0:nx], masseyr))
        except:
            print('no solution for massey rating')
            pass

    #print(colleyr)
    #print(masseyr)
    return colleyr, masseyr





def emotion_colley_rating_pagerank(person_pagerank_dict, persons=None):
    """
    compute Colley's rating with steady states

    upper level function

    Parameters
    ----------
    emotion_list_person_dict : dict of dict
        dict as person: steady states as a dict
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

    print('emotion_colley_rating_pagerank, construct matches')

    emotion_person_count_dict = construct_emotion_person_count_2d_dict(person_pagerank_dict)

    if persons is None:
        persons = list(person_pagerank_dict.keys())
        persons.sort()

    print('normalize emotions')
    #print('-----'*12)
    #print(emotion_person_count_dict)

    emotion_person_normalized_dict = {}
    for p in emotion_person_count_dict:
        emotion_person_normalized_dict[p] = normalize_emotion_count(emotion_person_count_dict[p])

    #print('normalized emotion dict')
    #print('-----'*12)
    #print(emotion_person_normalized_dict)
    #print('-----'*12)

    print('construct markov chain')

    colleyr = []
    masseyr = []

    G, G_no_tie, count_edges, count_edges_no_tie, count_ins, count_outs = generate_emotion_state_markov_chain(emotion_person_normalized_dict, persons)

    if G.number_of_nodes() < 2:
        print('G.number_of_nodes()', G.number_of_nodes())
        return colleyr, masseyr

    print('construct massey matrix')

    # G, count_edges, count_ins, count_outs, using_graph
    M,C = massey_matrix(G, count_edges, count_ins, count_outs)
    np.savetxt('SteadyM.out', M, delimiter=' ')
    np.savetxt('SteadyC.out', C, delimiter=' ')

    print('construct colley side vector')

    b,p = colley_side_vector(G_no_tie)

    rated_persons = list(G_no_tie.nodes)
    rated_persons.sort()

    nrc, ncc = C.shape
    nrm, ncm = M.shape

    print('Colley\'s rating for steady states C', type(C), C.shape)
    print('Colley\'s rating for steady states M', type(M), M.shape)

    if nrc * ncc != 0:
        try:
            print('solving linear equation for Colley')
            colleyr = np.linalg.solve(C,b)
            print('solved linear equation for Colley')
            nx, ny = colleyr.shape
            print('Colley matrix shape', nx, ny)
            colleyr = colleyr.reshape(nx)
            colleyr = list(zip(rated_persons[0:nx], colleyr))
        except:
            print('no solution for colley rating')
            pass

    if nrm * ncm != 0:
        try:
            print('solving linear equation for Massey')
            masseyr = np.linalg.solve(M,p)
            print('solved linear equation for Massey')
            nx, ny = masseyr.shape
            print('Massey matrix shape', nx, ny)
            masseyr = masseyr.reshape(nx)
            masseyr = list(zip(rated_persons[0:nx], masseyr))
        except:
            print('no solution for massey rating')
            pass

    #print(colleyr)
    #print(masseyr)
    return colleyr, masseyr


def sp_app_extract_emotions(text_series):
    main_path = os.path.dirname(os.path.dirname(__file__))
    liwc_data = read_from_json_file(
        os.path.join(main_path, 'data', 'liwc2015.json')
    )
    twelve_emotions = read_from_json_file(
        os.path.join(main_path, 'data', 'twelve_emotions_liwc.json')
    )

    extended_emotion_dict = read_from_pickle_file(
        os.path.join(main_path, 'data', 'extended_emotions.pkl')
    )
    eedl, eel, eepl, eeprimel, eeprimestrl = (None, None, None, None, None)
    deedl, deel, deepl, deeprimel, deeprimestrl = (None, None, None, None, None)

    r = extract_extended_emotion_mix_series_dict_list(text_series,
            'email', 'time', 'date', twelve_emotions, extended_emotion_dict,
    )
    eedl, eel, eepl, eeprimel, eeprimestrl, deedl, deel, deepl, deeprimel, deeprimestrl = r

    d_df = {

            'extended_emotion_email_count'      :eedl,
            'extended_emotion_email'            :eel,
            'extended_emotion_email_percent'    :eepl,
            'extended_emotion_email_prime'      :eeprimel,
            'extended_emotion_email_prime_str'  :eeprimestrl,

            'extended_emotion_day_count'        :deedl,
            'extended_emotion_day'              :deel,
            'extended_emotion_day_percent'      :deepl,
            'extended_emotion_day_prime'        :deeprimel,
            'extended_emotion_day_prime_str'    :deeprimestrl,
    }
    print()
    return d_df
    
def app_extract_emotions(text_folder, file_pattern, output_folder, output_pattern, text_key, index_key, group_key, total_count):
    """
    detect emotions from text samples

    top work flow

    Parameters
    ----------
    text_folder     : text sample folders
    file_pattern    : file name prefix to choose files
    output_folder   : output to pickle format
    output_pattern  : add a prefix for outputs
    text_key        : dict key for email body
    index_key       : dict key for email order sequence, such as timestamp
    group_key       : dict key for time aggregation, such as date
    total_count     : list to store how many files been processed, record progress

    Returns
    -------
    progess messages:
        str
    """
    main_path = os.path.dirname(os.path.dirname(__file__))
    liwc_data = read_from_json_file(
        os.path.join(main_path, 'data', 'liwc2015.json')
    )
    twelve_emotions = read_from_json_file(
        os.path.join(main_path, 'data', 'twelve_emotions_liwc.json')
    )

    extended_emotion_dict = read_from_pickle_file(
        os.path.join(main_path, 'data', 'extended_emotions.pkl')
    )

    file_list = load_file_list_from_path(text_folder, '{0}*.pkl'.format(file_pattern))
    file_list.sort()

    file_count = len(file_list)

    if file_count == 0:
        msg = 'No file under dir'
        print(msg)
        return msg

    total_file_count = total_count[0]
    total_label_count = len(file_list)
    total_count[1] = total_label_count

    data_dict = {}

    start_point_time = datetime.datetime.now()
    end_point_time = datetime.datetime.now()

    index = 0
    progress_count = 0
    for file_name in file_list:
        index += 1

        text_series = read_from_pickle_file(file_name)
        print("----------------------------------->")
        print(text_series)
        file_label = os.path.basename(file_name)
        file_label = file_label.replace('.pkl', '')
        file_label = file_label.replace(file_pattern, '')
        end_point_time = datetime.datetime.now()
        yield 'labeling emotions for {0} {1}/{2} {3} ({4}/{5}) text documents, timing:{6}'.format(file_label, index, total_label_count, len(text_series), progress_count, total_file_count, (end_point_time - start_point_time))

        progress_count += len(text_series)

        eedl, eel, eepl, eeprimel, eeprimestrl = (None, None, None, None, None)
        deedl, deel, deepl, deeprimel, deeprimestrl = (None, None, None, None, None)

        r = extract_extended_emotion_mix_series_dict_list(
            text_series, text_key, index_key, group_key, twelve_emotions, extended_emotion_dict,
        )
        eedl, eel, eepl, eeprimel, eeprimestrl, deedl, deel, deepl, deeprimel, deeprimestrl = r

        d_df = {

            'extended_emotion_email_count'      :eedl,
            'extended_emotion_email'            :eel,
            'extended_emotion_email_percent'    :eepl,
            'extended_emotion_email_prime'      :eeprimel,
            'extended_emotion_email_prime_str'  :eeprimestrl,

            'extended_emotion_day_count'        :deedl,
            'extended_emotion_day'              :deel,
            'extended_emotion_day_percent'      :deepl,
            'extended_emotion_day_prime'        :deeprimel,
            'extended_emotion_day_prime_str'    :deeprimestrl,
        }

        data_dict[file_label] = d_df
        single_emotions_path = os.path.join(
            output_folder,
            '{0}{1}.pkl'.format(output_pattern, file_label)
        )
        print("*******************************************************")
        print(d_df)
        save_to_pickle_file(d_df, single_emotions_path)

    end_point_time = datetime.datetime.now()

    print('Task finished! time is', end_point_time - start_point_time)

    return index


def get_max_entropy(n):
    """
    compute maximum entropy for given number of emotions

    used for normalization

    Parameters
    ----------
    n : int
        number of emotions

    Returns
    -------
    float
        maximum entropy
    """
    return -math.pow(n, 2) * math.pow((1.0/n), 2) * math.log(1.0/n)


def get_negative_steady_states(steady_states):
    """
    get negative states from steady states

    six emotions for the twelve emotions state scheme

    Parameters
    ----------
    steady_states : dict


    Returns
    -------
    s: float
        probability for negative states
    """
    negative_emotions = [
        u'Anger', u'Anxiety',
        u'Disgust', u'Fear',
        u'Sadness', u'Shame',
    ]
    s = 0.0
    for e in negative_emotions:
        if e in steady_states:
            s += steady_states[e]
    return s


def get_negative_emotions(d):
    """
    select negative emotions, remove positive emotions

    negative emotion filter

    Parameters
    ----------
    d : dict
        emotion: value

    Returns
    -------
    dict
        keys be negative emotions only
    """
    negative_emotions = [
        u'Anger', u'Anxiety',
        u'Disgust', u'Fear',
        u'Sadness', u'Shame',
    ]
    dd = {}
    for e in d:
        if e in negative_emotions:
            dd[e] = d[e]
    return dd


def process_run_length(run_length):
    """
    compute running length for emotional bursts

    low level function

    Parameters
    ----------
    steady_states : list of tuple pairs
        [(emotion, run_length)]
        for prime emotions ['Joy', 'Joy', 'Joy', 'Joy', 'Anger', 'Joy', 'Joy']
        [
            ('Joy',   3),
            ('Anger', 0),
            ('Joy',   1),
        ]

    Returns
    -------
    d: dict of value
        maximum running length for each emotional state
        {
            'Joy'   :3,
            'Anger' :0,
        }
    dd: dict of list
        all running length in the list
        {
            'Joy'   :[1,3],
            'Anger' :[0],
        }
    negative_max: int
        maximum negative running length
    """
    negative_emotions = [
        u'Anger',   u'Anxiety',
        u'Disgust', u'Fear',
        u'Sadness', u'Shame',
    ]
    d = {}
    dd = {}
    for e,c in run_length:
        if e in d:
            if d[e] < c:
                d[e] = c
            dd[e].append(c)
        else:
            d[e] = c
            dd[e] = [c]

    negative_max = 0
    for e in negative_emotions:
        if e in d:
            if d[e] > negative_max:
                negative_max = d[e]

    return d, dd, negative_max


def process_prime_emotions(emotion_prime_str_list, prefix, emotion_dict, max_entropy):
    """
    Summary line.

    Extended description of function.

    Parameters
    ----------
    emotion_prime_str_list: list of strings
        prime emotions
    prefix:
        add info to csv
    emotion_dict:
        output
    max_entropy : int
        maximum entropy for normalization

    Returns
    -------
    emotions: list
        all keys
    steady:
        steady states
    dfn:
        transition matrix
    run_length_dict:
        running length for emotional bursts
    """
    df = None
    dfn = None
    entropy_score = None
    count_days = len(emotion_prime_str_list)

    prime_emotions_dict = extract_prime_emotion_percentage(emotion_prime_str_list, set(emotion_prime_str_list))

    df, dfn, entropy_score, steady = analyze_markov_chain_entropy_matrix_operation(emotion_prime_str_list)
    run_length = extract_emotion_burst(emotion_prime_str_list)
    #print('run_length:', run_length)
    max_run_length, run_length_dict, max_negative_run_length = process_run_length(run_length)

    if entropy_score is None:
        print('error processing:', emotion_prime_str_list)
        return []

    emotions = []

    for i in prime_emotions_dict:
        if i is None:
            print('prime list', emotion_prime_str_list)
        else:
            key = prefix + 'probability_' + i
            emotion_dict[key] = round(prime_emotions_dict[i], 4)
            emotions.append(key)

    for i in steady:
        if i is None:
            print('prime list', emotion_prime_str_list)
        else:
            key = prefix + 'steady_' + i
            emotion_dict[key] = round(steady[i], 4)
            emotions.append(key)

    for i in max_run_length:
        if i is None:
            print('prime list', emotion_prime_str_list)
            print('process_prime_emotions', max_run_length, prefix, i)
        else:
            key = prefix + 'max_run_length_' + i
            emotion_dict[key] = max_run_length[i]
            emotions.append(key)

    if entropy_score == 0.0:
        print('0 entropy:', steady, emotion_prime_str_list)
        #print(dfn)

    emotion_dict['entropy'] = entropy_score
    emotions.append('entropy')

    emotion_dict[prefix+'count'] = count_days
    emotions.append(prefix+'count')

    negative_probablity = get_negative_steady_states(prime_emotions_dict)
    emotion_dict[prefix+'negative_probablity'] = round(negative_probablity, 4)
    emotions.append(prefix+'negative_probablity')
    negative_steady_states = get_negative_steady_states(steady)
    emotion_dict[prefix+'negative_steady_states'] = round(negative_steady_states, 4)
    emotions.append(prefix+'negative_steady_states')

    emotion_dict[prefix+'max_negative_run_length'] = max_negative_run_length
    emotions.append(prefix+'max_negative_run_length')

    risk = 0
    if negative_steady_states > 0.15 or max_negative_run_length > 3:
        risk = 1

    emotion_dict['target_'+prefix+'risk'] = risk
    emotions.append('target_'+prefix+'risk')
    emotions.sort()

    return emotions, steady, dfn, run_length_dict


def app_analyze_emotions(text_folder, file_pattern, output_folder, output_pattern, csv_folder, colley=True):
    """
    deep emotion analytics, for steady states, entropy, emotional bursts

    top level workflow

    Parameters
    ----------
    text_folder: str
        folder location for labeled emotions
    file_pattern:
        file prefix
    output_folder:
        output location
    output_pattern:
        add file prefix when output
    csv_folder: str
        location for outputting csv file
    colley: bool
        True -- compute Colley's rating
        False -- do not compute Colley's rating

    Returns
    -------
    str
        progress
    """
    file_list = load_file_list_from_path(text_folder, '{0}*.pkl'.format(file_pattern))
    file_list.sort()

    if len(file_list) == 0:
        msg = 'No file under dir'
        print(msg)
        return msg

    start_point_time = datetime.datetime.now()
    end_point_time = datetime.datetime.now()

    emotion_data = []
    account_list = []
    emotion_columns = []

    max_entropy = get_max_entropy(13)

    steady_person_dict = {}
    eprime_person_dict = {}

    total_count = len(file_list)
    index = 0

    for file_name in file_list:
        index += 1
        emotions_series = read_from_pickle_file(file_name)
        file_label = os.path.basename(file_name)
        file_label = file_label.replace('.pkl', '')
        file_label = file_label.replace(file_pattern, '')
        yield('analyzing emotions for {0} {1}/{2}'.format(file_label, index, total_count))

        emotion_dict = {}

        emotion_dict['id'] = file_label

        emotion_prime_str_list = emotions_series['extended_emotion_email_prime_str']
        prefix = 'emotion_email_'
        #print('email prime list:', emotion_prime_str_list)
        cols, ss, tm, run_length_dict = process_prime_emotions(emotion_prime_str_list, prefix, emotion_dict, max_entropy)
        #print('email run length dict:', file_label, run_length_dict)
        emotion_columns.extend(cols)

        output_file_name = os.path.join(
            output_folder,
            '{0}{1}_email.csv'.format(output_pattern, file_label)
        )
        tm.to_csv(output_file_name, index=False)

        output_file_name = os.path.join(
            output_folder,
            '{0}{1}_email.json'.format(output_pattern, file_label)
        )
        save_to_json_file(run_length_dict, output_file_name)

        emotion_prime_str_list = emotions_series['extended_emotion_day_prime_str']
        prefix = 'emotion_day_'
        #print('daily prime list:', emotion_prime_str_list)
        cols, ss, tm, run_length_dict = process_prime_emotions(emotion_prime_str_list, prefix, emotion_dict, max_entropy)
        #print('day run length dict:', file_label, run_length_dict)
        emotion_columns.extend(cols)

        output_file_name = os.path.join(
            output_folder,
            '{0}{1}_day.csv'.format(output_pattern, file_label)
        )
        tm.to_csv(output_file_name, index=False)

        output_file_name = os.path.join(
            output_folder,
            '{0}{1}_day.json'.format(output_pattern, file_label)
        )
        save_to_json_file(run_length_dict, output_file_name)

        emotion_data.append(emotion_dict)

        steady_person_dict[file_label] = get_negative_emotions(ss)
        eprime_person_dict[file_label] = emotion_prime_str_list
        account_list.append(file_label)

    yield('computing Colley\'s rating')

    #print('app_analyze_emotions', steady_person_dict)

    colley_rating, massey_rating = ([], [])
    colley_rate_p, massey_rate_p = ([], [])

    if colley:
        colley_rating, massey_rating = emotion_colley_rating_pagerank(steady_person_dict)
        colley_rate_p, massey_rate_p = emotion_colley_rating_prime_emotion_count(eprime_person_dict, account_list)

    # colley_rating
    colley_rating_dict = {}
    colley_rating_score = []
    for e in colley_rating:
        p, c = e
        c = round(c, 4)
        colley_rating_dict[p] = c
        colley_rating_score.append(c)

    colley_rating_score.sort()

    cut_length_min = int(len(colley_rating_score) * 0.1)
    cut_length_max = int(len(colley_rating_score) - len(colley_rating_score) * 0.1)

    prefix = 'emotion_'
    for e in emotion_data:
        person_name = e['id']
        e['colley_steady_rating'] = 0.0
        if person_name in colley_rating_dict:
            e['colley_steady_rating'] = colley_rating_dict[person_name]
            e['colley_steady_ranking'] = colley_rating_score.index(colley_rating_dict[person_name]) + 1

            if e['colley_steady_ranking'] <= cut_length_min or e['colley_steady_ranking'] >= cut_length_max:
                e['target_'+prefix+'risk'] = 1

        else:
            e['target_'+prefix+'risk'] = 0

    # colley_rate_p
    colley_rating_dict = {}
    colley_rating_score = []
    for e in colley_rate_p:
        p, c = e
        c = round(c, 4)
        colley_rating_dict[p] = c
        colley_rating_score.append(c)

    colley_rating_score.sort()

    cut_length_min = int(len(colley_rating_score) * 0.1)
    cut_length_max = int(len(colley_rating_score) - len(colley_rating_score) * 0.1)

    for e in emotion_data:
        person_name = e['id']
        e['colley_prime_rating'] = 0.0
        if person_name in colley_rating_dict:
            e['colley_prime_rating'] = colley_rating_dict[person_name]
            e['colley_prime_ranking'] = colley_rating_score.index(colley_rating_dict[person_name]) + 1

    # exporting
    emotion_columns = list(set(emotion_columns))
    emotion_columns.sort()

    columns = ['colley_steady_rating', 'colley_steady_ranking', 'colley_prime_rating', 'colley_prime_ranking']
    columns.extend(emotion_columns)
    df = pd.DataFrame(emotion_data)
    df.fillna(0, inplace=True)
    df.set_index('id', inplace=True)
    df.index.names = ['Employee_ID']

    output_file_name = os.path.join(
            csv_folder,
            'emotion_analysis_{0}.csv'.format('results')
        )

    df.to_csv(output_file_name, columns=columns, index=True)

    end_point_time = datetime.datetime.now()
    print('Task finished! time is', end_point_time - start_point_time)

    return index



# mode = ['email', day', 'prime_email', 'prime_day'] : [True, True, True, True]
def app_plot_emotion_series(text_folder, file_pattern, output_folder, mode=[True, True, True, True]):
    """
    plot function

    generates line plots for labeled emotions

    Parameters
    ----------


    Returns
    -------

    """
    file_list = load_file_list_from_path(text_folder, '{0}*.pkl'.format(file_pattern))
    file_list.sort()
    total_count = len(file_list)
    index = 0

    extended_colors = {
        u'Relief':'#69CBDE',
        u'Contentment':'#7CD3F7',
        u'Agreeableness':'#66C07F',
        u'Interest':'#90D2C1',
        u'Pride':'#B589BD',
        u'Joy':'#478DCB',
        u'Shame':'#941A1D',
        u'Disgust':'#EE3B90',
        u'Fear':'#FFD579',
        u'Anxiety':'#935523',
        u'Sadness':'#F7931E',
        u'Anger':'#EE3624',
        u'NA':'#000000',
    }

    for file_name in file_list:
        index += 1
        emotions_series = read_from_pickle_file(file_name)

        file_label = os.path.basename(file_name)
        file_label = file_label.replace('.pkl', '')
        file_label = file_label.replace(file_pattern, '')

        yield('plotting labeled emotions for {0} {1}/{2}'.format(file_label, index, total_count))

        eedl        = emotions_series['extended_emotion_email_count']
        eel         = emotions_series['extended_emotion_email']
        eepl        = emotions_series['extended_emotion_email_percent']
        eeprimel    = emotions_series['extended_emotion_email_prime']
        eeprimestrl = emotions_series['extended_emotion_email_prime_str']

        deedl        = emotions_series['extended_emotion_day_count']
        deel         = emotions_series['extended_emotion_day']
        deepl        = emotions_series['extended_emotion_day_percent']
        deeprimel    = emotions_series['extended_emotion_day_prime']
        deeprimestrl = emotions_series['extended_emotion_day_prime_str']

        figures = []

        #print(file_label, 'emotion item length', len(eepl), len(eedl), len(eeprimel), len(deepl), len(deedl), len(deeprimel))

        #if len(deedl) == 1:
        #    print(file_label, eepl)
        #    print(file_label, eeprimel)
        #    print(file_label, deepl)
        #    print(file_label, deeprimel)

        #plotly_draw_pie_chart()
        s = plotly_draw_stacked_lines(
            eepl, extended_colors,
            eedl, 'Emotions',
            'Emotions from emails', file_label,
            offline=os.path.join(output_folder, file_label),#None,#
            to_div=True,
        )
        figures.append(s)

        s = plotly_draw_stacked_lines(
            deepl, extended_colors,
            deedl, 'Emotions',
            'Daily Emotions', file_label,
            offline=os.path.join(output_folder, file_label),#None,#
            to_div=True,
        )
        figures.append(s)

        s = plotly_draw_stacked_lines(
            eeprimel, extended_colors,
            eedl, 'Emotions',
            'Prime Emotions from emails', file_label,
            offline=os.path.join(output_folder, file_label),#None,#
            to_div=True,
        )
        figures.append(s)

        s = plotly_draw_stacked_lines(
            deeprimel, extended_colors,
            deedl, 'Emotions',
            'Daily Prime Emotions', file_label,
            offline=os.path.join(output_folder, file_label),#None,#
            to_div=True,
        )
        figures.append(s)

        output_subplots(figures, os.path.join(output_folder, 'analytics_'+file_label+'.html'))

    return index


def app_plot_analytics_results(text_folder, file_pattern, output_folder):
    """
    generate plots for emotional bursts

    plot function

    Parameters
    ----------


    Returns
    -------

    """
    file_list = load_file_list_from_path(text_folder, '{0}*.json'.format(file_pattern))
    file_list.sort()

    file_dict = {}

    for file_name in file_list:
        file_label = os.path.basename(file_name)
        file_label = file_label.replace(file_pattern, '')

        if file_label.endswith('_email.json'):
            file_label = file_label.replace('_email.json', '')
        elif file_label.endswith('_day.json'):
            file_label = file_label.replace('_day.json', '')

        #print('file label is', file_label)

        if file_label in file_dict:
            file_dict[file_label].append(file_name)
        else:
            file_dict[file_label] = [file_name]

    extended_colors = [
        (u'Relief'          ,   '#69CBDE'),
        (u'Contentment'     ,   '#7CD3F7'),
        (u'Agreeableness'   ,   '#66C07F'),
        (u'Interest'        ,   '#90D2C1'),
        (u'Pride'           ,   '#B589BD'),
        (u'Joy'             ,   '#478DCB'),
        (u'Shame'           ,   '#941A1D'),
        (u'Disgust'         ,   '#EE3B90'),
        (u'Fear'            ,   '#FFD579'),
        (u'Anxiety'         ,   '#935523'),
        (u'Sadness'         ,   '#F7931E'),
        (u'Anger'           ,   '#EE3624'),
        (u'NA'              ,   '#000000'),
    ]

    total_count = len(file_dict)
    index = 0

    file_names = list(file_dict.keys())
    file_names.sort()

    for file_name in file_names:
        index += 1

        #print('plot for data', file_name)

        file_list = file_dict[file_name]
        file_list.sort(reverse=True)

        yield('plotting emotional bursts for {0} {1}/{2}'.format(file_name, index, total_count))

        figures = []

        for fn in file_list:
            data = read_from_json_file(fn)
            #print('plot for data', file_name, os.path.basename(fn))

            title = 'Daily Running Length'

            if fn.endswith('_email.json'):
                title = 'Running Length for Emails'
            elif fn.endswith('_day.json'):
                title = 'Daily Running Length'

            s = plotly_draw_scatter_samples(
                data, extended_colors,
                title,
                None,
                offline=None, to_div=True
            )
            figures.append(s)

        output_subplots(figures, os.path.join(output_folder, 'analytics_'+file_name+'_burst'+'.html'))

    return index



def app_plot_csv_results(text_folder, file_pattern, output_folder):
    """
    plot information from the generated csv file

    plot function

    Parameters
    ----------


    Returns
    -------

    """
    file_list = load_file_list_from_path(text_folder, '*.csv'.format(file_pattern))
    file_list.sort()

    index = 0

    extended_colors = {
        'Relief'        :'#69CBDE',
        'Contentment'   :'#7CD3F7',
        'Agreeableness' :'#66C07F',
        'Interest'      :'#90D2C1',
        'Pride'         :'#B589BD',
        'Joy'           :'#478DCB',
        'Shame'         :'#941A1D',
        'Disgust'       :'#EE3B90',
        'Fear'          :'#FFD579',
        'Anxiety'       :'#935523',
        'Sadness'       :'#F7931E',
        'Anger'         :'#EE3624',
        'NA'            :'#000000',
    }

    rating_colors = {
        'prime_rating'  : '#FF00FF',
        'steady_rating' : '#00FF00',
    }

    risk_colors = {
        'day'  : '#FF00FF',
        'email' : '#00FF00',
    }

    entropy_color = {
        'entropy' : '#00FF00',
    }

    for fn in file_list:
        index += 1
        yield('plotting for {0}'.format(fn))

        df = pd.read_csv(fn)
        df.sort_index(inplace=True)
        df.set_index('Employee_ID', inplace=True)

        data = {}

        column_names = list(df)
        column_names.sort()
        for i in column_names:
            names = i.split('_')

            head = '_'.join(names[0:-1])
            tail = names[-1]

            if i.startswith('colley'):
                head = names[0]
                tail = '_'.join(names[1:])

            elif i.startswith('entropy'):
                head = i
                tail = i

            elif i.startswith('target'):
                head = '_'.join(names[0:2])
                tail = names[2]

            #print(head, tail)

            if head in data:
                data[head].append((tail, i))
            else:
                data[head] = [(tail, i)]

        figures = []

        heads = list(data.keys())
        heads.sort()

        for head in heads:
            ls = data[head]
            columns = list(zip(*ls))
            tags = list(columns[0])
            cols = list(columns[1])

            #print(head, tags)
            #print(head, cols)

            dfi = df[cols]

            plot_type = None
            color_map = extended_colors
            title = ''
            y_label = ''

            if 'colley' == head:
                plot_type = 'colley'
                color_map = rating_colors
                title = 'Colley\'s Rating'
                y_label = 'Colley\'s Rating'
            elif 'entropy' == head:
                plot_type = 'entropy'
                color_map = entropy_color
                title = 'Entropy of Emotions'
                y_label = 'Entropy'
            elif head.startswith('target'):
                plot_type = 'target'
                color_map = risk_colors
                title = 'Risk'
                y_label = 'Risk'
            if len(tags) > 0:
                if tags[0] in extended_colors:
                    plot_type = 'emotion'
                    color_map = extended_colors
                    head_info = head.split('_')
                    head_info = [i.title() for i in head_info]
                    if 'day_max_run_length' in head:
                        title = 'Daily Maximum Run Length'
                        y_label = 'Maximum Run Length'
                    elif 'day_probability' in head:
                        title = 'Daily Probabilities'
                        y_label = 'Probability'
                    elif 'day_steady' in head:
                        title = 'Daily Steady States Probabilities'
                        y_label = 'Probability'
                    elif 'email_max_run_length' in head:
                        title = 'Maximum Run Length for Emails'
                        y_label = 'Maximum Run Length'
                    elif 'email_probability' in head:
                        title = 'Probabilities from Emails'
                        y_label = 'Probability'
                    elif 'email_steady' in head:
                        title = 'Steady States Probabilities from Emails'
                        y_label = 'Probability'

            if plot_type is None:
                pass
            elif plot_type is 'colley':
                sort_keys = ['colley_prime_rating', 'colley_steady_rating', ]
                dfi = dfi[sort_keys]
                dfi.sort_values(sort_keys, ascending=False, inplace=True)
                dfi.rename(
                    index=str,
                    columns={
                        "colley_steady_rating"  : "steady_rating",
                        "colley_prime_rating"   : "prime_rating"
                    },
                    inplace=True,
                )

                #print('data size', dfi.shape)

                s = plotly_draw_stacked_lines(
                    dfi, color_map,
                    dfi.index.tolist(), y_label,
                    title, head,
                    offline=None,#
                    to_div=True,
                )
                figures.append(s)

            elif plot_type is 'entropy':
                sort_keys = ['entropy',]
                dfi = dfi[sort_keys]
                dfi.sort_values(sort_keys, ascending=False, inplace=True)

                s = plotly_draw_stacked_lines(
                    dfi, color_map,
                    dfi.index.tolist(), y_label,
                    title, head,
                    offline=None,#
                    to_div=True,
                )
                figures.append(s)

            elif plot_type is 'target':
                sort_keys = ['target_emotion_day_risk', 'target_emotion_email_risk', ]
                dfi = dfi[sort_keys]
                dfi.sort_values(sort_keys, ascending=False, inplace=True)
                dfi.rename(
                    index=str,
                    columns={
                        "target_emotion_day_risk"  : "day",
                        "target_emotion_email_risk"   : "email"
                    },
                    inplace=True,
                )

                #print('data size', dfi.shape)

                s = plotly_draw_stacked_lines(
                    dfi, color_map,
                    dfi.index.tolist(), y_label,
                    title, head,
                    offline=None,#
                    to_div=True,
                )
                figures.append(s)
            elif plot_type is 'emotion':
                col_map = {}
                for i, j in zip(cols, tags):
                    col_map[i] = j
                dfi.rename(
                    index=str,
                    columns=col_map,
                    inplace=True,
                )

                #print('data size', dfi.shape)

                s = plotly_draw_stacked_lines(
                    dfi, color_map,
                    dfi.index.tolist(), y_label,
                    title, head,
                    offline=None,
                    to_div=True,
                )
                figures.append(s)

        output_html_fn = os.path.basename(fn)
        output_html_fn = output_html_fn.replace('.csv', '.html')
        output_subplots(figures, os.path.join(output_folder, output_html_fn))

    return index


