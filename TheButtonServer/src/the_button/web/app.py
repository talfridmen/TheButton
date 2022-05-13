import json

from flask import Flask, request
from flask_cors import CORS

from the_button.orm.Alert import Alert
from the_button.orm.Relations import UserRespondingToAlertRelation
from the_button.orm.User import User
from the_button.orm.base_orm import Database
from the_button.web.firebase import send_notification
from the_button.web.utils import connection_required

app = Flask(__name__)
cors = CORS(app)


@app.route('/api/register', methods=['POST'])
@connection_required
def register():
    data = json.loads(request.data)
    if 'name' not in data or 'phone' not in data:
        return 'Missing Info', 400
    user = User.get_or_create(name=data['name'], phone=data['phone'], token=data['token'])
    Database.commit()
    return json.dumps({'userId': user.get_id()})


@app.route('/api/location/update', methods=['POST'])
@connection_required
def update_location():
    data = json.loads(request.data)
    if 'userId' not in data or 'longitude' not in data or 'latitude' not in data:
        return 'Missing Info', 400
    user = User.get_by_id(data['userId'])
    if not user:
        return 'User not found', 400
    user.longitude = data['longitude']
    user.latitude = data['latitude']
    Database.commit()
    return json.dumps({"userId": user.get_id()})


@app.route('/api/token/update', methods=['POST'])
@connection_required
def update_token():
    data = json.loads(request.data)
    if 'token' not in data or 'userId' not in data:
        return 'Missing Info', 400
    user = User.get_by_id(data['userId'])
    if not user:
        user = User()
    user.token = data['token']
    Database.commit()
    return json.dumps({'userId': user.get_id()})


def send_noticiations_by_location(alert_uuid, user, latitude, longitude):
    send_notification(
        [u.token for u in User.get(
            User.latitude > latitude - 1,
            User.latitude < latitude + 1,
            User.longitude > longitude - 1,
            User.longitude < longitude + 1,
        ).all() if user.get_id() != u.get_id() and u.token],
        'Alert!',
        f'{user.name} needs your assistance!',
        {'alertUUID': alert_uuid}
    )


@app.route('/api/alert', methods=['POST'])
@connection_required
def new_alert():
    data = json.loads(request.data)
    if any(key not in data for key in ['userId', 'latitude', 'longitude', 'alertUUID']):
        return 'Missing Info', 400
    alert = Alert(
        alert_uuid=data['alertUUID'],
        user_id=data['userId'],
        latitude=data['latitude'],
        longitude=data['longitude']
    )
    Database.commit()
    send_noticiations_by_location(
        data['alertUUID'],
        User.get_by_id(data['userId']),
        data['latitude'],
        data['longitude']
    )
    return json.dumps({'alertId': alert.get_id()})


@app.route('/api/alert/<alert_id>')
def get_alert_data(alert_id):
    alert = Alert.get(alert_uuid=alert_id).first()
    if alert is None:
        return 'Alert not found', 400

    return json.dumps({'latitude': alert.latitude, 'longitude': alert.longitude, 'alertActive': alert.active})


@app.route('/api/alert/<alert_uuid>/respond', methods=['POST'])
@connection_required
def respond(alert_uuid):
    data = json.loads(request.data)
    if 'userId' not in data:
        return 'Missing Info', 400

    alert = Alert.get(alert_uuid=alert_uuid).first()
    if alert is None:
        return 'Alert not found', 400
    user = User.get(_id=data['userId']).first()
    if user is None:
        return 'User not found', 400

    response = UserRespondingToAlertRelation(user_id=user.get_id(), alert_id=alert.get_id())
    return json.dumps({'userId': response.user_id, 'alertId': response.alert_id})


@app.route('/api/alert/<alert_id>/responding', methods=['GET'])
@connection_required
def get_responding(alert_id):
    alert = Alert.get(alert_uuid=alert_id).first()
    if alert is None:
        return 'Alert not found', 400
    responding_list = [responding_user.to_dict() for responding_user in alert.responding]
    return json.dumps({'responding': responding_list})


@app.route("/api/alert/<alert_uuid>/phone")
def get_phone(alert_uuid):
    alert = Alert.get(alert_uuid=alert_uuid).first()
    if alert is None:
        return "Alert not found", 400
    return json.dumps({"phone": alert.user.phone})


@app.route('/api/alert/<alert_uuid>/cancel', methods=['POST'])
@connection_required
def cancel_alert(alert_uuid):
    alert = Alert.get(alert_uuid=alert_uuid).first()
    if alert is None:
        return 'Alert not found', 400
    alert.active = False
    Database.commit()
    return json.dumps({'alertUUID': alert_uuid})


if __name__ == '__main__':
    app.run('0.0.0.0', port=5000)
