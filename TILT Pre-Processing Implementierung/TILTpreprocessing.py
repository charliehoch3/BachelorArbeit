import json, jsonpath_ng
import csv
import sys #für Aurufargumente
from libretranslatepy import LibreTranslateAPI #Translator

tiltDatei = sys.argv[1] #Sei die erste Variable nach Aufruf der Pfad zur gewünschten TILT Datei

libretranslate = LibreTranslateAPI("http://localhost:5000")

#extrahiere die Daten aus der TILT Datei
#json_data = übergebene TILT Datei
#jsonpath_expr = "$.dataDisclosed[*].category,subcategories" 
#extracted_data = category, [subcategories], ... oder category, category, ...
with open(tiltDatei, "r", encoding = "UTF8") as jsonfile:
    json_data = json.load(jsonfile)
jsonpath_expr = jsonpath_ng.parse("$.dataDisclosed[*].category,subcategories") 
extracted_data_raw = jsonpath_expr.find(json_data) #raw noch mit Bonusinfos zu extrahierten Daten
extracted_data = []
for x in extracted_data_raw:
    extracted_data.append(x.value)


#extracted_data in eine Tupelliste umwandeln
data_array = []
translation_array = []
i = 0
while i < (len(extracted_data)):
    cat = extracted_data[i]
    if i!=len(extracted_data)-1:
        if type(extracted_data[i+1]) is list:
            for subcat in extracted_data[i+1]:
                data_array.append((cat, subcat))
            i+=1
        else:
            data_array.append((cat, cat))
    else:
        data_array.append((cat, cat))
    i+=1
    
    
if(libretranslate.detect(data_array[0][0])[0].get("language") != "en"):
    index = 0
    for x in data_array:
        category_translated = libretranslate.translate(x[0], "de", "en")
        subcategory_translated = libretranslate.translate(x[1], "de", "en")
        translation_array.append(((x[0] + "__" + x[1]), (category_translated + "__" + subcategory_translated)))
        data_array[index] = (category_translated, subcategory_translated)
        index+=1
                
    #Erstelle eine csv.-Datei "translation.csv"
    with open('translation.csv', 'w', newline='', encoding = "UTF8") as csvfile:
        filewriter = csv.writer(csvfile, delimiter=',', 
                        quotechar='"', quoting=csv.QUOTE_ALL)
    #fülle "translation.csv" mit den Einträgen aus data_array
        filewriter.writerow(("de", "en"))
        for x in translation_array:
            filewriter.writerow(x)

    
#Erstelle eine csv.-Datei "dateschutzerklaerung.csv"
with open('datenschutzerkaerung.csv', 'w', newline='', encoding = "UTF8") as csvfile:
    filewriter = csv.writer(csvfile, delimiter=',', 
                            quotechar='"', quoting=csv.QUOTE_ALL)
    #fülle "dateschutzerklaerung.csv" mit den Einträgen aus data_array
    filewriter.writerow(("table", "column"))
    for x in data_array:
        filewriter.writerow(x)



