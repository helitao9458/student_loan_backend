import pytest
from api.common_api import common_api
from api.user_api import user_api
from utils.assert_util import assert_util
from utils.log_util import logger
from data.data_reader import data_reader

# 加载YAML测试数据（全局加载一次）
TEST_DATA = data_reader.load_yaml_data()
# 拆分各接口数据
LOGIN_DATA = TEST_DATA["user_role"]["login"]
ADD_ROLE_DATA = TEST_DATA["user_role"]["add_role"]

# 构造参数化用例数据（格式：[(用例名, 入参, 预期结果), ...]）
login_case_data = [(k, v["json"], v["expected"]) for k, v in LOGIN_DATA.items()]
add_role_case_data = [(k, v["params"], v["expected"]) for k, v in ADD_ROLE_DATA.items()]

class TestUserRole:
    """用户/角色模块测试用例"""
    # 登录接口测试
    @pytest.mark.parametrize("case_name, login_json, expected", login_case_data)
    def test_login(self, case_name, login_json, expected):
        """登录接口-数据驱动：多组用例（正常/异常）"""
        logger.info(f"执行用例：{case_name}")
        response = common_api.login(login_json)
        # 通用断言（自动匹配expected中的字段）
        assert_util.assert_common(response, expected)

    # 登出接口测试
    def test_logout(self, admin_headers):
        response = common_api.logout(admin_headers)
        assert_util.assert_common(response ,expected={"code": 200})

    # 获取用户列表测试
    def test_get_all_user(self, admin_headers):
        params = {"page": 1, "realName": "路人甲", "idCard": 1}
        payload = {"userName": "hlt", "password": "20011228@Hh"}
        response = user_api.get_all_user(admin_headers, params, payload)
        assert_util.assert_common(response ,expected={"code": 200})

    # 添加角色测试（参数化）
    @pytest.mark.parametrize("case_name, params, expected", add_role_case_data)
    def test_add_role(self, case_name, params, expected, admin_headers):
        """添加角色接口-数据驱动：多组用例（正常/异常）"""
        logger.info(f"执行用例：{case_name}")
        response = user_api.add_role(admin_headers, params)
        assert_util.assert_common(response, expected)