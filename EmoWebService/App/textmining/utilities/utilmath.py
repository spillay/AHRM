import sys

python_version = sys.version_info[0]
if python_version != 2 and python_version != 3:
    print('Error Python version {0}, quit'.format(python_version))
    exit(0)

    
def get_3d_centroid(data_list_3d):
    xx = []
    yy = []
    zz = []
    for x,y,z in data_list_3d:
        xx.append(x)
        yy.append(y)
        zz.append(z)
    length = len(data_list_3d)
    cx = sum(xx) / float(length)
    cy = sum(yy) / float(length)
    cz = sum(zz) / float(length)
    return (cx,cy,cz)



def get_3d_distance(pfrom, pto):
    if len(pfrom) < 3:
        return None
    if len(pto) < 3:
        return None
    x1 = pfrom[0]
    y1 = pfrom[1]
    z1 = pfrom[2]
    x2 = pto[0]
    y2 = pto[1]
    z2 = pto[2]
    d = (x1-x2)**2 + (y1-y2)**2 + (z1-z2)**2
    d = math.sqrt(d)
    return d
