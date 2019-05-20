# -*- coding: cp936 -*-
###extract the feature values from the email
import nltk.probability as prob
import os
from re import *
from string import *
from shutil import *
import numpy
import string
#from mlabwrap import mlab
from decimal import *
from math import *
import sys
FilePath = 'c:\\web2py'# os.path.abspath(os.path.dirname(sys.argv[0]))
RESULT_GENDER_PATH= FilePath + '\\applications\\init\\uploads\\'
FileCue = FilePath + '\\applications\\init\\cues\\'
FileSVM = FilePath + '\\applications\\init\\svm\\'

getcontext().prec = 4

def createzerolist(p_size):
    ZeroList=[]
    for i in range(0,p_size):
        ZeroList.append(0)
    return ZeroList

def DivideListByWC(p_List,p_WC):
##    print "WC Value:",p_WC
    New_List=[]
    for oldlist in p_List:
##        newvalue=oldlist/p_WC
##        print "newvalue=",newvalue
        New_List.append(float(oldlist)/p_WC)
    return New_List    
        
def count_number_blank_lines(filepath):
    emptyline_count = 0
##    emptyline_no = 0
    '''count the number of blank lines and divide the total number of lines, return the number of blankline and total lines'''
    emptyline = ['']
    noemptyline = ['']
    for num, line in enumerate(open(filepath)):
        if len(split(line)) == 0:
           emptyline.append(num)
        else:
            noemptyline.append(num)
        maxline = num
        emptyline_no = emptyline[1:]
        print(emptyline_no)
    for i in range(0,len(emptyline_no)):
        if maxline in emptyline_no:
            maxline = maxline-1
        emptyline_count = 0
    for i in range(0,len(emptyline_no)):
        if emptyline[i] < maxline:
            emptyline_count = emptyline_count + 1
    
    if emptyline_count == 0:
        return [0,maxline+1,noemptyline[1:]]
    else:
        return [emptyline_count,maxline+1,noemptyline[1:]]

def puncsplit(word,punc):
   '''seperate the words by punc'''
   newword = ['']
   for singleword in word:
      if type(singleword) == type('a'):
         singleword = singleword.split(punc)
         if len(singleword) > 1:
            for i in range(0,len(singleword)):
             newword.append(singleword[i])
         else: newword.append(singleword)
      elif type(singleword) == type(newword):
         singleword = singleword[0].split(punc)
         if len(singleword) > 1:
            for i in range(0,len(singleword)):
             newword.append(singleword[i])
         else: newword.append(singleword)
   return newword[1:len(newword)]
#------------------------------------------------------------------
def prefinalword(wordlistinit):
   '''given the filename,will output a word list without punctuations'''
# process the wordlist in the finalist
   puncuation = [".",",",":",";","?","!","-",'"',"(",
               ")","{","}","[","]","/","~","@","#","$",
               "%","^","&","*","+","|","^",
               "=","|",">","<","_","\\"]
   for punc in puncuation:
      wordlistinit = puncsplit(wordlistinit,punc)
   neword = ['']
   for word in wordlistinit:
       if len(word) > 0:
         neword.append(word)
   thelist = ['']
   for word in neword:
     if len(word) > 0:
       thelist.append(word[0])
   thethelist = ['']
   for word in thelist:
     if len(word) > 0:
       thethelist.append(word)
   finalist = thethelist[1:len(thethelist)]
   return finalist
#---------------------------------------------------------------------
def readfile(filename):
   '''read file '''
   f = open(filename,'r')
   text = f.read()
   f.close()
   return text
#-------------------------------------------------------------------
def DetermineDictionaryCounts(wordlist,dictlist):
 '''count the LIWC cue value according to the dictlist'''
 grandtotal = 0
 for word in wordlist:
     count = 0
     for wordcheck in dictlist:
         lastchar = wordcheck[-1:]
         if lastchar in ['*']:
             wordcheck = wordcheck.rstrip(lastchar)        
             if match(wordcheck,word):
                 count = count+1          
         elif (word == wordcheck):             ## when the cue is part of a long word?
                count = count+1                 
     grandtotal = grandtotal+count  
 return grandtotal
#-------------------------------------------------------------------------
def findnextcharacter(text,index):
    while index < len(text):
        if len(text[index].split())>0:
           startindex = index                                 ###################??????????????????????????????
           return startindex
           break
        else:
           index = index+1
        if index > len(text):
            return -1
#---------------------------------------------------------------------------------------------
def count_function_words(text, function_words_detailed_path):
 '''count the function words value according to the function word list'''
 function_text = readfile(function_words_detailed_path)
 function_list = function_text.split('\n')
 list_length = len(function_list)
## print list_length
 count_function_words = createzerolist(list_length)
 for i in range(0,list_length):
     for word in text:
         if word == function_list[i]:
             count_function_words[i] = count_function_words[i] + 1
## print len(count_function_words)
 return count_function_words


#----------------------------------------------------------------------------------------------
def extractcues(filename):
    netabbfile = FileCue  + 'netabbrevlist.txt'
    ditionarypath = FileCue  + 'selfdict'
    cues = ['achieve', 'Affect', 'Anger', 'Anx', 'article', 'Assent', 'Body',\
     'cause', 'certain', 'Cogmech', 'comm', 'Death', 'Discrep', 'down',\
     'eating', 'Excl', 'Family', 'Feel', 'Fillers', 'friends', 'future',\
     'Groom', 'hear', 'Home', 'Human', 'I', 'Incl', 'Inhib', 'insight',\
     'Job', 'Leisure', 'Metaph', 'Money', 'Motion', 'Music', 'Negate',\
     'Negemo', 'Nonflu', 'Number', 'Occup', 'Optim', 'other', 'othref',\
     'Past', 'Physical', 'Posefeel', 'Posemo', 'Preps', 'Present', 'pronoun',\
     'Relig', 'sad', 'School', 'see', 'self', 'sense', 'Sexual', 'Sleep',\
     'social', 'Space', 'Sports', 'Swear', 'tentative', 'time','TV', 'up',\
    'we', 'you']
    function_words_path = FileCue  + 'function_words'
##   function_words = ['articles', 'pro-sentences', 'pronouns', 'auxiliary verbs',\
#                     'conjunctions', 'interjections', 'adpositions']
    new_cues_path = FileCue  + 'newcues'
    new_cues = ['affective_adjectives', 'expletives', 'exclamation', 'hedges', 'othermale',\
                'Intensive_adverbs', 'Uncertainty_verbs', 'Judgmental_adjectives', 'otherfemale']

#read the file
    text = readfile(filename)
#(5)count the number of white space
    whie_space = count(text,' ')
#(6)count the number of tab space
    tab_space = count(text,'   ')
#split the words
    wordlistinit = text.split()
#split the words with punctuations

    wordlist = prefinalword(wordlistinit)
#(30) get the word count WC
    WC = len(wordlist)

#(314)get the total number of lines
    blanklines = count_number_blank_lines(filename)
    total_lines = blanklines[1]
#(323)get the ratio of blank lines in total lines blanklines
    blanklineratio = float(blanklines[0])/total_lines

#(324)compute the length of non blank line
    no_blank_line = blanklines[1]-blanklines[0]
    len_of_line = float(WC)/no_blank_line

#(325)(326)get the first word and the last word of the content
#and check if it is the greeting words and farewell words,if it is,
#give an index in greeting and farewell.Otherwise, give 10.
    greeting_words = ['hey','hi','hello','dear']
    farewell_words = ['thanks','wishes','regards','yours','love','best']
    firstword=string.lower(wordlist[0])
    lastwords = wordlist[-5:]
    greeting = 0
    farewell = 0
    if firstword in greeting_words:
       greeting = 1

    for lastword in lastwords:
        word = string.lower(lastword)
        for fare_word in farewell_words:
            if count(farewell_words,word):
                farewell = 1

    #print 'farewell'
    #print farewell
#(43)find the number of net abbreviation words
    nettext = readfile(netabbfile)
    netlist = nettext.split()
    netcount = 0
    for word in wordlist:
        for netword in netlist:
            if word == netword:
               netcount = netcount+1
    netratio = float(netcount)/WC
    #print "netratio: ", netratio
#-----------------------------------------
#(316) count the number of paragraphs
    numberlines = blanklines[2]
##    print numberlines
    
    para_count = 1
    if numberlines == [1]:                                 
             para_count = 1
    else:
         for i in range(0,len(numberlines)-1):
                if (numberlines[i]) != (numberlines[i+1])-1:
                    para_count = para_count+1
          
##    print para_count
#-----------------------------------------------    
#(318) find the average word per paragraph
    aver_para = float(WC)/para_count

#-----------------------------------------------
    

#Punctuation cues
    PERIODS = ('.')
    QUESTIONMARK = ('?')
    EXLAMATIONMARK = ('!')
    comma = (',')
    colon = (':')
    semicolon = (';')
#special characters:    
    dash = ('-')
    DOLLARSIGNS = ('$')
    percent = ('%')
    quote = ("'")
    leftparentheses = ('(')
    rightparenthese = (')')
    asterisks = ('*')
    plussign = ('+')
    forwardslash = ('\\')
    lesssign = ('<')
    equalsign = ('=')
    greatsign = ('>')
    pandsign = ('#')
    atsign = ('@')
    leftsquare = ('[')
    rightsquare = (']')
    backslash = ('/')
    underscore = ('_')
    andsign = ('&')
    leftbig = ('{')
    rightbig = ('}')
    cur = ('~')
    hat = ('^')
    dsign = ('|')

#(7)number of dash marks
    number_of_dash = count(text,dash)
#(8)number of dollarsigns marks
    number_of_dollarsigns = count(text,DOLLARSIGNS)
#(9)number of percent marks
    number_of_percent = count(text,percent)
#(10)number of left parathese
    number_of_leftpara = count(text,leftparentheses)
##    print number_of_leftpara
#(11)number of right parathese
    number_of_rightpara = count(text,rightparenthese)
#(12)number of asterisks
    number_of_asterisks = count(text,asterisks)
#(13)number of plus
    number_of_plus = count(text,plussign)
#(14)number of andsign 
    number_of_andsign = count(text,andsign)
#(15)number of forward slash
    number_of_forslash = count(text,forwardslash)    
#(16)number of lesssign
    number_of_lesssign = count(text,lesssign)
#(17)number of equal sign
    number_of_equalsign = count(text,equalsign)
#(18)number of greatsign 
    number_of_greatsign = count(text,greatsign)
#(19)number of pand sign
    number_of_pand = count(text,pandsign)
#(20) number of at sign
    number_of_at = count(text,atsign)
#(21)number of leftsquare
    number_of_leftsquare = count(text,leftsquare)
#(22)number of rightsquare
    number_of_rightsquare = count(text,rightsquare)
#(23)number of backslash
    number_of_backslash = count(text,backslash)
#(24)number of underscore
    number_of_underscore = count(text,underscore)
#(25)number of rightbig
    number_of_rightbig = count(text,rightbig)
#(26)number of leftbig
    number_of_leftbig = count(text,leftbig)
#(27)number of cur
    number_of_cur = count(text,cur)
#(28)number of dsign    
    number_of_dsign = count(text,dsign)
#(29)number of hat    
    number_of_hat = count(text,hat)

#(132)number of quote marks
    number_of_quote = count(text,quote)    
#(133)number of comma
    number_of_comma = count(text,comma)
#(134)number of periods
    number_of_period = count(text,PERIODS)
#(135)number of colons
    number_of_colons = count(text,colon)
#(136)number_of_semicolons
    number_of_semicolons = count(text,semicolon)
#(137)number of question marks
    number_of_qmark = count(text,QUESTIONMARK)
#(139)number of exlamation marks
    number_of_exlam = count(text,EXLAMATIONMARK)
#(315)find the number of sentences
    TotalSentances = number_of_period + number_of_qmark + number_of_exlam
    if  TotalSentances == 0:
        TotalSentances = 1
#(320) average word per sentance
    ave_sentance = float(WC)/TotalSentances
#(317) find the average senteces per paragraph
    aver_sentence_para = float(TotalSentances)/para_count
    
#(141)number of ellipsis
    number_of_ellipsis = count(text,'...')
#(138)number of multiple question marks
    number_of_multiqmark = count(text,'??')
#(140) number of multiple exclamation marks
    number_of_multiexcla = count(text,'!!')


#(23)total number of punctuations characters
    totalpunc = number_of_period + number_of_qmark + number_of_exlam + number_of_dash\
           + number_of_dollarsigns + number_of_percent + number_of_quote + number_of_leftpara\
           + number_of_rightpara + number_of_asterisks + number_of_plus + number_of_comma\
           + number_of_forslash + number_of_colons + number_of_semicolons + number_of_lesssign\
           + number_of_equalsign + number_of_greatsign + number_of_pand + number_of_at + number_of_leftsquare\
           + number_of_rightsquare + number_of_backslash + number_of_underscore + number_of_andsign + number_of_leftbig\
           + number_of_rightbig + number_of_cur


#-------------------------------------------------------------------
#(32) get the vocabulary richness
#convert to lower case
    newword = ['']
    for word in wordlist:
         newword.append(word.lower())
    lowerwordlist = newword[1:]

    fdist = prob.FreqDist(lowerwordlist)                                
    uniquewords = fdist.B()
    uniqueratio = float(uniquewords)/WC    ## number of different words/total words

#--------------------------------------------------------------------
# (35) frequency of once-occurring words
    Hapax_legomena = fdist.Nr(1)
# (36) frequency of twice-occurring words
    Hapax_dislegomena = fdist.Nr(2)
# (39) Sichel's S measure
    Sichels_S= float(Hapax_dislegomena)/WC
# (41) Honore's R measure
##    if Hapax_legomena == uniquewords:
##        Honores_R = None
##    else:
    Honores_R= 100*log10(WC)/(1.0001-float(Hapax_legomena)/uniquewords)
##    print Honores_R
# (42) Entropy measure, Simpson_D, Yule_K    
    max_count = fdist[fdist.max()]
    #print 'max count of different words:',max_count
    Entropy = 0
    Simpson_D = 0
    Yule_K = 0
    uniquewords_count = createzerolist(max_count)
    #print 'WC:',WC
    for i in range(1,max_count+1):
        uniquewords_count = fdist.Nr(i)
##        print 'uniquewords_count %(#)d:'%{"#": i},uniquewords_count
        sub_Entropy = float(uniquewords_count)*(float(i)/WC)*(-log10(float(i)/WC))
##        print 'sub_Entropy %(#)d:'%{"#": i},sub_Entropy
        sub_Simpson = float(uniquewords_count)*(float(i)/WC)*(float(i-1)/(WC-1))
##        print 'sub_Simpson %(#)d:'%{"#": i},sub_Simpson
        sub_Yule = float(uniquewords_count)*((float(i)/WC)**2)
##        print 'sub_Yule %(#)d:'%{"#": i}, sub_Yule
        Entropy = Entropy + sub_Entropy
        Simpson_D = Simpson_D + sub_Simpson
        Yule_K = Yule_K + sub_Yule
    Yule_K = 10**4*(float(-1)/WC+Yule_K)
    
#(1)total number of characters in words
    totalcharalen = 0
    for word in wordlist:
        totalcharalen = totalcharalen+len(word)

#(319) average number of characters per paragraph
    average_char_para = float(totalcharalen)/para_count

#(31)average length per word
    average_per_word = float(totalcharalen)/WC

#------------------------------------------------------------------
#(33)(34)words longer than 6 characters and shorter then 3 characters
    sixltr = 0
    short = 0
    for word in wordlist:
        if len(word) >= 6:
##            print word
            sixltr = sixltr + 1
        if len(word) <= 3:
##            print word
            short = short + 1
    sixltrratio = float(sixltr)/WC
    shortratio = float(short)/WC

#(44...63)word length frequency distribution                 
    y =createzerolist(20)
    for word in wordlist:
        L = len(word)
        if L <= 20:
            y[L-1] = y[L-1]+1
    #print y
    y = DivideListByWC(y,WC)
##    for i in range(0,20):
        
####        b = y
####        print "b= ",b
##        y[i] = float(y[i])/WC
    #print y
#-------------------------------------------------------------------
#(4)digital characters
    digital = string.digits
    numberdigit= 0
    for word in wordlist:
        for i in word:
            if i in digital:
                numberdigit = numberdigit + 1
    digitratio = float(numberdigit)/totalcharalen

#------------------------------------------------------------------
#(3)total number of upper characters
    uppercount = 0
    for word in text:
        if word[0] in uppercase:
            uppercount = uppercount+1
    uppercountratio = float(uppercount)/totalcharalen

#------------------------------------------------------------------
#(2)ratio of total number of letters/total characters
    ratio_of_letters = float(totalcharalen-numberdigit)/totalcharalen

#-----------------------------------------------------------------
#(321)(322)find the number of senteces beginning with upper case and lowercase

    startindex = findnextcharacter(text,0)
    uppersentence = 0
    lowersentence = 0
#find the sentence close sign index
    sentencesign = ['.','?','!']
    index = 0
    signindex = []
    while index < len(text):
        if text[index] in sentencesign:
            signindex.append(index)
        index = index + 1
    beginindex = []
    for i in signindex[0:-1]:
        beginindex.append(findnextcharacter(text,i+1))
    beginindex.insert(0,startindex)
    for i in beginindex:
        if text[i] in uppercase:
            uppersentence = uppersentence + 1
        elif text[i] in lowercase:
            lowersentence = lowersentence + 1
    upperratio = float(uppersentence)/TotalSentances
    lowerratio = float(lowersentence)/TotalSentances

#----------------------------------------------
#(64...131)cues from LIWC
    
        
    cuesfreq =createzerolist(68)
    
    for i in range(0,len(cues)):
        listpath = ditionarypath + '/' + cues[i] + '.txt'
        dicttext = readfile(listpath)
        dictlist = dicttext.split()
        cuesfreq[i] = float(DetermineDictionaryCounts(lowerwordlist,dictlist))/WC

#(142...313)  number of function words
    function_words_detailed_path = function_words_path + '/' + 'function_words.txt'   
    function_words = count_function_words(lowerwordlist,function_words_detailed_path)
        
#(326��328)  number of articles (a, an, the)
    function_words_detailed_path = function_words_path + '/' + 'articles.txt'   
    function_articles = count_function_words(lowerwordlist,function_words_detailed_path)
##    print function_articles
    
#(329��332 ) number of pro sentences (yes, no, okay, OK)
    function_words_detailed_path = function_words_path + '/' + 'pro-sentences.txt'   
    function_pro_sentences = count_function_words(lowerwordlist,function_words_detailed_path)
##    print function_pro_sentences

#(333��406) number of pronouns 74 features
    function_words_detailed_path = function_words_path + '/' + 'pronouns.txt'   
    function_pronouns = count_function_words(lowerwordlist,function_words_detailed_path)
##    print len(function_pronouns)

#(407��453) number of auxiliary verbs 47 features
    function_words_detailed_path = function_words_path + '/' + 'auxiliary verbs.txt'   
    function_auxiliary_verbs = count_function_words(lowerwordlist,function_words_detailed_path)
##    print function_auxiliary_verbs

#(454��475) number of conjunctions 22 features
    function_words_detailed_path = function_words_path + '/' + 'conjunctions.txt'   
    function_conjunctions = count_function_words(lowerwordlist,function_words_detailed_path)
##    print function_conjunctions
    
#(476��584) number of interjections 109 features
    function_words_detailed_path = function_words_path + '/' + 'interjections.txt'   
    function_interjections = count_function_words(lowerwordlist,function_words_detailed_path)
##    print function_interjections
    
#(585��708) number of adpositions 124 features
    function_words_detailed_path = function_words_path + '/' + 'adpositions.txt'   
    function_adpositions = count_function_words(lowerwordlist,function_words_detailed_path)
##    print function_adpositions

## number of new_cues
    new_cuesfreq = createzerolist(9)
    for i in range(0,len(new_cues)):
        new_listpath = new_cues_path + '/' + new_cues[i] + '.txt'
        new_dicttext = readfile(new_listpath)
        new_dictlist = new_dicttext.split()
        new_cuesfreq[i] = float(DetermineDictionaryCounts(lowerwordlist,new_dictlist))/WC

        
    output = createzerolist(545)
## character based features
    output[0] = totalcharalen
    output[1] = ratio_of_letters
    output[2] = uppercountratio
    output[3] = digitratio
    output[4] = float(whie_space)/totalcharalen
    output[5] = float(tab_space)/totalcharalen
    output[6] = float(number_of_dash)/totalcharalen
    output[7] = float(number_of_dollarsigns)/totalcharalen
    output[8] = float(number_of_percent)/totalcharalen
    output[9] = float(number_of_leftpara)/totalcharalen
    output[10] = float(number_of_rightpara)/totalcharalen
    output[11] = float(number_of_asterisks)/totalcharalen
    output[12] = float(number_of_plus)/totalcharalen
    output[13] = float(number_of_andsign)/totalcharalen
    output[14] = float(number_of_forslash)/totalcharalen
    output[15] = float(number_of_lesssign)/totalcharalen
    output[16] = float(number_of_equalsign)/totalcharalen
    output[17] = float(number_of_greatsign)/totalcharalen
    output[18] = float(number_of_pand)/totalcharalen
    output[19] = float(number_of_at)/totalcharalen
    output[20] = float(number_of_leftsquare)/totalcharalen
    output[21] = float(number_of_rightsquare)/totalcharalen
    output[22] = float(number_of_backslash)/totalcharalen
    output[23] = float(number_of_underscore)/totalcharalen
    output[24] = float(number_of_rightbig)/totalcharalen
    output[25] = float(number_of_leftbig)/totalcharalen
    output[26] = float(number_of_cur)/totalcharalen
    output[27] = float(number_of_dsign)/totalcharalen
    output[28] = float(number_of_hat)/totalcharalen

## word based features
    output[29] = WC
    output[30] = average_per_word
    output[31] = uniqueratio
    output[32] = sixltrratio   
    output[33] = shortratio
    output[34] = float(Hapax_legomena)/WC
    output[35] = float(Hapax_dislegomena)/WC
##    print output[34]
##    print output[35]
    output[36] = Yule_K 
    output[37] = Simpson_D
    output[38] = Sichels_S
    output[39] = Honores_R
    output[40] = Entropy
    output[41] = netratio
    output[42:62] = y
    output[62:130] = cuesfreq

## syntactic features
    output[130] = float(number_of_quote)/totalcharalen
    output[131] = float(number_of_comma)/totalcharalen
    output[132] = float(number_of_period)/totalcharalen
    output[133] = float(number_of_colons)/totalcharalen
    output[134] = float(number_of_semicolons)/totalcharalen
    output[135] = float(number_of_qmark)/totalcharalen
    output[136] = float(number_of_multiqmark)/totalcharalen
    output[137] = float(number_of_exlam)/totalcharalen
    output[138] = float(number_of_multiexcla)/totalcharalen
    output[139] = float(number_of_ellipsis)/totalcharalen
##    output[140:312] = function_words/WC
##
#### structural features
##    output[312] = total_lines
##    output[313] = TotalSentances
##    output[314] = para_count
##    output[315] = aver_sentence_para
##    output[316] = aver_para
##    output[317] = average_char_para
##    output[318] = ave_sentance
##    output[319] = upperratio 
##    output[320] = lowerratio
##    output[321] = blanklineratio
##    output[322] = len_of_line
##    output[323] = greeting
##    output[324] = farewell   
##  
##
#### extended function words
##    output[325:328] = function_articles/WC
##    output[328:332] = function_pro_sentences/WC
##    output[332:406] = function_pronouns/WC
##    output[406:453] = function_auxiliary_verbs/WC
##    output[453:475] = function_conjunctions/WC
##    output[475:584] = function_interjections/WC
##    output[584:708] = function_adpositions/WC
##    output[708:717] = new_cuesfreq


## structural features
    output[140] = total_lines
    output[141] = TotalSentances
    output[142] = para_count
    output[143] = aver_sentence_para
    output[144] = aver_para
    output[145] = average_char_para
    output[146] = ave_sentance
    output[147] = upperratio 
    output[148] = lowerratio
    output[149] = blanklineratio
    output[150] = len_of_line
    output[151] = greeting
    output[152] = farewell   
  

## extended function words
##    print function_articles
    output[153:156]=DivideListByWC(function_articles,WC)
    output[156:160] = DivideListByWC(function_pro_sentences,WC)
    output[160:234] = DivideListByWC(function_pronouns,WC)
    output[234:281] = DivideListByWC(function_auxiliary_verbs,WC)
    output[281:303] = DivideListByWC(function_conjunctions,WC)
    output[303:412] = DivideListByWC(function_interjections,WC)
    output[412:536] = DivideListByWC(function_adpositions,WC)
    output[536:545] = new_cuesfreq
    
##    print output[153:156]
##    output[153:156] = float(function_articles)/WC
##    output[156:160] = float(function_pro_sentences)/WC
##    output[160:234] = float(function_pronouns)/WC
##    output[234:281] = float(function_auxiliary_verbs)/WC
##    output[281:303] = function_conjunctions/WC
##    output[303:412] = function_interjections/WC
##    output[412:536] = function_adpositions/WC
##    output[536:545] = new_cuesfreq

##    print output
    return output
  

def GenerateFeatureValueText(filename,Filename):

       
    #print "Data to be read",data
    getcontext().prec = 4

    result = extractcues(filename)
    #rint
##    print result
    (filepath,filename ) = os.path.split(filename)  
    new_location = RESULT_GENDER_PATH +"featuredtext"+"_" + Filename + ".txt"
    print(new_location)
    f = open(new_location,'w')
    for i in range(0,545):
       im = str(result[i]) + ' '
       f.write(im)
    f.write('\n')   
    f.close()
##print result

def GenerateFeatureValueTextTest():
    data="Dear Friends, Please do not take this for a junk letter. Bill Gates is sharing his fortune. If you ignore this you will repent later. Microsoft and AOL are now the largest Internet companies and in an effort to make sure that Internet Explorer remains the most widely used program, Microsoft and AOL are running an e-mail beta test. When you forward this e-mail to friends, Microsoft can and will track it (if you are a Microsoft Windows user) for a two week time period. For every person that you forward this e-mail to, Microsoft will pay you $245.00, for every person that you sent it to that forwards it on, Microsoft will pay you $243.00 and for every third person that receives it, you will be paid $241.00. Within two week! s, Microsoft will contact you for your address and then send you a cheque."
##    filepath = r'D:\\'+filename

##    print "Data to be read",data
        
    result = extractcues(data)
    #rint
##    print result
    
    new_location = RESULT_GENDER_PATH+'testrohan.txt'

    f = open(new_location,'w')
    for i in range(0,545):
       im = str(result[i]) + ' '
       f.write(im)
    f.write('\n')   
    f.close()

##gender = mlab.test_gender(result)
##print gender
#GenerateFeatureValueText('gates.txt')
#GenerateFeatureValueTextTest()
##
#filename = r'D:\Python25\Deceptive\Gates.txt'
#count_number_blank_lines(filename)
#GenerateFeatureValueText(filename)

#print rohan



















