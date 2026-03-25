import os
import logging
from logging.handlers import RotatingFileHandler

# 日志配置
def setup_logger():
    """配置日志输出（控制台+文件）"""
    log_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), "logs")
    os.makedirs(log_dir, exist_ok=True)
    log_file = os.path.join(log_dir, "test_run.log")

    # 日志格式
    formatter = logging.Formatter(
        "%(asctime)s - %(name)s - %(levelname)s - %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S"
    )

    # 文件处理器（按大小分割，保留5个文件，每个50MB）
    file_handler = RotatingFileHandler(
        log_file, maxBytes=50*1024*1024, backupCount=5, encoding="utf-8"
    )
    file_handler.setFormatter(formatter)

    # 控制台处理器
    console_handler = logging.StreamHandler()
    console_handler.setFormatter(formatter)

    # 配置logger
    logger = logging.getLogger("api_auto_test")
    logger.setLevel(logging.INFO)
    logger.addHandler(file_handler)
    logger.addHandler(console_handler)

    return logger

# 全局日志对象
logger = setup_logger()