from setuptools import setup, find_packages

setup(
    name="my_package",
    version="0.1.0",
    description="My test package",
    packages=find_packages(),
    install_requires=[
        "requests",  # 示例依赖
    ],
)
