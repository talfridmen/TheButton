from sqlalchemy import Column, Integer, BLOB, Boolean, ForeignKey, String
from sqlalchemy.orm import relationship

from .Relations import UserRespondingToAlertRelation
from .base_orm import Base


class Alert(Base):
    __tablename__ = 'alert'

    alert_uuid = Column(String)
    user_id = Column(Integer, ForeignKey('user.id'))
    recording = Column(String)
    active = Column(Boolean, default=True)

    user = relationship('User')
    responding = relationship('User', secondary=UserRespondingToAlertRelation.__tablename__)
