import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)

import os
import os.path
import pypff
import re
import datetime
from dateutil.parser import parse

from textmining.utilities.utilfileio import write_text_file, read_from_json_file, save_to_json_file, load_file_list_from_path_recursive, PLATFORM

def parse_date(s):
    try:
        return parse(s)
    except:
        return


def parse_email_address(s):
    reg = r'([^@<>\'\"\s]+@[^@<>\'\"\s]+)'
    items = re.findall(reg, s)
    #print(s, items)
    return items


def process_header(header):
    if header is None:
        return

    ls = header.split('\n')

    date = ''
    from_email = ''
    to_emails = ''

    date_start = 'Date:'
    from_start = 'From:'
    to_start   = 'To:'

    """ example
    Date: Wed, 04 Oct 2017 10:06:01 EDT

    From: =?utf-8?B?RGVsbCBNUFA=?= <dell_epp@home.dell.com>

    To: doris.south@outlook.com
    """

    for i in ls:
        line = i.strip()
        if line.startswith(date_start):
            date = line.replace(date_start, '')
            date = parse_date(date)
        elif line.startswith(from_start):
            from_email = parse_email_address(line)
        elif line.startswith(to_start):
            to_emails = parse_email_address(line)

    return from_email, to_emails, date


def output_email(email_dict, output_dir):
    folder = os.path.join(output_dir, email_dict['sender'])
    if os.path.isdir(folder):
        pass
    else:
        os.makedirs(folder)

    file_name = email_dict['time'].strftime("%Y-%m-%d--%H-%M-%S-%f")
    configure_name = email_dict['sender']

    text_fn = os.path.join(folder, file_name + '.txt')
    json_fn = os.path.join(folder, file_name + '.json')

    if os.path.exists(text_fn):
        pass
    else:
        write_text_file(email_dict['email'], text_fn, encode='utf-8')

    if os.path.exists(json_fn):
        if email_dict['receivers'] is None:
            pass

        else:
            d = read_from_json_file(json_fn)
            receivers = email_dict['receivers']
            receivers.extend(d['receivers'])
            receivers = list(set(receivers))
            d['receivers'] = receivers
            save_to_json_file(
                d,
                json_fn
            )

    else:
        email_dict['time'] = email_dict['time'].strftime("%Y-%m-%d--%H-%M-%S-%f")
        save_to_json_file(
            email_dict,
            json_fn
        )



def process_email(message, output_dir, dict_keys):
    subject = message.subject
    sender_name = message.sender_name
    header = message.get_transport_headers()
    email_body = message.get_plain_text_body()

    if header is None:
        return
    if email_body is None:
        return

    email_body = email_body.decode("utf-8")

    sender_email, receivers_email, date = (None, None, None)
    r = process_header(header)

    if r is not None:
        sender_email, receivers_email, date = r
        if len(sender_email) == 1:
            sender_email = sender_email[0]
        else:
          return
    else:
        return

    if date is None:
        return

    #print('subject', subject)
    #print('sender', sender_name, sender_email)
    #print('date', date)
    #print('receivers', receivers_email)

    email_dict = {
        dict_keys['subject']: subject,
        dict_keys['sender_name']: sender_name,
        dict_keys['sender']:sender_email,
        dict_keys['time']: date,
        dict_keys['email']: email_body,
        dict_keys['receivers']:receivers_email,
    }
    output_email(email_dict, output_dir)


def check_emails_in_folder(folder, output_dir, dict_keys):
    count_msg = folder.get_number_of_sub_messages()

    for i in range(count_msg):
        message = folder.get_sub_message(i)
        message_dict = process_email(message, output_dir, dict_keys)


def folder_traverse(base, output_dir, dict_keys):
    for folder in base.sub_folders:
        if folder.number_of_sub_folders:
            folder_traverse(folder, output_dir, dict_keys)
        check_emails_in_folder(folder, output_dir, dict_keys)



def parse_pst_file(fn, output_dir, dict_keys):
    opst = pypff.open(fn)
    root = opst.get_root_folder()

    folder_traverse(root, output_dir, dict_keys)



def parse_pst_in_folder(input_dir, output_dir, dict_keys):
    pst_list = load_file_list_from_path_recursive(input_dir, '*.pst')

    total = len(pst_list)

    index = 0
    for fn in pst_list:
        #print(fn)
        index += 1
        parse_pst_file(fn, output_dir, dict_keys)

        yield('converting .PST {0}/{1} {2}%'.format(index, total, round(float(index) / float(total), 2)))

    return index





