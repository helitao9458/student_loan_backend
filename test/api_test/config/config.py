import os
import yaml

# 读取配置文件
def load_config():
    """读取config.yaml配置文件"""
    config_path = os.path.join(os.path.dirname(__file__), "config.yaml")
    with open(config_path, "r", encoding="utf-8") as f:
        config = yaml.safe_load(f)
    return config

# 全局配置对象
CONFIG = load_config()

# 快捷获取配置
def get_base_url():
    """获取测试环境基础URL"""
    return CONFIG["env"]["test"]["base_url"]

def get_account(role="admin"):
    """获取指定角色的账号密码"""
    return CONFIG["account"][role]

def get_file_path(file_name):
    # 1. 拼接基础路径（修复：先获取绝对路径）
    project_root = os.path.dirname(os.path.dirname(__file__))  # 项目根目录
    base_path = CONFIG["file_path"]["test_images"]  # 配置中的相对路径
    full_base_path = os.path.join(project_root, base_path)  # 拼接为绝对路径
    
    # 2. 拼接文件名（确保file_name是字符串）
    if isinstance(file_name, list):
        file_name = file_name[0]  # 兼容意外传入列表的情况
    full_file_path = os.path.join(full_base_path, file_name)
    
    # 3. 确保路径存在
    os.makedirs(full_base_path, exist_ok=True)
    return full_file_path