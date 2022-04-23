from setuptools import setup, find_packages

setup(
    name='the_button_orm',
    author='Ben Friedman',
    version='1.0.0',
    packages=find_packages(include=['orm'])
)

setup(
    name='the_button_web',
    author='Ben Friedman',
    version='1.0.0',
    packages=find_packages(include=['web'])
)
