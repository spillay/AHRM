import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)



grammatical_units = [
    'word',
    'phrase',
    'clause',
    'sentence',
]

phrase = [
    'verb phrase',
    'noun phrase',
    'adjective phrase',
    'adverb phrase',
    'prepositional phrase'
]

word_class_level_01 = [
    'Verb',
    'Noun',
    'Adjective',
    'Adverb',
    'Preposition',
    'Determiner',
    'Pronoun',
    'Conjunction',
]


word_class_level_02 = {
    'Verb'          :[
        {'Ordinary-verb'    :[]},
        {'Auxiliary-verb'   :[
                'be', 'is', 'am', 'are', 'was', 'were',
                'have', 'has', 'had',
                'can', 'could',
                'dare',
                'do', 'did', ''
                'might', 'may',
                'need',
                'would', 'will',
                'should', 'shall',
                'ought',
                'must',
            ]},
        ],
    'Adverb'        :[
        'Manner-adverb',
        'Frequency-adverb',
        'Place-adverb',
        'Time-adverb',
        {'Linking-adverb'   :[
                {'Addition'     :[
                    'also',
                    'again',
                    'another',
                    'thus',
                    'furthermore',
                    'thereafter',
                    'in addition',
                    'moreover',
                    'additionally',
                    'besides',
                    'finally',
                    'meanwhile',
                    'on top of that',
                    ]},
                {'Detail'       :[
                    'namely',
                    ]},
                {'Alternative'  :[
                    'otherwise',
                    'rather',
                    ]},
                {'Cause-Effect' :[
                    'therefore',
                    'consequently',
                    'as a consequence',
                    'as a result',
                    'hence'
                    ]},
                {'Comparison'   :[
                    'likewise',
                    'in the same way',
                    'similarly',
                    'in contrast',
                    'unlike',
                    'just like',
                    'jsut as',
                    ]},
                {'Condition'    :[
                    'otherwise',
                    'in the event',
                    'anyway'
                    ]},
                {'Contrast'     :[
                    'nevertheless',
                    'nonetheless',
                    'on the other hand',
                    'in contrast to',
                    'however',
                    'instead',
                    ]},
                {'Emphasis'     :[
                    'indeed',
                    'in fact',
                    ]},
            ]},
        ],
    'Determiner'    :[
        {'Article-determiner'       :[
            'a', 'the', 'an'
            ]},
        {'Possessive-determiner'    :[
            'my', 'your', 'his', 'her'
            ]},
        {'Demonstrative-determiner' :[
            'this', 'that'
            ]},
        {'Quantifier-determiner'    :[
            'all',
            ]},
        ],
    'Conjunction'   :[
        {'Addition'     :['and']},
        {'Alternative'  :['or']},
        {'Cause-Effect' :['because', 'Accordingly',]},
        {'Comparison'   :[]},
        {'Condition'    :['if']},
        {'Contrast'     :['']},
        {'Emphasis'     :[]},
        ],
}

sentence_element = [
    'Subject',
    'Verb',
    'Object',
    'Complement',
    'Adverbial',
]

meaning_indication = [
    'object',
    'person',
    'place',
    'time',
    'action',
    'description',
    'behavior',
]



verb_abbreviations = {
    "'s"        :'is', # is was has
    "'re"       :'are', # are were
    "'m"        :'am',
    "'ve"       :'have',
    "'d"        :'had', # had would
    "'ll"       :'will',
}


phrase_abbreviations = {
    'FYI'       : 'for your information',
    'ASAP'      : 'as soon as possible',
}


pos_full_names = {
    # see nltk.help.upenn_tagset()
    '$'         : 'dollar',
    "'"         : 'quotation mark',
    '"'         : 'double quotation mark',
    '('         : 'opening parenthesis',
    ')'         : 'closing parenthesis',
    '['         : 'opening square',
    ']'         : 'closing square',
    '{'         : 'opening bracket',
    '}'         : 'closing bracket',
    ','         : 'comma',
    '--'        : 'dash',
    '.'         : 'dot sentence terminator',
    '!'         : 'exclamation sentence terminator',
    '?'         : 'question sentence terminator',
    ':'         : 'colon',
    ';'         : 'semicolon',
    '...'       : 'ellipsis',
    'CC'        : 'conjunction', # & 'n and both but either et for less minus neither nor or plus so therefore times v. versus vs. whether yet
    'CD'        : 'numeral',
    'DT'        : 'determiner', # all an another any both del each either every half la many much nary neither no some such that the them these this those
    'EX'        : 'existential there', # there
    'FW'        : 'foreign word',
    'IN'        : 'preposition or conjunction, subordinating', # astride among uppon whether out inside pro despite on by throughout below within for towards near behind atop around if like until below next into if beside ...
    'JJ'        : 'adjective',
    'JJR'       : 'comparative adjective',
    'JJS'       : 'superlative adjective',
    'LS'        : 'list item marker',
    'MD'        : 'modal auxiliary', # can cannot could couldn't dare may might must need ought shall should shouldn't will would
    'NN'        : 'noun',
    'NNP'       : 'proper noun',
    'NNPS'      : 'proper plural noun',
    'NNS'       : 'plural noun',
    'PDT'       : 'pre-determiner', # all both half many quite such sure this
    'POS'       : 'genitive marker', # 's
    'PRP'       : 'pronoun', # hers herself him himself hisself it itself me myself one oneself ours ourselves ownself self she thee theirs them themselves they thou thy us
    'PRP$'      : 'possessive pronoun', # her his mine my our ours their thy your
    'RB'        : 'adverb',
    'RBR'       : 'comparative adverb',
    'RBS'       : 'superlative adverb',
    'RP'        : 'particle', # aboard about across along apart around aside at away back before behind by crop down ever fast for forth from go high i.e. in into just later low more off on open out over per pie raising start teeth that through under unto up up-pp upon whole with you
    'SYM'       : 'symbol',
    'TO'        : "to",
    'UH'        : 'interjection',
    'VB'        : 'verb', # do
    'VBD'       : 'past verb did', # did
    'VBG'       : 'gerund verb doing', # do-ing
    'VBN'       : 'past participle verb done', # done
    'VBP'       : 'present verb do', # do
    'VBZ'       : 'present verb does', # does
    'WDT'       : 'wh-determiner', # that what whatever which whichever
    'WP'        : 'wh-pronoun', # that what whatever whatsoever which who whom whosoever
    'WP$'       : 'whose', # possessive WH-pronoun, whose
    'WRB'       : 'wh-adverb', # how however whence whenever where whereby whereever wherein whereof why
}


pos_short_names = {
    # see nltk.help.upenn_tagset()
    '$'         : '$',
    "'"         : "'",
    '"'         : '"',
    '('         : '(',
    ')'         : ')',
    '['         : '[',
    ']'         : ']',
    '{'         : '{',
    '}'         : '}',
    ','         : ',',
    '--'        : '--',
    '.'         : '.',
    '!'         : '!',
    '?'         : '?',
    ':'         : ':',
    ';'         : ';',
    '...'       : '...',
    'CC'        : 'conjunction', # & 'n and both but either et for less minus neither nor or plus so therefore times v. versus vs. whether yet
    'CD'        : 'number',
    'DT'        : 'determiner', # all an another any both del each either every half la many much nary neither no some such that the them these this those
    'EX'        : 'there', # there
    'FW'        : 'foreign',
    'IN'        : 'preposition', # astride among uppon whether out inside pro despite on by throughout below within for towards near behind atop around if like until below next into if beside ...
    'JJ'        : 'adjective',
    'JJR'       : 'adjective-er',
    'JJS'       : 'adjective-est',
    'LS'        : 'list',
    'MD'        : 'auxiliary', # can cannot could couldn't dare may might must need ought shall should shouldn't will would
    'NN'        : 'object',
    'NNP'       : 'Object',
    'NNPS'      : 'Objects',
    'NNS'       : 'objects',
    'PDT'       : 'pre-determiner', # all both half many quite such sure this
    'POS'       : 'genitive', # 's
    'PRP'       : 'it', # hers herself him himself hisself it itself me myself one oneself ours ourselves ownself self she thee theirs them themselves they thou thy us
    'PRP$'      : 'its', # her his mine my our ours their thy your
    'RB'        : 'adverb',
    'RBR'       : 'adverb-er',
    'RBS'       : 'adverb-est',
    'RP'        : 'particle', # aboard about across along apart around aside at away back before behind by crop down ever fast for forth from go high i.e. in into just later low more off on open out over per pie raising start teeth that through under unto up up-pp upon whole with you
    'SYM'       : 'symbol',
    'TO'        : "to",
    'UH'        : 'interjection',
    'VB'        : 'do', # do
    'VBD'       : 'did', # did
    'VBG'       : 'doing', # do-ing
    'VBN'       : 'done', # done
    'VBP'       : 'do', # do
    'VBZ'       : 'does', # does
    'WDT'       : 'wh-determiner', # that what whatever which whichever
    'WP'        : 'wh-pronoun', # that what whatever whatsoever which who whom whosoever
    'WP$'       : 'whose', # possessive WH-pronoun, whose
    'WRB'       : 'wh-adverb', # how however whence whenever where whereby whereever wherein whereof why
}


word_replacement = {
    '('                 :'(',
    ')'                 :')',
    '['                 :'[',
    ']'                 :']',
    '{'                 :'{',
    '}'                 :'}',
    'am'                :'be',
    'is'                :'be',
    'are'               :'be',
    'was'               :'be',
    'were'              :'be',
    'be'                :'be',
    'being'             :'being',
    'been'              :'been',
    'has'               :'have',
    'have'              :'have',
    'had'               :'have',
    #'the'               :'the',
    #
    'and'               :'and',
    'or'                :'or',
    'but'               :'but',
    'nor'               :'nor',
    'so'                :'so',
    'yet'               :'yet',
    'of'                :'of',
    'for'               :'for',
    'by'                :'by',
    #
    'on'                :'on',
    'at'                :'at',
    'in'                :'in',
    #
    'January'           :'time',
    'February'          :'time',
    'March'             :'time',
    'April'             :'time',
    'May'               :'time',
    'June'              :'time',
    'July'              :'time',
    'August'            :'time',
    'September'         :'time',
    'October'           :'time',
    'November'          :'time',
    'December'          :'time',
    #
    'Jan.'              :'time',
    'Feb.'              :'time',
    'Aug.'              :'time',
    'Sept.'             :'time',
    'Oct.'              :'time',
    'Nov.'              :'time',
    'Dec.'              :'time',
    #
    'Monday'            :'time',
    'Tuesday'           :'time',
    'Wednesday'         :'time',
    'Thursday'          :'time',
    'Friday'            :'time',
    'Saturday'          :'time',
    'Sunday'            :'time',
}




conj_replacement = {
    #
    'though'            :'though',
    'although'          :'although',
    'even'              :'even', #even though
    'while'             :'while',
    #
    'only'              :'only',
    'as'                :'as',
    'lest'              :'lest',
    #
    'if'                :'if',
    'whether'           :'whether',
    'though'            :'though',
    'although'          :'although',
    'unless'            :'unless',
    'until'             :'until',
    #
    'because'           :'because',
    'therefore'         :'therefore',
    'thus'              :'thus',
    'since'             :'since',
    #'why'               :'why',
    #
    'during'            :'during',
    'till'              :'till',
    'until'             :'until',
    #
    #'that'              :'that',
}


conj_subordinating_chunk_stack = [
    # Concession
    ("<even><though><SVO>",                                                 "Chunk Concession"),
    ("<though><SVO>",                                                       "Chunk Concession"),
    ("<although><SVO>",                                                     "Chunk Concession"),
    ("<while><SVO>",                                                        "Chunk Concession"),
    # Condition
    ("<even><if><SVO>",                                                     "Chunk Condition"),
    ("<only><if><SVO>",                                                     "Chunk Condition"),
    ("<in><case><SVO>",                                                     "Chunk Condition"),
    ("<provided><that><SVO>",                                               "Chunk Condition"),
    ("<assuming><that><SVO>",                                               "Chunk Condition"),
    ("<unless><SVO>",                                                       "Chunk Condition"),
    ("<until><SVO>",                                                        "Chunk Condition"),
    ("<least><SVO>",                                                        "Chunk Condition"),
    ("<till><SVO>",                                                         "Chunk Condition"),
    # Comparison
    ("<rather><than><SVO>",                                                 "Chunk Comparison"),
    ("<as><much><as><SVO>",                                                 "Chunk Comparison"),
    ("<than><SVO>",                                                         "Chunk Comparison"),
    ("<whether><SVO>",                                                      "Chunk Comparison"),
    ("<whereas><SVO>",                                                      "Chunk Comparison"),
    # Time
    ("<as><long><as><SVO>",                                                 "Chunk Time"),
    ("<as><soon><as><SVO>",                                                 "Chunk Time"),
    ("<by><the><time><SVO>",                                                "Chunk Time"),
    ("<now><that><SVO>",                                                    "Chunk Time"),
    ("<after><time|Object|object|Ojbects|objects|ObjectG|SVO>",             "Chunk Time"),
    ("<before><time|Object|object|Ojbects|objects|ObjectG|SVO>",            "Chunk Time"),
    ("<once><time|Object|object|Ojbects|objects|ObjectG|SVO>",              "Chunk Time"),
    ("<since><time|Object|object|Ojbects|objects|ObjectG|SVO>",             "Chunk Time"),
    ("<till><time|Object|object|Ojbects|objects|ObjectG|SVO>",              "Chunk Time"),
    ("<until><time|Object|object|Ojbects|objects|ObjectG|SVO>",             "Chunk Time"),
    ("<when><SVO>",                                                         "Chunk Time"),
    ("<whenver><SVO>",                                                      "Chunk Time"),
    ("<while><SVO>",                                                        "Chunk Time"),
    # Reason
    ("<in><order><that><SVO>",                                              "Chunk Reason"),
    ("<so><that><SVO>",                                                     "Chunk Reason"),
    ("<because><SVO>",                                                      "Chunk Reason"),
    ("<because><of><Object|object|Ojbects|objects|ObjectG|SVO>",            "Chunk Reason"),
    ("<why><SVO>",                                                          "Chunk Reason"),
    ("<since><SVO>",                                                        "Chunk Reason"),
    # Adjective
    ("<that><SVO>",                                                         "Chunk Adjective"),
    ("<what><SVO>",                                                         "Chunk Adjective"),
    ("<which><SVO>",                                                        "Chunk Adjective"),
    ("<whatever><SVO>",                                                     "Chunk Adjective"),
    ("<whichever><SVO>",                                                    "Chunk Adjective"),
    # Pronoun
    ("<who><SVO>",                                                          "Chunk Pronoun"),
    ("<whom><SVO>",                                                         "Chunk Pronoun"),
    ("<whose><SVO>",                                                        "Chunk Pronoun"),
    ("<whoever><SVO>",                                                      "Chunk Pronoun"),
    ("<whomever><SVO>",                                                     "Chunk Pronoun"),
    # Manner
    ("<as><though><SVO>",                                                   "Chunk Manner"),
    ("<as><if><SVO>",                                                       "Chunk Manner"),
    ("<how><SVO>",                                                          "Chunk Manner"),
    # Place
    ("<where><SVO>",                                                        "Chunk Place"),
    ("<wherever><SVO>",                                                     "Chunk Place"),
]

conj_adv_chunk_stack = [
    # And
    ("<also><,>*",                                                          "Chunk And"),
    ("<and><,>*",                                                           "Chunk And"),
    ("<besides><,>*",                                                       "Chunk And"),
    ("<furthermore><,>*",                                                   "Chunk And"),
    ("<likewise><,>*",                                                      "Chunk And"),
    ("<moreover><,>*",                                                      "Chunk And"),
    # But
    ("<however><,>*",                                                       "Chunk But"),
    ("<nevertheless><,>*",                                                  "Chunk But"),
    ("<nonetheless><,>*",                                                   "Chunk But"),
    ("<still><,>*",                                                         "Chunk But"),
    ("<conversely><,>*",                                                    "Chunk But"),
    ("<instead><,>*",                                                       "Chunk But"),
    ("<otherwise><,>*",                                                     "Chunk But"),
    ("<rather><,>*",                                                        "Chunk But"),
    # So
    ("<accordingly><,>*",                                                   "Chunk So"),
    ("<consequently><,>*",                                                  "Chunk So"),
    ("<hence><,>*",                                                         "Chunk So"),
    ("<meanwhile><,>*",                                                     "Chunk So"),
    ("<then><,>*",                                                          "Chunk So"),
    ("<therefore><,>*",                                                     "Chunk So"),
    ("<thus><,>*",                                                          "Chunk So"),
]

#Correlative Conjunctions
#as . . . as
#just as . . . so
#both . . . and
#hardly . . . when
#scarcely . . . when
#either . . . or
#neither . . . nor
#
#if . . . then
#not . . . but
#what with . . . and
#whether . . . or
#not only . . . but also
#no sooner . . . than
#rather . . . than



grammar_stack = [
    "<have><to><do>",
    "<time>*<,>*<number>+"
    "<adjective.*>*<o|Object.*>+<number>*"
    "<to><do>",
    "<have><been><doing>",
    "<be><doing>",
    "<have><done>",
    "<be>",
]

grammar = r'''
    time    :   {<time>*<,>*<number>+}
    verb    :   {<have><to><do>}
    to-do   :   {<to><do>}
    verb    :   {<have><been><doing>}
    verb    :   {<be><doing>}
    verb    :   {<have><done>}
    verb    :   {<be>}
    object  :   {<.*determiner>*<adjective.*>*<o|Object.*>+<number>*}
    object  :   {<o|Object.*>}
    NP: {<DET|PRON>?<ADJ>*<NOUNGROUP>+}
    VP: {<ADV>*<PRT>*<ADV>*<VERBGROUP>+<ADV>*}
    VPP: {<ADP>*<VERB><ADP>*}
    PRTPHRS: {<PRT><NP>}
    SENT: {<NP|DET><VP><NP>*<PRTPHRS>*}
    CONJSENT: {<CONJ><SENT>}
'''