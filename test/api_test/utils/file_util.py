# utils/file_util.py
import os
from utils.log_util import logger
from config.config import get_file_path

class FileUtil:
    """文件处理工具类：专门处理上传文件的参数构造"""
    @staticmethod
    def build_file_params(file_config: dict) -> dict:
        """
        构造文件上传参数（适配requests的files格式）
        :param file_config: YAML中读取的files配置（如{"images": ["1.png", "2.jpg"]}）
        :return: 可直接传入requests的files字典（如{"images": [(文件名, 文件对象, 类型), ...]}）
        """
        file_params = {}
        if not file_config:  # 无文件时返回空字典
            return file_params
        
        for field_name, file_names in file_config.items():
            # 单个字段对应多个文件（如images字段传多张图片）
            file_tuples = []
            for file_name in file_names:
                try:
                    # 获取文件完整路径（从config中读取基础路径）
                    file_path = get_file_path(file_name)
                    # 识别文件MIME类型（简化版，可扩展）
                    file_type = FileUtil.get_file_mime_type(file_name)
                    # 打开文件并构造元组（requests要求格式：(文件名, 文件对象, Content-Type)）
                    file_obj = open(file_path, "rb")
                    file_tuples.append((file_name, file_obj, file_type))
                    logger.info(f"加载上传文件成功：{file_name} | 路径：{file_path}")
                except FileNotFoundError:
                    logger.error(f"上传文件不存在：{file_name} | 路径：{file_path}")
                    raise
            file_params[field_name] = file_tuples
        return file_params

    @staticmethod
    def get_file_mime_type(file_name: str) -> str:
        """获取文件MIME类型（适配常见图片格式）"""
        suffix = os.path.splitext(file_name)[-1].lower()
        mime_map = {
            ".png": "image/png",
            ".jpg": "image/jpeg",
            ".jpeg": "image/jpeg",
            ".pdf": "application/pdf",
            ".docx": "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        }
        return mime_map.get(suffix, "application/octet-stream")

    @staticmethod
    def close_file_params(file_params: dict):
        """关闭文件对象（避免资源泄漏）"""
        for field_name, file_tuples in file_params.items():
            for file_tuple in file_tuples:
                try:
                    file_tuple[1].close()  # 关闭文件对象
                    logger.info(f"关闭文件对象：{file_tuple[0]}")
                except Exception as e:
                    logger.warning(f"关闭文件对象失败：{file_tuple[0]} | 异常：{str(e)}")

# 全局文件工具对象
file_util = FileUtil()