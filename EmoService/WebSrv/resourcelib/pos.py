import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)



pos_names = {
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
