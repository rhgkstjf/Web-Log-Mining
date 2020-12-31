from flask import Flask, render_template
from functools import lru_cache
import json

app = Flask(__name__)

#@app.route('/')
#def mainpage():
    #return render_template('mainpage.html')

def load_json_list_file(file_path):
    for line in open(file_path, 'r', encoding="utf-8"):
        yield json.loads(line)

hack = list(load_json_list_file('/var/www/FLASKAPPS/Hack.json'))
content = list(load_json_list_file('/var/www/FLASKAPPS/Content.json'))
country = list(load_json_list_file('/var/www/FLASKAPPS/Country.json'))
classfication = list(load_json_list_file('/var/www/FLASKAPPS/Classfication.json'))

@app.route('/')
def test():
    return render_template('chart.html',result=[hack, content, country, classfication])
