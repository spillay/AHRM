import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)


import plotly
import plotly.plotly as py
import plotly.graph_objs as go
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot


def map_data_frequency(y):
    d = {}

    for i in y:
        if i in d:
            d[i] += 1
        else:
            d[i]  = 1

    ls = list(d.items())
    ls.sort(key=lambda tup: tup[0])

    return zip(*ls)


def plotly_draw_scatter_samples(emotion_list_dict, color_pair_list, title, fn, offline=None, to_div=False):

    data = []

    emotions = []

    for emotion_name, emotion_color in color_pair_list:
        if emotion_name in emotion_list_dict:
            emotions.append(emotion_name)
            y = emotion_list_dict[emotion_name]
            ls = map_data_frequency(y)
            y,z = tuple(ls)
            x = [emotion_name] * len(y)

            #print('x', x)
            #print('y', y)
            #print('z', z)

            text = ['{0} Burst Length {1} Count {2}'.format(emotion_name, i,j) for i,j in zip(y,z)]

            msize = [10+4*i for i in z]

            trace= go.Scatter(
                x=y,
                y=z,
                text=text,
                mode='markers',
                marker=dict(
                    size=msize,
                    line=dict(width=1,color=emotion_color),
                    color=emotion_color,
                    opacity= 0.5
                ),
                name=emotion_name,
            )

            data.append(trace)

    if len(data) == 0:
        return

    xaxis = go.XAxis(
        title="Running Length",
        showticklabels=True,
        linewidth=2,
        ticktext=emotions,
    )
    yaxis = go.YAxis(
        title="Occurrence",
        ticks="inside",
        linewidth=2,
        tickwidth=2,
        zerolinewidth=2
    )

    layout = go.Layout(
        title=title,
        xaxis=xaxis,
        yaxis=yaxis,
    )

    figure = go.Figure(data=data, layout=layout)

    if to_div:
        s = plot(figure, include_plotlyjs=False, output_type='div')
        return s
    else:
        if offline is not None:
            if not os.path.isdir(offline):
                os.makedirs(offline)
            plot(figure, filename=os.path.join(offline, fn), show_link=False, validate=False, auto_open=False)
        else:
            py.iplot(figure, filename=fn)
            py.image.save_as(figure, filename='{0}.pdf'.format(fn))




if __name__ == '__main__':

    emotion_list_dict = {
        u'Relief':[4,5,7,8,3,3,6,7,3],
        u'Anger':[5,3,3,3]
    }

    extended_colors = [
        (u'Relief'          ,   '#69CBDE' ),
        (u'Contentment'     ,   '#7CD3F7' ),
        (u'Agreeableness'   ,   '#66C07F' ),
        (u'Interest'        ,   '#90D2C1' ),
        (u'Pride'           ,   '#B589BD' ),
        (u'Joy'             ,   '#478DCB' ),
        (u'Shame'           ,   '#941A1D' ),
        (u'Disgust'         ,   '#EE3B90' ),
        (u'Fear'            ,   '#FFD579' ),
        (u'Anxiety'         ,   '#935523' ),
        (u'Sadness'         ,   '#F7931E' ),
        (u'Anger'           ,   '#EE3624' ),
        (u'NA'              ,   '#000000' ),
    ]

    plotly_draw_scatter_samples(
        emotion_list_dict, extended_colors,
        'emotion running length',
        'emotion-running-length',
        offline=None, to_div=False
    )