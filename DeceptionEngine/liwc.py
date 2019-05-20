import re
import os, glob
import threading
from scipy import stats, mgrid, c_, zeros
from numpy import median, array, abs
from math import pow


def cuecountsthread(cuename, cues, words, result):
    """
    This is a thread function for the :py:func:`cuecounts`. *cuename* is the name of the cue and each thread must
    have a unique/different cue name. *result* must be a dictionary object that will store the result. *cues* and *words*
    are the same as in :py:func:`cuecounts`.
    """
    result[cuename] = cuecounts(cues, words)


def cuecounts(cues, words):
    """
    Given a list of *words* and a list of cue words *cues* return the
    percentage of words matching the cue words.
    """
    count = 0
    for cue in cues:
        # For whatever reason I get an empty string. Probably a bug in loadcues(). Once we
        # move our data to the database we will be able to remove this check.
        if len(cue) == 0:
            continue

        # We have some redundant code here, but it improves performance about 2x.
        if cue[-1] == '*':
            # re.compile is slow it takes about 75% of total excution time
            regex = re.compile(cue)
            for word in words:
                match = regex.match(word)
                if match and match.group(0) == word:
                    count = count + 1
        else:
            for word in words:
                if word == cue:
                    count = count + 1
    return float(count) / len(words) * 100


def loadcues(path=None):
    """
    Construct a cue word dictionary from the files in the given *path*.
    """
    if path == None:
        path = os.path.dirname(os.path.abspath(__file__)) + "/cues/"
    cues = dict()
    for filename in glob.glob(os.path.join(path, '*.txt')):
        cuename = os.path.split(filename)[1]
        cuename = re.sub(r'\.txt$', '', cuename)
        f = open(filename, 'r')
        # Splitting \r\n because the files we have use windows/dos encoding
        cuewords = f.read().split('\r\n')
        f.close()
        cues[cuename] = cuewords
    return cues


def replaceandcount(text, symbols, replacement):
    """
    Replace every symbol in the *text* with a given *replacement* and return a tuple of
    a new text and the replacement count. *symbols* is a string of characters where each of
    them will be replaced with *replacement*.
    """
    counts = dict()
    text = list(text)
    r = range(len(symbols))
    for i in r:
        counts[symbols[i]] = 0

    r = range(len(text))
    for i in r:
        if text[i] in symbols:
            counts[text[i]] += 1
            text[i] = replacement
    return (''.join(text), counts)


def liwc(cues, text, use_threads=False):
    """
    Calculate cue values for a given *text*. *cues* is a dictionary where each key (cue name)
    maps to a list of cue words. If *use_threads* is set to *True* then the treaded version of
    :py:func:`cuecounts` will be used. The return value is a dictionary where each key is the
    cue name mapped to a cue value.
    """
    cuevalue = dict()
    (newtext, counts) = replaceandcount(text.lower(), '.,:;?!\-"(){}[]/~@#$%^&*+=\|><_', ' ')
    newtext = newtext.split()
    wordcount = len(newtext)

    if wordcount != 0:
        cuevalue['wc'] = float(wordcount)
        cuevalue['allpunc'] = float(sum(counts.values())) / wordcount * 100
        cuevalue['period'] = float(counts['.']) / wordcount * 100
        cuevalue['exclam'] = float(counts['!']) / wordcount * 100
        cuevalue['comma'] = float(counts[',']) / wordcount * 100
        cuevalue['qmark'] = float(counts['?']) / wordcount * 100
        cuevalue['colon'] = float(counts[':']) / wordcount * 100
        cuevalue['semic'] = float(counts[';']) / wordcount * 100
        cuevalue['dash'] = float(counts['-']) / wordcount * 100
        cuevalue['quote'] = float(counts['"']) / wordcount * 100
        cuevalue['parenthesis'] = float(counts['('] + counts[')']) / wordcount * 100

        # The following two counts aren't very efficient, but I couln't think of anything better.
        cuevalue['emoticons'] = float(text.count(':-)') + text.count(':-(')) / wordcount * 100
        # Search for '??'
        cuevalue['qmarks'] = float(text.count('??')) / wordcount * 100

        # Six letter
        tmp = 0
        for word in newtext:
            if len(word) > 6:
                tmp += 1
        cuevalue['sixltr'] = float(tmp) / wordcount * 100

        tmp = 0
        for i in '.!,?:;-"()':
            tmp += counts[i]
        cuevalue['otherp'] = cuevalue['allpunc'] - (float(tmp) / wordcount * 100) - cuevalue['emoticons'] - cuevalue['qmarks']

        tmp = counts['?'] + counts['!'] + counts['.']
        if tmp == 0:
            cuevalue['wps'] = wordcount
        else:
            cuevalue['wps'] = wordcount / tmp

        cuevalue['unique'] = float(len(set(newtext))) / wordcount * 100


        if use_threads:
            threads = []
            for cue in cues.keys():
                thread = threading.Thread(target=cuecountsthread, args=([cue, cues[cue], newtext, cuevalue]))
                thread.start()
                threads.append(thread)

            for thread in threads:
                thread.join()
        else:
            for cue in cues.keys():
                cuevalue[cue] = cuecounts(cues[cue], newtext)

        return cuevalue
    else:
        return None


def distribution_range(data):
    """
    Calculate min and max values for the given list of sample values (numbers) *data*.
    """
    a = 1.0 * array(data)
    n = len(a)
    min = a.min()
    max = a.max()
    med = median(a)
    sig = median(abs(a - med)) / 0.6745
    if sig <= 0:
        sig = max - min
    if sig > 0:
        u = sig * pow((4.0 / (3.0 * n)), 0.2)
    else:
        u = 1.0

    return (min - 3.0 * u), (max + 3.0 * u)


def makedistribution(cuevalues, bins=100):
    """
    Generates the probability density using *cuevalues*. *cuevalues* a dictionary
    (key (cue name) -> list of cue values) usually generated with :py:func:`liwc`
    and manually normalized. The result is a dictionary where each key (cue name)
    maps to a list of points and the corresponding values for each point. For example::

        {'wc' : {'points' : array([ -2.52481806, -2.25736142, -1.98990478, -1.72244813, ...]),
                 'values' : array([ 2.49737331e-04, 6.05512523e-04, 1.33479919e-03, ...])},
         ...
        }
    """
    #normalized = dict()
    #for cuevalue in cuevalues:
    #    for key in cuevalue.keys():
    #        if normalized.has_key(key):
    #            normalized[key].append(cuevalue[key])
    #        else:
    #            normalized[key] = []
    #            normalized[key].append(cuevalue[key])

    result = dict()
    #for key in normalized.keys():
    #    values = c_[normalized[key]]
    for key in cuevalues:
        values = c_[cuevalues[key]]
        min, max = distribution_range(values)
        points = c_[mgrid[min:max:complex(0, bins)].ravel()]
        result[key] = dict({'points' : points.ravel()})
        try:
            kernel = stats.kde.gaussian_kde(values.T)
            result[key]['values'] = kernel(points.T)
        except:
            result[key]['values'] = zeros(bins)

    return result
