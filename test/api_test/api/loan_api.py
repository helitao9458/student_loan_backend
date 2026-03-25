import os
from utils.request_util import request_util
from utils.file_util import file_util

class LoanApi:
    """贷款模块接口"""
    @staticmethod
    def get_my_info(headers):
        """获取学生个人信息"""
        url = "user/getmy"
        return request_util.post(url, headers=headers)

    @staticmethod
    def add_loan(headers, loan_params: dict, loan_files: dict = None):
        """申请贷款（含文件上传）"""
        url = "loan/addloan"
        # 构造文件参数
        file_params = file_util.build_file_params(loan_files)
        data_params = loan_params
        response = request_util.post(
            url=url,
            headers=headers,
            data=data_params,  # 普通参数用data传递
            files=file_params  # 文件参数用files传递
        )
        return response

    @staticmethod
    def get_all_loan(headers, params):
        """管理员查看贷款审核列表"""
        url = "loan/getallloan"
        return request_util.post(url, headers=headers, params=params)

    @staticmethod
    def update_loan_status(headers, params):
        """修改贷款审核状态"""
        url = "loan/updatestatus"
        return request_util.post(url, headers=headers, params=params)

    @staticmethod
    def get_loan_self(headers, params):
        """学生查看自己的贷款记录"""
        url = "loan/getbyself"
        return request_util.post(url, headers=headers, params=params)

# 全局贷款接口对象
loan_api = LoanApi()
