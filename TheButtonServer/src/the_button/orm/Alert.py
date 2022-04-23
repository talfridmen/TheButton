from sqlalchemy import Column, Integer, BLOB, Boolean, ForeignKey
from sqlalchemy.orm import relationship

from .Relations import UserRespondingToAlertRelation
from .base_orm import Base


class Alert(Base):
    __tablename__ = 'alert'

    user_id = Column(Integer, ForeignKey('user.id'))
    recording = Column(BLOB)
    active = Column(Boolean, default=True)

    user = relationship('User')
    responding = relationship('User', secondary=UserRespondingToAlertRelation.__tablename__)
