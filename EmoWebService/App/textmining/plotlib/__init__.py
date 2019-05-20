import sys

python_version = sys.version_info[0]


if python_version == 2:
    from plotbar import *
    from ploterrorbars import *
    from plotfeatures import *
    from plotheat import *
    from plothistogram import *
    from plotlines import *
    from plotpie import *
    from plotscatter import *
    from plotseries import *
    from plottree import *
    from plotsubplots import *
elif python_version == 3:
    from .plotbar import *
    from .ploterrorbars import *
    from .plotfeatures import *
    from .plotheat import *
    from .plothistogram import *
    from .plotlines import *
    from .plotpie import *
    from .plotscatter import *
    from .plotseries import *
    from .plottree import *
    from .plotsubplots import *
else:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)
