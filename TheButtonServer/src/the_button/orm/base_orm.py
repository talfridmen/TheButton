from functools import wraps

import sqlalchemy.orm
from sqlalchemy import create_engine, Column, Integer
from sqlalchemy.orm import sessionmaker, declarative_base
from typing import Callable


DeclarativeBase = declarative_base()


class Database:
    _engine = create_engine('sqlite:///:memory:', echo=True)
    _sessionmaker = sessionmaker(bind=_engine)
    session = None

    @classmethod
    def connect(cls):
        cls.session = cls._sessionmaker()
        DeclarativeBase.metadata.create_all(cls._engine)

    @classmethod
    def disconnect(cls):
        if cls.session:
            cls.session.close()

    @classmethod
    def commit(cls):
        if cls.session:
            cls.session.commit()

    @classmethod
    def rollback(cls):
        if cls.session:
            cls.session.rollback()


class Base(DeclarativeBase):
    __abstract__ = True
    _id = Column(Integer, name='id', primary_key=True)

    def __init__(self, **kwargs):
        legal_keys = list(filter(lambda k: not k.startswith('_'), self.__class__.__dict__.keys()))
        for key, value in kwargs.items():
            if key not in legal_keys:
                raise KeyError(f'Table {self.__tablename__} does not have field {key}')
            setattr(self, key, value)
        Database.session.add(self)

    def get_id(self):
        return self._id

    @classmethod
    def get_by_id(cls, id_):
        return Query(cls=cls).filter(cls._id == id_).first()

    @classmethod
    def get(cls, *criteria, **kwargs):
        return Query(cls=cls).filter(*criteria, **kwargs)

    @classmethod
    def get_or_create(cls, **kwargs):
        q = Query(cls=cls).filter(**kwargs)
        if q.count():
            if q.count() > 1:
                raise ValueError('More than one possible match. Please fix the criteria')
            return q.first()
        return cls(**kwargs)


class Relation(DeclarativeBase):
    __abstract__ = True

    def __init__(self, **kwargs):
        legal_keys = list(filter(lambda k: not k.startswith('_'), self.__class__.__dict__.keys()))
        for key, value in kwargs.items():
            if key not in legal_keys:
                raise KeyError(f'Table {self.__tablename__} does not have field {key}')
            setattr(self, key, value)
        Database.session.add(self)

    @classmethod
    def get(cls, *criteria, **kwargs):
        return Query(cls=cls).filter(*criteria, **kwargs)

    @classmethod
    def get_or_create(cls, **kwargs):
        q = Query(cls=cls).filter(**kwargs)
        if q.count():
            if q.count() > 1:
                raise ValueError('More than one possible match. Please fix the criteria')
            return q.first()
        return cls(**kwargs)


class Query:
    def update_query(func: Callable):
        @wraps(func)
        def wrapper(self, *args, **kwargs):
            self._query = func(self, *args, **kwargs)
            return self
        return wrapper

    def __init__(self, cls):
        self._query: sqlalchemy.orm.Query = Database.session.query(cls)

    @update_query
    def filter(self, *criteria, **kwargs):
        return self._query.filter(*criteria).filter_by(**kwargs)

    @update_query
    def join(self, cls):
        return self._query.join(cls)

    def all(self):
        return self._query.all()

    def first(self):
        return self._query.first()

    def get_many(self, num):
        return self._query.limit(num).all()

    def count(self):
        return self._query.count()

    update_query = staticmethod(update_query)
