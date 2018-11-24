import psycopg2

from flask import Flask
from fuzzywuzzy import fuzz

app = Flask(__name__)


SIMILARITY_RATIO = 50


@app.route("/")
def matching(corporation_id):
    try:
        conn = psycopg2.connect(
            "dbname='*' user='*' host='*' password='*'")
    except:
        print("I am unable to connect to the database")

    cur = conn.cursor()
    industry_SQL = "SELECT * FROM corporations WHERE corporation_id = %s"
    data = (corporation_id,)
    cur.execute(industry_SQL, data)
    industries = cur.fetchall()

    matched_results = set()

    for industry in industries:
        SQL = "SELECT * FROM startups WHERE %s IN industries"
        data = (industry,)
        cur.execute(SQL, data)
        startups = cur.fetchall()
        startups_ids = [startup.id for startup in startups]

        industry_description = industry["description"]

        for startup_id in startups_ids:
            startup_SQL = "SELECT * FROM startups WHERE id = %"
            data = (startup_id,)
            cur.execute(startup_SQL, data)
            startup = cur.fetchone()

            if fuzz.ratio(industry_description, startup["description"]) > SIMILARITY_RATIO:
                matched_results.add(startup["id"])

    return matched_results

