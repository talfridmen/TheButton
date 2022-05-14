from functools import wraps

from the_button.orm.base_orm import Database


def connection_required(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        if not Database.is_connected():
            Database.connect()
        return func(*args, **kwargs)

    return wrapper
