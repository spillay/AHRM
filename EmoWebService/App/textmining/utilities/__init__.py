import sys

python_version = sys.version_info[0]


if python_version == 2:
    #from utilpst import *
    from utilfileio import *
    from utilmath import *
    from utilstring import *
elif python_version == 3:
    #from .utilpst import *
    from .utilfileio import *
    from .utilmath import *
    from .utilstring import *
else:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)
