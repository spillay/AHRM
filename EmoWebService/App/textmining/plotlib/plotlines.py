import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)

from operator import itemgetter

import os
import os.path
import pandas as pd
import numpy as np


import plotly
import plotly.plotly as py
import plotly.graph_objs as go
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot


def plotly_draw_stacked_lines(emotion_series, color_dict, xlabel, ylabel, file_label, fn, offline=None, to_div=False):
    df = emotion_series
    if isinstance(df, list):
        df = pd.DataFrame(emotion_series)
    df.fillna(0, inplace=True)

    cols = list(df.columns.values)
    cols.sort()
    #print('cols', cols)

    rmlist = []
    for c in cols:
        if c not in color_dict:
            rmlist.append(c)

    #print('rmlist', rmlist)

    for c in rmlist:
        cols.remove(c)

    #print('cols', cols)

    dimension = len(emotion_series)
    X = range(1, dimension+1)
    x_title = xlabel
    if xlabel is None:
        Xlabel = X
    elif isinstance(xlabel, str):
        Xlabel = [xlabel + str(i) for i in X]
        x_title = ''
    elif isinstance(xlabel, list):
        Xlabel = [str(i) for i in xlabel]
        x_title = ''
    else:
        Xlabel = X

    #print('Xlabel', Xlabel)

    data = []
    for e in cols:
        trace = go.Scatter(
            x=Xlabel,
            y=df[e],
            name=e,
            mode='lines+markers',
            marker=dict(
                color=color_dict[e],
                line=dict(
                    color=color_dict[e],
                ),
                opacity=0.65,
                symbol="hexagram-open",
                size=8,
            ),
            opacity=0.65,
            line=dict(
                width=4.0,
                color=color_dict[e],
            ),
        )
        data.append(trace)

    layout = go.Layout(
        autosize=False,
        width=1400,
        height=1000,
        title=file_label.replace('_', ' '),
        #hovermode="closest",
        legend=dict(
            x=0.5,
            y=1.05,
            font=dict(
                size=15,
            ),
            orientation="h",
            traceorder="normal",
            xanchor="center",
            yanchor="bottom",
        ),
        margin=dict(
            t=200,
            b=300,
        ),
        xaxis=dict(
                title=x_title,
                tickfont=dict(
                    size=15,
                ),
                titlefont=dict(
                    size=15,
                ),
            ),
        yaxis=dict(
                title=ylabel,
                tickfont=dict(
                    size=15,
                ),
                titlefont=dict(
                    size=15,
                ),
        ),
    )

    #print('data length', len(data))

    figure = go.Figure(data=data, layout=layout)
    if to_div:
        # export to html div
        s = plot(figure, include_plotlyjs=False, output_type='div')
        return s
    else:
        # plot to a single file
        if offline is not None:
            if not os.path.isdir(offline):
                os.makedirs(offline)
            plot(figure, filename=os.path.join(offline, fn+'_'+file_label), show_link=False, validate=False, auto_open=False)
        else:
            try:
                py.iplot(figure, filename=fn)
                py.image.save_as(figure, filename=os.path.join(offline, '{0}.pdf'.format(fn)))
            except:
                pass


