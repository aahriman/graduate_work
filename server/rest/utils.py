import sys
sys.path.append("../data");
sys.path.append("../database");

import json
import random

from model import Model

MODEL = Model("localhost","root","root","transcripter")

def get_model():
    return MODEL

def bytes_to_str(data):
    if(data):
        return data.decode('utf-8')
    else:
        return None

def get_practise(users):
    if(not isinstance(users, list)): users = [users] 

    data = MODEL.get_records_for_users([user["id"] for user in users], users[0]["selected_language"])
    if isinstance(data, list):
       data = random.choice(data)
    transcripts = []
    words = json.loads(data["sending_data"])
    for user in users:
        transcript_id = MODEL.save_transcript_send(data["id"], user["id"])
        transcripts.append ( {
          "id": transcript_id,
          "url": data["path_audio"],
          "words": words
        })
       
    return transcripts if len(transcripts) > 1 else transcripts[0];

def user_public_data(user):
    return({"nick_name": user["nick_name"], "position": user["position"], "score": user["score"]})

def get_user(data):
    if(data):
        data = json.loads(bytes_to_str(data))
        return MODEL.get_user(data["person-id"]) if("person-id" in data) else None
        
