import sys

python_version = sys.version_info[0]


if python_version == 2:
    from applications import *
    from linguistic import *
    from plotlib import *
    from resourcelib import *
    from staticlib import *
    from utilities import *
elif python_version == 3:
    from .applications import *
    from .linguistic import *
    from .plotlib import *
    from .resourcelib import *
    from .staticlib import *
    from .utilities import *
else:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)
