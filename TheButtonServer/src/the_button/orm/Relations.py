from sqlalchemy import Integer, Column, ForeignKey
from sqlalchemy.orm import relationship

from .base_orm import Relation


class UserRespondingToAlertRelation(Relation):
    __tablename__ = 'user_responding_to_alert_relation'

    user_id = Column(Integer, ForeignKey('user.id'), primary_key=True)
    alert_id = Column(Integer, ForeignKey('alert.id'), primary_key=True)

    user = relationship('User')
    alert = relationship('Alert')
