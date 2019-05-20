#!/usr/local/bin/python3
# -*- coding: utf-8 -*-


import glob
import numpy as np
import os.path
from pickle import load, dump
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D



if __name__ == '__main__':

    fig = plt.figure()
    ax = fig.gca(projection='3d')
    number_of_emails_in_group = 4

    people_list = {
        'sherron':{'$regex':'sherron\.watkins@'},
        'krautz':{'$regex':'michael\.krautz@'},
        'shelby':{'$regex':'rex\.shelby@'},
        'brown':{'$regex':'james\.brown@'},
        'causey':{'$regex':'richard\.causey@'},
        'calger':{'$regex':'christopher\.calger@'},
        'despain':{'$regex':'tim\.despain@'},
        'hannon':{'$regex':'kevin\.hannon@'},
        'koenig':{'$regex':'mark\.koenig@'},
        'forney':{'$regex':'john\.forney@'},
        'rice':{'$regex':'ken\.rice@'},
        'rieker':{'$regex':'paula\.rieker@'},
        'fastow':{'$regex':'a.+\.fastow@'},
        'delainey':{'$regex':'david\.delainey@'},
        'glisan':{'$regex':'ben\.glisan@'},
        'richter':{'$regex':'jeff.*\.richter@'},
        'lawyer':{'$regex':'l.*\.lawyer@'},
        'belden':{'$regex':'t.*\.belden@'},
        'bowen':{'$regex':'r.*\.bowen@'},
        'colwell':{'$regex':'w.*\.colwell@'},
        'boyle':{'$regex':'d.*\.boyle@'},
    }

    file_list = []

    for person_name in people_list:
        fls = glob.glob('{0}/{1}_email_group_squence_*.pkl'.format(person_name, 'all'))
        fls.sort()
        print(fls[0:5])
        file_list.extend(fls)

    blue_center = [0.0, 0.0, 0.0]
    blue_dots = []
    blue_group = []
    red_dots = []
    tagged_blue = []

    index = 0

    for fn in file_list:
        print(fn)
        f = open(fn, 'rb')
        matrix_list = load(f)
        f.close()

        m = np.matrix(matrix_list)

        u,s,v = np.linalg.svd(m)

        x,y = u.shape

        blues = [0.0,0.0,0.0]
        count_blues = 0
        blue_single = []

        for i in range(x):
            #print(i)
            u1 = u.item((i, 0))
            u2 = u.item((i, 1))
            u3 = u.item((i, 2))
            tu = (u1,u2,u3)

            c = 'b'
            if i+1 == x:
                c = 'r'
                red_dots.append(tu)
            else:
                blue_center[0] += u1
                blue_center[1] += u2
                blue_center[2] += u3
                blues[0] += u1
                blues[1] += u2
                blues[2] += u3

                blue_single.append((u1,u2,u3))

                if u1 > 0.5:
                    tagged_blue.append(tu)
                    print(index, tu)

                index += 1
                count_blues += 1

            ax.scatter(u1,u2,u3, c=c, marker='o')


        blues[0] /= float(count_blues)
        blues[1] /= float(count_blues)
        blues[2] /= float(count_blues)
        blue_dots.append(blues)
        blue_group.append(blue_single)


    ax.set_xlabel('X')
    ax.set_ylabel('Y')
    ax.set_zlabel('Z')

    blue_center[0] /= float(index)
    blue_center[1] /= float(index)
    blue_center[2] /= float(index)

    result = (blue_center, blue_dots, blue_group, red_dots, tagged_blue)

    f = open('result_enron_email_group_squence_svd_distance.pkl', 'wb')
    dump(result, f)
    f.close()

    plt.show()

    print('End of Program')
