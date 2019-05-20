import os, sys
from liwc import liwc,loadcues, makedistribution
from ppmc import getentropy,preprocess, isdeceptive
#from spreadsheet import SimpleCRUD
#from gluon.tools import Auth
#from gluon.tools import Service
#import text_db
import re
import extract_feature_values as gender

#print 'in deception'



FilePath = os.path.abspath(os.path.dirname(sys.argv[0]))
FileGender = FilePath + '\\applications\\init\\genders\\'
FileUploads = FilePath + '\\applications\\init\\uploads\\'
FileCues = FilePath + '\\cues\\'

TEXT_DATA='My name is Rohan'


def concat(a,b):
    return a+b


def analyze(newtext):
    from ppmc_partdetect import ppmc_detect
    result,tmp2 = ppmc_detect(newtext)
    return result,tmp2



def analyze_gender(newtext):
    import uuid
    import os.path
    #from time import time

    text_title = newtext
    
    #start = time()  #  start time
    
    Item_name = ('Experimental', 'Linguistic','Personal','Pyschological','Relativity')
    row_liwc = 0
    Item_liwc = {}
    for filename in os.listdir(FileCues):
        cues_relativity = open(FileCues + filename).readlines()
        cues_num = 0
        for cue in cues_relativity:
            if '*' in cue:
                result_tmp = re.findall(cue[:-2].lower(),text_title)
                cues_num += len(result_tmp)
            else:
                result_tmp = re.findall(cue[:-1].lower(),text_title)
                cues_num += len(result_tmp)
        Item_liwc[Item_name[row_liwc]] = cues_num
        
        #print row_liwc, filename, cues_num
        row_liwc += 1
        
        
    import urllib
    random_name = str(uuid.uuid4())
    Test_name = FileUploads  + random_name + '_test.txt'
    Result_name =  'genderresult.txt'
    
    while os.path.exists(Test_name) == False :
    
        #MyUser = session.auth.user.id
        #MyDate = str(request.utcnow)[:19]

        record_id = '' 
        f = open(Test_name,'w')
        f.write(text_title)
        f.close()
        
        gender.GenerateFeatureValueText(Test_name,random_name)
        FileSvmFeature = FileUploads + 'featuredtext_' + random_name+ '.txt'
        
        gender_command = FileGender+ 'test_gender.exe ' + FileSvmFeature 
        print(gender_command)
        
        try:
           os.system(gender_command)
        except Exception(e): 
           print(e)
           
        MyResult = open(Result_name).read().split('\n')
        MyPrediction , MyConfidence = MyResult[0], MyResult[1]
        
        return MyPrediction , MyConfidence



result,value=analyze(TEXT_DATA)
print("Value of Result=",result)
print("Score Value=",value)


#result_gender,value_gender=analyze_gender('My name is Rohan')
#print "Value of Result=",result_gender
#print "Value=",value_gender

        
