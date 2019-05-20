#!/usr/local/bin/python3
# -*- coding: utf-8 -*-

import os
import os.path
import nltk
import re
import math
import os.path
import datetime
from pickle import load, dump
from pymongo import MongoClient, ASCENDING, DESCENDING


aaer_samples = [

'Keep me posted as to how ... many frequent flier miles you have got this far and how many you plan to get by Friday. Will be in Boston tomorrow. Plans for a trip look fine so far. Worst case we can get a refund by Monday, hopefully we do not.',

'As I mentioned, I just got into this frequent flyer program. I got five thousand of sign-in bonus miles but thinking maybe if I fly often, I will get additional three to five K miles.',

'On the frequent flyer program topic you mentioned, I think you should sign up for another flight, if you can, since they are providing bonus mileage soon.',

'Let me know if you finished your recent harvest arrangements and how many kilos are available for my parents. They are in Turkey now and could use some once they are back.',

'This year the potato yield was not as high as the last one. Whatever is collected is now being transported in the warehouse, with special climate conditions, from where it is going to be available for delivery. My estimates are about 6.8 kilo per square yard. ? Of course, some potato need to be left for the next year seeds but it should not be a concern since I have a vendor who will provide enough once the spring comes.',

'Happy to talk about sales items and etc. ... sale ends soon ...so hurry up.',

'Yep, I have set it up. Better do it now when they have sale. I could not believe how many things one needs once engaged. Single life was much easier if you ask me. It is always good idea to know about coupons available. I try to follow up on the rebates programs currently in place but often miss many due to lack of time. Thanks for pointing it out to me. ... Although wedding day is not yet announced, I hope to get all the important items ahead of time: I even started buying small things that usually not important until you need them.',

'Good points ... sale ends on Friday ... see if you can get registered for as many items as possible ... more you get now ... more you save ... We should start tracking these events more actively.',

]

email_samples = [
    'hi how are you no contact what is the progress on the projects please update me on cameras.',
    'Heard old uncle of yours got H1 virus too and the doctors at the hospital want to give him a check up',
    'my old uncle is fit and healthy like anything. In the days he is moving back and forth for his business like tornedo don’t put an ear to rumour',
]

stemmer = nltk.stem.snowball.EnglishStemmer()
stoplist = nltk.corpus.stopwords.words('english')



def connect_to_db(host, port, user=None, pw=None):
    client = MongoClient('mongodb://{0}:{1}/'.format(host, port))
    return client


if __name__ == '__main__':

    client = connect_to_db('localhost', '27017')
    db = client['enron']
    collection = db['emails']


    sec_index = 0
    number_of_emails_in_group = 4
    group_size = number_of_emails_in_group + 1
    text_samples = aaer_samples
    sample_size = len(text_samples)

    people_list = {
        'sherron':{'$regex':'sherron\.watkins@'},
        'krautz':{'$regex':'michael\.krautz@'},
        'shelby':{'$regex':'rex\.shelby@'},
        'brown':{'$regex':'james\.brown@'},
        'causey':{'$regex':'richard\.causey@'},
        'calger':{'$regex':'christopher\.calger@'},
        'despain':{'$regex':'tim\.despain@'},
        'hannon':{'$regex':'kevin\.hannon@'},
        'koenig':{'$regex':'mark\.koenig@'},
        'forney':{'$regex':'john\.forney@'},
        'rice':{'$regex':'ken\.rice@'},
        'rieker':{'$regex':'paula\.rieker@'},
        'fastow':{'$regex':'a.+\.fastow@'},
        'delainey':{'$regex':'david\.delainey@'},
        'glisan':{'$regex':'ben\.glisan@'},
        'richter':{'$regex':'jeff.*\.richter@'},
        'lawyer':{'$regex':'l.*\.lawyer@'},
        'belden':{'$regex':'t.*\.belden@'},
        'bowen':{'$regex':'r.*\.bowen@'},
        'colwell':{'$regex':'w.*\.colwell@'},
        'boyle':{'$regex':'d.*\.boyle@'},
    }

    condition = {'from':{'$regex':'rex\.shelby@'}}


    for person_name in people_list:

        condition = {'from': people_list[person_name]}
        print(condition)

        data_path = os.path.join(os.getcwd(), person_name)
        if not os.path.exists(data_path):
            os.makedirs(data_path)

        count_emails = collection.count(condition)
        number_of_groups = int(count_emails / number_of_emails_in_group)
        print('There are {0} groups in total, and {1} emails in total'.format(number_of_groups, count_emails))

        ls = collection.find(condition).sort('date_time', ASCENDING)

        group_list = []
        count_email_in_group = 0

        for i in ls:
            count_email_in_group += 1
            text = i['email_text']
            group_list.append(text.lower())
            if count_email_in_group % number_of_emails_in_group == 0:
                group_list.append(text_samples[sec_index])
                sec_index += 1
                sec_index = sec_index % sample_size

        print('add enron emails to groups')

        for i in range(number_of_groups):
            start_index = i*group_size
            end_index = (i+1)*group_size
            group_emails = group_list[start_index:end_index]

            word_dist = nltk.FreqDist()
            words_dist = []

            for t in group_emails:
                tokens = nltk.word_tokenize(t)
                tokens = [t for t in tokens if t not in stoplist]
                tokens = [stemmer.stem(t) for t in tokens]
                tokens = [t for t in tokens if t not in stoplist]
                fdist = nltk.FreqDist(tokens)
                word_dist.update(tokens)
                words_dist.append(fdist)

            ls = list(word_dist.items())
            ls.sort(key=lambda tup: tup[1])
            ls.reverse()

            fdist = []
            for d in words_dist:
                dist = []
                for t,f in ls:
                    if t in d:
                        dist.append(d[t] / float(d.N()))
                    else:
                        dist.append(0.0)
                fdist.append(dist)

            f = open(os.path.join(data_path,'{1}_{2}_email_group_squence_{0:04}.pkl'.format(i,person_name,number_of_emails_in_group)), 'wb')
            dump(fdist, f)
            f.close()
            print('finished gathering the {0}-th group'.format(i))

    print('End of Program')
