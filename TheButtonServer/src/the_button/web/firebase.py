import firebase_admin
from firebase_admin import messaging

SERVER_TOKEN = 'AAAAlF4gliQ:APA91bEJ4mysycPNPIZwHgWqdKehUx60omBdd2JQfR69zZk9UNfSQpQ-D-OLFV5-' \
               'HTgC05OpSThXI0e4gkCxYyCE3YC1Gu5VnA2tQEdAFQ-mGAIikSuLKl5TK3x6eL9d-1dPUzKFEYgZ'

NOTIFICATION_TITLE = 'Button Alert'
NOTIFICATION_BODY_PATTERN = '{user.name} needs your help!'


def send_notification(device_tokens, title, body, data):
    firebase_admin.initialize_app(firebase_admin.credentials.Certificate('./credentials.json'))
    message = messaging.MulticastMessage(
        notification=messaging.Notification(
            title=title,
            body=body
        ),
        data=data,
        tokens=device_tokens
    )

    response = messaging.send_multicast(message)
    print(response.responses)


if __name__ == '__main__':
    send_notification(
        ["f-uqHRMlT_W8eDreM8-8m9:APA91bGYgBqQ25APBNkv5p6oueGFCa0UGXW8Oxo5GrjgFb2WlFi_u2w6f4v0h13-UaNqINl71yzUDszppcnWWeesp7ltw5llPf2qa3Ub0tVfSdetBK7S5Xt4zUZ6dfVA24HXJdBXsEFG"],
        "title",
        "body",
        {}
    )
