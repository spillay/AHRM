import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)

import os
import os.path

from WebSrv.utilities import read_from_json_file

token_rules = [
    r'^\w+$',
    r'^([a-zA-Z0-9]+[\-\.@])+[a-zA-Z0-9]*$',
    r'^[a-zA-Z]+\'s$',
]

split_rules = [
    r'^([\(\[\{\<\"\'])([a-zA-Z]+)$',
    r'^([a-zA-Z]+)([\:\,\;\.\!\?\'\"\>\}\]\)])$',
]

#http://abbreviations.yourdictionary.com/articles/list-of-commonly-used-abbreviations.html

latin_abbreviations = {
    'e.g.':'for example',
    'i.e.':'',
    'etc.':'',
    'viz.':'',
    'n.b.':'',
}


internet_chats_abbreviations = {
    'ACE'    : 'a cool experience',
    'AD'     : 'awesome dude',
    'AFAIK'  : 'as far as I know',
    'AFK'    : 'away from keyboard',
    'ANI'    : 'age not important',
    'BRB'    : 'be right back',
    'CUL'    : 'see you later',
    'CWYL'   : 'chat with you later',
    'IIRC'   : 'if I recall/remember correctly',
    'IQ'     : 'ignorance quotient',
    'LOL'    : 'laugh-out-loud',
    'NP'     : 'no problem',
    'ROFL'   : 'rolling on the floor laughing',
    'TY'     : 'thank you',
    'WC'     : 'wrong conversation',
}


writing_abbreviations = {
    'R.S.V.P.'    :'respond, if you please',
    'P.S.'        :'post script.',
    'A.S.A.P.'    :'As soon as possible',
    'E.T.A.'      :'estimated time of arrival',
    'B.Y.O.B.'    :'Bring your own bottle',
    'D.I.Y.'      :'do it yourself',
    'F.Y.I.'      :'for your information',
}


def load_dictionary_pos(path):
    dictionary_pos = read_from_json_file(os.path.join(path, 'dictionary-pos.json'))
    return dictionary_pos
