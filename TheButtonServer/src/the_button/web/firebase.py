import json

import firebase_admin
from firebase_admin import messaging

SERVER_TOKEN = 'AAAAlF4gliQ:APA91bEJ4mysycPNPIZwHgWqdKehUx60omBdd2JQfR69zZk9UNfSQpQ-D-OLFV5-' \
               'HTgC05OpSThXI0e4gkCxYyCE3YC1Gu5VnA2tQEdAFQ-mGAIikSuLKl5TK3x6eL9d-1dPUzKFEYgZ'

NOTIFICATION_TITLE = 'Button Alert'
NOTIFICATION_BODY_PATTERN = '{user.name} needs your help!'


def send_notification(device_tokens, title, body, data):
    try:
        app = firebase_admin.get_app(name="the_button")
    except ValueError:
        app = firebase_admin.initialize_app(
            credential=firebase_admin.credentials.Certificate('./credentials.json'),
            name="the_button"
        )

    message = messaging.MulticastMessage(
        notification=messaging.Notification(
            title=title,
            body=body
        ),
        data=data,
        tokens=device_tokens
    )

    response = messaging.send_multicast(message, app=app)
    for res in response.responses:
        print(res.__dict__)


if __name__ == '__main__':
    send_notification(
        [
            "dN1WJiASQQ6yaIEYGRbH8P:APA91bFw296O6_3B4A9A2x0aKPxqTedDRjPXCzfuZjkUlUvADjsPurXJUSr1O9Jb3qF8k4USBXxoR3vAYbNCZKsF9Rr1XUWOVfGI8ca4COAcKo_aid0dtKtmTe_BJ3uLrBpcmhkZYA8u"],
        "title",
        "body",
        {'alertUUID': 'c5a9f655-649f-4fab-90a5-8aff9adffa2e'}
    )
