import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)

from textmining.utilities import write_text_file

template = """<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title></title>
    </head>
    <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
    <body>
    {0}
    </body>
</html>
"""

def output_subplots(fig_list, fn):
    s = '\n\n'.join(fig_list)
    ss = template.format(s)
    write_text_file(ss, fn, 'utf-8')

