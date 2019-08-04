from flask import Flask ,redirect,url_for,request,render_template
from flask_assets import Bundle, Environment
from flask_fontawesome import FontAwesome
from flask_pymongo import PyMongo



app = Flask(__name__)
app.config["MONGO_URI"] = "mongodb://localhost:27017/bouncecab"
mongo = PyMongo(app)
fa = FontAwesome(app)
js = Bundle('jquery.min.js', 'perfect-scrollbar.jquery.min.js', 'chartjs.min.js', 'bootstrap.min.js',
            'bootstrap-notify.js',  'demo.js', 'now-ui-dashboard.min.js',
            'now-ui-dashboard.js',
            'popper.min.js', output='gen/main.js')

assets = Environment(app)
assets.register('main_js', js)

@app.route('/')
def dashboard():
    return render_template('dashboard.html')
@app.route('/index')
def index():
    cab_list = mongo.db.cabs_fd.find().limit(10)
    return render_template('index.html', cab_list=cab_list)

if __name__ =='__main__':
    app.run(debug=True)