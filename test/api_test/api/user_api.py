from utils.request_util import request_util

class UserApi:
    """用户/角色模块接口"""
    @staticmethod
    def get_all_user(headers, params, payload):
        """获取用户列表"""
        url = "user/getalluser"
        return request_util.post(url, headers=headers, params=params, json=payload)

    @staticmethod
    def add_user(headers, payload):
        """添加用户"""
        url = "user/adduser"
        return request_util.post(url, headers=headers, json=payload)

    @staticmethod
    def add_role(headers, params):
        """添加角色"""
        url = "role/addrole"
        return request_util.post(url, headers=headers, params=params)

    @staticmethod
    def change_role(headers, params):
        """分配角色"""
        url = "user/changeRole"
        return request_util.post(url, headers=headers, params=params)

    @staticmethod
    def select_role_by_id(headers, params):
        """根据用户ID查询角色"""
        url = "role/selectByid"
        return request_util.post(url, headers=headers, params=params)

# 全局用户接口对象
user_api = UserApi()