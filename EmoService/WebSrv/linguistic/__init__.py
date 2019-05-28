import sys

python_version = sys.version_info[0]


if python_version == 2:
    from lpos import *
    from ltokenize import *
    import WebSrv
elif python_version == 3:
    from .lpos import *
    from .ltokenize import *
    import WebSrv
else:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)
