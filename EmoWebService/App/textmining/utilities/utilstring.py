import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)

import re
import string




def is_token_word(token):
    if re.search(r'^[a-zA-Z]+$', token) is not None:
        return True
    return False



def is_token_punctuation(token):
    if len(token) == 1:
        if token in string.punctuation:
            return True
    return False



def is_token_number(token):
    if re.match(r'[0-9]+st|nd|rd|th', token) is not None:
        return True
    elif re.match(r'[0-9]+', token) is not None:
        return True
    return False




if __name__ == '__main__':

    print(is_token_word('first'))
    print(is_token_punctuation(','))
    print(is_token_punctuation(',,'))
    print(is_token_number('1950'))
    print(is_token_number('1st'))
    print(is_token_number('2st'))
    print(is_token_number('3st'))
    print(is_token_number('15st'))
    print(is_token_number('first'))
