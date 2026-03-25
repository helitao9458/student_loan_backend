import pytest
from api.loan_api import loan_api
from utils.assert_util import assert_util
from utils.log_util import logger
from data.data_reader import data_reader

# 加载YAML测试数据
TEST_DATA = data_reader.load_yaml_data()
GET_MY_INFO_DATA = TEST_DATA["student_loan"]["get_my_info"]
ADD_LOAN_DATA = TEST_DATA["student_loan"]["add_loan"]

# ===================== 修复这里 =====================
get_my_info_case = [(k, v["params"], v["expected"]) for k, v in GET_MY_INFO_DATA.items()]
add_loan_case = [(k, v["params"], v["files"], v["expected"]) for k, v in ADD_LOAN_DATA.items()]


class TestStudentLoan:
    """贷款模块测试用例"""
    # 学生获取个人信息
    @pytest.mark.parametrize("case_name, expected", get_my_info_case)
    def test_get_my_info(self, case_name, expected, student_headers):
        """获取个人信息-数据驱动"""
        logger.info(f"执行用例：{case_name}")
        response = loan_api.get_my_info(student_headers)
        assert_util.assert_common(response, expected)

    # 学生申请贷款
    @pytest.mark.parametrize("case_name, params, expected", get_my_info_case)
    def test_get_my_info(self, case_name, params, expected, student_headers):
        """获取个人信息（普通接口，仅参数）"""
        logger.info(f"执行用例：{case_name}")
        response = loan_api.get_my_info(student_headers)
        assert_util.assert_common(response, expected)

    # 管理员审核贷款列表
    def test_get_all_loan(self, admin_headers):
        params = {"page": 1, "applicant": 2, "status": 1, "school": 12}
        response = loan_api.get_all_loan(admin_headers, params)
        assert_util.assert_common(response ,expected={"code": 200})

    # 管理员修改贷款状态（参数化）
    @pytest.mark.parametrize("loan_id, status, comment", [(6, 2, "材料不全"), (7, 1, "审核通过")])
    def test_update_loan_status(self, admin_headers, loan_id, status, comment):
        params = {"id": loan_id, "status": status, "reviewComments": comment}
        response = loan_api.update_loan_status(admin_headers, params)
        assert_util.assert_common(response ,expected={"code": 200})