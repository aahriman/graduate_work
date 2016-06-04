import pymysql
import parse_fst
import string
import json

class Model:
    host = None
    user = None
    passwd = None
    db = None

    rowcount = None
    def __init__(self, host, user, passwd, db):
        self.host = host
        self.user = user
        self.passwd = passwd
        self.db = db

    def test(self):
        return self.query("SHOW DATABASES")[1];

    def get_users_by_score(self, limit, offset):
        return self.query("SELECT * FROM users ORDER BY score DESC, nick_name LIMIT {:d} OFFSET {:d}".format(limit,offset))[1]

    def get_user(self, person_id):
        (rowcount, user) = self.query("SELECT * FROM users WHERE `google_id`={!r}".format(person_id))
        user["position"] = self.query("SELECT COUNT(*) as c FROM users WHERE score > {0:d} OR (score = {0:d} AND nick_name < {1!r})".format(user["score"], user["nick_name"]))[1]["c"] + 1
        return user

    def save_user(self, update, u_email, u_nickname, u_google_id, u_lang):
        if(update):
            query_string = "UPDATE users SET `nick_name` = {!r}, `selected_language` = {!r} WHERE `google_id` = {!r} ".format(u_nickname, u_lang, u_google_id);
        else:
            query_string = "INSERT IGNORE users SET `nick_name` = {!r}, `email` = {!r}, `google_id` = {!r} ".format( u_nickname, u_email, u_google_id);
        return self.query(query_string)[0] == 1



    def save_record(self, path_audio, path_fst, language, transcript):
        sending_data = self._sending_data(path_fst, language)
        if(sending_data):
            self.query("INSERT INTO records SET `path_audio`={!r}, `path_fst`={!r}, `language`={!r}, sending_data={!r}, transcript={!r}".format(path_audio, path_fst, language, sending_data, transcript));

    def get_records_for_users(self, users_id, lang):
        query_start = "SELECT * FROM records rec WHERE language={!r} ".format(lang);
        query = query_start
        for user_id in users_id:
            query +="AND  rec.id NOT IN (SELECT record_id FROM responses WHERE user_id = {:d})".format(user_id)
        (rowcount, data) = self.query(query);
        if(data):
            return data
        else:
            query = query_start
            for user_id in users_id:
                query +="AND  rec.id NOT IN (SELECT record_id FROM responses WHERE user_id = {:d} AND user_response <>'')".format(user_id)
            data = self.query(query)[1]
        if(data):
            return data
        else:
            return self.query("SELECT * FROM records WHERE laguage={!r}".formar(lang))[1]


    def save_transcript(self, t_id, t_changes, correct):
      if(correct):
        query_string = "UPDATE responses SET `user_response` = {!r}, `answer` = CURRENT_TIMESTAMP, correct = 1 WHERE `id` = {:d} AND `user_response` =''".format(t_changes, t_id)
        # AND `user_response` = '' is only safely lock for dont rewriting
        (rowcount, trash) = self.query(query_string);
        if(rowcount):
            query_string =("SELECT @uid:=`user_id`, @rid:=`record_id`,@answer:=`answer` FROM `responses` WHERE `id` = {:d};"
                       "SELECT @count:=COUNT(*) FROM `responses` WHERE `user_id` = @uid AND `record_id` = @rid AND `answer` <= @answer AND `user_response` <> '';"
                       "UPDATE `users` SET score = score + 50 + 50/@count WHERE id =@uid;").format(t_id);
            self.query(query_string);
      else:
        query_string = "INSERT INTO responses (`user_response`,`user_id`, `record_id`)  SELECT {!r},`user_id`,`record_id` FROM `responses` WHERE `id` = {:d}".format(t_changes, t_id)
        self.query(query_string);
      return None;

    def save_transcript_send(self, r_id, u_id):
        query_string = "INSERT responses SET `record_id` = {:d}, user_id = {:d}".format(r_id, u_id)
        return self.query(query_string)[1];

    def is_correct(self, id, u_transcript):
        query_string = "SELECT `records`.`transcript`, `records`.`sending_data` FROM responses JOIN `records` ON `records`.`id` = `responses`.`record_id` WHERE `responses`.`id` = {:d}".format(id);
        (rowcount, data) = self.query(query_string);
        transcript = data["transcript"];
        sending_data = json.loads(data["sending_data"])
        user_transcript = [];
        for i in sending_data:
            user_transcript.append(i["origin"])

        for i in u_transcript:
            user_transcript[i["word-id"]] = i["correct-to"]
        user_transcript_str = " ".join(user_transcript).strip()
        return user_transcript_str == transcript
        

    def query(self,sql):
        __connection = pymysql.connect(host=self.host, user=self.user, passwd=self.passwd, db=self.db,charset='utf8',use_unicode=True)
        cursor = __connection.cursor();
        cursor.execute(sql)
        result = None
        if cursor.description:
             fields = map(lambda x:x[0], cursor.description)
             columns = [field for field in fields]
             result = [dict(zip(columns,row))  for row in cursor.fetchall()]
        rowcount = cursor.rowcount
        cursor.close()
        __connection.close()
        if (result == None):
            return (rowcount, cursor.lastrowid)
        elif (len(result) == 1):
            return (rowcount, result[0])
        else:
            return (rowcount, result)
        return (rowcount, result)

    def _sending_data(self, fst_path, language):
         posibilities = parse_fst.posibilities(fst_path, parse_fst.wst2dict("../data/{!s}/words.txt".format(language)));
         words = []
         for i in posibilities:
             i = list(i)
             words.append({"origin": i[0], "alternatives" : i})
         if(len(words) > 0):
             return json.dumps(words)
         else:
             return None


