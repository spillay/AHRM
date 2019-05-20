import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)

from textmining.utilities import NLTK_PATH

import nltk

nltk.data.path.append(NLTK_PATH)

nltk_stopwords = nltk.corpus.stopwords.words('english')
nltk_sent_tokenizer = nltk.data.LazyLoader('tokenizers/punkt/english.pickle')



def tokenize_sentence_to_tokens_nltk(sentence):
    return nltk.word_tokenize(sentence)



def pos_tagging_nltk(tokens):
    return nltk.pos_tag(tokens)




def load_gutenberg_tokens():
    vocabulary = set()
    for fileid in nltk.corpus.gutenberg.fileids():
        words = nltk.corpus.gutenberg.words(fileid)
        vocabulary.update(words)

    return vocabulary



def load_webtext_tokens():
    vocabulary = set()
    for fileid in nltk.corpus.webtext.fileids():
        words = nltk.corpus.webtext.words(fileid)
        vocabulary.update(words)

    return vocabulary



def load_brown_tokens():
    vocabulary = set()
    for fileid in nltk.corpus.brown.categories():
        words = nltk.corpus.brown.words(categories=fileid)
        vocabulary.update(words)

    return vocabulary



def load_reuters_tokens():
    vocabulary = set()
    for fileid in nltk.corpus.reuters.fileids():
        words = nltk.corpus.reuters.words(fileid)
        vocabulary.update(words)

    return vocabulary



def load_inaugural_tokens():
    vocabulary = set()
    for fileid in nltk.corpus.inaugural.fileids():
        words = nltk.corpus.inaugural.words(fileid)
        vocabulary.update(words)

    return vocabulary
