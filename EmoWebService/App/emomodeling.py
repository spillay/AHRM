import os
import os.path

import json
import pickle

import re
import math
import datetime

import numpy
import pandas
import scipy.sparse

import nltk
import spacy

from nltk.corpus import stopwords

import plotly
import plotly.plotly as py
import plotly.graph_objs as go
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot

stopwords_en = set(stopwords.words('english'))
nlp = spacy.load('en_core_web_lg')

cwd = os.getcwd()
print('current path: {0}'.format(cwd))

print('os path: {0}'.format(os.path.dirname(cwd)))
print('path {0}'.format(cwd))

enron_path = os.path.join(cwd, 'dataset', 'enron')
#enron_path = os.path.join(os.path.dirname(cwd), 'dataset', 'enron')
print('enron path: {0}'.format(enron_path))


def read_json_file(fn):
    f = open(fn, 'r')
    data = json.load(f)
    f.close()
    return data


def save_json_file(data, fn):
    f = open(fn, 'w')
    json.dump(
        data, f,
        sort_keys=True,
        indent=4,
        separators=(',', ': '),
    )
    f.close()


def read_pickle_file(fn):
    f = open(fn, 'rb')
    data = pickle.load(f)
    f.close()
    return data


def save_pickle_file(data, fn):
    f = open(fn, 'wb')
    pickle.dump(data, f, protocol=2)
    f.close()


def remove_errors_from_text(text):
    text = text.replace('=20\r\n', ' ')
    text = text.replace('=\r\n', '')
    text = text.replace('\r\n', ' ')
    text = text.replace('\n', ' ')
    return text


def process_text(df_text):
    data = []
    vocabulary = set([])

    for index,row in df_text.iterrows():
        text = remove_errors_from_text(row['email'])
        doc = nlp(text)
        sentences = []

        key_neg = 'neg'
        key_token = 'token'
        key_en = 'en'

        for s in doc.sents:
            sent = {key_neg:0, key_token:[]}
            for token in s:
                if token.is_alpha:
                    if token.pos_ in ['ADJ', 'ADV', 'NOUN', 'VERB']:
                        if token.is_stop:
                            pass
                        else:
                            sent[key_token].append(token.lemma_)
                            vocabulary.add(token.lemma_)
                        if token.dep_ == 'neg':
                            sent[key_neg] += 1

            sentences.append(sent)
        d = {'date':row['date'], 'sender':row['sender'], 'time':row['time']}
        d['text'] = sentences
        data.append(d)
    return data, vocabulary


def is_word(t):
    reg = r'[a-zA-Z]+'
    r = re.match(reg, t)
    if r is None:
        return False
    return True


def add_emotion(w, d, r, t):
    pre = 'Word-'

    if w not in d:
        return

    e = d[w]
    if e in r:
        r[e] += 1
        t[pre+e].append(w)
    else:
        r[e]  = 1
        t[pre+e] = [w]


def form_sentence_dataframe(data):
    email_sent_data = []
    for d in data:
        d_sender  = d['sender']
        d_date    = d['date']
        d_time    = d['time']
        sentences = d['text']
        for sent in sentences:
            d_neg = sent['neg']
            words = sent['token']
            d_emo = {}
            d_tgt = {}
            for w in words:
                add_emotion(w, enron_emo12_word_label, d_emo, d_tgt)
            for k in d_tgt:
                d_tgt[k] = ' '.join(d_tgt[k])
            d_emo.update(d_tgt)
            d_emo['sender'] = d_sender
            d_emo['date']   = d_date
            d_emo['time']   = d_time
            d_emo['neg']    = d_neg
            d_emo['text']   = sent.text

            email_sent_data.append(d_emo)

    return email_sent_data




def print_non_zeros(row, emo):
    s = ''
    for e in emo:
        if row[e] > 0:
            s += '{0}:{1} '.format(e, row[e])
    print(s)


def list_ties(df):
    total = df.shape[0]
    count_tie = 0
    list_tie = []

    for index,row in df.iterrows():
        emo12_data = row.tolist()
        maximum = max(emo12_data)
        count_max = emo12_data.count(maximum)
        if count_max > 1 and maximum > 0:
            count_tie += 1
            list_tie.append((maximum, count_max))

    return count_tie, total, list_tie



def find_prime_emotion(row, emo):
    emo12_data = row.tolist()
    maximum = max(emo12_data)
    maximum_dict = {}

    if maximum == 0.0:
        return 'NA'

    for e in emo:
        count_e = row[e]
        if count_e == maximum:
            maximum_dict[e] = count_e

    if len(maximum_dict) == 1:
        return list(maximum_dict.keys())[0]

    seq = [
        'Shame',  'Fear',  'Anger',    'Disgust',       'Sadness',     'Anxiety',
        'Relief', 'Pride', 'Interest', 'Agreeableness', 'Contentment', 'Joy',
    ]

    for i in seq:
        if i in maximum_dict:
            return i

    return 'NA'


def convert_date(s):
    date = s.split('-')
    date = [int(i) for i in date]
    return datetime.date(date[0], date[1], date[2],)



def construct_prime_df(df, emo12_cols):
    sender_list = []
    date_list   = []
    prime_list  = []

    for index,row in df.iterrows():
        prime = find_prime_emotion(row, emo12_cols)
        sender, date = index
        date = convert_date(date)

        sender_list.append(sender)
        date_list.append(date)
        prime_list.append(prime)

    prime_df = pandas.DataFrame({
        'sender': sender_list,
        'date':   date_list,
        'prime':  prime_list,
    })

    return prime_df


non_guilty_str = """Andy Zipper
Thomas Martin
Andrew Lewis
Jeffrey Shankman
Richard Sanders
Chris Dorland
Cooper Richey
Dana Davis
Danny McCarty
Daren Farmer
Drew Fossum
Mark Haedicke
Sandra Brawner
Fletcher Sturm
Frank Ermis
Hunter Shively
Jane Tholt
John Hodge
John Lavorato
Steven Kean
Sherron Watkins"""


guilty_str = """Wesley Colwell
Timothy Despain
Kevin P. Hannon
Rex Shelby
David Delainey
Jeffery Skilling
Kenneth Rice
Kenneth Lay
Andrew Fastow
Ben F. Glisan
Mark E. Koenig
Dan Boyle
Richard Alan Causey
James A. Brown
Timothy Belden
Paula Rieker
John Forney
Jeffrey Richter
Raymond Morris Bowen
Christopher Calger
Michael W. Krautz
Lawrence Lawyer             """


def get_person_list(list_str):
    persons = list_str.split('\n')
    persons = [s.strip() for s in persons]
    persons = [s.split()[-1] for s in persons]
    persons.sort()
    return persons

guilty_players = get_person_list(guilty_str)
nonguilty_managers = get_person_list(non_guilty_str)



def analyze_emotional_bursts_and_returns(ls):
    #print(ls)
    states = list(set(ls))
    states.sort()
    rt = {}
    ht = {}
    for si in states:
        rt[si] = []
        ht[si] = []

    s = pandas.Series(ls, range(1,len(ls)+1))
    for si in states:
        index_list = s[s == si].index
        index_list = index_list.tolist()
        if len(index_list) <= 1:
            continue

        #print(si, index_list)
        first = index_list[0]
        last  = index_list[0]
        for index in index_list[1:]:
            if index == last + 1:
                last = index
                #print('index =', index, 'move index last =', last, 'first remains as', first)
            else:
                lasting  = last - first
                interval = index - last
                if lasting > 0:
                    rt[si].append(lasting)
                ht[si].append(interval)
                #print('index =', index, 'first =', first, 'last =', last,
                #      'running =', lasting,
                #      'hitting =', interval)
                first = index
                last = index

        if last > first + 1:
            rt[si].append(last - first)

    return rt, ht



def calculate_mean_std(dict_ls):
    d = {}
    for e in dict_ls:
        ls = dict_ls[e]
        if len(ls) > 0:
            m = numpy.mean(ls)
            std = numpy.std(ls)
            d[e+' mean'] = m
            d[e+' std' ] = std
    return d



def formulate_bursts_and_returns(person_list, prime_dict):
    rt_list = []
    ht_list = []

    for p in person_list:
        rt, ht = analyze_emotional_bursts_and_returns(prime_dict[p])
        rti = calculate_mean_std(rt)
        hti = calculate_mean_std(ht)

        rti['sender'] = p
        hti['sender'] = p

        rt_list.append(rti)
        ht_list.append(hti)

    rt_df = pandas.DataFrame(rt_list)
    rt_df.set_index(['sender'], inplace=True)
    rt_df.fillna(0, inplace=True)
    rt_df = round(rt_df, 2)

    ht_df = pandas.DataFrame(ht_list)
    ht_df.set_index(['sender'], inplace=True)
    ht_df.fillna(0, inplace=True)
    ht_df = round(ht_df, 2)

    return rt_df, ht_df


def add_series_to_dict(series, key, data_type, r):
    d1 = {'group':key, 'category':data_type}
    d2 = {'group':key, 'category':data_type}
    for i,v in series.iteritems():
        info = i.split()
        emo, value = tuple(info)
        if value == 'mean':
            d1['data'] = value
            d1[emo] = v
        else:
            d2['data'] = value
            d2[emo] = v

    r.append(d1)
    r.append(d2)


def compare_bursts_and_returns_between_groups(group1, group2, group1_name, group2_name):
    rt1, ht1 = group1
    rt2, ht2 = group2
    rt1_mean = rt1.mean()
    ht1_mean = ht1.mean()
    rt2_mean = rt2.mean()
    ht2_mean = ht2.mean()

    rtht_overall = []
    add_series_to_dict(rt1_mean, group1_name, 'running times', rtht_overall)
    add_series_to_dict(ht1_mean, group1_name, 'hitting times', rtht_overall)
    add_series_to_dict(rt2_mean, group2_name, 'running times', rtht_overall)
    add_series_to_dict(ht2_mean, group2_name, 'hitting times', rtht_overall)

    rtht_overall_df = pandas.DataFrame(rtht_overall)
    rtht_overall_df.fillna(0, inplace=True)
    rtht_overall_df = round(rtht_overall_df, 3)
    return rtht_overall_df.loc[rtht_overall_df['data']=='mean'], rtht_overall_df.loc[rtht_overall_df['data']=='std']




def get_stochastic_matrix(prime_str_list, states):
    length = len(prime_str_list)
    pairs = list(zip(prime_str_list[0:length-1], prime_str_list[1:length]))

    items = list(set(pairs))
    items.sort()

    prime_states = set(prime_str_list)

    adjancent_matrix = []

    col_index = 'Emotion'
    for si in states:
        d = {col_index:si}
        for sj in states:
            d[sj] = 0
        adjancent_matrix.append(d)

    adjancent_matrix_df = pandas.DataFrame(adjancent_matrix)
    adjancent_matrix_df.set_index([col_index], inplace=True)
    adjancent_matrix_df = adjancent_matrix_df.loc[:,states]

    for i in items:
        from_i, to_i = i
        count = pairs.count(i)
        adjancent_matrix_df.loc[from_i,to_i] = count

    stochastic_matrix_df = adjancent_matrix_df.div(adjancent_matrix_df.sum(axis=1), axis=0)
    stochastic_matrix_df.fillna(0.0, inplace=True)
    stochastic_matrix_df = stochastic_matrix_df.loc[:,states]

    return adjancent_matrix_df, stochastic_matrix_df


def get_prime_entropy(states, stochastic_matrix_df, steady_states, normalize=True):
    number_of_states = len(states)
    max_entropy = -math.pow(number_of_states, 2) * math.pow((1.0/number_of_states), 2) * math.log(1.0/number_of_states)

    entropy = 0.0
    for si in states:
        for sj in states:
            if si in steady_states:
                p = stochastic_matrix_df.loc[sj,si]
                s = steady_states[si]
                if p > 0.0:
                    entropy -= s*p*math.log(p)

    if normalize:
        entropy = entropy/max_entropy

    return entropy



def get_steady_states(df, states, s=0.85, maxerr=0.000001):
    dfr = df.loc[(df != 0).any(axis=1), :]
    dfc = df.loc[:, (df != 0).any(axis=0)]

    row_items = dfr.index.tolist()
    col_items = list(dfc)

    col_select = row_items
    if len(row_items) < len(col_items):
        col_select = col_items

    dfx = df.loc[col_select, col_select]

    G = dfx.as_matrix(columns=col_select)
    nr, nc = G.shape

    # transform G into markov matrix A
    A = scipy.sparse.csc_matrix(G, dtype=numpy.float)
    rsums = numpy.array(A.sum(1))[:,0]
    ri, ci = A.nonzero()
    A.data /= rsums[ri]

    # bool array of sink states
    sink = rsums==0

    # Compute pagerank r until we converge
    ro, r = numpy.zeros(nr), numpy.ones(nr)
    while numpy.sum(numpy.abs(r-ro)) > maxerr:
        ro = r.copy()
        # calculate each pagerank at a time
        for i in range(0,nr):
            # inlinks of state i
            Ai = numpy.array(A[:,i].todense())[:,0]
            # account for sink states
            Di = sink / float(nr)
            # account for teleportation to state i
            Ei = numpy.ones(nr) / float(nr)

            r[i] = ro.dot( Ai*s + Di*s + Ei*(1-s) )

    # normalized pagerank
    v = r/float(sum(r))

    steady_states = {}
    for n,vi in zip(col_select,v):
        steady_states[n] = vi

    for s in states:
        if s not in steady_states:
            steady_states[s] = 0.0

    return dfx, steady_states




def compute_steady_and_entropy(person_list, prime_dict, states):
    steady_list = []
    entropy_list = []

    for p in person_list:
        ps = prime_dict[p]
        am, sm = get_stochastic_matrix(ps, states)
        dfx, steady = get_steady_states(sm, states)
        entropy = get_prime_entropy(states, sm, steady, normalize=False)
        steady['sender'] = p
        steady_list.append(steady)
        entropy_list.append(entropy)

    steady_df = pandas.DataFrame(steady_list)
    steady_df.set_index(['sender'], inplace=True)
    steady_df = round(steady_df, 3)

    return steady_df, entropy_list




def get_person_match(person_emo_dict, states):
    matches = []
    persons = list(person_emo_dict.keys())
    persons.sort()
    col_person = 'Person'
    for s in states:
        ds = []
        for prow in persons:
            dsrow = {col_person:prow}
            for pcol in persons:
                match = int(round(person_emo_dict[prow][s] * 100)) - int(round(person_emo_dict[pcol][s] * 100))
                dsrow[pcol] = match
            ds.append(dsrow)
        dsf = pandas.DataFrame(ds)
        dsf.set_index([col_person], inplace=True)
        matches.append(dsf)
    return matches, persons

def get_massey_colley_matrix_vector_rating(matches, persons, states):
    count_match = None
    massey_matrix = None

    number_of_person  = len(persons)
    number_of_emotion = len(states)

    if number_of_person < 3:
        return None, None, None, None, None, None

    I = pandas.DataFrame(numpy.identity(number_of_person), index=persons, columns=persons)

    massey_matrix = pandas.DataFrame((0 - number_of_emotion), index=persons, columns=persons)
    diag = (number_of_person * number_of_emotion) * I
    massey_matrix = massey_matrix + diag
    colley_matrix = 2 * I + massey_matrix
    massey_matrix.loc[persons[-1],:] = 1

    scores = pandas.DataFrame(0, index=persons, columns=persons)
    counts = pandas.DataFrame(0, index=persons, columns=persons)
    for match_result in matches:
        scores = scores + match_result
        counts = counts + numpy.ceil(match_result/100)+ numpy.floor(match_result/100)

    dimension = len(persons)

    massey_vector = scores.sum(axis=1)
    massey_vector = massey_vector.as_matrix()
    massey_vector = massey_vector.reshape((number_of_person,1))

    colley_vector = counts.sum(axis=1)
    colley_vector = colley_vector.as_matrix()
    colley_vector = colley_vector.reshape((number_of_person,1))
    colley_vector = colley_vector/2
    colley_vector = colley_vector + 1

    massey_rating = numpy.linalg.solve(massey_matrix.as_matrix(columns=persons), massey_vector)
    colley_rating = numpy.linalg.solve(colley_matrix.as_matrix(columns=persons), colley_vector)

    return massey_matrix, massey_vector, massey_rating, colley_matrix, colley_vector, colley_rating


def generate_ranking(persons, colley, guilty, non_guilty):
    colley_rating = list(zip(persons, colley.flatten().tolist()))
    colley_rating.sort(key=lambda tup: tup[1])
    colley_rating.reverse()
    x = list(zip(*colley_rating))
    colley_rating = list(zip(list(x[0]), list(x[1]), range(1,1+len(persons))))
    print(colley_rating)
    guilty_rating = [v for n,v,r in colley_rating if n in guilty]
    nonguilty_rating = [v for n,v,r in colley_rating if n in non_guilty]
    guilty_ranking = [r for n,v,r in colley_rating if n in guilty]
    nonguilty_ranking = [r for n,v,r in colley_rating if n in non_guilty]

    result = [guilty_rating, nonguilty_rating, guilty_ranking, nonguilty_ranking]

    for i in result:
        print(numpy.mean(i), numpy.std(i))

    return result



def plot_emo_hist(ls, emo, color):
    data = [
        go.Histogram(
            x=ls,
            xbins=dict(
                size=0.8,
            ),
            marker=dict(
                color=color,
                line=dict(
                    color='rgb(8,48,107)',
                    width=1.0,
                )
            ),
            histnorm='count')
    ]
    layout = go.Layout(
        autosize=False,
        width=400,
        height=400,
        xaxis=dict(
            title='Daily occurrences of {0}'.format(emo),
            #range=[-1, 5],
        ),
        yaxis=dict(
            title='Count',
            autorange=True,
            showgrid=True,
            zeroline=True,
            showline=True,
            autotick=True,
            showticklabels=True
        )
    )

    iplot(go.Figure(data=data, layout=layout), show_link=False)



def plot_emotion_occurrence_hist(ls, color):
    data = [
        go.Histogram(
            x=ls,
            xbins=dict(
                size=0.8,
            ),
            marker=dict(
                color=color,
                line=dict(
                    color='rgb(8,48,107)',
                    width=1.0,
                )
            ),
            histnorm='count')
    ]
    layout = go.Layout(
        autosize=False,
        width=600,
        height=600,
        xaxis=dict(
            title='Number of emotion categories',
            range=[-1, 5],
        ),
        yaxis=dict(
            title='Count',
            autorange=True,
            showgrid=True,
            zeroline=True,
            showline=True,
            autotick=True,
            showticklabels=True
        )
    )

    iplot(go.Figure(data=data, layout=layout), show_link=False)


def plot_error_bars(guilty_mean, guilty_std, nonguilty_mean, nonguilty_std, order_seq, colors):
    x = order_seq

    y1 = [guilty_mean[k] for k in order_seq]
    e1 = [guilty_std[k] for k in order_seq]

    y2 = [nonguilty_mean[k] for k in order_seq]
    e2 = [nonguilty_std[k] for k in order_seq]

    trace1 = go.Bar(
        x=x,
        y=y1,
        name='Guilty',
        error_y=dict(
            type='data',
            array=e1,
            visible=True
        ),
        marker=dict(
            color=colors[0],
            line=dict(
                color='rgb(8,48,107)',
                width=1.0,
            )
        ),
        showlegend=True,
    )
    trace2 = go.Bar(
        x=x,
        y=y2,
        name='Non-guilty',
        error_y=dict(
            type='data',
            array=e2,
            visible=True
        ),
        marker=dict(
            color=colors[1],
            line=dict(
                color='rgb(8,48,107)',
                width=1.0,
            )
        ),
        showlegend=True,
    )
    data = [trace1, trace2]
    layout = go.Layout(
        barmode='group',
        autosize=False,
        width=1100,
        height=400,
        xaxis=dict(
            title='Emotion steady states',
        ),
        yaxis=dict(
            title='Probability',
        ),
        showlegend=True,
        legend=dict(orientation="h"),
    )

    iplot(go.Figure(data=data, layout=layout), show_link=True)


def plot_rtht_error_bars(df_mean, df_std, order_seq, colors):
    x = order_seq

    data = []

    for index,row in df_mean.iterrows():
        trace = go.Bar(
            x=order_seq,
            y=df_mean.loc[index,order_seq].tolist(),
            name=row['group'] + ' ' + row['category'],
            error_y=dict(
                type='data',
                array=df_std.loc[index,order_seq].tolist(),
                visible=True
            ),
            marker=dict(
                color=colors[index],
                line=dict(
                    color='rgb(8,48,107)',
                    width=1.0,
                )
            ),
            showlegend=True,
        )
        data.append(trace)

    layout = go.Layout(
        barmode='group',
        autosize=False,
        width=1100,
        height=400,
        xaxis=dict(
            title='',
        ),
        yaxis=dict(
            title='Average of individuals in group',
        ),
        showlegend=True,
        legend=dict(orientation="h"),
    )

    iplot(go.Figure(data=data, layout=layout), show_link=True)




def plot_entropy_hist(guilty, nonguilty, colors):
    trace0 = go.Histogram(
        x=guilty,
        marker=dict(
            color=colors[0],
            line=dict(
                color='rgb(8,48,107)',
                width=1.0,
            )
        ),
        name='Guilty',
    )
    trace1 = go.Histogram(
        x=nonguilty,
        marker=dict(
            color=colors[1],
            line=dict(
                color='rgb(8,48,107)',
                width=1.0,
            )
        ),
        name='Non-guilty',
    )
    data = [trace0, trace1]
    layout = go.Layout(
        #barmode='stack',
        autosize=False,
        width=1100,
        height=400,
        showlegend=True,
        legend=dict(orientation="h"),
        xaxis=dict(
            title='Entropy rate',
        ),
        yaxis=dict(
            title='Count',
        ),
    )

    iplot(go.Figure(data=data, layout=layout), show_link=True)


def plot_colley_hist(guilty, nonguilty, x_axis, colors):
    trace0 = go.Histogram(
        x=guilty,
        marker=dict(
            color=colors[0],
            line=dict(
                color='rgb(8,48,107)',
                width=1.0,
            )
        ),
        name='Guilty',
    )
    trace1 = go.Histogram(
        x=nonguilty,
        marker=dict(
            color=colors[1],
            line=dict(
                color='rgb(8,48,107)',
                width=1.0,
            )
        ),
        name='Non-guilty',
    )
    data = [trace0, trace1]
    layout = go.Layout(
        #barmode='stack',
        autosize=False,
        width=1100,
        height=400,
        showlegend=True,
        legend=dict(orientation="h"),
        xaxis=dict(
            title=x_axis,
        ),
        yaxis=dict(
            title='Count',
        ),
    )

    iplot(go.Figure(data=data, layout=layout), show_link=True)









