# api/common_api.py
from utils.request_util import request_util
from utils.log_util import logger

class CommonApi:
    """公共接口（登录/登出）"""
    @staticmethod
    def login(login_json: dict):
        """登录接口（适配数据驱动，入参为YAML读取的json）"""
        url = "user/login"
        response = request_util.post(url, json=login_json)
        return response

    @staticmethod
    def logout(headers):
        """登出接口"""
        url = "user/logout"
        response = request_util.post(url, headers=headers)
        return response

# 全局公共接口对象
common_api = CommonApi()