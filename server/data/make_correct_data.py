import sys
import string
import os.path
import os
import re

BASE_DIR = "/root/graduate_work/server/data/";

if __name__ == "__main__":
    sys.path.append("../data");
    sys.path.append("../database");
    sys.path.append("../rest");

    from model import Model
    model = Model("localhost","root","root","transcripter");

    f = open("correct_transcripts.txt","r")

    for line in f:
        (path, trash, correct_transcript) = line.partition("=>");

        correct_transcript = re.sub("_.*_","", correct_transcript).strip()

        path = path.strip()

        directory = path[ : path.rfind("/")+1]
        file_name = path[path.rfind("/")+1 : -3] + "fst"

        path_fst = path[:-3] + "fst";

        if(os.path.exists(BASE_DIR + file_name) and os.path.exists(directory)):
            os.rename(BASE_DIR + file_name, directory + file_name)

        if(os.path.exists(path) and os.path.exists(path_fst) and len(correct_transcript) > 0):
             language = path[len(BASE_DIR) : path.find("/", len(BASE_DIR))]
             path_audio =  "http://147.251.253.7/data/" + path[len(BASE_DIR):];
             model.save_record(path_audio, path_fst, language, correct_transcript)
