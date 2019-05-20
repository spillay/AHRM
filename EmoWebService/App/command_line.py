import time
import os.path

import sys

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


if config[0]:
    for i in parse_pst_in_folder(
        pst_dir,
        text_dir,
        {
            "sender":"sender",
            "sender_name":"sender_name",
            "subject":"subject",
            "time":"time",
            "email":"email",
            "receivers":"receivers",
        }
    ):
        print(i)
    print("finished parsing .PST files")

if config[1]:
    for i in transform_text_file_to_pickle(
        text_dir,
        input_dir,
        'enron_email_df_',
        ['label', 'email', 'time', 'date'],
        total_count
    ):
        print(i)
    print("finished regulating raw text")
    file_count = total_count[0]
if config[2]:
    for i in app_extract_emotions(
        input_dir,
        'enron_email_df_',
        emotion_dir,
        'emotion_',
        'email', 'time', 'date',
        total_count
    ):
        print(i)
    print("finished labeling emotions")
    label_count = total_count[1]
if config[3]:
    for i in app_analyze_emotions(
        emotion_dir,
        'emotion_',
        ax_dir,
        'analytics_',
        csv_dir,
        config[7],
    ):
        print(i)
    print("finished deep emotion analytics")
if config[4]:
    for i in app_plot_emotion_series(
            emotion_dir,
            'emotion_',
            ax_dir,
    ):
        print(i)
    print("finished plotting emotion detection results")
if config[5]:
    for i in app_plot_analytics_results(
        ax_dir,
        'analytics_',
        ax_dir,
    ):
        print(i)
    print("finished plotting analytics results")

if config[6]:
    print('CSV folder is', csv_dir)
    for i in app_plot_csv_results(
        csv_dir,
        'emotion_analysis_',
        csv_dir,
    ):
        print(i)
    print("finished plotting with CSV")

print('finish')


