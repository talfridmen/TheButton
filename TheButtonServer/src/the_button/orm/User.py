from sqlalchemy import String, Column, Float

from .base_orm import Base


class User(Base):
    __tablename__ = 'user'

    name = Column(String)
    phone = Column(String)
    latitude = Column(Float, nullable=True)
    longitude = Column(Float, nullable=True)
