import sys

python_version = sys.version_info[0]


if python_version == 2:
    from grammar import *
    from pos import *
    from sentence import *
    from vocabulary import *
    from wordbank import *
elif python_version == 3:
    from .grammar import *
    from .pos import *
    from .sentence import *
    from .vocabulary import *
    from .wordbank import *
else:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)
