import pandas as pd
import numpy as np

import plotly
import plotly.plotly as py
import plotly.graph_objs as go
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot


def plotly_draw_stacked_bars(emotion_series, color_dict, xlabel, ylabel, index, fn):
    df = butils.df_from_json(emotion_series)
    df.fillna(0, inplace=True)

    cols = list(df.columns.values)
    cols.sort()
    rmlist = []
    for c in cols:
      if c not in color_dict:
        rmlist.append(c)

    for c in rmlist:
      cols.remove(c)

    dimension = len(emotion_series)
    X = range(1, dimension+1)
    if xlabel is None:
        Xlabel = X
    else:
        Xlabel = [xlabel + str(i) for i in X]

    print(Xlabel)

    data = []
    for e in cols:
        trace = go.Bar(
            x=Xlabel,
            y=df[e],
            name=e,
            marker=dict(
                color=color_dict[e],
                line=dict(
                    color=color_dict[e],
                )
            ),
            opacity=0.9,
        )
        data.append(trace)

    layout = go.Layout(
        barmode='stack',
        xaxis=dict(
            title=xlabel,
          ),
        yaxis=dict(
            title=ylabel,
        ),
    )

    figure = go.Figure(data=data, layout=layout)
    try:
        plot(figure, filename=fn)
    except:
        pass
    py.image.save_as(figure, filename='{0}.pdf'.format(fn))



if __name__ == '__main__':
    pass
