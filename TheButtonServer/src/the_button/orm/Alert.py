from sqlalchemy import Column, Integer, Boolean, ForeignKey, String, Float
from sqlalchemy.orm import relationship

from .Relations import UserRespondingToAlertRelation
from .base_orm import Base


class Alert(Base):
    __tablename__ = 'alert'

    alert_uuid = Column(String)
    user_id = Column(Integer, ForeignKey('user.id'))
    active = Column(Boolean, default=True)
    latitude = Column(Float)
    longitude = Column(Float)

    user = relationship('User')
    responding = relationship('User', secondary=UserRespondingToAlertRelation.__tablename__)
