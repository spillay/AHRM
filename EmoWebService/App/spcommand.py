import time
import os
import os.path

import sys
from textmining.applications.appemotions import *

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)

from textmining import *


def expand_path(path):
    if len(path) > 2:
        if path.startswith('/'):
            return path
        elif path[1] == ':':
            return path
        elif path.startswith('\\'):
            return path
        else:
            return os.path.join(LOCAL_PATH, path)
    return path


LOCAL_PATH = os.path.dirname(os.path.dirname(TM_PATH))
print("LOCAL_PATH " + LOCAL_PATH)

configure_fn = os.path.join(LOCAL_PATH, 'config.json')
configure = read_from_json_file(configure_fn)

pst_dir = expand_path(configure['pst_dir'])
text_dir = expand_path(configure['text_dir'])
input_dir = expand_path(configure['input_dir'])
emotion_dir = expand_path(configure['emotion_dir'])
ax_dir = expand_path(configure['ax_dir'])
csv_dir = expand_path(configure['csv_dir'])

print("""
PST dir is:
{0}
Text dir is:
{1}
""".format(pst_dir, text_dir))

config = configure['check_box']
total_count = [configure["file_count"], configure["label_count"]]

#text_series = "[{'label': 'zshao2@stevens.edu', 'email': 'On the frequent flyer program topic you mentioned, I think you should sign up for another flight, if you can, since they are providing bonus mileage soon.\n\n\n', 'time': datetime.datetime(2016, 6, 28, 14, 12, 17), 'date': datetime.date(2016, 6, 28)}]"

main_path = os.path.join(os.path.dirname(__file__))
print("main path " + main_path)
liwc_data = read_from_json_file(
    os.path.join(main_path, 'textmining/data', 'liwc2015.json')
)
twelve_emotions = read_from_json_file(
    os.path.join(main_path, 'textmining/data', 'twelve_emotions_liwc.json')
)

extended_emotion_dict = read_from_pickle_file(
    os.path.join(main_path, 'textmining/data', 'extended_emotions.pkl')
)
    
    
    
text = "On the frequent flyer program topic you mentioned, I think you should sign up for another flight, if you can, since they are providing bonus mileage soon.\n\n\n'"
basic_emotions = {
        'anger'      : liwc_data['affective process']['anger'],
        'anxiety'    : liwc_data['affective process']['anxiety'],
        'sadness'    : liwc_data['affective process']['sadness'],
        'positivity' : liwc_data['affective process']['positive emotion'],
}

res = extract_basic_emotion(text, basic_emotions)
#print(res)
print("------------------end----------------------")
sherron_email = [
        """wow - you are really busy if your lunch calendar is booked for the remainder of the month! """,
        """sept. lunch sounds good. """,
        """i sure hope we make good use of the bad news about skilling's resignation and do some house cleaning -- can we write down some problem assets and unwind raptor? """,
        """i've been horribly uncomfortable about some of our accounting in the past few years and with the number of 'redeployments' up, i'm concerned some disgruntled employee will tattle. """,
        """can you influence some sanity?"""
 ]
emotions = {}
for email_text in sherron_email:
    #ec, ep, ew = extract_basic_emotion(email_text, basic_emotions)
    ec, ep, ew = extract_extended_emotion(email_text, twelve_emotions, extended_emotion_dict)
    print(ec)
    print(ep)
    print(ew)
    #merge_dict(emotions, ec)


