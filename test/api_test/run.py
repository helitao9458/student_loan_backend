import os
import pytest
from utils.log_util import logger

def run_tests():
    """运行所有测试用例"""
    # 定义报告路径
    report_dir = os.path.join(os.path.dirname(__file__), "report", "html")
    os.makedirs(report_dir, exist_ok=True)
    report_file = os.path.join(report_dir, "test_report.html")

    # 构造pytest命令
    pytest_args = [
        "testcases/",  # 用例目录
        # "-v",  # 详细输出
        # "--html=" + report_file,  # HTML报告
        # "--self-contained-html",  # 独立HTML文件
        # "--alluredir=report/allure-results"  # Allure报告（可选）
    ]

    # 执行测试
    logger.info("开始执行接口自动化测试...")
    pytest.main(pytest_args)
    logger.info(f"测试完成！HTML报告路径：{report_file}")

if __name__ == "__main__":
    run_tests()