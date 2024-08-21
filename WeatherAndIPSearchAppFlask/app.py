from flask import Flask, render_template, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime
import json
import requests
from fpdf import FPDF
from werkzeug.middleware.proxy_fix import ProxyFix


app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"]='sqlite:///mydb.db'

db=SQLAlchemy(app)


API_KEY='f81b655d1cmsh026bb3bc59e50f7p1e3b86jsn7674e709d49f'
API_URL='https://weatherapi-com.p.rapidapi.com/current.json?q=53.1%2C-0.13'
API_HOST='weatherapi-com.p.rapidapi.com'
API_KEY_FOR_LOCATIONS='AIzaSyDTvtR7CjYURclwGfH-6GWG7kmCC1nziCk'



API_URL_FOR_MOVIES="https://streaming-availability.p.rapidapi.com/shows/%7Btype%7D/%7Bid%7D"


class Users(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    username=db.Column(db.String(20), nullable=False)
    password=db.Column(db.String(20),nullable=False)
    date=db.Column(db.DateTime, default=datetime.now)
    
    
    
    def __repr__(self):
        return '<Username %r>' % self.username  % self.id
    
class Movies(db.Model):
     
    id = db.Column(db.Integer, primary_key=True)
    itemType = db.Column(db.String(50), nullable=False)
    showType = db.Column(db.String(50), nullable=False)
    title = db.Column(db.String(255), nullable=False)
    overview = db.Column(db.Text, nullable=True)
    releaseYear = db.Column(db.Integer, nullable=False)
    genres = db.Column(db.String(255), nullable=True)
    directors = db.Column(db.String(255), nullable=True)
    cast = db.Column(db.Text, nullable=True)
    imageSet = db.Column(db.String(255), nullable=True)
    
    def __repr__(self):
        return f'<Movie {self.title}>'

app.wsgi_app=ProxyFix(
    app.wsgi_app, x_for=1, x_port=1, x_host=1, x_prefix=1
)

@app.route('/')  
def index():
    return render_template('home.html')

@app.route("/ip_checker")
def check_ip():
    background_img_url='weatherApp\static\icons\ip-search-background.jpg'
    return render_template('ip_checker.html')



#START OF MOVIES FETCHING

API_KEY_FOR_MOVIES = 'f81b655d1cmsh026bb3bc59e50f7p1e3b86jsn7674e709d49f'
API_HOST_FOR_MOVIES = 'moviedatabase8.p.rapidapi.com'
API_URL_FOR_MOVIES='https://moviedatabase8.p.rapidapi.com/Search/Incep'

def fetch_movies():
    headers = {
        "x-rapidapi-key": API_KEY_FOR_MOVIES,
        "x-rapidapi-host": API_HOST_FOR_MOVIES
    }

    try:
        response = requests.get(API_URL_FOR_MOVIES, headers=headers)
        response.raise_for_status()
        data = response.json()
        return data
    
    except requests.exceptions.RequestException as e:
        print(f"Hiba történt az API hívás során: {e}")
        return []

def save_movies_to_db(movies):
    with app.app_context():
        for movie_data in movies:
            movie = Movies(
                itemType=movie_data.get('itemType'),
                showType=movie_data.get('showType'),
                id=movie_data.get('id'),
                title=movie_data.get('title'),
                overview=movie_data.get('overview'),
                releaseYear=movie_data.get('releaseYear'),
                genres=', '.join(movie_data.get('genres', [])),  # list to string
                directors=', '.join(movie_data.get('directors', [])),  # list to string
                cast=', '.join(movie_data.get('cast', [])),  # list to string
                imageSet=movie_data.get('imageSet')
            )
            db.session.add(movie)
        db.session.commit()
        
        # Ellenőrizzük, hogy elmentettük az adatokat
        saved_movies = Movies.query.all()
        print("Saved Movies:", saved_movies)
@app.route('/fetch_and_list_movies')
def fetch_and_list_movies():
    movies = fetch_movies()
    return render_template('movies.html', movies=movies)


@app.route("/movies", methods=['GET'])
def movies_rout():
    movies = Movies.query.limit(5).all()  # Csak az első 5 filmet kérjük le
    return render_template("movies.html", movies=movies)       

@app.route("/get_ip", methods=['GET'])
def get_ip():
    response=requests.get('https://api.ipify.org?format=json')
    data=response.json()
    return jsonify(data)

#END OF MOVIES FETCHING


@app.route('/location_suggestion', methods=['GET'])
def location_suggestion():
    query=request.args.get('query', '')
    if not query:
        return jsonify([])
    url = f'https://maps.googleapis.com/maps/api/place/autocomplete/json?input={query}&key={API_KEY_FOR_LOCATIONS}'
    response=requests.get(url)
    data=response.json()

    suggestions = [result['description'] for result in data.get('predictions', [])]  
    return jsonify(suggestions)
  
@app.route('/predict_weather', methods=['POST'])
def predict_weather():
    location = request.form['location']
    querystring = {"q": location}
    headers = {
        "X-RapidAPI-Key": API_KEY,
        "X-RapidAPI-Host": API_HOST
    }
    url = f"https://weatherapi-com.p.rapidapi.com/current.json"

    try:
        response = requests.get(url, headers=headers, params=querystring)
        json_data = json.loads(response.text)

        name = json_data['location']['name']
        region = json_data['location']['region']
        country = json_data['location']['country']
        lat = json_data['location']['lat']
        lon = json_data['location']['lon']
        tz_id = json_data['location']['tz_id']
        localtime_epoch = json_data['location']['localtime_epoch']
        localtime = json_data['location']['localtime']
        last_updated_epoch = json_data['current']['last_updated_epoch']
        last_updated = json_data['current']['last_updated']
        temp_c = json_data['current']['temp_c']
        temp_f = json_data['current']['temp_f']
        is_day = json_data['current']['is_day']
        condition_text = json_data['current']['condition']['text']
        condition_icon = json_data['current']['condition']['icon']
        wind_mph = json_data['current']['wind_mph']
        wind_kph = json_data['current']['wind_kph']
        wind_degree = json_data['current']['wind_degree']
        wind_dir = json_data['current']['wind_dir']
        pressure_mb = json_data['current']['pressure_mb']
        pressure_in = json_data['current']['pressure_in']
        precip_mm = json_data['current']['precip_mm']
        precip_in = json_data['current']['precip_in']
        humidity = json_data['current']['humidity']
        cloud = json_data['current']['cloud']
        feelslike_c = json_data['current']['feelslike_c']
        feelslike_f = json_data['current']['feelslike_f']
        vis_km = json_data['current']['vis_km']
        vis_miles = json_data['current']['vis_miles']
        uv = json_data['current']['uv']
        gust_mph = json_data['current']['gust_mph']
        gust_kph = json_data['current']['gust_kph']
        
        
        background_class=''
        if condition_text.lower()=='sunny':
            background_class='sunny'
            print("sunny")
        elif 'rain' in condition_text.lower():
            background_class='rainy'
            print("rain")
        elif 'cloud' in condition_text.lower():
            print("cloud")
            background_class='cloudy'

        return render_template('home.html', name=name, region=region, country=country, lat=lat, lon=lon, tz_id=tz_id,
                               localtime_epoch=localtime_epoch, localtime=localtime, last_updated_epoch=last_updated_epoch, last_updated=last_updated,
                               temp_c=temp_c, temp_f=temp_f, is_day=is_day, condition_text=condition_text, condition_icon=condition_icon, wind_mph=wind_mph,
                               wind_kph=wind_kph, wind_degree=wind_degree, wind_dir=wind_dir, pressure_mb=pressure_mb, pressure_in=pressure_in, precip_mm=precip_mm,
                               precip_in=precip_in, humidity=humidity, cloud=cloud, feelslike_c=feelslike_c, feelslike_f=feelslike_f, vis_km=vis_km,
                               vis_miles=vis_miles, uv=uv, gust_mph=gust_mph, gust_kph=gust_kph, background_class=background_class)

    except Exception as e:
        print(e)
        return render_template('home.html', error='Please enter a correct Place name...')



if __name__ == '__main__':
    with app.app_context():
        db.create_all()
   
    app.run(debug=True)