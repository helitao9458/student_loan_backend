# data/data_reader.py
import os
import yaml
from utils.log_util import logger

class DataReader:
    """测试数据读取工具类，统一加载YAML测试数据"""
    @staticmethod
    def load_yaml_data(file_name: str = "test_data.yaml") -> dict:
        """
        加载指定YAML测试数据文件
        :param file_name: 数据文件名（默认test_data.yaml）
        :return: 解析后的字典格式测试数据
        """
        data_path = os.path.join(os.path.dirname(__file__), file_name)
        try:
            with open(data_path, "r", encoding="utf-8") as f:
                test_data = yaml.safe_load(f)
            logger.info(f"成功加载测试数据：{data_path}，数据模块：{list(test_data.keys())}")
            return test_data
        except FileNotFoundError:
            logger.error(f"测试数据文件不存在：{data_path}")
            raise
        except yaml.YAMLError as e:
            logger.error(f"YAML数据解析失败：{str(e)}")
            raise

# 全局数据读取对象
data_reader = DataReader()