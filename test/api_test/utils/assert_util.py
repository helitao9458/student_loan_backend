# utils/assert_util.py
from utils.log_util import logger

class AssertUtil:
    """断言工具类（已修复 dict / response 混用问题）"""

    @staticmethod
    def assert_status_code(response, expected_code=200):
        """断言 HTTP 状态码"""
        actual_code = response.status_code
        assert actual_code == expected_code, f"状态码错误：预期{expected_code}，实际{actual_code}"
        logger.info(f"✅ 状态码断言成功：{actual_code} == {expected_code}")

    @staticmethod
    def assert_json(response):
        """安全获取 JSON，兼容 response 对象 / 字典"""
        if isinstance(response, dict):
            return response
        try:
            return response.json()
        except Exception:
            return {}

    @staticmethod
    def assert_common(response, expected: dict):
        """
        通用综合断言（100% 兼容数据驱动）
        :param response: requests 响应对象
        :param expected: 预期结果
        """
        # 1. 获取 JSON
        resp_json = AssertUtil.assert_json(response)

        # 2. 断言 HTTP 状态码
        if "code" in expected:
            expected_code = expected["code"]
            AssertUtil.assert_status_code(response, expected_code)

        # 3. 断言业务 code（如果接口返回格式是 {code:200, msg:...}）
        if "biz_code" in expected:
            biz_code = resp_json.get("code")
            assert biz_code == expected["biz_code"], f"业务码错误：预期{expected['biz_code']}，实际{biz_code}"
            logger.info(f"✅ 业务码断言成功：{biz_code} == {expected['biz_code']}")

        # 4. 断言 token 是否存在
        if "has_token" in expected:
            token = resp_json.get("data", {}).get("token")
            if expected["has_token"]:
                assert token, "token 不存在"
                logger.info("✅ token 存在断言成功")
            else:
                assert not token, "不应存在 token"
                logger.info("✅ token 不存在断言成功")

        # 5. 断言 data 是否存在
        if "has_data" in expected:
            data = resp_json.get("data")
            if expected["has_data"]:
                assert data is not None, "data 为空"
                logger.info("✅ data 存在断言成功")
            else:
                assert data is None, "data 不应存在"
                logger.info("✅ data 不存在断言成功")

assert_util = AssertUtil()