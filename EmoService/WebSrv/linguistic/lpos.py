import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)




def pos_tagging_from_dictionary(tokens, dictionary_pos):
    tags = []
    for t in tokens:
        word = t.lower()
        if word in dictionary_pos:
            tags.append((t, dictionary_pos[word]))
        else:
            tags.append((t, None))
    return tags




def translate_pos(tags, pos_names):
    named_tags = []
    for w,t in tags:
        if t in pos_names:
            nt = pos_names[t]
            named_tags.append((w, nt))
        else:
            named_tags.append((w, t))
    return named_tags
