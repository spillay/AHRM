import nltk
import math


def preprocess(text):
    """
    Return a list of words after stemming has been applied to the *text*.
    """
    text = nltk.tokenize.wordpunct_tokenize(text.lower())
    stemmer = nltk.PorterStemmer()
    #text = tokenize.wordpunct_tokenize(text.lower())
    #stemmer = PorterStemmer()
    result = []
    for word in text:
        result.append(stemmer.stem(word))
    return result


def getentropy(words, dist):
    """
    Calculate the entropy of the *words* for the given distribution *dist*. The
    distribution is a dictionary where each key maps to a number (i.e. word
    frequency).
    """
    entropy = 0
    total = 0
    for key in dist:
        total += dist[key]
    total = float(total)
    escapecount = float(len(dist))

    for word in words:
        try:
            count = float(dist[word])
        except:
            count = 0.0
        probability = count / (total + escapecount)
        if probability == 0.0:
            probability = escapecount / (total + escapecount) * 1.0 / (30000 - escapecount)
        entropy = entropy - math.log(probability, 2)
    return entropy


def isdeceptive(text, normal_dist, deceptive_dist):
    """
    Return true if the given text is deceptive. *normal_dist* is the word
    distribution (word -> number map) of non-deceptive training dataset, whereas
    *deceptive_dist* is the word distribution for deceptive dataset.
    """
    text = preprocess(text)
    normal_ent = getentropy(text, normal_dist)
    deceptive_ent = getentropy(text, deceptive_dist)
    if deceptive_ent < normal_ent:
        return True
    else:
        return False
