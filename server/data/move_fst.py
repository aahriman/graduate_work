import sys
import os
import string

BASE_DIR = "/root/graduate_work/server/data/";

if __name__ == "__main__":
    sys.path.append("../data");
    sys.path.append("../database");
    sys.path.append("../rest");

    from model import Model
    model = Model("localhost","root","root","transcripter");

    f = open("correct_transcripts.txt","r")

    for line in f:
        (path, trash, correct_transcript) = line.partition("=>")
        path = path.strip()
        directory = path[ : path.rfind("/")+1]
        file_name = path[path.rfind("/")+1 : -3] + "fst"
        os.rename(BASE_DIR + file_name, directory + file_name)
