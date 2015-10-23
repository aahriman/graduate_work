import os
import json
import traceback
import logging
from flask import Flask, Response, request, jsonify, stream_with_context


app = Flask(__name__)
app.config.update(
    SECRET_KEY = '12345',
    DEBUG = 'DEBUG' in os.environ,
    #SQLALCHEMY_DATABASE_URI = os.environ['CONNECTION_STRING']
)



@app.route("/statistics", methods=['GET'])
def statistics():
    
    data = {
        "offset": int(request.args.get("offset", "0")),
        "limit": int(request.args.get("limit", "1")),
    }

    
    def generate(response):
        for result in response:
            yield json.dumps(result)
            
    try:
        response = []
        for i in range(data["offset"], data["offset"]+data["limit"]):
            response.append({"name": "test"+str(i), "score": 100/(i+1), "position": i })
    
        print(response);
        return Response(stream_with_context(generate(response)))
    except:
        print("Unexpected error:")
        logging.error(traceback.format_exc())
        return jsonify({"status": "error", "message": "Some internal error. If you are developer, see log."}), 500
        
        
@app.route("/practise", methods=['GET'])
def practise():
    try:
        data = {
            "lang": request.args.get("lang", "cs"),
        }
            
    
        response = {
          "url": "atrey.karlin.mff.cuni.cz/~andokajn/test/pisnicky/do_stanice_ceskolipska.wav",
          "words": [{"origin": "do", "alternatives": ["do","z", "od"]},
                    {"origin": "stanice", "alternatives": ["stanice","zadnice", "plostice"]},
                    {"origin": "českolipská", "alternatives": ["častolipská","čístolipská", "českolipská"]}
                    ]
        }
            
        print(response);
        print(json.dumps(response));
        return Response(stream_with_context(json.dumps(response)))
    except:
        print("Unexpected error:")
        logging.error(traceback.format_exc())
        return jsonify({"status": "error", "message": "Some internal error. If you are developer, see log."}), 500    

if __name__ == "__main__":
    app.run(host="127.0.0.1", port=8000)