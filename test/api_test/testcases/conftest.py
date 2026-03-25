import pytest
from api.common_api import common_api
from config.config import get_account
from utils.log_util import logger
from utils.assert_util import assert_util
from data.data_reader import data_reader

# 加载登录正常用例数据
TEST_DATA = data_reader.load_yaml_data()
ADMIN_LOGIN_JSON = TEST_DATA["user_role"]["login"]["admin_normal"]["json"]
STUDENT_LOGIN_JSON = TEST_DATA["user_role"]["login"]["student_normal"]["json"]
# 通用请求头构造
def get_common_headers(token):
    return {
        "sec-ch-ua-platform": "\"Windows\"",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36",
        "Accept": "application/json, text/plain, */*",
        "sec-ch-ua": "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"",
        "token": token,
        "sec-ch-ua-mobile": "?0",
        "Sec-Fetch-Site": "same-site",
        "Sec-Fetch-Mode": "cors",
        "Sec-Fetch-Dest": "empty",
        "host": "localhost"
    }

# 管理员token夹具（会话级，使用YAML正常登录数据）
@pytest.fixture(scope="session")
def admin_token():
    """获取管理员token（数据驱动适配）"""
    response = common_api.login(ADMIN_LOGIN_JSON)
    assert_util.assert_common(response, {"code":200, "has_token":True})
    token = response.json()["data"]["token"]
    logger.info("管理员token获取成功（数据驱动）")
    return token

# 学生token夹具（会话级，使用YAML正常登录数据）
@pytest.fixture(scope="session")
def student_token():
    """获取学生token（数据驱动适配）"""
    response = common_api.login(STUDENT_LOGIN_JSON)
    assert_util.assert_common(response, {"code":200, "has_token":True})
    token = response.json()["data"]["token"]
    logger.info("学生token获取成功（数据驱动）")
    return token

# 管理员/学生请求头夹具（保持原有）
@pytest.fixture(scope="function")
def admin_headers(admin_token):
    return get_common_headers(admin_token)

@pytest.fixture(scope="function")
def student_headers(student_token):
    return get_common_headers(student_token)