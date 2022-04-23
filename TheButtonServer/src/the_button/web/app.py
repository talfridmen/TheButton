import json

from flask import Flask, request
from flask_cors import CORS

from the_button.orm.Alert import Alert
from the_button.orm.Relations import UserRespondingToAlertRelation
from the_button.orm.User import User
from the_button.orm.base_orm import Database

app = Flask(__name__)
cors = CORS(app)


@app.route('/api/register', methods=['POST'])
def register():
    data = json.loads(request.data)
    if 'name' not in data or 'phoneNumber' not in data:
        return 'Missing Info', 400
    user = User.get_or_create(name=data['name'], phone=data['phoneNumber'])
    Database.commit()
    return json.dumps({'userId': user.get_id()})


@app.route('/api/alert', methods=['POST'])
def new_alert():
    data = json.loads(request.data)
    print(data)
    return "", 200
    data = json.loads(request.data)
    if 'userId' not in data or 'recording' not in data:
        return 'Missing Info', 400
    alert = Alert(user_id=data['userId'], recording=data['recording'])
    Database.commit()
    return json.dumps({'alertId': alert.get_id()})


@app.route('/api/alert/<int:alert_id>/respond', methods=['POST'])
def respond(alert_id):
    if 'userId' not in data:
        return 'Missing Info', 400

    alert = Alert.get(id=alert_id).first()
    if alert is None:
        return 'Alert not found', 400
    user = User.get(id=data['userId']).first()
    if user is None:
        return 'User not found', 400

    response = UserRespondingToAlertRelation(user_id=data['userId'], alert_id=alert_id)
    return json.dumps({'userId': response.user_id, 'alertId': response.alert_id})


@app.route('/api/alert/<int:alert_id>/responding', methods=['GET'])
def get_responding(alert_id):
    alert = Alert.get(id=alert_id).first()
    if alert is None:
        return 'Alert not found', 400
    responding_users = alert.responding_users.all()
    responding_list = [responding_user.user.to_dict() for responding_user in responding_users]
    return json.dumps({'responding': responding_list})


@app.route('/api/alert/<int:alert_id>/cancel', methods=['POST'])
def cancel_alert(alert_id):
    alert = Alert.get(id=alert_id).first()
    if alert is None:
        return 'Alert not found', 400
    alert.active = False
    Database.commit()
    return json.dumps({'alertId': alert_id})


if __name__ == '__main__':
    app.run('0.0.0.0', port=5000)