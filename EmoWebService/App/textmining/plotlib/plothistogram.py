import pandas as pd
import numpy as np

import plotly
import plotly.plotly as py
import plotly.graph_objs as go
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot


def plotly_draw_hitogram(value_tuple, color_dict, bins, alpha, fn):
    if value_tuple is None:
        return
    print(value_tuple)

    max_value = 0

    dist= {}
    for s, c in value_tuple:
        if s in dist:
            dist[s].append(c)
        else:
            dist[s] = [c]
        if c > max_value:
            max_value = c

    data = []

    for e in dist:
        x = dist[e]
        c = 'b'
        if e in color_dict:
            c = color_dict[e]

        print(e, x)

        trace = go.Histogram(
            x=x,
            marker=dict(
                color=c,
                line=dict(
                    color='black',
                    width=0
                )
            ),
            opacity=alpha,
            #histnorm='probability',
            histnorm='count',
            name=e
        )
        data.append(trace)

    layout = go.Layout(
        barmode='group',
        legend=dict(
            x=0.8,
            y=1,
            font=dict(
                size=18,
            ),
        ),
        margin=dict(
            t=80,
        ),
        xaxis=dict(
            autorange=True,
            title='Running times',
            tickfont=dict(
                size=18,
            ),
            titlefont=dict(
                size=20,
            ),
        ),
        yaxis=dict(
            autorange=True,
            title='Count',
            tickfont=dict(
                size=18,
            ),
            titlefont=dict(
                size=20,
            ),
        )
    )
    figure = go.Figure(data=data, layout=layout)
    try:
        py.iplot(figure, filename=fn)
    except:
        pass
    py.image.save_as(figure, filename='{0}.pdf'.format(fn))



if __name__ == '__main__':
    exit(0)
