import sys
import random
import os
import json
import traceback
import logging
from flask import Flask, Response, request, jsonify, stream_with_context

sys.path.append("../data");
sys.path.append("../database");


from utils import *
import parse_fst

logging.basicConfig(filename="log_rest_api.log",
                    level=logging.NOTSET,
                    )
words = parse_fst.wst2dict("../data/cs/words.txt")

app = Flask(__name__)
app.config.update(
    SECRET_KEY = '12345',
    DEBUG = 'DEBUG' in os.environ,
    #SQLALCHEMY_DATABASE_URI = os.environ['CONNECTION_STRING']
)



@app.route("/statistics", methods=['POST'])
def statistics():
    
    data = {
       "offset": int(request.args.get("offset", "0")),
       "limit": int(request.args.get("limit", "1")),
    }
    user = get_user(request.data)

    try:
        rows = []
    
        rows_data = model.get_users_by_score(data["limit"], data["offset"])
        if(not isinstance(rows_data, list)):
            rows_data = [rows_data]
        for i in range(len(rows_data)):
           rows.append({"name": rows_data[i]["nick_name"], "score": rows_data[i]["score"], "position": 1+i+data["offset"]})
        response = {"position": user["position"], "score": user["score"], "rows":rows, "max": len(rows)}
        print(response)
        return Response(stream_with_context(json.dumps(response)))
    except:
        print("Unexpected error:")
        logging.error(request.data)
        logging.error(traceback.format_exc())
        return jsonify({"status": "error", "message": "Some internal error. If you are developer, see log."}), 500
        
        
@app.route("/practise", methods=['POST'])
def practise():
    try:
        user = get_user(request.data)
        response = get_practise(user)
            
        return Response(stream_with_context(json.dumps(response)))
    except:
        print("Unexpected error:")
        logging.error(request.data)
        logging.error(traceback.format_exc())
        return jsonify({"status": "error", "message": "Some internal error. If you are developer, see log."}), 500    

@app.route("/transcript", methods=['POST'])
def transcript():
    try:
        data = json.loads(bytes_to_str(request.data));
        transcripts = json.dumps(data["transcript-corrections"]);
        correct = model.is_correct(int(data["id"]), data["transcript-corrections"]);
        model.save_transcript(int(data["id"]), transcripts, correct);
        dialog = {"cs": { "title": ("Gratulujeme!" if correct else "Zkus to znovu"),
                                           "message": ("Odpověděl jste správně. Přejděte k další nahrávce a získejte tak ještě více bodů." if correct  else "Bohužel vaše odpověď byla chybná. Zkuste se opravit.")},
                                   "en": { "title": ("Congratulation!" if correct else "Try it again"),
                                           "message": ("Your answer is right. Go to next record and get more score points." if correct else "Unfortunatelly your answer is wrong. Try it again.")},
                                   "background": "#106A10" if correct else "#FF4000"
                        }
        return jsonify({"status": "ok",
			"message": "Data was saved.",
                        "dialog": dialog}), 200    
    except:
        print("Unexpected error:")
        logging.error(request.data)
        logging.error(traceback.format_exc())
        return jsonify({"status": "error", "message": "Some internal error. If you are developer, see log."}), 500   

@app.route("/registration", methods=['POST'])
def registration():
    try:
        if (request.data):
             data = json.loads(bytes_to_str(request.data));
             model.save_user(False, data["email"], data["nickname"], data["google-id"], "cs");
             return jsonify({"status": "ok", "message": "User was saved."}), 200
        else:
             return jsonify({"status": "wrong", "message": "Data not sended."}), 200
             
    except:
        print("Unexpected error:")
        logging.error(request.data)
        logging.error(traceback.format_exc())
        return jsonify({"status": "error", "message": "Some internal error. If you are developer, see log."}), 500    

@app.route("/setting", methods=['POST'])
def setting():
    try:
        user = get_user(request.data)
        print(user)
        response = { "languages" : [ "cs" ], "language": user["selected_language"], "nickname": user["nick_name"] }
        print("resnpose: \t", response)
        return Response(stream_with_context(json.dumps(response)))
    except:
        print("Unexpected error:")
        logging.error(request.data)
        logging.error(traceback.format_exc())
        return jsonify({"status": "error", "message": "Some internal error. If you are developer, see log."}), 500    


@app.route("/setting-set", methods=['POST'])
def setting_set():
    try:
        data = json.loads(bytes_to_str(request.data));
        model.save_user(True, None, data["nickname"], data["person-id"], data["language"]) 
        return setting()
    except:
        print("Unexpected error:")
        logging.error(request.data)
        logging.error(traceback.format_exc())
        return jsonify({"status": "error", "message": "Some internal error. If you are developer, see log."}), 500    



if __name__ == "__main__":
    model = get_model()
    app.run(host="0.0.0.0", port=8000)
