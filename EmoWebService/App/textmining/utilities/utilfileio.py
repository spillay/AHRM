import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)

import json
import pickle
import csv
import chardet
import codecs
import os
import os.path
import sys
import string
import glob
import platform
import time
import datetime
import xml.etree.ElementTree as ET
from unidecode import unidecode


PLATFORM = platform.system()
PLATFORM = PLATFORM.lower()

MAC = 'darwin'
WIN = 'windows'

print('Platform is {0}'.format(PLATFORM))

TM_PATH = os.path.dirname(os.path.dirname(__file__))

if PLATFORM == WIN:
    TM_PATH = TM_PATH.replace('/', '\\')
elif PLATFORM == MAC:
    TM_PATH = TM_PATH.replace('\\', '/')

NLTK_PATH = os.path.join(TM_PATH, 'nltk_data')


class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'


def load_text_file(fn):
    #print(fn)
    printable = set(string.printable)
    txt = ""

    f = open(fn, 'rb')
    binary = f.read()
    f.close()

    encoding = chardet.detect(binary)
    if 'encoding' in encoding:
        encoding = encoding['encoding']
        print('file encoding is {0} for file {1}'.format(encoding, os.path.basename(fn)))
    else:
        print(bcolors.FAIL + 'WARNING: cannot decode file {0}'.format(os.path.basename(fn)) + + bcolors.ENDC)
        print('using no encoding scheme')
        try:
            f = open(fn, 'r', errors='ignore')
            txt = f.read()
            f.close()
            txt = unidecode(txt)
        except:
            return ''
    try:
        f = open(fn, 'r', encoding=encoding, errors='ignore')
        txt = f.read()
        f.close()
        txt = unidecode(txt)
    except:
        print('using no encoding scheme')
        try:
            f = open(fn, 'r', errors='ignore')
            txt = f.read()
            f.close()
            txt = unidecode(txt)
        except:
            return ''
    return txt



def write_text_file(text, fn, encode=None):
    if encode is None:
        f = open(fn, 'w')
        f.write(text)
        f.close()
        return
    f = codecs.open(fn, 'w', encode)
    f.write(text)
    f.close()
    return



def read_from_json_file(fn):
    f = open(fn, 'r')
    data = json.load(f)
    f.close()
    return data



def save_to_json_file(data, fn):
    f = open(fn, 'w')
    json.dump(
        data, f,
        sort_keys=True,
        indent=4,
        separators=(',', ': '),
    )
    f.close()



def read_from_pickle_file(fn):
    f = open(fn, 'rb')
    data = pickle.load(f)
    f.close()
    return data



def save_to_pickle_file(data, fn):
    f = open(fn, 'wb')
    pickle.dump(data, f, protocol=2)
    f.close()



def read_from_csv_file(fn, encode='utf-8'):
    rows = []
    with codecs.open(fn, 'rb', encode) as csvfile:
        csvfilereader = csv.reader(csvfile, quotechar='"')
        for row in csvfilereader:
            row_data = []
            for i in row:
                row_data.append(i.strip())
            if len(row_data) != 0:
                rows.append(row_data)
    return rows


def read_from_xml_file(fn):
    tree = ET.parse(fn)
    root = tree.getroot()
    return root



def find_file_names_exclude(path, exclude_ls=[]):
    results = []
    ls = os.listdir(path)
    for i in ls:
        pathi = os.path.join(path, i)
        if os.path.isdir(pathi):
            results.extend(find_file_names_exclude(pathi, exclude_ls))
        elif os.path.isfile(pathi):
            is_included = True
            for e in exclude_ls:
                if e in i:
                    is_included = False
                    break
            if is_included:
                results.append(pathi)
    return results



def find_file_names_include(path, include_ls=[]):
    results = []
    ls = os.listdir(path)
    for i in ls:
        pathi = os.path.join(path, i)
        if os.path.isdir(pathi):
            results.extend(find_file_names_include(pathi, include_ls))
        elif os.path.isfile(pathi):
            is_included = False
            for e in include_ls:
                if e in i:
                    is_included = True
                    break
            if is_included:
                results.append(pathi)
    return results



def load_file_list_from_path(path, prefix):
    patterns = os.path.join(path, prefix)
    file_list = glob.glob(patterns)
    return file_list



def load_file_list_from_path_recursive(path, prefix):
    results = []
    ls = os.listdir(path)
    for i in ls:
        pathi = os.path.join(path, i)
        if os.path.isdir(pathi):
            results.extend(load_file_list_from_path_recursive(pathi, prefix))
    results.extend(glob.glob(os.path.join(path, prefix)))
    return results


def translate_time_stamp(s):
    ss = s.replace('_', '-')
    while '--' in ss:
        ss = s.replace('--', '-')
    ss = ss.split('-')

    year, month, day, hour, minute, sec, millisec = (
        0, 0, 0, 0, 0, 0, 0
    )

    print('time_stamp', ss)

    if len(ss) > 6:
        year, month, day, hour, minute, sec, millisec = (
            int(ss[0]), int(ss[1]), int(ss[2]), int(ss[3]), int(ss[4]), int(ss[5]), int(ss[6])
        )
    elif len(ss) > 5:
        year, month, day, hour, minute, sec, millisec = (
            int(ss[0]), int(ss[1]), int(ss[2]), int(ss[3]), int(ss[4]), int(ss[5]),          0,
        )
    elif len(ss) > 4:
        year, month, day, hour, minute, sec, millisec = (
            int(ss[0]), int(ss[1]), int(ss[2]), int(ss[3]), int(ss[4]),          0,          0,
        )
    elif len(ss) > 3:
        year, month, day, hour, minute, sec, millisec = (
            int(ss[0]), int(ss[1]), int(ss[2]), int(ss[3]),          0,          0,          0,
        )
    elif len(ss) > 2:
        year, month, day, hour, minute, sec, millisec = (
            int(ss[0]), int(ss[1]), int(ss[2]),          0,          0,          0,          0,
        )
    elif len(ss) > 1:
        year, month, day, hour, minute, sec, millisec = (
            int(ss[0]), int(ss[1]),          0,          0,          0,          0,          0,
        )

    millisec = millisec % 1000000

    return datetime.datetime(year, month, day, hour, minute, sec, millisec)


def transform_text_file_to_pickle(folder, output_folder, output_pattern, dict_keys, total_unit_count):
    file_list = load_file_list_from_path_recursive(folder, '*.txt')
    file_list.sort()
    total_file_count = len(file_list)

    file_group_list = {}

    index = 0
    for fn in file_list:
        index += 1
        if index % 100 == 0:
            s_out = 'processing {0}/{1}'.format(index, total_file_count)
            yield(s_out)
            sys.stdout.write('\r')
            # the exact output you're looking for:
            sys.stdout.write(s_out)
            sys.stdout.flush()

        file_dir = os.path.dirname(fn)
        file_name = os.path.basename(fn)
        file_name = file_name.replace('.txt', '')
        file_label = os.path.basename(file_dir)
        print('read info', file_label, file_name)
        time = translate_time_stamp(file_name)
        date = time.date()
        print('parsed timestamp', file_label, time)
        text = load_text_file(fn)
        print('read files', file_label, file_name)

        d = {
            dict_keys[0]:file_label,
            dict_keys[1]:text,
            dict_keys[2]:time,
            dict_keys[3]:date,
        }

        if file_label in file_group_list:
            file_group_list[file_label].append(d)
        else:
            file_group_list[file_label] = [d]

        #json_file_name = time.strftime("%Y-%m-%d--%H-%M-%S-%f")
        #save_to_json_file(
        #    d,
        #    os.path.join(file_dir, json_file_name+'_from_text.json')
        #)

    yield('saving to files ...')
    total_label_count = len(file_group_list)
    index = 0
    for label in file_group_list:
        index += 1
        #print('saving {0}/{1}'.format(index, total_label_count))
        output_fn = os.path.join(output_folder, output_pattern+label+'.pkl')
        save_to_pickle_file(file_group_list[label], output_fn)

        print('saved pickle file', label)

    total_unit_count[0] = total_file_count

    return index


