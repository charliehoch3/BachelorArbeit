import os
import pandas as pd


CURRENT_DIR = os.path.dirname(__file__)

# Spalte 0 ist de, Spalte 1 ist en
TRANSLATOR = CURRENT_DIR + '/../data/cupid/paper/translation.csv'


def listreader():
    translatorlist = []
    csvcontent = pd.read_csv(TRANSLATOR)
    i = 0
    # csvcontent.size gibt alle Einträge zurück
    # Da es 2 Spalten gibt teile ich durch 2
    while i < csvcontent.size/2: 
        translatorlist.append((csvcontent.iat[i,0], csvcontent.iat[i,1]))
        i += 1

    return translatorlist


def reverse_translator(tupellist):
    translatorlist = listreader()
    sometemplist = tupellist.copy()
    for index, matching in enumerate(sometemplist):
        for translation in translatorlist:
            if (translation[1] == matching[0]):
                translatedMatching = (translation[0], matching[1])
                tupellist[index] = translatedMatching
                break
    
    return(tupellist)


# sims ist hier ein Dictonary, welches alle möglichen Matchings enthält
# Ein Dictonary ist immer im Muster key : value
# Mit list("namedesDictonarys".keys()) erhält man eine Liste aller keys, mit list("namedesDictonarys".values()) eine Liste aller Values
# Mit "namedesDictonarys".get(key) erhält man den Value zu einem bestimmten key
# Mit list("namedesDictonarys".items()) erhält man eine Liste der (key,value) Einträge als Tupel
# Die Einträge im sims Dictonary sind im Muster: ('category1__subcategory1', 'category2__subcategory2'): {'ssim': x, 'lsim': y, 'wsim': z}
# ('category1__subcategory1', 'category2__subcategory2') ist list("namedesDictionarys".items())[i][0] und ist ein Tupel
# {'ssim': x, 'lsim': y, 'wsim': z} ist list("namedesDictionarys".items())[i][1] und ist ein Dictionary
