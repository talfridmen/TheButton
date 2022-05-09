from sqlalchemy import String, Column, Float

from .base_orm import Base


class User(Base):
    __tablename__ = 'user'

    name = Column(String)
    phone = Column(String)
    latitude = Column(Float, nullable=True)
    longitude = Column(Float, nullable=True)

    def to_dict(self):
        return {
            "id": self.get_id(),
            "name": self.name,
            "longitude": self.longitude,
            "latitude": self.latitude,
        }
